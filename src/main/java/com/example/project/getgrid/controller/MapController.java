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
            double latitude = addressInfo.getAsJsonObject("addresses").getAsJsonArray("x").get(0).getAsDouble();
            double longitude = addressInfo.getAsJsonObject("addresses").getAsJsonArray("y").get(0).getAsDouble();
            int[] grid = coordinateConverter.toGrid(latitude, longitude);

            // 가져온 정보를 모델에 추가
            model.addAttribute("latitude", latitude);
            model.addAttribute("longitude", longitude);
            model.addAttribute("gridX", grid[0]);
            model.addAttribute("gridY", grid[1]);
        } catch (Exception e) {
            // 오류 처리
            model.addAttribute("error", "검색 중 오류가 발생했습니다.");
        }
        return "search-result";
    }
}
