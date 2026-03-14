package org.simulatest.example.library;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.example.library.environment.StaffEnvironment;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests at LEVEL 3b — staff only. Sibling of CatalogEnvironment:
 * books, members, and loans are invisible (rolled back automatically).
 *
 * <p>This test class serves a DUAL purpose:
 * <ol>
 *   <li>Staff CRUD and isolation at this level</li>
 *   <li>PROOF that sibling subtree isolation works — when CatalogEnvironment's
 *       entire subtree (books, copies, members, loans, holds) was rolled
 *       back before StaffEnvironment ran, all those tables must be empty here</li>
 * </ol>
 */
@UseEnvironment(StaffEnvironment.class)
class StaffTest {

	// =========================================================================
	// Staff CRUD
	// =========================================================================

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
		assertEquals(1, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM staff WHERE branch_id = 1 AND role = 'HEAD_LIBRARIAN'"));

		LibraryDatabase.execute(
			"UPDATE staff SET role = 'LIBRARIAN' WHERE branch_id = 1 AND role = 'HEAD_LIBRARIAN'");
		LibraryDatabase.execute(
			"UPDATE staff SET role = 'HEAD_LIBRARIAN' WHERE id = 2");

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
		// Guards against fireAllStaff leaking.
		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM staff WHERE role = 'HEAD_LIBRARIAN'"));
	}

	@Test
	void threeDistinctRolesExist() {
		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT COUNT(DISTINCT role) FROM staff"));
	}

	// =========================================================================
	// Same-row isolation — promoteToHeadLibrarian demotes Margaret (id=1)
	// and promotes Robert (id=2). transferStaffToDifferentBranch moves
	// Robert to branch 3. If either leaked, these catch it.
	// =========================================================================

	@Test
	void margaretIsStillHeadLibrarian() {
		assertEquals("HEAD_LIBRARIAN", LibraryDatabase.queryString(
			"SELECT role FROM staff WHERE id = 1"));
	}

	@Test
	void robertIsStillAtDowntown() {
		assertEquals(1, LibraryDatabase.queryInt(
			"SELECT branch_id FROM staff WHERE id = 2"));
		assertEquals("LIBRARIAN", LibraryDatabase.queryString(
			"SELECT role FROM staff WHERE id = 2"));
	}

	// =========================================================================
	// Ancestor data visible
	// =========================================================================

	@Test
	void ancestorDataIsVisible() {
		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM branch"));
		assertEquals(5, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));
		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member_type"));
	}

	// =========================================================================
	// SIBLING ISOLATION — THE CROWN JEWEL.
	//
	// CatalogEnvironment and its entire subtree (books, copies, members,
	// loans, holds) ran BEFORE StaffEnvironment. The Insistence Layer
	// rolled ALL of it back. If ANY data from that subtree is visible here,
	// sibling isolation is broken and the entire tree model is untrustworthy.
	// =========================================================================

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
