package cl.parriagada.ms.authentication.common.config

import org.springframework.security.jwt.JwtHelper
import org.springframework.security.jwt.crypto.sign.RsaSigner
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.util.JsonParserFactory
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey

class JwtCustomHeadersAccessTokenConverter() : JwtAccessTokenConverter(){

    private var customHeaders: Map<String, String> = HashMap()
    private val objectMapper = JsonParserFactory.create()
    var signer: RsaSigner? = null

     constructor(customHeaders: Map<String, String>, keyPair: KeyPair) : this() {
        super.setKeyPair(keyPair)
        signer = RsaSigner(keyPair.private as RSAPrivateKey)
        this.customHeaders = customHeaders
    }

     override fun encode(accessToken: OAuth2AccessToken?, authentication: OAuth2Authentication?): String? {
        val content: String
        content = try {
            objectMapper.formatMap(accessTokenConverter.convertAccessToken(accessToken, authentication))
        } catch (ex: Exception) {
            throw IllegalStateException("Cannot convert access token to JSON", ex)
        }
        return JwtHelper.encode(content, signer, customHeaders)
                .encoded
    }
}