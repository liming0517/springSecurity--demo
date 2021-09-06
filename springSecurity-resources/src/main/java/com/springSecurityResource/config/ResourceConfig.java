package com.springSecurityResource.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * 资源Server端
 */
@Configuration
@EnableResourceServer
public class ResourceConfig extends ResourceServerConfigurerAdapter {

    /*连接数据库对象*/
    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    /*密码*/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*tokenstore 用于存取token*/
    @Bean
    public JdbcTokenStore jdbcTokenStore(DataSource datasorse) {
        return new JdbcTokenStore(datasorse);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //设置创建session策略
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        //放行的 url
        http.authorizeRequests().antMatchers("/login").permitAll();//
        //所有请求必须授权
        http.authorizeRequests().anyRequest().authenticated();
    }

    //需要保护的资源id
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId("datacenter").stateless(true);//resourceid 一个资源服务器对应一个，用于设置认证服务器能够访问的资源服务器有哪些
        resources.tokenStore(this.jdbcTokenStore(dataSource));//读取jdbc内容
    }

    /*自己定义token的过期时间等 ，primary必填*/
/*   @Primary
    @Bean
    public ResourceServerTokenServices remoteTokenServices() {
        final RemoteTokenServices tokenServices = new RemoteTokenServices();
        //设置授权服务器check_token端点完整地址
        tokenServices.setCheckTokenEndpointUrl("http://localhost:8080/oauth/check_token");
        //设置客户端id与secret，注意：client_secret值不能使用passwordEncoder加密！
        tokenServices.setClientId(mayiktAppId);
        tokenServices.setClientSecret(mayiktAppSecret);
        return tokenServices;

*//*        DefaultTokenServices services = new DefaultTokenServices();
        return services;*//*
    }*/

}