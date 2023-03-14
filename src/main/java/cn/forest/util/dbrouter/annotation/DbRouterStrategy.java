package cn.forest.util.dbrouter.annotation;

import java.lang.annotation.*;

/**
 * @description:
 * @author：Forest
 * @date: 2023/3/14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DbRouterStrategy {
    boolean splitTable() default false;
}
