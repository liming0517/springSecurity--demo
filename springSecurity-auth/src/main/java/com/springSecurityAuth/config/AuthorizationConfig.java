package com.springSecurityAuth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * 认证授权Server端
 */
@Component
@EnableAuthorizationServer
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {


    /*数据库连接工具：只需要配置yml即可*/
    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;
/*  redis roken使用
    @Autowired
    private TokenStore redisTokenStore;*/

    //@Autowired redis 使用的配置类
/*    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;*/

    /*jdbc token store*/
    @Bean
    public TokenStore tokenStore() {
        // return new InMemoryTokenStore(); //使用内存中的 token store
        JdbcTokenStore jdbcTokenStore = new JdbcTokenStore(dataSource);

        return new JdbcTokenStore(dataSource); /// 使用Jdbctoken store
    }

    /*客户端数据库实体*/
    @Bean
    public ClientDetailsService clientDetails() {
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        jdbcClientDetailsService.setPasswordEncoder(new BCryptPasswordEncoder());
        return jdbcClientDetailsService;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //允许表单提交
        security.allowFormAuthenticationForClients();
        security.checkTokenAccess("permitAll()");
        security.tokenKeyAccess("permitAll()");
        security.passwordEncoder(new BCryptPasswordEncoder());//使用BCry密码解密
        //security.passwordEncoder(NoOpPasswordEncoder.getInstance());//不需要加密
    }
    /**
     * 针对端点的配置
     *
     * @param authorizationServerEndpointsConfigurer
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer authorizationServerEndpointsConfigurer) throws Exception {
        //authorizationServerEndpointsConfigurer.authenticationManager(authenticationConfiguration.getAuthenticationManager());
       /* authorizationServerEndpointsConfigurer.tokenStore(redisTokenStore)  //将Token存放到Redis中
                .authenticationManager(authenticationManager);*/
        // 设置令牌存储在数据库
        authorizationServerEndpointsConfigurer.tokenStore(tokenStore());
    }
    /**
     * appid mayikt secret= 123456
     * 针对第三方客户端的有关配置
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        clients.withClientDetails(this.clientDetails());//存储到数据库

/*                .withClient("liming")
                // appsecret
                .secret(passwordEncoder.encode("123456"))//secret加密
                // 模式
                .authorizedGrantTypes("client_credentials", "refresh_token")
                // 作用域
                .scopes("all")
                // 资源的id
                .resourceIds("mayikt_resource·")
                .accessTokenValiditySeconds(60);//访问token过期时间*/
                //.refreshTokenValiditySeconds(120); //刷新token过期时间
    }

    /**
     * TokenStore   负责令牌的存取
     *
     * @param redisConnectionFactory
     * @return
     */
/* redis要使用的 tokenstore
@Bean
    public TokenStore redisTokenStore(RedisConnectionFactory redisConnectionFactory) {
       // return new RedisTokenStore(redisConnectionFactory); //使用redis tokenstore
        return new JdbcTokenStore(dataSource); //使用jdbc token store
    }*/

    //  生成token的处理
/*    @Primary
    @Bean
    public DefaultTokenServices defaultTokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(redisTokenStore);
        // 是否支持 refreshToken
        tokenServices.setSupportRefreshToken(true);
        // 是否复用 refreshToken
        tokenServices.setReuseRefreshToken(true);
        // tokenServices.setTokenEnhancer(tokenEnhancer());
        // token有效期自定义设置，默认12小时
        tokenServices.setAccessTokenValiditySeconds(60 * 60 * 12);
        //默认30天，这里修改
        tokenServices.setRefreshTokenValiditySeconds(60 * 60 * 24 * 7);
        return tokenServices;
    }*/
}