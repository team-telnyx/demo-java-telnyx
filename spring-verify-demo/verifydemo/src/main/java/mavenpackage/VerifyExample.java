package mavenpackage;

import java.util.Scanner;
import com.telnyx.sdk.*;
import com.telnyx.sdk.api.MessagesApi;
import com.telnyx.sdk.api.NumberSearchApi;
import com.telnyx.sdk.api.VerifyApi;
import com.telnyx.sdk.auth.*;
import com.telnyx.sdk.model.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;;

public class VerifyExample {

    static Dotenv dotenv = Dotenv.load();
    private static final String TELNYX_API_KEY = dotenv.get("TELNYX_API_KEY");
    private static final String TELNYX_VERIFY_PROFILE_ID = dotenv.get("TELNYX_VERIFY_PROFILE_ID");

    public static void SendVerificationCode (String phoneNumber) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://api.telnyx.com/v2");

        // Configure HTTP bearer authorization: bearerAuth
        HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
        bearerAuth.setBearerToken(TELNYX_API_KEY);

        com.telnyx.sdk.api.VerifyApi apiInstance = new com.telnyx.sdk.api.VerifyApi(defaultClient);
        // CreateVerifyRequest / Verify payload
        CreateVerificationRequest createVerificationRequest = new CreateVerificationRequest()
            .phoneNumber(phoneNumber)
            .verifyProfileId(UUID.fromString(TELNYX_VERIFY_PROFILE_ID))
            .type(VerificationType.SMS)
            .timeoutSecs(300);

        try {
            apiInstance.createVerification(createVerificationRequest);
            System.out.print("Verification code sent to " + phoneNumber);
        } catch (ApiException e) {
            System.err.println("Exception when calling VerificationApi#createVerification");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }

    public static void CodeVerify(String phoneNumber) {
        int attempts = 0;
        int maxAttempts = 5;

        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://api.telnyx.com/v2");

        // Configure HTTP bearer authorization: bearerAuth
        HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
        bearerAuth.setBearerToken(TELNYX_API_KEY);

        VerifyApi apiInstance = new VerifyApi(defaultClient);
        while(attempts < maxAttempts) {
            // Get input, increment attempts
            Scanner codeObj = new Scanner(System.in);
            System.out.print("Verification code?: ");
            String inputVerify = codeObj.nextLine(); // Grab verify code
            attempts++;
            VerifyVerificationCodeRequest verifyVerificationCodeRequest = new VerifyVerificationCodeRequest()
                .code(inputVerify);
            try {
                VerifyVerificationCodeResponse result = apiInstance.verifyVerificationCode(phoneNumber, verifyVerificationCodeRequest);
                System.out.println(result);
                Enum responseStatus = result.getData().getResponseCode();
                if (result.getData().getResponseCode() == result.getData().getResponseCode().ACCEPTED)
                {
                    System.out.print("Verification accepted!");
                    break;
                }
                else
                {
                    if (attempts >= maxAttempts){
                        System.out.print("Verification max attempts reached!");
                    }
                }
            }
            catch (ApiException e) {
                System.err.println("Exception when calling VerifyApi#verifyVerification");
                System.err.println("Status code: " + e.getCode());
                System.err.println("Reason: " + e.getResponseBody());
                System.err.println("Response headers: " + e.getResponseHeaders());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
                
        if (TELNYX_API_KEY == "" || TELNYX_VERIFY_PROFILE_ID == "") {
            System.out.println("Check .env variables!");
            System.exit(-1);
        }
        Scanner numObj = new Scanner(System.in); // Create a Scanner object
        System.out.println("Phone Number (+E164 Format) to Verify?: ");

        String phoneNumber = numObj.nextLine(); // Grab phone number

        SendVerificationCode(phoneNumber);
        CodeVerify(phoneNumber);
    }
}
