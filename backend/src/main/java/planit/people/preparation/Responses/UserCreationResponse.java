package planit.people.preparation.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserCreationResponse(@JsonProperty String planit_userId, @JsonProperty String google_account_id){
}
