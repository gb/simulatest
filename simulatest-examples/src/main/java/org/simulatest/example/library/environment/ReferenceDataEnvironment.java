package org.simulatest.example.library.environment;

import org.simulatest.environment.environment.Environment;
import org.simulatest.example.library.LibraryDatabase;

/**
 * The Community Library — Environment Tree:
 *
 * <pre>
 *   ReferenceDataEnvironment  ◄── ROOT
 *     └── BranchesEnvironment
 *           ├── CatalogEnvironment
 *           │     └── MembersEnvironment
 *           │           └── LoansEnvironment
 *           └── StaffEnvironment
 * </pre>
 *
 * Root environment. Inserts reference data — genres and membership tiers —
 * the foundation every other environment builds upon. An environment trusts
 * its parent the same way a class trusts its superclass.
 *
 * <p>Schema is created by {@link LibraryDatabase#createSchema()} BEFORE the
 * tree runs, because DDL causes implicit commits that invalidate savepoints.
 */
public class ReferenceDataEnvironment implements Environment {

	@Override
	public void run() {
		// Genres
		LibraryDatabase.execute("INSERT INTO genre VALUES (1, 'Fiction')");
		LibraryDatabase.execute("INSERT INTO genre VALUES (2, 'Non-Fiction')");
		LibraryDatabase.execute("INSERT INTO genre VALUES (3, 'Science')");
		LibraryDatabase.execute("INSERT INTO genre VALUES (4, 'History')");
		LibraryDatabase.execute("INSERT INTO genre VALUES (5, 'Children')");

		// Member types: (name, max_checkouts, loan_period_days, fine_per_day_cents)
		LibraryDatabase.execute("INSERT INTO member_type VALUES (1, 'Regular',  5, 14, 25)");
		LibraryDatabase.execute("INSERT INTO member_type VALUES (2, 'Premium', 10, 21, 10)");
		LibraryDatabase.execute("INSERT INTO member_type VALUES (3, 'Children', 3, 14,  0)");
	}

}
