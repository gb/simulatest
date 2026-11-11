package org.simulatest.environment.test.testdouble;

import org.junit.Test;
import org.simulatest.environment.test.TestSetup;

// A test class whose only test never runs, because the environment each
// subclass names with @UseEnvironment never comes up.
//
// The names do not end in Test on purpose: Surefire would then run the classes
// on their own, without their environment.
@SuppressWarnings("java:S3577")
public abstract class NeverRunningTestDouble {

	static { TestSetup.configure(); }

	// Has no assertion on purpose: this method only gives JUnit a test to find.
	@Test
	@SuppressWarnings("java:S2699")
	public void shouldNeverRun() {
		throw new UnsupportedOperationException("Should never be reached: the environment fails first");
	}

}
