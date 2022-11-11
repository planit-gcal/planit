package planit.people.preparation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import planit.people.preparation.APIs.API_OAuth;
import planit.people.preparation.DAOs.IDAO_GoogleAccount;
import planit.people.preparation.Entities.Entity_GoogleAccount;
import planit.people.preparation.Entities.Entity_User;
import planit.people.preparation.Scheduling.Converter;

import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(API_OAuth.class)
@AutoConfigureRestDocs(outputDir = "src/main/java/planit/people/preparation/API_Documentation/snippets/oauth")
public class OAuthWebLayerTest {
    @MockBean
    IDAO_GoogleAccount idaoGoogleAccount;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getPlanItUserIdFromTheirEmailTest() throws Exception {
        Entity_GoogleAccount response = new Entity_GoogleAccount(1L, new Entity_User(1L));
        doReturn(response).when(idaoGoogleAccount).getIdOfUserFromEmail(Converter.decodeURLString("user@email.com"));
        this.mockMvc
                .perform(
                        RestDocumentationRequestBuilders
                                .get("/plan-it/oauth/users?email="+Converter.encodeString("user@email.com"))
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-planit-user-id",
                        responseFields(
                                fieldWithPath("planit_user_id").description("The unique identifier for the PlanIt User Account").type(Long.class),
                                fieldWithPath("google_account_id").description("The unique identifier for the Google Account User").type(Long.class)
                        ),
                        requestParameters(
                                parameterWithName("email").description("the email of the PlanIt user")
                        )));
    }

}
