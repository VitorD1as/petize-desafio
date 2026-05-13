package com.vitor.petize_desafio;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PetizeDesafioApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void tarefasSemTokenRetorna401() throws Exception {
		mockMvc.perform(get("/api/tarefas"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void registrarLoginEListarTarefas() throws Exception {
		String body = """
				{"nome":"Maria","email":"maria@test.com","senha":"senha12345"}
				""";
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.token").exists());

		String loginBody = """
				{"email":"maria@test.com","senha":"senha12345"}
				""";
		var result = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(loginBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists())
				.andReturn();
		String token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");

		mockMvc.perform(get("/api/tarefas")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}
}
