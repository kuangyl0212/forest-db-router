package cn.forest.util.dbrouter.aspects;

import cn.forest.util.dbrouter.DbContextHolder;
import cn.forest.util.dbrouter.DbRouterConfig;
import cn.forest.util.dbrouter.annotation.DBRouter;
import cn.forest.util.dbrouter.strategy.IDbRouterStrategy;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Forest
 * @date 2023/3/14 13:38
 */
@Aspect
public class DbRouterJoinPoint {

    private DbRouterConfig dbRouterConfig;
    private IDbRouterStrategy routerStrategy;

    private static Logger logger = LoggerFactory.getLogger(DbRouterJoinPoint.class);

    public DbRouterJoinPoint(DbRouterConfig dbRouterConfig, IDbRouterStrategy routerStrategy) {
        this.dbRouterConfig = dbRouterConfig;
        this.routerStrategy = routerStrategy;
    }

    @Pointcut("@annotation(cn.forest.util.dbrouter.annotation.DBRouter)")
    public void pointcut() {}

    @Around("pointcut() && @annotation(dbRouter)")
    public Object doRouter(ProceedingJoinPoint joinPoint, DBRouter dbRouter) throws Throwable {
        String dbKey = dbRouter.key();
        if (StringUtils.isBlank(dbKey) && StringUtils.isBlank(dbRouterConfig.getRouterKey())) {
            throw new RuntimeException("db key and default db key is null");
        }

        if (StringUtils.isBlank(dbKey)) {
            dbKey = dbRouterConfig.getRouterKey();
        }

        String dbKeyValue = "";

        Object[] args = joinPoint.getArgs();
//        if (args.length == 1) {
//            if (args[0] instanceof String) {
//                dbKeyValue = args[0].toString();
//            }
//        }

        for (Object arg : args) {
            Class<?> clazz = arg.getClass();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field: fields) {
                if (field.getName().equals(dbKey)) {
                    field.setAccessible(true);
                    Object fieldValue = field.get(arg);
                    if (StringUtils.isNotBlank(fieldValue.toString())) {
                        dbKeyValue = fieldValue.toString();
                        break;
                    }
                    field.setAccessible(false);
                }
            }
        }

        if (StringUtils.isNotBlank(dbKeyValue)) {
            routerStrategy.doRoute(dbKeyValue);
        }

        try {
            return joinPoint.proceed();
        } finally {
            DbContextHolder.removeDbKey();
            DbContextHolder.removeTbKey();
        }
    }

    Map<String, Object> getNameAndValue(ProceedingJoinPoint joinPoint) {
        Map<String, Object> param = new HashMap<>();
        Object[] paramValues = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < paramNames.length; i++) {
            param.put(paramNames[i], paramValues[i]);
        }
        return param;
    }

}
