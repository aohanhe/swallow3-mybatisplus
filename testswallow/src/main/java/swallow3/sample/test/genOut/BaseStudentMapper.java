package swallow3.sample.test.genOut;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import swallow3.sample.test.Student;

/**
 * BaseStudentMapper
 */
public interface BaseStudentMapper extends BaseMapper<Student>{

    @SelectProvider(type = BaseStudentSql.class,method = "findAllItemByPage")
    public Page<Student>  findAllItemByPage(Page page ,@Param(Constants.WRAPPER) Wrapper<Student> wrapper);    
}