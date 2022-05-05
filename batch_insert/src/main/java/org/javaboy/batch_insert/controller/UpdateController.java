package org.javaboy.batch_insert.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.javaboy.batch_insert.service.impl.UserServiceUpdate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : zyf
 * @date : 2022/5/5 11:50
 */
@RestController
@RequestMapping("/update")
@RequiredArgsConstructor
public class UpdateController {
    private final UserServiceUpdate userServiceUpdate;

    @ApiOperation("一条一条修改5万条数据")
    @RequestMapping("/one")
    public void updateOneByOne(){
        userServiceUpdate.updateUser();
    }

    @ApiOperation("手动事务方式,和注解应该差不多")
    @RequestMapping("/two")
    public void updateStudentWithTrans(){
        userServiceUpdate.updateStudentWithTrans();
    }

    @ApiOperation("多线程进行数据修改")
    @RequestMapping("/three")
    public void updateStudentWithThreads(){
        userServiceUpdate.updateStudentWithThreads();
    }



}
