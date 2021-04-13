package com.cloud.sdas.gateway.api;

import static com.cloud.sdas.gateway.contants.Constant.LOGGER_NAME;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.sdas.gateway.config.PropertiesConfig;
import com.netflix.discovery.DiscoveryManager;

@RestController
@RequestMapping("/sdas/admin/gateway")
public class GatewayAPI {
	Logger logger = LoggerFactory.getLogger(LOGGER_NAME);
	
	private String success= "success";
	
	@Autowired
	private DiscoveryClient client;
	
	@Autowired
	private PropertiesConfig propertiesConfig;
	
	@SuppressWarnings("all")
	@RequestMapping("/offline")
	public String offline(){
		logger.info(propertiesConfig.getAppName() +" offline");
		DiscoveryManager.getInstance().shutdownComponent();
		return success;
	}
	
	@RequestMapping("/list")
	public List<ServiceInstance> list(){
		logger.info(propertiesConfig.getAppName() + " list instanceInfo");
		return client.getInstances(propertiesConfig.getAppName());
	}
}
