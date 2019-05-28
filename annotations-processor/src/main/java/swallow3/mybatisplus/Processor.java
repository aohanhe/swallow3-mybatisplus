package swallow3.mybatisplus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

import com.google.auto.service.AutoService;


import swallow3.mybatisplus.codegenhelper.Config;
import swallow3.mybatisplus.codegenhelper.ConfigHelper;
import swallow3.mybatisplus.codegenhelper.LoadTableInfoHelper;
import swallow3.mybatisplus.codegenhelper.TableInfo;
import swallow3.mybatisplus.codegenhelper.codeGener.ICodeGener;
import swallow3.mybatisplus.codegenhelper.mysql.MysqlJavatypeToJdbType;
import javax.annotation.processing.Messager;

/**
 * 注解处理器，将搜索到的SwallowEnity进行解析，以生成Mapper对象,以及查询辅助对象
 */
// @AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({ "test1", "test2" })
public class Processor extends AbstractProcessor {
    private Types types;
    private Elements elements;
    private Messager messager;
    private Config config;
    private ICodeGener codeGener;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {

        super.init(processingEnv);
        processingEnv.getMessager().printMessage(Kind.NOTE, "swallow-mybatis-plus annotations processor初始化");

        types = processingEnv.getTypeUtils();
        elements = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();

        // 加载配置
        this.config = ConfigHelper.getConfigFromOptions(processingEnv.getOptions());

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!this.config.isGenCode()) {
            messager.printMessage(Kind.WARNING, "没有打开生成代码开关，如果需要和成代码请添加-A" + Config.Option_GenCode + "=true");
            return false;
        }

        this.codeGener = ConfigHelper.getCodeGener(config.getDbType());

        // 取得所有的实体类
        Set<? extends Element> sets = roundEnv.getElementsAnnotatedWith(SwallowEnity.class);
        if (sets == null || sets.size() == 0)
            return false; // 不是自己处理时跳过

        sets.forEach(element -> {
            PackageElement elPackage = elements.getPackageOf(element);

            try {

                LoadTableInfoHelper helper = new LoadTableInfoHelper(elPackage, element, this.types,
                        new MysqlJavatypeToJdbType(),config);
                TableInfo tableInfo = helper.loadFromClassInfo();

                if (this.config.isDebug())
                    tableInfo.printToMessager(messager);

                messager.printMessage(Kind.NOTE,
                        String.format("开始处理类%s.%s:", elPackage.getQualifiedName(), element.getSimpleName()));

                messager.printMessage(Kind.NOTE,
                        String.format("处理类%s.%s完成:", elPackage.getQualifiedName(), element.getSimpleName()));

                this.codeGener.genCreateTableSql(tableInfo, config);
                this.codeGener.genTableInfoCode(tableInfo, config, this.elements, this.filer);
                this.codeGener.genQueryMetaHelperCode(tableInfo, config, this.elements, filer);

            } catch (Exception e) {
                messager.printMessage(Kind.ERROR, String.format("处理类%s.%s时发生错误:" + e.getMessage(),
                        elPackage.getQualifiedName(), element.getSimpleName()));
                throw new RuntimeException(e);
            }

        });

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(SwallowEnity.class.getCanonicalName());
        return set;
    }

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> set = new HashSet<>();
        set.addAll(ConfigHelper.getSupportOptions());
        return set;
    }

}