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
 * <h2>Usage</h2>
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
public final class InsistenceLayerDebugger {

	private static final Logger logger = LoggerFactory.getLogger(InsistenceLayerDebugger.class);

	private InsistenceLayerDebugger() {
	}

	private static final String UI_PROPERTY = "simulatest.debug.ui";
	private static final String UI_GUI = "gui";
	private static final String UI_CONSOLE = "console";

	/**
	 * Pauses the test and opens an interactive debugger. The UI is chosen by:
	 * <ol>
	 *   <li>System property {@code -Dsimulatest.debug.ui=gui|console} (explicit override)</li>
	 *   <li>IDE heuristic based on classpath tokens, if no override is set</li>
	 * </ol>
	 */
	public static void debug() {
		if (shouldUseGui()) {
			logger.info("Opening SQL Window");
			SQLWindow.debug();
		} else {
			logger.info("Opening CLI console");
			InsistenceLayerConsole.debug();
		}
	}

	private static boolean shouldUseGui() {
		String override = System.getProperty(UI_PROPERTY);
		if (UI_GUI.equalsIgnoreCase(override)) return true;
		if (UI_CONSOLE.equalsIgnoreCase(override)) return false;
		return isRunningFromIDE();
	}

	private static boolean isRunningFromIDE() {
		if (GraphicsEnvironment.isHeadless()) return false;

		String classPath = System.getProperty("java.class.path", "");
		return classPath.contains("idea_rt.jar")     // IntelliJ IDEA test runner
			|| classPath.contains("eclipse.launcher") // Eclipse launcher
			|| classPath.contains("vscode")           // VS Code Java test runner
			|| System.getProperty("idea.test.cyclic.buffer.size") != null; // IDEA diagnostic flag
	}

}
