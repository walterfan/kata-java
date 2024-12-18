# Spring Boot Tips

## @SpringBootTest 的用法


## @RestControllerAdvice 的用法

它是一个用于在 Spring Boot 应用程序中统一处理异常和增强 @RestController 的功能的注解。它结合了 @ControllerAdvice 和 @ResponseBody，使得它能自动地将返回的对象序列化为 JSON 或其他格式并响应给客户端。

1. 基本用法

@RestControllerAdvice 主要用于捕获和处理控制器中的异常，并提供全局的异常处理逻辑。它适用于所有的 @RestController（或 @Controller，如果使用了 @ResponseBody 的话）中定义的处理方法。

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理特定异常类型
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>("Resource not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // 处理通用异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

1. 主要功能

  * 捕获和处理异常：使用 @ExceptionHandler 方法来处理指定类型的异常。
  * 全局处理：与 @ControllerAdvice 类似，@RestControllerAdvice 能够捕获所有 @RestController 中未被单独处理的异常。
  * 自动返回 JSON：因为 @RestControllerAdvice 等同于 @ControllerAdvice + @ResponseBody，返回的数据会自动被序列化为 JSON 格式，适合用于 RESTful API。

2. 配合其他注解使用

@RestControllerAdvice 可以结合其他注解来扩展其功能：

  * @ExceptionHandler：指定要处理的异常类型。
  * @ModelAttribute：在每个控制器方法调用之前执行的方法。
  * @InitBinder：用于初始化 WebDataBinder，进行数据绑定。

4. 示例：处理不同异常的返回格式

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理自定义异常
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 处理所有其他异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "An unexpected error occurred.");
        response.put("details", ex.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```
5. 使用场景

  * 集中异常处理：对于一个大型项目，当有多个 @RestController 需要处理类似的异常时，使用 @RestControllerAdvice 可以统一管理异常处理逻辑，避免在每个控制器中重复编写异常处理代码。
  * 日志记录：可以在异常处理方法中添加日志记录，方便调试和运维。
  * 响应格式一致性：保证异常处理返回的格式一致，提升 API 的可维护性和易用性。

总结

@RestControllerAdvice 是用于处理 @RestController 中异常的全局注解。它不仅能提高代码的复用性和可维护性，还能保证异常处理的一致性和统一响应格式。