package lighttunnel.api.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.*
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslHandler
import io.netty.util.AttributeKey
import java.net.URI

class ApiClient(
    workerGroup: NioEventLoopGroup,
    private val sslContext: SslContext? = null,
    private val maxContentLength: Int = 512 * 1024
) {
    companion object {
        internal val AK_REQUEST_CALLBACK: AttributeKey<RequestCallback> =
            AttributeKey.newInstance("lighttunnel.api.client.RequestCallback")
    }

    private val bootstrap = Bootstrap()

    init {
        bootstrap
            .group(workerGroup)
            .channel(NioSocketChannel::class.java)
            .option(ChannelOption.AUTO_READ, true)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel?) {
                    ch ?: return
                    if (sslContext != null) {
                        ch.pipeline().addFirst(
                            "ssl", SslHandler(sslContext.newEngine(ch.alloc()))
                        )
                    }
                    ch.pipeline()
                        .addLast("codec", HttpClientCodec())
                        .addLast("aggregator", HttpObjectAggregator(maxContentLength))
                        .addLast("handler", ApiClientChannelHandler())
                }

            })
    }

    @Throws(Exception::class)
    fun request(url: String, method: HttpMethod, callback: RequestCallback) {
        val uri = URI.create(url)
        val request = DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, url)
        println(uri.host)
        bootstrap.connect(uri.path, if (uri.port == -1) {
            if ("https".equals(uri.scheme, true)) 443 else 80
        } else {
            uri.port
        }).sync().channel().also {
            it.attr(AK_REQUEST_CALLBACK).set(callback)
        }.writeAndFlush(request)
    }


}