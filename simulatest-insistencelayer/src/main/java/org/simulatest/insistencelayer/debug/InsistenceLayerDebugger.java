package org.simulatest.insistencelayer.debug;

import java.awt.GraphicsEnvironment;

import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.debug.gui.SQLWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single entry point for debugging test database state.
 *
 * <p>Automatically selects the best debugging interface based on the
 * execution environment:</p>
 * <ul>
 *   <li><b>Terminal</b> (mvn test, command line): opens an interactive
 *       SQL console via {@link InsistenceLayerConsole}</li>
 *   <li><b>IDE</b> (IntelliJ, Eclipse): opens the SQL Window
 *       via {@link SQLWindow}</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * <pre>
 * {@literal @}Test
 * public void myTest() throws Exception {
 *     // ... setup ...
 *     InsistenceLayerDebugger.debug(); // pauses here
 *     // ... assertions ...
 * }
 * </pre>
 *
 * <p>The test pauses at this line. You can inspect tables, run SQL queries,
 * and explore the database state. Type {@code resume} (terminal) or close
 * the window (IDE) to continue test execution.</p>
 *
 * <p>Remove the {@code debug()} call when done investigating.</p>
 *
 * @see InsistenceLayerConsole
 * @see SQLWindow
 * @see InsistenceLayer
 */
public class InsistenceLayerDebugger {

	private static final Logger logger = LoggerFactory.getLogger(InsistenceLayerDebugger.class);

	private InsistenceLayerDebugger() {
		// call InsistenceLayerDebugger.debug()
	}

	public static void debug() {
		if (isRunningFromIDE()) {
			logger.info("IDE detected, opening SQL Window");
			SQLWindow.debug();
		} else {
			logger.info("Opening CLI console");
			InsistenceLayerConsole.debug();
		}
	}

	private static boolean isRunningFromIDE() {
		if (GraphicsEnvironment.isHeadless()) return false;

		String classPath = System.getProperty("java.class.path", "");
		return classPath.contains("idea_rt.jar")
			|| classPath.contains("eclipse.launcher")
			|| classPath.contains("vscode")
			|| System.getProperty("idea.test.cyclic.buffer.size") != null;
	}

}
