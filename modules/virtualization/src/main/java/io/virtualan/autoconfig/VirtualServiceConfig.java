/*
 * Copyright 2018 Virtualan Contributors (https://virtualan.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.virtualan.autoconfig;


import io.virtualan.core.VirtualServiceInfoFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


/**
 *  This is base class to load the service virtaulized service(Virtualan).
 * 
 * @author  Elan Thangamani
 * 
 **/

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@ComponentScan(basePackages = {"io.virtualan"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class VirtualServiceConfig {

    @Bean
    public ServiceLocatorFactoryBean myFactoryServiceLocatorFactoryBean() {
        ServiceLocatorFactoryBean bean = new ServiceLocatorFactoryBean();
        bean.setServiceLocatorInterface(VirtualServiceInfoFactory.class);
        return bean;
    }

    @Value("${virtualan.task.pool.size:5}")
    private int poolSize;

    @Value("${virtualan.queue.capacity:10}")
    private int queueCapacity;

    @Bean(name="asyncWorkExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(poolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setThreadNamePrefix("vUsage-");
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    
}
