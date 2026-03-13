package org.simulatest.example.library.environment;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.environment.Environment;
import org.simulatest.example.library.LibraryDatabase;

/**
 * Level 3a — Book Catalog and Physical Copies.
 *
 * <p>Parent: {@link BranchesEnvironment}.
 * Sibling: {@link StaffEnvironment} — its data is invisible here and vice versa,
 * because the Insistence Layer rolls back each sibling subtree automatically.
 */
@EnvironmentParent(BranchesEnvironment.class)
public class CatalogEnvironment implements Environment {

	@Override
	public void run() {
		// 10 books across all 5 genres
		// (id, title, author, isbn, genre_id, publication_year)
		LibraryDatabase.execute("INSERT INTO book VALUES (1, 'The Great Adventure',       'Alice Walker',    '9780001111111', 1, 2020)");
		LibraryDatabase.execute("INSERT INTO book VALUES (6, 'Mystery at Midnight',       'Frank Green',     '9780006666666', 1, 2021)");
		LibraryDatabase.execute("INSERT INTO book VALUES (2, 'Database Design Patterns',  'Bob Smith',       '9780002222222', 2, 2019)");
		LibraryDatabase.execute("INSERT INTO book VALUES (7, 'Cooking for Engineers',     'Grace Lee',       '9780007777777', 2, 2020)");
		LibraryDatabase.execute("INSERT INTO book VALUES (3, 'Quantum Physics Explained', 'Carol Johnson',   '9780003333333', 3, 2021)");
		LibraryDatabase.execute("INSERT INTO book VALUES (8, 'The Solar System',          'Henry Davis',     '9780008888888', 3, 2019)");
		LibraryDatabase.execute("INSERT INTO book VALUES (4, 'World War II: A History',   'David Brown',     '9780004444444', 4, 2018)");
		LibraryDatabase.execute("INSERT INTO book VALUES (9, 'Ancient Rome',              'Irene Martinez',  '9780009999999', 4, 2017)");
		LibraryDatabase.execute("INSERT INTO book VALUES (5, 'The Little Explorer',       'Eve White',       '9780005555555', 5, 2022)");
		LibraryDatabase.execute("INSERT INTO book VALUES (10, 'Dragons and Wizards',      'Jack Wilson',     '9780010101010', 5, 2023)");

		// 18 copies distributed across 3 branches (id, book_id, branch_id, status)
		// Downtown: 7 copies, Westside: 6 copies, Eastville: 5 copies
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (1,  1,  1, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (2,  1,  2, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (3,  2,  1, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (4,  2,  3, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (5,  3,  2, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (6,  3,  3, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (7,  4,  1, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (8,  5,  2, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (9,  5,  3, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (10, 5,  1, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (11, 6,  1, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (12, 6,  2, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (13, 7,  3, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (14, 8,  1, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (15, 8,  2, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (16, 9,  3, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (17, 10, 1, 'AVAILABLE')");
		LibraryDatabase.execute("INSERT INTO book_copy VALUES (18, 10, 2, 'AVAILABLE')");
	}

}
