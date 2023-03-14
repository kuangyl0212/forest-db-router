package cn.forest.util.dbrouter;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author Forest
 * @date 2023/3/14 13:15
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return "db" + DbContextHolder.getDbKey();
    }
}
