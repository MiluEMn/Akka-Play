package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;
import static play.test.Helpers.start;
import static play.test.Helpers.stop;

import com.google.inject.Guice;

import org.junit.Test;
import play.ApplicationLoader.Context;
import play.Environment;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.twirl.api.Content;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

public class ApplicationTest {

  @Inject
  private play.Application application;

  @Test
  public void testHello() throws ExecutionException, InterruptedException {

    Result result = retrieveResult(null);
    assertResult(result, OK, "<h1>Hello, World!</h1>");

    result = retrieveResult("");
    assertResult(result, OK, "<h1>Hello, World!</h1>");

    result = retrieveResult("   ");
    assertResult(result, OK, "<h1>Hello, World!</h1>");

    result = retrieveResult("Michael");
    assertResult(result, OK, "<h1>Hello, Michael!</h1>");
  }

  @Test
  public void testHelloTemplate() {

    Content html = views.html.hello.render(null);
    assertRenderedConent(html, "<h1>Hello, !</h1>");

    html = views.html.hello.render("");
    assertRenderedConent(html, "<h1>Hello, !</h1>");

    html = views.html.hello.render("   ");
    assertRenderedConent(html, "<h1>Hello,    !</h1>");

    html = views.html.hello.render("Michael");
    assertRenderedConent(html, "<h1>Hello, Michael!</h1>");
  }

  @Test
  public void testHelloRoute() {

    try {
      setupApplication();

      Result result = retrieveRouteResult(GET, "/hello");
      assertResult(result, OK, "<h1>Hello, World!</h1>");

      result = retrieveRouteResult(GET, "/hello?foo=bar");
      assertResult(result, OK, "<h1>Hello, World!</h1>");

      result = retrieveRouteResult(GET, "/hello?name=");
      assertResult(result, OK, "<h1>Hello, World!</h1>");

      result = retrieveRouteResult(GET, "/hello?name=Michael");
      assertResult(result, OK, "<h1>Hello, Michael!</h1>");

      result = retrieveRouteResult(GET, "/hello?foo=bar&name=Michael");
      assertResult(result, OK, "<h1>Hello, Michael!</h1>");

      result = retrieveRouteResult(POST, "/hello");
      assertResult(result, NOT_FOUND, "<title>Action Not Found</title>");
    } finally {
      stopApplication();
    }
  }

  private Result retrieveResult(String input) throws ExecutionException, InterruptedException {

    return new Application().hello(input).toCompletableFuture().get();
  }

  private Result retrieveRouteResult(String method, String uri) {

    RequestBuilder req = fakeRequest()
        .method(method)
        .uri(uri);

    return route(application, req);
  }

  private void assertResult(Result result, int expectedStatus, String expectedContent) {

    assertEquals(expectedStatus, result.status());
    assertTrue(contentAsString(result).contains(expectedContent));
  }

  private void assertRenderedConent(Content html, String expectedContent) {

    assertEquals("text/html", html.contentType());
    assertTrue(contentAsString(html).contains(expectedContent));
  }

  private void setupApplication() {

    GuiceApplicationBuilder builder = new GuiceApplicationLoader()
        .builder(new Context(Environment.simple()));

    Guice.createInjector(builder.applicationModule()).injectMembers(this);

    start(application);
  }

  private void stopApplication() {

    stop(application);
  }
}