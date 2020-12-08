package com.telnyx.example;

import com.telnyx.example.model.NumberMapping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NumberMappings {

//    private HashMap<String, List<String>> numberMap = new HashMap<String, List<String>>();
    private final List<NumberMapping> numberMappings = new ArrayList<NumberMapping>();

    public String getDestinationNumber(String telnyxPhoneNumber, String endUserPhoneNumber){
//        String forwardToNumber = numberMap.get(telnyxPhoneNumber)
//                .stream()
//                .filter(phoneNumber -> !phoneNumber.equals(endUserPhoneNumber)).collect(Collectors.joining());

        String forwardToNumber = getNumberMapping(telnyxPhoneNumber, endUserPhoneNumber)
                .user_phone_numbers.stream()
                .filter(phoneNumber -> !phoneNumber.equals(endUserPhoneNumber)).collect(Collectors.joining());

//        String  = numberMappings
//                .stream()
//                .filter(numberMapping -> numberMapping.telnyx_phone_number.equals(telnyxPhoneNumber))
//                .filter(numberMapping -> numberMapping.user_phone_numbers.contains(endUserPhoneNumber))
//                .reduce((a, b) -> {
//                    throw new IllegalStateException("Multiple elements: " + a + ", " + b);
//                })
//                .get()
//                .user_phone_numbers
//                .stream()
//                .filter(phoneNumber -> !phoneNumber.equals(endUserPhoneNumber)).collect(Collectors.joining());

        return forwardToNumber;
    }

    public List<NumberMapping> getNumberMappingsByTelnyxPhoneNumber(String telnyxPhoneNumber) {
        return numberMappings
                .stream()
                .filter(numberMapping -> numberMapping.telnyx_phone_number.equals(telnyxPhoneNumber))
                .collect(Collectors.toList());
    }

    public List<NumberMapping> getNumberMappingsByEndUserPhoneNumber(String endUserPhoneNumber){
         return numberMappings
            .stream()
            .filter(numberMapping -> numberMapping.user_phone_numbers.contains(endUserPhoneNumber))
            .collect(Collectors.toList());
    }

    public NumberMapping getNumberMapping(String telnyxPhoneNumber, String endUserPhoneNumber) {
        return numberMappings
                .stream()
                .filter(numberMapping -> numberMapping.telnyx_phone_number.equals(telnyxPhoneNumber))
                .filter(numberMapping -> numberMapping.user_phone_numbers.contains(endUserPhoneNumber))
                .reduce((a, b) -> {
                    throw new IllegalStateException("Multiple elements: " + a + ", " + b);
                })
                .get();
    }

    public List<NumberMapping> getNumberMappings(){
        return numberMappings;
    }

    public void addNumberMapping(NumberMapping numberMapping){
//        numberMap.put(numberMapping.telnyx_phone_number, numberMapping.user_phone_numbers);
        numberMappings.add(numberMapping);
    }
}
