package com.d3code.waf.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.HttpFiltersAdapter;

/**
 * @author Aaron
 * @date 2018/8/23
 */
public class HttpFilterAdapterImpl extends HttpFiltersAdapter {

    public HttpFilterAdapterImpl(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        super(originalRequest, ctx);
    }
}
