package org.javaboy.batch_insert.service.impl;

import lombok.RequiredArgsConstructor;
import org.javaboy.batch_insert.mapper.UserMapper;
import org.javaboy.batch_insert.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Random;

/**
 * 多线程具体执行的一个更新方法
 * @author : zyf
 * @date : 2022/5/5 15:16
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
@RequiredArgsConstructor
public class UpdateUsersTransaction {

    private final UserMapper userMapper;

    public void updateStudentsTransaction(PlatformTransactionManager transactionManager, List<TransactionStatus> transactionStatuses, List<User> users) {
        // 使用这种方式将事务状态都放在同一个事务里面
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些。
        TransactionStatus status = transactionManager.getTransaction(def); // 获得事务状态
        transactionStatuses.add(status);

        users.forEach(user -> {
            //更新用户信息
            String username = user.getUsername();
            String newUsername=username+"_zhangyafeng_"+new Random().nextInt(100);
            user.setUsername(newUsername);
            userMapper.updateById(user);
        });
        System.out.println("子线程：" + Thread.currentThread().getName());
    }
}
