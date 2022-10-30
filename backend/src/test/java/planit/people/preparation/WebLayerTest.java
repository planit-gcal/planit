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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.*;


import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(API_People.class)
@AutoConfigureRestDocs(outputDir = "src/main/java/planit/people/preparation/API_Documentation/snippets")
public class WebLayerTest {
//    @MockBean
//    private IDAO_Person idao_person;
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//org.springframework.beans.factory.UnsatisfiedDependencyException:
// Error creating bean with name 'API_Calendar' defined in file [C:\Users\mmari\Documents\GitHub\planit\backend\target\classes\planit\people\preparation\APIs\API_Calendar.class]:
// Unsatisfied dependency expressed through constructor parameter 0; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'service_Calendar' defined in file [C:\Users\mmari\Documents\GitHub\planit\backend\target\classes\planit\people\preparation\Services\Service_Calendar.class]:
// Unsatisfied dependency expressed through constructor parameter 3; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'IDAO_PresetAvailability' defined in planit.people.preparation.DAOs.IDAO_PresetAvailability defined in @EnableJpaRepositories declared on JpaRepositoriesRegistrar.EnableJpaRepositoriesConfiguration: Invocation of init method failed; nested exception is org.springframework.data.repository.query.QueryCreationException: Could not create query for public abstract java.util.Set planit.people.preparation.DAOs.IDAO_PresetAvailability.getAllByIdEventPreset(java.util.Set); Reason: Validation failed for query for method public abstract java.util.Set planit.people.preparation.DAOs.IDAO_PresetAvailability.getAllByIdEventPreset(java.util.Set)!; nested exception is java.lang.IllegalArgumentException: Validation failed for query for method public abstract java.util.Set planit.people.preparation.DAOs.IDAO_PresetAvailability.getAllByIdEventPreset(java.util.Set)!
//    /**
//     *
//     * @throws Exception in case of any error.
//     * @see API_People#createPerson(Entity_Person)
//     */
//    @Test
//    public void createNewPersonTest() throws Exception {
//        Entity_Person entity_person = new Entity_Person("male", "Mustafa", "Alhamoud", 23);
//        Entity_Person returned_person = new Entity_Person(1L, "male", "Mustafa", "Alhamoud", 23);
//        doReturn(returned_person).when(idao_person).save(any());
//         this.mockMvc
//                 .perform(
//                         MockMvcRequestBuilders
//                                 .post("/plan-it/people/create-person")
//                                 .contentType(MediaType.APPLICATION_JSON)
//                                 .accept(MediaType.APPLICATION_JSON)
//                                 .content(objectMapper.writeValueAsString(entity_person)))
//                 .andExpect(status().isCreated())
//                 .andExpect(jsonPath("$.person_id").value(1L))
//                 .andDo(
//                         document("create_person",
//                                 responseFields(
//                                     fieldWithPath("person_id").description("the id of the newly created person"),
//                                     fieldWithPath("gender").description("the gender of the newly created person"),
//                                     fieldWithPath("name").description("the first name of the newly created person"),
//                                     fieldWithPath("surname").description("the last name of the newly created person"),
//                                     fieldWithPath("age").description("the age of the newly created person")
//                                 ),
//                                 requestFields(
//                                         fieldWithPath("name").description("the first name of the person to be created"),
//                                         fieldWithPath("gender").description("the gender of the person to be created"),
//                                         fieldWithPath("surname").description("the last name of the person to be created"),
//                                         fieldWithPath("age").description("the age of the person to be created"),
//                                         fieldWithPath("person_id").ignored()
//                                 )
//                         )
//                 )
//                 .andReturn();
//
//    }
//    @Test
//    public void getAPersonByIdTest() throws Exception {
//        Entity_Person returned_person = new Entity_Person(1L, "male", "Mustafa", "Alhamoud", 23);
//        Optional<Entity_Person> entity_person = Optional.of(returned_person);
//        doReturn(entity_person).when(idao_person).findById(any());
//        this.mockMvc
//                .perform(
//                        get("/plan-it/people/{person_id}", 1)
//                                .accept(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andDo(
//                        document("get_person",
//                                responseFields(
//                                        fieldWithPath("person_id").description("the id of the retrieved person"),
//                                        fieldWithPath("gender").description("the gender of the retrieved person"),
//                                        fieldWithPath("name").description("the first name of the retrieved person"),
//                                        fieldWithPath("surname").description("the last name of the retrieved person"),
//                                        fieldWithPath("age").description("the age of the retrieved person")
//                                ),
//                                pathParameters(
//                                        parameterWithName("person_id").description("the id of the person to be retrieved")
//                                ))
//                );
//    }
//    @Test
//    public void getAllPeopleTest() throws Exception {
//        List<Entity_Person> entity_people = new ArrayList<>();
//        entity_people.add(new Entity_Person(1L, "male", "Same", "Smith", 23));
//        entity_people.add(new Entity_Person(2L, "female", "Reem", "Smith", 21));
//        doReturn(entity_people).when(idao_person).findAll();
//        this.mockMvc
//                .perform(
//                        get("/plan-it/people")
//                                .accept(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andDo(print())
//                .andDo(document("get_all"));
//    }
//    @Test
//    public void deleteAPersonById() throws Exception{
//        this.mockMvc
//                .perform(
//                        delete("/plan-it/people/{person_id}", 1)
//                                .accept(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andDo(
//                        document("delete_person", pathParameters(parameterWithName("person_id").description("id of person to be deleted")),
//                                responseFields(
//                                        fieldWithPath("success").description("indicate whether the person was deleted successfully or not"),
//                                        fieldWithPath("message").description("display an appropriate message regarding the deletion process")
//                                )));
//    }

}
