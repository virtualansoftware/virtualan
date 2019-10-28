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


import io.virtualan.controller.VirtualServiceController;
import io.virtualan.core.VirtualServiceInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 *  This is base class to load the service virtaulized service(Virtualan).
 * 
 * @author  Elan Thangamani
 * 
 **/

@Configuration
@EnableAspectJAutoProxy
@EnableSwagger2
@EnableAsync
@ComponentScan(basePackages = {"io.virtualan"})
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
