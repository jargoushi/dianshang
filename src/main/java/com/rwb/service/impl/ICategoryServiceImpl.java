package com.rwb.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rwb.common.ServerResponse;
import com.rwb.dao.CategoryMapper;
import com.rwb.pojo.Category;
import com.rwb.service.ICategoryService;

@Service("categoryService")
public class ICategoryServiceImpl implements ICategoryService {
	
	@Autowired
	private CategoryMapper categoryMapper;

	@Override
	public ServerResponse addCategory(String categoryName, Integer parentId) {
		if (parentId == null || StringUtils.isBlank(categoryName)) {
			return ServerResponse.createByErrorMessage("添加品类参数错误");
		}
		Category category = new Category();
		category.setName(categoryName);
		category.setParentId(parentId);
		category.setStatus(true);
		
		int insert = categoryMapper.insert(category);
		
		if (insert > 0) {
			return ServerResponse.createBySuccess("添加品类成功");
		}
		return ServerResponse.createByErrorMessage("添加品类失败");
	}
	
	@Override
	public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
		if (categoryId == null || StringUtils.isBlank(categoryName)) {
			return ServerResponse.createByErrorMessage("更新品类参数错误");
		}
		Category category = new Category();
		category.setId(categoryId);
		category.setName(categoryName);
		int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
		if (rowCount > 0) {
			return ServerResponse.createBySuccess("更新品类成功");
		}
		return ServerResponse.createByErrorMessage("更新品类失败");
	}
	
	public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
		
	}
}
