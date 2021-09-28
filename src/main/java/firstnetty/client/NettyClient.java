package firstnetty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        //客户端需要一个时间循环包
        EventLoopGroup eventExecutors=new NioEventLoopGroup();


        //创建客户端启动对象
        //注意客户端使用的不是serverBootstrap,而是bootstrap
        Bootstrap bootstrap=new Bootstrap();
try {


    //设置相关参数
    bootstrap.group(eventExecutors)//设置线程组
            .channel(NioSocketChannel.class)//设置客户端通道的实现类（反射）
            .handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new NettyClientHandler());//加入自己的handler
                }
            });
    System.out.println("客户端 ok....");

    //启动客户端去连接handler
    ChannelFuture channelFuture =
            bootstrap.connect("127.0.0.1", 6668).sync();
//给关闭通道进行监听
    channelFuture.channel().closeFuture().sync();
}finally {
    eventExecutors.shutdownGracefully();
}
    }

}



