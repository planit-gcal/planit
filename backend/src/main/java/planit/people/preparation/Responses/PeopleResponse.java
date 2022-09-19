package planit.people.preparation.Responses;

/**
 * Construct a response object for delete person APIs
 *
 * @param success indicate if the request successfully processed or not
 * @param message display an appropriate message of how the request was processed
 * @author Mustafa Alhamoud
 * @version 1.0
 * @since 1.0
 */
public record PeopleResponse(Boolean success, String message) {

    /**
     * constructor to build the response
     *
     * @param success set the success attribute
     * @param message set the message attribute
     * @author Mustafa Alhamoud
     * @since 1.0
     */
    public PeopleResponse {
    }


}
