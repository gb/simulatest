package org.simulatest.example.library;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.example.library.environment.BranchesEnvironment;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests at LEVEL 2 — reference data + 3 branches.
 *
 * <p>Branches are the pivot point of the tree: CatalogEnvironment and
 * StaffEnvironment are both children of BranchesEnvironment. If branch
 * data leaks between tests here, every downstream environment inherits
 * corrupt state.
 */
@UseEnvironment(BranchesEnvironment.class)
class BranchTest {

	// =========================================================================
	// Branch CRUD
	// =========================================================================

	@Test
	void threeBranchesExist() {
		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM branch"));
	}

	@Test
	void openNewBranch() {
		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM branch"));

		LibraryDatabase.execute(
			"INSERT INTO branch VALUES (4, 'Northgate Branch', '400 North Ave')");

		assertEquals(4, LibraryDatabase.queryInt("SELECT COUNT(*) FROM branch"));
		assertTrue(LibraryDatabase.queryExists(
			"SELECT 1 FROM branch WHERE name = 'Northgate Branch'"));
	}

	@Test
	void renameBranch() {
		assertEquals("Downtown Branch", LibraryDatabase.queryString(
			"SELECT name FROM branch WHERE id = 1"));

		LibraryDatabase.execute(
			"UPDATE branch SET name = 'Central Library' WHERE id = 1");

		assertEquals("Central Library", LibraryDatabase.queryString(
			"SELECT name FROM branch WHERE id = 1"));
	}

	@Test
	void relocateBranch() {
		String oldAddress = LibraryDatabase.queryString(
			"SELECT address FROM branch WHERE id = 2");

		LibraryDatabase.execute(
			"UPDATE branch SET address = '999 New Street' WHERE id = 2");

		assertEquals("999 New Street", LibraryDatabase.queryString(
			"SELECT address FROM branch WHERE id = 2"));
		assertNotEquals("999 New Street", oldAddress);
	}

	@Test
	void closeAllBranches() {
		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM branch"));

		LibraryDatabase.execute("DELETE FROM branch");

		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM branch"));
	}

	// =========================================================================
	// Same-row isolation — verify EXACT VALUES, not just existence.
	// renameBranch changes id=1 to "Central Library". relocateBranch changes
	// id=2's address. If either leaked, these catch it. closeAllBranches
	// deleting everything would also be caught here (queryString throws on
	// missing rows).
	// =========================================================================

	@Test
	void downtownBranchIsStillCalledDowntown() {
		assertEquals("Downtown Branch", LibraryDatabase.queryString(
			"SELECT name FROM branch WHERE id = 1"));
	}

	@Test
	void westsideAddressIsStillOriginal() {
		assertEquals("250 Oak Avenue", LibraryDatabase.queryString(
			"SELECT address FROM branch WHERE id = 2"));
	}

	// =========================================================================
	// Parent data visible, child data not yet available
	// =========================================================================

	@Test
	void parentReferenceDataIsAccessible() {
		assertEquals(5, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));
		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member_type"));
	}

	@Test
	void noBooksExistYet() {
		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book"));
	}

}
