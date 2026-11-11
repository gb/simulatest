package org.simulatest.environment.test.testdouble;

import org.simulatest.environment.annotation.UseEnvironment;

// Its environment always fails.
@SuppressWarnings("java:S3577")
@UseEnvironment(FailingRowEnvironment.class)
public class FailingEnvironmentTestDouble extends NeverRunningTestDouble {
}
