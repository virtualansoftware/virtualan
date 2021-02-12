package io.virtualan.autoconfig;

import io.virtualan.core.util.VirtualanClassLoader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
public class ApplicationContextProvider  {

  @Autowired
  private GenericApplicationContext genericApplicationContext;
  VirtualanClassLoader virtualanClassLoader;

  public VirtualanClassLoader getVirtualanClassLoader() {
    if(virtualanClassLoader == null){
      return new VirtualanClassLoader(genericApplicationContext.getClassLoader());
    }
    return virtualanClassLoader;
  }

  public void setVirtualanClassLoader(VirtualanClassLoader virtualanClassLoader) {
    this.virtualanClassLoader = virtualanClassLoader;
    genericApplicationContext.setClassLoader(virtualanClassLoader);
  }

  public void classLoader(ClassLoader classLoader) {
    genericApplicationContext.setClassLoader(classLoader);
  }

  public ClassLoader getClassLoader() {
    return genericApplicationContext.getClassLoader();
  }

  public boolean containsBean(String beanName) {
    return genericApplicationContext.containsBean(beanName);
  }

  public void addBean(String beanName, GenericBeanDefinition beanObject) {
    genericApplicationContext.registerBeanDefinition(beanName, beanObject);
  }

  public void removeBean(String beanName) {
    genericApplicationContext.removeBeanDefinition(beanName);
  }
}