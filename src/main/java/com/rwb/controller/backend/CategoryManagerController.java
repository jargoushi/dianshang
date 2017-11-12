package com.rwb.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rwb.common.Const;
import com.rwb.common.ResponseCode;
import com.rwb.service.ICategoryService;
import com.rwb.service.IUserService;
import com.rwb.common.ServerResponse;
import com.rwb.common.ServerResponse;
import com.rwb.pojo.User;
import com.rwb.service.IUserService;

/**
 * 
* @ClassName: CategoryManagerController
* @Description: 分类管理Controller
* @author ruwenbo
* @date 2017年11月12日 下午6:14:44
*
 */
@Controller
@RequestMapping("/manager/category/")
public class CategoryManagerController {
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ICategoryService categoryService;

	@RequestMapping("add_category.do")
	@ResponseBody
	public ServerResponse addCategory(HttpSession session, String categoryName,
			@RequestParam(value = "parentId", defaultValue = "0") int parentId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
		}
		// 校验一下是否是管理员
		if (userService.checkAdminRole(user).isSuccess()) {
			// 是管理员
			// 添加我们处理分类的逻辑
			return categoryService.addCategory(categoryName, parentId);
		} else {
			return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
		}
	}
	
	@RequestMapping("set_category_name.do")
	@ResponseBody
	public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
		}
		// 校验一下是否是管理员
		if (userService.checkAdminRole(user).isSuccess()) {
			// 是管理员
			// 添加更新品类逻辑
			return categoryService.updateCategoryName(categoryId, categoryName);
		} else {
			return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
		}
	}
	
	public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
		}
		// 校验一下是否是管理员
		if (userService.checkAdminRole(user).isSuccess()) {
			// 是管理员
			// 查询子节点的category信息不递归
			
			
		} else {
			return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
		}
	}
	
}
