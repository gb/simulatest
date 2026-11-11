package org.simulatest.environment;

import java.util.Objects;

import org.simulatest.insistencelayer.InsistenceLayer;

/**
 * The Insistence Layer level that sits on top of a setup: the run of an
 * environment, or the class setup of a test class.
 *
 * <p>A setup writes its data in the level that is current when it runs. This
 * level opens right after, so what runs on top of the setup can be reset any
 * number of times without losing the setup data. Closing the level undoes
 * what ran on top; closing it and undoing the setup also undoes the setup
 * data in the level below, with a single rollback.</p>
 *
 * <p>A level that was never opened is safe to close. That is the case when
 * the setup failed: nothing is left open, and undoing the setup still undoes
 * what the failed setup wrote.</p>
 *
 * <p><b>Thread-safety:</b> not thread-safe, like the layer it drives.</p>
 */
public final class SetupLevel {

	private static final InsistenceLayer NO_LAYER = new NoLayer();

	private final InsistenceLayer layer;
	private boolean open;

	private SetupLevel(InsistenceLayer layer) {
		this.layer = layer;
	}

	public static SetupLevel on(InsistenceLayer layer) {
		return new SetupLevel(Objects.requireNonNull(layer, "layer must not be null"));
	}

	/** A level for a run with no Insistence Layer configured; it does nothing. */
	public static SetupLevel withoutLayer() {
		return new SetupLevel(NO_LAYER);
	}

	/** Opens the level. Does nothing when it is already open. */
	public void open() {
		if (open) return;

		layer.increaseLevel();
		open = true;
	}

	/** Undoes what ran on top of the setup and keeps the setup data. */
	public void close() {
		if (!open) return;

		open = false;
		layer.decreaseLevelOrCleanup();
	}

	/** Undoes what ran on top of the setup and the setup data itself. */
	public void closeUndoingSetup() {
		if (!open) {
			layer.resetCurrentLevel();
			return;
		}

		open = false;
		layer.decreaseLevelAndResetOrCleanup();
	}

	private static final class NoLayer implements InsistenceLayer {

		@Override
		public int getCurrentLevel() {
			return 0;
		}

		@Override
		public void increaseLevel() { }

		@Override
		public void decreaseLevel() { }

		@Override
		public void resetCurrentLevel() { }

	}

}
