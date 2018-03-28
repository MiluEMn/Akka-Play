package util;

import actors.ClusterListener;
import akka.actor.ActorSystem;
import akka.actor.Props;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ClusterListenerStarter {

  @Inject
  public ClusterListenerStarter(ActorSystem system) {

    system.actorOf(Props.create(ClusterListener.class), "clusterListener");
  }
}
