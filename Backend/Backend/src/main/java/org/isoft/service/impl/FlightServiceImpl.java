package org.isoft.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.isoft.Entity.Cityinfo;
import org.isoft.Entity.Flightinfo;
import org.isoft.mapper.CityMapper;
import org.isoft.mapper.FlightMapper;
import org.isoft.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class FlightServiceImpl implements FlightService {
        @Autowired
        private FlightMapper flightMapper;
        @Autowired  // 注入新增的CityMapper
        private CityMapper cityMapper;
        @Override
        public List<Flightinfo> findflight() {
            // 这里可以添加具体查询逻辑
            return flightMapper.selectList(null);
        }
        @Override
        public List<Flightinfo> getFlightWithCityNames() {
                return flightMapper.getFlightWithCityNames();
        }
        // FlightServiceImpl.java
        @Override
        public void deleteFlightById(int flightid) {
                Flightinfo flightinfo = flightMapper.selectById(flightid);
                if (flightinfo != null) {
                        // 删除关联的城市信息
                        cityMapper.deleteById(flightinfo.getStartcity());
                        cityMapper.deleteById(flightinfo.getEndcity());
                }
                flightMapper.deleteFlightById(flightid);
        }
        // FlightServiceImpl.java
        @Transactional
        @Override
        public void addFlight(Flightinfo flightinfo) {
                // 确保能够正确获取城市名称，无论大小写
                String startCityName = flightinfo.getStartCityName();
                String endCityName = flightinfo.getEndCityName();

                if (startCityName == null || endCityName == null) {
                        throw new IllegalArgumentException("起始城市和目的城市不能为空");
                }
                Integer maxId = flightMapper.getMaxFlightId();
                // 设置新ID
                flightinfo.setFlightid(maxId == null ? 1 : maxId + 1);
                // 插入记录
                // 转换起始城市
                Integer startCityId = getOrCreateCity(startCityName);
                // 转换目的城市
                Integer endCityId = getOrCreateCity(endCityName);

                // 设置实际字段值
                flightinfo.setStartcity(startCityId);
                flightinfo.setEndcity(endCityId);

                // 插入航班信息
                flightMapper.insert(flightinfo);
        }

        // 保持原有私有方法不变
        private Integer getOrCreateCity(String cityName) {
                // 查询城市是否存在
                Cityinfo city = cityMapper.selectByName(cityName);

                if (city == null) {
                        // 创建新城市
                        city = new Cityinfo();
                        city.setCityid(null);  // Explicitly set to null (optional with Integer type)
                        city.setCityname(cityName);
                        cityMapper.insert(city); // 让数据库自动生成ID
                }

                return city.getCityid();
        }
        // FlightServiceImpl.java
        @Override
        public Map<String, Object> getFlightsByPage(int pageNum, int pageSize) {
                // 计算偏移量
                int offset = (pageNum - 1) * pageSize;
                
                // 查询分页数据
                List<Flightinfo> records = flightMapper.selectFlightsByPage(pageSize, offset);
                
                // 查询总记录数
                int total = flightMapper.countFlights();
                
                // 计算总页数
                int totalPages = (int) Math.ceil((double) total / pageSize);
                
                // 构建分页结果
                Map<String, Object> pageResult = new HashMap<>();
                pageResult.put("records", records);
                pageResult.put("total", total);
                pageResult.put("size", pageSize);
                pageResult.put("current", pageNum);
                pageResult.put("pages", totalPages);
                
                return pageResult;
        }
}