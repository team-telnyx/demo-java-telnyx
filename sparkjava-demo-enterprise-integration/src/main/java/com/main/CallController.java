package com.main;

import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.api.CallCommandsApi;
import com.telnyx.sdk.model.Call;
import com.telnyx.sdk.model.CallRequest;
import com.telnyx.sdk.model.RetrieveCallStatusResponse;
import java.util.Base64;
import java.util.UUID;

import static com.main.TelnyxExample.TELNYX_PHONE_NUMBER;
import static com.main.TelnyxExample.defaultClient;

public class CallController {

    static CallCommandsApi apiInstance = new CallCommandsApi(defaultClient);

    public static String step1CreateOutboundLeg (String inboundCallControlId) throws ApiException {
        String clientState = Base64.getEncoder().encodeToString(String.valueOf(inboundCallControlId).getBytes());
        String helloWorld = "";
        Call inboundCall = new Call();
        inboundCall.setCallControlId(String.valueOf(inboundCallControlId));
        CallRequest outboundCallRequest = new CallRequest()
                .from(TELNYX_PHONE_NUMBER)
                .to("+19198675309")
                .connectionId("1471919317632156796")
//                .webhookUrl(WEBHOOK_URL)
                .clientState(clientState);
        RetrieveCallStatusResponse outboundCall = null;
        try {
            outboundCall = apiInstance.callDial(outboundCallRequest);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return outboundCall.getData().getCallControlId();
    }

    public static void step2HandleOutboundAnswer () {}

    public static void step3HandleGatherEvent () {}

    public static void step4AnswerInboundCallAndBridge () {}


}
