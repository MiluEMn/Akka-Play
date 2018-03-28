package com.emnify.milu.play.receiver;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import dto.RequestDataMessage;

import java.util.ArrayList;
import java.util.List;

public class ReceiverActor extends AbstractActor {

  private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
  private final List<String> receivedMessages = new ArrayList<>();

  @Override
  public Receive createReceive() {

    return receiveBuilder()
        .match(String.class, receivedMessages::add)
        .match(RequestDataMessage.class, msg -> getSender().tell(receivedMessages, getSelf()))
        .matchAny(msg -> log
            .warning("Unexpectedly got: {} of type {}", msg, msg.getClass().getSimpleName()))
        .build();
  }
}
