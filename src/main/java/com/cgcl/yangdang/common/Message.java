package com.cgcl.yangdang.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * HTTP 返回信息的封装
 * </p>
 *
 * @author Liu Cong
 * @since 2019-04-04
 */
@Data
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    //状态码
    private int code;
    //提示信息
    private String message;

    //用户要返回给浏览器的数据
    private Map<String, Object> extend = new HashMap<>();

    public static Message success() {
        Message result = new Message();
        result.setCode(100);
        result.setMessage("success");
        return result;
    }

    public static Message fail() {
        Message result = new Message();
        result.setCode(200);
        result.setMessage("error");
        return result;
    }

    public Message add(String key, Object value) {
        this.getExtend().put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}
