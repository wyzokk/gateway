package com.cloud.sdas.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloud.sdas.gateway.filter.TokenFilter;


@Configuration
public class FilterConfig {

	@Bean
	public TokenFilter tokenFilter(){
		return new TokenFilter();
	}
}
