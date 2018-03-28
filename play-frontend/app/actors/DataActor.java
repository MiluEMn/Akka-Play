package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;
import dto.RequestDataMessage;
import dto.RequestMemberMessage;

public class DataActor extends AbstractActor {

  private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
  private final ActorRef router = getContext().system()
      .actorOf(FromConfig.getInstance().props(), "messageRouter");
  private final ActorSelection clusterListener = getContext().system()
      .actorSelection("/user/clusterListener");

  @Override
  public Receive createReceive() {

    return receiveBuilder()
        .match(String.class, str -> router.forward(str, getContext()))
        .match(RequestMemberMessage.class, msg -> clusterListener.forward(msg, getContext()))
        .match(RequestDataMessage.class, this::forwardDataRequest)
        .matchAny(msg -> log
            .warning("Unexpectedly got: {} of type {}", msg, msg.getClass().getSimpleName()))
        .build();
  }

  private void forwardDataRequest(RequestDataMessage requestDataMessage) {

    ActorSelection targetActor = getContext().system()
        .actorSelection(requestDataMessage.getActorRef());
    targetActor.forward(requestDataMessage, getContext());
  }
}
