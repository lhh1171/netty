package heartbeatretry.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HeartBeatServer {


    public void bind(String host, int port) throws InterruptedException {
        EventLoopGroup worker = new NioEventLoopGroup();
        EventLoopGroup boss = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelPipelineInit());
        serverBootstrap.bind(host, port).sync();
    }

    public static void main(String[] args) throws InterruptedException {
        new HeartBeatServer().bind("localhost", 55555);
    }
}
