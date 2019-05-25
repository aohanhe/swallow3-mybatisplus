package swallow3.mybatisplus.codegenhelper.mysql;






import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.util.StringUtils;

import swallow3.mybatisplus.codegenhelper.Config;
import swallow3.mybatisplus.codegenhelper.TableFieldInfo;
import swallow3.mybatisplus.codegenhelper.TableInfo;
import swallow3.mybatisplus.codegenhelper.TableLeftJoinInfo;
import swallow3.mybatisplus.codegenhelper.codeGener.ICodeGener;

/**
 * mysql代码生成器
 */
public class MySqlCodeGener implements ICodeGener {

    @Override
    public void genTableInfoCode(TableInfo tableInfo, Config config, Elements elementsTool, Filer filer)
            throws IOException {

        
        // 1 生面SQL类
        this.writeBaseMapperSqlClass(tableInfo, config.getGenCodeDir(), filer);
        // 2 生成基础查询类
        this.writeBaseMapperClass(tableInfo, config.getGenCodeDir(), filer);
        // 3 生成查询类
        this.writeMapperClass(tableInfo, config.getCodeDir(), filer);

    }

    @Override
    public void genCreateTableSql(TableInfo tableInfo, Config config) throws IOException{
        String strTemplate="Create table %s \n(\n %s\n) engine = InnoDB;";

        List<String> list=tableInfo.getFields().stream()
                .filter(item->!item.isTransient())
                .map(this::getFieldSql)
                .collect(Collectors.toList());

        String strList=String.join(",\n", list.toArray(new String[]{}));

        String data=String.format(strTemplate, tableInfo.getName(),strList);
        
        String outDir=Paths.get(config.getSqlDir(),tableInfo.getEntityPacketName().replaceAll("\\.", "/")).toString();
        File fileDir=new File(outDir);
        // 指定目录不存在
        if(!fileDir.exists()){
            // 创建目录
            fileDir.mkdirs();
        }
        
        // 写入文件
        try(FileWriter wr=new FileWriter(Paths.get(outDir,tableInfo.getEntityName()+".sql").toString())){
            wr.write(data);
            wr.flush();
        }
        
    }

    /**
     * 生成查询信息助手类
     */
    @Override
    public void genQueryMetaHelperCode(TableInfo tableInfo, Config config, Elements elementsTool, Filer filer)
                    throws IOException {
        String metaName = String.format("%sMeta", tableInfo.getEntityName());

        TypeSpec.Builder builder = TypeSpec.classBuilder(metaName).addJavadoc(
                "实体$L.$L对应的基础mybatis的meta对象,请不要在这里添加代码，以防止下次生成时被覆盖", tableInfo.getEntityPacketName(),
                                tableInfo.getEntityName());    
    
        for(TableFieldInfo fieldInfo:tableInfo.getFields()){
            builder.addField(FieldSpec.builder(String.class, fieldInfo.getName(), 
                Modifier.PUBLIC,Modifier.FINAL,Modifier.STATIC)
                .initializer("$S", String.format("%s.%s", fieldInfo.getTableAliasName(),fieldInfo.getFieldAliasName()))
                .build());    
        }
        JavaFile javaFile = JavaFile.builder(tableInfo.getEntityPacketName(), builder.build()).build();
        javaFile.writeTo(filer);
        javaFile.writeTo(Paths.get(config.getGenCodeDir()));
    }

    /**
     * 生成查询列表
     * 
     * @param tableInfo
     * @return
     */
    /**
     * 取得查询选择列表
     */
    private String getSelectList(TableInfo tableInfo) {
        // 主表列
        StringBuffer sql = new StringBuffer();

        boolean isFirst = true;
        // 添加从表列
        for (TableFieldInfo item : tableInfo.getFields()) {
            sql.append((isFirst ? "" : ",") + String.format("%s.%s", item.getTableAliasName(), item.getFieldName()));
            isFirst = false;
        }

        return sql.toString();
    }

    /**
     * 取得表列表字串
     * 
     * @return
     */
    private String getTableList(TableInfo tableInfo) {
        // 主表
        StringBuffer sql = new StringBuffer();
        String mainAlisasName = tableInfo.getAliasName();
        sql.append(String.format("%s %s ", tableInfo.getName(), mainAlisasName));

        // 添加join列表
        for (TableLeftJoinInfo join : tableInfo.getJoins()) {
            sql.append(String.format("left join %s %s on %s.%s=%s.%s ", join.getTableName(), join.getTableAliasName(),
                    join.getMainTableAliasName(), join.getMainFieldName(), join.getTableAliasName(),
                    join.getFieldName()));
        }

        return sql.toString();
    }

    private final String method_findAllItem = "findAllItem";
    private final String method_findAllItemByPage = "findAllItemByPage";

    /**
     * 生成基础类
     * 
     * @throws IOException
     */
    private void writeBaseMapperClass(TableInfo tableInfo, String pathDir, Filer filer) throws IOException {
        String mapperName = String.format("Base%sMapper", tableInfo.getEntityName());
        // 生成查询注解
        AnnotationSpec.Builder selectAnnFindAll = AnnotationSpec.builder(SelectProvider.class)
                .addMember("type", mapperName + "Sql.class").addMember("method", "$S", method_findAllItem);
        AnnotationSpec.Builder selectAnnFindAllByPage = AnnotationSpec.builder(SelectProvider.class)
                .addMember("type", mapperName + "Sql.class").addMember("method", "$S", method_findAllItemByPage);

        // 添加类主体
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(mapperName)
                .addJavadoc(String.format("实体%s.%s对应的基础mybatis的mapper对象,请不要在这里添加代码，以防止下次生成时被覆盖",
                        tableInfo.getEntityPacketName(), tableInfo.getEntityName()))
                .addModifiers(Modifier.PUBLIC).addSuperinterface(ParameterizedTypeName
                        .get(ClassName.get(BaseMapper.class), TypeName.get(tableInfo.getRawClassInfo())));

        // 添加标准查询方法
        MethodSpec.Builder methodFindAll = MethodSpec.methodBuilder(method_findAllItem);

        // 查询参数
        ParameterSpec.Builder param = ParameterSpec
                .builder(
                        ParameterizedTypeName
                                .get(ClassName.get(Wrapper.class), TypeName.get(tableInfo.getRawClassInfo()))
                                .annotated(AnnotationSpec.builder(Param.class)
                                        .addMember("value", CodeBlock.of("$T.WRAPPER", Constants.class)).build()),
                        "Wrapper");

        // 添加返回对象 返回List<T>对象
        methodFindAll.addJavadoc(String.format("根据条件查询所有的%s数据", tableInfo.getEntityName()))
                .addAnnotation(selectAnnFindAll.build()).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(
                        ParameterizedTypeName.get(ClassName.get(List.class), TypeName.get(tableInfo.getRawClassInfo())))
                .addParameter(param.build());

        // 分页返回T对象
        MethodSpec.Builder methodFindAllByPage = MethodSpec.methodBuilder(method_findAllItemByPage);
        methodFindAllByPage.addJavadoc(String.format("根据条件分页查询所有的%s数据", tableInfo.getEntityName()))
                .addAnnotation(
                        selectAnnFindAllByPage.build())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(ParameterizedTypeName.get(ClassName.get(IPage.class),
                        TypeName.get(tableInfo.getRawClassInfo())))
                .addParameter(ParameterSpec.builder(
                        ParameterizedTypeName.get(ClassName.get(Page.class), TypeName.get(tableInfo.getRawClassInfo())),
                        "page").build())
                .addParameter(param.build());

        builder.addMethod(methodFindAll.build());
        builder.addMethod(methodFindAllByPage.build());

        // 写入文件到生成目录下
        JavaFile javaFile = JavaFile.builder(tableInfo.getEntityPacketName(), builder.build()).build();
        javaFile.writeTo(filer);
        javaFile.writeTo(Paths.get(pathDir));
    }

    /**
     * 生成sql类
     */
    private void writeBaseMapperSqlClass(TableInfo tableInfo, String pathDir, Filer filer) throws IOException {
        String mapperSqlName = String.format("Base%sMapperSql", tableInfo.getEntityName());

        TypeSpec.Builder builder = TypeSpec.classBuilder(mapperSqlName).addJavadoc(
                "实体$L.$L对应的基础mybatis的mapper对象,请不要在这里添加代码，以防止下次生成时被覆盖", tableInfo.getEntityPacketName(),
                tableInfo.getEntityName());

        // 查询参数
        ParameterSpec.Builder param = ParameterSpec
                .builder(
                        ParameterizedTypeName
                                .get(ClassName.get(Wrapper.class), TypeName.get(tableInfo.getRawClassInfo()))
                                .annotated(AnnotationSpec.builder(Param.class)
                                        .addMember("value", CodeBlock.of("$T.WRAPPER", Constants.class)).build()),
                        "Wrapper");

        String sqlSelectList = this.getSelectList(tableInfo);
        String sqlFromList = this.getTableList(tableInfo);

        builder.addField(FieldSpec.builder(String.class, "SELECTLIST", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", sqlSelectList).build());
        builder.addField(FieldSpec.builder(String.class, "FROMLIST", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", sqlFromList).build());

        // 添加标准查询方法
        MethodSpec.Builder methodFindAll = MethodSpec.methodBuilder(method_findAllItem);
        // 添加返回对象 返回List<T>对象
        methodFindAll.addJavadoc("根据条件查询所有的$L数据", tableInfo.getEntityName()).addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(String.class)).addParameter(param.build())
                .addStatement("return $S+SELECTLIST+$S+FROMLIST+$S", "Select ", " Form ", " ${ew.customSqlSegment}");
        // 分页返回T对象
        MethodSpec.Builder methodFindAllByPage = MethodSpec.methodBuilder(method_findAllItemByPage);
        methodFindAllByPage.addJavadoc("根据条件查询所有的$L数据", tableInfo.getEntityName()).addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(String.class))
                .addParameter(ParameterSpec.builder(
                        ParameterizedTypeName.get(ClassName.get(Page.class), TypeName.get(tableInfo.getRawClassInfo())),
                        "page").build())
                .addParameter(param.build())
                .addStatement("return $S+SELECTLIST+$S+FROMLIST+$S", "Select ", " Form ", " ${ew.customSqlSegment}");
        builder.addMethod(methodFindAll.build());
        builder.addMethod(methodFindAllByPage.build());

        // 写入文件到生成目录下
        JavaFile javaFile = JavaFile.builder(tableInfo.getEntityPacketName(), builder.build()).build();

        javaFile.writeTo(filer);
        javaFile.writeTo(Paths.get(pathDir));

    }

    /**
     * 生成可编辑的mapper类
     * 
     * @throws IOException
     */
    private void writeMapperClass(TableInfo tableInfo, String pathDir, Filer filer) throws IOException {
        String mapperName = String.format("%sMapper", tableInfo.getEntityName());
        String baseMapperName = String.format("Base%sMapper", tableInfo.getEntityName());

        String classFilePath=Paths.get(pathDir,tableInfo.getEntityPacketName().replaceAll("\\.", "/"),mapperName+".java").toString();
        
        File file =Paths.get(pathDir,tableInfo.getEntityPacketName().replaceAll("\\.", "/"),mapperName+".java").toFile();
        // 原来代码已经存在，不再生成
        if(file.exists()) return ;    

        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(mapperName)
                .addJavadoc("实体$L.$L对应的mybatis的mapper对象,自定义的代码请添加在这里", tableInfo.getEntityPacketName(),
                        tableInfo.getEntityName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(tableInfo.getEntityPacketName(), baseMapperName))

        ;

                
        // 写入文件到生成目录下
        JavaFile javaFile=JavaFile.builder(tableInfo.getEntityPacketName(), builder.build())       
                                            
                .build();
        
        // 先把类放入编译器
        javaFile.writeTo(filer);        
        
        // 如果原来类存在则不保存
        javaFile.writeTo(Paths.get(pathDir)); 
    }


    /**
     * 通过字段取得sql语句
     */
    private String getFieldSql(TableFieldInfo field){
        String strTemplate="%s %s %s %s";
        String strFieldTypeTemplate=StringUtils.hasText(field.getLen())?"(%s)":"";
        String strFieldType=String.format(field.getJdbcType()+strFieldTypeTemplate, field.getLen());

        return String.format(strTemplate, 
            field.getFieldName(),
            strFieldType,
            (field.isCanNull()?"null":""),
            (field.isPrimaryKey()?"auto_increment primary key":""));

    }
    
}