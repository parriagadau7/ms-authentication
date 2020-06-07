package cl.parriagada.ms.authentication

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.hamcrest.Matchers.containsString
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,classes = [AuthenticationApplication::class])
@AutoConfigureMockMvc
class AuthenticationApplicationTests {

    private val code = "_RllFU"
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun getKeys() {
        this.mockMvc.perform(get("/oauth2/keys")).andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.keys[0].kty").exists())
                .andExpect(jsonPath("$.keys[0].alg").value("RS256"));
    }

    @Test
    fun getAuthorized() {
        this.mockMvc.perform(get("/authorized").param("code",code)).andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().string(containsString(code)));
    }

}
