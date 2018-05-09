package com.lhiot.mall.wholesale;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lhiot.mall.wholesale.QueueProperties.PREFIX;

@Data
@ConfigurationProperties(prefix = PREFIX)
public class QueueProperties {
    static final String PREFIX = "wholesale-mall.rabbitmq";

    private Map<String, List<String>> fanoutQueue = new HashMap<>(0);
//    @Data
//    public static final class FanoutQueue{
//        private Map<String, List<String>> publisher = new HashMap<>(0);
//    }

    private Map<String,Map<String,String>> directQueue = new HashMap<>(0);

//    @Data
//    public static final class DirectQueue{
//        private Map<String,Map<String,String>> publisher =new HashMap<>(0);
//    }
}
