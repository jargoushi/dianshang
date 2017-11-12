package com.rwb.controller.protal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rwb.common.Const;
import com.rwb.common.ResponseCode;
import com.rwb.common.ServerResponse;
import com.rwb.pojo.User;
import com.rwb.service.IUserService;

/**
 * 
* @ClassName: UserController
* @Description: 用户管理Controller
* @author ruwenbo
* @date 2017年10月26日 下午9:56:50
*
 */
@Controller
@RequestMapping("/user/")
public class UserController {
	
	@Autowired
	private IUserService userService;
	
	/**
	 * 
	* @Title: login
	* @Description: 用户登录
	* @param @param username
	* @param @param password
	* @param @param session
	* @param @return    设定文件
	* @return Object    返回类型
	* @throws
	 */
	@RequestMapping(value = "login.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(String username, String password, HttpSession session) {
		ServerResponse<User> result = userService.login(username, password);
		if (result.isSuccess()) {
			session.setAttribute(Const.CURRENT_USER, result.getData());
		}
		return result;
	}
	
	/**
	 * 
	* @Title: logout
	* @Description: 用户注销
	* @param @return    设定文件
	* @return ServerResponse<String>    返回类型
	* @throws
	 */
	@RequestMapping(value = "logout.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> logout(HttpSession session) {
		session.removeAttribute(Const.CURRENT_USER);
		return ServerResponse.createBySuccess();
	}
	
	/**
	 * 
	* @Title: register
	* @Description: 用户注册
	* @param @param user
	* @param @return    设定文件
	* @return ServerResponse<String>    返回类型
	* @throws
	 */
	@RequestMapping(value = "register.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> register(User user) {		
		ServerResponse<String> result = userService.register(user);
		return result;
	}
	
	/**
	 * 
	* @Title: checkValid
	* @Description: 用户名email校验
	* @param @param str
	* @param @param type
	* @param @return    设定文件
	* @return ServerResponse<String>    返回类型
	* @throws
	 */
	@RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> checkValid(String str, String type){
		return userService.checkValid(str, type);
	}
	
	/**
	 * 
	* @Title: getUserInfo
	* @Description: 获取登录用户信息
	* @param @param session
	* @param @return    设定文件
	* @return ServerResponse<User>    返回类型
	* @throws
	 */
	@RequestMapping(value = "getUserInfo.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpSession session) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
		}
		return ServerResponse.createBySuccess(user);
	}
	
	/**
	 * 
	* @Title: forgetGetQuestion
	* @Description: 找回密码答案
	* @param @param username
	* @param @return    设定文件
	* @return ServerResponse<String>    返回类型
	* @throws
	 */
	@RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetGetQuestion(String username) {
		return userService.selectQuestion(username);
	}
	
	/**
	 * 
	* @Title: forgetCheckAnswer
	* @Description: 检查问题答案
	* @param @param username
	* @param @param question
	* @param @param answer
	* @param @return    设定文件
	* @return ServerResponse<String>    返回类型
	* @throws
	 */
	@RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
		return userService.checkAnswer(username, question, answer);
	}
	
	/**
	 * 
	* @Title: forgetCheckAnswer
	* @Description: 重置密码
	* @param @param username
	* @param @param question
	* @param @param answer
	* @param @return    设定文件
	* @return ServerResponse<String>    返回类型
	* @throws
	 */
	@RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetRestToken(String username, String passwordNew, String forgetToken) {
		return userService.forgetResetPassword(username, passwordNew, forgetToken);
	}
	
	/**
	 * 
	* @Title: resetPassword
	* @Description: 登录状态的重置密码
	* @param @param session
	* @param @param passwordOld
	* @param @param passwordNew
	* @param @return    设定文件
	* @return ServerResponse<String>    返回类型
	* @throws
	 */
	@RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorMessage("用户未登录");
		}
		return userService.resetPassword(passwordOld, passwordNew, user);
	}
	
	/**
	 * 
	* @Title: resetPassword
	* @Description: 更新个人信息
	* @param @param session
	* @param @param passwordOld
	* @param @param passwordNew
	* @param @return    设定文件
	* @return ServerResponse<String>    返回类型
	* @throws
	 */
	@RequestMapping(value = "update_information.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> updatInformation(HttpSession session, User user) {
		User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
		if (currentUser == null) {
			return ServerResponse.createByErrorMessage("用户未登录");
		}
		user.setId(currentUser.getId());
		ServerResponse<User> resultResponse = userService.updatInformation(user);
		if (resultResponse.isSuccess()) {
			session.setAttribute(Const.CURRENT_USER, resultResponse.getData());
		}
		return resultResponse;
	}
	
	/**
	 * 
	* @Title: getInformation
	* @Description: 获取登录状态下的个人用户信息
	* @param @param session
	* @param @return    设定文件
	* @return ServerResponse<User>    返回类型
	* @throws
	 */
	@RequestMapping(value = "get_information.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getInformation(HttpSession session) {
		User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
		if (currentUser == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,需要强制登录status=10");
		}
		return userService.getInformation(currentUser.getId());
	}
}
