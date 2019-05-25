package swallow3.mybatisplus.codegenhelper;

import lombok.Data;

/**
 * 表左联结信息
 */
@Data
public class TableLeftJoinInfo {
    private String fieldName;
    private String tableName;
    private String tableAliasName;
    private String mainFieldName;
    private String mainTableName;    
    private String mainTableAliasName;
}