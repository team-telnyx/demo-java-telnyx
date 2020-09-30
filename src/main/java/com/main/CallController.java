package com.main;

import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.apis.CallCommandsApi;
import com.telnyx.sdk.models.Call;
import com.telnyx.sdk.models.DialRequest;
import com.telnyx.sdk.models.RetrieveCallStatusResponse;
import java.util.Base64;
import java.util.UUID;

import static com.main.TelnyxExample.TELNYX_PHONE_NUMBER;
import static com.main.TelnyxExample.WEBHOOK_URL;
import static com.main.TelnyxExample.defaultClient;

public class CallController {

    static CallCommandsApi apiInstance = new CallCommandsApi(defaultClient);

    public static UUID step1CreateOutboundLeg (UUID inboundCallControlId) throws ApiException {
        String clientState = Base64.getEncoder().encodeToString(String.valueOf(inboundCallControlId).getBytes());
        String helloWorld = "";
        Call inboundCall = new Call();
        inboundCall.setCallControlId(String.valueOf(inboundCallControlId));
        DialRequest outboundCallRequest = new DialRequest()
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
        return UUID.fromString(outboundCall.getData().getCallControlId());
    }

    public static void step2HandleOutboundAnswer () {}

    public static void step3HandleGatherEvent () {}

    public static void step4AnswerInboundCallAndBridge () {}


}
