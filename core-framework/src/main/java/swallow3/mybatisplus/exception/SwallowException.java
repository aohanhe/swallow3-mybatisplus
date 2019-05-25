package swallow3.mybatisplus.exception;

/**
 * 异常信息类
 */
public class SwallowException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public SwallowException(Throwable ex){
        super(ex);
    }
    
    public SwallowException(String msg,Throwable ex){
        super(msg, ex);
    }
    
}