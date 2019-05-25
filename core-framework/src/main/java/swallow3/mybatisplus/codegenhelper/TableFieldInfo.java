package swallow3.mybatisplus.codegenhelper;

import lombok.Data;

/**
 * 表的字段信息
 */
@Data
public class TableFieldInfo {
    // 字段名称
    private String name;

    // 表字段名称
    private String fieldName;

    // 生成字段别名
    private String fieldAliasName;
    
    // 对应表的别名
    private String tableAliasName;

    // 是否快照字段
    private boolean isTransient;

    // 对应数据的jdbc类型
    private String jdbcType;    

    // 字段长度
    private String len;

    // 是否为主键
    private boolean primaryKey=false;

    // 是否充许为空
    private boolean canNull=true;
}