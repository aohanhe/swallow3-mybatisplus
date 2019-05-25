package swallow3.mybatisplus.codegenhelper.codeGener;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.util.Elements;

import swallow3.mybatisplus.codegenhelper.Config;
import swallow3.mybatisplus.codegenhelper.TableInfo;

/**
 * 代码生成器接口定义
 */
public interface ICodeGener {

    /**
     * 通过表定义生成代码
     * @param tableInfo
     * @param config
     */
    void genTableInfoCode(TableInfo tableInfo,Config config,Elements elementsTool,Filer filer) throws IOException;   

    /**
     * 通过表定义生成建表语句
     */
    void genCreateTableSql(TableInfo tableInfo,Config config) throws IOException;

    /**
     * 通过表定义生成查询助手
     * @param tableInfo
     * @param config
     * @param elementsTool
     * @param filer
     * @throws IOException
     */
    void genQueryMetaHelperCode(TableInfo tableInfo,Config config,Elements elementsTool,Filer filer) throws IOException;
    
}