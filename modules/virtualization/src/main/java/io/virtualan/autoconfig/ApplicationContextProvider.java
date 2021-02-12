package io.virtualan.autoconfig;

import io.virtualan.core.util.VirtualanClassLoader;
import io.virtualan.core.util.VirtualanConfiguration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider {

  VirtualanClassLoader virtualanClassLoader;
  @Autowired
  private GenericApplicationContext genericApplicationContext;

  public VirtualanClassLoader getVirtualanClassLoader() {
    if (virtualanClassLoader == null) {
      return new VirtualanClassLoader(genericApplicationContext.getClassLoader());
    }
    return virtualanClassLoader;
  }

  private void addToClasspath(ClassLoader virtualanClassLoader)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    for (URL url : VirtualanConfiguration.getUrls()) {
      Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
      method.setAccessible(true);
      method.invoke(genericApplicationContext.getClassLoader(), new Object[]{url});
    }
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

  public void addBean(String beanName, GenericBeanDefinition beanDefinition) {
    genericApplicationContext.registerBeanDefinition(beanName, beanDefinition);
  }

  public void removeBean(String beanName) {
    genericApplicationContext.removeBeanDefinition(beanName);
  }
}