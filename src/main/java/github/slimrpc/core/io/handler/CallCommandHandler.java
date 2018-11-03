package github.slimrpc.core.io.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import github.slimrpc.core.domain.Cookie;
import github.slimrpc.core.io.cmd.CallCommand;
import github.slimrpc.core.io.cmd.ExceptionErrorCommand;
import github.slimrpc.core.io.cmd.ResultCommand;
import github.slimrpc.core.io.manager.ServerCookieManager;
import github.slimrpc.core.metadata.MetaHolder;
import github.slimrpc.core.metadata.ProviderMeta;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * client 执行RPC调用
 *
 */
public class CallCommandHandler {
    static Logger log = LoggerFactory.getLogger(CallCommandHandler.class);

    public static void processCommand(MetaHolder metaHolder, ChannelHandlerContext channelCtx, JSONArray jsonArray) {
        CallCommand callCmd = new CallCommand();
        try {
            //数据解析
            long requestId = jsonArray.getLongValue(1);
            JSONObject options = jsonArray.getJSONObject(2);
            String interfaceName = jsonArray.getString(3);
            String methodSign = jsonArray.getString(4);
            String paramValues = jsonArray.getString(5);

            //封装
            callCmd.setRequestId(requestId);
            callCmd.setOptions(options);
            callCmd.setItfName(interfaceName);
            callCmd.setMethodSign(methodSign);
            //调用
            Map<String, ProviderMeta> metaMap = metaHolder.getProviderHolder().get(interfaceName);
            if (metaMap == null || metaMap.size() == 0) {
                log.info("收到客户端调用接口:{},未找到对应的提供bean,请确认是否未配置提供的bean!", interfaceName);
                ExceptionErrorCommand errCmd = new ExceptionErrorCommand(callCmd.getRequestId(),new Throwable("未找到对应的提供bean,请确认是否未配置提供的bean!") );
                channelCtx.writeAndFlush(errCmd) ;
                return;
            }
            ProviderMeta providerMeta = metaMap.get(methodSign);
            Method method = providerMeta.getMethod();
            Object[] args = JSONObject.parseArray(paramValues, method.getGenericParameterTypes()).toArray();

            JSONArray cookieArray = callCmd.getOptions().getJSONArray("Cookie");
            if (cookieArray != null) {
                Map<String, Cookie> receiveCookieMap = new ConcurrentHashMap<>(8);
                for (int i = 0; i < cookieArray.size(); i++) {
                    Cookie cookie = cookieArray.getObject(i, Cookie.class);
                    receiveCookieMap.put(cookie.getName(), cookie);
                }
                ServerCookieManager.setReceiveCookieMap(receiveCookieMap);
            } else {
                ServerCookieManager.setReceiveCookieMap(new ConcurrentHashMap<>(1));
            }

            Object result = method.invoke(providerMeta.getServiceImpl(), args);

            ServerCookieManager.clearReceiveCookieMap();

            ResultCommand resultCmd = new ResultCommand(callCmd.getRequestId(), result);
            Map<String, Cookie> setCookieMap = ServerCookieManager.getSetCookieMap();
            if (setCookieMap != null && setCookieMap.size() > 0) {
                resultCmd.getDetails().put("Set-Cookie", setCookieMap.values().toArray());
                ServerCookieManager.clearSetCookieMap();
            }

            channelCtx.writeAndFlush(resultCmd);
        } catch (Throwable e) {
            log.error("{procedureUri:'" + callCmd.getItfName() + callCmd.getMethodSign() + "'}", e);
            ExceptionErrorCommand errCmd = new ExceptionErrorCommand(callCmd.getRequestId(), e);// 需要将e堆栈展开成字符串
            channelCtx.writeAndFlush(errCmd);
        }

    }
}
