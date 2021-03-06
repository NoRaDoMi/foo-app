package vn.zalopay.phucvt.fooapp.dagger;

import dagger.Component;
import io.vertx.core.Vertx;
import vn.zalopay.phucvt.fooapp.grpc.gRPCServer;
import vn.zalopay.phucvt.fooapp.server.RestfulAPI;
import vn.zalopay.phucvt.fooapp.server.WebSocketServer;

import javax.inject.Singleton;

@Singleton
@Component(modules = {ServiceModule.class})
public interface ServiceComponent {
  RestfulAPI getRestfulAPI();

  WebSocketServer getWebSocketServer();

  gRPCServer getGRPCServer();

  Vertx getVertx();
}
