package lighttunnel.proto

import lighttunnel.util.LongUtil

class ProtoMessage(
    val cmd: ProtoCommand,
    val head: ByteArray = ProtoConsts.emptyBytes,
    val data: ByteArray = ProtoConsts.emptyBytes
) {

    val tunnelId by lazy { LongUtil.fromBytes(head, 0) }
    val sessionId by lazy { LongUtil.fromBytes(head, 8) }

    override fun toString(): String {
        return "ProtoMessage(cmd=$cmd, head.length=${head.size}, data.length=${data.size})"
    }


}