package cn.forest.util.dbrouter;

import cn.forest.util.dbrouter.strategy.FibonacciHashRouterStrategy;
import cn.forest.util.dbrouter.strategy.IDbRouterStrategy;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Forest
 * @date 2023/3/14 17:00
 */


public class RouterStrategyTest {

    private static Logger logger = LoggerFactory.getLogger(RouterStrategyTest.class);

    @Test
    public void testRouterStrategy() {
        IDbRouterStrategy strategy = new FibonacciHashRouterStrategy(new DbRouterConfig(
                8,
                16,
                "uid"
        ));
        Map<String, Integer> routeMap = new HashMap<>();
//        for (int i = 1000; i < 99999; i++) {
//            strategy.doRoute(String.valueOf(i));
//            if (routeMap.containsKey(DbContextHolder.getDbKey())) {
//                routeMap.put(DbContextHolder.getDbKey(), routeMap.get(DbContextHolder.getDbKey()) + 1);
//            } else {
//                routeMap.put(DbContextHolder.getDbKey(), 1);
//            }
//        }
        strategy.doRoute("24123i74345");

        logger.info(routeMap.toString());
    }
}
