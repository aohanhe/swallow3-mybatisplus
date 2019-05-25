package swallow3.mybatisplus.codegenhelper;

import java.util.List;
import java.util.Arrays;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import swallow3.mybatisplus.codegenhelper.codeGener.ICodeGener;
import swallow3.mybatisplus.codegenhelper.mysql.MySqlCodeGener;

/**
 * 配置创建助手
 */
public class ConfigHelper {
    /**
     * 通过参数生成配置数据
     * @param map
     * @return
     */
    public static Config getConfigFromOptions(Map<String,String> map){
        Assert.notNull(map, "没有取得编译参数");
        String dbType = map.get(Config.Option_DbType);

        // mysql的配置
        if(StringUtils.hasText(dbType)&&dbType.equals("mysql"))
        {
            return new Config(map);
        }

        return new Config(map);
    }    

    /**
     * 支持的参数，如果有新的参数可以在这里添加
     */
    public static List<String> getSupportOptions(){   
        return Arrays.asList((new Config()).getOptions());
    }

    /**
     * 取得代码生成器
     */
    public static ICodeGener getCodeGener(String dbType){
        
        Assert.hasText(dbType, "参数dbType不允许为空");

        if(dbType.equals("mysql")){
            return new MySqlCodeGener();
        }
        throw new RuntimeException("不支持的数据库类型:"+dbType);
    }
}