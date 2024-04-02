package com.chat.talk.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;
    @Autowired
    AuthSuccessHandler authSuccessHandler;
    @Autowired
    SessionRegistry sessionRegistry;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
    		.csrf().disable().authorizeRequests()
                .antMatchers("/", "/account/**", "/css/**", "/js/**", "/images/**", "/imgchange", "/msgchange").permitAll() //무조건 접근 허가
                .antMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/account/login") //로그인 관련 페이지 설정..
           		.loginProcessingUrl("/loginProcess")
                .usernameParameter("username")
                .passwordParameter("password")
//				.defaultSuccessUrl("/")  
                .successHandler(authSuccessHandler) //로그인 성공 후 해당 handler 가기
                .permitAll()
                .and()
            .logout()
	            .deleteCookies("remove")
	            .invalidateHttpSession(true)
                .permitAll()
                .and()
        	.sessionManagement()
		        .maximumSessions(1)
		        .maxSessionsPreventsLogin(true)//중복 로그인 방지-true:나중 접속자 로그인 방지,false:먼저 접속자 logout처리
		        //true:정상적으로 Logout 하지 않고 브라우저 종료 할 때 그 누구도 로그인 하지 못하는 상황이 발생할 수 있으니, 몇 분 이상 action이 없는 사용자의 경우 강제 session kill로직 필요
	            .sessionRegistry(sessionRegistry())
	            //이거 안 하면 사용자가 Logout 후 다시 Login 할 때 "Maximum sessions of 1 for this principal exceeded" 에러를 발생시키며 로그인 되지 않습니다.
		        .and()
		       .invalidSessionUrl("/");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery("select username, password, enabled "
                        + "from userr "
                        + "where username = ?")
                .authoritiesByUsernameQuery("select u.username, r.name "
                        + "from user_role ur inner join userr u on ur.user_id = u.id "
                        + "inner join role r on ur.role_id = r.id "
                        + "where u.username = ?");
    }
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationSuccessHandler successHandler() {
      return new AuthSuccessHandler();
    }

    // logout 후 login할 때 정상동작을 위함
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }// Register HttpSessionEventPublisher

	// was가 여러개 있을 때(session clustering)
    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }
}