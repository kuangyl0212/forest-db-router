package cn.forest.util.dbrouter.strategy;

/**
 * @author Forest
 * @date 2023/3/14 16:12
 */
public interface IDbRouterStrategy {
    /**
     * 执行路由切换
     * @param routeKey 路由key
     */
    void doRoute(String routeKey);
}
