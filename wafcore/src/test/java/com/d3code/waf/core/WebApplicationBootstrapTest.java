package com.d3code.waf.core;

import com.d3code.waf.core.config.WebApplicationFirewallConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebApplicationBootstrapTest {

    public static void main(String[] args) {
        WebApplicationFirewallConfig config = new WebApplicationFirewallConfig();
        config.setHost("127.0.0.1");
        config.setPort(8888);

        WebApplicationBootstrap bootstrap = new WebApplicationBootstrap(config);

        Map<String, List<String>> hostResolverMap = new HashMap<String, List<String>>();
        String[] targets = new String[]{"220.181.57.216:80"};
        hostResolverMap.put("www.baidu.com", Arrays.asList(targets));
        HostResolverImpl hostResolver = new HostResolverImpl(hostResolverMap);

        bootstrap.start();
    }
}
