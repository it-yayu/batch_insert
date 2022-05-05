package org.javaboy.batch_insert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.javaboy.batch_insert.mapper.UserMapper;
import org.javaboy.batch_insert.model.User;
import org.javaboy.batch_insert.service.IUserService;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author : zyf
 * @date : 2022/5/5 11:55
 */
@Service
@RequiredArgsConstructor
public class UserServiceUpdate extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;

    private final UpdateUsersTransaction updateStudentsTransaction;

    /**
     * 手动事务方式
     */
    private final DataSourceTransactionManager dataSourceTransactionManager;

    private final TransactionDefinition transactionDefinition;

    private final PlatformTransactionManager transactionManager;

    List<TransactionStatus> transactionStatuses = Collections.synchronizedList(new ArrayList<TransactionStatus>());


    public void updateUser(){
        List<User> users = userMapper.selectList(new QueryWrapper<>());
        Optional.ofNullable(users).orElseGet(ArrayList::new).forEach(user->{
            //更新用户信息
            String username = user.getUsername();
            String newUsername=username+"_zhangyafeng_"+new Random().nextInt(100);
            user.setUsername(newUsername);
            userMapper.updateById(user);
        });
    }


    public void updateStudentWithTrans(){
        List<User> users = userMapper.selectList(new QueryWrapper<>());
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            Optional.ofNullable(users).orElseGet(ArrayList::new).forEach(user->{
                //更新用户信息
                String username = user.getUsername();
                String newUsername=username+"_zhangyafeng_"+new Random().nextInt(100);
                user.setUsername(newUsername);
                userMapper.updateById(user);
            });
            dataSourceTransactionManager.commit(transactionStatus);
        } catch (TransactionException e) {
            dataSourceTransactionManager.rollback(transactionStatus);
            e.printStackTrace();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void updateStudentWithThreads() {
        //查询总数据
        List<User> users = userMapper.selectList(new QueryWrapper<>());
        // 线程数量
        final Integer threadCount = 2;

        //每个线程处理的数据量
        final Integer dataPartionLength = (users.size() + threadCount - 1) / threadCount;

        // 创建多线程处理任务
        ExecutorService studentThreadPool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch threadLatchs = new CountDownLatch(threadCount);
        AtomicBoolean isError = new AtomicBoolean(false);

        for (int i = 0; i < threadCount; i++) {
            // 每个线程处理的数据
            List<User> threadDatas = users.stream()
                    .skip(i * dataPartionLength).limit(dataPartionLength).collect(Collectors.toList());
            studentThreadPool.execute(() -> {
                try {
                    try {
                        updateStudentsTransaction.updateStudentsTransaction(transactionManager, transactionStatuses, threadDatas);
                    } catch (Exception e) {
                        isError.set(true);
                        e.printStackTrace();
                    } finally {
                        threadLatchs.countDown();
                    }
                } catch (Exception e) {
                    isError.set(true);
                    e.printStackTrace();
                }
            });
        }
        try {
            // 倒计时锁设置超时时间 30s
            boolean await = threadLatchs.await(30, TimeUnit.SECONDS);
            // 判断是否超时
            if (!await) {
                isError.set(true);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (!transactionStatuses.isEmpty()) {
            if (isError.get()) {
                transactionStatuses.forEach(transactionManager::rollback);
            } else {
                transactionStatuses.forEach(transactionManager::commit);
            }
        }

        System.out.println("主线程完成");
    }

    public void updateStudents(List<User> students, CountDownLatch threadLatch) {
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        System.out.println("子线程：" + Thread.currentThread().getName());
        try {
            students.forEach(user -> {
                //更新用户信息
                String username = user.getUsername();
                String newUsername=username+"_zhangyafeng_"+new Random().nextInt(100);
                user.setUsername(newUsername);
                userMapper.updateById(user);
            });
            dataSourceTransactionManager.commit(transactionStatus);
            threadLatch.countDown();
        } catch (Throwable e) {
            e.printStackTrace();
            dataSourceTransactionManager.rollback(transactionStatus);
        }
    }


}
