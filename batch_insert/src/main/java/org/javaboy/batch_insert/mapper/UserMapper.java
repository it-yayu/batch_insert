package org.javaboy.batch_insert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.javaboy.batch_insert.model.User;

import java.util.List;
/**
 * @author v-zhangyafeng3
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 单条插入
     * @param user
     * @return
     */
    Integer addUserOneByOne(User user);

    /**
     * 合并为一条sql插入
     * @param users
     */
    void addByOneSQL(@Param("users") List<User> users);
}
