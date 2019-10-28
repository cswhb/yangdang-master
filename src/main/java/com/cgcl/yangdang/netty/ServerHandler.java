package com.cgcl.yangdang.netty;

import com.cgcl.yangdang.common.JsonUtils;
import com.cgcl.yangdang.common.Message;
import com.cgcl.yangdang.entity.Point;
import com.cgcl.yangdang.websocket.WebSocketServer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * </p>
 *
 * @author Liu Cong
 * @since 2019-04-04
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private static Map<String, List<Point>> lineMap = new ConcurrentHashMap<>();

    static {
        lineMap.put("HMCached", new ArrayList<>());
        lineMap.put("HM-UNI(Memcached)", new ArrayList<>());
        lineMap.put("DRAM-HMC", new ArrayList<>());
        lineMap.put("DRAM-UNI", new ArrayList<>());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("接受到的消息为：" + msg);
        try {
            Point point = formatMsg(msg);
            Map map = JsonUtils.parse(msg.trim(), Map.class);
            if(point.getName().equals("HMCached")){
                List<Point> TD = lineMap.get("HMCached");
                TD.add(point);
            }
            if(point.getName().equals("HM-UNI(Memcached)")){
                List<Point> TN = lineMap.get("HM-UNI(Memcached)");
                TN.add(point);
            }
            if(point.getName().equals("DRAM-HMC")){
                List<Point> MD = lineMap.get("DRAM-HMC");
                MD.add(point);
            }
            if(point.getName().equals("DRAM-UNI")){
                List<Point> MN = lineMap.get("DRAM-UNI");
                MN.add(point);
            }
            
            
            
            log.info(JsonUtils.toJson(lineMap));
            WebSocketServer.broadCastInfo(getLineMapMessageWraps());
            ctx.writeAndFlush("success");
        } catch (RuntimeException e) {
            log.warn("解析json出错！");
            ctx.writeAndFlush("error, msg=" + msg);
            e.printStackTrace();
        }
    }
    private Point formatMsg(String msg) {
        Map map = JsonUtils.parse(msg.trim(), Map.class);
        Point point = new Point();
        point.setName(map.get("name").toString());
        point.setX(Long.parseLong(map.get("x").toString()));
        point.id=(Long.parseLong(map.get("x").toString()));
        point.setY(Double.parseDouble(map.get("y").toString()));
        return point;
    }
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress() + ": Channel Registered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress() + ": Channel Unregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress() + ": Channel Active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress() + ": Channel Inactive");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress() + ": Channel Read Complete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Memory information receiver occurs errors!");
        cause.printStackTrace();
        ctx.close();
    }

    public static String getLineMapMessageWraps() {
        return Message.success().add("lineMap", lineMap).toString();
    }

    public static void clearLineMap() {
        lineMap.put("HMCached", new ArrayList<>());
        lineMap.put("HM-UNI(Memcached)", new ArrayList<>());
        lineMap.put("DRAM-HMC", new ArrayList<>());
        lineMap.put("DRAM-UNI", new ArrayList<>());
    }
}
