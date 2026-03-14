package org.simulatest.example.library;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.example.library.environment.MembersEnvironment;
import org.simulatest.example.library.util.LibraryTestSetup;

import static org.junit.jupiter.api.Assertions.*;

/** Tests at LEVEL 4 — full catalog + 8 members. Loans don't exist yet. */
@UseEnvironment(MembersEnvironment.class)
class MemberTest {

	static { LibraryTestSetup.init(); }

	// --- Member operations ---

	@Test
	void eightMembersRegistered() {
		assertEquals(8, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member"));
	}

	@Test
	void registerNewMember() {
		assertEquals(8, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member"));

		LibraryDatabase.execute(
			"INSERT INTO member VALUES (9, 'Iris Newman', 'iris@email.com', 1, 1)");

		assertEquals(9, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member"));
		assertTrue(LibraryDatabase.queryExists(
			"SELECT 1 FROM member WHERE name = 'Iris Newman'"));
	}

	@Test
	void registerFamilyBatch() {
		assertEquals(8, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member"));

		LibraryDatabase.execute(
			"INSERT INTO member VALUES (9,  'Kevin Park', 'kevin@email.com', 2, 2)");
		LibraryDatabase.execute(
			"INSERT INTO member VALUES (10, 'Laura Park', 'laura@email.com', 2, 2)");
		LibraryDatabase.execute(
			"INSERT INTO member VALUES (11, 'Max Park',   'max@email.com',   3, 2)");

		assertEquals(11, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member"));
		assertEquals(6, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM member WHERE home_branch_id = 2"));
	}

	@Test
	void upgradeMembership() {
		assertEquals(1, LibraryDatabase.queryInt(
			"SELECT member_type_id FROM member WHERE id = 1"));

		LibraryDatabase.execute(
			"UPDATE member SET member_type_id = 2 WHERE id = 1");

		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT member_type_id FROM member WHERE id = 1"));
		assertEquals(4, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM member WHERE member_type_id = 2"));
	}

	@Test
	void transferHomeBranch() {
		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT home_branch_id FROM member WHERE id = 4"));

		LibraryDatabase.execute(
			"UPDATE member SET home_branch_id = 1 WHERE id = 4");

		assertEquals(1, LibraryDatabase.queryInt(
			"SELECT home_branch_id FROM member WHERE id = 4"));
		assertEquals(4, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM member WHERE home_branch_id = 1"));
	}

	@Test
	void updateEmail() {
		assertEquals("bob@email.com", LibraryDatabase.queryString(
			"SELECT email FROM member WHERE id = 2"));

		LibraryDatabase.execute(
			"UPDATE member SET email = 'robert@newmail.com' WHERE id = 2");

		assertEquals("robert@newmail.com", LibraryDatabase.queryString(
			"SELECT email FROM member WHERE id = 2"));
		assertFalse(LibraryDatabase.queryExists(
			"SELECT 1 FROM member WHERE email = 'bob@email.com'"));
	}

	@Test
	void deleteMember() {
		assertEquals(8, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member"));

		LibraryDatabase.execute("DELETE FROM member WHERE id = 7");

		assertEquals(7, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member"));
		assertFalse(LibraryDatabase.queryExists(
			"SELECT 1 FROM member WHERE name = 'George Clark'"));
	}

	@Test
	void deleteAllChildrenMembers() {
		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM member WHERE member_type_id = 3"));

		LibraryDatabase.execute("DELETE FROM member WHERE member_type_id = 3");

		assertEquals(6, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member"));
		assertEquals(0, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM member WHERE member_type_id = 3"));
		// Regular and Premium unchanged
		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM member WHERE member_type_id = 1"));
		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM member WHERE member_type_id = 2"));
	}

	@Test
	void threeRegularThreePremiumTwoChildren() {
		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM member WHERE member_type_id = 1"));
		assertEquals(3, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM member WHERE member_type_id = 2"));
		assertEquals(2, LibraryDatabase.queryInt(
			"SELECT COUNT(*) FROM member WHERE member_type_id = 3"));
	}

	@Test
	void allMembersHaveUniqueEmails() {
		int total = LibraryDatabase.queryInt("SELECT COUNT(email) FROM member");
		int unique = LibraryDatabase.queryInt("SELECT COUNT(DISTINCT email) FROM member");
		assertEquals(total, unique);
	}

	// --- Constraint enforcement ---

	@Test
	void duplicateEmailIsRejected() {
		assertThrows(RuntimeException.class, () ->
			LibraryDatabase.execute(
				"INSERT INTO member VALUES (9, 'Fake Alice', 'alice@email.com', 1, 1)"));

		assertEquals(8, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member"));
	}

	@Test
	void invalidBranchIsRejected() {
		assertThrows(RuntimeException.class, () ->
			LibraryDatabase.execute(
				"INSERT INTO member VALUES (9, 'Nowhere Man', 'nowhere@email.com', 1, 99)"));

		assertEquals(8, LibraryDatabase.queryInt("SELECT COUNT(*) FROM member"));
	}

	// --- Tree visibility ---

	@Test
	void parentCatalogDataVisible() {
		assertEquals(10, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book"));
		assertEquals(18, LibraryDatabase.queryInt("SELECT COUNT(*) FROM book_copy"));
	}

	@Test
	void noLoansExistYet() {
		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM loan"));
		assertEquals(0, LibraryDatabase.queryInt("SELECT COUNT(*) FROM hold"));
	}

}
