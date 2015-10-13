package com.tcs.destination.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.stereotype.Component;

@Component
public class PropertyUtil extends PropertyPlaceholderConfigurer {

    private static Map<String, String> propertiesMap;
    private int springSystemPropertiesMode = SYSTEM_PROPERTIES_MODE_NEVER;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
        super.processProperties(beanFactory, props);

        propertiesMap = (propertiesMap == null) ? new HashMap<String, String>(): propertiesMap;
        for (Object key : props.keySet()) {
            String valueStr = resolvePlaceholder((String)key, props, springSystemPropertiesMode);
            propertiesMap.put((String)key, valueStr);
        }
    }
    
    public static String getProperty(String name) {
        return propertiesMap.get(name);
    }

    @Override
	public void setIgnoreResourceNotFound(boolean ignoreResourceNotFound) {
    	super.setIgnoreResourceNotFound(ignoreResourceNotFound);
	}

    @Override
	public void setIgnoreUnresolvablePlaceholders(
			boolean ignoreUnresolvablePlaceholders) {
    	super.setIgnoreUnresolvablePlaceholders(ignoreUnresolvablePlaceholders);
	}

    @Override
	public void setOrder(int order) {
    	super.setOrder(order);
	}

    @Override
    public void setSystemPropertiesMode(int systemPropertiesMode) {
        super.setSystemPropertiesMode(systemPropertiesMode);
        this.springSystemPropertiesMode = systemPropertiesMode;
    }
    
}

