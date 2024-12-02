# MyBatic FAQ

## What's the basic steps to use MyBatis in Java

1. 创建实体类 Entity class

例如:

```java
public class Task {

    private String id;
    private String name;
    private int priority;
}
```

2. 创建接口文件

```java
public interface TaskMapper {

    Task getTaskById(String id);
}

```

3. 创建映射文件

```xml
<mapper namespace="com.fanyamin.jwhat.mapper.TaskMapper">
  <select id="getTaskById" parameterType="java.lang.String" resultType="com.fanyamin.jwhat.Task">
    select * from task where id = #{id}
  </select>
</mpper>
```