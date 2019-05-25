package swallow3.mybatisplus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实体对象，用于指定对应的主表别名,如果为空，则使用mybatisplus的tablename
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SwallowEnity {
    String aliasName() default "";
}