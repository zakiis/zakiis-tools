### 简介
提供k8s模版，运行在docker上的高可用Eureka Server注册中心

### 配置项（K8S传入的环境变量）

| 配置名 | 描述 | 是否必输 | 描述 |
| :---  | :---| :-------| :----------|
| PORT | 端口号 | N | 8761 |
| EUREKA_INSTANCE_HOSTNAME | eureka实例的hostname | N | 不传默认localhost，传的话应该传pod名|
| BOOL_REGISTER | 是否注册本服务到Eureka | N | true |
| BOOL_FETCH | 是否拉取Registry | N | true |
| EUREKA_SERSER | eureka服务地址 | Y | 推荐配置http://eureka-service.eureka-server1:8761/eureka/,http://eureka-service.eureka-server2:8761/eureka/,http://eureka-service.eureka-server3:8761/eureka/, |

### Dockerfile



