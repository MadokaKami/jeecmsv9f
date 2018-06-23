package com.jeecms.common.web.springmvc;

import java.util.List;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.jeecms.cms.web.AdminApiInterceptor;
import com.jeecms.common.util.PropertyUtils;


/**
 * 
 * @author tom
 * @date 2017年11月17日
 */
@Configuration
//@EnableWebMvc
//WebMvcConfigurerAdapter  WebMvcConfigurationSupport
public class AdminCorsConfigurerAdapter  extends    WebMvcConfigurationSupport {

	private static final String cross_prefix="Cross.domain";
	
    public void addCorsMappings(CorsRegistry registry) {
    	String pathPattern="/**";
        CorsRegistration corsRe=(CorsRegistration) registry.addMapping(pathPattern);
        corsRe.allowedHeaders("*").allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS");
        List<String>urls=getUrls();
        int urlSize=urls.size();
        if(urlSize<=0){
        	corsRe.allowedOrigins("*");
        }else if(urlSize<=1){
        	corsRe.allowedOrigins(urls.get(0));
        }else if(urlSize<=2){
        	corsRe.allowedOrigins(urls.get(0),urls.get(1));
        }else if(urlSize<=3){
        	corsRe.allowedOrigins(urls.get(0),urls.get(1),urls.get(2));
        }else if(urlSize<=4){
        	corsRe.allowedOrigins(urls.get(0),urls.get(1),urls.get(2),urls.get(3));
        }else if(urlSize<=5){
        	corsRe.allowedOrigins(urls.get(0),urls.get(1),urls.get(2),urls.get(3),urls.get(4));
        }
    }
    
	public void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(adminApiInterceptor);
		super.addInterceptors(registry);
	}
    
    
    @Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		exceptionResolvers.add(handlerExceptionResolver);
		super.configureHandlerExceptionResolvers(exceptionResolvers);
	}


	@PostConstruct
    public void init(){
		List<String>urls=propertyUtils.getList(cross_prefix);
		setUrls(urls);
    }

    @Autowired
	private AdminApiInterceptor adminApiInterceptor;
    @Autowired
    private HandlerApiExceptionResolver handlerExceptionResolver;
    @Autowired
    private PropertyUtils propertyUtils;
    private List<String>urls;

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}
	
    
}

