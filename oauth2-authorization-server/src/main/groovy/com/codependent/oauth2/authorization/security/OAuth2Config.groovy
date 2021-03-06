package com.codependent.oauth2.authorization.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@Configuration
class OAuth2Config extends AuthorizationServerConfigurerAdapter{

	@Autowired
	private AuthenticationManager authenticationManager
	
	/*
	@Bean
	public UserDetailsService userDetailsService() throws Exception {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager([])
		manager.createUser(new User("jose","mypassword", [new SimpleGrantedAuthority("ROLE_USER")]))
		manager.createUser(new User("themostuntrustedclientid","themostuntrustedclientsecret", [new SimpleGrantedAuthority("ROLE_USER")]))
		return manager
	}
	*/
	
	@Bean
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
		
		.withClient("supertrustedclientid")
			.secret("supertrustedclientsecret")
			.authorizedGrantTypes("client_credentials")
			.authorities("ROLE_USER")
			.scopes("read_users", "write_users")
			.accessTokenValiditySeconds(60)
		.and()
		//curl trustedclient:trustedclientsecret@localhost:8082/oauth/token -d grant_type=password -d username=user -d password=cec31d99-e5ee-4f1d-b9a3-8d16d0c6eeb5 -d scope=read
		.withClient("trustedclientid")
			.secret("trustedclientsecret")
            .authorizedGrantTypes("password")
            .authorities("ROLE_USER")
            .scopes("read_users", "write_users")
            .accessTokenValiditySeconds(60)
		.and()
		.withClient("untrustedclientid")
			.secret("untrustedclientsecret")
			.authorizedGrantTypes("authorization_code")
			.authorities("ROLE_USER")
			.scopes("read_users", "write_users")
			.accessTokenValiditySeconds(60)
	    .and()
			.withClient("themostuntrustedclientid")
			.secret("themostuntrustedclientsecret")
			.authorizedGrantTypes("implicit")
			.authorities("ROLE_USER")
			.scopes("read_users", "write_users")
			.accessTokenValiditySeconds(60)
		.and()
		.withClient("usersResourceProvider")
			.secret("usersResourceProviderSecret")
			.authorities("ROLE_RESOURCE_PROVIDER")
	
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints)	throws Exception {
		endpoints.authenticationManager(this.authenticationManager);
	}
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		//security.checkTokenAccess('hasRole("ROLE_RESOURCE_PROVIDER")')
		security.checkTokenAccess('isAuthenticated()')
	}

}
