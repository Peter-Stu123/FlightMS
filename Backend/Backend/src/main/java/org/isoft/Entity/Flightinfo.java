package org.isoft.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("Flightinfo")
public class Flightinfo {
        @TableId(type = IdType.AUTO)
        private Integer flightid;
        private String flightnum;
        // 与JSON中的flightnum匹配
        private String flydate;
        // 与JSON中的flydate匹配
        private String starttime;
        // 与JSON中的starttime匹配
        private String flytime;
        // 与JSON中的flytime匹配

        @TableField(value = "startcity")
        private int startcity;
        // 数据库字段

        @TableField(value = "endcity")
        private int endcity;
        // 数据库字段

        private int seatnum;           // 与JSON中的seatnum匹配

        // 接口字段（城市名称，非数据库字段）
        @TableField(exist = false)
        private String startCityName;   // 与JSON中的startCityName匹配

        @TableField(exist = false)
        private String endCityName;     // 与JSON中的endCityName匹配
}