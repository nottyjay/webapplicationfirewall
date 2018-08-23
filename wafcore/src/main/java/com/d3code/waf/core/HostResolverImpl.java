package com.d3code.waf.core;

import com.d3code.waf.core.interfaces.WafHostResolver;
import com.d3code.waf.core.util.WeightedRoundRobinScheduling;
import org.littleshoot.proxy.HostResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Aaron
 * @date 2018/8/21
 */
public class HostResolverImpl implements HostResolver, WafHostResolver {
    private static final Logger LOG = LoggerFactory.getLogger(HostResolverImpl.class);

    private Map<String, WeightedRoundRobinScheduling> serverMap = new HashMap<String, WeightedRoundRobinScheduling>();

    public HostResolverImpl(Map<String, List<String>> hostResolverMap){
        for (Map.Entry<String, List<String>> hostResolver : hostResolverMap.entrySet()) {
            addNewHost(hostResolver.getKey(), hostResolver.getValue());
        }
    }

    public void addNewHost(String domain, List<String> targetServers) {
        List<WeightedRoundRobinScheduling.Server> serverList = new ArrayList<WeightedRoundRobinScheduling.Server>();
        for (String targetServer : targetServers) {
            int weight = 1;
            String[] serverAndPort = targetServer.split(":");
            if(serverAndPort.length < 2 || serverAndPort.length > 3){
                continue;
            }else if (serverAndPort.length == 3){
                weight = Integer.valueOf(serverAndPort[2]);
            }
            WeightedRoundRobinScheduling.Server server = new WeightedRoundRobinScheduling.Server(serverAndPort[0], Integer.valueOf(serverAndPort[1]), weight);
            serverList.add(server);
        }
        serverMap.put(domain, new WeightedRoundRobinScheduling(serverList));
    }

    public InetSocketAddress resolve(String host, int port) throws UnknownHostException {
//        String key =
        return null;
    }
}
