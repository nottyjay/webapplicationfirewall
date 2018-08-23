package com.d3code.waf.core.interfaces;

import java.util.List;

/**
 * @author Aaron
 * @date 2018/8/21
 */
public interface WafHostResolver {

    /**
     * Add new domain
     *
     * @param domain
     * @param targets
     */
    void addNewHost(String domain, List<String> targets);
}
