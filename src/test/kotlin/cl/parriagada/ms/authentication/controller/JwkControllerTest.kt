package cl.parriagada.ms.authentication.controller

import com.nimbusds.jose.jwk.JWKSet

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class JwkControllerTest {

    private val code = "_RllFU"
    @InjectMocks
    lateinit var jwkController: JwkController
    @Mock
    lateinit var jWKSet: JWKSet

    @Test
    fun keys() {
        doReturn(code).`when`(jWKSet).toString()
        val result = jwkController.keys()
        assertNotNull(result)
        assertEquals(code, result)
    }

    @Test
    fun passwordGrant() {
        val result = jwkController.passwordGrant(code)
        assertNotNull(result)
        assertEquals(code, result)
    }

}