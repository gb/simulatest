package org.simulatest.di.guice;

import java.util.List;

import com.google.inject.Module;

public interface GuiceModuleProvider {

	List<Module> modules();

}
