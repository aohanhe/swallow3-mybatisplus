package swallow3.mybatisplus.codegenhelper;

import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import org.springframework.util.Assert;

import lombok.Data;

/**
 * 用于描述实体对应的表的信息
 */
@Data
public class TableInfo {
    // 对应的类信息
    private TypeMirror rawClassInfo;
    // 实体的包名
    private String entityPacketName;
    // 实体的名称
    private String entityName;
    // 表名称
    private String name;
    // 别名
    private String aliasName;
    // 对应的主键名
    private String primaryKey;

    // 表的联结数据
    private List<TableLeftJoinInfo> joins;

    // 表的字段信息
    private List<TableFieldInfo> fields;


    /**
     * 检查左联结是否合法 主要检查同一个表是否出现多次，但是别名又一样
     */
    public  void checkLeftJoinIsOk(){     

        for(int i=0;i<joins.size();i++){
            TableLeftJoinInfo joinWantCheck=joins.get(i);
            for(int j=i+1;j<joins.size();j++){
                TableLeftJoinInfo join=joins.get(j);
                if(joinWantCheck.getTableName().equals(join.getTableName())){
                    Assert.isTrue(
                        !joinWantCheck.getTableAliasName().equals(join.getTableAliasName()),
                        String.format("join table %s 多次使用但别名也相同", joinWantCheck.getTableName()));                    
                }
            }
        }
    }


    /**
     * 检查字段设置是否合法 检查字段中引用到的表，是否在join中出现
     */
    public  void checkFieldInfo(){

        for(TableFieldInfo field:this.fields){
            String tableName=field.getTableAliasName();
            if(this.aliasName.equals(tableName)) continue; //如果是主表跳过
            boolean isFind=false;
            for(TableLeftJoinInfo join:joins){
                if(join.getTableAliasName().equals(tableName)) {
                    isFind=true;
                    break;
                }
            }
            Assert.isTrue(isFind,String.format("字段 '%s' 所设置的join表别名%s没有在任何的join中发现", field.getName(),tableName));
        }

    }

    /**
     * 把结果数据打印到消息中
     */
    public void printToMessager(Messager messager){
        messager.printMessage(Kind.NOTE, "=========================================\n");
        messager.printMessage(Kind.NOTE,String.format("处理实体%s.%s\n", this.entityPacketName,this.entityName));
        messager.printMessage(Kind.NOTE, "实体名称:"+this.entityName+"\n");
        messager.printMessage(Kind.NOTE, String.format("表名称:%s  别名:%s", this.name,this.aliasName));

        messager.printMessage(Kind.NOTE, "**********字段设置*******\n");

        for(TableFieldInfo fieldInfo:this.fields){
            String fieldStr=String.format("字段名:%s  表字段:%s.%s 允许空:%s 查询别名:%s 类型:%s  长度:%s "
                +(fieldInfo.isTransient()?"transient":"")+(fieldInfo.isPrimaryKey()?" 主键":""),
            fieldInfo.getName(),fieldInfo.getTableAliasName(),fieldInfo.getFieldName(), fieldInfo.isCanNull(),           
            fieldInfo.getFieldAliasName(),fieldInfo.getJdbcType(),fieldInfo.getLen());
            
            messager.printMessage(Kind.NOTE, fieldStr);
        }

        messager.printMessage(Kind.NOTE, "*********************\n\n\n");

        messager.printMessage(Kind.NOTE, "**********联结设置******\n");

        for(TableLeftJoinInfo join:this.joins){
            messager.printMessage(Kind.NOTE, String.format("联结表 %s 别名 %s on %s.%s=%s.%s 主表 %s", 
                join.getTableName(),join.getTableAliasName(),join.getMainTableAliasName(),join.getMainFieldName(),
                join.getTableAliasName(),join.getFieldName(),join.getMainTableName()
            ));
        }

        messager.printMessage(Kind.NOTE, "*********************\n");



        messager.printMessage(Kind.NOTE, "=========================================\n");
    }


}