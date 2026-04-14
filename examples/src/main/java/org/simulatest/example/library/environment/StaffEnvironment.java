package org.simulatest.example.library.environment;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.Environment;
import org.simulatest.example.library.LibraryDatabase;

/**
 * Level 3b — Library Staff.
 *
 * <p>Parent: {@link BranchesEnvironment}.
 * Sibling: {@link CatalogEnvironment} — by the time this environment runs,
 * the entire Catalog subtree (books, members, loans) has been rolled back.
 * See {@code StaffTest} for assertions that prove this sibling isolation.
 */
@EnvironmentParent(BranchesEnvironment.class)
public final class StaffEnvironment implements Environment {

	@Override
	public void run() {
		// 7 staff across 3 branches (id, name, role, branch_id)
		LibraryDatabase.execute("INSERT INTO staff VALUES (1, 'Margaret Chen',   'HEAD_LIBRARIAN', 1)");
		LibraryDatabase.execute("INSERT INTO staff VALUES (2, 'Robert Taylor',   'LIBRARIAN',      1)");
		LibraryDatabase.execute("INSERT INTO staff VALUES (7, 'Linda Garcia',    'ASSISTANT',      1)");
		LibraryDatabase.execute("INSERT INTO staff VALUES (3, 'Susan Park',      'HEAD_LIBRARIAN', 2)");
		LibraryDatabase.execute("INSERT INTO staff VALUES (4, 'James Wilson',    'ASSISTANT',      2)");
		LibraryDatabase.execute("INSERT INTO staff VALUES (5, 'Patricia Adams',  'HEAD_LIBRARIAN', 3)");
		LibraryDatabase.execute("INSERT INTO staff VALUES (6, 'Michael Brown',   'LIBRARIAN',      3)");
	}

}
