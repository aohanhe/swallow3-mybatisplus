package swallow3.mybatisplus.codegenhelper;

import java.lang.reflect.Type;

import javax.lang.model.type.TypeKind;

/**
 * java数据类型转换为jdbc类型
 */
public interface IJavaTypeToJdbType {

    /**
     * 从javaType到得jdbc类型
     * @param type
     * @return
     */
    String getJdbcType(TypeKind javaType);

    /**
     * 取得生成sql语句时jdbc类型的打印模板
     * @param jdbcType
     * @return
     */
    //String getJdbcTypeFormat(String jdbcType);

    /**
     * 是否必须要长度字段
     * @param jdbcType
     * @return
     */
    boolean isMustLen(String jdbcType);
    
}