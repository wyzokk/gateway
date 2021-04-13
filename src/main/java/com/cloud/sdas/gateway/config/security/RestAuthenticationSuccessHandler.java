/*package com.cloud.sdas.gateway.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.cloud.sdas.common.utils.JsonUtils;
import com.cloud.sdas.common.utils.RandomUtils;

import static com.cloud.sdas.common.constants.SSID.*;
import static com.cloud.sdas.common.constants.Redis.*;

@Component
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	private RedisUtils redisUtils;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String ssid = SSID_PREFIX + RandomUtils.generateSSID();
		BaseApiResponse<String> result = new BaseApiResponse<String>(true, null, ssid);
		//redisUtils.set(ssid, value, expireTime);
		WebUserDetails userdetails =(WebUserDetails) authentication.getPrincipal();
		redisUtils.set(ssid, JsonUtils.toJSon(userdetails), REDIS_SSID_TIMEOUT);
		String resultStr = JsonUtils.toJSon(result);
		response.getWriter().write(resultStr);
		response.getWriter().flush();
	}

}*/