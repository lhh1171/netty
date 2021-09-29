package heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by andilyliao on 16-6-7.
 */
public class SimpleClient {
    public static void main(String[] args) throws Exception {
        new SimpleClient("127.0.0.1", 9597).run();
        }
    private final String host;
    private final int port;
    public SimpleClient(String host, int port) {
        this.host = host;
        this.port = port;
        }
    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap().group(group).channel(
                    NioSocketChannel.class).handler(
                    new SimpleClientInitializer());
            Channel channel = bootstrap.connect(host, port).sync().channel();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            while (true) {
                channel.writeAndFlush(in.readLine());
            }
            } catch (Exception e) {
            e.printStackTrace();
            } finally {
            group.shutdownGracefully();
            }
        }
}
