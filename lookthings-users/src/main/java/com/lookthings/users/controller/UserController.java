package com.lookthings.users.controller;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageInfo;
import com.lookthings.core.json.JsonResult;
import com.lookthings.core.service.impl.CommonConfig;
import com.lookthings.users.model.UserDO;
import com.lookthings.users.service.UserService;
import org.apache.log4j.Logger;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fantasy on 2018/4/19.
 *
 * @author Fantasy
 */
@Controller
@RequestMapping("user")
public class UserController {
    private static Logger log = Logger.getLogger(UserController.class);
    @Resource
    private UserService userService;

    @Resource
    private CommonConfig commonConfig;

    /**
     * 根据用户参数进行用户的查询
     *
     * @param userId
     * @param userName
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping("/getUsers")
    public JsonResult<PageInfo<UserDO>> getUsers(String userId, String userName, Integer pageNo, Integer pageSize) {
        JsonResult<PageInfo<UserDO>> jsonResult = new JsonResult();
        UserDO userDO = new UserDO();
        if (userId != null) {
            userDO.setId(new Integer(userId));
        }
        if (userName != null) {
            userDO.setUserName(userName);
        }
        log.debug("[getUsers] [userDO]" + userDO.toString());
        jsonResult.setSuccess(true);
        jsonResult.setResult(userService.getUsersByPageIndex(userDO, pageNo, pageSize));
        return jsonResult;
    }

    /**
     * 批量插入用户数据
     *
     * @param userDOS
     * @return
     */
    @ResponseBody
    @RequestMapping("/insertUserBatch")
    public JsonResult<Boolean> insertUserBatch(String userDOS) {
        JsonResult<Boolean> jsonResult = new JsonResult();
        if (userDOS == null) {
            return jsonResult;
        }
        List<UserDO> userList = JSONArray.parseArray(userDOS, UserDO.class);
        userList.forEach(userItem -> {
            ByteSource salt = ByteSource.Util.bytes(commonConfig.getIsaKey());
            SimpleHash simpleHashPassword = new SimpleHash("md5", userItem.getUserPassword(), salt, 2);
            userItem.setUserPassword(simpleHashPassword.toString());
        });

        Boolean isSuccess = userService.insertUserByUserInfo(userList);
        jsonResult.setSuccess(isSuccess);
        jsonResult.setResult(isSuccess);
        return jsonResult;
    }

    /**
     * 批量更新用户
     *
     * @param userDOS
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateUserBatch")
    public JsonResult<Boolean> updateUserBatch(String userDOS) {
        JsonResult<Boolean> jsonResult = new JsonResult();
        if (userDOS == null) {
            return jsonResult;
        }
        List<UserDO> userList = JSONArray.parseArray(userDOS, UserDO.class);
        Boolean isSuccess = userService.updateUserByUserInfo(userList);
        jsonResult.setSuccess(isSuccess);
        jsonResult.setResult(isSuccess);
        return jsonResult;
    }

    /**
     * 批量删除用户
     *
     * @param userDOS
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteUserBatch")
    public JsonResult<Boolean> deleteUserBatch(String userDOS) {
        JsonResult<Boolean> jsonResult = new JsonResult();
        if (userDOS == null) {
            return jsonResult;
        }
        List<String> userIds = JSONArray.parseArray(userDOS, String.class);
        List<Integer> userIdsInt = new ArrayList<>();
        for (String Item : userIds) {
            userIdsInt.add(Integer.parseInt(Item));
        }
        Boolean isSuccess = userService.deleteUserByUserInfo(userIdsInt);
        jsonResult.setSuccess(isSuccess);
        jsonResult.setResult(isSuccess);
        return jsonResult;
    }
}
