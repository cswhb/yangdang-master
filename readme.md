
## 环境搭建
* 安装 jdk 8+
* 安装 maven 3+

## 修改配置
* 修改vis\src\main\resources\application.yml里面的配置文件，如下：
```
# web app 端口
server:
  port: 8080

# socket监听的端口
netty:
  server:
    port: 3333
```

## 运行方式
### 第一种：打包成jar包运行
1.进入vis项目的根目录，然后运行`mvn package`命令把工程打包成jar；
```cmd
vis\: mvn package
```
2.运行jar包：maven默认打包在target目录，进入vis\target目录，然后运行`java -jar vis.jar`
```cmd
vis\target\: java -jar yangdang-1.0.0.jar
```

### 第二种：使用maven运行
进入vis项目的根目录，然后运行`mvn spring-boot:run`命令。
```cmd
vis\: mvn spring-boot:run
```

