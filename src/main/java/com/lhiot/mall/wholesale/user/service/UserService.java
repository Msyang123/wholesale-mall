package com.lhiot.mall.wholesale.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.microx.util.BeanUtils;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.user.mapper.UserMapper;
import com.lhiot.mall.wholesale.user.vo.SearchUser;
import com.lhiot.mall.wholesale.user.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserService {

    private final SnowflakeId snowflakeId;

    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper, SnowflakeId snowflakeId) {
        this.userMapper = userMapper;
        this.snowflakeId = snowflakeId;
    }

    public boolean save(User user) {
        if (user.getId() > 0) {
            return userMapper.update(user) > 0;
        } else {
            user.setId(snowflakeId.longId());
            return userMapper.insert(user) > 0;
        }
    }

    public void delete(long id) {
        userMapper.remove(id);
    }

    public User user(long id) {
        return userMapper.select(id);
    }

    public List<User> users(SearchUser param) {
        return userMapper.search(BeanUtils.toMap(param));
    }

    /**
     * 通过微信返回用户详细信息转换成系统用户
     * @param userStr
     * @return
     */
    public User convert(String userStr) throws IOException {
        ObjectMapper om = new ObjectMapper();
        Map<String,String> wxUserMap=om.readValue(userStr, Map.class);

        SearchUser searchUser=new SearchUser();
        searchUser.setLikeName(wxUserMap.get("openid"));
        List<User> users=users(searchUser);
        if (users.size()==0)
            return null;
        User returnUser=users.get(0);
        returnUser.setNickname(wxUserMap.get("nickname"));

        //FIXME 相关其余信息等user 全部完善再赋值
        /*

        String unionid = userJson.getString("unionid");
        if (StringUtil.isNull(nickname)) {
            return user;
        }
        if ("byte".equals(this.filterEmoji)) {
            nickname = replaceByte4(nickname);
        }
        if ("regular".equals(this.filterEmoji)) {
            nickname = replaceEmoji(nickname);
        }

        if(StringUtil.isNotNull(nickname)){
            user.set("nickname", nickname);
            logger.debug("=======授权用户：" + nickname + "======");
        }
        if(StringUtil.isNotNull(userJson.get("sex")+"")){
            user.set("sex", userJson.get("sex"));
        }
        if(StringUtil.isNotNull(userJson.get("headimgurl")+"")){
            user.set("user_img_id", userJson.get("headimgurl"));
        }
        String address = userJson.get("country") + " " + userJson.get("province") + " " + userJson.get("city");
        if(StringUtil.isNotNull(address)){
            user.set("user_address", address);
        }

        if(StringUtil.isNotNull(unionid)){
            user.set("union_id", unionid);
        }*/
        return returnUser;
    }
}
