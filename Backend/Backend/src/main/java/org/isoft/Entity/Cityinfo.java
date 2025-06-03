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
@TableName("cityinfo")
public class Cityinfo {
    @TableId(type = IdType.AUTO)
    private Integer cityid;  // Changed from int to Integer
    @TableField(value = "cityname")
    private String cityname;

}