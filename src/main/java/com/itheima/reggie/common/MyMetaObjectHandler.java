package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器，实现公共字段自动填充
 * createTime/updateTime/createUser/updateUser
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作，自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());

        //更新时间与创建时间：当前时间
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        //创建人与修改人：从ThreadLocal中获取当前登录用户id
        Long currentId = BaseContext.getCurrentId();
        if(currentId != null){
            metaObject.setValue("createUser", currentId);
            metaObject.setValue("updateUser", currentId);
        }
    }

    /**
     * 更新操作，自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());

        //更新时间：当前时间
        metaObject.setValue("updateTime", LocalDateTime.now());

        //修改人：从ThreadLocal中获取当前登录用户id
        Long currentId = BaseContext.getCurrentId();
        if(currentId != null){
            metaObject.setValue("updateUser", currentId);
        }
    }
}
