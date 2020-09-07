package vn.zalopay.phucvt.fooapp.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServiceConfig {
  private VertxConfig vertxConfig;
  private MySQLConfig mySQLConfig;
  private CacheConfig cacheConfig;
  private JwtConfig jwtConfig;
  private ChatConfig chatConfig;
  int port;
  int wsPort;
}
