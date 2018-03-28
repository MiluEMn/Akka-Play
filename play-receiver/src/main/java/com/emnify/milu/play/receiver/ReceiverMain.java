package com.emnify.milu.play.receiver;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.IOException;

public class ReceiverMain {

  public static void main(String[] args) throws IOException {

    Config conf = ConfigFactory.load("application.conf");
    ActorSystem system = ActorSystem.create("akka-play", conf);
    system.actorOf(Props.create(ReceiverActor.class), "receiver");

    System.in.read();
    system.terminate();
  }
}
