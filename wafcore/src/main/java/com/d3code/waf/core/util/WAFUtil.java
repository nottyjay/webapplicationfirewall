package com.d3code.waf.core.util;

import com.google.common.collect.Lists;
import io.netty.handler.codec.http.HttpMessage;

import java.util.List;
import java.util.Map;

/**
 * @author Aaron
 * @date 2018/8/23
 */
public class WAFUtil {

    /**
     * RFC7230/RFC7231/RFC7232/RFC7233/RFC7234
     * Each header field consists of a case-insensitive field name followed
     * by a colon (":"), optional leading whitespace, the field value, and
     * optional trailing whitespace.
     *
     * @param httpMessage
     * @param headerName
     * @return headerValue
     */
    public static List<String> getHeaderValues(HttpMessage httpMessage, String headerName){
        List<String> list = Lists.newArrayList();
        for (Map.Entry<String, String> header : httpMessage.headers().entries()) {
            if(header.getKey().toLowerCase().equals(headerName.toLowerCase())){
                list.add(header.getValue());
            }
        }
        return list;
    }
}
