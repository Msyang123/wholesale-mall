package com.lhiot.mall.wholesale.user.pojo;

import com.lhiot.mall.wholesale.base.IdGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@ToString
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(generator = "snowflakeId")
    @GenericGenerator(name = "snowflakeId", strategy = IdGenerator.STRATEGY, parameters = {
            @Parameter(name = "dataCenterId", value = "1"),
            @Parameter(name = "workerId", value = "1")
    })
    private Long id;

    private String name;

    private Integer age;

    private Sex sex;

    public enum Sex{
        MALE, FEMALE
    }
}
