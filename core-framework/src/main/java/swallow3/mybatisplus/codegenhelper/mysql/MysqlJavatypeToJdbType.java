package swallow3.mybatisplus.codegenhelper.mysql;



import javax.lang.model.type.TypeKind;

import swallow3.mybatisplus.codegenhelper.IJavaTypeToJdbType;

/**
 * MysqlJavatypeToJdbType
 */
public class MysqlJavatypeToJdbType implements IJavaTypeToJdbType {

    @Override
    public String getJdbcType(TypeKind javaType) {

        String jdbcType = null;

        switch (javaType) {
        case INT:
            return "int";
        case LONG:
            return "bigint";
        case FLOAT:
            return "float";
        case DOUBLE:
            return "double";
        case BOOLEAN:
            return "boolean";
        case CHAR:
            return "char";
        case SHORT:
            return "TINYINT";
        default:
            return "varchar";
        }
    }

   

    @Override
    public boolean isMustLen(String jdbcType) {
        
        if("varchar".equals(jdbcType)) return true;
        return false;
    }

}