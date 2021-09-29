package heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by andilyliao on 16-6-7.
 */
public class HeartBeatServer {

    // 端口
    private int port ;
    public HeartBeatServer(int port) {
        this.port = port;
    }

    ChannelFuture f ;

    ServerBootstrap b ;

    // 检测chanel是否接受过心跳数据时间间隔（单位秒）
    private static final int READ_WAIT_SECONDS = 10;


    public void startServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new HeartBeatServerInitializer());
            // 服务器绑定端口监听
            f = b.bind(port).sync();
            // 监听服务器关闭监听，此方法会阻塞
            f.channel().closeFuture().sync();
            // 可以简写为
            /* b.bind(portNumber).sync().channel().closeFuture().sync(); */
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    /**
     * 消息处理器
     * @author cullen edward
     */
    private class HeartBeatServerInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            /*
             * 使用ObjectDecoder和ObjectEncoder
             * 因为双向都有写数据和读数据，所以这里需要两个都设置
             * 如果只读，那么只需要ObjectDecoder即可
             */
            pipeline.addLast("decoder", new StringDecoder());
            pipeline.addLast("encoder", new StringEncoder());

            /*
             * 这里只监听读操作
             * 可以根据需求，监听写操作和总得操作
             */
            /*
            readerIdleTime读空闲超时时间设定，如果channelRead()方法超过readerIdleTime时间未被调用则会触发超时事件调用userEventTrigger()方法；

            writerIdleTime写空闲超时时间设定，如果write()方法超过writerIdleTime时间未被调用则会触发超时事件调用userEventTrigger()方法；

            allIdleTime所有类型的空闲超时时间设定，包括读空闲和写空闲；

            unit时间单位，包括时分秒等；
            */
            pipeline.addLast("pong", new IdleStateHandler(READ_WAIT_SECONDS, 0, 0, TimeUnit.SECONDS));
            pipeline.addLast("handler", new HeartbeatHandler());
        }
    }

    public void stopServer(){
        if(f!=null){
            f.channel().close();
        }
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        HeartBeatServer heartbeatServer = new HeartBeatServer(9597);
        heartbeatServer.startServer();
    }
}
