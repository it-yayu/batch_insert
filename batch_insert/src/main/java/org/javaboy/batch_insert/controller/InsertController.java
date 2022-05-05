package org.javaboy.batch_insert.controller;

import lombok.RequiredArgsConstructor;
import org.javaboy.batch_insert.model.User;
import org.javaboy.batch_insert.service.impl.UserService;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author v-zhangyafeng3
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/insert")
public class InsertController {
    private static final Logger logger = getLogger(InsertController.class);

    private final UserService userService;

    /**
     * MyBatis Plus 批量插入
     */
    @GetMapping("/user3")
    public void user3() {
        List<User> users = new ArrayList<>(50000);
        for (int i = 0; i < 50000; i++) {
            User u = new User();
            u.setAddress("广州：" + i);
            u.setUsername("张三：" + i);
            u.setPassword("123：" + i);
            users.add(u);
        }
        long startTime = System.currentTimeMillis();
        userService.saveBatch(users);
        long endTime = System.currentTimeMillis();
        logger.info("MyBaits Plus 批量插入耗时 {}", (endTime - startTime));
    }

    /**
     * 一条一条插入
     */
    @GetMapping("/user2")
    public void user2() {
        List<User> users = new ArrayList<>(50000);
        for (int i = 0; i < 50000; i++) {
            User u = new User();
            u.setAddress("广州：" + i);
            u.setUsername("张三：" + i);
            u.setPassword("123：" + i);
            users.add(u);
        }
        userService.addUserOneByOne(users);
    }

    /**
     * 合并成一条 SQL 插入
     */
    @GetMapping("/user")
    public void hello() {
        List<User> users = new ArrayList<>(50000);
        for (int i = 0; i < 50000; i++) {
            User u = new User();
            u.setAddress("广州：" + i);
            u.setUsername("张三：" + i);
            u.setPassword("123：" + i);
            users.add(u);
        }
        userService.addByOneSQL(users);
    }
}
