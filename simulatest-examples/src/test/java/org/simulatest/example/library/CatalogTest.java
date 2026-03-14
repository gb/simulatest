package org.simulatest.example.library;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.example.library.environment.CatalogEnvironment;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests at LEVEL 3a — reference data + branches + 10 books + 18 copies.
 *
 * <p>The catalog level is where data complexity jumps: two tables (book and
 * book_copy) with FK relationships between them and to parent tables (genre,
 * branch). This is the first level where cascading FK violations become
 * testable, and where bulk operations affect subsets of rows.
 */
@UseEnvironment(CatalogEnvironment.class)
class CatalogTest {

	// =========================================================================
	// Book catalog — CRUD
	// =========================================================================

	@Test
	void tenBooksInCatalog() {
		assertEquals(10, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book"));
	}

	@Test
	void addNewBook() {
		assertEquals(10, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book"));

		LibraryDatabase.execute(
			"INSERT INTO book VALUES (11, 'The Martian', 'Andy Weir', " +
			"'9780553418026', 3, 2014)");

		assertEquals(11, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book"));
		assertTrue(LibraryDatabase.queryExists(
			"SELECT 1 FROM book WHERE title = 'The Martian'"));
	}

	@Test
	void updateBookTitle() {
		assertEquals("The Great Adventure", LibraryDatabase.queryString(
			"SELECT title FROM book WHERE id = 1"));

		LibraryDatabase.execute(
			"UPDATE book SET title = 'The Greatest Adventure' WHERE id = 1");

		assertEquals("The Greatest Adventure", LibraryDatabase.queryString(
			"SELECT title FROM book WHERE id = 1"));
	}

	@Test
	void changeBookGenre() {
		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT genre_id FROM book WHERE id = 2"));

		LibraryDatabase.execute("UPDATE book SET genre_id = 3 WHERE id = 2");

		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT genre_id FROM book WHERE id = 2"));
	}

	@Test
	void removeBookAndItsCopies() {
		assertEquals(10, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book"));
		assertEquals(18, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book_copy"));

		LibraryDatabase.execute("DELETE FROM book_copy WHERE book_id = 7");
		LibraryDatabase.execute("DELETE FROM book WHERE id = 7");

		assertEquals(9, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book"));
		assertFalse(LibraryDatabase.queryExists(
			"SELECT 1 FROM book WHERE title = 'Cooking for Engineers'"));
	}

	@Test
	void everyBookHasAnAuthorAndGenre() {
		assertEquals(10, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book b JOIN genre g ON b.genre_id = g.id"));
		assertEquals(0, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book WHERE author IS NULL OR author = ''"));
	}

	@Test
	void twoFictionTwoScienceTwoChildrens() {
		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book WHERE genre_id = 1"));
		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book WHERE genre_id = 3"));
		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book WHERE genre_id = 5"));
	}

	// =========================================================================
	// Same-row isolation — updateBookTitle and changeBookGenre mutate specific
	// rows. Checking exact values catches leaks that count checks would miss.
	// =========================================================================

	@Test
	void bookTitleIsExactlyOriginal() {
		// updateBookTitle changes it to "The Greatest Adventure".
		assertEquals("The Great Adventure", LibraryDatabase.queryString(
			"SELECT title FROM book WHERE id = 1"));
	}

	@Test
	void bookTwoGenreIsStillNonFiction() {
		// changeBookGenre moves book 2 from Non-Fiction(2) to Science(3).
		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT genre_id FROM book WHERE id = 2"));
	}

	// =========================================================================
	// Book copies
	// =========================================================================

	@Test
	void eighteenCopiesAcrossThreeBranches() {
		// 18 total: Downtown 7, Westside 6, Eastville 5
		assertEquals(18, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book_copy"));
		assertEquals(7, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE branch_id = 1"));
		assertEquals(6, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE branch_id = 2"));
		assertEquals(5, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE branch_id = 3"));
	}

	@Test
	void addCopyToNewBranch() {
		assertEquals(5, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE branch_id = 3"));

		LibraryDatabase.execute(
			"INSERT INTO book_copy VALUES (19, 1, 3, 'AVAILABLE')");

		assertEquals(6, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE branch_id = 3"));
		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT COUNT(DISTINCT branch_id) FROM book_copy WHERE book_id = 1"));
	}

	@Test
	void markCopyAsDamaged() {
		assertEquals(18, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE status = 'AVAILABLE'"));

		LibraryDatabase.execute(
			"UPDATE book_copy SET status = 'DAMAGED' WHERE id = 13");

		assertEquals(17, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE status = 'AVAILABLE'"));
		assertEquals("DAMAGED", LibraryDatabase.queryString(
			"SELECT status FROM book_copy WHERE id = 13"));
	}

	@Test
	void deleteAllCopiesAtEastville() {
		assertEquals(5, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE branch_id = 3"));

		LibraryDatabase.execute("DELETE FROM book_copy WHERE branch_id = 3");

		assertEquals(0, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE branch_id = 3"));
		// Other branches untouched
		assertEquals(7, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE branch_id = 1"));
		assertEquals(6, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE branch_id = 2"));
	}

	@Test
	void allCopiesStillAvailable() {
		// Guards against deleteAllCopiesAtEastville and markCopyAsDamaged leaking.
		// Checks both total count AND status — a leaked delete changes the count,
		// while a leaked status change preserves count but shifts distribution.
		assertEquals(18, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book_copy"));
		assertEquals(18, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE status = 'AVAILABLE'"));
	}

	// =========================================================================
	// Bulk partial delete — delete all copies of ONE book across branches.
	// Different from deleteAllCopiesAtEastville (by branch) — this uses a
	// cross-branch WHERE clause that touches rows in multiple branches at once.
	// =========================================================================

	@Test
	void bulkDeleteCopiesOfOneBook() {
		// Book 1 ("The Great Adventure") has 2 copies (at Downtown and Westside)
		int copiesOfBook1 = LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE book_id = 1");
		assertTrue(copiesOfBook1 > 1, "Book 1 should have copies at multiple branches");

		LibraryDatabase.execute("DELETE FROM book_copy WHERE book_id = 1");

		assertEquals(0, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE book_id = 1"));
		assertEquals(18 - copiesOfBook1, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy"));
	}

	// =========================================================================
	// FK constraint violations — a failed statement must not corrupt the
	// savepoint. If the savepoint is damaged by a constraint violation,
	// every subsequent test gets garbage.
	// =========================================================================

	@Test
	void deletingBranchWithCopiesFails() {
		// Branch 1 has 7 copies — FK constraint prevents deletion.
		assertThrows(RuntimeException.class, () ->
			LibraryDatabase.execute("DELETE FROM branch WHERE id = 1"));

		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM branch"));
		assertEquals(7, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE branch_id = 1"));
	}

	@Test
	void insertingBookWithInvalidGenreFails() {
		// Genre 99 doesn't exist — FK prevents insertion.
		assertThrows(RuntimeException.class, () ->
			LibraryDatabase.execute(
				"INSERT INTO book VALUES (11, 'Ghost Book', 'Nobody', '0000000000000', 99, 2024)"));

		assertEquals(10, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book"));
	}

	// =========================================================================
	// Child data not yet available
	// =========================================================================

	@Test
	void noMembersExistYet() {
		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member"));
	}

	@Test
	void noStaffExist() {
		// StaffEnvironment is a sibling of CatalogEnvironment — its data
		// is invisible here, proving sibling isolation at this level.
		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM staff"));
	}

}
