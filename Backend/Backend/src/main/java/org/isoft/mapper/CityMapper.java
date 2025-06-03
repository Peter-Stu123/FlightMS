// src/main/java/org/isoft/mapper/CityMapper.java
package org.isoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.isoft.Entity.Cityinfo;

@Mapper
public interface CityMapper extends BaseMapper<Cityinfo> {
    // MyBatis-Plus已提供基础CRUD方法
    @Select("SELECT * FROM cityinfo WHERE cityname = #{cityName}")
    Cityinfo selectByName(String cityName);

}
