package com.d3code.waf.core;

import com.d3code.waf.core.config.WebApplicationFirewallConfig;
import com.d3code.waf.core.util.NetUtils;
import com.d3code.waf.core.util.WAFUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.collections.ListUtils;
import org.littleshoot.proxy.*;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.impl.ThreadPoolConfiguration;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Aaron
 * @date 2018/8/21
 */
public class WebApplicationBootstrap {

    private static WebApplicationBootstrap instance;
    private HttpProxyServerBootstrap httpProxyServerBootstrap;
    private List<HttpFiltersSource> httpFiltersSources = new ArrayList<HttpFiltersSource>();
    private List<ActivityTracker> activityTrackers = new ArrayList<ActivityTracker>();
    private HostResolverImpl hostResolver;

    public WebApplicationBootstrap(WebApplicationFirewallConfig config){
        httpProxyServerBootstrap = DefaultHttpProxyServer.bootstrap().withAddress(new InetSocketAddress(config.getHost(), config.getPort()));
        ThreadPoolConfiguration threadPoolConfiguration = new ThreadPoolConfiguration();
        threadPoolConfiguration.withAcceptorThreads(1);
        threadPoolConfiguration.withClientToProxyWorkerThreads(100);
        threadPoolConfiguration.withProxyToServerWorkerThreads(100);

        httpProxyServerBootstrap.withAllowRequestToOriginServer(true)
                .withProxyAlias("com.d3code.waf")
                .withThreadPoolConfiguration(threadPoolConfiguration)
                .withServerResolver(hostResolver)
                .plusActivityTracker(
                        new ActivityTrackerAdapter(){
                            @Override
                            public void requestReceivedFromClient(FlowContext flowContext, HttpRequest httpRequest) {
                                // Add X-Forwarded-For header
                                String xffKey = "X-Forwarded-For";
                                StringBuffer xff = new StringBuffer();
                                List<String> headerValuesXff = WAFUtil.getHeaderValues(httpRequest, xffKey);
                                if (headerValuesXff.size() > 0 && headerValuesXff.get(0) != null){
                                    xff.append(headerValuesXff.get(0)).append(", ");
                                }
                                xff.append(NetUtils.getLocalHost());
                                httpRequest.headers().set(xffKey, xff.toString());

                                // Add X-Real-IP header
                                String xriKey = "X-Real-IP";
                                List<String> headerValues2 = WAFUtil.getHeaderValues(httpRequest, xriKey);
                                if (headerValues2.size() == 0) {
                                    String remoteAddress = flowContext.getClientAddress().getAddress().getHostAddress();
                                    httpRequest.headers().add(xriKey, remoteAddress);
                                }
                            }
                        }
                )
                .withFiltersSource(
                        new HttpFiltersSourceAdapter(){
                            @Override
                            public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                                return new HttpFilterAdapterImpl(originalRequest, ctx);
                            }
                        }
                );
    }

    public WebApplicationBootstrap addHttpFiltersSource(HttpFiltersSource... httpFiltersSources){
        ListUtils.union(this.httpFiltersSources, Arrays.asList(httpFiltersSources));
        return this;
    }

    public WebApplicationBootstrap addActivityTracker(ActivityTracker... activityTrackers){
        ListUtils.union(this.activityTrackers, Arrays.asList(activityTrackers));
        return this;
    }

    public WebApplicationBootstrap setHostResolver(HostResolverImpl hostResolver) {
        this.hostResolver = hostResolver;
        return this;
    }

    public void start(){
        for (ActivityTracker activityTracker : activityTrackers) {
            httpProxyServerBootstrap.plusActivityTracker(activityTracker);
        }
        for (HttpFiltersSource httpFiltersSource : httpFiltersSources) {
            httpProxyServerBootstrap.withFiltersSource(httpFiltersSource);
        }
        httpProxyServerBootstrap.start();
    }

}
