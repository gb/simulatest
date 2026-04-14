package org.simulatest.example.library.environment;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.Environment;
import org.simulatest.example.library.LibraryDatabase;

/**
 * Level 2 — Library Branches.
 *
 * <p>Parent: {@link ReferenceDataEnvironment} (genres, member types).
 * Children: {@link CatalogEnvironment} and {@link StaffEnvironment} —
 * siblings whose data is automatically isolated by the Insistence Layer.
 */
@EnvironmentParent(ReferenceDataEnvironment.class)
public final class BranchesEnvironment implements Environment {

	@Override
	public void run() {
		LibraryDatabase.execute("INSERT INTO branch VALUES (1, 'Downtown Branch', '100 Main Street')");
		LibraryDatabase.execute("INSERT INTO branch VALUES (2, 'Westside Branch', '250 Oak Avenue')");
		LibraryDatabase.execute("INSERT INTO branch VALUES (3, 'Eastville Branch', '75 Elm Road')");
	}

}
