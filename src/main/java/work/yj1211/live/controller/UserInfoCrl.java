package work.yj1211.live.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import work.yj1211.live.factory.ResultFactory;
import work.yj1211.live.service.UserService;
import work.yj1211.live.vo.LiveRoomInfo;
import work.yj1211.live.vo.Result;
import work.yj1211.live.vo.UpdateInfo;
import work.yj1211.live.vo.UserInfo;
import work.yj1211.live.vo.platformArea.AreaSimple;

import javax.websocket.server.PathParam;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
public class UserInfoCrl {

    private static final Logger logger = LoggerFactory.getLogger(UserInfo.class);

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     * @param username 用户名
     * @param password 加密后的用户密码
     * @return 登录结果
     */
    @CrossOrigin
    @RequestMapping(value = "/api/login", method = RequestMethod.POST, produces = "application/json; charset = UTF-8")
    @ResponseBody
    public Result login(@PathParam("username")String username, @PathParam("password") String password){
        UserInfo user = userService.login(username, password);
        if (null == user){
            log.info(username+"---账户密码错误");
            return ResultFactory.buildFailResult("账户密码错误");
        }
        log.info(username+"---登陆成功");
        logger.info(username+"---登陆成功");
        return ResultFactory.buildSuccessResult(user);
    }

    /**
     * 用户注册接口
     * @param username 新用户名
     * @param nickname 新昵称
     * @param password 新密码
     * @return 用户名已存在或注册成功
     */
    @CrossOrigin
    @RequestMapping(value = "/api/register", method = RequestMethod.POST, produces = "application/json; charset = UTF-8")
    @ResponseBody
    public Result register(@PathParam("username")String username, @PathParam("nickname")String nickname, @PathParam("password")String password){
        if(null != userService.findUserByName(username)){
            log.info(username+"---注册失败---用户名已存在");
            return ResultFactory.buildFailResult("用户名已存在");
        }

        UserInfo user = new UserInfo();
        String uuid = UUID.randomUUID().toString().replace("-","");
        user.setUid(uuid);
        user.setUserName(username);
        user.setNickName(nickname);
        user.setPassword(password);
        userService.register(user);
        log.info(username+"---注册成功");
        logger.info(username+"---注册成功");
        return ResultFactory.buildSuccessResult(user);
    }

    /**
     * 关注分区
     * @param uid
     * @param areaType
     * @param area
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/api/live/followArea", method = RequestMethod.GET, produces = "application/json; charset = UTF-8")
    @ResponseBody
    public Result followArea(@PathParam("uid")String uid, @PathParam("areaType")String areaType, @PathParam("area")String area){
        userService.followArea(areaType, area, uid);
        return ResultFactory.buildSuccessResult("关注成功");
    }

    /**
     * 取消关注分区
     * @param uid
     * @param areaType
     * @param area
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/api/live/unFollowArea", method = RequestMethod.GET, produces = "application/json; charset = UTF-8")
    @ResponseBody
    public Result unFollowArea(@PathParam("uid")String uid, @PathParam("areaType")String areaType, @PathParam("area")String area){
        userService.unFollowArea(areaType, area, uid);
        return ResultFactory.buildSuccessResult("已经取消关注");
    }

    /**
     * 获取用户关注的所有分区
     * @param uid 用户账户id
     * @return 所有关注的直播间的List
     */
    @CrossOrigin
    @RequestMapping(value = "/api/live/getFollowedAreas", method = RequestMethod.GET, produces = "application/json; charset = UTF-8")
    @ResponseBody
    public Result getFollowedAreas(@PathParam("uid")String uid){
        List<AreaSimple> areaList = userService.getAreasByUid(uid);
        return ResultFactory.buildSuccessResult(areaList);
    }

    /**
     * 关注直播间
     * @param platform 直播平台
     * @param roomId 直播房间号
     * @param uid 用户账号id
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/api/live/follow", method = RequestMethod.GET, produces = "application/json; charset = UTF-8")
    @ResponseBody
    public Result follow(@PathParam("platform")String platform, @PathParam("roomId")String roomId, @PathParam("uid")String uid){
        userService.followRoom(platform, roomId, uid);
        log.info(uid+"---关注成功---直播间信息：" + platform + "-" + roomId);
        return ResultFactory.buildSuccessResult("关注成功");
    }

    /**
     * 取消关注
     * @param platform
     * @param roomId
     * @param uid
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/api/live/unFollow", method = RequestMethod.GET, produces = "application/json; charset = UTF-8")
    @ResponseBody
    public Result unFollow(@PathParam("platform")String platform, @PathParam("roomId")String roomId, @PathParam("uid")String uid){
        userService.unFollowRoom(platform, roomId, uid);
        log.info(uid+"---取消关注成功---直播间信息：" + platform + "-" + roomId);
        return ResultFactory.buildSuccessResult("已经取消关注");
    }

    /**
     * 信息修改接口
     * @param userInfo 新的用户信息
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/api/live/changeUserInfo", method = RequestMethod.POST, produces = "application/json; charset = UTF-8")
    @ResponseBody
    public Result changeUserInfo(@RequestBody UserInfo userInfo){
        userService.changeUserInfo(userInfo);
        userService.changeUserBan(userInfo);
        UserInfo userInfo1 = userService.findUserByName(userInfo.getUserName());
        return ResultFactory.buildSuccessResult(userInfo1);
    }

    /**
     * 用户密码修改接口
     * @param userName 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/api/live/changePassword", method = RequestMethod.GET, produces = "application/json; charset = UTF-8")
    @ResponseBody
    public Result changePassword(@PathParam("userName")String userName, @PathParam("oldPassword")String oldPassword, @PathParam("newPassword")String newPassword){
        UserInfo user = userService.login(userName, oldPassword);
        if (null == user){
            log.info(userName + "---修改密码失败---旧密码错误");
            return ResultFactory.buildFailResult("旧密码错误");
        }else{
            userService.changePassword(userName, newPassword);
            log.info(userName + "---修改密码成功");
            return ResultFactory.buildSuccessResult("密码修改成功");
        }
    }

    /**
     * AndroidApp版本更新接口
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/api/live/versionUpdate", method = RequestMethod.GET, produces = "application/json; charset = UTF-8")
    @ResponseBody
    public Result versionUpdate(){
        UpdateInfo updateInfo = userService.checkUpdate();
        return ResultFactory.buildSuccessResult(updateInfo);
    }
}
