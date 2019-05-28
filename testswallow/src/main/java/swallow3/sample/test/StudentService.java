package swallow3.sample.test;

import java.io.Serializable;
import java.lang.Override;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import swallow3.mybatisplus.SwallowService;

/**
 * 实体swallow3.sample.test.Student对应的Service对象,下次生成时不会被覆盖 */
@Slf4j
@Service
public class StudentService extends SwallowService<StudentMapper, Student> {
  /**
   * 取得id对应的Student信息 */
  @Override
  public Student getById(Serializable id) {
    return this.baseMapper.findItemById(id);
  }
}
