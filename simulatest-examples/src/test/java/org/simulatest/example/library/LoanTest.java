package org.simulatest.example.library;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.example.library.environment.LoansEnvironment;
import org.simulatest.example.library.util.LibraryTestSetup;

import static org.junit.jupiter.api.Assertions.*;

/** Tests at LEVEL 5 (leaf) — everything exists: 5 loans, 2 holds, the full library. */
@UseEnvironment(LoansEnvironment.class)
public class LoanTest {

	static { LibraryTestSetup.init(); }

	// --- Loan operations ---

	@Test
	void fiveActiveLoans() {
		assertEquals(5, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM loan WHERE return_date IS NULL"));
	}

	@Test
	void checkoutNewBook() {
		assertEquals(5, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM loan WHERE return_date IS NULL"));

		LibraryDatabase.execute(
			"INSERT INTO loan VALUES (6, 3, 7, CURRENT_DATE, " +
			"DATEADD('DAY', 14, CURRENT_DATE), NULL)");
		LibraryDatabase.execute(
			"UPDATE book_copy SET status = 'CHECKED_OUT' WHERE id = 3");

		assertEquals(6, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM loan WHERE return_date IS NULL"));
		assertEquals("CHECKED_OUT", LibraryDatabase.queryString(
			"SELECT status FROM book_copy WHERE id = 3"));
	}

	@Test
	void returnBook() {
		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM loan WHERE member_id = 1 AND return_date IS NULL"));

		LibraryDatabase.execute(
			"UPDATE loan SET return_date = CURRENT_DATE WHERE id = 1");
		LibraryDatabase.execute(
			"UPDATE book_copy SET status = 'AVAILABLE' WHERE id = 1");

		assertEquals(1, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM loan WHERE member_id = 1 AND return_date IS NULL"));
		assertEquals("AVAILABLE", LibraryDatabase.queryString(
			"SELECT status FROM book_copy WHERE id = 1"));
	}

	@Test
	void returnOverdueBook() {
		assertEquals(1, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM loan WHERE due_date < CURRENT_DATE AND return_date IS NULL"));

		LibraryDatabase.execute(
			"UPDATE loan SET return_date = CURRENT_DATE WHERE id = 2");
		LibraryDatabase.execute(
			"UPDATE book_copy SET status = 'AVAILABLE' WHERE id = 5");

		assertEquals(0, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM loan WHERE due_date < CURRENT_DATE AND return_date IS NULL"));
	}

	@Test
	void deleteAllLoans() {
		assertEquals(5, LibraryDatabase.queryInt("SELECT COUNT(*) FROM loan"));

		LibraryDatabase.execute("DELETE FROM loan");
		LibraryDatabase.execute(
			"UPDATE book_copy SET status = 'AVAILABLE' WHERE status = 'CHECKED_OUT'");

		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM loan"));
		assertEquals(18, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE status = 'AVAILABLE'"));
	}

	@Test
	void aliceStillHasTwoActiveLoans() {
		// If deleteAllLoans or returnBook leaked, this fails
		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM loan WHERE member_id = 1 AND return_date IS NULL"));
	}

	@Test
	void bobsLoanIsStillOverdue() {
		// If returnOverdueBook leaked, this fails
		assertTrue(LibraryDatabase.queryExists(
			"SELECT 1 FROM loan " +
			"WHERE member_id = 2 AND due_date < CURRENT_DATE AND return_date IS NULL"));
	}

	// --- Hold operations ---

	@Test
	void twoActiveHolds() {
		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM hold WHERE status = 'ACTIVE'"));
	}

	@Test
	void placeNewHold() {
		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM hold WHERE status = 'ACTIVE'"));

		LibraryDatabase.execute(
			"INSERT INTO hold VALUES (3, 8, 6, CURRENT_DATE, 'ACTIVE')");

		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM hold WHERE status = 'ACTIVE'"));
	}

	@Test
	void cancelHold() {
		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM hold WHERE status = 'ACTIVE'"));

		LibraryDatabase.execute(
			"UPDATE hold SET status = 'CANCELLED' WHERE id = 1");

		assertEquals(1, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM hold WHERE status = 'ACTIVE'"));
		assertEquals("CANCELLED", LibraryDatabase.queryString(
			"SELECT status FROM hold WHERE id = 1"));
	}

	@Test
	void deleteHold() {
		assertEquals(2, LibraryDatabase.queryInt("SELECT COUNT(*) FROM hold"));

		LibraryDatabase.execute("DELETE FROM hold WHERE id = 2");

		assertEquals(1, LibraryDatabase.queryInt("SELECT COUNT(*) FROM hold"));
		assertFalse(LibraryDatabase.queryExists("SELECT 1 FROM hold WHERE id = 2"));
	}

	// --- Copy status ---

	@Test
	void fiveCopiesCheckedOutThirteenAvailable() {
		assertEquals(5, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE status = 'CHECKED_OUT'"));
		assertEquals(13, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE status = 'AVAILABLE'"));
	}

	@Test
	void markCopyAsLost() {
		assertEquals(0, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE status = 'LOST'"));

		LibraryDatabase.execute(
			"UPDATE book_copy SET status = 'LOST' WHERE id = 5");

		assertEquals(1, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE status = 'LOST'"));
		assertEquals(4, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM book_copy WHERE status = 'CHECKED_OUT'"));
	}

	// --- Multi-step workflows ---

	@Test
	void fullCheckoutWorkflow() {
		assertEquals(5, LibraryDatabase.queryInt("SELECT COUNT(*) FROM loan"));
		assertEquals(2, LibraryDatabase.queryInt("SELECT COUNT(*) FROM hold"));

		// Ethan checks out "Dragons and Wizards"
		LibraryDatabase.execute(
			"INSERT INTO loan VALUES (6, 17, 5, CURRENT_DATE, " +
			"DATEADD('DAY', 14, CURRENT_DATE), NULL)");
		LibraryDatabase.execute(
			"UPDATE book_copy SET status = 'CHECKED_OUT' WHERE id = 17");

		// Alice returns "The Great Adventure", fulfilling George's hold
		LibraryDatabase.execute(
			"UPDATE loan SET return_date = CURRENT_DATE WHERE id = 1");
		LibraryDatabase.execute(
			"UPDATE book_copy SET status = 'AVAILABLE' WHERE id = 1");
		LibraryDatabase.execute(
			"UPDATE hold SET status = 'FULFILLED' WHERE id = 1");

		// George picks up his held book
		LibraryDatabase.execute(
			"INSERT INTO loan VALUES (7, 1, 7, CURRENT_DATE, " +
			"DATEADD('DAY', 14, CURRENT_DATE), NULL)");
		LibraryDatabase.execute(
			"UPDATE book_copy SET status = 'CHECKED_OUT' WHERE id = 1");

		assertEquals(7, LibraryDatabase.queryInt("SELECT COUNT(*) FROM loan"));
		assertEquals(6, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM loan WHERE return_date IS NULL"));
		assertEquals("FULFILLED", LibraryDatabase.queryString(
			"SELECT status FROM hold WHERE id = 1"));
		assertEquals(1, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM hold WHERE status = 'ACTIVE'"));
	}

	@Test
	void memberReachesCheckoutLimit() {
		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM loan WHERE member_id = 1 AND return_date IS NULL"));

		// Alice checks out 3 more, reaching her Regular limit of 5
		LibraryDatabase.execute(
			"INSERT INTO loan VALUES (6, 3, 1, CURRENT_DATE, " +
			"DATEADD('DAY', 14, CURRENT_DATE), NULL)");
		LibraryDatabase.execute(
			"INSERT INTO loan VALUES (7, 10, 1, CURRENT_DATE, " +
			"DATEADD('DAY', 14, CURRENT_DATE), NULL)");
		LibraryDatabase.execute(
			"INSERT INTO loan VALUES (8, 14, 1, CURRENT_DATE, " +
			"DATEADD('DAY', 14, CURRENT_DATE), NULL)");

		int aliceLoans = LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM loan WHERE member_id = 1 AND return_date IS NULL");
		int aliceLimit = LibraryDatabase.queryInt(
			"SELECT mt.max_checkouts FROM member_type mt " +
			"JOIN member m ON m.member_type_id = mt.id WHERE m.id = 1");
		assertEquals(5, aliceLoans);
		assertEquals(aliceLimit, aliceLoans);
	}

	// --- Ancestry verification ---

	@Test
	void entireAncestryIsVisible() {
		assertEquals(5,  LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));
		assertEquals(3,  LibraryDatabase.queryInt("SELECT COUNT(*) FROM member_type"));
		assertEquals(3,  LibraryDatabase.queryInt("SELECT COUNT(*) FROM branch"));
		assertEquals(10, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book"));
		assertEquals(18, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book_copy"));
		assertEquals(8,  LibraryDatabase.queryInt("SELECT COUNT(*) FROM member"));
	}

}
