package com.opentool.gateway.domain.vto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * ScUser类
 * / @Author: ZenSheep
 * / @Date: 2023/12/28 14:49
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ScUser implements Serializable {
    private static final long serialVersionUID = 1283801992434446677L;

    private Long id;
    private String name;
    private Date createTime;
}
