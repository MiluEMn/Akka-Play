package controllers;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static util.JsonUtils.listToJson;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import actors.DataActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import dto.RequestDataMessage;
import dto.RequestMemberMessage;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.BodyParser.Of;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.Await;
import scala.concurrent.Future;
import util.With;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class Data extends Controller {

  private ActorRef dataActor;

  @Inject
  public Data(ActorSystem system) {

    dataActor = system.actorOf(Props.create(DataActor.class), "data");
  }

  @Of(BodyParser.Json.class)
  public CompletionStage<Result> sendMessage() {

    return sendMessage.with(request().body().asJson());
  }

  public CompletionStage<Result> retrieveData(int id) {

    return supplyAsync(() -> {
      List<String> receivers = retrieveReceivers();

      if (null == receivers) {
        return internalServerError();
      } else if (id >= receivers.size()) {
        return badRequest("Requested receiever #" + id + " not found");
      }

      List<String> result;
      Timeout timeout = new Timeout(500L, TimeUnit.MILLISECONDS);
      Future<Object> request;

      RequestDataMessage rdm = new RequestDataMessage(receivers.get(id) + "/user/receiver");
      try {
        request = Patterns.ask(dataActor, rdm, timeout);
        result = (List<String>) Await.result(request, timeout.duration());
      } catch (Exception e) {
        return internalServerError();
      }

      return ok(listToJson(result));
    });
  }

  public CompletionStage<Result> getReceivers() {

    return supplyAsync(() -> {
      List<String> receivers = retrieveReceivers();

      if (null == receivers) {
        return internalServerError();
      }

      return ok(listToJson(receivers));
    });
  }

  public CompletionStage<Result> getReceiver(int id) {

    return supplyAsync(() -> {
      List<String> receivers = retrieveReceivers();

      if (null == receivers) {
        return internalServerError();
      }

      if (id >= receivers.size()) {
        return badRequest("Requested receiver #" + id + " not found");
      }

      return ok(Json.toJson(receivers.get(id)));
    });
  }

  private With<JsonNode, CompletionStage<Result>> sendMessage = payload -> supplyAsync(() -> {

    if (null == payload || null == payload.get("message") ||
        "".equals(payload.get("message").asText().trim())) {
      return badRequest("Request must contain a \"message\" field");
    }

    dataActor.tell(payload.get("message").asText(), ActorRef.noSender());
    return ok();
  });

  private List<String> retrieveReceivers() {

    List<String> result;
    Timeout timeout = new Timeout(500L, TimeUnit.MILLISECONDS);
    Future<Object> request;

    try {
      request = Patterns.ask(dataActor, new RequestMemberMessage("receiver"), timeout);
      result = (List<String>) Await.result(request, timeout.duration());
    } catch (Exception e) {
      return null;
    }

    return result;
  }
}
