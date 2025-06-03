package org.isoft.controller;

import org.isoft.Entity.Cityinfo;
import org.isoft.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173") // 允许特定源的跨域请求
@RequestMapping("/city") // API的基础路径
public class CityController {

    @Autowired
    private CityService cityService;

    @GetMapping("/all")
    public Map<String, Object> getAllCities() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Cityinfo> cities = cityService.getAllCities();
            result.put("code", 0);
            result.put("msg", "success");
            result.put("data", cities);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "获取城市信息失败: " + e.getMessage());
            result.put("data", null);
        }
        return result;
    }
}