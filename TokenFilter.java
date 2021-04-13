package com.cloud.sdas.gateway.filter;

import static com.cloud.sdas.common.constants.SSID.SSID_PREFIX;
import static com.cloud.sdas.gateway.contants.Constant.LOGGER_NAME;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.cloud.sdas.common.constants.ResponseCode;
import com.cloud.sdas.common.utils.RandomUtils;
import com.cloud.sdas.common.utils.RequestUtils;
import com.cloud.sdas.gateway.config.PropertiesConfig;
import com.cloud.sdas.gateway.util.RedisUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;



public class TokenFilter extends ZuulFilter{

	Logger logger = LoggerFactory.getLogger(LOGGER_NAME);
	
	@Autowired
	private PropertiesConfig propertiesConfig;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Value("${sdas.user-mgnt-service-name}")
	private String usermgntServiceName;
	
	@Value("${sdas.toPortal}")
	private boolean toPortal;
	
	@Value("${sdas.protal-login-url}")
	private String protalLoginUrl;
	
	@Value("${sdas.protal-personal-url-prefix}")
	private String protalPersonalUrlPrefix;
	
	@Value("${sdas.swagger-uris}")
	private String[] swaggerUris;
	
	@Value("${sdas.static-subfixes}")
	private String[] staticSubfixes;
	
	@Value("${sdas.asdp-permit-uris}")
	private String[] asdpPermitUris;
	
	@Value("${sdas.static-subfixes}")
	private String[] asdpPermitPrefixUris;
	
	@Value("${sdas.asdp-app-name}")
	private String[] asdpPrefix;
	
	@Value("${sdas.asdp-permit-prefix-uris}")
	private String[] asdpPermitPrefixUris2;
	
	@Value("${sdas.static-cache-control}")
	private String staticCacheControl;
	
	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		URL sss =ctx.getRouteHost();
		HttpServletRequest request = ctx.getRequest();
		/*HttpServletResponse response = ctx.getResponse();
		setResponse(request, response);*/
		String ssid = RequestUtils.getSSID(request);
		logger.info("zuul flter: ["+ssid+"],uri:" + request.getRequestURL().toString());
		logger.info("context path: " + request.getContextPath());
		test();
		if(!canPass(ssid)){
			ctx.setSendZuulResponse(false);
			URL url =ctx.getRouteHost();
			List<ServiceInstance> instancelist =discoveryClient.getInstances(usermgntServiceName);
			if(toPortal){
				String serverName = request.getServerName();
				int port = request.getServerPort();
				String scheme = request.getScheme();
				String urlforwad = scheme + "://"+serverName+":"+port+"/"+protalLoginUrl;
				sendRedirect(ctx.getResponse(), urlforwad);
			}else{
				try {
					if(CollectionUtils.isEmpty(instancelist)){
						ctx.setSendZuulResponse(false);
						ctx.setResponseStatusCode(ResponseCode.HTTP_CODE_403);
						ctx.setResponseBody("登录服务未开启，请联系管理员");
						return null;
					}
					String serverName = request.getServerName();
					int port = request.getServerPort();
					String scheme = request.getScheme();
					String urlforwad = scheme + "://"+serverName+":"+port+"/"+usermgntServiceName+"/";
					url = new URL(urlforwad);
				} catch (MalformedURLException e) {
					ctx.setSendZuulResponse(false);
					ctx.setResponseStatusCode(ResponseCode.HTTP_CODE_403);
					ctx.setResponseBody(ExceptionUtils.getFullStackTrace(e));
					return null;
				}
				sendRedirect(ctx.getResponse(), url+"admin/login");
			}
			
		}
		return null;
	}
	
	private void test(){
		List<String> services  = discoveryClient.getServices();
		List<ServiceInstance> instancelist =discoveryClient.getInstances("asdp");
		System.out.println(111);
	}
	
	private void sendRedirect(HttpServletResponse response, String redirectUrl){
        try {
            /*response.setHeader(HttpHeaders.LOCATION, redirectUrl);
            response.setStatus(HttpStatus.MOVED_PERMANENTLY.value());*/
        	response.sendRedirect(redirectUrl);
            response.flushBuffer();
        } catch (Exception ex) {
        	logger.error("Could not redirect to: " + redirectUrl, ex);
        }
    }
	
	private boolean canPass(String ssid){
		if(propertiesConfig.isMockenable()){
			ssid = SSID_PREFIX + RandomUtils.generateSSID();
			redisUtils.set(ssid, "lixu");
		}
		Object userId = redisUtils.get(ssid); 
		if(userId != null)
			return true;
		return false;
	}

	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		HttpServletResponse response = ctx.getResponse();
		String uri = request.getRequestURI();
		setResponse(request, response);
		//静态资源
		if(isStaticResources(uri)){
			if(!isHtmRequest(uri)){
				setResponseCache(request, response);
			}
			return false;
		}
		setResponseNoCache(request, response);
		if(StringUtils.equals(uri, "/"))
			return false;
		//swagger
		boolean isSwagger = StringUtils.containsAny(uri, swaggerUris);
		if(isSwagger){
			return false;
		}
		//门户需要登录的资源校验
		if(StringUtils.contains(uri, protalPersonalUrlPrefix)){
			return true;
		}

		
		/*for(String s : staticSubfixes){
			if(StringUtils.endsWith(uri, s)){
				return false;
			}
		}*/
		//门户不需要登录资源校验
		if(RequestUtils.isFrontPublicQuery(request))
			return false;
		//asdp校验 begin
		if(StringUtils.equalsAny(uri, asdpPermitUris)){
			return false;
		}
		if(StringUtils.startsWithAny(uri, asdpPermitPrefixUris2)){
			return false;
		}
	/*	if(StringUtils.endsWithAny(uri, asdpPermitPrefixUris2)){
			return false;
		}*/
		//asdp校验 end
		return true;
	}
	
	/**
	 * 静态资源判断
	 * <br/>asdp 中的htm除外
	 * @param uri
	 * @return
	 */
	private boolean isStaticResources(String uri){
		boolean isAsdpHtm = StringUtils.containsAny(uri, asdpPrefix) && StringUtils.contains(uri, "htm");
		if(isAsdpHtm)
			return false;
		if(StringUtils.endsWithAny(uri, staticSubfixes)){
			return true;
		}
		return false;
	}

	@Override
	public int filterOrder() {
		return 0;
	}

	@Override
	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}

	private void setResponse(HttpServletRequest request, HttpServletResponse response){
    	response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");
        //response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        response.setHeader("Access-Control-Expose-Headers", "*");
        response.setHeader( "Access-Control-Allow-Credentials", "true" );
	}

	private void setResponseNoCache(HttpServletRequest request, HttpServletResponse response){
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
	}

	private void setResponseCache(HttpServletRequest request, HttpServletResponse response){
		response.setHeader("Cache-Control", staticCacheControl);
	}
	
	private boolean isHtmRequest(String uri){
		if(StringUtils.endsWith(uri, ".htm")){
			return true;
		}
		return false;
	}
}
