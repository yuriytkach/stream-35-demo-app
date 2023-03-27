package com.yuriytkach;


import java.time.Duration;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "app")
public interface AppProperties {
  Duration recordExpiration();
}
