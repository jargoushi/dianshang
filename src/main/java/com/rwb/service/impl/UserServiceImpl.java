package com.rwb.service.impl;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rwb.common.Const;
import com.rwb.common.ServerResponse;
import com.rwb.common.TokenCache;
import com.rwb.dao.UserMapper;
import com.rwb.pojo.User;
import com.rwb.service.IUserService;
import com.rwb.util.MD5Util;

/**
 * 
* @ClassName: UserServiceImpl
* @Description: 用户管理Service
* @author ruwenbo
* @date 2017年10月26日 下午10:07:17
*
 */
@Service("userService")
public class UserServiceImpl implements IUserService{
	
	@Autowired
	private UserMapper userMapper;

	@Override
	public ServerResponse<User> login(String username, String password) {
		int resultCount = userMapper.checkUserName(username);
		if (resultCount == 0) {
			return ServerResponse.createByErrorMessage("用户名不存在");
		}
		//TODO 密码登录MD5
		String md5Password = MD5Util.MD5EncodeUtf8(password);
		User user = userMapper.selectLogin(username, md5Password); 
		if (user == null) {
			return ServerResponse.createByErrorMessage("密码错误");
		}
		user.setPassword(StringUtils.EMPTY);
		return ServerResponse.createBySuccess(user);
	}

	@Override
	public ServerResponse<String> register(User user) {
		ServerResponse<String> validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
		if (!validResponse.isSuccess()) {
			return validResponse;
		}
		validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
		if (!validResponse.isSuccess()) {
			return validResponse;
		}
		user.setRole(Const.Role.ROLE_CUSTOMER);
		// md5加密
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		
		int resultCount = userMapper.insert(user);
		if (resultCount == 0) {
			return ServerResponse.createByErrorMessage("注册失败");
		}
		return ServerResponse.createBySuccess("注册成功");
	}

	@Override
	public ServerResponse<String> checkValid(String str, String type) {
		if (StringUtils.isNotBlank(type)) {
			// 开始校验email
			if (Const.EMAIL.equals(type)) {
				int resultCount = userMapper.checkEmail(str);
				if (resultCount > 0) {
					return ServerResponse.createByErrorMessage("email已存在");
				}
			}
			// 开始校验用户名
			if (Const.USERNAME.equals(type)) {
				int resultCount = userMapper.checkUserName(str);
				if (resultCount > 0) {
					return ServerResponse.createByErrorMessage("用户名已存在");
				}
			}
		} else {
			return ServerResponse.createByErrorMessage("参数错误");
		}
		return ServerResponse.createBySuccessMessage("校验成功");
	}
	
	@Override
	public ServerResponse<String> selectQuestion(String username) {
		ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
		if (validResponse.isSuccess()) {
			// 用户名不存在
			return ServerResponse.createByErrorMessage("用户名不存在");
		}
		String question = userMapper.selectQuestionByUserName(username);
		if (StringUtils.isNoneBlank(question)) {
			return ServerResponse.createBySuccess(question);
		}
		return ServerResponse.createByErrorMessage("找回密码的问题是空的");
	}
	
	@Override
	public ServerResponse<String> checkAnswer(String username, String question, String answer) {
		int resultCount = userMapper.checkAnswer(username, question, answer);
		if (resultCount > 0) {
			// 说明是问题及问题答案是这个用户并且是答案的
			String forgetToken = UUID.randomUUID().toString();
			// 添加本地缓存
			TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
			return ServerResponse.createBySuccess(forgetToken);
		}
		return ServerResponse.createByErrorMessage("问题的答案错误");
	}
	
	@Override
	public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
		if (StringUtils.isNotBlank(forgetToken)) {
			return ServerResponse.createByErrorMessage("参数错误");
		}
		ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
		if (validResponse.isSuccess()) {
			// 用户不存在
			return ServerResponse.createByErrorMessage("用户不存在");
		}
		String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
		if (StringUtils.isBlank(token)) {
			return ServerResponse.createByErrorMessage("token无效或者过期");
		}
		if (StringUtils.equals(forgetToken, token)) {
			String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
			int resultCount = userMapper.updatePasswordByUserName(username, md5Password);
			if (resultCount > 0) {
				return ServerResponse.createBySuccessMessage("用户密码修改成功");
			}
		} else {
			return ServerResponse.createByErrorMessage("token错误,请重新获取修改重置密码的token");
		}
		return ServerResponse.createByErrorMessage("用户密码修改失败");
	}
	
	@Override
	public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
		// 防止横向越权，一定要校验这个用户的旧密码，是属于这个用户的
		int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
		if (resultCount == 0) {
			return ServerResponse.createByErrorMessage("旧密码错误");
		}
		user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
		int updateCount = userMapper.updateByPrimaryKeySelective(user);
		if (updateCount > 0) {
			return ServerResponse.createBySuccess("密码更新成功");
		}
		return ServerResponse.createByErrorMessage("密码更新失败");
	}
	
	@Override
	public ServerResponse<User> updatInformation(User user) {
		// username 不能为更新
		// emai 要校验，校验新的email是否已经存在，并且存在的emai如果相同的话，不能是我们当前用户的
		int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
		if (resultCount > 0) {
			return ServerResponse.createByErrorMessage("email已经存在，请更换email再尝试更新");
		}
		User updateUser = new User();
		updateUser.setId(user.getId());
		updateUser.setEmail(user.getEmail());
		updateUser.setPhone(user.getPhone());
		updateUser.setQuestion(user.getQuestion());
		updateUser.setAnswer(user.getAnswer());
		int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
		if (updateCount > 0) {
			return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
		}
		return ServerResponse.createByErrorMessage("更新个人信息失败");
	}
	
	@Override
	public ServerResponse<User> getInformation(Integer userId) {
		User user = userMapper.selectByPrimaryKey(userId);
		if (user == null) {
			return ServerResponse.createByErrorMessage("找不到当前用户");
		}
		user.setPassword(StringUtils.EMPTY);
		return ServerResponse.createBySuccess(user);
	}
	
	@Override
	public ServerResponse checkAdminRole(User user) {
		if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
			return ServerResponse.createBySuccess();
		} else {
			return ServerResponse.createByError();
		}
	}

}
