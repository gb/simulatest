package org.simulatest.example.library;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.example.library.environment.ReferenceDataEnvironment;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests at the ROOT level — only genres and member types exist.
 *
 * <p>This is the foundation layer. Every test here mutates reference data and
 * relies on per-test rollback to keep siblings from seeing the damage.
 * If ANY of the isolation tests fail, the Insistence Layer's savepoint
 * stack is broken and nothing above this level can be trusted.
 */
@UseEnvironment(ReferenceDataEnvironment.class)
class ReferenceDataTest {

	// =========================================================================
	// Genres — CRUD + isolation
	// =========================================================================

	@Test
	void fiveGenresExist() {
		assertEquals(5, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));
	}

	@Test
	void addNewGenre() {
		assertEquals(5, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));

		LibraryDatabase.execute("INSERT INTO genre VALUES (6, 'Romance')");

		assertEquals(6, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));
		assertTrue(LibraryDatabase.queryExists(
			"SELECT 1 FROM genre WHERE name = 'Romance'"));
	}

	@Test
	void renameGenre() {
		assertEquals("Fiction", LibraryDatabase.queryString(
			"SELECT name FROM genre WHERE id = 1"));

		LibraryDatabase.execute("UPDATE genre SET name = 'Literary Fiction' WHERE id = 1");

		assertEquals("Literary Fiction", LibraryDatabase.queryString(
			"SELECT name FROM genre WHERE id = 1"));
	}

	@Test
	void deleteAllGenres() {
		assertEquals(5, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));

		LibraryDatabase.execute("DELETE FROM genre");

		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));
	}

	@Test
	void scienceGenreExists() {
		// If deleteAllGenres leaked, this would fail.
		assertTrue(LibraryDatabase.queryExists(
			"SELECT 1 FROM genre WHERE id = 3 AND name = 'Science'"));
	}

	// =========================================================================
	// Same-row isolation — renameGenre changes genre 1 to "Literary Fiction".
	// fiveGenresExist wouldn't catch this (count stays 5 either way).
	// Checking the EXACT VALUE proves the savepoint restored cell content,
	// not just row existence.
	// =========================================================================

	@Test
	void fictionGenreIsStillFiction() {
		assertEquals("Fiction", LibraryDatabase.queryString(
			"SELECT name FROM genre WHERE id = 1"));
	}

	// =========================================================================
	// Delete-and-reinsert same PK — the nastiest isolation scenario.
	// We delete genre 5 ("Children"), then insert a DIFFERENT genre with PK=5.
	// If the savepoint doesn't restore the ORIGINAL row, genreFiveIsStillChildren
	// will see "Philosophy" instead of "Children". COUNT(*) stays 5 either way.
	// =========================================================================

	@Test
	void deleteAndReinsertSamePk() {
		assertEquals("Children", LibraryDatabase.queryString(
			"SELECT name FROM genre WHERE id = 5"));

		LibraryDatabase.execute("DELETE FROM genre WHERE id = 5");
		LibraryDatabase.execute("INSERT INTO genre VALUES (5, 'Philosophy')");

		assertEquals("Philosophy", LibraryDatabase.queryString(
			"SELECT name FROM genre WHERE id = 5"));
		assertEquals(5, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));
	}

	@Test
	void genreFiveIsStillChildren() {
		assertEquals("Children", LibraryDatabase.queryString(
			"SELECT name FROM genre WHERE id = 5"));
	}

	// =========================================================================
	// Insert-then-delete within the same test — the net effect is zero rows
	// changed, but the savepoint must still track the intermediate state.
	// =========================================================================

	@Test
	void insertGenreThenDeleteItWithinSameTest() {
		LibraryDatabase.execute("INSERT INTO genre VALUES (6, 'Romance')");
		assertEquals(6, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));

		LibraryDatabase.execute("DELETE FROM genre WHERE id = 6");
		assertEquals(5, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));
	}

	// =========================================================================
	// No-op mutation — UPDATE that matches zero rows.
	// If a no-op corrupts the savepoint stack (e.g., pushes without popping),
	// subsequent tests will see stale or missing data.
	// This pattern is tested here once; it doesn't need repeating elsewhere.
	// =========================================================================

	@Test
	void updateNonexistentGenreChangesNothing() {
		LibraryDatabase.execute("UPDATE genre SET name = 'Ghost' WHERE id = 999");

		assertEquals(5, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));
		assertFalse(LibraryDatabase.queryExists("SELECT 1 FROM genre WHERE name = 'Ghost'"));
	}

	// =========================================================================
	// Member types — CRUD + isolation
	// =========================================================================

	@Test
	void threeMemberTypesExist() {
		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member_type"));
	}

	@Test
	void addSeniorMemberType() {
		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member_type"));

		LibraryDatabase.execute(
			"INSERT INTO member_type VALUES (4, 'Senior', 7, 28, 0)");

		assertEquals(4, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member_type"));
		assertEquals(28, LibraryDatabase.queryInt(
			"SELECT loan_period_days FROM member_type WHERE name = 'Senior'"));
	}

	@Test
	void increasePremiumCheckoutLimit() {
		assertEquals(10, LibraryDatabase.queryInt(
			"SELECT max_checkouts FROM member_type WHERE name = 'Premium'"));

		LibraryDatabase.execute(
			"UPDATE member_type SET max_checkouts = 15 WHERE name = 'Premium'");

		assertEquals(15, LibraryDatabase.queryInt(
			"SELECT max_checkouts FROM member_type WHERE name = 'Premium'"));
	}

	@Test
	void premiumCheckoutLimitIsStillTen() {
		// increasePremiumCheckoutLimit changes Premium to 15.
		// This is the same-row isolation check: it verifies the exact row
		// that was mutated is back to its original value.
		assertEquals(10, LibraryDatabase.queryInt(
			"SELECT max_checkouts FROM member_type WHERE name = 'Premium'"));
	}

	@Test
	void childrenMemberHasNoFines() {
		assertEquals(0, LibraryDatabase.queryInt(
			"SELECT fine_per_day_cents FROM member_type WHERE name = 'Children'"));

		LibraryDatabase.execute(
			"UPDATE member_type SET fine_per_day_cents = 50 WHERE name = 'Children'");

		assertEquals(50, LibraryDatabase.queryInt(
			"SELECT fine_per_day_cents FROM member_type WHERE name = 'Children'"));
	}

	@Test
	void childrenFinesAreStillZero() {
		// childrenMemberHasNoFines sets fine_per_day_cents to 50.
		// If that leaked, children would be charged fines they shouldn't owe.
		assertEquals(0, LibraryDatabase.queryInt(
			"SELECT fine_per_day_cents FROM member_type WHERE name = 'Children'"));
	}

	@Test
	void deleteChildrenMemberType() {
		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member_type"));

		LibraryDatabase.execute("DELETE FROM member_type WHERE name = 'Children'");

		assertEquals(2, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member_type"));
		assertFalse(LibraryDatabase.queryExists(
			"SELECT 1 FROM member_type WHERE name = 'Children'"));
	}

	// =========================================================================
	// Bulk partial delete — WHERE matches SOME rows, not all.
	// Different from deleteAllGenres (everything) and deleteChildrenMemberType
	// (one row). Tests the boundary between affected and unaffected rows.
	// =========================================================================

	@Test
	void deleteMultipleGenresButNotAll() {
		LibraryDatabase.execute("DELETE FROM genre WHERE id IN (1, 2)");

		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));
		assertFalse(LibraryDatabase.queryExists("SELECT 1 FROM genre WHERE id = 1"));
		assertFalse(LibraryDatabase.queryExists("SELECT 1 FROM genre WHERE id = 2"));
		assertTrue(LibraryDatabase.queryExists("SELECT 1 FROM genre WHERE id = 3"));
	}

	@Test
	void allFiveGenresStillExistAfterPartialDelete() {
		assertEquals(5, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));
		assertTrue(LibraryDatabase.queryExists("SELECT 1 FROM genre WHERE id = 1"));
		assertTrue(LibraryDatabase.queryExists("SELECT 1 FROM genre WHERE id = 2"));
	}

}
