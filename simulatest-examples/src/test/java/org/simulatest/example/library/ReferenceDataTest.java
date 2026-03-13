package org.simulatest.example.library;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.example.library.environment.ReferenceDataEnvironment;
import org.simulatest.example.library.util.LibraryTestSetup;

import static org.junit.jupiter.api.Assertions.*;

/** Tests at the ROOT level — only genres and member types exist. */
@UseEnvironment(ReferenceDataEnvironment.class)
public class ReferenceDataTest {

	static { LibraryTestSetup.init(); }

	// --- Genres ---

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
		// If deleteAllGenres leaked, this would fail
		assertTrue(LibraryDatabase.queryExists(
			"SELECT 1 FROM genre WHERE id = 3 AND name = 'Science'"));
	}

	@Test
	void historyGenreExists() {
		assertTrue(LibraryDatabase.queryExists(
			"SELECT 1 FROM genre WHERE id = 4 AND name = 'History'"));
	}

	// --- Member types ---

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
	void regularMemberCanCheckoutFiveBooks() {
		// If increasePremiumCheckoutLimit leaked into Regular, this would fail
		assertEquals(5, LibraryDatabase.queryInt(
			"SELECT max_checkouts FROM member_type WHERE name = 'Regular'"));
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
	void deleteChildrenMemberType() {
		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member_type"));

		LibraryDatabase.execute("DELETE FROM member_type WHERE name = 'Children'");

		assertEquals(2, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member_type"));
		assertFalse(LibraryDatabase.queryExists(
			"SELECT 1 FROM member_type WHERE name = 'Children'"));
	}

}
