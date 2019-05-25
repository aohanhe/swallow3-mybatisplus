package swallow3.sample.test;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import swallow3.mybatisplus.SwallowEnity;
import swallow3.mybatisplus.SwallowField;
import swallow3.mybatisplus.SwallowLeftJoin;
import swallow3.sample.test.genOut.BaseStudent;

/**
 * TestEntity
 */
@Data
@TableName("student")
@SwallowEnity(aliasName = "student")

public class Student extends BaseStudent{
    @SwallowField(tableAliasName ="student",canNull = false)
    @TableField("id")
    @TableId
    private int id;
    
    @SwallowField(length = "30")
    private String name;

    @SwallowLeftJoin(fieldName="id",joinTableName="classInfo",joinTableAliasName="classinfo")    
    @TableField("class_id")
    private int classId;

    @SwallowField(fieldName = "ttt",tableAliasName="classinfo")
    @TableField("class_name")    
    private transient String className;     
}