package lighttunnel.server.tcp

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import lighttunnel.server.SessionChannels

class TcpDescriptor(
    val addr: String?,
    val port: Int,
    val sessionPool: SessionChannels
) {
    private var bindChannelFuture: ChannelFuture? = null

    @Throws(Exception::class)
    fun open(serverBootstrap: ServerBootstrap) {
        bindChannelFuture = if (addr != null) serverBootstrap.bind(addr, port)
        else serverBootstrap.bind(port)
    }

    fun close() {
        bindChannelFuture?.channel()?.close()
        sessionPool.destroy()
    }
}