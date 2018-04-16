package com.lhiot.mall.wholesale.user.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Data
@ToString
public class SearchUser {

    private Long[] ids;
    private String likeName;

    public void setIds(String ids){
        String[] array = StringUtils.tokenizeToStringArray(ids, ",");
        if (!ObjectUtils.isEmpty(array)){
            this.ids = new Long[array.length];
            for (int i = 0; i < array.length; i++) {
                this.ids[i] = Long.valueOf(array[i]);
            }
        }
    }
}
