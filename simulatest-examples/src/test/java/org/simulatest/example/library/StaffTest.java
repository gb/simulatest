package org.simulatest.example.library;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.example.library.environment.StaffEnvironment;
import org.simulatest.example.library.util.LibraryTestSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests at LEVEL 3b — staff only. Sibling of CatalogEnvironment:
 * books, members, and loans are invisible (rolled back automatically).
 */
@UseEnvironment(StaffEnvironment.class)
class StaffTest {

	static { LibraryTestSetup.init(); }

	// --- Staff operations ---

	@Test
	void sevenStaffMembers() {
		assertEquals(7, LibraryDatabase.queryInt("SELECT COUNT(*) FROM staff"));
	}

	@Test
	void hireNewStaff() {
		assertEquals(7, LibraryDatabase.queryInt("SELECT COUNT(*) FROM staff"));

		LibraryDatabase.execute(
			"INSERT INTO staff VALUES (8, 'Zoe Chen', 'ASSISTANT', 2)");

		assertEquals(8, LibraryDatabase.queryInt("SELECT COUNT(*) FROM staff"));
		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM staff WHERE branch_id = 2"));
	}

	@Test
	void promoteToHeadLibrarian() {
		// Downtown has 1 HEAD_LIBRARIAN
		assertEquals(1, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM staff WHERE branch_id = 1 AND role = 'HEAD_LIBRARIAN'"));

		// Fire the current head, promote someone else
		LibraryDatabase.execute(
			"UPDATE staff SET role = 'LIBRARIAN' WHERE branch_id = 1 AND role = 'HEAD_LIBRARIAN'");
		LibraryDatabase.execute(
			"UPDATE staff SET role = 'HEAD_LIBRARIAN' WHERE id = 2");

		// Still exactly 1 HEAD_LIBRARIAN at Downtown
		assertEquals(1, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM staff WHERE branch_id = 1 AND role = 'HEAD_LIBRARIAN'"));
	}

	@Test
	void transferStaffToDifferentBranch() {
		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM staff WHERE branch_id = 1"));
		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM staff WHERE branch_id = 3"));

		LibraryDatabase.execute(
			"UPDATE staff SET branch_id = 3 WHERE id = 2");

		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM staff WHERE branch_id = 1"));
		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM staff WHERE branch_id = 3"));
	}

	@Test
	void fireAllStaff() {
		assertEquals(7, LibraryDatabase.queryInt("SELECT COUNT(*) FROM staff"));

		LibraryDatabase.execute("DELETE FROM staff");

		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM staff"));
	}

	@Test
	void everyBranchStillHasAHeadLibrarian() {
		// If fireAllStaff leaked, this would fail
		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM staff WHERE role = 'HEAD_LIBRARIAN'"));
	}

	@Test
	void threeDistinctRolesExist() {
		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT COUNT(DISTINCT role) FROM staff"));
	}

	// --- Ancestor data visible ---

	@Test
	void ancestorDataIsVisible() {
		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM branch"));
		assertEquals(5, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));
	}

	// --- Sibling isolation ---

	@Test
	void noBooksExist_siblingCatalogWasRolledBack() {
		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book"));
		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book_copy"));
	}

	@Test
	void noMembersOrLoansExist_entireSubtreeWasRolledBack() {
		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member"));
		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM loan"));
		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM hold"));
	}

}
