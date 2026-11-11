package org.simulatest.environment;

import java.util.Objects;
import java.util.Optional;

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
 * <p>A level opens once and closes once; every other call does nothing. A
 * level that never opened is the case of a setup that failed: nothing is
 * left open, and undoing the setup still undoes what the failed setup
 * wrote.</p>
 *
 * <p><b>Thread-safety:</b> not thread-safe, like the layer it drives.</p>
 */
public final class SetupLevel {

	private enum State { NOT_OPENED, OPEN, CLOSED }

	private static final InsistenceLayer NO_LAYER = new NoLayer();

	private final InsistenceLayer layer;
	private State state = State.NOT_OPENED;

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

	/** A level on the given layer, or one that does nothing when there is no layer. */
	public static SetupLevel onLayerIfAny(Optional<InsistenceLayer> layer) {
		return layer.map(SetupLevel::on).orElseGet(SetupLevel::withoutLayer);
	}

	public void open() {
		if (state != State.NOT_OPENED) return;

		layer.increaseLevel();
		state = State.OPEN;
	}

	/** Undoes what ran on top of the setup since the level opened or was last reset. */
	public void reset() {
		if (state == State.OPEN) layer.resetCurrentLevel();
	}

	/** Undoes what ran on top of the setup and keeps the setup data. */
	public void close() {
		State stateBeforeClosing = state;
		state = State.CLOSED;

		if (stateBeforeClosing == State.OPEN) layer.decreaseLevelOrCleanup();
	}

	/** Undoes what ran on top of the setup and the setup data itself. */
	public void closeUndoingSetup() {
		State stateBeforeClosing = state;
		state = State.CLOSED;

		if (stateBeforeClosing == State.OPEN) layer.decreaseLevelAndResetOrCleanup();
		if (stateBeforeClosing == State.NOT_OPENED) layer.resetCurrentLevel();
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
