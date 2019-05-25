package swallow3.mybatisplus.codegenhelper;

import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import lombok.Data;

/**
 * 设置接口
 */
@Data
public class Config {
    public static final String Option_GenCode="genCode";
    public static final String Option_GenCodeDir="genCodeDir";
    public static final String Option_CodeDir="codeDir";
    public static final String Option_SqlDir="sqlDir";
    public static final String Option_Debug="debug";
    public static final String Option_DbType="dbType";
    public static final String Option_CreateSql="createSql";

    //是否生成代码
    private boolean genCode=false;

    // 生成代码目录
    private String genCodeDir;
    // 代码目录
    private String codeDir;

    private String sqlDir;

    // 是否显示debug数据
    private boolean debug=false;

    // 数据库类型
    private String dbType;

    // 是否生成建表语句
    private boolean createSql=false;


    public Config(){

    }

    /**
     * 通过hashMap创建配置对象
     * @param values
     */
    public Config(Map<String,String> values){
        System.out.println(values.get(Option_GenCode));
        if(StringUtils.hasText(values.get(Option_GenCode))){            
            this.genCode=Boolean.parseBoolean(values.get(Option_GenCode));
        }

        //如果不需要生成代码退出读取配置
        if(!this.genCode) return;

        this.codeDir=values.get(Option_CodeDir);
        Assert.hasText(this.codeDir, "没有设置"+Option_CodeDir+"参数,请在编译指令中添加-A"+Option_CodeDir+"=XXX");
        this.genCodeDir=values.get(Option_GenCodeDir);
        Assert.hasText(this.genCodeDir, "没有设置"+Option_GenCodeDir+"参数,请在编译指令中添加-A"+Option_GenCodeDir+"=XXX");
        this.dbType=values.get(Option_DbType);
        Assert.hasText(this.dbType, "没有设置"+Option_DbType+"参数,请在编译指令中添加-A"+Option_DbType+"=XXX");
        this.sqlDir=values.get(Option_SqlDir);
        
        
        if(StringUtils.hasText(values.get(Option_CreateSql))){
            this.createSql=Boolean.parseBoolean(values.get(Option_CreateSql));
        }

        if(this.createSql){
            Assert.hasText(this.sqlDir, "没有设置"+Option_SqlDir+"参数,请在编译指令中添加-A"+Option_SqlDir+"=XXX");
        }

        if(StringUtils.hasText(values.get(Option_Debug))){
            this.debug=Boolean.parseBoolean(values.get(Option_Debug));
        }
    }

    

    /**
     * 返回当前系统支持的选项列表
     */
    public String[] getOptions(){
        String []options={Option_GenCode,Option_GenCodeDir,Option_CodeDir,Option_SqlDir,Option_Debug,Option_DbType,Option_CreateSql};
        return options;
    }
    
}