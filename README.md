# 数据库 - self_study

## TODO: 数据库没有清理过往数据功能
## TODO: 数据库时区确认
## 表结构
    CREATE DATABASE study;
### 用户表 - user_form
|名字|类型|
|--- |---|
|openid🔑|char|
|session_key|varchar|
|avatar|varchar|
|cookie|varchar|
|vip_daypass|smallint|
|vip_time|int|
|user_status|tinyint| // 
    USE study;
    CREATE TABLE user_form
    (
        openid char(100) NOT NULL,
        session_key varchar(100),
        avatar varchar(100),
        cookie varchar(100),
        vip_daypass smallint,
        vip_time int,
        user_status tinyint,
        PRIMARY KEY(openid)
    );
    
#### 状态表
|状态代码|状态含义|
|-|-|
|0|无状态|
|1|正在使用时长|
|2|正在使用天卡|
|3|已预定|

### 自习桌表 - table_form
|名字|类型|
|--- |---|
|table_id🔑|int|
|is_reserve|tinyint|
|is_using|tinyint|


    USE study;
    CREATE TABLE table_form
    (
        table_id int NOT NULL AUTO_INCREMENT,
        is_reserve tinyint,
        is_using tinyint,
        PRIMARY KEY(table_id)
    );


### 预定表 - reserve_form
|名字|类型|
|--- |---|
|reserve_id🔑|int|
|reserve_start|datetime|
|reserve_end|datetime|
|create_time|datetime|
|openid|varchar|
|table_id|int|
|reserve_status|tinyint|

    USE study;
    CREATE TABLE reserve_form
    (
        reserve_id int NOT NULL AUTO_INCREMENT,
        reserve_start datetime,
        reserve_end datetime,
        create_time datetime,
        openid varchar(100),
        table_id int,
        reserve_status tinyint,
        PRIMARY KEY(reserve_id)
    );
- ~~注意！这里的代码顺序很重要，因为mysql返回的datatime类型的数据是TimeStamp类型，所以需要通过构造函数把TimeStamp转换为String，但是这样就覆盖了无参的构造函数，导致mybatis无法通过变量名自动匹配变量。为了保证变量构造正确，需要数据库中的变量顺序和有参构造函数一致。~~

#### 状态表
|状态代码|状态含义|
|-|-|
|0|已完成|
|1|已过期|
|2|待确认|
|3|正在使用|
|4|已确认未使用|

### 会员充值记录表 - recharge_record_form  
|名字|类型|
|--- |---|
|recharge_record_id🔑|int|
|wechat_pay_id|char(100)|
|vip_daypass|smallint|
|vip_time|int|
|openid|char(100)|
|create_time|datetime|

    USE study;
    CREATE TABLE recharge_record_form
    (
        recharge_record_id int NOT NULL AUTO_INCREMENT,
        wechat_pay_id char(100),
        vip_daypass smallint,
        vip_time int,
        openid char(100),
        create_time datetime,
        PRIMARY KEY(recharge_record_id)
    );


## MySQL创建代码
    mysql -u root -p
    root
    CREATE DATABASE study;
    USE study;
    CREATE TABLE user_form
    (
        openid char(100) NOT NULL,
        session_key varchar(100),
        avatar varchar(100),
        cookie varchar(100),
        vip_daypass smallint,
        vip_time int,
        user_status tinyint,
        PRIMARY KEY(openid)
    );
    USE study;
    CREATE TABLE table_form
    (
        table_id int NOT NULL AUTO_INCREMENT,
        is_reserve tinyint,
        is_using tinyint,
        PRIMARY KEY(table_id)
    );
    USE study;
    CREATE TABLE reserve_form
    (
        reserve_id int NOT NULL AUTO_INCREMENT,
        reserve_start datetime,
        reserve_end datetime,
        create_time datetime,
        openid varchar(100),
        table_id int,
        reserve_status tinyint,
        PRIMARY KEY(reserve_id)
    );

## Cookie
创建cookie : openid + session_key ==sha==> cookie  
验证cookie : 通过openid得到session_key,然后加密并验证

## 笔记
### 获取当前时间戳
    System.currentTimeMillis();
### 时间戳转字符串
    Long timenow = System.currentTimeMillis();
    SimpleDateFormat s= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String now = s.format(timenow);
    显示结果：2021-03-10 03:32:53
### 字符串转时间戳
    SimpleDateFormat s= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    System.out.println(s.parse("2021-03-10 03:32:53").getTime());
    显示：1615318373000
### 泛型
    public static <T> Response<T> fail(String msg) {
        return new Response<T>(0, msg, null);
    }
- 第一个\<T>表示这个函数是一个泛型函数，第二个\<T>则是Response的泛型

### 插入数据并返回新数据的主键
1. 在application.yml添加配置  

        mybatis:
            mapper-locations:  
                classpath:com.example.study.mapper/*.xml
2. 在resources文件夹下建立 com.example.study.mapper/ReserveMapper.xml
3. ReserveMapper.xml内容：

        <?xml version="1.0" encoding="UTF-8" ?>
            <!DOCTYPE mapper
                    PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
                    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

        <mapper namespace="com.example.study.mapper.ReserveMapper">
            <insert id="reserveTest" useGeneratedKeys="true"  keyProperty="reserve_id" parameterType="java.lang.Integer">
                INSERT INTO reserve_form (table_id) VALUE (#{table_id});
            </insert>
        </mapper> 
4.  ReserveMapper：
    
        public void reserveTest(Reserve reserve);
5. 运行后reserve的reserve_id属性就是返回的键值

### 项目打成Jar包
[教程链接][1]

### 调用resources目录下的string.properties文件
    ResourceBundle res = ResourceBundle.getBundle("string"); // 文件名
    String userName = res.getString("user.name");//获取资源application中的user.name字段的值——root
    Integer userPassword = new Integer(res.getString("user.password"));
    System.out.println(userName + userPassword);

### 注解
#### @Resource
没弄明白，就当作可以自动实例化一个类的成员变量。
#### @Service @RestController @Mapper
标记类在Spring中的作用。Controller层调用Service层，Service层调用Mapper层？
#### @JsonIgnore
不传输给前台，包括Swagger
#### Swagger注解
 [教程链接][2]

### 分层
#### entity层  
* entity就是属性类，通常定义在model层里面，相当于MVC的M层，属于数据模型层  
* 一般得实体类对应一个数据表，其中的属性定义数据表中的字段，实体类的字段数量 >= 数据库表中需要操作的字段数量  
#### dao层
* A：dao层叫做数据访问层，全称为data access object，属于一种比较底层基础得操作，具体到对某个表得增删改查，换句话说，某个dao一定是和数据库中的某一张表一一对应的，而且其中也只是封装了增删改查得方法

#### service层  
* service层即为业务逻辑层，可以理解为对一个或者多个dao进行得再次封装，主要是针对具体的问题的操作，把一些数据层的操作进行组合，间接与数据库打交道(提供操作数据库的方法)。要做这一层的话，要先设计接口，再实现类。

#### controller层  
* 负责请求转发，接收页面过来的参数，传给service处理，接到返回值，并再次传给页面  
#### mapper层  
* 数据存储对象，相当于DAO层，mapper层直接与数据库打交道(执行SQL语句)，接口提供给service层。





[1]: https://blog.csdn.net/peng86788/article/details/80534086
[2]: https://zhuanlan.zhihu.com/p/49996147