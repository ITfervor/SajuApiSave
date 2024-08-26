package com.example.dbsave;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.json.simple.parser.JSONParser;


public class ApiExplorer {
    public static void xmlToJson(String str) {

        try{
            String xml = str;
            JSONObject jObject = XML.toJSONObject(xml);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            Object json = mapper.readValue(jObject.toString(), Object.class);
            String output = mapper.writeValueAsString(json);
            System.out.println(output);
            System.out.println("변환 성공");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        int INDENT_FACTOR = 4;
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B090041/openapi/service/LrsrCldInfoService/getLunCalInfo"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + ""); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("solYear", "UTF-8") + "=" + URLEncoder.encode("1997", "UTF-8")); /*연*/
        urlBuilder.append("&" + URLEncoder.encode("solMonth", "UTF-8") + "=" + URLEncoder.encode("04", "UTF-8")); /*월*/
        urlBuilder.append("&" + URLEncoder.encode("solDay", "UTF-8") + "=" + URLEncoder.encode("01", "UTF-8")); /*일*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        String uglyString;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        uglyString = sb.toString();

        JSONObject xmlJSONObj = XML.toJSONObject(uglyString);
        String jsonPrettyPrintString = xmlJSONObj.toString(INDENT_FACTOR);
        System.out.println(jsonPrettyPrintString);

        //해당 부분은 그냥 json 파싱으로 추출이 아닌 substring을 통한 추출 방법
        //System.out.println( "일주 위치 : "+jsonPrettyPrintString.indexOf("lunIljin\":"));
        //System.out.println(jsonPrettyPrintString.indexOf("갑오(甲午)"));
        System.out.println("문자 추출 : " + jsonPrettyPrintString.substring(313, 319));

        try {

            JSONObject jsonObject = new JSONObject(jsonPrettyPrintString);
            JSONObject response = jsonObject.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONObject items = body.getJSONObject("items");
            JSONObject item = items.getJSONObject("item");

            //JSON 데이터 추출
            System.out.println("일주: " + item.getString("lunIljin"));
            System.out.println("월주: " + item.getString("lunWolgeon"));
            System.out.println("년주: " + item.getString("lunSecha"));

        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("실패");
        }


    }//for 문 끝
}



