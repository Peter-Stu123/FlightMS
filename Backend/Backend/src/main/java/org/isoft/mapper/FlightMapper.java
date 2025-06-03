package org.isoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.isoft.Entity.Flightinfo;

import java.util.List;

@Mapper
public interface FlightMapper extends BaseMapper<Flightinfo> {
    // 内连接查询
    @Select("SELECT f.*, " +
            "c1.cityname AS startCityName, " +
            "c2.cityname AS endCityName " +
            "FROM Flightinfo f " +
            "INNER JOIN Cityinfo c1 ON f.startcity = c1.cityid " +
            "INNER JOIN Cityinfo c2 ON f.endcity = c2.cityid")
    List<Flightinfo> getFlightWithCityNames();
//删除
    @Delete("DELETE FROM Flightinfo WHERE flightid = #{flightid}")
    int deleteFlightById(int flightid);
//    添加 - 使用自增ID
    @Insert("INSERT INTO Flightinfo (flightnum, flydate, starttime, flytime, startcity, endcity, seatnum) " +
            "VALUES (#{flightnum}, #{flydate}, #{starttime}, #{flytime}, #{startcity}, #{endcity}, #{seatnum})")
    int insertFlight(Flightinfo flightinfo);
//    分页查询 - 带城市名称
    @Select("SELECT f.*, " +
            "c1.cityname AS startCityName, " +
            "c2.cityname AS endCityName " +
            "FROM Flightinfo f " +
            "INNER JOIN Cityinfo c1 ON f.startcity = c1.cityid " +
            "INNER JOIN Cityinfo c2 ON f.endcity = c2.cityid " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<Flightinfo> selectFlightsByPage(int pageSize, int offset);
    // 获取总记录数
    @Select("SELECT COUNT(*) FROM Flightinfo")
    int countFlights();
// 获取最大航班ID
    @Select("SELECT MAX(flightid) FROM flightinfo")
    Integer getMaxFlightId();
    List<Flightinfo> getFlightWithCityNamesById(int flightId);

}
