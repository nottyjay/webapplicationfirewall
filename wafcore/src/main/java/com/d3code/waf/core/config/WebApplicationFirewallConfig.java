package com.d3code.waf.core.config;

/**
 * @author Aaron
 * @date 2018/8/23
 */
public class WebApplicationFirewallConfig {

    private String host;
    private int port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
