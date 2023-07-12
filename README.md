<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">OPEN-SCOPE v2.0.0</h1>
<h4 align="center">open-scope</h4>

## 介绍

OpenScope是一种轻量级、易维护的数据权限的解决方案，它能处理比较复杂的权限操作逻辑。兼容操作权限Shiro等框架。

OpenScope提供了一种基于SQL的智能添加权限范围列的方案，相对原始的数据权限方案，它是轻量级的，它只有一些配置代码，同时它也是提高了代码的可维护性。另外它不需要额外的更改您的程序结构，就能轻松使您的项目支持数据权限操作。

什么是操作权限，什么是数据权限详细见[WIKI 简介](http://)

## 当前版本支持的功能

- 数据权限查询过滤
- 支持注解或代码方式添加范围ID
- 兼容其他Mybatis插件
- 支持较细粒度的多范围认证
- 支持数据SQL列过滤
- 支持数据返回结果属性过滤

## 当前版本处理流程

### 数据权限查询过滤

- 提取参数中的业务ID(SSID) 记为集合A
- 将集合A转换为范围ID(SID) 记为集合B
- 将集合B根据各自的范围拼装进SQL处理

### 数据权限鉴权

- 提取参数中的业务ID(SSID) 记为集合A
- 将集合A转换为范围ID(SID) 记为集合B
- 校验用户是否拥有集合B的数据权限

### 数据权限列数据过滤

- 提供对简单SQL列预查询过滤
- 提供对返回结果进行属性进行过滤（Map\List<POJO OR Map>\POJO）

## 涉及技术

### 编译环境：

- JDK:1.8
- SpringBoot:2.4.0(最低支持) ~ 2.7.8(最高支持)
- hutool:5.8.12
- druid:1.2.9(最低支持) ~ 1.2.16(最高支持)
- mybatis:3.4.5(最低支持)  ~ 3.5.11(最高支持)
- mybatis-spring-boot:2.1.4(最低支持) ~ 3.0.1(最高支持)

其他环境需要自行测试和替换可选依赖部分。

### 测试环境:

- junit:4.13.2
- h2:2.1.214

### 简单用法

#### 依赖部分

注意依赖使可选项，所以不会主动依赖，建议使用推荐的编译环境的依赖版本号。

~~~
<dependency>
    <groupId>com.mofum.scope</groupId>
    <artifactId>scope-mybatis-boot-starter</artifactId>
    <version>2.0.0.RELEASE</version>
</dependency>
~~~

以下依赖为【可替换】依赖，如果原环境没有，则需要添加，不会主动依赖。

~~~
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>${spring-boot.version}</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
    <version>${spring-boot.version}</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-autoconfigure</artifactId>
    <version>${spring-boot.version}</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>${mybatis-spring-boot.version}</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>${druid.version}</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>cglib</groupId>
    <artifactId>cglib-nodep</artifactId>
    <version>${cglib.version}</version>
    <scope>compile</scope>
</dependency>
~~~

#### 代码部分

1. 基础使用

##### 注解说明：

open-scope 提供了丰富的注解，来支持数据操作。

~~~
@SID //范围ID注解：用于指明具体的列信息,该注解优先级高于@SSID @Schema

@SSID //业务ID注解：用于提取业务ID，转换范围ID，认证等操作。注解：该注解优先级高于@Schema

@Scope //范围注解：用于指明返回结果的的列，查询结果的列（只有加了@Scope注解时@SSID才会生效）

@Schema //表注解：用于指明同一查询中的的表名，表别名.
~~~

以下是一个简单使用示例 ，具体的使用示例可以看我们配置的 【RuoYi-Vue 示例】:https://gitee.com/mofum/open-scope-demo

~~~
@RestController
@Scope //被Scope标记的将会被开启权限范围扫描
@RequestMapping("/scope")
public class ScopeController {
    @Autowired
    ScopeService scopeService;

    @GetMapping("/user/list")
    @Scope //被Scope标记的将会被开启转换器功能 业务ID转范围ID
    @Schema(value = "sys_user", alias = "u") //被Schema注解标记则会填充当前业务ID没有指定的表信息
    @SID("dept_id") //被SID标记则会填充具体的数据列对应关系
    public List<SysUser> userList(@SSID String deptId) { // @SSID表示这是一个业务对象，可以是带有@Scope注解的对象，也可以是List，也可以是Map,还可以是JSON字符串(对象和数组)
        return scopeService.selectList();
    }

    //这是一个没有开启权限范围的方法
    @GetMapping("/user/list2")
    public List<SysUser> userList2() {
        return scopeService.selectList();
    }

}
~~~

2. 编程方式使用

~~~
//内置环境,编码调整数据权限
SID sid = new SID();
sid.setColumn("test");
sid.setValue("2334");
SID sid2 = new SID();
sid2.setColumn("test");
sid2.setValue("2335");

Envs.addSID(sid);
Envs.addSID(sid2);

//配合SQL重构器来配合其他使用(例如JDBC工具类)
DruidSQLRewriter rewriter = new DruidSQLRewriter();
rewriter.getFormatOption().setPrettyFormat(false);
rewriter.getFormatOption().setUppCase(false);
String newSql = rewriter.rewrite(sql);
~~~

3. SQL重写器（默认：DruidSQLRewriter）

~~~
@Bean
public SQLRewriter sqlRewriter(SQLCompatibleProcessor sqlCompatibleProcessor) {
    CustomSQLRewriter sqlRewriter = new CustomSQLRewriter();
    sqlRewriter.setCompatibleProcessor(sqlCompatibleProcessor);
    return sqlRewriter;
}
~~~

4. SQL兼容器（QL重写器是：DruidSQLRewriter才会生效）

~~~
@Bean
public SQLCompatibleProcessor sqlCompatibleProcessor() {
    return new DruidSQLCompatibleProcessor();
}
~~~

5. 关于规则表达式

~~~
<name>-<quidn>
<列名/属性名>-<权限>
q:查询权限
u:更新权限
i:插入权限
d:删除权限
n:无权限
~~~

6. 数据结果SQL列过滤

~~~
@Scope 指定 columnRules属性即可

//意思是id没有权限，将被过滤
@Scope(columnRules="id-n")
~~~

7. 数据结果属性过滤

~~~
@Scope 指定 propertiesRules属性即可

//意思是属性id没有权限，将被过滤
@Scope(columnRules="id-n")
~~~

8. 编程方式使用过滤

~~~
//属性过滤
EnvsRule.addPropertiesRule("prop-n");
//SQL列过滤
EnvsRule.addColumnRule("col-n");
~~~

9. 关于认证器
   
认证器提供对每一组业务ID，权限ID进行单独校验，你只需要在@SSID注解中指定authenticator或者authenticatorClass即可

~~~
authenticator ： 取全局环境的配置，如果全局没有，存在Spring环境，则取Spring容器的认证器。
authenticatorClass ：静态认证器,通过反射新建认证器进行认证
~~~

#### 联系我们

QQ交流群:1062019634(200人)


