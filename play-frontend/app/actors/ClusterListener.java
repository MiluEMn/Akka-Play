package actors;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.Member;
import dto.RequestMemberMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ClusterListener extends AbstractActor {

  private final Cluster cluster = Cluster.get(getContext().system());
  private final Map<String, Set<String>> members = new HashMap();

  @Override
  public void preStart() {

    cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), MemberEvent.class);
  }

  @Override
  public void postStop() {

    cluster.unsubscribe(getSelf());
  }

  @Override
  public Receive createReceive() {

    return receiveBuilder()
        .match(MemberUp.class, this::onMemberUp)
        .match(MemberRemoved.class, this::onMemberRemoved)
        .match(RequestMemberMessage.class,
            msg -> getSender().tell(provideMembersForRole(msg.getRequestedRole()), getSelf()))
        .build();
  }

  private void onMemberUp(MemberUp memberUp) {

    Member member = memberUp.member();
    Set<String> roles = member.getRoles().stream()
        .filter(role -> !"dc-default".equals(role))
        .collect(Collectors.toCollection(HashSet::new));

    members.put(member.address().toString(), roles);
  }

  private void onMemberRemoved(MemberRemoved memberRemoved) {

    members.remove(memberRemoved.member().address());
  }

  private List<String> provideMembersForRole(String role) {

    return members.keySet().stream()
        .filter(key -> members.get(key).contains(role))
        .collect(Collectors.toList());
  }
}
