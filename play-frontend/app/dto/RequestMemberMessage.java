package dto;

import java.io.Serializable;

public class RequestMemberMessage implements Serializable {

  private String requestedRole;

  public RequestMemberMessage(String requestedRole) {

    this.requestedRole = requestedRole;
  }

  public String getRequestedRole() {

    return requestedRole;
  }
}
