package cn.forest.util.dbrouter.annotation;

import java.lang.annotation.*;

/**
 * @author Forest
 * @date 2023/3/14 10:34
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DBRouter {
    String key() default "";
}
