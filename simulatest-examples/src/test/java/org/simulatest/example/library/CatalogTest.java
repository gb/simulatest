package org.simulatest.example.library;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.example.library.environment.CatalogEnvironment;

import static org.junit.jupiter.api.Assertions.*;

/** Tests at LEVEL 3a — reference data + branches + 10 books + 18 copies. */
@UseEnvironment(CatalogEnvironment.class)
class CatalogTest {

	// --- Book catalog ---

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

	// --- Book copies ---

	@Test
	void eighteenCopiesAcrossThreeBranches() {
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
		assertEquals(18, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE status = 'AVAILABLE'"));
	}

	// --- Not yet available ---

	@Test
	void noMembersExistYet() {
		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member"));
	}

	@Test
	void noStaffExist() {
		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM staff"));
	}

}
