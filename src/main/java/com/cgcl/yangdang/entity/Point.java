package com.cgcl.yangdang.entity;

import com.cgcl.yangdang.common.JsonUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author Liu Cong
 * @since 2019-04-07
 */
@Data
public class Point implements Serializable {
    private static final long serialVersionUID = 1L;

    public Long id;

    private String name;

    private Long x;

    private Double y;

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}
