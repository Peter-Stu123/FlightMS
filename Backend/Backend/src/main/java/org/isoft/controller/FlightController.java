package org.isoft.controller;

import org.isoft.Entity.Flightinfo;
import org.isoft.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/flight")
public class FlightController {
    @Autowired
    private FlightService flightService;
    @GetMapping("/flightsWithCityNames")
    public Map<String, Object> getFlightsWithCityNames() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", flightService.getFlightWithCityNames());
        return result;
    }
    @DeleteMapping("/deleteFlight/{id}")
    public Map<String, Object> deleteFlight(@PathVariable("id") int id) {
        flightService.deleteFlightById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        return result;
    }
    @PostMapping("/addFlight")
    public Map<String, Object> addFlight(@RequestBody Flightinfo flightinfo) {
        // 确保字段名称正确映射
        flightService.addFlight(flightinfo);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0); // 使用0表示成功，与其他API保持一致
        result.put("msg", "success");
        return result;
    }
    @GetMapping("/flightsByPage")
    public Map<String, Object> getFlightsByPage(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", flightService.getFlightsByPage(pageNum, pageSize));
        return result;
    }
}
