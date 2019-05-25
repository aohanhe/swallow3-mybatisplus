package swallow3.mybatisplus.codegenhelper;



import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
import org.w3c.dom.TypeInfo;

/**
 * 代码生成器
 */
public class CodeGener {
    private TableInfo tableInfo;
   

    public CodeGener(TableInfo tableInfo) {
        this.tableInfo = tableInfo;       

       // assert tableInfo != null : "params tableInfo can`t be null";
    }

    /**
     * 生成基础mapper类
     * 
     * @throws IOException
     */
    public void outBaseMapper(TypeMirror entity) throws IOException {
        TypeSpec.Builder builder=TypeSpec.interfaceBuilder(String.format("Base%sMapper",
            "Test"));

            TypeName tt=TypeName.get(entity);

            MethodSpec.Builder methodFindAll=MethodSpec.methodBuilder("findAllItem");
            methodFindAll.returns(tt)
                .addModifiers(Modifier.PUBLIC,Modifier.ABSTRACT);
        
                FieldSpec.Builder fbu= FieldSpec.builder(tt, "test",
                 Modifier.PUBLIC,Modifier.FINAL,Modifier.STATIC)
            .initializer(CodeBlock.of("new $T()",tt)) ;      
            
        builder.addField(fbu.build());

        builder.addMethod(methodFindAll.build());

        JavaFile javaFile=JavaFile.builder("swallow3.sample.test.do", builder.build())             
                                 
            .build();

        
      
            

        
        javaFile.writeTo(Paths.get("D:\\tmp"));

    }
    public void outBaseMapper1() throws IOException {
        
        String sql=this.getSelectList()+" "+this.getTableList()+" ${ew.customSqlSegment}";

      
        TypeSpec.Builder builder=TypeSpec.interfaceBuilder(String.format("Base%sMapper",
            tableInfo.getEntityName()));
        
        builder=builder.addJavadoc(
            String.format("实体%s.%s对应的基础mybatis的mapper对象,请不要在这里添加代码，以防止下次生成时被覆盖", 
                tableInfo.getEntityPacketName(),tableInfo.getEntityName()))
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(
                ParameterizedTypeName.get(ClassName.get(BaseMapper.class), TypeName.get(tableInfo.getRawClassInfo()))    
            );
            

        MethodSpec.Builder methodFindAll=MethodSpec.methodBuilder("findAllItem");

        ParameterSpec.Builder param=ParameterSpec.builder(
            ParameterizedTypeName.get(ClassName.get(Wrapper.class), TypeName.get(tableInfo.getRawClassInfo()))
                .annotated(AnnotationSpec.builder(Param.class)
                                             .addMember("value", "Constants.WRAPPER")
                                             .build())
                ,"Wrapper");

        // 返回List<T>对象
        methodFindAll
            .addJavadoc("根据条件查询所有的%s数据", tableInfo.getName())
            .addModifiers(Modifier.PUBLIC,Modifier.ABSTRACT)
            .returns(ParameterizedTypeName.get(ClassName.get(List.class), TypeName.get(tableInfo.getRawClassInfo())))
            .addParameter( param.build())
            ;
        
        //  分页返回T对象
        MethodSpec.Builder methodFindAllByPage=MethodSpec.methodBuilder("findAllItemByPage");
        methodFindAllByPage
            .addModifiers(Modifier.PUBLIC,Modifier.ABSTRACT)
            .returns(ParameterizedTypeName.get(ClassName.get(Page.class),TypeName.get(tableInfo.getRawClassInfo())))
            .addParameter(
                ParameterSpec.builder(ClassName.get(Page.class), "page").build()
            ) 
            .addParameter(
                // 添加 @Param(Constants.WRAPPER) Wrapper<T> wrapper 参数
                ParameterSpec.builder(
                  ParameterizedTypeName.get(
                      ClassName.get(Wrapper.class),ClassName.get(tableInfo.getEntityPacketName(), "Student")), "wrapper")
                    .addAnnotation(AnnotationSpec.builder(Param.class).addMember("value", 
                        CodeBlock.builder().add("$T.WRAPPER", Constants.class).build()).build())
                    .build())                  
            .addJavadoc("根据条件查询所有的%s数据", tableInfo.getName());
            
            

        builder.addMethod(methodFindAll.build());
        builder.addMethod(methodFindAllByPage.build());
                
        JavaFile javaFile=JavaFile.builder(tableInfo.getEntityPacketName(), builder.build())                                   
            .build();

      

        
        javaFile.writeTo(Paths.get("D:\\tmp"));
    }

    /**
     * 取得查询选择列表
     */
    private String getSelectList(){
        // 主表列
        StringBuffer sql=new StringBuffer();
        String mainSelect=String.format("Select %s.* ", tableInfo.getAliasName());
        sql.append(mainSelect);

        // 添加从表列
        for (TableFieldInfo item : tableInfo.getFields()) {
            if(!item.isTransient()) continue;

            sql.append(String.format(",%s.%s", item.getTableAliasName(),item.getFieldName()));
        } 

        return sql.toString();
    }

    /**
     * 取得表列表字串
     * @return
     */
    private String getTableList(){
        // 主表
        StringBuffer sql=new StringBuffer();
        String mainAlisasName=this.tableInfo.getAliasName();
        sql.append(String.format("from %s %s ", this.tableInfo.getName(),mainAlisasName));

        // 添加join列表
        for(TableLeftJoinInfo join:tableInfo.getJoins()){
            sql.append(String.format("left join %s %s on %s.%s=%s.%s ", 
                join.getTableName(),join.getTableAliasName(),
                join.getMainTableAliasName(),join.getMainFieldName(),
                join.getTableAliasName(),join.getFieldName()));
        }

        return sql.toString();        
    }
    
}