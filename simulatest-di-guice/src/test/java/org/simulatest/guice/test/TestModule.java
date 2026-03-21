package org.simulatest.guice.test;

import com.google.inject.AbstractModule;

import org.simulatest.guice.test.testdouble.BritishTeacher;
import org.simulatest.guice.test.testdouble.LanguageTeacher;
import org.simulatest.guice.test.testdouble.mock.DatabaseMock;

public class TestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(LanguageTeacher.class).to(BritishTeacher.class);
		bind(DatabaseMock.class).asEagerSingleton();
	}

}
