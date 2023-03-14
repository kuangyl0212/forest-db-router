package cn.forest.util.dbrouter.interceptor;

import cn.forest.util.dbrouter.DbContextHolder;
import cn.forest.util.dbrouter.annotation.DbRouterStrategy;
import cn.forest.util.dbrouter.strategy.IDbRouterStrategy;
import com.sun.org.apache.xpath.internal.axes.FilterExprIterator;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author：Forest
 * @date: 2023/3/14
 */
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
public class MybatisRouterInterceptor implements Interceptor {
    private static Logger logger = LoggerFactory.getLogger(MybatisRouterInterceptor.class);

    private Pattern pattern = Pattern.compile("(from|into|update)[\\s]+(\\w+)", Pattern.CASE_INSENSITIVE);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(
                statementHandler,
                SystemMetaObject.DEFAULT_OBJECT_FACTORY,
                SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
                new DefaultReflectorFactory());
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        String id = mappedStatement.getId();
        String className = id.substring(0, id.lastIndexOf("."));
        Class<?> clazz = Class.forName(className);
        DbRouterStrategy dbRouterStrategy = clazz.getAnnotation(DbRouterStrategy.class);
        if (null == dbRouterStrategy || !dbRouterStrategy.splitTable()) {
            return invocation.proceed();
        }

        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();

        Matcher matcher = pattern.matcher(sql);
        String tabName = null;
        if (matcher.find()) {
            tabName = matcher.group().trim();
        }
        assert null != tabName;
        String replacedSql = matcher.replaceAll(tabName + "_" + DbContextHolder.getTbKey());

        logger.debug(replacedSql);

        Field field = boundSql.getClass().getDeclaredField("sql");
        field.setAccessible(true);
        field.set(boundSql, replacedSql);
        field.setAccessible(false);
        return invocation.proceed();
    }
}
