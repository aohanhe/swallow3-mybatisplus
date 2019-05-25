package swallow3.mybatisplus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 设置表的左联结 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE})
@Repeatable(SwallowLeftJoins.class)
public @interface SwallowLeftJoin {
        
    // 被联结表的字段名
    String fieldName() default "" ;
    // 联结表的名称
    String joinTableName()  default "";

    // 联结表的别名
    String joinTableAliasName() default "";

    // 主表字段名称 如果是当前实体自己，可以省略
    String mainFieldName() default "";

    //  主表的名称
    String mainTableName() default "";

    // 主表的别名 如果是当前实体自己，可以省略
    String mainTableAliasName() default "";
}