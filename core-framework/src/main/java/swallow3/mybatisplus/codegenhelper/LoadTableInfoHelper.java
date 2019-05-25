package swallow3.mybatisplus.codegenhelper;


import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.lang.model.element.Modifier;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import swallow3.mybatisplus.SwallowEnity;
import swallow3.mybatisplus.SwallowField;
import swallow3.mybatisplus.SwallowLeftJoin;

/**
 * 表信息加载助手
 */
public class LoadTableInfoHelper {
    private final PackageElement packageEl;
    private final Element el;
    private final TypeMirror typeRawMirror;
    private final Types typesTool;
    private final IJavaTypeToJdbType javaTypeToJdbType;
   

    /**
     * 构造加载类
     * 
     * @param packageEl
     * @param el
     * @param typeMirror
     * @param typesTool
     * @param javaTypeToJdbType
     */
    public LoadTableInfoHelper(PackageElement packageEl, Element el,  Types typesTool,
            IJavaTypeToJdbType javaTypeToJdbType) {
        this.packageEl = packageEl;
        this.el = el;
        this.typeRawMirror = el.asType();
        this.typesTool = typesTool;
        this.javaTypeToJdbType = javaTypeToJdbType;
     

    }

    /**
     * 从指定的类中加载类的信息
     * 
     * @param classInfo
     * @return
     */
    public TableInfo loadFromClassInfo() {

        SwallowEnity swallowEnity = el.getAnnotation(SwallowEnity.class);
        Assert.notNull(swallowEnity, "classInfo want annotation SwallowEnity");

        TableInfo tableInfo = new TableInfo();
        tableInfo.setRawClassInfo(typeRawMirror);

        tableInfo.setEntityPacketName(packageEl.getQualifiedName().toString());
        tableInfo.setEntityName(el.getSimpleName().toString());

        // 加载表基础信息
        loadTableInfo(tableInfo);

        // 加载字段信息
        loadFields(tableInfo);

        // 加载左联结
        loadLeftJoins(tableInfo);

        // 检查联结是否法
        tableInfo.checkLeftJoinIsOk();
        // 检查字段是否合法
        tableInfo.checkFieldInfo();

        return tableInfo;
    }

    /**
     * 加载表信息
     * 
     * @param classInfo
     * @param tableInfo
     */
    private void loadTableInfo(TableInfo tableInfo) {
        SwallowEnity meta = el.getAnnotation(SwallowEnity.class);
        String aliasName = meta.aliasName();

        TableName tableNameAnn = el.getAnnotation(TableName.class);
        Assert.notNull(tableNameAnn, String.format("类%s.%s没有设置SwallowEnity别名也没有设置@TableName",
         tableInfo.getEntityPacketName(),tableInfo.getEntityName()));
        String tableName = tableNameAnn.value();

        if (StringUtils.isEmpty(aliasName)) {
            aliasName = tableName;
        }

        Assert.hasText(aliasName, String.format("没有找到实体%s.%s的表别名", tableInfo.getEntityPacketName(),tableInfo.getEntityName()));

        tableInfo.setAliasName(aliasName);
        tableInfo.setName(tableName);

    }

    /**
     * 加载所有的字段列表信息
     * 
     * @param classInfo
     * @param tableInfo
     */
    private void loadFields(TableInfo tableInfo) {
        List<Element> fields = getClassAllFields(typeRawMirror, typesTool);
        List<TableFieldInfo> listFields = new ArrayList<>();

        for (Element field : fields) {

            TableFieldInfo fieldInfo = new TableFieldInfo();
            String name = field.getSimpleName().toString();
            fieldInfo.setName(name);
            fieldInfo.setFieldAliasName(name);

            // 确定字段是不是主键
            if(field.getAnnotation(TableId.class)!=null){
                fieldInfo.setPrimaryKey(true);
            }
            // 检查是否为快照数据
            fieldInfo.setTransient(field.getModifiers().contains(Modifier.TRANSIENT));

            // 计算字段的名称
            String tableFieldName = name;
            TableField tableField = field.getAnnotation(TableField.class);
            SwallowField swallowField = field.getAnnotation(SwallowField.class);

            if(swallowField!=null&&StringUtils.hasText(swallowField.fieldName())){
                tableFieldName=swallowField.fieldName();
            }else if (tableField != null && StringUtils.hasText(tableField.value())) {
                tableFieldName = tableField.value();
            }
           

            // 设置字段的别名
            if (tableField != null && StringUtils.hasText(tableField.value())) {
                fieldInfo.setFieldAliasName(tableField.value());
            }

            Assert.hasText(tableFieldName, String.format("字段 %s 同时没有设置@SwallowField的fieldName与@TableField的value", name));

            fieldInfo.setFieldName(tableFieldName);

            // 设置字段是否可以为空
            if(swallowField!=null&&!swallowField.canNull()){
                fieldInfo.setCanNull(false);
            }

            // 检查注解是否设置了jdbcType
            String jdbcType = null;
            String len = null;
            if (swallowField != null) {
                jdbcType = swallowField.jdbcType();
                len = swallowField.length();
            }

            if (!StringUtils.hasText(jdbcType)) {
                jdbcType = this.javaTypeToJdbType.getJdbcType(this.getFieldTypeKind(field));
            }
            fieldInfo.setJdbcType(jdbcType);

            // 检查是否必须提供长度
            if(javaTypeToJdbType.isMustLen(jdbcType)&&!fieldInfo.isTransient())  
                Assert.hasText(len,String.format("字段%s是必需提供长度的%s子段",name, jdbcType));

            fieldInfo.setLen(len);

            // 计算字段对应的表别名
            String tableAliasName = tableInfo.getAliasName();

            if (swallowField != null) {
                if (StringUtils.hasText(swallowField.tableAliasName())) {
                    tableAliasName = swallowField.tableAliasName();
                }
            }

            fieldInfo.setTableAliasName(tableAliasName);

            listFields.add(fieldInfo);
        }

        tableInfo.setFields(listFields);
    }

    /**
     * 取得字段的数据类型
     */
    private TypeKind getFieldTypeKind(Element el) {
        TypeMirror type = el.asType();
        return type.getKind();
    }

    /**
     * 加载类所有的左联结
     * 
     * @param classInfo
     * @param tableInfo
     */
    private void loadLeftJoins(TableInfo tableInfo) {

        // 加载类一级的左联结
        List<TableLeftJoinInfo> joins = new ArrayList<>();
        joins.addAll(getClassLevelAllLeftJoin());
        joins.addAll(getAllFieldLefJoinInfo(tableInfo.getName(), tableInfo.getAliasName()));

        final String mainTable = tableInfo.getName();
        // 对join进行排序，保证主表是当前实体的排在前面
        joins.sort((item1, item2) -> {
            // 两表相同表示相等
            if (item1.getMainTableName().equals(item2.getMainTableName()))
                return 0;
            // 两个表都不是主表 也是相等
            if ((!item1.getMainTableName().equals(mainTable)) && (!item2.getMainTableName().equals(mainTable)))
                return 0;
            // 余下两个有一个是主表 如果第一个是主表，第一个更小
            if (item1.getMainTableName().equals(mainTable))
                return -1;
            // 后一个是主表
            return 1;
        });

        tableInfo.setJoins(joins);
    }

    /**
     * 取得类的所有字段信息，包括基础类
     * 
     * @param classInfo
     * @return
     */
    private static List<Element> getClassAllFields(TypeMirror typeMirror, Types typesTool) {
        List<Element> listFields = new ArrayList<>();
        while (typeMirror != null) {
            Element element = typesTool.asElement(typeMirror);

            // 添加所有的属性
            for (Element subEl : element.getEnclosedElements()) {
                if (subEl.getKind() == ElementKind.FIELD) {
                    
                    listFields.add(subEl);
                }
            }

            List<? extends TypeMirror> parents = typesTool.directSupertypes(typeMirror);

            // 取得父一级的类
            typeMirror = null;
            if (parents != null) {
                for (TypeMirror type : parents) {
                    Element el = typesTool.asElement(type);
                    if (el.getKind() == ElementKind.CLASS) {
                        typeMirror = type;
                    }
                }
            }
        }
        return listFields;
    }

    /**
     * 取得所有字段的联结信息
     */
    private List<TableLeftJoinInfo> getAllFieldLefJoinInfo(String mainTableName, String mainTableAliasName) {

        List<Element> fields = getClassAllFields(typeRawMirror, typesTool);

        List<TableLeftJoinInfo> res = new ArrayList<>();
        for (Element field : fields) {
            if (field.getAnnotation(SwallowLeftJoin.class) == null)
                continue;
            String mainFieldName = field.getSimpleName().toString();
            // 如果加了tablefield标签用tablefield的值
            TableField tf = field.getAnnotation(TableField.class);
            if (tf != null) {
                if (StringUtils.hasText(tf.value()))
                    mainFieldName = tf.value();
            }
            
            res.add(getLeftJoinInfoFromSwallowLeftJoin(field.getAnnotation(SwallowLeftJoin.class), false, mainFieldName,
                    mainTableName, mainTableAliasName));
        }

        return res;
    }

    // 取得类的所在类一级所有的左联结
    private List<TableLeftJoinInfo> getClassLevelAllLeftJoin() {
        List<TableLeftJoinInfo> res = new ArrayList<>();
        TypeMirror typeMirror=this.typeRawMirror;
        while (typeMirror != null) {
            SwallowLeftJoin[] joins = typesTool.asElement(typeMirror).getAnnotationsByType(SwallowLeftJoin.class);

            if (joins != null) {
                for (SwallowLeftJoin join : joins) {
                    res.add(getLeftJoinInfoFromSwallowLeftJoin(join, true, null, null, null));
                }
            }

            // 取得父一级的类
            List<? extends TypeMirror> parents = typesTool.directSupertypes(typeMirror);
            typeMirror = null;
            if (parents != null) {
                for (TypeMirror type : parents) {
                    Element elCur = typesTool.asElement(type);
                    if (elCur.getKind() == ElementKind.CLASS) {
                        typeMirror = type;
                    }
                }
            }
        }
        return res;
    }

    /**
     * 将注解转换为joininfo
     * 
     * @param join
     * @param mainTableName
     * @param mainTableAliasName
     * @return
     */
    private static TableLeftJoinInfo getLeftJoinInfoFromSwallowLeftJoin(SwallowLeftJoin join, boolean isFromClass,
            String fieldRawName, String enityTableName, String entityTableAliasName) {

        TableLeftJoinInfo joinInfo = new TableLeftJoinInfo();

        // 联结字段名
        String fieldName = join.fieldName();
        Assert.hasText(fieldName,"@TableLeftJoinInfo 在类/字段上申明必须设置 fieldName ");
        joinInfo.setFieldName(fieldName);

        // 联结表名
        String tableName = join.joinTableName();
        Assert.hasText(tableName, "@TableLeftJoinInfo 在类/字段上申明必须设置 joinTableName ");
        joinInfo.setTableName(tableName);

        // 联结表别名
        String tableAliasName = join.joinTableAliasName();
        Assert.hasText(tableAliasName, "@TableLeftJoinInfo 在类/字段上申明必须设置 joinTableAliasName ");
        joinInfo.setTableAliasName(tableAliasName);

        // 主表字段名
        String mainFieldName = join.mainFieldName();
        if (isFromClass)
            Assert.hasText(mainFieldName,"@TableLeftJoinInfo 在类上申明必须设置 mainFieldName ");
        else
            mainFieldName = fieldRawName;
        joinInfo.setMainFieldName(mainFieldName);

        // 主表名称
        String mainTableName = join.mainTableName();
        if (isFromClass)
            Assert.hasText(mainTableName,"@TableLeftJoinInfo 在类上申明必须设置 mainTableName ");
        else
            mainTableName = enityTableName;
        joinInfo.setMainTableName(mainTableName);

        // 主表别名
        String mainTableAliasName = join.mainTableAliasName();
        if (isFromClass)
            Assert.hasText(mainTableAliasName, "@TableLeftJoinInfo 在类上申明必须设置 mainTableAliasName ");
        else
            mainTableAliasName = entityTableAliasName;
        joinInfo.setMainTableAliasName(mainTableAliasName);

        return joinInfo;

    }

}