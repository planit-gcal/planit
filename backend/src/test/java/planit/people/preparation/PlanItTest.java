package planit.people.preparation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import planit.people.preparation.APIs.API_User;
import planit.people.preparation.DAOs.IDAO_GoogleAccount;
import planit.people.preparation.DTOs.DTO_Code;
import planit.people.preparation.Responses.UserCreationResponse;
import planit.people.preparation.Services.Service_User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(API_User.class)
@AutoConfigureRestDocs(outputDir = "src/main/java/planit/people/preparation/API_Documentation/snippets")
public class PlanItTest {
    @MockBean
    IDAO_GoogleAccount idaoGoogleAccount;
    @MockBean
    Service_User serviceUser;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    public void createNewUserTest() throws Exception {

        UserCreationResponse userCreationResponse = new UserCreationResponse("1", "1");
        doReturn(userCreationResponse).when(idaoGoogleAccount).save(any());
        DTO_Code dto_code = new DTO_Code(null, "code");
        this.mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/plan-it/user/token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto_code)))
                .andExpect(status().isCreated())
                .andDo(
                        document("create_new_user",
                                responseFields(
                                        fieldWithPath("planit_userId").description("PlanIt user id, used to check userInfo (name, surname)"),
                                        fieldWithPath("google_account_id").description("Google user id, used to check email and refresh token")
                                ),
                                requestFields(
                                        fieldWithPath("planit_userId").description("An Id of an already authorized user (optional)"),
                                        fieldWithPath("code").description("the code returned by google api when an authorization request is sent.")
                                )
                        )
                )
                .andReturn();
    }
}
