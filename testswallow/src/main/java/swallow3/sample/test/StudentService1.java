package swallow3.sample.test;

import java.io.Serializable;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import swallow3.mybatisplus.IEntityEventLog;
import swallow3.mybatisplus.SwallowService;

/**
 * StudentService
 */
@Slf4j
@Service
public class StudentService1 extends SwallowService<StudentMapper,Student>{

    
    
    @Override
    public Student getById(Serializable id) {
       return this.baseMapper.findItemById(id);     
    }

    
    
}