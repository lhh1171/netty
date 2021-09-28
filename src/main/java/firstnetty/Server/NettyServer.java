package firstnetty.Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        //创建boss(处理链接请求)，work（处理读写请求）
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        NioEventLoopGroup workerGroup=
                new NioEventLoopGroup();

        //创建引导
        ServerBootstrap serverBootstrap=new ServerBootstrap();

        //绑定
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,128)//设置线程队列连接个数
                .childOption(ChannelOption.SO_KEEPALIVE,true)//设置保持活动连接状态
                .childHandler(//给我们的workerGroup的eventloop对应的handler
                        new  ChannelInitializer<SocketChannel>(){
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                System.out.println("客户端 socketchannel hashcode"+socketChannel.hashCode());
                                //可以使用集合管理hashcode,在推送消息时，可以将业务channel对应的nioeventloop的taskqueue
                                //或者scheduletaskqueue
                                    socketChannel.pipeline().addLast(new nettyServerHandler());
                            }
                        });
        System.out.println("服务器 is ready");

        //绑定一个端口并且同步，生成一个channelFuture对象
        //启动服务器
        ChannelFuture cf=serverBootstrap.bind(6668).sync();

        //对关闭通道进行监听
        cf.channel().closeFuture().sync();

    }
}
