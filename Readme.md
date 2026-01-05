管理员 用户名：admin

​		密码：123456

医生    用户名：**xxgnk_1**

​	    密码：123456

患者     用户名：patient001

​		密码：123456

解释类的功能

Result.java
作用说明  
Result<T> 是一个“统一接口响应”对象。所有 Controller 方法都可以返回 Result，而不必每次手写 code / message / data。  
字段含义  
• code   – 状态码（约定 200 成功，其余为错误）  
• message – 提示信息（“操作成功 / 失败原因”）  
• data   – 真正业务数据，类型不固定  
• timestamp – 返回时间戳，客户端可做缓存、日志  

类内提供了一组静态工厂方法，便于快速生成响应：  
```
Result.success()                  // 只表示成功
Result.success(data)              // 成功并带数据
Result.success("自定义", data)     // 自定义提示
Result.failed("错误信息")          // 失败
Result.failed(code,"错误信息")     // 自定义错误码
```

什么是泛型 <T>  
Java 的泛型（Generic）让类或方法把“数据类型”当成参数来写。  
• 这里的 <T> 表示“任意类型”。  
• 当你调用 `Result.success(List<User>)` 时，编译器会把 T 推断为 `List<User>`；  
  调用 `Result.failed()` 时 T 可推断为 `Void` 或不关心。  
好处：  
1. type-safe 编译期检查类型，不需要强转。  
2. 代码复用   一个 Result 类即可返回任何类型的数据，而不用写多个类。


ResultCode.java
ResultCode 是“统一状态码枚举”。  
它把系统常见的返回状态集中成若干常量，供 Result 对象或业务代码引用，避免魔法数字，提升可读性。

枚举成员  
• SUCCESS(200, "操作成功")  
• FAILED(500, "操作失败")  
• PARAM_ERROR(400, "参数错误")  
• NOT_FOUND(404, "资源不存在")  
• UNAUTHORIZED(401, "未登录")  
• FORBIDDEN(403, "无权限")  
• USERNAME_EXISTS(1001, "用户名已存在")  
• USER_NOT_FOUND(1002, "用户不存在")  
• PASSWORD_ERROR(1003, "密码错误")  
• ACCOUNT_DISABLED(1004, "账号已被禁用")

每个枚举值持有两项数据：  
int code   —— 状态码  
String message —— 默认提示信息

典型用法  
```java
return Result.failed(ResultCode.PARAM_ERROR);
```
等价于  
```java
return Result.failed(400, "参数错误");
```
这样做的好处  
1. 统一管理：所有状态码/消息集中在一个枚举里。  
2. 避免硬编码：业务层不直接写数字 200、500。  
3. 类型安全：只能使用定义好的枚举常量，防止写错。

1. @Component 注解  
• 这是 Spring 的通用组件注解。  
• 把 CustomAuthFailureHandler 声明为一个 Bean，启动时会被 Spring 自动扫描并注册到 IoC 容器。  
• 这样 Spring-Security 在配置 `failureHandler(new CustomAuthFailureHandler())` 时可以直接注入，而无需手动 new。

2. isAjax 这一行含义  
目的是判断当前登录请求是否是“前后端分离的 Ajax/Json 请求”，从而决定返回 JSON 还是重定向页面。

拆解：  
```java
boolean isAjax =
    "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
 // 借助前端通常会在 ajax 请求头加 X-Requested-With=XMLHttpRequest
 ||
    Optional.ofNullable(request.getHeader("Accept")) // 取 Accept 头
            .map(h -> h.contains("application/json")) // 是否声明想接收 json
            .orElse(false);                           // 为空默认 false
```

• 第一部分 `"XMLHttpRequest".equals(...)`  
  ‑ 传统 jQuery、Axios 会自动带上 `X-Requested-With: XMLHttpRequest`，可作为 Ajax 标志。

• 第二部分  
  ‑ 某些前端只会设置 `Accept: application/json`，不带 X-Requested-With，所以再检测一次 Accept 头是否包含 `application/json`。

合起来：  
isAjax = true  ⇒ 返回 HTTP 401 + JSON  
isAjax = false ⇒ 浏览器表单登录，重定向 `/login?error`，提高用户体验。



### @Override

- 表明子类中的方法重写了父类中的方法或者实现了接口中的方法。
- 它主要是为了增强代码的可读性和可靠性。

### @RestController

表示该类是一个RESTful控制器

这是一个组合注解，相当于 `@Controller` 和 `@ResponseBody` 的合集。它用于标记一个类为RESTful控制器，意味着这个类中的所有方法都会返回一个对象，而不是视图。Spring会自动将返回的对象转换为JSON或XML格式的响应体。

## `@Component`

`@Component` 是一个注解，它允许 Spring 自动检测自定义 Bean。

换句话说，无需编写任何明确的代码，Spring 就能做到：

- 扫描应用，查找注解为 `@Component` 的类
- 将它们实例化，并注入任何指定的依赖
- 在需要的地方注入





CustomAuthSuccessHandler.java

这个类 `CustomAuthSuccessHandler` 是 Spring Security 的**登录成功处理器**，实现了 `AuthenticationSuccessHandler` 接口。

## 1）它在系统里做什么？
当用户通过 Spring Security 的表单登录（或 Security 认证流程）**登录成功**后，Spring Security 会回调：

```java
onAuthenticationSuccess(request, response, authentication)
```

你在这里可以自定义“登录成功后跳转哪里 / 返回什么内容”。

本类的目标：**根据角色跳转到不同首页**。

---

## 2）@Component 的作用
和你前面看到的一样：  
`@Component` 表示把这个类注册为 Spring Bean，让 Spring Security 配置可以自动注入使用。

---

## 3）核心参数解释
方法签名：

- `HttpServletRequest request`：本次请求信息（URL、Header、参数等）
- `HttpServletResponse response`：你可以在这里返回重定向、写 JSON、设置 Cookie
- `Authentication authentication`：认证成功后的身份对象，里面包含：
  - 用户名（principal）
  - 权限/角色（authorities）
  - 是否认证成功等

---

## 4）角色判断逻辑是什么意思？
```java
boolean isDoctor = authentication.getAuthorities().stream()
        .anyMatch(a -> "ROLE_DOCTOR".equals(a.getAuthority()));
boolean isAdmin = authentication.getAuthorities().stream()
        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
```

解释：
- `authentication.getAuthorities()` 拿到当前用户拥有的权限集合（通常是角色 ROLE_xxx）。
- `.stream().anyMatch(...)` 表示：**只要集合里有一个满足条件就返回 true**。
- `a.getAuthority()` 就是某个权限字符串，例如 `"ROLE_DOCTOR"`。

所以：
- `isDoctor == true` 表示该用户拥有医生角色
- `isAdmin == true` 表示该用户拥有管理员角色
- 两者都 false 就默认当患者处理

---

## 5）跳转分支逻辑
```java
if (isDoctor) {
    response.sendRedirect("/doctor/workspace");
} else if (isAdmin) {
    response.sendRedirect("/admin/dashboard");
} else {
    response.sendRedirect("/patient/home");
}
```

含义：
- 医生 → 跳转到医生工作台
- 管理员 → 跳转到管理员控制台
- 否则 → 跳转到患者主页

`response.sendRedirect(url)` 是返回一个 302 重定向，让浏览器自动跳转到新的页面地址。

---

## 6）补充：它适用于哪种登录方式？
这个 SuccessHandler **主要对应 Spring Security 的“表单登录流程”**（即由 Spring Security 接管的登录）。  
你们项目里还有 `/api/auth/login` 这种自己写的登录接口（返回 JSON + token），那条链路一般不会走这个 handler。



DashScopeConfig.java

这个 `DashScopeConfig` 是 Spring Boot 的配置类，用于读取和管理阿里云 DashScope（通义千问）AI 服务的配置信息。

## 1. 整体作用
从 `application.properties` 读取 AI 服务相关配置（API Key、模型名、基础 URL），并提供给其他服务类使用。

---

## 2. 注解解释

### `@Configuration`
- 标记为 Spring 配置类，会被 Spring 扫描并管理
- 通常与 `@ConfigurationProperties` 配合使用

### `@ConfigurationProperties(prefix = "ai.dashscope")`
- 自动绑定配置文件中以 `ai.dashscope` 开头的属性
- 例如，`application.properties` 中可能有：
```properties
ai.dashscope.api-key=sk-xxxxx
ai.dashscope.model=qwen-vl-plus
ai.dashscope.basee-url=https://dashscope.aliyuncs.com
```
Spring 会自动将这些值注入到类的字段中。

---

## 3. 字段说明

```java
private String apiKey;           // API密钥，用于身份验证
private String model="qwen-vl-plus";  // AI模型名称，默认值
private String baseeUrl;         // API基础URL（注意：这里拼写是 baseeUrl，可能是 typo，应该是 baseUrl）
```

注意：`baseeUrl` 可能是拼写错误，通常应该是 `baseUrl`。

---

## 4. 方法说明

### `getAuthorizationHeader()`
```java
public String getAuthorizationHeader() {
    return "Bearer " + apiKey;
}
```
作用：生成 HTTP 请求的 Authorization 头，格式为 `Bearer {apiKey}`。  
用途：调用 DashScope API 时需要在请求头中携带此认证信息。

### Getter/Setter 方法
- `getApiKey()` / `setApiKey()`：获取/设置 API 密钥
- `getModel()` / `setModel()`：获取/设置模型名称
- `getBaseeUrl()` / `setBaseeUrl()`：获取/设置基础 URL

---

## 5. 使用场景示例
在其他服务类（如 `QwenVisionService`）中，可以这样注入使用：

```java
@Service
public class QwenVisionService {
    @Autowired
    private DashScopeConfig dashScopeConfig;
    
    public void callAI() {
        String authHeader = dashScopeConfig.getAuthorizationHeader();
        String model = dashScopeConfig.getModel();
        String baseUrl = dashScopeConfig.getBaseeUrl();
        
        // 使用这些配置调用 AI API
        // ...
    }
}
```

---

## 6. 注意事项
1. 第 6 行注释掉的 `@Data`：如果使用 Lombok，可以用 `@Data` 自动生成 getter/setter，但这里手动写了。
2. `baseeUrl` 拼写：建议改为 `baseUrl` 以保持一致性。
3. 配置绑定：确保 `application.properties` 中的属性名与字段名匹配（Spring 会自动处理驼峰和下划线转换）。

---

## 总结
这是一个配置绑定类，将外部配置（`application.properties`）中的 AI 服务参数映射到 Java 对象，并提供便捷方法（如生成认证头）供业务代码使用。这是 Spring Boot 中管理外部配置的常见做法。



DoctorDataSeeder.java

这段 `DoctorDataSeeder` 的代码是一个**启动数据填充器**，在 Spring Boot 项目启动完成后自动执行，用来确保「医生表」里为每个科室准备至少 3 名医生。下面按模块逐行解释其含义与作用。

──────────────────────────────────────────────
1. 类定义与注解  
• `@Component`  
    把该类注册为 Spring Bean。  
• `implements CommandLineRunner`  
    表示当 Spring Boot 完全启动后，会自动调用 `run(String... args)` 方法。  
    典型用途：初始化数据、执行脚本、检查环境等。

2. Logger  
```java
private static final Logger log = LoggerFactory.getLogger(DoctorDataSeeder.class);
```
使用 SLF4J 提供日志功能，方便在启动过程中打印信息。

3. 默认密码  
```java
private static final String DEFAULT_PWD = new BCryptPasswordEncoder().encode("123456");
```
• 使用 BCrypt 算法把明文 `123456` 加密成哈希，生成医生初始密码。  
• BCrypt 具备随机盐，每次生成值不同，安全性高。

4. 随机数据字典  
```java
SURNAMES, GIVEN, TITLES
```
• `SURNAMES` ：常见姓氏  
• `GIVEN`    ：常见名  
• `TITLES`   ：医生职称  
后续随机组合生成 `realName` 与 `title`。

5. 依赖注入  
```java
@Autowired DoctorMapper doctorMapper;
@Autowired DepartmentMapper departmentMapper;
```
• 这两个 Mapper 分别操作 `doctor`、`department` 表；依赖 MyBatis／MyBatis-Plus。

6. run(...) 逻辑  
```java
public void run(String... args)
```
启动后依次执行以下步骤：

⑴ 判断医生总数  
```java
long total = doctorMapper.countAll();
if(total > 50) {  // 防止重复填充
    log.info("医生表已有 {} 条记录，跳过数据填充", total);
    return;
}
```
– 若表里已有超过 50 条记录，则认为数据充足，不再插入。

⑵ 读取所有科室  
```java
List<Department> depts = departmentMapper.selectAll();
```

⑶ 遍历科室生成医生  
```java
for (Department d : depts) {
    int existing = doctorMapper.countByDeptName(d.getName());
    for (int i = existing; i < 3; i++) {
        Doctor doc = new Doctor();
        // ① 账号：科室简码_序号 例：ent_1
        doc.setUsername(d.getCode().toLowerCase() + "_" + (i + 1));
        // ② 密码：统一使用 DEFAULT_PWD
        doc.setPassword(DEFAULT_PWD);
        // ③ 姓名：随机“姓 + 名” 例：王伟
        doc.setRealName(
            SURNAMES[r.nextInt(SURNAMES.length)] +
            GIVEN[r.nextInt(GIVEN.length)]
        );
        // ④ 其它属性
        doc.setDepartment(d.getName());
        doc.setHospitalName("中医院");
        doc.setTitle(TITLES[r.nextInt(TITLES.length)]);
        doc.setStatus(1);                        // 1=正常
        doc.setCreateTime(LocalDateTime.now());
        doc.setUpdateTime(LocalDateTime.now());

        doctorMapper.insert(doc);                // 插入数据库
        created++;
    }
}
```
– `existing`：当前科室已有的医生数量  
– 只有当 `existing < 3` 时才会补充。  
– 每条插入都使用随机名称和职称。

⑷ 日志输出  
```java
log.info("DoctorDataSeeder 生成医生 {} 条", created);
```
记录本次实际生成的医生数量，方便调试。

──────────────────────────────────────────────
总结  
DoctorDataSeeder 的目的是保证开发 / 测试环境在启动后拥有可用的演示数据，避免手动建医生账号。它通过：

• CommandLineRunner → 自动执行  
• 随机姓名 + 随机职称 → 数据更真实  
• 防重逻辑（>50 条则跳过 + 每科室已有 3 名则不再插入）→ 避免反复插入垃圾数据  

这样，启动完成后 UI 层即可立刻使用这些医生账号进行登录和功能测试。



JwtAuthenticationFilter.java

这段 `JwtAuthenticationFilter` 是一个自定义的 **JWT 认证过滤器**，继承自 `OncePerRequestFilter`，保证每个请求只执行一次。它在 Spring Security 过滤器链里执行，主要完成“从 Cookie 里解析并校验 JWT → 把登录信息放入 Spring Security 上下文”这一整套流程。下面逐行拆解。

────────────────────────────
1. 类与依赖

```java
@Component                      // 让 Spring 扫描成 Bean，自动加入过滤器链
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = ...
    @Autowired
    private JwtUtil jwtUtil;     // 工具类：生成、解析、校验 JWT
```

2. 入口方法 `doFilterInternal`

```java
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)
```

• 每次 HTTP 请求进入时都会走到这里。  
• 典型三步：①提取 token → ②校验 token → ③把用户信息写入 `SecurityContextHolder`。

3. 提取 Token

```java
String token = resolveTokenFromCookie(request);
```
`resolveTokenFromCookie` 只从 **HttpOnly Cookie** 中找名为 `token` 的值。为什么不用 Header？——浏览器端已把 JWT 存进 Cookie 以便自动携带；且 Cookie HttpOnly 可以防止 XSS 读取。

4. 判空与重复登录校验

```java
if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
```
• 若当前上下文已经有认证信息就不用再解析（避免重复）。

5. 校验 + 解析

```java
if (jwtUtil.validateToken(token)) {
    Long userId = jwtUtil.getUserIdFromToken(token);
    String role  = jwtUtil.getRoleFromToken(token);
```
`validateToken` 通常会验证：签名是否正确、是否过期、是否被吊销等。

6. 把信息放到 Request Attribute（方便后端接口直接取）

```java
request.setAttribute("userId", userId);
request.setAttribute("role", role);
```

7. 构造 Spring Security 的 `Authentication`

```java
List<GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(userId, null, authorities);

auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
SecurityContextHolder.getContext().setAuthentication(auth);
```

• 这里把 `userId` 当作 principal（也可以放整个 User 对象）。  
• 权限集合只放一条：`ROLE_医生/管理员/患者...`。  
• 填好后放入 `SecurityContextHolder`，之后任何 `@PreAuthorize` 等都能识别。

8. 异常处理

```java
catch (Exception e) { log.warn("JWT token validation failed ..."); }
```
• 捕获解析/校验异常，不阻断请求，让后续逻辑自己判断未认证状态。

9. 继续过滤链

```java
filterChain.doFilter(request, response);
```
• 即使认证失败，也让请求流向下一个过滤器；只是 `SecurityContext` 里没有用户信息，后面访问受保护接口时会抛 401。

10. `resolveTokenFromCookie` 细节

```java
private String resolveTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() == null) return null;
    return Arrays.stream(request.getCookies())
                 .filter(c -> "token".equals(c.getName()))
                 .map(Cookie::getValue)
                 .findFirst()
                 .orElse(null);
}
```
• Java Stream 遍历 Cookie 数组，匹配名为 `token` 的 Cookie，取其值。  
• 找不到返回 `null`。

────────────────────────────
为什么要这么写？

1. 前后端分离场景：前端登录接口拿到 JWT 后写入 HttpOnly Cookie；后续请求浏览器自动带 Cookie。  
2. 统一在后台过滤器层做认证，Controller 不用关心解析逻辑。  
3. 使用 Spring Security 原生的 `SecurityContext`，可无缝使用 `@AuthenticationPrincipal`、`hasRole()` 等注解/表达式。



SecurityConfig.java

这份 `SecurityConfig` 是一个“开发排错专用的极简 Spring-Security 配置”，核心思想：**什么安全功能都先关掉，只保留 BCrypt 密码加密器**。逐行说明如下。

1. 注解  
• `@Configuration`：声明为配置类；  
• `@EnableWebSecurity`：开启 Spring Security 自动配置。

2. `securityFilterChain()`  
```java
http
    .csrf(csrf -> csrf.disable())                         // ① 关闭 CSRF 防护
    .sessionManagement(sess ->                           // ② 会话无状态
        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(auth ->                       // ③ 所有请求直接放行
        auth.anyRequest().permitAll())
    .logout(l -> l.disable())                            // ④ 关闭登出端点
    .formLogin(f -> f.disable());                        // ⑤ 关闭默认登录页
return http.build();
```
含义：  
① CSRF：对浏览器表单攻击无防护；  
② STATELESS：Spring 不创建 `HttpSession`，接口完全无登录态概念；  
③ 权限：`anyRequest().permitAll()`，对谁都开放；  
④/⑤：把 Spring Security 自带的登录 / 登出功能也一起禁用，以免误触发 302。

3. `PasswordEncoder`  
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```
仍提供 BCrypt，便于注册用户时对密码做安全哈希。

整体效果  
• 启动后，所有 URL（含 `/api/**`、HTML 页面、静态资源）均无需登录可访问；  
• 不生成 JSESSIONID；  
• 项目中若自行引入 JWT、BasicAuth 等也不会被 Spring Security 拦截。  

仅适用于：  
• 本地快速调试 UI、接口；  
• 初期功能联调。

上生产需改进  
1. 把 `.anyRequest().permitAll()` 换成分段的 `.hasRole()/authenticated()` 显式授权；  
2. 如果用 JWT，添加自定义过滤器 `.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)`；  
3. 恢复或自行实现登录、登出端点；  
4. 重新启用 CSRF（或改用 Token/Cookie 双重验证）；  
5. 考虑开启 CORS、HSTS、X-Frame-Options 等头部安全策略。



WebMvcConfig.java

这份 `WebMvcConfig` 是一个 Spring MVC 配置类，借助 `WebMvcConfigurer` 接口来自定义静态资源映射与简易的 URL→视图跳转规则。逐段解析如下。

────────────────────────
1. 注解与接口  
```java
@Configuration           // 让 Spring 扫描并加载此配置
public class WebMvcConfig implements WebMvcConfigurer {
```
实现 `WebMvcConfigurer` 后，可按需覆写其中的钩子方法，而不会覆盖 Spring Boot 默认配置（若用 `@EnableWebMvc` 就会覆盖）。

────────────────────────
2. 静态资源映射 addResourceHandlers
```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String uploadPath = Paths.get("uploads").toAbsolutePath().toUri().toString();
    registry.addResourceHandler("/uploads/**")
            .addResourceLocations(uploadPath)
            .setCachePeriod(3600);
}
```
功能：  
• 把本地 `uploads` 目录（通常存放用户头像、舌象图片）暴露为 `/uploads/**` 路径。  
• `setCachePeriod(3600)` 指浏览器可缓存 1 小时（3600 秒）。  
• 注意 `uploadPath` 必须以 `file:/` 开头，因此 `.toUri().toString()` 转成 URI。

使用示例：  
前端 `<img src="/uploads/tongues/20260102_abc.jpg">` 即可访问硬盘 uploads/tongues/… 的文件。

────────────────────────
3. 视图直达 addViewControllers
```java
@Override
public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/login").setViewName("user/login");
    ...
}
```
作用：**无需写 Controller，就能把简易 URL 映射到模板/HTML**。  
流程：请求 `/login` → Thymeleaf 解析 `templates/user/login.html` 并返回。更轻量，也不会被 `@ResponseBody` 误伤。

• 用户模块  
  - `/login` → `user/login.html`  
  - `/register` → `user/register.html`

• 患者端  
  - `/patient/home` → 患者首页  
  - `/patient/tongue-diagnosis` → 上传舌象页面

• 医生端  
  - `/doctor/home`  
  - `/doctor/tongue-review`（医生审核舌象）  
  - `/doctor/prescriptions`

• 管理员端  
  - `/admin/dashboard`

这样前后端分离/多角色同域项目能快速直达视图；若后期需要逻辑处理，再改成普通 `@Controller` 即可。

────────────────────────
4. 总结
• `addResourceHandlers`：把磁盘目录映射成静态访问路径，解决用户上传文件的访问问题；  
• `addViewControllers`：用零代码的方式把 URL 直接映射到 HTML 模板，简化简单页面跳转；  
• 整个类不依赖安全配置，配合 `SecurityConfig` 使用时需确保相应路径已放行。



AdminApiController.java



这是后台「系统管理员接口」`AdminApiController` 的完整实现，统筹了患者管理、医生审核与管理、统计以及管理员个人信息查询。下面按功能模块逐段讲解。

=========================  
一、类级别配置
```
@RestController                 // 返回 JSON
@RequestMapping("/api/admin")   // 统一前缀
```
所有接口都是 `/api/admin/**` 路径，返回值被 Spring MVC 自动序列化成 JSON。

依赖注入：
```java
@Autowired PatientMapper patientMapper;
@Autowired DoctorMapper  doctorMapper;
@Autowired AdminMapper   adminMapper;
@Autowired JwtUtil       jwtUtil;
@Autowired DoctorService doctorService;
```
• `*Mapper` 负责直接对表 CRUD  
• `DoctorService` 封装了审核逻辑  
• `JwtUtil` 用来解析 JWT；管理员端采用 Header: `Authorization: Bearer xxx` 传 Token。

=========================  
二、患者管理

1. 查询所有患者  
```java
GET /api/admin/patients
```
- 拉出全部患者列表，返回前先 `setPassword(null)` 屏蔽密码字段。

2. 修改患者启用/禁用状态  
```java
PUT /api/admin/patients/{id}/status  body: { "status": 0|1 }
```
- 校验参数必须是 0/1  
- 记录 `update_time = now()`  
- 返回 Result.success / failed

3. 更新患者信息  
```java
PUT /api/admin/patients/{id}  body: Patient
```
- 若 ID 不存在直接失败  
- 设置 `updateTime = now()`，调用 `patientMapper.update(...)`

=========================  
三、医生审核（status=0 待审核）

1. 列出待审核医生  
```java
GET /api/admin/doctors/pending
```
- 调 `doctorService.findPending()` 返回。

2. 通过/驳回  
```java
POST /api/admin/doctors/{id}/approve
POST /api/admin/doctors/{id}/reject
```
- approve 将 status 设为 1，reject 设为 -1  
- 由 `doctorService.updateStatus(...)` 处理并反馈条数。

=========================  
四、医生管理（非待审医生）

1. 查询全部医生  
```java
GET /api/admin/doctors
```
- 同患者，隐藏密码字段。

2. 启用/禁用  
```java
PUT /api/admin/doctors/{id}/status  body: { "status": 0|1 }
```

3. 更新医生信息  
```java
PUT /api/admin/doctors/{id}  body: Doctor
```

=========================  
五、系统统计  
```java
GET /api/admin/stats
```
- 统计总患者数 / 启用患者 / 禁用患者  
- 统计总医生数 / 启用医生 / 禁用医生  
- 直接用 `Mapper.selectAll()` 然后在内存中 `stream().filter()` 统计；对数据量小的后台管理系统足够。

=========================  
六、管理员个人信息  
```java
GET /api/admin/info
Header: Authorization: Bearer <JWT>
```
- 去掉 `Bearer ` 前缀后用 `JwtUtil.getUserIdFromToken()` 解析管理员 ID  
- 读库查管理员信息并隐藏密码  
- 返回 200 / 400 风格的 `Result`

=========================  
返回统一响应包装  
全部接口返回 `Result<T>`，携带 `code`、`message`、`data`（见你们自定义的 `Result` 类），前端可统一处理。

=========================  
安全说明  
当前 `SecurityConfig` 全放行，若上线需要：  
- `/api/admin/**` 需绑定角色 `ADMIN`  
- `JwtAuthenticationFilter` 要放行静态但保护 API  
- Controller 方法可加 `@PreAuthorize("hasRole('ADMIN')")` 双保险。



AppointmentApiController.java

这段 `AppointmentApiController` 只负责“医生端删除预约”这一件事，删除时还会顺带把关联的支付单一并清掉。逐行说明如下。

────────────────────────────
一、类声明

```java
@RestController
@RequestMapping("/api/appointment")
public class AppointmentApiController {
```
• 所有接口前缀都是 `/api/appointment`  
• `@RestController` ＝ `@Controller + @ResponseBody`，自动返回 JSON

注入的 Bean：
```java
@Autowired AppointmentMapper appointmentMapper; // 预约表操作
@Autowired PaymentMapper     paymentMapper;     // 支付单操作
@Autowired JwtUtil           jwtUtil;           // 解析 JWT
```

────────────────────────────
二、唯一接口：DELETE /api/appointment/{id}

```java
@DeleteMapping("/{id}")
public Result<?> delete(@RequestHeader("Authorization") String auth,
                        @PathVariable Long id)
```

1. 取出当前登录医生 ID  
```java
Long doctorId =
    jwtUtil.getUserIdFromToken(auth.replace("Bearer ", ""));
```
前端把 JWT 放在 `Authorization: Bearer xxx`，这里去掉 `Bearer ` 前缀后解析。

2. 校验记录存在且归属当前医生  
```java
Appointment a = appointmentMapper.findById(id);
if (a == null)          return Result.failed("记录不存在");
if (!doctorId.equals(a.getDoctorId()))
                      return Result.failed("无权限删除");
```
防止越权操作。

3. 执行级联删除  
```java
appointmentMapper.deleteById(id);     // 删预约
paymentMapper.deleteByAppointmentId(id); // 删对应支付单
```

4. 返回统一包装  
```java
return Result.success("已删除");
```

────────────────────────────
使用示例

```
DELETE /api/appointment/123
Header: Authorization: Bearer <token>
```
若 token 对应医生正好拥有 ID=123 的预约，则删除成功；否则返回 400+自定义错误码。

────────────────────────────
安全前提

• 项目当前 `SecurityConfig` 处于全放行状态，依赖这个控制器自己做的鉴权（通过 JWT 中 doctorId 与记录归属比较）。  
• 若后续启用 Spring Security 的角色控制，应在配置中给 `/api/appointment/**` 加上 `hasRole('DOCTOR')`，并确保 `JwtAuthenticationFilter` 能把角色写入 `SecurityContext`。



CatalogApiController.java

`CatalogApiController` 负责向前端提供「非药品收费项目」下拉列表数据，供挂号费、检查费等场景选择。

关键点说明
────────────────────
1. 类级别注解  
```java
@RestController
@RequestMapping("/api/catalog")
```
• 统一路径前缀 `/api/catalog`  
• `@RestController` = `@Controller + @ResponseBody` → 默认返回 JSON

2. 依赖注入  
```java
@Autowired
private NonDrugCatalogMapper catalogMapper;
```
Mapper 对应表 `non_drug_catalog`，封装了查询 SQL。

3. 接口：GET /api/catalog/non-drug-items  
```java
@GetMapping("/non-drug-items")
public Result<List<NonDrugCatalog>> listItems() {
    List<NonDrugCatalog> list = catalogMapper.listPayItems();
    return Result.success(list);
}
```
• 调 `catalogMapper.listPayItems()` 查询所有收费项目（列出 `item_name`、`unit_price`，如同挂号费 10 元）。  
• 使用自家封装 `Result.success()` 返回：`{code:200, message:"操作成功", data:[...], timestamp:xxx}`。

使用示例  
```
GET /api/catalog/non-drug-items
→ [
   { "itemName":"挂号费", "unitPrice":10.00 },
   { "itemName":"心电图", "unitPrice":30.00 }
  ]
```
前端可把 `itemName` 直接显示为下拉文本，或显示 `itemName + " ￥" + unitPrice` 让用户选择。

接口特点  
• 纯 GET、无权限限制（若需要可在 SecurityConfig 中开放）。  
• 不分页，数据量很小。  
• 若同一个 `item_name` 有多条不同价格，Mapper 会取最小价用于展示（逻辑在 SQL）。

DepartmentApiController.java



`DepartmentApiController` 提供“科室目录”接口，便于前端（患者挂号、医生管理等）获取科室下拉列表。

代码解读
──────────────────────
1. 类声明  
```java
@RestController
@RequestMapping("/api/department")
```
- 所有方法路径以 `/api/department` 开头  
- 使用 `@RestController`，返回值自动序列化为 JSON

2. 依赖注入  
```java
@Autowired
private DepartmentMapper departmentMapper;
```
`DepartmentMapper` 是 MyBatis 映射接口，封装了 `selectAll()` 查询所有科室的 SQL。

3. 接口：GET /api/department/all  
```java
@GetMapping("/all")
public Result<?> listAll(){
    return Result.success(departmentMapper.selectAll());
}
```
- 查询科室表所有记录 `departmentMapper.selectAll()`  
- 用通用响应包装 `Result.success(...)` 返回  
  形如：`{code:200, message:"操作成功", data:[ {id:1, code:"ENT", name:"耳鼻喉科"}, ... ], timestamp:...}`

4. 适用场景  
- 患者端注册/挂号选择科室  
- 医生端个人信息维护选择所属科室  
- 管理员新建医生时下拉选科室

5. 安全说明  
当前项目 SecurityConfig 全放行，所以接口无需身份即可访问；  
若后期开启权限，通常 `/api/department/all` 仍会保留 `permitAll()` 因为属于公共数据。









下面按你列出来的 **每个 Controller 类**，用“作用/主要接口/关键逻辑点”说明清楚，并在每个类之间留出间隙（分隔线）。

---

## 1）DoctorApiController（医生个人信息 API）
**作用**：给医生端前端提供“查询/修改我的医生资料”的接口。  
**主要接口**：  
- `GET /api/doctor/info`：解析 `Authorization` 中 JWT，取 doctorId → 查 doctor 表 → 返回医生信息（不返回密码）。  
- `PUT /api/doctor/info`：解析 doctorId → 校验医生存在 → 更新允许修改字段（通过 mapper.update），并写入 `updateTime`。  
**关键点**：用 `JwtUtil` 从 token 解析医生 ID，避免前端传 doctorId 造成越权。

---

## 2）DoctorAppointmentApi（医生预约列表 API）
**作用**：医生端查看“我的预约”。  
**主要接口**：  
- `GET /api/doctor/appointment/my`：token → doctorId → `appointmentService.listForDoctor(doctorId)` 返回。  
**关键点**：医生只能查询自己的预约。

---

## 3）DoctorDeptApiController（按科室查询医生列表）
**作用**：患者预约页面选择科室后，拉取该科室可预约的医生列表。  
**主要接口**：  
- `GET /api/doctor/by-dept/{id}`：先用 deptId 查科室是否存在 → 再按 deptName 查询在岗医生。  
**关键点**：对外只暴露“科室→医生列表”，不需要登录也可用（取决于安全配置）。

---

## 4）DrugCatalogController（药品检索 API）
**作用**：医生开处方时搜索药品目录。  
**主要接口**：  
- `GET /api/drug/search?keyword=&page=&size=`：分页搜索药品表，返回药品列表。  
**关键点**：用 `page/size` 转换成 `offset` 做分页查询。

---

## 5）PatientAppointmentApiController（患者创建预约）
**作用**：患者提交预约（选择医生/科室/时间）。  
**主要接口**：  
- `POST /api/appointment`：token→uid；body 取 doctorId/deptId/time → `appointmentService.create(...)` 创建预约。  
**关键点**：患者 ID 从 token 来；时间使用 `LocalDateTime.parse`，前端必须传 ISO 格式字符串。

---

## 6）PaymentApiController（支付单 API）
**作用**：问诊结束生成支付单、患者模拟支付、查询支付单详情。  
**主要接口**：  
- `POST /api/payment/create-from-appointment/{appointmentId}?itemName=`：医生结束问诊生成支付单。  
- `POST /api/payment/complete/{paymentId}`：患者模拟支付成功，更新支付状态。  
- `GET /api/payment/{id}`：查询支付单详情。  
**关键点**：支付是“问诊后结算”的流程入口，支付单跟 appointment 关联。

---

## 7）PrescriptionApiController（处方模块新接口 / 页面用）
**作用**：给处方管理页面（`doctor/prescriptions.html`）提供接口。  
**主要接口**：  
- `GET /api/prescription/my`：医生自己的处方列表（返回字段做了适配：`totalPrice=totalAmount`）。  
- `GET /api/prescription/{id}/items`：处方明细（把实体字段映射成前端期望字段：drugName、dosage 等）。  
- `POST /api/prescription`：创建处方并落库。  
**关键点**：这里做了“字段名转换”，解决前端 undefined/金额为 0 的问题。

---

## 8）PrescriptionController（处方模块旧接口 / 兼容）
**作用**：另一套历史接口，提供创建处方、查列表、查详情。  
**主要接口**：  
- `POST /api/prescription/create`  
- `GET /api/prescription/doctor/my-prescriptions`  
- `GET /api/prescription/detail/{id}`  
**关键点**：与 PrescriptionApiController 路径有重叠风险（同是 `/api/prescription`），实际项目中建议保留一套并删掉另一套，避免维护成本与冲突。

---

## 9）ConsultationController（问诊记录 API）
**作用**：患者问诊记录 CRUD（创建、详情、列表、分页、删除）。  
**主要接口**：  
- `POST /api/consultation`：创建问诊记录（从 request attribute 取 userId）。  
- `GET /api/consultation/{id}`：查详情（校验 userId）。  
- `GET /api/consultation/my`：查我的全部问诊。  
- `GET /api/consultation/my/page?page=&size=`：分页查我的问诊。  
- `DELETE /api/consultation/{id}`：删除问诊记录（校验归属）。  
**关键点**：这里的登录态依赖过滤器把 userId 放进 request attribute（例如 JwtAuthenticationFilter）。

---

## 10）TongueController（舌诊分析/历史/统计 API）
**作用**：舌诊核心业务 API：上传分析、历史、趋势、对比、统计、详情等。  
**主要接口**：  
- `POST /api/tongue/analyze`：上传图片→AI分析→写库。  
- `GET /api/tongue/history`：历史记录。  
- `GET /api/tongue/trend`：健康趋势分析。  
- `GET /api/tongue/compare`：两次诊断对比。  
- `GET /api/tongue/statistics`：统计聚合。  
- `GET /api/tongue/detail/{id}`：详情（不需要登录）。  
- `GET /api/tongue/syndrome/{syndrome}`：按证型查询。  
**关键点**：`resolveUserId()` 支持两种来源：Header token 或 request attribute，提升兼容性。

---

## 11）DoctorController（医生端页面 Controller）
**作用**：返回 Thymeleaf 页面（不是 JSON），并在 model 中放入 doctorId / doctorName。  
**主要页面**：  
- `GET /doctor/home`：医生首页  
- `GET /doctor/pending-diagnosis`：待处理页面  
- `GET /doctor/diagnosis-detail?id=`：诊断详情页面  
**关键点**：`resolveUserId()` 会从 request attribute / Authorization / Cookie 多来源取 token，并强制校验角色为 DOCTOR。

---

## 12）TongueReviewController（医生舌诊审核 API）
**作用**：医生审核舌诊记录的接口层。  
**主要接口**：  
- `GET /api/doctor/tongue-review/pending`：待审核列表。  
- `GET /api/doctor/tongue-review/my-reviews`：我审核过的列表（token→doctorId）。  
- `GET /api/doctor/tongue-review/detail/{tongueId}`：单条详情。  
- `POST /api/doctor/tongue-review/submit`：提交审核结果。  
**关键点**：审核提交会把 finalSyndrome/finalAdvice 写回数据库（由 service 完成）。

---

## 13）PatientController（患者 API Controller，混合了页面与 JSON）
**作用**：患者端接口集合：个人信息、修改资料、舌诊记录、上传头像等。  
**主要接口**：  
- `GET /api/patient/home`：返回页面（patient-home）  
- `GET /api/patient/info`：当前登录患者信息  
- `PUT /api/patient/info`：修改患者信息  
- `GET /api/patient/records`：诊断记录（舌诊历史）  
- `GET /api/patient/info-page`：页面聚合数据  
- `GET /api/patient/diagnosis`：舌诊历史  
- `POST /api/patient/avatar`：上传头像 + 更新 patient.avatar_url  
**关键点**：这里大量逻辑是“从 Authorization / request attribute 取 userId”，并对文件上传做大小/类型校验。

---

## 14）AuthController（登录与注册 API）
**作用**：统一提供登录与注册（患者/医生）接口。  
**主要接口**：  
- `POST /api/auth/login`：登录并写 HttpOnly Cookie。  
- `POST /api/auth/register/patient`：患者注册。  
- `POST /api/auth/register/doctor`：医生注册（status=0，待审核）。  
**关键点**：医生注册成功返回“等待管理员审核”的提示，审核通过后才能作为医生正常使用系统。

---

## 15）UserController（用户个人中心 API，针对 user 表）
**作用**：用于 “user” 表的个人信息查询与修改（更像旧模块遗留）。  
**主要接口**：  
- `GET /api/user/info`：从 request attribute 取 userId → 查 user 表返回。  
- `PUT /api/user/info`：更新真实姓名/邮箱/性别等。  
**关键点**：如果当前项目主数据已经分成 patient/doctor/admin 三表，这块要注意是否仍需要，避免数据体系重复。

---

## 16）AdminWebController（管理员页面路由）
**作用**：管理员端页面跳转路由（返回模板）。  
**页面**：  
- `/admin/dashboard` `/admin/home` → dashboard  
- `/admin/patients` → patients  
- `/admin/doctors` → doctors  
- `/admin/stats` → stats  
- `/admin/profile` → profile  
**关键点**：只负责返回视图，不做业务处理。

---

## 17）DoctorConsultationWebController（医生问诊页面路由）
**作用**：进入医生问诊页面，把 appointmentId/doctorId 放入 model。  
**页面**：  
- `GET /doctor/consultation/{appointmentId}` → `doctor/consultation`  
**关键点**：doctorId 从 request attribute（过滤器解析 token 后放入）。

---

## 18）DoctorWebController（医生端页面路由）
**作用**：医生端一些页面入口。  
**页面**：  
- `/doctor/consultations` → doctor/consultations  
- `/doctor/prescriptions` → doctor/prescriptions  
- `/doctor/profile` → doctor/profile  
**关键点**：统一把 doctorId 塞入 model，方便页面 JS 请求接口时使用。

---

## 19）PatientPayWebController（患者支付页面路由）
**作用**：进入支付页面时把 paymentId 放进模板变量。  
**页面**：  
- `GET /patient/pay/{paymentId}` → `patient/pay`  
**关键点**：页面 JS 再通过 `/api/payment/{id}` 拉详情。

---

## 20）PatientWebController（患者页面路由）
**作用**：患者端所有页面入口（Thymeleaf）。  
**页面**：  
- `/patient/home`：患者主页  
- `/patient/diagnosis`：舌诊分析页面  
- `/patient/records`：诊断记录列表页  
- `/patient/diagnosis/detail/{id}`：舌诊详情页（含归属校验）  
- `/patient/appointment`：预约页  
- `/patient/history`：历史页  
- `/patient/profile`：个人中心页  
**关键点**：detail 页会校验诊断记录是否属于当前用户，防止越权查看。

---

## 21）HomeController（按角色跳转主页）
**作用**：访问 `/home` 时根据 request attribute 中的 role 重定向到对应首页。  
**逻辑**：  
- PATIENT → `/patient/home`  
- DOCTOR → `/doctor/workspace`（注意你现在医生主页实际是 `/doctor/home`，这里可能需要统一）  
- ADMIN → `/admin/dashboard`  
**关键点**：依赖过滤器提前把 role 写入 request。

---

## 22）LoginController（登录/注册页面 + 表单注册）
**作用**：提供登录/注册页面，以及表单提交注册的后端处理。  
**主要接口**：  
- `GET /login` → login.html  
- `GET /register` → register.html  
- `POST /register`：根据 role 分流  
  - role=DOCTOR → `authService.registerDoctor`（待审核）  
  - role=PATIENT → `authService.registerPatient`  
  成功后通过 `RedirectAttributes` 带 success 提示并跳转 `/login`。  
  **关键点**：这是“页面表单注册”的入口，与 `/api/auth/register/*` API 并存。

---







[6 tools called]

下面按你说的 **DTO层 / Service层 / Entity实体层 / Exception层 / Mapper层 / Util层** 逐个讲清楚，每一层中再按文件列出“作用 + 主要内容/典型用途”。（我会留一些间隙，方便你直接整理进文档）

---

## 一、DTO 层（dto）
DTO = Data Transfer Object，负责“接口入参/出参的数据结构”，与数据库实体解耦，避免把 entity 直接暴露给前端。

### 1）request（请求 DTO）
- **LoginRequest**  
  作用：登录接口入参（username/password）。

- **UserRegisterRequest**  
  作用：患者注册入参，包含用户名、密码、确认密码、真实姓名、手机号、邮箱、性别等，并带校验注解。

- **DoctorRegisterRequest**  
  作用：医生注册入参，在患者注册基础上增加医院、科室、职称、专长等字段；医生注册后默认 status=0 待审核。

- **PrescriptionCreateRequest**  
  作用：医生创建处方的入参：patientId、diagnosisId（可选）、items（药品列表：drugCode、drugName、dosage、quantity、unitPrice）。

- **TongueReviewRequest**  
  作用：医生审核舌诊入参：tongueId、finalSyndrome、finalAdvice。

- **ConsultationCreateRequest**  
  作用：创建问诊记录的入参（具体字段视文件定义，通常含症状描述、主诉等）。

### 2）response（响应 DTO）
- **UserInfo**  
  作用：统一的“用户信息响应对象”（已合并原 UserInfoResponse）。登录成功返回的 userInfo、注册成功返回信息等都用它。

- **LoginResponse**  
  作用：登录响应：token + role + userInfo。

- **PatientInfoResponse / DoctorInfoResponse**  
  作用：分别用于患者/医生个人资料展示（避免直接暴露 entity，能隐藏敏感字段）。

- **PrescriptionResponse**  
  作用：处方详情响应（处方基础信息 + items 明细）。

- **TongueDetailResponse**  
  作用：舌诊审核详情响应：包含图片、AI分析、医生最终审核内容等。

- **DiagnosisStatisticsResponse / DiagnosisComparisonResponse / HealthTrendResponse**  
  作用：舌诊统计、对比、趋势分析三类聚合型结果，用于患者端图表/对比展示。

- **ConsultationResponse**  
  作用：问诊记录查询/创建后的统一返回结构。

### 3）dto 其他
- **DoctorSimpleDTO**  
  作用：轻量医生信息（id/realName/title），用于预约页面“按科室列医生”下拉列表。

---

## 二、Service 层（service）
Service = 业务层，负责组合 Mapper、处理业务规则、事务控制、数据校验、跨表写入等。

- **AuthService**  
  作用：登录 + 注册核心业务。  
  - registerPatient：写 patient 表，密码加密  
  - registerDoctor：写 doctor 表，status=0 待审核  
  - loginAndSetCookie：根据 username 在 patient/doctor/admin 表中查用户并校验密码，生成 JWT，写入 HttpOnly cookie，并返回 LoginResponse

- **DoctorService**  
  作用：医生基础查询 + 审核相关逻辑  
  - findPending：找 status=0 待审核医生  
  - updateStatus：管理员通过/驳回审核修改 status

- **PatientService**  
  作用：患者基础 CRUD（查、改、删、注册等）主要调用 PatientMapper。

- **AppointmentService**  
  作用：预约业务  
  - create：患者创建预约（可能同时生成支付单或检查冲突等）  
  - listForDoctor：医生查看预约

- **PaymentService**  
  作用：支付单业务  
  - createPaymentForAppointment：问诊结束生成支付单  
  - completePayment：模拟支付成功，更新支付状态  
  - getPaymentById：查询支付单

- **PrescriptionService**  
  作用：处方业务  
  - create：创建处方、计算总价、插入 prescription + 批量插入 prescription_item（事务）  
  - listByDoctor/getItems：查询医生处方和明细  
  - createPrescription/getDoctorPrescriptions/getPrescriptionDetail：兼容另一套旧接口返回 PrescriptionResponse

- **TongueReviewService**  
  作用：医生审核舌诊  
  - getPendingReviews：待审列表  
  - getReviewedByDoctor：我审核的列表  
  - reviewTongue：提交审核结果并写回舌诊记录

- **diagnosis/TongueDiagnosisService**  
  作用：舌诊核心业务  
  - analyzeTongue：调用 AI 服务分析、存库  
  - getUserHistory：历史记录  
  - analyzeHealthTrend / compareDiagnosis / getUserStatistics：趋势/对比/统计聚合  
  - getDetail/findBySyndrome 等查询

- **ai/QwenVisionService**  
  作用：对接通义千问多模态接口，完成图片 + prompt → 文本分析结果。  
  （你前面已精简了启动日志与未用图片增强逻辑）

- **consultation/ConsultationService + ConsultationServiceImpl**  
  作用：问诊记录业务：创建、查询详情、列表、分页、删除，并校验归属。

- **OrderService / AdminService**  
  作用：订单与管理员相关业务（若当前页面/接口未大量使用，多为支撑类）。

---

## 三、实体层（entity）
Entity = 数据库表的 Java 映射对象（字段通常对应表字段），主要用于 MyBatis 查询/写入。

- **Patient / Doctor / Admin / User**  
  作用：三类用户实体（Patient、Doctor、Admin 分表；User 可能是旧系统遗留表）。  
  注意：对外响应通常不直接返回 entity（会用 DTO 或将密码置空）。

- **Department**  
  作用：科室表实体（id/code/name/type），用于预约/医生归属。

- **Appointment**  
  作用：预约表实体（患者、医生、科室、时间、状态等）。

- **Payment**  
  作用：支付单实体（与 appointment 关联、金额、项目、状态、时间等）。

- **Prescription / PrescriptionItem**  
  作用：处方主表与明细表实体，支持处方创建与查询。

- **DrugCatalogItem**  
  作用：药品目录实体，用于医生搜索药品。

- **NonDrugCatalog**  
  作用：非药品收费目录（挂号费、检查费等），用于支付项目下拉框。

- **TongueDiagnosis**  
  作用：舌诊记录实体（图片 URL、AI结果、最终审核结果、状态、时间等）。

- **Consultation**  
  作用：问诊记录实体（问诊内容、医生/患者关联等）。

- **TcmOrder**  
  作用：订单实体（支付/处方/问诊的业务延伸，具体看表设计）。

---

## 四、Exception 层（exception）
- **GlobalExceptionHandler**  
  作用：全局异常捕获（`@ControllerAdvice`/`@ExceptionHandler`），把系统异常统一转换成 `Result.failed(...)` 形式，避免返回白页/堆栈给前端。  
  常见处理：参数错误、业务 RuntimeException、MethodNotSupported 等。

---

## 五、Mapper 层（mapper）
Mapper = MyBatis 数据访问层，直接执行 SQL（注解 SQL 或 XML SQL），把数据库结果映射为实体/DTO。

- **PatientMapper / DoctorMapper / AdminMapper / UserMapper**  
  作用：用户相关表 CRUD、按用户名查、更新状态等。  
  DoctorMapper 还提供 `findByDeptName`（返回 DoctorSimpleDTO）。

- **DepartmentMapper**  
  作用：科室表查询（selectAll/selectById）。

- **AppointmentMapper**  
  作用：预约表增删改查（findById/deleteById 等）。

- **PaymentMapper**  
  作用：支付单表 CRUD，并包含按 appointmentId 删除支付单等方法。

- **DrugCatalogMapper**  
  作用：药品目录搜索（支持 keyword + offset + size）。

- **NonDrugCatalogMapper**  
  作用：非药品收费项目查询，提供 listPayItems 给下拉框。

- **PrescriptionMapper / PrescriptionItemMapper**  
  作用：处方主表插入/查询；明细表批量插入/按处方 ID 查询等。

- **TongueDiagnosisMapper**  
  作用：舌诊记录查询/插入/更新（历史列表、详情、待审核等）。

- **ConsultationMapper**  
  作用：问诊记录的数据库操作。

- **TcmOrderMapper**  
  作用：订单表数据库操作。

---

## 六、Util 层（util）
- **JwtUtil**  
  作用：JWT 工具类：生成 token、校验 token、从 token 解析 userId、role、过期时间等。  
  配合 `JwtAuthenticationFilter` 把解析结果放入 request attribute 和 SecurityContext（如果安全开启）。

---





患者业务完整链路（代码与数据库流向）

1. 预约（Appointment）
   • 前端：/patient/appointment 页面  
     – 选择科室→调用 GET /api/department/all  
     – 选择医生→调用 GET /api/doctor/by-dept/{deptId}  
     – 选时间点击预约→POST /api/appointment  
   • 后端：`PatientAppointmentApiController.create()` → `AppointmentService.create()`  
     – 写表 appointment(id, patient_id, doctor_id, dept_id, appoint_time, status=0)

2. 医生问诊（Consultation）
   • 医生端：/doctor/consultation/{appointmentId}  
     – 填诊断/医嘱/处方，选收费项目  
     – 点击“结束问诊并生成支付单”  
       ① 保存咨询内容 `/api/consultation/save`（写表 consultation、处方 prescription…）  
       ② 调用 `/api/payment/create-from-appointment/{appointmentId}?itemName=xxx`  
   • 后端：`PaymentService.createPaymentForAppointment()`  
     – 读取 non_drug_catalog_sql_results 找到 unit_price  
     – 写表 payment(id, appointment_id, patient_id, doctor_id, item_name, amount, status=0)

3. 患者支付（Payment）
   • 前端：/patient/pay/{paymentId}  
     – GET /api/payment/{paymentId} 加载 item_name + amount  
     – 点击“立即支付”→ POST /api/payment/complete/{paymentId}  
   • 后端：`PaymentApiController.completePayment()`  
     – payment.status 置 1，pay_time=now

4. 处方查看/下载
   • 患者端：/patient/prescription/{id}  
     – GET /api/prescription/{id} 读取 prescription + prescription_item  
   • 处方生成逻辑已在医生问诊保存时完成

5. 舌诊结果
   • 患者上传舌象图 `/patient/tongue/upload`  
   • AI 识别 `TongueDiagnosisService` 存 tongue_diagnosis 表  
   • 患者端 /patient/tongue/history 拉取 GET /api/tongue/my

关键表关系  
Patient(患者) ←→ Appointment ←→ Consultation / Payment ←→ Prescription ｜ TongueDiagnosis

删除/级联  
• 删除预约接口 `/api/appointment/{id}`（医生端）：  
  先校验 doctorId，再 `appointmentMapper.deleteById` + `paymentMapper.deleteByAppointmentId`

这样从预约到舌诊的患者全流程即完成。







