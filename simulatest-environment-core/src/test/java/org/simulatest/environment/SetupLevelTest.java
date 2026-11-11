package org.simulatest.environment;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
	public void openShouldIncreaseTheLevelOnlyOnce() {
		level.open();
		level.open();

		verify(layer, times(1)).increaseLevel();
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
	public void aLevelWithoutLayerShouldAcceptEveryCall() {
		SetupLevel withoutLayer = SetupLevel.withoutLayer();

		withoutLayer.open();
		withoutLayer.close();
		withoutLayer.closeUndoingSetup();
	}

}
