package org.simulatest.environment;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.simulatest.insistencelayer.InsistenceLayer;

public class SetupLevelTest {

	private InsistenceLayer layer;
	private SetupLevel level;

	@Before
	public void setup() {
		layer = mock(InsistenceLayer.class);
		level = SetupLevel.on(layer);
	}

	@Test
	public void shouldNotAcceptANullLayer() {
		assertThrows(NullPointerException.class, () -> SetupLevel.on(null));
	}

	@Test
	public void openShouldIncreaseTheLevelOnlyOnce() {
		level.open();
		level.open();

		verify(layer, times(1)).increaseLevel();
	}

	@Test
	public void openShouldDoNothingOnceTheLevelIsClosed() {
		level.open();
		level.close();

		level.open();

		verify(layer, times(1)).increaseLevel();
	}

	@Test
	public void resetShouldResetTheOpenLevel() {
		level.open();

		level.reset();

		verify(layer).resetCurrentLevel();
	}

	@Test
	public void resetShouldDoNothingWhenTheLevelNeverOpened() {
		level.reset();

		verifyNoInteractions(layer);
	}

	@Test
	public void resetShouldDoNothingOnceTheLevelIsClosed() {
		level.open();
		level.close();

		level.reset();

		verify(layer, never()).resetCurrentLevel();
	}

	@Test
	public void closeShouldDecreaseTheLevelItOpened() {
		level.open();

		level.close();

		InOrder order = inOrder(layer);
		order.verify(layer).increaseLevel();
		order.verify(layer).decreaseLevelOrCleanup();
		verifyNoMoreInteractions(layer);
	}

	@Test
	public void closeShouldDoNothingWhenTheLevelNeverOpened() {
		level.close();

		verifyNoInteractions(layer);
	}

	@Test
	public void closeShouldDoNothingTheSecondTime() {
		level.open();
		level.close();

		level.close();

		verify(layer, times(1)).decreaseLevelOrCleanup();
	}

	@Test
	public void theLevelShouldCountAsClosedEvenWhenClosingFails() {
		doThrow(new IllegalStateException("rollback failed")).when(layer).decreaseLevelOrCleanup();
		level.open();
		assertThrows(IllegalStateException.class, level::close);

		level.close();

		verify(layer, times(1)).decreaseLevelOrCleanup();
	}

	@Test
	public void closeUndoingSetupShouldLeaveTheLevelAndResetTheOneBelowInOneStep() {
		level.open();

		level.closeUndoingSetup();

		InOrder order = inOrder(layer);
		order.verify(layer).increaseLevel();
		order.verify(layer).decreaseLevelAndResetOrCleanup();
		verifyNoMoreInteractions(layer);
	}

	@Test
	public void closeUndoingSetupShouldOnlyResetWhenTheLevelNeverOpened() {
		level.closeUndoingSetup();

		verify(layer).resetCurrentLevel();
		verifyNoMoreInteractions(layer);
	}

	@Test
	public void closeUndoingSetupShouldDoNothingOnceTheLevelIsClosed() {
		level.open();
		level.close();

		level.closeUndoingSetup();

		verify(layer, never()).decreaseLevelAndResetOrCleanup();
		verify(layer, never()).resetCurrentLevel();
	}

	@Test
	public void closeUndoingSetupShouldDoNothingTheSecondTime() {
		level.closeUndoingSetup();

		level.closeUndoingSetup();

		verify(layer, times(1)).resetCurrentLevel();
	}

	@Test
	public void onLayerIfAnyShouldDriveTheLayerWhenThereIsOne() {
		SetupLevel onLayer = SetupLevel.onLayerIfAny(Optional.of(layer));

		onLayer.open();

		verify(layer).increaseLevel();
	}

	@Test
	public void aLevelWithoutLayerShouldNotThrowOnAnyCall() {
		SetupLevel withoutLayer = SetupLevel.onLayerIfAny(Optional.empty());

		withoutLayer.open();
		withoutLayer.reset();
		withoutLayer.close();
		withoutLayer.closeUndoingSetup();
	}

}
