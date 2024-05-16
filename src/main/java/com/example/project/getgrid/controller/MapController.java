package com.example.project.getgrid.controller;

import com.example.project.getgrid.model.MapTest;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MapController {

    @GetMapping("/search-form")
    public String showSearchForm() {
        return "search-form";
    }

    @PostMapping("/search")
    public String searchAddress(@RequestParam("address") String address, Model model) {
        try {
            // MapTest 클래스의 주소 검색 메서드 호출
            MapTest mapTest = new MapTest();
            MapTest.CoordinateConverter coordinateConverter = new MapTest.CoordinateConverter();

            // 주소 검색 결과 가져오기
            JsonObject addressInfo = mapTest.searchAddress(address);
            if (addressInfo != null) {
                // addressInfo 로그 출력
                System.out.println("Address Info: " + addressInfo.toString());

                if (addressInfo.has("addresses") && addressInfo.getAsJsonArray("addresses").size() > 0) {
                    JsonObject firstAddress = addressInfo.getAsJsonArray("addresses").get(0).getAsJsonObject();
                    double latitude = firstAddress.get("y").getAsDouble();
                    double longitude = firstAddress.get("x").getAsDouble();
                    int[] grid = coordinateConverter.toGrid(latitude, longitude);

                    // 가져온 정보를 모델에 추가
                    model.addAttribute("latitude", latitude);
                    model.addAttribute("longitude", longitude);
                    model.addAttribute("gridX", grid[0]);
                    model.addAttribute("gridY", grid[1]);
                } else {
                    model.addAttribute("error", "검색 결과가 없습니다.");
                }
            } else {
                model.addAttribute("error", "주소 정보를 불러올 수 없습니다.");
            }
        } catch (Exception e) {
            // 오류 처리
            e.printStackTrace();  // 콘솔에 예외 스택 트레이스를 출력하여 디버깅에 도움
            model.addAttribute("error", "검색 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "search-result";
    }
}
