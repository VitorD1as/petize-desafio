package com.vitor.petize_desafio.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TarefasApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void actuatorHealthPublico() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void openApiDocsPublico() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Petize Desafio — API de Tarefas"));
    }

    @Test
    void registroDuplicadoRetorna409() throws Exception {
        String email = "dup-" + UUID.randomUUID() + "@test.com";
        String body = """
                {"nome":"Duplicado","email":"%s","senha":"senha12345"}
                """.formatted(email);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").value("E-mail já cadastrado"));
    }

    @Test
    void estatisticasAposLoginRetornaZeros() throws Exception {
        String email = "stats-" + UUID.randomUUID() + "@test.com";
        String reg = """
                {"nome":"Stats","email":"%s","senha":"senha12345"}
                """.formatted(email);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reg))
                .andExpect(status().isCreated());

        String login = """
                {"email":"%s","senha":"senha12345"}
                """.formatted(email);
        var result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isOk())
                .andReturn();
        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");

        mockMvc.perform(get("/api/tarefas/estatisticas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.criadas").value(0))
                .andExpect(jsonPath("$.emProgresso").value(0))
                .andExpect(jsonPath("$.finalizadas").value(0))
                .andExpect(jsonPath("$.canceladas").value(0));
    }
}
