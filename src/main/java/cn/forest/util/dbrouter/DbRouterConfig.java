package cn.forest.util.dbrouter;

/**
 * @author Forest
 * @date 2023/3/14 10:44
 */
public class DbRouterConfig {
    private int dbCount;
    private int tbCount;
    private String routerKey;

    public DbRouterConfig(int dbCount, int tbCount, String routerKey) {
        this.dbCount = dbCount;
        this.tbCount = tbCount;
        this.routerKey = routerKey;
    }

    public int getDbCount() {
        return dbCount;
    }

    public int getTbCount() {
        return tbCount;
    }

    public String getRouterKey() {
        return routerKey;
    }

    @Override
    public String toString() {
        return "DbRouterConfig{" +
                "dbCount=" + dbCount +
                ", tbCount=" + tbCount +
                ", routerKey='" + routerKey + '\'' +
                '}';
    }
}
