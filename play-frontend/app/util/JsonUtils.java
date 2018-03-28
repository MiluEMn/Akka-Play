package util;

import com.fasterxml.jackson.databind.node.ArrayNode;

import play.libs.Json;

import java.util.List;

public class JsonUtils {

  public static <T> ArrayNode listToJson(List<T> list) {

    return list.stream()
        .collect(
            () -> Json.newArray(),
            (jsonNodes, item) -> jsonNodes.add(Json.toJson(item)),
            (jsonNodes, jsonNodes2) -> jsonNodes.addAll(jsonNodes2)
        );
  }
}
