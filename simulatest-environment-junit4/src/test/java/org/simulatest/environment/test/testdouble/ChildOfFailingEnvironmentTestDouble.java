package org.simulatest.environment.test.testdouble;

import org.simulatest.environment.annotation.UseEnvironment;

// The parent of its environment always fails.
@SuppressWarnings("java:S3577")
@UseEnvironment(ChildOfFailingRowEnvironment.class)
public class ChildOfFailingEnvironmentTestDouble extends NeverRunningTestDouble {
}
