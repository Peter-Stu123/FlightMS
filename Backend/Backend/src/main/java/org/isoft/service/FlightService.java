package org.isoft.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.isoft.Entity.Flightinfo;
import java.util.Map;
import java.util.List;

public interface FlightService {
    List<Flightinfo> findflight();
    List<Flightinfo> getFlightWithCityNames();
    // FlightService.java
    void deleteFlightById(int flightid);
    // FlightService.java
    void addFlight(Flightinfo flightinfo);
    // FlightService.java
    Map<String, Object> getFlightsByPage(int pageNum, int pageSize);
}
