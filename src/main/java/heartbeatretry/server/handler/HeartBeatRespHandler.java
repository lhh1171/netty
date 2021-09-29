package heartbeatretry.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Date;

public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Date date=new Date();
        System.out.println();
//        super.channelRead(ctx, msg);
        String heartBeat = (String) msg;
        if (heartBeat != null && heartBeat.equals("clientConnect")) {
            //检测到client连接上才发一个包
            ctx.writeAndFlush("connect");
        } else if (heartBeat != null && heartBeat.equals("clientSend")) {
            System.out.println("receive entity heartBeat");
            System.out.println("send server heartBeat to entity");
            //发送心跳
            ctx.writeAndFlush("serverSend");
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
