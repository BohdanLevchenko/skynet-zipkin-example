package skynet;

import com.github.kristofa.brave.LocalTracer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class TracedBeanEnhancer implements BeanPostProcessor, ApplicationContextAware {
	private final Map<String, AbstractMap.SimpleEntry<Traced, String>> tracedBeans = new HashMap<>();
	private ApplicationContext applicationContext;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		final Class<?> beanClass = bean.getClass();
		final Method[] methods   = beanClass.getMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(Traced.class)) {
				tracedBeans.put(beanName, new AbstractMap.SimpleEntry<>(method.getAnnotation(Traced.class), method.getName()));
			}
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (tracedBeans.containsKey(beanName)) {
			final AbstractMap.SimpleEntry<Traced, String> traceInformation = tracedBeans.get(beanName);
			final Class<?> beanClass = bean.getClass();
			return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					if (method.getName().equals(traceInformation.getValue())) {
						final String spanName = traceInformation.getKey().value();
						final LocalTracer localTracer = applicationContext.getBean(LocalTracer.class);
						localTracer.startNewSpan(spanName, "");
						try {
							return method.invoke(bean, args);
						} finally {
							localTracer.finishSpan();
						}
					}
					return method.invoke(bean, args);
				}
			});
		}

		return bean;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
