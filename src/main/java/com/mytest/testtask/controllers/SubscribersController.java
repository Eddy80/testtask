package com.mytest.testtask.controllers;

import com.mytest.testtask.models.*;
import com.mytest.testtask.repositories.SmsRepository;
import com.mytest.testtask.repositories.SubscriberRepository;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.json.JSONException;
import org.json.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;

import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.mytest.testtask.utils.ParameterStringBuilder;


@RestController
@RequestMapping("/api")
@Api( tags = {"Subscribers"})
public class SubscribersController {

    @Autowired
    SubscriberRepository subscriberRepository;
    @Autowired
    SmsRepository smsRepository;

    @PostMapping("/ping")
    @ApiOperation(value = "Ping msisdn")
    public @ResponseBody
    ResponseEntity<PingResponse> pingSubscriber (@RequestParam(value = "msisdn") String msisdn){

        PingResponse pingResponse = new PingResponse();

        int min = 1;
        int max = 9;
        int randomNum = ThreadLocalRandom.current().nextInt(min, max);

        if (randomNum > 5) {
            pingResponse.setStatus("unavailableSubscriber");
            pingResponse.setStatusNumber(0);
        }
        else {
            pingResponse.setStatus("inNetwork");
            pingResponse.setStatusNumber(1);
        }
        return ResponseEntity.ok(pingResponse);
    }

    @PostMapping("/sms")
    @ApiOperation(value = "Send sms to Caller")
    public @ResponseBody
    ResponseEntity<Boolean> senSMS (@RequestBody SmsPayload payload){
       Boolean isSend = false;
       if ((payload.getMsisdnA() != null) || (payload.getMsisdnA() != "")) {
            // send sms
           Sms foundSms = smsRepository.findByMsisdn(payload.getMsisdnA()).orElse(null);
           if (foundSms != null) {
               Integer smsCount = foundSms.getSmsCount();
               if (smsCount < 300) {
                   smsCount++;
                   foundSms.setSmsCount(smsCount);
                   Sms savedSms =  smsRepository.saveAndFlush(foundSms);
                   if (savedSms != null)
                       isSend = true;
               }
           } else {
               Sms newSms = new Sms();
               newSms.setSmsCount(1);
               newSms.setMsisdn(payload.getMsisdnA());
               Sms savedSms =  smsRepository.saveAndFlush(newSms);
               if (savedSms != null)
                   isSend = true;
           }
       }
       return ResponseEntity.ok(isSend);
    }


    @PostMapping("/unavailableSubscriber")
    @ApiOperation(value = "Notify Caller")
    public @ResponseBody
    ResponseEntity<?> checkAvailability (@RequestBody UnavalilableSubscriberPayload payload   )  throws Exception {

        String status = null;
        Integer statusNumber = 0;
        String response = null;
        String responseSms = null;
        String msisdnA = payload.getMsisdnA();
        String msisdnB = payload.getMsisdnB();
        Subscriber foundSubscriber = null;


        String defaultTimeZone = "UTC +0";
        String defaultLanguage = "EN";

        String subscriberTimeZone = defaultTimeZone;
        String subscriberLanguage = defaultLanguage;

        String smsText  = null;
        String smsTextDefault = "Этот абонент снова в сети";
        String smsTextRu = "Этот абонент снова в сети";
        String smsTextEn = "Subscriber is already in Network";

        String externalUrl = "http://localhost:4001/api/ping";
        String smsUrl = "http://localhost:4001/api/sms";

        Integer secondsToSleep = 3;

        try {
            if ((msisdnB != null)) {
                foundSubscriber = subscriberRepository.findByMsisdn(payload.getMsisdnB()).orElse(null);
            } else {
                System.err.println("Номер вызываемого абонента пустой !");
                return ResponseEntity.badRequest().body("Номер вызываемого абонента пустой !");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при проверке принадлежности вызываемого абонента к нашей сети !");
            return ResponseEntity.badRequest().body("Ошибка при проверке принадлежности вызываемого абонента к нашей сети !");
        }

        if (foundSubscriber != null) {

            subscriberTimeZone = foundSubscriber.getTimeZone();
            subscriberLanguage = foundSubscriber.getLanguage();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("msisdn", msisdnB);

            Integer loopNumber = 1;
            while (statusNumber == 0) {
                try {
                    if (loopNumber < 5184000) {
                        System.out.println("LoopNumber :" + loopNumber);
                        String params = ParameterStringBuilder.getParamsString(parameters);
                        response = executePost(externalUrl, params);
                        System.out.println(response);
                        if (response == null) {
                            System.err.println("Ошибка при подключении к URL !");
                            return ResponseEntity.badRequest().body("Ошибка при подключении к URL !");
                        }


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            status = jsonObject.get("status").toString();
                            statusNumber = Integer.valueOf(jsonObject.get("statusNumber").toString());
                            System.out.println(status);

                            if (subscriberLanguage.equals("EN"))
                                smsText = smsTextEn;
                            else if (subscriberLanguage.equals("RU"))
                                smsText = smsTextRu;
                            else
                                smsText = smsTextDefault;

                            String bodySms = "{" +
                                    "\"msisdnA\":\"" + msisdnA + "\"," +
                                    "\"msisdnB\":\"" + msisdnB + "\"," +
                                    "\"text\":\"" + smsText + "\"}";


                            Instant instant = Instant.now();
                            ZoneId z = ZoneId.of(subscriberTimeZone);
                            ZonedDateTime zdt = instant.atZone(z);

                            LocalTime time = zdt.toLocalTime();

                            if (isBetween(time, LocalTime.of(9, 0), LocalTime.of(22, 0))) {
                                responseSms = executePostWithPayload(smsUrl, bodySms);
                                return ResponseEntity.ok(responseSms);
                            }

                        } catch (JSONException err) {
                            System.err.println("Error :" + err.toString());
                        }


                        try {
                            TimeUnit.SECONDS.sleep(secondsToSleep);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        System.err.println("Пинг не возможно более 3 суток !");
                        return ResponseEntity.badRequest().body("\"Пинг не возможно более 3 суток !");
                    }
                } catch(IOException e){
                    System.err.println("Ошибка при получении результата !");
                    return ResponseEntity.badRequest().body("Ошибка при получении результата !");
                }
                loopNumber++;
            }
        } else {
            System.err.println("Вызываемый абонент не принадлежит к нашей сети !");
            return ResponseEntity.badRequest().body("Вызываемый абонент не принадлежит к нашей сети !");
        }
        return null;
    }

    public static String executePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            int status = connection.getResponseCode();

            Reader streamReader = null;

            if (status > 299) {
                streamReader = new InputStreamReader(connection.getErrorStream());
            } else {
                streamReader = new InputStreamReader(connection.getInputStream());
            }


            BufferedReader rd = new BufferedReader(streamReader);

            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = rd.readLine()) != null) {
                content.append(inputLine);
            }
            rd.close();
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    public static String executePostWithPayload(String targetURL, String body) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");

            connection.setRequestProperty("Content-Language", "en-US");
            connection.setRequestProperty("Accept", "application/json");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            String jsonInputString =  body; // "{"name": "Upendra", "job": "Programmer"}";
            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
                return  response.toString();
            }

//            ///////////////////
//            OutputStream os = connection.getOutputStream();
//            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
//            osw.write(body);
//            osw.flush();
//            osw.close();
//            os.close();
//            connection.connect();
//
//            String result;
//            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
//            ByteArrayOutputStream buf = new ByteArrayOutputStream();
//            int result2 = bis.read();
//            while(result2 != -1) {
//                buf.write((byte) result2);
//                result2 = bis.read();
//            }
//            result = buf.toString();
//            System.out.println(result);
//            /////////////////////




        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    public static boolean isBetween(LocalTime candidate, LocalTime start, LocalTime end) {
        return !candidate.isBefore(start) && !candidate.isAfter(end);  // Inclusive.
    }
}
