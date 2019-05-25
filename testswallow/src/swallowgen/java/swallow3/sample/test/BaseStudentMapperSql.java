package swallow3.sample.test;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.lang.String;
import org.apache.ibatis.annotations.Param;

/**
 * 实体swallow3.sample.test.Student对应的基础mybatis的mapper对象,请不要在这里添加代码，以防止下次生成时被覆盖 */
class BaseStudentMapperSql {
  public static final String SELECTLIST = "student.id,student.name,student.class_id,classinfo.ttt,student.myname";

  public static final String FROMLIST = "student student left join classInfo classinfo on student.class_id=classinfo.id ";

  /**
   * 根据条件查询所有的Student数据 */
  public String findAllItem(@Param(Constants.WRAPPER) Wrapper<Student> Wrapper) {
    return "Select "+SELECTLIST+" Form "+FROMLIST+" ${ew.customSqlSegment}";
  }

  /**
   * 根据条件查询所有的Student数据 */
  public String findAllItemByPage(Page<Student> page,
      @Param(Constants.WRAPPER) Wrapper<Student> Wrapper) {
    return "Select "+SELECTLIST+" Form "+FROMLIST+" ${ew.customSqlSegment}";
  }
}
