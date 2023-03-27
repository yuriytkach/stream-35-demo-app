package com.yuriytkach;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
public class GreetingResourceTest {

  @InjectMock
  StorageService storageService;

  @Test
  public void shouldReturn200OnSuccessFindInRedis() {
    when(storageService.getKey(any())).thenReturn(Optional.of("hello"));

    given()
      .when().get("/hello/{name}", "abc")
      .then()
      .statusCode(200);

    verify(storageService).getKey("abc");
  }

  @Test
  public void shouldReturn404OnFailedFindInRedis() {
    when(storageService.getKey(any())).thenReturn(Optional.empty());

    given()
      .when().get("/hello/{name}", "abc")
      .then()
      .statusCode(404);

    verify(storageService).getKey("abc");
  }

  @Test
  public void shouldReturn201AfterWriteToRedis() {
    given()
      .when().post("/hello/{name}", "abc")
      .then()
      .statusCode(201);

    verify(storageService).setKey("abc");
  }

  @Test
  public void shouldReturn500AfterFailureWriteToRedis() {
    doThrow(IllegalArgumentException.class).when(storageService).setKey(any());

    given()
      .when().post("/hello/{name}", "abc")
      .then()
      .statusCode(500);

    verify(storageService).setKey("abc");
  }

  @Test
  public void shouldReturn204AfterDeleteFromRedis() {
    when(storageService.delKey(any())).thenReturn(Optional.of("hello"));

    given()
      .when().delete("/hello/{name}", "abc")
      .then()
      .statusCode(204);

    verify(storageService).delKey("abc");
  }

  @Test
  public void shouldReturn404AfterDeleteFromRedisDidntFindRecord() {
    when(storageService.delKey(any())).thenReturn(Optional.empty());

    given()
      .when().delete("/hello/{name}", "abc")
      .then()
      .statusCode(404);

    verify(storageService).delKey("abc");
  }

}
