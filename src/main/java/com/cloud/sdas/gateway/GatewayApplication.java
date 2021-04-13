package com.cloud.sdas.gateway;

import static com.cloud.sdas.gateway.contants.Constant.LOGGER_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
public class GatewayApplication extends SpringBootServletInitializer{

	private static Logger logger = LoggerFactory.getLogger(LOGGER_NAME);
	
    public static void main( String[] args )
    {
        SpringApplication.run(GatewayApplication.class, args);
        logger.info("Gateway start");
    }
    
    @Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(GatewayApplication.class);
	}
}
