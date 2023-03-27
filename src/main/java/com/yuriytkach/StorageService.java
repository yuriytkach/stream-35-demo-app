package com.yuriytkach;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class StorageService {

  @Inject
  RedisDataSource redis;

  @Inject
  AppProperties properties;

  public Optional<String> getKey(final String name) {
    final KeyCommands<String> keyCommands = redis.key();
    return keyCommands.exists(name) ? Optional.of(name) : Optional.empty();
  }

  public void setKey(final String name) {
    log.info("Setting key for name: {}", name);
    final ValueCommands<String, String> valueCommands = redis.value(String.class);
    valueCommands.set(name, name);

    log.info("Setting expiration for key {}: {}", name, properties.recordExpiration());
    redis.key().expire(name, properties.recordExpiration());
  }

  public Optional<String> delKey(final String name) {
    final KeyCommands<String> keyCommands = redis.key();
    return keyCommands.del(name) > 0 ? Optional.of(name) : Optional.empty();
  }
}
