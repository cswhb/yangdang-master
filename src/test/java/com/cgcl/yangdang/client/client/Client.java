package com.cgcl.yangdang.client.client;

import com.cgcl.yangdang.common.JsonUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 *
 * </p>
 *
 * @author Liu Cong
 * @since Created in 2019/3/5
 */
@Slf4j
public class Client {

    private EventLoopGroup group = new NioEventLoopGroup();

    @Value("${netty.client.host}")
    private String host;
    @Value("${netty.client.port}")
    private Integer port;

    @PostConstruct
    public void start() {
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new Handler());
            ChannelFuture future = bootstrap.connect(host, port).sync();
            if (future.isSuccess()) {
                log.info("启动 Netty Client");
            }
        } catch (Exception e) {
            group.shutdownGracefully();
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static class Producer implements Runnable {

        @Override
        public void run() {
            EventLoopGroup group = new NioEventLoopGroup();
            String host = "localhost";
            Integer port = 3333;
            try {
                Bootstrap bootstrap = new Bootstrap()
                        .group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new Initializer());
                ChannelFuture future = bootstrap.connect(host, port).sync();
                if (future.isSuccess()) {
                    log.info("启动 Netty Client");
                }
                Channel channel = future.channel();
//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                Long count = 0L;
                while (true) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("mem-D", Math.random() * 10000);
                    map.put("mem-N", Math.random() * 10000);
                    map.put("Throughput-D", Math.random() * 10000);
                    map.put("Throughput-N", Math.random() * 10000);
                    channel.writeAndFlush(JsonUtils.toJson(map) + "\r\n");
                    Thread.sleep(3000);
                }
            } catch (Exception e) {
                group.shutdownGracefully();
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
        }
    }

    @PreDestroy
    public void destory() throws InterruptedException {
        group.shutdownGracefully().sync();
        log.info("关闭 Netty Client");
    }

    public static void main(String[] args) {
        Runnable task1 = new Producer();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(task1);
    }
}
