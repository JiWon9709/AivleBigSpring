/**
 *
 */
package kt.aivle.util;

import org.springframework.context.ApplicationContext;

public class BeanUtil {

  public static Object getBean(String beanName) {
    ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
    return applicationContext.getBean(beanName);
  }

}
