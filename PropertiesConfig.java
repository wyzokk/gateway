package com.cloud.sdas.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiesConfig {

	@Value("${sdas.mockenable}")
	private boolean mockenable;

	@Value("${spring.application.name}")
	private String appName;
	
	public boolean isMockenable() {
		return mockenable;
	}

	public void setMockenable(boolean mockenable) {
		this.mockenable = mockenable;
	}
	
	

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	
	
}
