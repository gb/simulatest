package org.simulatest.insistencelayer;

/**
 * A transactional sandbox for your database.
 *
 * <p>The Insistence Layer wraps a database connection so that all data
 * changes are temporary. It maintains a stack of checkpoints (levels);
 * data written at a given level exists only while that level is active
 * and is undone when the level is left. Consumers entering the same
 * level always see the same initial state.</p>
 *
 * <p>"Insist, insist, insist ... but never persist."</p>
 *
 * <h2>Typical lifecycle</h2>
 * <pre>
 *   increaseLevel()          // capture state before environment subtree
 *     increaseLevel()        // capture state before deeper environment
 *       resetCurrentLevel()  // restore between sibling tests
 *       resetCurrentLevel()
 *     decreaseLevel()        // restore state, leaving deeper environment
 *   decreaseLevel()          // restore state, leaving subtree
 * </pre>
 *
 * @see LocalInsistenceLayer
 */
public interface InsistenceLayer extends AutoCloseable {

	/**
	 * Returns the current depth of the checkpoint stack.
	 * Zero means the layer is inactive (no checkpoints held).
	 */
	int getCurrentLevel();

	/**
	 * Captures the current state and pushes a new checkpoint onto the stack.
	 */
	void increaseLevel();

	/**
	 * Restores the state captured by the topmost checkpoint and removes it
	 * from the stack.
	 *
	 * @throws IllegalStateException if the stack is already at level 0
	 */
	void decreaseLevel();

	/**
	 * Restores the state captured by the topmost checkpoint without removing
	 * it. All changes made at the current level are undone, bringing the
	 * state back to what it was right after the last {@link #increaseLevel()}.
	 *
	 * <p>Does nothing if the stack is at level 0.</p>
	 */
	void resetCurrentLevel();

	/**
	 * Restores all levels back to zero, equivalent to {@code setLevelTo(0)}.
	 */
	default void decreaseAllLevels() {
		setLevelTo(0);
	}

	/**
	 * Adjusts the stack to the given target level by increasing or
	 * decreasing as needed.
	 *
	 * @param level the target level (must be non-negative)
	 * @throws IllegalArgumentException if level is negative
	 */
	default void setLevelTo(int level) {
		if (level < 0) throw new IllegalArgumentException("Level cannot be negative");

		while (getCurrentLevel() > level) decreaseLevel();
		while (getCurrentLevel() < level) increaseLevel();
	}

	/**
	 * Releases any resources held by this layer. Default is a no-op for
	 * in-process layers; remote implementations override to tear down
	 * their transport.
	 */
	@Override
	default void close() { }

	/**
	 * Runs the given action inside a level boundary: {@link #increaseLevel()}
	 * beforehand, {@link #decreaseLevel()} on normal completion, and
	 * {@link #decreaseAllLevels()} as emergency cleanup if the action throws.
	 * A secondary cleanup failure is attached as suppressed to the original.
	 */
	default void runIsolated(Runnable action) {
		increaseLevel();
		try {
			action.run();
			decreaseLevel();
		} catch (RuntimeException primary) {
			try {
				decreaseAllLevels();
			} catch (RuntimeException cleanup) {
				primary.addSuppressed(cleanup);
			}
			throw primary;
		}
	}

	/**
	 * Attempts to {@link #decreaseLevel()}. If that fails, falls back to
	 * {@link #decreaseAllLevels()} as emergency cleanup, suppressing any
	 * secondary exception onto the original.
	 *
	 * @throws RuntimeException the original exception from {@code decreaseLevel()}
	 */
	default void decreaseLevelOrCleanup() {
		try {
			decreaseLevel();
		} catch (RuntimeException exception) {
			try {
				decreaseAllLevels();
			} catch (RuntimeException cleanupException) {
				exception.addSuppressed(cleanupException);
			}
			throw exception;
		}
	}

}
