

```markdown
# Netty Spring Boot Integration

## 项目介绍

本项目展示了如何在 Spring Boot 应用程序中集成 Netty，支持 MQTT、TCP 和 WebSocket 协议。通过提供三个主要的处理器类 (`MqttHandler`, `TcpHandler`, 和 `WebSocketHandler`)，实现对不同协议消息的接收和发送。
 


## 环境

- JDK 1.8
- Maven 3.6.3 或更高版本
- Spring Boot 2.7.7

## 依赖项

```xml
<dependencies>
    <!-- Spring Boot 核心依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <version>2.7.7</version>
    </dependency>
    <!-- Spring Boot Actuator，用于监控 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
        <version>2.7.7</version>
    </dependency>
    <!-- Netty 依赖，用于创建网络服务器 -->
    <dependency>
        <groupId>io.netty</groupId>
        <artifactId>netty-all</artifactId>
        <version>4.1.97.Final</version>
    </dependency>
    <!-- Netty MQTT 编解码依赖 -->
    <dependency>
        <groupId>io.netty</groupId>
        <artifactId>netty-codec-mqtt</artifactId>
        <version>4.1.97.Final</version>
    </dependency>
</dependencies>
```

## 快速开始

1. 克隆项目

```bash
git clone https://github.com/your-repo/netty-spring-boot.git
cd netty-spring-boot
```

2. 使用 Maven 构建项目

```bash
mvn clean install
```

3. 运行 Spring Boot 应用

```bash
mvn spring-boot:run
```

## 详细说明

### MqttHandler

`MqttHandler` 用于处理 MQTT 协议消息。

- **接收消息**

```java
// 从客户端接收 MQTT 消息
mqttHandler.receiveMessage(ctx, mqttMessage);
```

- **发布消息**

```java
// 发布 MQTT 消息到指定主题
mqttHandler.publishMessage(channel, "topic/name", "Hello MQTT");
```

### TcpHandler

`TcpHandler` 用于处理 TCP 协议消息。

- **接收消息**

```java
// 从客户端接收 TCP 消息
tcpHandler.receiveMessage(ctx, "Hello TCP");
```

- **发送消息**

```java
// 发送 TCP 消息到客户端
tcpHandler.sendMessage(channel, "Hello TCP");
```

### WebSocketHandler

`WebSocketHandler` 用于处理 WebSocket 协议消息。

- **接收消息**

```java
// 从客户端接收 WebSocket 消息
webSocketHandler.receiveMessage(ctx, new TextWebSocketFrame("Hello WebSocket"));
```

- **发送消息**

```java
// 发送 WebSocket 消息到客户端
webSocketHandler.sendMessage(channel, "Hello WebSocket");
```

