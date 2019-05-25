package swallow3.sample.test.genOut;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Param;

import swallow3.sample.test.Student;

/**
 * BaseStudentSql
 */
public class BaseStudentSql {
    public final String selectList=""; 
    public final String fromList="";

    public String  findAllItemByPage(Page page ,@Param(Constants.WRAPPER) Wrapper<Student> wrapper){
        return "Select "+selectList+" "+fromList+" ${ew.customSqlSegment}";        
    }  
}