package com.lhiot.mall.wholesale.base;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

public class IdGenerator implements Configurable, IdentifierGenerator {

    public static final String STRATEGY = "com.lhiot.mall.wholesale.base.IdGenerator";

    private SnowflakeId snowflakeId;

    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {
        String dataCenterId = properties.getProperty("dataCenterId");
        String workerId = properties.getProperty("workerId");
        this.snowflakeId = new SnowflakeId(Long.valueOf(workerId), Long.valueOf(dataCenterId));
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        return this.snowflakeId.longId();
    }
}
