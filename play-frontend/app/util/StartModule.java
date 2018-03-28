package util;

import com.google.inject.AbstractModule;

public class StartModule extends AbstractModule {

  @Override
  protected void configure() {

    bind(ClusterListenerStarter.class).asEagerSingleton();
  }
}
