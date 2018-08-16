/********************************************************************************* *
 * Purpose: To create an implementation to GoogleKeep(ToDoApplication) and implementing microservices. This application is basically storing and maintaining the notes.
 * Creating an interceptor which is called before command goes to controller.This is for configuring all the interceptors
 * @author Saurav Manchanda
 * @version 1.0
 * @since 30/07/2018
 *********************************************************************************/
package com.bridgelabz.userservice.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bridgelabz.userservice.utilservice.interceptor.LoggerInterceptor;

/**
 * @author Saurav
 *         <p>
 *         This is an Interceptor config class which is used to configure the
 *         interceptors in the ToDoApplication
 *         </p>
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
	@Autowired
	LoggerInterceptor loggerInterceptor;

	public void addInterceptors(InterceptorRegistry registry) {
		/**
		 * For adding the loggerInterceptor in the application
		 */
		registry.addInterceptor(loggerInterceptor);
	}
}
