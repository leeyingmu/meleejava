# meleejava
基于 java servlet3.0 实现的 java web 基础框架

- com.melee.meleejava.web.BaseListener 
  
  ```
  系统启动抽象类，首先初始化Config和RDSPool，然后调用其子类的的初始化的方法。
  使用者自己的Listener需要集成该类，然后实现其初始化方法。
  使用方式参考 com.melee.meleejava.demo
  ```
  
- com.melee.meleejava.web.BaseServlet 
  
  ```
  所有接口的基类，同python版melee一样，已经实现对于输入的签名验证等操作，其子类只需要实现 process 方法来处理具体的业务逻辑。
  使用方式参考 com.melee.meleejava.demo
  ```
  
- com.melee.meleejava.web

  ```
  web 基础包，exceptions 为 http 请求处理返回值的基础异常，任何业务逻辑处理的地方都可以抛出这类一样，BaseServlet 将会统一处理.
  ```
 
- com.melee.meleejava.utils.Config

  ```
  全局配置类，其配置文件为 WEB-INF 目录下的 config.yaml
  ```

- com.melee.meleejava.utils
  
  ```
  基础工具包
  ```
  
- com.melee.meleejava.rds
  
  ```
  关系数据库基础库
  ```


