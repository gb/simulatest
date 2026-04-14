package org.simulatest.example.library.environment;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.Environment;
import org.simulatest.example.library.LibraryDatabase;

/**
 * Level 4 — Library Members.
 *
 * <p>Parent: {@link CatalogEnvironment} (books and copies).
 * Child: {@link LoansEnvironment} (loans and holds — not yet visible here).
 */
@EnvironmentParent(CatalogEnvironment.class)
public final class MembersEnvironment implements Environment {

	@Override
	public void run() {
		// 8 members: 3 Regular, 3 Premium, 2 Children (id, name, email, type_id, branch_id)
		LibraryDatabase.execute("INSERT INTO member VALUES (1, 'Alice Thompson',  'alice@email.com',   1, 1)");
		LibraryDatabase.execute("INSERT INTO member VALUES (2, 'Bob Martinez',    'bob@email.com',     1, 2)");
		LibraryDatabase.execute("INSERT INTO member VALUES (7, 'George Clark',    'george@email.com',  1, 3)");
		LibraryDatabase.execute("INSERT INTO member VALUES (3, 'Charlie Wilson',  'charlie@email.com', 2, 1)");
		LibraryDatabase.execute("INSERT INTO member VALUES (4, 'Diana Lee',       'diana@email.com',   2, 3)");
		LibraryDatabase.execute("INSERT INTO member VALUES (8, 'Hannah White',    'hannah@email.com',  2, 2)");
		LibraryDatabase.execute("INSERT INTO member VALUES (5, 'Ethan Brown',     'ethan@email.com',   3, 2)");
		LibraryDatabase.execute("INSERT INTO member VALUES (6, 'Fiona Davis',     'fiona@email.com',   3, 1)");
	}

}
