package swallow3.mybatisplus;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.Wrapper;

/**
 * SwallowService 在mybatis的serviceImpl基础上添加了事务以及日志
 */
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
public class SwallowService<M extends ISwallowMapper<T>, T> extends ServiceImpl<M,T>{

    @Autowired(required = false)
    protected IEntityEventLog eventLog;
    
        
    @Transactional
    @Override
    public boolean save(T entity) {
        try {
            boolean re= super.save(entity);
            if(this.eventLog!=null) eventLog.onEntitySave(entity);
            return re;
        } catch (Exception e) {
            String strMsg=String.format("保存实体(%s)对象时出错:",entity.toString())+e.getMessage();
            log.error(strMsg, e);
            throw new RuntimeException(strMsg, e);
        }        
    }

    @Transactional
    @Override
    public boolean updateById(T entity) {        
        try {
            boolean re= super.updateById(entity);
            if(this.eventLog!=null) eventLog.onEntityUpdate(entity);
            return re;
        } catch (Exception e) {
            String strMsg=String.format("删除实体(%s)对象时出错:",entity.toString())+e.getMessage();
            log.error(strMsg, e);
            throw new RuntimeException(strMsg, e);
        }  
    }

    
    @Transactional
    @Override
    public boolean removeById(Serializable id) {        
        try {
            boolean re= super.removeById(id);
            if(this.eventLog!=null) eventLog.onEntityDel(id);
            return re;
        } catch (Exception e) {
            String strMsg=String.format("删除实体(id=%d)对象时出错:",id)+e.getMessage();
            log.error(strMsg, e);
            throw new RuntimeException(strMsg, e);
        }    
    }

    
    @Override
    public IPage<T> page(IPage<T> page) {
        return getBaseMapper().findAllItemByPage(page, Wrappers.emptyWrapper());
    }

    @Override
    public IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper) {
        return getBaseMapper().findAllItemByPage(page,queryWrapper);
    }

    @Override
    public List<T> list(Wrapper<T> queryWrapper) {
        return getBaseMapper().findAllItem(queryWrapper);
    }
}