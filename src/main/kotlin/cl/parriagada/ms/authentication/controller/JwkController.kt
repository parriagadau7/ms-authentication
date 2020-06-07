package cl.parriagada.ms.authentication.controller

import com.nimbusds.jose.jwk.JWKSet
import net.minidev.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class JwkController(@Autowired private val jwkSet: JWKSet) {

    @GetMapping(value = ["/oauth2/keys"], produces = ["application/json; charset=UTF-8"])
    fun keys(): JSONObject? {
        return jwkSet.toJSONObject()
    }

    @GetMapping(value = ["/authorized"])
    fun passwordGrant(@RequestParam code: String): String {
        return code
    }
}