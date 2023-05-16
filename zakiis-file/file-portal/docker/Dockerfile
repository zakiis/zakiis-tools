# 基础镜像
FROM openjdk:8-jdk-alpine
# 获取--build-arg参数
ARG JAR_FILE
# 创建用户和文件夹
RUN addgroup -g 1001 -S wls
RUN adduser -S -u 1001 -G wls wls81
RUN mkdir -p /wls/deploy/logs && touch /wls/deploy/logs/app.log
# copy当前工程jar包至容器内
COPY ${JAR_FILE} /wls/deploy/app.jar

# 修改权限
RUN chmod 700 /wls/deploy && chown -R wls81.wls /wls

EXPOSE 8081
USER wls81
WORKDIR /wls/deploy
# 运行jar包
ENTRYPOINT java -server -Duser.timezone=${APP_TIMEZONE} \
-Xms1024m -Xmx1024m -Xss1M -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m \
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/wls/deploy/logs \
-jar /wls/deploy/app.jar \
--spring.profiles.active=${ACTIVE_PROFILE}