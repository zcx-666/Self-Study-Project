# 数据库 - self_study
## 表结构
    CREATE DATABASE study;
### 用户表 - user_form
openid
reserve_status
using_status
isadmin
avatar
vip_daypass
vip_time
session_key
cookie
is_using_daypass
overdue_time

| 名字             | 类型     |
| ---------------- | -------- |
| openid🔑          | char     |
| session_key      | varchar  |
| avatar           | varchar  |
| cookie           | varchar  |
| vip_daypass      | smallint |
| vip_time         | int      |
| isadmin          | tinyint  |
| reserve_status   | tinyint  |
| using_status     | tinyint  |
| is_using_daypass | tinyint  |
| overdue_time     | datetime |


    USE study;
    CREATE TABLE user_form
    (
        openid char(100) NOT NULL,
        session_key varchar(100),
        avatar varchar(100),
        cookie varchar(100),
        vip_daypass smallint,
        vip_time int,
        isadmin tinyint,
        reserve_status tinyint,
        using_status tinyint,
        is_using_daypass tinyint,
        overdue_time datetime,
        PRIMARY KEY(openid)
    );
    
#### 状态表
| 状态代码 | 状态含义                           |
| -------- | ---------------------------------- |
| 0        | 无状态                             |
| 1        | 正在使用时长                       |
| 2        | 正在使用天卡                       |
| 3        | 已使用时长预定                     |
| 4        | 已使用天卡预定                     |
| 5        | 正在使用天卡，但是没有在使用自习室 |
| 6        | 天卡使用中，没有在使用，且有预定   |

### 自习桌表 - table_form
| 名字       | 类型    |
| ---------- | ------- |
| table_id🔑  | int     |
| is_reserve | tinyint |
| is_using   | tinyint |


    USE study;
    CREATE TABLE table_form
    (
        table_id int NOT NULL AUTO_INCREMENT,
        is_reserve tinyint,
        is_using tinyint,
        PRIMARY KEY(table_id)
    );


### 预定表 - reserve_form
| 名字           | 类型     |
| -------------- | -------- |
| reserve_id🔑    | int      |
| reserve_start  | datetime |
| reserve_end    | datetime |
| create_time    | datetime |
| openid         | varchar  |
| table_id       | int      |
| reserve_status | tinyint  |

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
| 状态代码 | 状态含义       |
| -------- | -------------- |
| 0        | 已完成         |
| 1        | 已过期         |
| 2        | 待确认（后台） |
| 3        | 正在使用       |
| 4        | 已确认未使用   |
| 5        | 被取消         |

### 会员充值记录表 - recharge_record_form   
| 名字                | 类型      |
| ------------------- | --------- |
| recharge_record_id🔑 | int       |
| wechat_pay_id       | char(100) |
| vip_daypass         | smallint  |
| vip_time            | int       |
| openid              | char(100) |
| create_time         | datetime  |

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
        isadmin tinyint,
        reserve_status tinyint,
        using_status tinyint,
        is_using_daypass tinyint,
        overdue_time datetime,
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

    # 每分钟处理过期预定，目前不更新桌子的借阅状态
    USE study;
    CREATE EVENT reserve_overdue ON SCHEDULE EVERY '1' MINUTE DO
    UPDATE reserve_form, user_form
    SET reserve_form.reserve_status = 1, user_form.user_status = 0 
    WHERE
    (reserve_start + MINUTE ( 30 ) < NOW() OR reserve_form.reserve_end < NOW())
    AND reserve_status = 4 AND reserve_form.openid = user_form.openid;

    # 下班时把天卡生效中的用户（5）改为0，并扣除天卡时间
    CREATE EVENT `study`.`consum_vip`
    ON SCHEDULE
    EVERY '1' DAY STARTS '2021/4/11 23:00:00'
    DO UPDATE user_form SET user_status = 0, vip_daypass = vip_daypass - 1 WHERE user_status = 5;


## Cookie
创建cookie : openid + session_key ==sha==> cookie  
验证cookie : 通过openid得到session_key,然后加密并验证

## TODO
- [x] 刷新用户信息
- [ ] 定期转移陈旧的数据，增加数据库的运行效率
- [ ] 建立索引
- [ ] 下班自动处理到时间的使用，修改订单状态、用户状态、用户vip时长（没必要，不做了）
- [x] 管理员给管理员权限
- [x] 管理员下机
- [ ] 天卡推荐座位，**其实前端有了数据可以自己推荐**
- [x] 时长卡推荐时间(searchTableByTime)
- [ ] **每天下班清理用户的天卡使用状态**
- [ ] 数字大小越界测试
- [x] 预定只能预定同一天
- [ ] 信用系统（迟到、取消），让前端先做一个吓吓人
- [x] 使用桌子
- [x] 结束使用桌子（如果正在使用天卡，user.status = 5）
- [ ] 点击太快了，休息一下
- [ ] 把useTableRequest改成子类
- [ ] https://developers.weixin.qq.com/community/develop/doc/0006ca988c85587908a9a88c05bc09?_at=1617962069342
- [ ] Response日志，把返回错误代码的部分改成返回Response.fail
- [ ] JWT认证，负载带cookie好了
- [ ] 修改新的MySql代码
- [ ] 时长卡到期清零（是否处理负数的情况）
- [ ] 时长卡有九十天的有效期，如果充值的时长大于剩余时长就刷新有效期，时长卡充值的时候，结束时间为九十天后的下班时间
- [ ] 修改cookie寿命
### 前端
- [x] 预定的时候判断VIP是否足够，时长、次卡
- [ ] 时长卡可能是负数
- [ ] 返回值msg的修改
### 问一下
- [x] 预定最短时间（半小时）
- [x] 上下班时间(8:00-10:00)
- [x] 最远预定时间(一周)
- [x] 使用自习室的时候能不能预定另一张桌子（使用天卡占领两张桌子）不行

## 笔记
### @Valid
@Valid修饰请求参数后，下一个参数必须是bindingResult，然后通过它来获取报错信息
### useTable逻辑
    订单开始时间是“现在”，结束时间 min(下班时间, VIP时间, 下一个别人的预定的开始时间），不需要用户输入
    1.预定了，现在要使用
    如果使用时间和预定时间相交，直接并一下，如果没有冲突就更新用户状态、之前的预定记录的状态。如果有冲突要么是还没到时间，要么是结束时间太晚了。
    如果使用时间和预定时间不相交，那只能是使用时间早于预定时间（预定会过期），报错“预定的时间还没到”，前端询问用户是否取消这个预定直接使用。那其实这也不需要后端判断了，前端就可以进行判断
    2.没有预定直接用。
    根据表单创建预定记录，没有冲突就写入，有冲突可能是searchTableSchedule获得的记录陈旧，导致提交的数据陈旧。

    前端获得预定结果后返回给用户
    时长卡和天卡有没有区别？
    除了user.status没有了吧
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
### 每天3点执行事件
    CREATE EVENT event_demo_insert
    on schedule EVERY 1 DAY STARTS date_add(date( ADDDATE(curdate(),1)),interval 3 hour)  
    do INSERT INTO demo value(CURRENT_TIMESTAMP);

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

### MySql定时任务
[教程链接][3]

### SpringBoot自带Slf4j打印Mybatis的SQL语句
aplication.yml:  

    mybatis:
        configuration:
            log-impl: org.apache.ibatis.logging.stdout.StdOutImpl # 设置打印sql语句





[1]: https://blog.csdn.net/peng86788/article/details/80534086
[2]: https://zhuanlan.zhihu.com/p/49996147
[3]: https://www.cnblogs.com/javahr/p/9664203.html