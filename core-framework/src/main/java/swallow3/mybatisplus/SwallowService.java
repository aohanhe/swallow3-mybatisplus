package swallow3.mybatisplus;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
/**
 * SwallowService 在mybatis的serviceImpl基础上添加了事务以及日志
 */
public class SwallowService<M extends BaseMapper<T>, T> extends ServiceImpl<M,T>{

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
            String strMsg=String.format("保存Student(%s)对象时出错:",entity.toString())+e.getMessage();
            log.error(strMsg, e);
            throw new RuntimeException(strMsg, e);
        }        
    }

    @Override
    public boolean updateById(T entity) {        
        try {
            boolean re= super.updateById(entity);
            if(this.eventLog!=null) eventLog.onEntityUpdate(entity);
            return re;
        } catch (Exception e) {
            String strMsg=String.format("删除Student(%s)对象时出错:",entity.toString())+e.getMessage();
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
            String strMsg=String.format("删除Student(id=%d)对象时出错:",id)+e.getMessage();
            log.error(strMsg, e);
            throw new RuntimeException(strMsg, e);
        }    
    }
    
}