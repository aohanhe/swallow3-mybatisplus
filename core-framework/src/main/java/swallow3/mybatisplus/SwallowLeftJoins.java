package swallow3.mybatisplus;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 让类可以添加多个SwallowLeftJoin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SwallowLeftJoins {
    SwallowLeftJoin[] value();    
}