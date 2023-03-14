package cn.forest.util.dbrouter.strategy;

import cn.forest.util.dbrouter.DbContextHolder;
import cn.forest.util.dbrouter.DbRouterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Forest
 * @date 2023/3/14 16:26
 */
public class FibonacciHashRouterStrategy implements IDbRouterStrategy {

    private static Logger logger = LoggerFactory.getLogger(FibonacciHashRouterStrategy.class);

    private DbRouterConfig dbRouterConfig;

    public FibonacciHashRouterStrategy(DbRouterConfig dbRouterConfig) {
        this.dbRouterConfig = dbRouterConfig;
    }

    @Override
    public void doRoute(String routeKey) {
        int size = dbRouterConfig.getDbCount() * dbRouterConfig.getTbCount();
        int idx = (size - 1) & (routeKey.hashCode() ^ routeKey.hashCode() >>> 16);

        int dbIdx = (idx / dbRouterConfig.getDbCount()) % dbRouterConfig.getDbCount() + 1;
        int tbIdx = (idx % dbRouterConfig.getTbCount()) % dbRouterConfig.getTbCount() + 1;

        DbContextHolder.setDbKey(String.format("%02d", dbIdx));
        DbContextHolder.setTbKey(String.format("%03d", tbIdx));

        logger.info("数据库路由到 dbIdx({}) tbIdx({})", dbIdx, tbIdx);
    }
}
