package com.example.dbsave;

import com.example.dbsave.SajuDb;
import com.example.dbsave.SajuInfoRepository;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MaridDBSave {

    @Value("${api.key}")
    private String apiKey;
    String serviceKey = apiKey; // 공공데이터 API 키

    public List<SajuDb> sajuDbList = new ArrayList<>();

    @Autowired
    private SajuInfoRepository sajuInfoRepository;

    public static void main(String[] args) throws IOException {
        MaridDBSave maridDBSave = new MaridDBSave();

        for (int i = 1950; i < 2050; i++) {
            maridDBSave.fetchAndSaveYearData(i);
            System.out.println("저장시작연도 " + i);
        }
    }

    public void fetchAndSaveYearData(int lunYear) throws IOException {
        for (int lunMonth = 1; lunMonth <= 12; lunMonth++) {
            int maxDays = getMaxDaysInMonth(lunYear, lunMonth);
            for (int lunDay = 1; lunDay <= maxDays; lunDay++) {
                fetchDataAndAddToList(lunYear, lunMonth, lunDay);
            }
        }
        try {
            sajuInfoRepository.saveAll(sajuDbList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    private int getMaxDaysInMonth(int year, int month) {
        if (month == 2) {
            return (year % 4 == 0) ? 29 : 28;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else {
            return 31;
        }
    }

    private void fetchDataAndAddToList(int lunYear, int lunMonth, int lunDay) throws IOException {
        String url = buildUrl(lunYear, lunMonth, lunDay);
        String jsonResponse = getApiResponse(url);
        parseAndAddToDbList(jsonResponse);
    }

    private String buildUrl(int lunYear, int lunMonth, int lunDay) throws UnsupportedEncodingException {

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B090041/openapi/service/LrsrCldInfoService/getLunCalInfo");
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("solYear", "UTF-8") + "=" + URLEncoder.encode(String.format("%d", lunYear), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("solMonth", "UTF-8") + "=" + URLEncoder.encode(String.format("%02d", lunMonth), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("solDay", "UTF-8") + "=" + URLEncoder.encode(String.format("%02d", lunDay), "UTF-8"));
        return urlBuilder.toString();
    }

    private String getApiResponse(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            //해당 부분에 오류 발생했을 시 txt에 저장하는 방법 강구
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        return sb.toString();
    }

    private void parseAndAddToDbList(String jsonResponse) {
        int INDENT_FACTOR = 4;
        JSONObject xmlJSONObj = XML.toJSONObject(jsonResponse);
        String jsonPrettyPrintString = xmlJSONObj.toString(INDENT_FACTOR);

        try {
            JSONObject jsonObject = new JSONObject(jsonPrettyPrintString);
            JSONObject item = jsonObject.getJSONObject("response")
                    .getJSONObject("body")
                    .getJSONObject("items")
                    .getJSONObject("item");

            Long solDay = item.getLong("solDay");
            Long solMonth = item.getLong("solMonth");
            Long solYear = item.getLong("solYear");
            Long solJd = item.getLong("solJd"); //율리우스 적일 추가
            String lunIljin = item.getString("lunIljin");
            String lunWolgeon = item.getString("lunWolgeon");
            String lunSecha = item.getString("lunSecha");
            System.out.println(lunIljin);

            SajuDb sajuDb = SajuDb.builder()
                    .lunIljin(lunIljin)
                    .lunWolgeon(lunWolgeon)
                    .lunSecha(lunSecha)
                    .solDay(String.format("%d", solDay))
                    .solMonth(String.format("%d", solMonth))
                    .solYear(String.format("%d", solYear))
                    .solJd(String.format("%d", solJd))
                    .build();

            sajuDbList.add(sajuDb);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
//            System.out.println("연도" + lunYear + "\n");
//            System.out.println("월" + lunMonth + "\n");
//            System.out.println("알" + lunDay + "\n");            //해당 부분에도 추출해서 저장\
            //멈췄다가 다시 스케줄러 시작하고 다시 가동 해당 부분에 스케줄러에 대한 함수 적용 delay끝나느거
            TimeUnit.DAYS.toDays(1);
//            TimeUnit.DAYS.sleep(6);

        }
        //sleep추가(0814 회사에서 추가)
        finally{
            TimeUnit.MILLISECONDS.toMillis(1000);

        }
    }

}