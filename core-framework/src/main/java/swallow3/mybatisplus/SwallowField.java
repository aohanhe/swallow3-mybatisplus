package swallow3.mybatisplus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定字段对应的信息，如果没有设置，使用主表
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SwallowField {

    /**
     * 对应表的字段名，如果不设置则使用@TableField设置的名称
     * @return
     */
    String fieldName() default "";
    
    /**
     * 对应表的别名，如果为空，则使用主表的别名
     */
    String  tableAliasName() default ""; 
    // 对应的jdbc类型  
    String jdbcType() default "";
    // 表中字段的长度 ，如果没有配置生成的代码会指定255
    String length() default "";
    // 字段是否允许为空 默认是允许
    boolean canNull() default true;
}