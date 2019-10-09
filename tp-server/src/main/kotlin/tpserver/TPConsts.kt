package tpserver

import io.netty.util.AttributeKey

internal val AK_SESSION_ID = AttributeKey.newInstance<Long>("AK_SESSION_ID")

internal val AK_TP_SESSION_POOL = AttributeKey.newInstance<TPSessionPool>("AK_TP_SESSION_POOL")

internal val AK_HTTP_HOST = AttributeKey.newInstance<String>("AK_HTTP_HOST")

internal val AK_HTTP_SKIP = AttributeKey.newInstance<Boolean>("AK_HTTP_SKIP")