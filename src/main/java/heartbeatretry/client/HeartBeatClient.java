package heartbeatretry.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartBeatClient {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private EventLoopGroup group = new NioEventLoopGroup();

    public void connect(String host, int port) throws InterruptedException {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelPipelineInit());
            //async connect
            ChannelFuture connect = bootstrap.connect(new InetSocketAddress(host, port)).sync();
            connect.channel().closeFuture().sync();
        } finally {
            executorService.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    connect(host, port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }


    public static void main(String[] args) throws InterruptedException {
        new HeartBeatClient().connect("localhost", 55555);
    }

}
