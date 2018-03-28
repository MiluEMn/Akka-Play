package controllers;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static util.JsonUtils.listToJson;

import play.mvc.Controller;
import play.mvc.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Application extends Controller {

  public CompletionStage<Result> index() {

    return supplyAsync(() -> ok(listToJson(getEndpoints())));
  }

  public CompletionStage<Result> hello(final String name) {

    return supplyAsync(
        () -> ok("Hello, " + (null != name && !"".equals(name.trim()) ? name : "World") + "!"));
  }

  private static List<String> getEndpoints() {

    InputStream is = Application.class.getClassLoader().getResourceAsStream("routes");
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    final Pattern pattern = Pattern.compile(
        "^(?<method>(GET|POST|PUT|DELETE|PATCH|HEAD))[\\t ]+(?<url>/[a-zA-Z0-9()/$:?=]*)[\\t ]+.*"
    );

    try {
      return reader.lines()
          .map(str -> getFormattedLine(pattern.matcher(str), "method", "url"))
          .filter(str -> !"".equals(str))
          .collect(Collectors.toList());
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static String getFormattedLine(Matcher matcher, String... groups) {

    if (matcher.matches()) {
      return Arrays.stream(groups)
          .map(group -> matcher.group(group))
          .collect(Collectors.joining(" "));
    }

    return "";
  }
}
