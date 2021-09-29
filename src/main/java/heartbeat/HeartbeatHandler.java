package heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by andilyliao on 16-6-7.
 */
public class HeartbeatHandler extends SimpleChannelInboundHandler<String> {
    // 失败计数器：未收到client端发送的ping请求
    private int unRecPingTimes = 0 ;
    private String userid;

    // 定义服务端没有收到心跳消息的最大次数
    private static final int MAX_UN_REC_PING_TIMES = 3;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("----->msg=" + msg);    //msg格式约定为"operation,userid"
        String[] args = msg.split(",");             //HEARTBEAT,a
        String msg_operation = args[0];
        String msg_userid = args[1];
        if("LOGIN".equals(msg_operation)){
            if(msg_userid!=null&&!msg.equals("")){
                userid = msg_userid;
            }
            setUserOnlineStatus(userid, true);
        }else if("HEARTBEAT".equals(msg_operation)){
            ctx.channel().writeAndFlush("success");
            // 失败计数器清零
            unRecPingTimes = 0;
        }
    }

    /*
            readerIdleTime读空闲超时时间设定，如果channelRead()方法超过readerIdleTime时间未被调用则会触发超时事件调用userEventTrigger()方法；

            writerIdleTime写空闲超时时间设定，如果write()方法超过writerIdleTime时间未被调用则会触发超时事件调用userEventTrigger()方法；*/
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                /*读超时*/
                System.out.println("===服务端===(READER_IDLE 读超时)");
                // 失败计数器次数大于等于3次的时候，关闭链接，等待client重连
                if(unRecPingTimes >= MAX_UN_REC_PING_TIMES){
                    System.out.println("===服务端===(读超时，关闭chanel)");
                    // 连续超过N次未收到client的ping消息，那么关闭该通道，等待client重连
                    ctx.channel().close();
                }else{
                    // 失败计数器加1
                    unRecPingTimes++;
                }
            } else if (event.state() == IdleState.WRITER_IDLE) {
                /*写超时*/
                System.out.println("===服务端===(WRITER_IDLE 写超时)");
            } else if (event.state() == IdleState.ALL_IDLE) {
                /*总超时*/
                System.out.println("===服务端===(ALL_IDLE 总超时)");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("错误原因："+cause.getMessage());
        ctx.channel().close();
        setUserOnlineStatus(userid, false);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client active ");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 关闭，等待重连
        System.out.println("===服务端===(客户端失效)");
        setUserOnlineStatus(userid, false);
    }

    //设置用户在线离线状态
    private void setUserOnlineStatus(String userid, boolean isOnline){
        if(userid!=null&&!userid.equals("")){
            //更新用户信息为在线状态（此处代码省略）userid
        }
    }
}