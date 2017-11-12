package com.rwb.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rwb.common.Const;
import com.rwb.common.ServerResponse;
import com.rwb.pojo.User;
import com.rwb.service.IUserService;

/**
 * 
* @ClassName: UserManagerController
* @Description: 后台用户管理
* @author ruwenbo
* @date 2017年10月29日 下午2:08:45
*
 */
@Controller
@RequestMapping("/manager/user/")
public class UserManagerController {

	@Autowired
	private IUserService userService;
	
	@RequestMapping(value = "login.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(String username, String password, HttpSession session) {
		ServerResponse<User> resultResponse = userService.login(username, password);
		if (resultResponse.isSuccess()) {
			User user = resultResponse.getData();
			if (user.getRole() == Const.Role.ROLE_ADMIN) {
				// 说明登录的是管理员
				session.setAttribute(Const.CURRENT_USER, user);
				return resultResponse;
			} else {
				return ServerResponse.createByErrorMessage("不是管理员,无法登录");
			}
		}
		return resultResponse;
	}
}
