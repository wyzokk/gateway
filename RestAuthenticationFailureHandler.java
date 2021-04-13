/*package com.cloud.sdas.gateway.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.cloud.sdas.common.utils.JsonUtils;

import org.springframework.security.core.AuthenticationException;

@Component  
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		//request.setAttribute("message", "用户名或密码错误");
		//request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String result = "用户名或密码错误";
		String resultStr = JsonUtils.toJSon(result);
		response.getWriter().write(resultStr);
		response.getWriter().flush();
	}
  
  
  
} */