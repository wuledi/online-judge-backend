package com.wuledi.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 消息实体
 * 一个简单的类，把它作为信息发送：
 *
 * @author wuledi
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class KafkaMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int id; // 主键

    private String phone; // 手机号

    private Date birthDay; // 生日

}
