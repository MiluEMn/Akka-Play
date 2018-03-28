package dto;

import java.io.Serializable;

public class RequestDataMessage implements Serializable {

  private String actorRef;

  public RequestDataMessage(String actorRef) {

    this.actorRef = actorRef;
  }

  public String getActorRef() {

    return actorRef;
  }
}
