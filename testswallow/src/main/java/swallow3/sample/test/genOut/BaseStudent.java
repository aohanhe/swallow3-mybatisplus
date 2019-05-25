package swallow3.sample.test.genOut;

import lombok.Data;
import swallow3.mybatisplus.SwallowField;


/**
 * BaseStudent
 */
@Data
public class BaseStudent {

    @SwallowField(length = "100")
    private String myname;
    
}