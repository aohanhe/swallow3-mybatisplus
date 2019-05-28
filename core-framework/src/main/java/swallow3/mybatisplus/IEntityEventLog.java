package swallow3.mybatisplus;

import java.io.Serializable;

/**
 * 实体事务事件日志接口
 */
public interface IEntityEventLog {

    void onEntitySave(Object entity);
    void onEntityDel(Serializable id);
    void onEntityUpdate(Object entity);

    
}