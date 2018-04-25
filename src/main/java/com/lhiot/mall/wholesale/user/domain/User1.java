package com.lhiot.mall.wholesale.user.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@ApiModel
@NoArgsConstructor
public class User1 {

    private long id;

    private String name;

    private int age;

    private Sex sex;

    public enum Sex {
        MALE, FEMALE
    }
    private String nickname;
}
