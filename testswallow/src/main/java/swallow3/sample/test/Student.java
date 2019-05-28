package swallow3.sample.test;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import swallow3.mybatisplus.SwallowEnity;
import swallow3.mybatisplus.SwallowField;
import swallow3.mybatisplus.SwallowLeftJoin;


/**
 * TestEntity
 */
@Data
@TableName("student")
@SwallowEnity(aliasName = "student")
public class Student implements Serializable {

    private static final long serialVersionUID = -1102881406832899343L;

    @TableField("id")
    @TableId
    private int id;
    
    @SwallowField(length = "30")
    private String name;

    @SwallowLeftJoin(fieldName="id",joinTableName="class_info",joinTableAliasName="classinfo")    
    @TableField("class_id")
    private int classId;

    @SwallowField(fieldName = "name",tableAliasName="classinfo")
    @TableField("class_name")    
    private transient String className;     
}