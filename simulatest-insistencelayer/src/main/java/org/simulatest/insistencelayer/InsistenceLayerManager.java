package org.simulatest.insistencelayer;

/**
 * Controls the depth of the Insistence Layer, a boundary where all data
 * changes are temporary.
 *
 * <p>Each level deepens the layer. Data written at a given level exists
 * only while that level is active and is undone when the level is left.
 * Consumers entering the same level always see the same initial state.</p>
 *
 * <p>"Insist, insist, insist ... but never persist."</p>
 *
 * <h3>Typical lifecycle</h3>
 * <pre>
 *   increaseLevel()          // capture state before environment subtree
 *     increaseLevel()        // capture state before deeper environment
 *       resetCurrentLevel()  // restore between sibling tests
 *       resetCurrentLevel()
 *     decreaseLevel()        // restore state, leaving deeper environment
 *   decreaseLevel()          // restore state, leaving subtree
 * </pre>
 *
 * @see LocalInsistenceLayerManager
 */
public interface InsistenceLayerManager {

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
	void decreaseAllLevels();

	/**
	 * Adjusts the stack to the given target level by increasing or
	 * decreasing as needed.
	 *
	 * @param level the target level (must be non-negative)
	 * @throws IllegalArgumentException if level is negative
	 */
	void setLevelTo(int level);

}
