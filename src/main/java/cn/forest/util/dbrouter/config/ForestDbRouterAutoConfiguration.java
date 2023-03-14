package cn.forest.util.dbrouter.config;

import cn.forest.util.dbrouter.DbRouterConfig;
import cn.forest.util.dbrouter.DynamicDataSource;
import cn.forest.util.dbrouter.aspects.DbRouterJoinPoint;
import cn.forest.util.dbrouter.interceptor.MybatisRouterInterceptor;
import cn.forest.util.dbrouter.strategy.FibonacciHashRouterStrategy;
import cn.forest.util.dbrouter.strategy.IDbRouterStrategy;
import cn.forest.util.dbrouter.util.PropertyUtil;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Forest
 * @date 2023/3/14 10:26
 */
@ConditionalOnProperty(prefix = "forest-db-router", name = "enabled", havingValue = "true")
@Configuration
public class ForestDbRouterAutoConfiguration implements EnvironmentAware {
    private static final String PREFIX = "forest-db-router.";

    private Map<String, Map<String, Object>> dataSourceMap = new HashMap<>();

    private Map<String, Object> defaultDataSourceConfig;

    private int dbCount;
    private int tbCount;

    private String routerKey;

    @Bean
    public DbRouterConfig dbRouterConfig() {
        return new DbRouterConfig(dbCount, tbCount, routerKey);
    }

    @Bean("db-router-join-point")
    @ConditionalOnMissingBean
    public DbRouterJoinPoint joinPoint(DbRouterConfig dbRouterConfig, IDbRouterStrategy routerStrategy) {
        return new DbRouterJoinPoint(dbRouterConfig, routerStrategy);
    }

    @Bean
    public Interceptor routerPlugin() {
        return new MybatisRouterInterceptor();
    }

    @Bean
    public IDbRouterStrategy dbRouterStrategy(DbRouterConfig dbRouterConfig) {
        return new FibonacciHashRouterStrategy(dbRouterConfig);
    }

    @Bean
    public DataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        for (String dataSourceKey: dataSourceMap.keySet()) {
            Map<String, Object> dsInfo = dataSourceMap.get(dataSourceKey);
            targetDataSources.put(dataSourceKey, new DriverManagerDataSource(
                    dsInfo.get("url").toString(),
                    dsInfo.get("username").toString(),
                    dsInfo.get("password").toString()));
        }

        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);
        dataSource.setDefaultTargetDataSource(new DriverManagerDataSource(
                defaultDataSourceConfig.get("url").toString(),
                defaultDataSourceConfig.get("username").toString(),
                defaultDataSourceConfig.get("password").toString()
        ));
        return dataSource;
    }

    @Override
    public void setEnvironment(Environment environment) {
        dbCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty(PREFIX + "db-count")));
        tbCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty(PREFIX + "tb-count")));
        routerKey = Objects.requireNonNull(environment.getProperty(PREFIX + "router-key"));

        String dbListStr = Objects.requireNonNull(environment.getProperty(PREFIX + "db-list"));
        for (String dbInfo : dbListStr.split(",")) {
            dbInfo = dbInfo.trim();
            Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, PREFIX + dbInfo, Map.class);
            dataSourceMap.put(dbInfo, dataSourceProps);
        }

        String defaultDbKey = Objects.requireNonNull(environment.getProperty(PREFIX + "default-db"));

        defaultDataSourceConfig = PropertyUtil.handle(environment, PREFIX + defaultDbKey, Map.class);

    }
}
