package com.cgcl.yangdang.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * <p>
 *
 * </p>
 *
 * @author Liu Cong
 * @since 2019-04-04
 */
@Slf4j
@Component
public class Server {
    // boss 线程组用于处理连接工作
    private EventLoopGroup bossGroup = new NioEventLoopGroup();

    // work 线程组用于数据处理
    private EventLoopGroup workGroup = new NioEventLoopGroup();

    @Value("${netty.server.port}")
    private Integer port;


    /**
     * 启动 Netty Server
     */
    @PostConstruct
    public void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerInitializer());

            ChannelFuture future = bootstrap.bind(port).sync();
            if (future.isSuccess()){
                log.info("启动 Netty Server");
            }
        } catch (InterruptedException e) {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        bossGroup.shutdownGracefully().sync();
        workGroup.shutdownGracefully().sync();
        log.info("关闭 Netty Server");
    }
}
