package org.simulatest.example.library;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.example.library.environment.BranchesEnvironment;
import org.simulatest.example.library.util.LibraryTestSetup;

import static org.junit.jupiter.api.Assertions.*;

/** Tests at LEVEL 2 — reference data + 3 branches. */
@UseEnvironment(BranchesEnvironment.class)
public class BranchTest {

	static { LibraryTestSetup.init(); }

	// --- Branch operations ---

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
		assertNotEquals(oldAddress, "999 New Street");
	}

	@Test
	void closeAllBranches() {
		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM branch"));

		LibraryDatabase.execute("DELETE FROM branch");

		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM branch"));
	}

	@Test
	void downtownBranchStillExists() {
		// If closeAllBranches leaked, this would fail
		assertTrue(LibraryDatabase.queryExists(
			"SELECT 1 FROM branch WHERE name = 'Downtown Branch'"));
	}

	@Test
	void eastvilleBranchStillExists() {
		assertTrue(LibraryDatabase.queryExists(
			"SELECT 1 FROM branch WHERE name = 'Eastville Branch'"));
	}

	// --- Parent data visible ---

	@Test
	void parentReferenceDataIsAccessible() {
		assertEquals(5, LibraryDatabase.queryInt("SELECT COUNT(*) FROM genre"));
		assertEquals(3, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member_type"));
	}

	// --- Child data not yet available ---

	@Test
	void noBooksExistYet() {
		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book"));
	}

}
