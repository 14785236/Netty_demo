package com.example.config;

import com.example.service.NettyServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 配置类，定义 Bean
@Configuration
public class NettyServerConfig {
    // 从配置文件中读取 MQTT 端口
    @Value("${netty.mqtt.port}")
    private int mqttPort;

    // 从配置文件中读取 WebSocket 端口
    @Value("${netty.websocket.port}")
    private int websocketPort;

    // 从配置文件中读取 TCP 端口
    @Value("${netty.tcp.port}")
    private int tcpPort;

    // 创建并配置 NettyServer Bean
    @Bean
    public NettyServer nettyServer() {
        return new NettyServer(mqttPort, websocketPort, tcpPort);
    }
}
