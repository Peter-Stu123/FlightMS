package org.isoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Select;
import org.isoft.Entity.User;

import java.util.ArrayList;
import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM user WHERE username = #{username}")
    List<User> selectByUsername(String username);

    // 分页查询所有用户（大数据量时推荐）
    default List<User> getAllUsers(UserMapper mapper, int pageSize) {
        List<User> allUsers = new ArrayList<>();
        int pageNum = 1;
        while (true) {
            Page<User> page = new Page<>(pageNum++, pageSize);
            List<User> users = mapper.selectPage(page, null).getRecords();
            if (users.isEmpty()) break;
            allUsers.addAll(users);
        }
        return allUsers;
    }
}
