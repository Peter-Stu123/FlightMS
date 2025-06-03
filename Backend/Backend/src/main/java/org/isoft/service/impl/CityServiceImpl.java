package org.isoft.service.impl;

import org.isoft.Entity.Cityinfo;
import org.isoft.mapper.CityMapper;
import org.isoft.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    @Autowired
    private CityMapper cityMapper;

    @Override
    public List<Cityinfo> getAllCities() {
        return cityMapper.selectList(null); // 使用MyBatis-Plus的selectList查询所有城市
    }
}