package swallow3.sample.test;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * 实体swallow3.sample.test.Student对应的基础mybatis的mapper对象,请不要在这里添加代码，以防止下次生成时被覆盖 */
public interface BaseStudentMapper extends BaseMapper<Student> {
  /**
   * 根据条件查询所有的Student数据 */
  @SelectProvider(
      type = BaseStudentMapperSql.class,
      method = "findAllItem"
  )
  List<Student> findAllItem(@Param(Constants.WRAPPER) Wrapper<Student> Wrapper);

  /**
   * 根据条件分页查询所有的Student数据 */
  @SelectProvider(
      type = BaseStudentMapperSql.class,
      method = "findAllItemByPage"
  )
  IPage<Student> findAllItemByPage(Page<Student> page,
      @Param(Constants.WRAPPER) Wrapper<Student> Wrapper);
}
