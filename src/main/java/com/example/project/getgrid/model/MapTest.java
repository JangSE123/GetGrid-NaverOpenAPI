package com.example.project.getgrid.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.*;

public class MapTest {

    private static final String clientId = "hh8wofz0sz";
    private static final String clientPw = "ZUiL4hLLp5Jw19ebEA8lTLJirsUgwy4CLA26KwZY";
    private static final String apiUrl = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=";

    public static JsonObject searchAddress(String address) throws IOException {
        String encodedAddress = URLEncoder.encode(address, "UTF-8");
        URL url = new URL(apiUrl + encodedAddress);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
        conn.setRequestProperty("X-NCP-APIGW-API-KEY", clientPw);

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            conn.disconnect();

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            // JSON 응답 로그 출력
            System.out.println("API Response: " + jsonResponse.toString());

            return jsonResponse;
        } else {
            conn.disconnect();
            throw new IOException("HTTP error code: " + responseCode);
        }
    }

    public static class CoordinateConverter {
        private static final double RE = 6371.00877; // 지구 반경(km)
        private static final double GRID = 5.0; // 격자 간격(km)
        private static final double SLAT1 = 30.0; // 투영 위도1(degree)
        private static final double SLAT2 = 60.0; // 투영 위도2(degree)
        private static final double OLON = 126.0; // 기준점 경도(degree)
        private static final double OLAT = 38.0; // 기준점 위도(degree)
        private static final int XO = 43; // 기준점 X좌표(GRID)
        private static final int YO = 136; // 기준점 Y좌표(GRID)

        public static int[] toGrid(double latitude, double longitude) {
            double DEGRAD = Math.PI / 180.0;
            double RADDEG = 180.0 / Math.PI;

            double re = RE / GRID;
            double slat1 = SLAT1 * DEGRAD;
            double slat2 = SLAT2 * DEGRAD;
            double olon = OLON * DEGRAD;
            double olat = OLAT * DEGRAD;

            double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
            sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
            double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
            sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
            double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
            ro = re * sf / Math.pow(ro, sn);

            double ra = Math.tan(Math.PI * 0.25 + (latitude) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = longitude * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            int x = (int) Math.floor(ra * Math.sin(theta) + XO + 0.5);
            int y = (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

            return new int[]{x, y};
        }

        public static double[] toCoordinates(int x, int y) {
            double DEGRAD = Math.PI / 180.0;
            double RADDEG = 180.0 / Math.PI;

            double re = RE / GRID;
            double slat1 = SLAT1 * DEGRAD;
            double slat2 = SLAT2 * DEGRAD;
            double olon = OLON * DEGRAD;
            double olat = OLAT * DEGRAD;

            double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
            sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
            double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
            sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
            double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
            ro = re * sf / Math.pow(ro, sn);

            double xn = x - XO;
            double yn = ro - y + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) ra = -ra;
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

            double theta;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            } else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) theta = -theta;
                } else {
                    theta = Math.atan2(xn, yn);
                }
            }
            double alon = theta / sn + olon;

            return new double[]{alat * RADDEG, alon * RADDEG};
        }
    }
}
