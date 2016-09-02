package payroll

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentation
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PayrollServicesApplication.class)
@WebAppConfiguration
@ActiveProfiles('test')
class ApiDocumentation {
	@Rule
	public final RestDocumentation restDocumentation = new RestDocumentation("target/generated-snippets")

	@Autowired
	private WebApplicationContext context


	private MockMvc mockMvc


	@Before
	void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation))
				.build()
	}

	@Test
	void index() {
		this.mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("home"))
	}

	@Test
	void employees() {
		this.mockMvc.perform(get("/ws/v1/employees").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("employees",
			responseFields(
				fieldWithPath("[].id").description("id of the employee"),
				fieldWithPath("[].type").description("type of employees hourly/salaried/commissioned"),
				fieldWithPath("[].name").description("name of employee"),
				fieldWithPath("[].startDate").description("start date of employee"),
			)))
	}


	@Test
	void payroll() {
		this.mockMvc.perform(get("/ws/v1/payroll").param("day", "2016-08-22").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("payroll"))
	}
}
