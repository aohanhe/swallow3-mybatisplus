package swallow3.sample.test;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import java.io.Serializable;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import swallow3.mybatisplus.ISwallowMapper;

/**
 * 实体swallow3.sample.test.Student对应的基础mybatis的mapper对象,请不要在这里添加代码，以防止下次生成时被覆盖 */
public interface BaseStudentMapper extends ISwallowMapper<Student> {
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
  IPage<Student> findAllItemByPage(IPage<Student> page,
      @Param(Constants.WRAPPER) Wrapper<Student> Wrapper);

  /**
   * 根据ID查询Student数据 */
  @SelectProvider(
      type = BaseStudentMapperSql.class,
      method = "findItemById"
  )
  Student findItemById(@Param("id") Serializable id);
}
