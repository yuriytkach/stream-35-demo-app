package com.yuriytkach;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.inject.Inject;

import org.assertj.core.data.TemporalUnitWithinOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class StorageServiceTest {

  @Inject
  StorageService tested;

  @Inject
  RedisDataSource redis;

  @Inject
  AppProperties appProperties;

  @AfterEach
  void clearRedis() {
    redis.flushall();
  }

  @Test
  void shouldWriteToRedis() {
    tested.setKey("abc");

    assertThat(redis.key().exists("abc")).isTrue();

    final long expiretime = redis.key().expiretime("abc");
    final Instant expireInstant = Instant.ofEpochSecond(expiretime);

    final Instant expectedExpireInstant = Instant.now().plusSeconds(appProperties.recordExpiration().toSeconds());

    assertThat(expireInstant).isCloseTo(expectedExpireInstant, new TemporalUnitWithinOffset(2, ChronoUnit.SECONDS));
  }

  @Test
  void shouldReadFromToRedis() {
    redis.value(String.class).set("abc", "abc");

    assertThat(tested.getKey("abc")).isPresent();
  }

  @Test
  void shouldNotFindInRedis() {
    assertThat(tested.getKey("xyz")).isEmpty();
  }

  @Test
  void shouldDeleteFromToRedis() {
    redis.value(String.class).set("abc", "abc");

    assertThat(tested.delKey("abc")).isPresent();

    assertThat(redis.key().exists("abc")).isFalse();
  }

  @Test
  void shouldNotDeleteFromToRedis() {
    assertThat(tested.delKey("xyz")).isEmpty();
  }

}
