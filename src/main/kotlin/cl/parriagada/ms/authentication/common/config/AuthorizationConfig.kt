package cl.parriagada.ms.authentication.common.config

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.approval.ApprovalStore
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler
import org.springframework.security.oauth2.provider.approval.InMemoryApprovalStore
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory
import java.security.KeyPair
import java.security.interfaces.RSAPublicKey
import java.util.Collections

@Configuration
@EnableAuthorizationServer
class AuthorizationConfig(@Autowired
                          private val clientDetailsService: ClientDetailsService,
                          @Autowired
                          private val authenticationManager: AuthenticationManager) : AuthorizationServerConfigurerAdapter() {

    private val KEY_STORE_FILE = "mytest.jks"
    private val KEY_STORE_PASSWORD = "mypass"
    private val KEY_ALIAS = "mytest"
    private val JWK_KID = "my-key-id"

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.inMemory()
                .withClient("my-client")
                .authorities("ADMIN")
                .authorizedGrantTypes("authorization_code",
                        "client_credentials", "password")
                .scopes("message.read", "message.write")
                .secret("{noop}my-secret")
                .accessTokenValiditySeconds(20)
                .autoApprove(true)
                .redirectUris("http://localhost:8081/authorized");
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints
                .authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                .userApprovalHandler(userApprovalHandler())
                .accessTokenConverter(accessTokenConverter())
    }

    @Bean
    fun userApprovalHandler(): UserApprovalHandler? {
        val userApprovalHandler = ApprovalStoreUserApprovalHandler()
        userApprovalHandler.setApprovalStore(approvalStore())
        userApprovalHandler.setClientDetailsService(clientDetailsService)
        userApprovalHandler.setRequestFactory(DefaultOAuth2RequestFactory(clientDetailsService))
        return userApprovalHandler
    }

    @Bean
    fun tokenStore(): TokenStore? {
        val tokenStore = JwtTokenStore(accessTokenConverter())
        tokenStore.setApprovalStore(approvalStore())
        return tokenStore
    }

    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter? {
        val customHeaders = Collections.singletonMap<String, String>("kid", JWK_KID)
        return JwtCustomHeadersAccessTokenConverter(customHeaders, keyPair())
    }

    @Bean
    fun approvalStore(): ApprovalStore {
        return InMemoryApprovalStore()
    }

    @Bean
    fun keyPair(): KeyPair {
        val ksFile = ClassPathResource(KEY_STORE_FILE)
        val ksFactory = KeyStoreKeyFactory(ksFile, KEY_STORE_PASSWORD.toCharArray())
        return ksFactory.getKeyPair(KEY_ALIAS)
    }

    @Bean
    fun jwkSet(): JWKSet {
        val builder: RSAKey.Builder = RSAKey.Builder(
                keyPair().public as RSAPublicKey)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(JWK_KID)
        return JWKSet(builder.build())
    }
}