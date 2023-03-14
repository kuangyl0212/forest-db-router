package cn.forest.util.dbrouter;

/**
 * @author Forest
 * @date 2023/3/14 13:28
 */
public class DbContextHolder {
    private static final ThreadLocal<String> DB_KEY = new ThreadLocal<>();
    private static final ThreadLocal<String> TB_KEY = new ThreadLocal<>();

    public static String getDbKey() {
        return DB_KEY.get();
    }

    public static void setDbKey(String value) {
        DB_KEY.set(value);
    }

    public static void removeDbKey() {
        DB_KEY.remove();
    }

    public static String getTbKey() {
        return TB_KEY.get();
    }

    public static void setTbKey(String value) {
        TB_KEY.set(value);
    }

    public static void removeTbKey() {
        TB_KEY.remove();
    }
}
