package com.pawnder.service;

import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SmsService {
    @Value("${ncp.sms.api-key}")
    private String apiKey;

    @Value("${ncp.sms.secret-key}")
    private String apiSecret;

    @Value("${ncp.sms.sender}")
    private String senderPhone;

    public void sendSMS(String phoneNm, String message) {

        Message coolsms = new Message(apiKey, apiSecret);

        HashMap<String, String> params = new HashMap<>();
        params.put("to", phoneNm);
        params.put("from", senderPhone);
        params.put("type", "SMS");
        params.put("text", message);
        params.put("app_version", "gptonline v1.0");

        try {
            JSONObject result = coolsms.send(params);
            System.out.println("문자 전송 결과: " + result.toJSONString());
        } catch (CoolsmsException e) {
            System.err.println("문자 전송 실패: " + e.getMessage());
            System.err.println("에러 코드: " + e.getCode());
        }
    }
}
