package github.slimrpc.core.io.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import github.slimrpc.core.metadata.MetaHolder;
import github.slimrpc.core.rpc.CallResultFuture;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * client 解析server端返回的结果
 *
 */
public class ResultCommandHandler {
    static Logger log = LoggerFactory.getLogger(ResultCommandHandler.class);

    public static void processCommand(MetaHolder metaHolder, ChannelHandlerContext channelCtx, JSONArray jsonArray) {
        long requestId = jsonArray.getLongValue(1);
        CallResultFuture callResult = metaHolder.getRequestPool().get(requestId);
        if (callResult == null) {
            log.error("{msg:'receive timeout result, maybe server method too slow', requestId:" + requestId + "}");
            return;
        }
        callResult.setDetail(jsonArray.getJSONObject(2));

        if (callResult.getReturnType() == null) {
            callResult.returnWithVoid();
        } else {
            Object obj = jsonArray.getJSONArray(3).get(0);
            if (JSONObject.class.isAssignableFrom(obj.getClass())){
                JSONObject jsonObj = (JSONObject) obj;
                obj = jsonObj.toJavaObject(callResult.getReturnType());
            }
            Object result = obj;
            callResult.putResultAndReturn(result);
        }
    }
}
