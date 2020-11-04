package com.telnyx.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NumberMappings {

    private HashMap<String, List<String>> numberMap = new HashMap<String, List<String>>();

    public String getDestinationNumber(String telnyxPhoneNumber, String endUserPhoneNumber){
        List<String> endUsersPhoneNumbers = numberMap.get(telnyxPhoneNumber);
        String forwardToNumber = "";
        for (String phoneNumber : endUsersPhoneNumbers) {
            if (!phoneNumber.equals(endUserPhoneNumber)) {
                forwardToNumber = phoneNumber;
            }
        }
        return forwardToNumber;
    }

    public void addNumberMapping(String telnyxPhoneNumber, List<String> endUsersPhoneNumbers){
        numberMap.put(telnyxPhoneNumber, endUsersPhoneNumbers);
    }
}
