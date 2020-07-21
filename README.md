# SCUTStudentScoreControllerAndroid

This is the android app that work with the SCUTStudentScoreControllerWeb service. I will show you how to use Android Studio to develop your own app and introduce a lot of skills in using Kotlin. I will use Kotlin and a few Java codes to implement this work.

If you would like to know more, please follow the account "KevinC_" in www.bilibili.com, I offered all the video instructions there.

```
https://space.bilibili.com/38666989
```

GitHub repositories for Web Service: 

```
https://github.com/czf0613/SCUTStudentScoreControllerWeb.git
```

GitHub repository for Android Front End:

```
https://github.com/czf0613/SCUTStudentScoreControllerAndroid.git
```



Outline:

[TOC]



## Environment and tools

Unlike the old version SQL server, we apply the modern data base to our project, here are the tools and environments we used.

### 1, MySQL server

MySQL is a famous open source data base, because of its convenience and for-free, a lot of companies and personal websites are using MySQL, also, MySQL has a lot of good characteristics like 64-bit timestamp, binary large objects and native JSON support. To use those modern characteristics, we need MySQL which is newer than MySQL 5.7 or later (8.0 is recommended). You can download it via the official website. Reminded that you should remember the account name and password for it, or you are not allowed to access the data base.

### 2, JDK 11 or later

I used JDK 11 to setup the project because it is in LTS so you have less probability to compatibility problems. Do not use Java 8 or the latest JDK unless necessary.

### 3, IntelliJ IDEA and Android Studio

They are the IDEs we are going to use, be careful that we must use IDEA ultimate version instead of IDEA community version.

### 4, Navicat Premium

This is the toolkit for us to manage the data base with GUI, but this tool is not for free, if you cannot afford it, just ignore it (It's a little bit expensive).

<img src="https://i.loli.net/2020/07/20/HC1BQwVWTScP8pl.png" alt="image-20200716112319188" style="zoom:50%;" />

### To learn more, see this:

```
https://www.bilibili.com/video/BV1mv411B77g
```



## Why we don't use C# together with SQL Server?

### 1, C# has poor support for the types in Data Base.

For example, time type, JSON type and blob type can not be supported by C# (if you really want to get it, you need to use .Net instead of native C#), and even use byte array (byte[ ], excuse me, would you like to use this one in your program?) to deal with corresponding data type. WinForm and WPF technologies for building window programs are not used for database operation in any case, so in fact, this work should be handed over to the really suitable language. Therefore, my idea is that, with my ability, I can hand in two kinds of assignments: one is java web project, which is the real main battlefield of database; the other is android app, which uses SQLite database in android app, which is a very stable operation and not difficult to learn.

### 2, Lack of the  concept of connection pool.

To do database business development, in fact, there is a very important concept that has not been covered in the class. This is database connection. Real database access is often not a single thread, so how to schedule multiple tasks with multiple threads requires a connection pool to help us manage (or you can also use singleton instance & synchronized methods, but these design patterns are estimated to be understood by no other students in our class). It can reduce the waste of unnecessary resources and recycle zombie threads in time. However, native C# doesn't have these things, so the students will end up with: every time we operate the database, we will open a database connection, then execute SQL, and finally close the connection. This is actually a huge misunderstanding of the use of the database. Although most of the students may have searched for information, or wrote code like this unintentionally, it is not very good to operate in this way.

### 3, The last one is actually due to my selfishness.

Because my commercial development mainly uses MySQL and non relational databases, SQL server is not free of charge, and the exclusive relationship with .Net is too strong, which makes it almost difficult for me who use MySQL to integrate C# and MySQL (this involves the tricky operation of using byte array to do things. Unless there is ORM, the mapping rules below can really write about to death.)



# Route to go

## 1, set up a proper data base model for our project

Just analyze what kinds of requirements we are going to meet: login, user information together with edition, course management, score management, and the overall administer who can edit any data in the app.

So we get these 5 individual entities: student, teacher, administer, courses and scores. That means we have to create at least 5 tables matching these 5 entities. But, there could be some redundancy in the former 3 tables: student, teacher and administer. Why? Because they have to login, so these three tables must contains the same attributes like user_name and password, which are not necessary. So we have to do a partition here that is to extract user_name and password into another table called "user".

### Let's consider the relationships between these tables:

#### 1, student_id referenced by "user"

#### 2, teacher_id referenced by "user"

#### 3, administer_id referenced by "user"

#### 4, teacher_id and student_id referenced by "courses"

To search the course detail quickly, it must contain information like this: who is teaching this course, and who is having this course

#### 5, course_id referenced by "student"

In return, every student must know what courses he is taking. Although it is sort of redundant of the relation mentioned in label 4

#### 6, course_id referenced by "teacher"

Like label 5

#### 7, student_id and course_id referenced by "score"

Student_id together with course_id is a key for the table score since it can uniquely identify each row.



But, things got complicated because label 4, 5, 6, 7 are many to one or many to many mappings. How to deal with these complex relation ships? I offered two solutions, but you have to think over carefully to identify which one to use.

<img src="https://i.loli.net/2020/07/20/1nGDmoOd4hkFzxe.png" alt="未标题-4" style="zoom:50%;" />

### How to deal with many to one or many to many mappings?

#### 1, store it by serialization

I have already mentioned json before. So we can use json to solve this problem easily, for example, if we need to store what courses does a student select, we can use a list of integer that contains course ids. It seems that it's convenient, but it still have a lot of problems: editing data becomes difficult because you have to deserialize at first and then edit the list then put it back to database with serialization. So generally, strengths and weaknesses are listed here:

##### advantages:

###### 1, do not need an extra table

###### 2, save a lot of space since it contains less redundancy, especially when the table is large

###### 3, even if you don't have index, searching is still fast



##### disadvantages:

###### 1, editing becomes very difficult and complex

###### 2, if the user keep editing regardless of the completeness of the former deserialize and serialize procedure, an concurrent safety issue occurs

###### 3, unable to do statistics like sum, avg because you need deserialization each time, the efficient is poor



#### 2, use an extra table to store

This is the general solution for this case, but it has both strengths and weaknesses:

##### advantages:

###### 1, easy to maintain

###### 2, no serious concurrent safety issue

###### 3, could be able to do statistic like sum, avg and stuff like that



##### disadvantages:

###### 1, takes up a lot of space since the maximum could be up to n*m

###### 2, if the table is very large, it's hard to find out what courses does a certain student has already picked up 



So in the end, I chose strategy 1 to store course for each student and teacher, and I chose strategy 2 to maintain the score table. The SQL command to build the table is in the script "score.sql".



### The last thing, charset

Unlike SQL Server, MySQL fully support UTF-8 and other charsets. To avoid problem like "锟斤拷" and "烫烫烫", you'd better configure charset explicitly. As I know, SQL Server is using GBK2312 by default, so does the C# for Windows. If you apply these settings to other system, it would be a bunch of mess. In short, UTF-8 is the best choice for international, do not use other charset unless necessary.

finally:

```mysql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
CREATE TABLE `admin`  (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `command_level` int(8) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for course
-- ----------------------------
CREATE TABLE `course`  (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `course_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `teachers` json NULL,
  `credit` decimal(4, 2) NOT NULL,
  `grade` json NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `students` json NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for score
-- ----------------------------
CREATE TABLE `score`  (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `student_id` int(8) NOT NULL,
  `course_id` int(8) NOT NULL,
  `score` decimal(6, 3) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for student
-- ----------------------------
CREATE TABLE `student`  (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `gender` int(5) NOT NULL,
  `grade` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `major` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `clazz` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `birthday` date NULL DEFAULT NULL,
  `course` json NULL,
  `enrollment_time` date NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
CREATE TABLE `teacher`  (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `gender` int(5) NOT NULL,
  `major` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `course` json NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
CREATE TABLE `user`  (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `flag` int(5) NOT NULL,
  `account` int(8) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
```



### To know more, follow these links:

```
https://www.bilibili.com/video/BV1hp4y1U7Eb
https://www.bilibili.com/video/BV1VK411H7WH
https://www.bilibili.com/video/BV1Wp4y1S762
```



## 2, implement our Java Web Service

At first, we need to introduce a strong toolkit that helps us communicate with the data base engine. That is ORM framework. Generally, relational data base offered their query result with tuples, which is like the tuples used in Python. Using tuples in programming is not convenient because the only way to access the data is by index, and it would also cause a lot of problem if you modify the structure of your table, which means that you have to edit all the places once you make a little changes.

Let's consider, if we treat those attributes in the table as private members in a class, that means a row in database is right just an object matching this entity class, isn't it? So we use this strategy to build up a lot of entity classes matching tables and provide a lot of Data Access Objects to help us execute the query commands and map the result to the objects we want. just like this:

![image-20200716114354908](https://i.loli.net/2020/07/20/xnT6XO7UCMcp3zl.png)

### 1, introduce Entity Class

Take the student class as an example: that's easy, just convert them all into the class members with proper data types

```java
@Entity
@Table(name = "student",schema = "dealate")
@TypeDef(name="json",typeClass = JsonStringType.class)
public class Student {
    private Integer id;
    private String name;
    private int gender;
    private String grade;
    private String major;
    private String clazz;
    private Date birthday;
    private List<Integer> course;
    private Date enrollmentTime;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "gender", nullable = false)
    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Basic
    @Column(name = "grade", nullable = false, length = 50)
    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Basic
    @Column(name = "major", nullable = false, length = 50)
    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    @Basic
    @Column(name = "clazz", nullable = false, length = 50)
    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @Basic
    @Column(name = "birthday", nullable = true)
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Basic
    @Column(name = "course", nullable = true,columnDefinition = "json")
    @Type(type = "json")
    public List<Integer> getCourse() {
        if(course==null)
            course=new ArrayList<>();
        return course;
    }

    public void setCourse(List<Integer> course) {
        this.course = course;
    }

    @Basic
    @Column(name = "enrollment_time", nullable = false)
    public Date getEnrollmentTime() {
        return enrollmentTime;
    }
}
```



### 2, how about DAO?

DAO (Data Access Object) helps you generate a lot of query commands with template, just like, findById, findAllByUserName. You don't have to write down the actual query command but the template will help you, so it's really convenient.

```java
public interface StudentDAO extends JpaRepository<Student,Integer> {
    Student findById(int id);
    boolean existsById(int id);

    @Transactional
    void deleteById(int id);
}
```

But the functions offered by DAO is limited, you can not finish complex query commands with the template, so you also need to write some SQL commands instead, just like this:

```java
public interface ScoreDAO extends JpaRepository<Score, Integer> {
    @Query("select new com.czf.server.beans.CourseScore(u.courseId,u.score) from Score u where u.studentId= :stuId")
    List<CourseScore> sumByStuId(@Param("stuId")int id);

    @Query("select new com.czf.server.beans.CourseScore(u.courseId,u.score) from Score u where u.studentId= :stuId and u.courseId in :courses")
    List<CourseScore> sumByStuIdAndCourseIn(@Param("stuId")int id,@Param("courses")List<Integer> courses);

    @Query("select new com.czf.server.beans.CourseScoreInDouble(u.courseId,avg(u.score)) from Score u where u.courseId in :courses group by u.courseId")
    List<CourseScoreInDouble> avg(@Param("courses")List<Integer> courses);

    Score findByCourseIdAndStudentId(int courseId,int stuId);

    @Transactional
    void deleteAllByCourseId(int id);

    boolean existsByCourseIdAndStudentId(int courseId,int stuId);
}
```

Those SQL commands look strange, how can you write SQL command with "new"? That's true, because this is not native SQL, they are JPQL (Java Persistence Query Language), it is similar to SQL since you can just like it as SQL with OOP characteristics.



### 3, so, how to use them in Java?

That's easy, bind an object with @Autowired annotation, then you can execute all the functions created in the DAO interface, you don't need to implement those interfaces because Hibernate Engine help you implement them behind.

```java
@Autowired
private StudentDAO studentDAO;
    
public boolean checkId(int id) {
     return studentDAO.existsById(id);
}
```



### 4, complete the whole project with Java code

Although it's the main part of this project, but it's not related to Database Course, so if you need instructions, please follow:

```
https://space.bilibili.com/38666989/channel/detail?cid=137475
```

I have prepared all the stuff in detail:

![image-20200716121252834](https://i.loli.net/2020/07/20/npXrFYvyqxBt6Sz.png)





## 3, the Android front-end

This part is totally not related to Database, so if you would like to know more, please go:

```
https://space.bilibili.com/38666989/channel/detail?cid=137476
https://space.bilibili.com/38666989/channel/detail?cid=138765
```

I will show you how to use a marvelous language called Kotlin to build an Android App.

![image-20200716121641022](https://i.loli.net/2020/07/20/JyOz96pjrFDXokx.png)

![image-20200716121717402](https://i.loli.net/2020/07/20/e5tgRE23JnNQIS8.png)



# Special Techniques

This project is designed with high standard so you have to deal with something that may occur in enterprise developing. Actually, enterprise developing is very complicated and it requires a lot of base knowledges. So I only introduced two cases this time, they are: concurrent control (pessimistic lock implementation), debounce&throttle functions.

## 1, concurrent control

Let's assume that, we are using the register-account function, but at a special moment, Student1 and Student2 enter the same user name to register, so what will the computer react to this situation, which one will successfully register his account? However, using database transaction is not completely OK for this case since user name is not the primary key in the user table, it may turn out to be two possible consequences: 1, phantom reads (REPEATABLE READ ERROR in database transactions); 2, long-term table lock (because user name is not primary key, so it triggers full table scan at serious pessimistic lock)

我用中文说一下，估计有点难看懂……在user表中，用户名并不是主键，主键是一个自增长整数。那么也就意味着，它并不能使用行(háng)锁，必须使用表锁。(除非给user name加一个索引，那么可以解决这个问题)那么在插入时可能会引起两种错误：1，如果事务的隔离等级不够高的话，可能会引起幻读错误，但是在MySQL中，仅仅只有“顺序执行”的隔离等级可以防止幻读，但是性能就非常低下了。 2，为了防止重复插入相同用户名的人，在插入时必须发生表锁，也就是该表禁止写入，但可以读取，然而我就为了插入一个人，锁住所有的人，这个性能显然也是非常糟糕的。这也就是为啥在这里并不适合使用数据库事务来完成这个任务。

Since it can not perfectly solved in database, we can solve it in Java code. We can use synchronized method to make sure that only one thread is trying to edit the user table, so it does not need table lock, which greatly enhance the performance. But this solution is very simple, if the scale of table is large enough, other techniques should be used, but I cannot cover it here.

look at this:

```java
public synchronized boolean register(String userName, String password, Student student){
    try{
        int id=studentDAO.save(student).getId();

        User user=new User();
        user.setUserName(userName);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setFlag(1);
        user.setAccount(id);
        userDAO.save(user);
        return true;
    }
    catch (Exception e){
        return false;
    }
}
```

The "synchronized" word make sure that only one thread is editing. Although it's not a perfect solution, we can use something like Message Queue and other stuff to solve it.



## 2, debounce & throttle

Let's consider another situation, if I want to set up a dynamic input box which can react to what you input every time you input some characters, what would you do? Just like the Baidu website, you don't need to input Enter or click the button, the website will display the message matching your text automatically.

The most common idea is to set up a text watcher that receive the changes from the input box, if you input a character, it triggers once. So, if I input 123456789, how many times does the text listener triggers? It's 9, but is it necessary to trigger it 9 times? Of course not! Since your texting duration is very fast, I mean you spend less than 1 second to input 123456789, so the data base will receive a lot of requests in a short time and it could even block the service since a lot of request are enqueued.

How to avoid this problem and save the usage of requesting? Now I used a special method named coroutine to achieve this goal. Coroutine is a light-thread that can run with low consumption and it can be canceled easily. By the way, coroutines have a good feature that is, it's non-blocking. Let's see how I do this in Kotlin:

```kotlin
private var refreshJob:Job?=null

studentIdEditText.addTextChangedListener(object :TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            refreshJob?.cancel()
                
            refreshJob=lifecycleScope.launch {
                delay(300)
                val id=studentIdEditText.text.toString().toInt()

                launch { studentName.text="${NetWork.getStudentNameWithId(id)}学生的成绩：" }
                    
                launch {        	      
                    scoreAdapter = ScoreAdapter(ArrayList(NetWork.getSingleStudentScore(LocalPreferences.getInt("id") ?: 1, id)))
                    scoreListView.adapter=scoreAdapter
                }
            }
        }
})
```

in this way, we make the debounce function with the help of coroutines.

![img](https://i.loli.net/2020/07/20/Z9zTP43fwIsRV5k.png)

To see more:

```
https://blog.csdn.net/hupian1989/article/details/80920324
https://www.bilibili.com/video/BV1Ht4y1Q74L
```



# How to use this app?

## 1, deploy background services

### get jar file here:

```
https://github.com/czf0613/SCUTStudentScoreControllerWeb/releases/tag/1.0
```

If you do not have a server with public IP, please make sure that your phone and your computer is in the same LAN (local area network) that starts with 192.168.x.x, you can check it out in your task manager:

<img src="https://i.loli.net/2020/07/20/JYeghmyfI8naOlK.png" alt="微信截图_20200716142657" style="zoom:50%;" />

### Then open the console, execute the command java -jar xxxx.jar(xxxx is the name of the server jar package)

![image-20200716143115079](https://i.loli.net/2020/07/20/nJHq8i3Sxg6fYQr.png)

then you can see the server is running with logs:

![image-20200716143244159](https://i.loli.net/2020/07/20/1m3V2rU8ZOQjpuz.png)

if you are in troubles, please check:

1, wrong java version, the requirement is JDK 11 or later

2, check if there's any service that is occupying port 23333

3, may be the firewall reject your connection to my database in Aliyun

if you cannot solve it, use the url: http://106.54.9.186:23333, it's my private cloud server.



## 2, install Android APK

make sure that your phone is running Android 6.0 64-bit or later, or you will receive some problems in compatibility.

### get APK here:

```
https://github.com/czf0613/SCUTStudentScoreControllerAndroid/releases/tag/1.0
```

### 1, configure the server url, click the button on the right.

<img src="https://i.loli.net/2020/07/20/HcJolmQDI9rvwXx.png" alt="image-20200716144733782" style="zoom:50%;" />

the url has regex type checking, you must input the string that starts with http or https and end with the port number, for example, http://192.168.0.1:23333 or http://www.baidu.com:80



### 2, login or register

I have already register several accounts to test, they are:

| user_id | user_name | password  | type       |
| ------- | --------- | --------- | ---------- |
| 8       | czf       | 123456789 | student    |
| 9       | zy        | 123456789 | student    |
| 1       | teacher1  | 123456789 | teacher    |
| 2       | teacher2  | 123456789 | teacher    |
| 1       | root      | 123456789 | administer |

Be reminded that, one course could have multiple teachers.

| course_id | course_name    | credit | teachers |
| --------- | -------------- | ------ | -------- |
| 1         | 数据库         | 2.0    | 1        |
| 2         | 算法           | 2.0    | 1, 2     |
| 3         | 计算机组成原理 | 4.0    | 2        |
| 4         | 人工智能       | 2.0    | 2        |



### 3, student interface

student functions are easy. Be reminded that, one course could have multiple teachers.

#### 1,student information

<img src="https://i.loli.net/2020/07/20/sxtMST6bVaQcYnU.png" alt="image-20200716145634574" style="zoom:50%;" />

#### 2, searching course informations

<img src="https://i.loli.net/2020/07/20/7fOxzsreIAgv4MK.png" alt="image-20200716145804101" style="zoom:50%;" />

#### 3, selecting course

you can press plus to choose this course, and also you can press minus to remove this course. If you click fast and trigger twice, don't worry, I have already done the concurrent control, you data is safe, you won't select it twice.

<img src="https://i.loli.net/2020/07/20/VIj9TAsFzN6diC8.png" alt="image-20200716150131211" style="zoom:50%;" />

#### 4, searching your score

input course_id or course_name then the filter will help you.

<img src="https://i.loli.net/2020/07/20/ILbpOVuH96TSFYd.png" alt="image-20200716150255438" style="zoom:50%;" />



### 4, teacher interface

different teacher will receive different data, so you need to remind the role control.

#### 1, self information

<img src="https://i.loli.net/2020/07/20/w6xF4KnTPkVl8eX.png" alt="image-20200716150419558" style="zoom:50%;" />

#### 2, input score

you need to input student id at first, for example, student 8 is czf. Then the interface would refresh. Be reminded that you can only see the scores from the course you are teaching, for example, teacher1 is teaching 算法 and 数据库, so she can only receive data from these courses:

<img src="https://i.loli.net/2020/07/20/aky3ZWXwlmb4vhH.png" alt="image-20200716151521749" style="zoom:50%;" />

#### 3, average score

Like interface 2, you can only receive data from those courses your are teaching. Also, it has a filter above.

<img src="https://i.loli.net/2020/07/20/G4xZhomCcw5vYQW.png" alt="image-20200716151651694" style="zoom:50%;" />

#### 4, edit score

input student_id and press the button to search, press the score digit, then you can edit the score.

<img src="https://i.loli.net/2020/07/20/bEGJuqi8Xh7AjaU.png" alt="image-20200716151836421" style="zoom:50%;" />

<img src="https://i.loli.net/2020/07/20/im1bw2uQJn3dWCU.png" alt="image-20200716151902813" style="zoom:50%;" />

<img src="https://i.loli.net/2020/07/20/8vhqsYt7rzXIEgu.png" alt="image-20200716151918918" style="zoom:50%;" />

then the score of Database course is 75.



### 5, administer interface

it's more complex since he can manage all the data.

#### 1, modify any information

select the spinner then you can choose the information to show, then input the id.

<img src="https://i.loli.net/2020/07/20/XtL9zwIlh6xeOR8.png" alt="image-20200716152351014" style="zoom:50%;" />

you can reset student or teacher password or you can modify their information by clicking "确定" button. If you do not click "确定", the edition will not be saved.

#### 2, search&modify scores

this is the over view checker, you can see all scores from a students, or you can see all the average scores of any teacher

<img src="https://i.loli.net/2020/07/20/SITc2w5mfBMZ8Jv.png" alt="image-20200716152701900" style="zoom:50%;" />

<img src="https://i.loli.net/2020/07/20/EB4de6ZAQG8DmCg.png" alt="image-20200716152720060" style="zoom:50%;" />

#### 3, course selecting

you can see course selected by one student and you can modify their selections

<img src="https://i.loli.net/2020/07/20/heZ8Tft5Insb26X.png" alt="image-20200716152833529" style="zoom:50%;" />

also, you can see the courses offered by a certain teacher:

<img src="https://i.loli.net/2020/07/20/DbHuLJcAqxjnVQR.png" alt="image-20200716152932553" style="zoom:50%;" />

#### 4, add a course

<img src="https://i.loli.net/2020/07/20/eMfCUhd26KrEajZ.png" alt="image-20200716153017655" style="zoom:50%;" />



That's all about how to use in the android app.



# Conclusion:

This is a great project that meet the high-end standard in enterprise developing. I hope these techniques I am using could help you gain more knowledge in how to use database correctly. Thank you for watching this big deal. Thank you so much.
