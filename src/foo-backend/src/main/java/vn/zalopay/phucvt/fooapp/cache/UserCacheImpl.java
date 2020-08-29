package vn.zalopay.phucvt.fooapp.cache;

import io.vertx.core.Future;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RMap;
import org.redisson.api.RQueue;
import vn.zalopay.phucvt.fooapp.model.User;
import vn.zalopay.phucvt.fooapp.utils.AsyncHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
@Builder
public class UserCacheImpl implements UserCache {
  private final RedisCache redisCache;
  private final AsyncHandler asyncHandler;

  @Override
  public Future<User> set(User user) {
    Future future = Future.future();
    asyncHandler.run(
        () -> {
          try {
            RMap<Object, Object> userMap =
                redisCache.getRedissonClient().getMap(CacheKey.getUserKey(user.getUserId()));
            userMap.put("userId", user.getUserId());
            userMap.put("username", user.getUsername());
            userMap.put("fullname", user.getFullname());
            //            userMap.put("online", user.isOnline());
            //            userMap.expire(5, TimeUnit.MINUTES); // 5 minus
            future.complete(user);
          } catch (Exception e) {
            future.fail(e);
          }
        });
    return future;
  }

  @Override
  public Future<User> get(String userId) {
    Future future = Future.future();

    asyncHandler.run(
        () -> {
          try {
            RMap<String, String> userMap =
                redisCache.getRedissonClient().getMap(CacheKey.getUserKey(userId));
            User user =
                User.builder()
                    .username(userMap.get("username"))
                    .fullname(userMap.get("fullname"))
                    .build();
            log.info("get user with name: {}", user.getFullname());
            future.complete(user);
          } catch (Exception e) {
            future.fail(e);
          }
        });
    return future;
  }

  @Override
  public Future<Boolean> del(String userId) {
      Future future = Future.future();
      asyncHandler.run(
              () -> {
                  try{
                      String onlineUserKey = CacheKey.getUserKey(userId);
                      long res = redisCache.getRedissonClient().getKeys().delete(onlineUserKey);
                      future.complete(res != -1);
                  }
                  catch (Exception e){
                      future.fail(e);
                  }
              }
      );
    return future;
  }

    @Override
    public Future<Void> setUserList(List<User> user) {
        Future future = Future.future();
        asyncHandler.run(
                () -> {
                    try{
                        RQueue<User> userRQueue = redisCache.getRedissonClient().getQueue(CacheKey.getUserListKey());
                        userRQueue.clear();
                        userRQueue.addAll(user);
                        userRQueue.expire(5, TimeUnit.MINUTES);
                    }
                    catch (Exception e){
                        future.fail(e);
                    }
                }
        );
        return future;
    }

    @Override
    public Future<List<User>> getUserList() {
        Future<List<User>> future = Future.future();
        asyncHandler.run(
                () -> {
                    try{
                        RQueue<User> userRQueue = redisCache.getRedissonClient().getQueue(CacheKey.getUserListKey());
                        if(!userRQueue.isExists()){
                            future.failed();
                        }
                        else{
                            future.complete(userRQueue.readAll());
                        }
                    }
                    catch (Exception e){
                        future.fail(e);
                    }
                }
        );
        return future;
    }
}
