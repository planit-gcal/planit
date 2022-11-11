package planit.people.preparation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import planit.people.preparation.APIs.API_User;
import planit.people.preparation.DAOs.*;
import planit.people.preparation.DTOs.DTO_Code;
import planit.people.preparation.Responses.UserCreationResponse;
import planit.people.preparation.Services.Service_User;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(API_User.class)
@AutoConfigureRestDocs(outputDir = "src/main/java/planit/people/preparation/API_Documentation/snippets/user")

public class UserWebLayerTest {
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
        DTO_Code code = new DTO_Code(null, "code");
        UserCreationResponse response = new UserCreationResponse(1L, 1L);
        doReturn(response).when(serviceUser).getGoogleAccountId(code);
        this.mockMvc
                .perform(
                        RestDocumentationRequestBuilders
                                .post("/plan-it/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(code)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("create-new-user",
                        requestFields(
                                fieldWithPath("planit_user_id").ignored(),
                                fieldWithPath("code").description("Code returned by Google when a user authorize the App").type(String.class)
                        ),
                        responseFields(
                                fieldWithPath("planit_user_id").description("Auto generated Identifier for the User new PlanIt User account").type(Long.class),
                                fieldWithPath("google_account_id").description("Auto generated Identifier for the new Google Account that contains a unique email and refresh token").type(Long.class)
                        )
                ));
    }

    @Test
    public void addANewEmailForAnAlreadyExistingPlanItAccount() throws Exception {
        DTO_Code code = new DTO_Code(1L, "code");
        UserCreationResponse response = new UserCreationResponse(1L, 2L);
        doReturn(response).when(serviceUser).getGoogleAccountId(code);
        this.mockMvc
                .perform(
                        RestDocumentationRequestBuilders
                                .post("/plan-it/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(code)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("add-new-google-account",
                        requestFields(
                                fieldWithPath("planit_user_id").description("The unique identifier for the PlanIt User Account").type(Long.class),
                                fieldWithPath("code").description("Code returned by Google when a user authorize the App").type(String.class)
                        ),
                        responseFields(
                                fieldWithPath("planit_user_id").description("The unique identifier for the PlanIt User Account").type(Long.class),
                                fieldWithPath("google_account_id").description("Auto generated Identifier for the new Google Account that contains a unique email and refresh token").type(Long.class)
                        )
                ));
    }

    @Test
    public void getAllEmailsForAPlanItUser() throws Exception {
        List<String> emails = new ArrayList<>() {
            {
                add("university@test.com");
                add("personal@test.com");
                add("work@test.com");
            }
        };
        doReturn(emails).when(idaoGoogleAccount).getEmailsFromUserId(1L);
        this.mockMvc
                .perform(
                        RestDocumentationRequestBuilders
                                .get("/plan-it/users/{planit-user-id}/emails", 1)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andDo(print())
                .andDo(document("get-emails",
                        responseFields(
                                fieldWithPath("[]").description("A list containing all emails that belong to the provided PlanIt User Id")
                        ),
                        pathParameters(
                                parameterWithName("planit-user-id").description("The id of the PlanIt User whose emails should be returned")
                        )));
    }
}
