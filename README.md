# swallow3-mybatisplus
基于mybatisplus与springboot2.1.5的快速代码生成apt架构

## 源起
在使用mybatis的开发过程中，需要进行大量的mapper文件的编写，复杂的代码会造成项目管理维护的困难。偶然的机会，一个同事向我推荐了[mybatis-plugs](https://mp.baomidou.com/),这个项目大大的简化了我们使用mybatis的代码编写工作，但是实际的开发过程中我们还是有不少的不便的问题：
1. 使用wrapper可以帮助我们动态构建查询语句，但是如果使用lamda表达式查询，在spring boot工程中如果使用了devtool不能正常工作。如果我们是在开发一个大的工程，开发调试时每次都要重启应用那是一个很痛苦的工作。
2. 在项目的开发中，我们定义一个实体时，最常见的是这个实体通过一个主表关联多个从表，我们再使用mybatis映射到实体的transient中。原来我们在项目开发中，自己做了一个window程序来辅助我们进行这类sql以及mapper接口的生成。使用了mybatis-plus后这个工作一样存在。这一个工作是一个烦人，而且我们经验随着项目的需求变更需要对这些SQL、实体类进行调整。
3. 对实体的修改，往往我们需要对表也进行修改，这也是一个烦人的工作。

## 主体功能
针对以上问题，我们使用annotationProcessor技术，通过分析在entity上的注解，自辅助我们进行代码生成.
1. 生成BaseXXXMapper:这个类提供了联结表查询的功能，可以使用wrapper进行动态条件的构造。
2. 生成了BaseXXXMapperSql:这个类提供了表联结的Sql的生成，之所以分离这个类，是为了方便编写自主的查询时可以利用已经生成好的联结语句.
3. 生成了XXXMeta类:这个类方便我们在使用wrapper进行语句构建时不需要记录表的字段名称，没有这个类的辅助会很痛苦。我们生成了所有表字段的常量。这样我们就可以不用舍弃devtool而用lamda方案.
4. 生成了XXXMapper:这个类只会生成一次，如果您不删除它，再次编译时不会再自动生成。这个类用于存放用户自己编写的映射函数，它是从BaseXXXMapper派生出来的，请大家不要直接使用BaseXXXMapper。

## 使用方法
1. 使用spring boot2 创建一个工程，添加com.baomidou:mybatis-plus-boot-starter:3.1.1引用
2. 按以下的样例来修改build.gradle文件
```
plugins {
	id 'org.springframework.boot' version '2.1.5.RELEASE'
	id 'java'
}

apply plugin: 'io.spring.dependency-management'

group = 'swallow3.sample'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
	
}

repositories {
	mavenCentral()
}

dependencies {
	compile project(":core-framework")
	 
	implementation 'com.baomidou:mybatis-plus-boot-starter:3.1.1'
	implementation 'org.springframework.boot:spring-boot-starter'
	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'org.springframework.boot:spring-boot-devtools'
	runtimeOnly 'mysql:mysql-connector-java'
	annotationProcessor 'org.projectlombok:lombok'
	annotationProcessor project(":core-framework")
	annotationProcessor project(":annotations-processor")
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

// 源代码目录设置
sourceSets {
        main{
            java {
                srcDirs "src/main/java"
				srcDirs "src/swallowgen/java"            
            }
        }
 }

ext{
    // 指定生成代码的目录名
	outdir="swallowgen"
}

// 编译参数设置
compileJava.options.encoding = 'UTF-8'
// 指定可覆盖代码生成的目录
compileJava.options.compilerArgs<< "-AgenCodeDir=$projectDir/src/$outdir/java"
// 指定不可覆盖代码，初始生成的目录
compileJava.options.compilerArgs<< "-AcodeDir=$projectDir/src/main/java"
// 指定建表语句生成的目录
compileJava.options.compilerArgs<< "-AsqlDir=$projectDir/src/$outdir/sql"
// 指定编译时是否打印出分析到的实体信息
compileJava.options.compilerArgs<< "-Adebug=true"
// 指定数据库类型  当前我只编写了mysql版本 大家可以照着接口来修改
compileJava.options.compilerArgs<< "-AdbType=mysql"
// 是否生成建表sql的开关
compileJava.options.compilerArgs<< "-AcreateSql=true"

compileJava {	
	// 设置 在编译指令后加-PgenCode 进行代码生成
	doFirst{

		if (project.hasProperty("genCode")) {	
			// 生成或清队目录原有的数据
			file(new File(projectDir, "src/$outdir/java")).deleteDir()
			// 确保辅助文件所在的位置
			file(new File(projectDir, "src/$outdir/java")).mkdirs()	

			options.compilerArgs << "-AgenCode=true"
		}
	}
}

```
3. 执行`gradlew clean build -PgenCode` 即可生成代码。 注意，执这个指令时，会先删除上一次的所有可覆盖文件，如果编译时不想重复这个操作，可以直接使用`gradlew build`