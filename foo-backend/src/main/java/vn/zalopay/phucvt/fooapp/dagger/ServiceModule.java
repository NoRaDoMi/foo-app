package vn.zalopay.phucvt.fooapp.dagger;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import lombok.Builder;
import vn.zalopay.phucvt.fooapp.cache.*;
import vn.zalopay.phucvt.fooapp.config.ServiceConfig;
import vn.zalopay.phucvt.fooapp.da.*;
import vn.zalopay.phucvt.fooapp.grpc.AuthInterceptor;
import vn.zalopay.phucvt.fooapp.grpc.gRPCServer;
import vn.zalopay.phucvt.fooapp.grpc.handler.chat.AddFriendHandler;
import vn.zalopay.phucvt.fooapp.grpc.handler.chat.GetFriendListHandler;
import vn.zalopay.phucvt.fooapp.grpc.handler.chat.ResetUnseenHandler;
import vn.zalopay.phucvt.fooapp.grpc.handler.fintech.GetBalanceHandler;
import vn.zalopay.phucvt.fooapp.grpc.handler.fintech.GetHistoryHandler;
import vn.zalopay.phucvt.fooapp.grpc.handler.fintech.TransferMoneyHandler;
import vn.zalopay.phucvt.fooapp.grpc.service.ChatServiceImpl;
import vn.zalopay.phucvt.fooapp.grpc.service.FintechServiceImpl;
import vn.zalopay.phucvt.fooapp.handler.*;
import vn.zalopay.phucvt.fooapp.server.RestfulAPI;
import vn.zalopay.phucvt.fooapp.server.WebSocketServer;
import vn.zalopay.phucvt.fooapp.utils.AsyncHandler;
import vn.zalopay.phucvt.fooapp.utils.JwtUtils;
import vn.zalopay.phucvt.fooapp.utils.Tracker;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Module
@Builder
public class ServiceModule {
  ServiceConfig serviceConfig;

  @Provides
  @Singleton
  AuthInterceptor provideAuthInterceotor() {
    return AuthInterceptor.builder().publicKey(serviceConfig.getJwtConfig().getPublicKey()).build();
  }

  @Provides
  @Singleton
  AddFriendHandler provideAddFriendHandler(
      UserDA userDA, UserCache userCache, WSHandler wsHandler) {
    return AddFriendHandler.builder()
        .userDA(userDA)
        .userCache(userCache)
        .wsHandler(wsHandler)
        .build();
  }

  @Provides
  @Singleton
  GetFriendListHandler provideGetFriendListHandler(
      UserDA userDA, UserCache userCache, WSHandler wsHandler) {
    return GetFriendListHandler.builder()
        .userDA(userDA)
        .userCache(userCache)
        .wsHandler(wsHandler)
        .build();
  }

  @Provides
  @Singleton
  ResetUnseenHandler provideResetUnseenHandler(UserDA userDA) {
    return ResetUnseenHandler.builder().userDA(userDA).build();
  }

  @Provides
  @Singleton
  GetBalanceHandler provideGetBalanceHandler(UserDA userDA) {
    return GetBalanceHandler.builder().userDA(userDA).build();
  }

  @Provides
  @Singleton
  TransferMoneyHandler provideTransferMoneyHandler(
      UserDA userDA,
      FintechDA fintechDA,
      FintechCache fintechCache,
      ChatDA chatDA,
      ChatCache chatCache,
      WSHandler wsHandler,
      TransactionProvider transactionProvider) {
    return TransferMoneyHandler.builder()
        .userDA(userDA)
        .fintechDA(fintechDA)
        .fintechCache(fintechCache)
        .chatDA(chatDA)
        .chatCache(chatCache)
        .wsHandler(wsHandler)
        .transactionProvider(transactionProvider)
        .build();
  }

  @Provides
  @Singleton
  GetHistoryHandler provideGetHistory(FintechDA fintechDA, FintechCache fintechCache) {
    return GetHistoryHandler.builder().fintechDA(fintechDA).fintechCache(fintechCache).build();
  }

  @Provides
  @Singleton
  FintechServiceImpl provideFintechServiceImpl(
      GetBalanceHandler getBalanceHandler,
      TransferMoneyHandler transferMoneyHandler,
      GetHistoryHandler getHistoryHandler) {
    return FintechServiceImpl.builder()
        .getBalanceHandler(getBalanceHandler)
        .transferMoneyHandler(transferMoneyHandler)
        .getHistoryHandler(getHistoryHandler)
        .build();
  }

  @Provides
  @Singleton
  ChatServiceImpl provideChatServiceImpl(
      AddFriendHandler addFriendHandler,
      GetFriendListHandler getFriendListHandler,
      ResetUnseenHandler resetUnseenHandler) {
    return ChatServiceImpl.builder()
        .addFriendHandler(addFriendHandler)
        .getFriendListHandler(getFriendListHandler)
        .resetUnseenHandler(resetUnseenHandler)
        .build();
  }

  @Provides
  @Singleton
  FintechDA provideFintechDA(DataSourceProvider dataSourceProvider, AsyncHandler asyncHandler) {
    return FintechDAImpl.builder()
        .dataSource(dataSourceProvider.getDataSource(serviceConfig.getMySQLConfig()))
        .asyncHandler(asyncHandler)
        .build();
  }

  @Provides
  @Singleton
  gRPCServer provideGRPCServer(
      FintechServiceImpl fintechService,
      ChatServiceImpl chatService,
      AuthInterceptor authInterceptor) {
    return gRPCServer
        .builder()
        .port(serviceConfig.getGrpcPort())
        .fintechService(fintechService)
        .chatService(chatService)
        .authInterceptor(authInterceptor)
        .build();
  }

  @Provides
  @Singleton
  HandlerFactory provideHandler(
      LogInHandler loginHandler,
      SignUpHandler signUpHandler,
      LogOutHandler logOutHandler,
      UserListHandler userListHandler,
      MessageListHandler messageListHandler,
      AuthHandler authHandler) {
    return HandlerFactory.builder()
        .loginHandler(loginHandler)
        .signUpHandler(signUpHandler)
        .signOutHandler(logOutHandler)
        .userListHanlder(userListHandler)
        .messageListHandler(messageListHandler)
        .authHandler(authHandler)
        .build();
  }

  @Provides
  @Singleton
  AsyncHandler ProvideAsyncHandler(Vertx vertx) {
    return AsyncHandler.builder().vertx(vertx).build();
  }

  @Provides
  @Singleton
  RedisCache provideRedisCache() {
    try {
      return RedisCache.newInstance();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Provides
  @Singleton
  FintechCache provideFintechCache(RedisCache redisCache, AsyncHandler asyncHandler) {
    return FintechCacheImpl.builder()
        .redisCache(redisCache)
        .asyncHandler(asyncHandler)
        .cacheConfig(serviceConfig.getCacheConfig())
        .build();
  }

  @Provides
  @Singleton
  UserCache provideUserCache(RedisCache redisCache, AsyncHandler asyncHandler) {
    return UserCacheImpl.builder()
        .redisCache(redisCache)
        .asyncHandler(asyncHandler)
        .cacheConfig(serviceConfig.getCacheConfig())
        .build();
  }

  @Provides
  @Singleton
  ChatCache provideChatCache(RedisCache redisCache, AsyncHandler asyncHandler) {
    return ChatCacheImpl.builder()
        .redisCache(redisCache)
        .asyncHandler(asyncHandler)
        .cacheConfig(serviceConfig.getCacheConfig())
        .build();
  }

  @Provides
  @Singleton
  BlackListCache provideBlackListCache(RedisCache redisCache, AsyncHandler asyncHandler) {
    return BlackListCacheImpl.builder().redisCache(redisCache).asyncHandler(asyncHandler).build();
  }

  @Provides
  @Singleton
  UserDA provideUserDA(DataSourceProvider dataSourceProvider, AsyncHandler asyncHandler) {
    return new UserDAImpl(
        dataSourceProvider.getDataSource(serviceConfig.getMySQLConfig()), asyncHandler);
  }

  @Provides
  @Singleton
  ChatDA provideChatDA(DataSourceProvider dataSourceProvider, AsyncHandler asyncHandler) {
    return ChatDAImpl.builder()
        .dataSource(dataSourceProvider.getDataSource(serviceConfig.getMySQLConfig()))
        .asyncHandler(asyncHandler)
        .chatConfig(serviceConfig.getChatConfig())
        .build();
  }

  @Provides
  @Singleton
  AuthHandler provideAuthHandler(JwtUtils jwtUtils, BlackListCache blackListCache) {
    return AuthHandler.builder().jwtUtils(jwtUtils).blackListCache(blackListCache).build();
  }

  @Provides
  @Singleton
  SignUpHandler provideSignUpHandler(UserDA userDA, UserCache userCache) {
    return SignUpHandler.builder()
        .userCache(userCache)
        .userDA(userDA)
        .userConfig(serviceConfig.getUserConfig())
        .build();
  }

  @Provides
  @Singleton
  LogInHandler provideLoginHandler(UserDA userDA, UserCache userCache, JwtUtils jwtUtils) {
    return LogInHandler.builder().jwtUtils(jwtUtils).userDA(userDA).userCache(userCache).build();
  }

  @Provides
  @Singleton
  LogOutHandler provideLogOutHanler(BlackListCache blackListCache, UserCache userCache) {
    return LogOutHandler.builder().blackListCache(blackListCache).userCache(userCache).build();
  }

  @Provides
  @Singleton
  UserListHandler provideUserListHandler(
      JwtUtils jwtUtils, UserDA userDA, UserCache userCache, WSHandler wsHandler) {
    return UserListHandler.builder()
        .jwtUtils(jwtUtils)
        .userDA(userDA)
        .userCache(userCache)
        .wsHandler(wsHandler)
        .build();
  }

  @Provides
  @Singleton
  MessageListHandler provideMessageListHandler(
      JwtUtils jwtUtils, ChatDA chatDA, ChatCache chatCache) {
    return MessageListHandler.builder()
        .jwtUtils(jwtUtils)
        .chatCache(chatCache)
        .chatDA(chatDA)
        .build();
  }

  @Singleton
  @Provides
  DataSourceProvider provideDataSourceProvider(Vertx vertx) {
    return new DataSourceProviderImpl(vertx);
  }

  @Singleton
  @Provides
  TransactionProvider provideTransactionProvider(DataSourceProvider dataSourceProvider) {
    return new TransactionProviderImpl(
        dataSourceProvider.getVertxDataSource(serviceConfig.getMySQLConfig()));
  }

  @Provides
  @Singleton
  Vertx provideVertx() {
    return Vertx.vertx(
        new VertxOptions()
            .setPreferNativeTransport(true)
            .setMetricsOptions(
                new MicrometerMetricsOptions()
                    .setMicrometerRegistry(Tracker.getMeterRegistry())
                    .setJvmMetricsEnabled(true)
                    .setPrometheusOptions(
                        new VertxPrometheusOptions()
                            .setEnabled(true)
                            .setStartEmbeddedServer(true)
                            .setPublishQuantiles(true)
                            .setEmbeddedServerOptions(
                                new HttpServerOptions()
                                    .setPort(serviceConfig.getVertxConfig().getPrometheusPort()))
                            .setEmbeddedServerEndpoint("/metrics"))
                    .setEnabled(true)));
  }

  @Provides
  @Singleton
  JWTAuthOptions provideJWTAuthOptions() {
    // TODO: add key store
    return new JWTAuthOptions()
        .addPubSecKey(
            new PubSecKeyOptions()
                .setAlgorithm(serviceConfig.getJwtConfig().getAlgorithm())
                .setPublicKey(serviceConfig.getJwtConfig().getPublicKey())
                .setSymmetric(true));
  }

  @Provides
  @Singleton
  JWTAuth provideAuthProvider(Vertx vertx, JWTAuthOptions authConfig) {
    return JWTAuth.create(vertx, authConfig);
  }

  @Provides
  @Singleton
  JwtUtils provideJWTUtils(JWTAuth auth) {
    return JwtUtils.builder().jwtAuth(auth).jwtConfig(serviceConfig.getJwtConfig()).build();
  }

  @Provides
  @Singleton
  RestfulAPI provideRestfulAPI(HandlerFactory handlerFactory, Vertx vertx, JWTAuth authProvider) {
    return RestfulAPI.builder()
        .vertx(vertx)
        .authProvider(authProvider)
        .handlerFactory(handlerFactory)
        .port(serviceConfig.getPort())
        .build();
  }

  @Provides
  @Singleton
  WSHandler provideWSHandler(UserDA userDA, ChatDA chatDA, ChatCache chatCache) {
    return WSHandler.builder()
        .userDA(userDA)
        .chatCache(chatCache)
        .chatDA(chatDA)
        .clients(new ConcurrentHashMap<>())
        .build();
  }

  @Provides
  @Singleton
  WebSocketServer provideWebSocketServer(
      WSHandler wsHandler, Vertx vertx, JwtUtils jwtUtils, UserCache userCache) {
    return WebSocketServer.builder()
        .wsHandler(wsHandler)
        .vertx(vertx)
        .port(serviceConfig.getWsPort())
        .jwtUtils(jwtUtils)
        .userCache(userCache)
        .build();
  }
}
