package org.simulatest.example.library.environment;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.environment.Environment;
import org.simulatest.example.library.LibraryDatabase;

/**
 * Level 5 — Active Loans and Holds (leaf).
 *
 * <p>Parent: {@link MembersEnvironment}. Inherits the full ancestor chain:
 * genres, member types, branches, books, copies, and members.
 * This is the richest level — a fully operational library with checkouts,
 * an overdue loan, and active holds.
 */
@EnvironmentParent(MembersEnvironment.class)
public class LoansEnvironment implements Environment {

	@Override
	public void run() {
		// 5 active loans (id, copy_id, member_id, checkout_date, due_date, return_date)
		// Dates are relative to today so overdue status stays consistent.
		insertLoan(1, 1,  1,  -7,   7);  // Alice  → "The Great Adventure"
		insertLoan(2, 5,  2, -21,  -7);  // Bob    → "Quantum Physics"      (OVERDUE!)
		insertLoan(3, 7,  3,  -3,  18);  // Charlie → "World War II"
		insertLoan(4, 16, 4,  -5,  16);  // Diana  → "Ancient Rome"
		insertLoan(5, 11, 1, -10,   4);  // Alice  → "Mystery at Midnight"

		// Mark checked-out copies
		LibraryDatabase.execute("UPDATE book_copy SET status = 'CHECKED_OUT' WHERE id IN (1, 5, 7, 16, 11)");

		// 2 active holds (id, book_id, member_id, hold_date, status)
		LibraryDatabase.execute("INSERT INTO hold VALUES (1, 1, 7, DATEADD('DAY', -2, CURRENT_DATE), 'ACTIVE')");
		LibraryDatabase.execute("INSERT INTO hold VALUES (2, 3, 8, DATEADD('DAY', -1, CURRENT_DATE), 'ACTIVE')");
	}

	private void insertLoan(int id, int copyId, int memberId, int checkoutDaysAgo, int dueDaysFromNow) {
		LibraryDatabase.execute(
			"INSERT INTO loan VALUES (" + id + ", " + copyId + ", " + memberId + ", " +
			"DATEADD('DAY', " + checkoutDaysAgo + ", CURRENT_DATE), " +
			"DATEADD('DAY', " + dueDaysFromNow + ", CURRENT_DATE), NULL)");
	}

}
