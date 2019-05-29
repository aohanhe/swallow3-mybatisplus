package swallow3.mybatisplus;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Param;

/**s
 * ISwallowMapper
 */
public interface ISwallowMapper<T> extends  BaseMapper<T>{
    /**
   * 根据条件查询所有的Student数据 */
  
List<T> findAllItem(@Param(Constants.WRAPPER) Wrapper<T> Wrapper);

/**
 * 根据条件分页查询所有的Student数据 */

IPage<T> findAllItemByPage(IPage<T> page,
    @Param(Constants.WRAPPER) Wrapper<T> Wrapper);

/**
 * 根据ID查询Student数据 */

T findItemById(@Param("id") Serializable id);    
}