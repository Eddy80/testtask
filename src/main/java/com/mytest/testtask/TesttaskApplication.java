package com.mytest.testtask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class TesttaskApplication {

	public static final Logger log = LoggerFactory.getLogger(TesttaskApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(TesttaskApplication.class, args);
		log.info("Notification Web API started ...");
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).build();
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope =
				new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
	}

	@Bean
	public Docket SwaggerConfiguration(){
		return new Docket(DocumentationType.SWAGGER_2)
				.securityContexts(Arrays.asList(securityContext()))
				//.securitySchemes(Arrays.asList(apiKey()))
				//.securitySchemes(Arrays.asList(jwtScheme()))
				.select()
				//.paths(PathSelectors.ant("/api/**"))
				.paths(PathSelectors.ant("/AdyRestApi/api/**")) // BUNU - Serverə atanda açmalıyam
				.apis(RequestHandlerSelectors.basePackage("com.vizier"))
				.build();
				//.apiInfo(apiDetails());
	}

}
