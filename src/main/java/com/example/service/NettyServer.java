package com.example.service;


import com.example.handler.MqttHandler;
import com.example.handler.TcpHandler;
import com.example.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

// 标记为 Spring 的服务组件
@Service
public class NettyServer {

    private final int mqttPort;
    private final int websocketPort;
    private final int tcpPort;

    // 用于管理 Netty 事件循环
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    // 构造函数，初始化端口
    public NettyServer(int mqttPort, int websocketPort, int tcpPort) {
        this.mqttPort = mqttPort;
        this.websocketPort = websocketPort;
        this.tcpPort = tcpPort;
    }

    // Spring Bean 初始化后启动 Netty 服务器
    @PostConstruct
    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(); // Boss 线程组
        workerGroup = new NioEventLoopGroup(); // Worker 线程组
        try {
            startMqttServer(); // 启动 MQTT 服务器
            startWebSocketServer(); // 启动 WebSocket 服务器
            startTcpServer(); // 启动 TCP 服务器
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // 启动 MQTT 服务器
    private void startMqttServer() throws InterruptedException {
        ServerBootstrap mqttBootstrap = new ServerBootstrap(); // 创建引导类
        mqttBootstrap.group(bossGroup, workerGroup) // 设置事件循环组
                .channel(NioServerSocketChannel.class) // 指定通道类型
                .childHandler(new ChannelInitializer<SocketChannel>() { // 初始化通道
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new MqttDecoder()); // 添加 MQTT 解码器
                        ch.pipeline().addLast(MqttEncoder.INSTANCE); // 添加 MQTT 编码器
                        ch.pipeline().addLast(new MqttHandler()); // 添加 MQTT 处理器
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128) // 设置队列大小
                .childOption(ChannelOption.SO_KEEPALIVE, true); // 保持连接

        ChannelFuture mqttFuture = mqttBootstrap.bind(mqttPort).sync(); // 绑定端口并启动
        mqttFuture.channel().closeFuture().addListener(future -> System.out.println("MQTT Server closed")); // 添加关闭监听器
    }

    // 启动 WebSocket 服务器
    private void startWebSocketServer() throws InterruptedException {
        ServerBootstrap wsBootstrap = new ServerBootstrap();
        wsBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new HttpServerCodec()); // HTTP 编解码器
                        ch.pipeline().addLast(new ChunkedWriteHandler()); // 分块写入处理器
                        ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws")); // WebSocket 协议处理器
                        ch.pipeline().addLast(new WebSocketHandler()); // WebSocket 处理器
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture wsFuture = wsBootstrap.bind(websocketPort).sync();
        wsFuture.channel().closeFuture().addListener(future -> System.out.println("WebSocket Server closed"));
    }

    // 启动 TCP 服务器
    private void startTcpServer() throws InterruptedException {
        ServerBootstrap tcpBootstrap = new ServerBootstrap();
        tcpBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter())); // 分隔符解码器
                        ch.pipeline().addLast(new StringDecoder()); // 字符串解码器
                        ch.pipeline().addLast(new StringEncoder()); // 字符串编码器
                        ch.pipeline().addLast(new TcpHandler()); // TCP 处理器
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture tcpFuture = tcpBootstrap.bind(tcpPort).sync();
        tcpFuture.channel().closeFuture().addListener(future -> System.out.println("TCP Server closed"));
    }

    // Spring Bean 销毁前关闭 Netty 服务器
    @PreDestroy
    public void stop() {
        workerGroup.shutdownGracefully(); // 优雅地关闭 Worker 线程组
        bossGroup.shutdownGracefully(); // 优雅地关闭 Boss 线程组
    }
}
