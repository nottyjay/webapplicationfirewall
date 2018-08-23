package com.d3code.waf.core.util;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Aaron
 * @date 2018/8/21
 */
public class WeightedRoundRobinScheduling {

    private int currentIndex = -1;
    private int currentWeight = 0;
    public CopyOnWriteArrayList<Server> healthilyServers;
    public CopyOnWriteArrayList<Server> unhealthilyServers;
    public Map<String, Server> serverMap;

    /**
     * Calculate the greatest common divisor of two numbers
     * @param a
     * @param b
     * @return
     */
    private int gcd(int a, int b){
        BigInteger b1 = new BigInteger(String.valueOf(a));
        BigInteger b2 = new BigInteger(String.valueOf(b));
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }

    /**
     * Calculate the greatest common divisor of two numbers for servers
     * @param serverList
     * @return
     */
    private int getGCDForSevers(List<Server> serverList){
        int w = 0;
        for (int i = 0, len = serverList.size(); i < len -1; i++){
            if (w == 0){
                w = gcd(serverList.get(i).weight, serverList.get(i + 1).weight);
            }else{
                w = gcd(w, serverList.get(i + 1).weight);
            }
        }
        return w;
    }

    /**
     * Get max server's weight in server list
     * @param serverList
     * @return
     */
    private int getMaxWeightForServers(List<Server> serverList){
        int w = 0;
        for (int i = 0, len = serverList.size(); i < len - 1; i++){
            if (w == 0){
                w = Math.max(serverList.get(i).weight, serverList.get(i + 1).weight);
            }else {
                w = Math.max(w, serverList.get(i + 1).weight);
            }
        }
        return w;
    }

    /**
     * Get next healthily server
     * @return
     */
    public Server getServer(){
        if (healthilyServers.size() == 0){
            return null;
        }else if (healthilyServers.size() == 1){
            return healthilyServers.get(0);
        }else{
            while(true){
                currentIndex = ++currentIndex % healthilyServers.size();
                if (currentIndex == 0){
                    currentWeight = currentWeight - getGCDForSevers(healthilyServers);
                    if (currentWeight == 0){
                        currentWeight = getMaxWeightForServers(healthilyServers);
                        if (currentWeight == 0)
                            return null;
                    }
                }
                if (healthilyServers.get(currentIndex).weight >= currentWeight){
                    return healthilyServers.get(currentIndex);
                }
            }
        }
    }

    public WeightedRoundRobinScheduling(List<Server> healthilyServers){
        this.healthilyServers = new CopyOnWriteArrayList<Server>(healthilyServers);
        for (Server healthilyServer : healthilyServers) {
            serverMap.put(healthilyServer.getIp() + "_" + healthilyServer.getPort(), healthilyServer);
        }
    }

    public static class Server {
        String ip;
        int port;
        int weight;

        public Server(String ip, int port, int weight) {
            this.ip = ip;
            this.port = port;
            this.weight = weight;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }
}
