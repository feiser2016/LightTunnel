package com.tuuzed.tunnel.client;

import com.tuuzed.tunnel.common.logging.Logger;
import com.tuuzed.tunnel.common.logging.LoggerFactory;
import com.tuuzed.tunnel.common.protocol.TunnelMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.tuuzed.tunnel.common.protocol.TunnelConstants.*;

/**
 * 本地连接数据通道处理器
 */
public class LocalTunnelHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(LocalTunnelHandler.class);

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        long sessionToken = ctx.channel().attr(ATTR_SESSION_TOKEN).get();
        LocalTunnel.getInstance().removeLocalTunnelChannel(sessionToken);
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        int length = msg.readableBytes();
        byte[] data = new byte[length];
        msg.readBytes(data);
        final Channel tunnelClientChannel = ctx.channel().attr(ATTR_NEXT_CHANNEL).get();
        final long tunnelToken = ctx.channel().attr(ATTR_TUNNEL_TOKEN).get();
        final long sessionToken = ctx.channel().attr(ATTR_SESSION_TOKEN).get();
        logger.info("nextChannel: {}", tunnelClientChannel);
        tunnelClientChannel.writeAndFlush(
                TunnelMessage.newInstance(MESSAGE_TYPE_TRANSFER)
                        .setHead(Unpooled.copyLong(tunnelToken, sessionToken).array())
                        .setData(data)
        );
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        final Channel tunnelClientChannel = ctx.channel().attr(ATTR_NEXT_CHANNEL).get();
        tunnelClientChannel.config().setOption(ChannelOption.AUTO_READ, ctx.channel().isWritable());
        super.channelWritabilityChanged(ctx);
    }
}