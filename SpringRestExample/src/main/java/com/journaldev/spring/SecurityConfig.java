package com.journaldev.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
				.withUser("user").password("password").roles("USER");
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable() 
		//http://www.w3.org/TR/2010/WD-html5-diff-20101019/#changes-2010-06-24
		//csrf enabled causes put and delete to be non testable from browser as provided in the UI
		.authorizeRequests()
		.antMatchers("/**")
		.hasRole("USER")
		.anyRequest()
		.authenticated()
		.and()
		.formLogin();
	}
}