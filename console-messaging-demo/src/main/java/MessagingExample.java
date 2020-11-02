import com.telnyx.sdk.*;
import com.telnyx.sdk.api.MessagesApi;
import com.telnyx.sdk.api.NumberSearchApi;
import com.telnyx.sdk.auth.*;
import com.telnyx.sdk.model.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MessagingExample {

    private static final String YOUR_TELNYX_NUMBER = "+19842550944";
    private static final String YOUR_MOBILE_NUMBER = "+19198675309";
    private static final String YOUR_TELNYX_API_KEY = "";

    public static void sendMessageExample() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://api.telnyx.com/v2");

        // Configure HTTP bearer authorization: bearerAuth
        HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
        bearerAuth.setBearerToken(YOUR_TELNYX_API_KEY);

        MessagesApi apiInstance = new MessagesApi(defaultClient);
         // CreateMessageRequest | Message payload
        CreateMessageRequest createMessageRequest = new CreateMessageRequest()
                .from(YOUR_TELNYX_NUMBER)
                .to(YOUR_MOBILE_NUMBER)
                .text("Hello From Telnyx")
                .mediaUrls(Arrays.asList(
                        "https://upload.wikimedia.org/wikipedia/en/5/5f/Original_Doge_meme.jpg"
                ));
        try {
            MessageResponse result = apiInstance.createMessage(createMessageRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling MessagesApi#createMessage");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }

    public static void getMDRExample() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://api.telnyx.com/v2");

        // Configure HTTP bearer authorization: bearerAuth
        HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
        bearerAuth.setBearerToken(YOUR_TELNYX_API_KEY);

        MessagesApi apiInstance = new MessagesApi(defaultClient);
        UUID id = UUID.fromString("4031757b-300b-4d34-a211-8347126eac19"); // UUID | The id of the message
        try {
            MessageResponse result = apiInstance.retrieveMessage(id);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling MessagesApi#retrieveMessage");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }


    public static void searchNumberExample() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://api.telnyx.com/v2");

        // Configure HTTP bearer authorization: bearerAuth
        HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
        bearerAuth.setBearerToken(YOUR_TELNYX_API_KEY);

        NumberSearchApi apiInstance = new NumberSearchApi(defaultClient);
//        String filterPhoneNumberStartsWith = FOO; // String | Filter numbers starting with a pattern (meant to be used after `national_destination_code` filter has been set).
//        String filterPhoneNumberEndsWith = CALL; // String | Filter numbers ending with a pattern.
//        String filterPhoneNumberContains = TELNYX; // String | Filter numbers containing a pattern.
//        String filterLocality = "Chicago"; // String | Filter phone numbers by city.
//        String filterAdministrativeArea = "IL"; // String | Filter phone numbers by US state/CA province.
//        String filterCountryCode = US; // String | Filter phone numbers by ISO alpha-2 country code.
//        String filterNationalDestinationCode = 312; // String | Filter by the national destination code of the number. This filter is only applicable to North American numbers.
//        String filterRateCenter = CHICAGO HEIGHTS; // String | Filter phone numbers by NANP rate center. This filter is only applicable to North American numbers.
//        String filterNumberType = local; // String | Filter phone numbers by number type.
//        List<String> filterFeatures = voice,sms; // List<String> | Filter if the phone number should be used for voice, fax, mms, sms, emergency.
//        Integer filterLimit = 100; // Integer | Limits the number of results.
//        Boolean filterBestEffort = false; // Boolean | Filter to determine if best effort results should be included.
//        Boolean filterQuickship = true; // Boolean | Filter to exclude phone numbers that need additional time after to purchase to receive phone calls.
//        Boolean filterReservable = true; // Boolean | Filter to exclude phone numbers that cannot be reserved before purchase.
        try {
            ListAvailablePhoneNumbersResponse result = apiInstance.listAvailablePhoneNumbers()
//                .filterPhoneNumberStartsWith(filterPhoneNumberStartsWith)
//                .filterPhoneNumberEndsWith(filterPhoneNumberEndsWith)
//                .filterPhoneNumberContains(filterPhoneNumberContains)
//                .filterLocality(filterLocality)
//                .filterAdministrativeArea(filterAdministrativeArea)
//                .filterCountryCode(filterCountryCode)
//                .filterNationalDestinationCode(filterNationalDestinationCode)
//                .filterRateCenter(filterRateCenter)
//                .filterNumberType(filterNumberType)
//                .filterFeatures(filterFeatures)
//                .filterLimit(filterLimit)
//                .filterBestEffort(filterBestEffort)
//                .filterQuickship(filterQuickship)
//                .filterReservable(filterReservable)
                .execute();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling NumberSearchApi#listAvailablePhoneNumbers");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        sendMessageExample();
        getMDRExample();
        searchNumberExample();
    }

}
