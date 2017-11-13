package com.rwb.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.rwb.common.ServerResponse;
import com.rwb.dao.CategoryMapper;
import com.rwb.pojo.Category;
import com.rwb.service.ICategoryService;

@Service("categoryService")
public class ICategoryServiceImpl implements ICategoryService {
	
	private static final Logger log = LoggerFactory.getLogger(ICategoryServiceImpl.class);
	
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
	
	@Override
	public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
		List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
		if (CollectionUtils.isEmpty(categoryList)) {
			log.info("未找到当前分类的子分类");
		}
		return ServerResponse.createBySuccess(categoryList);
	}
	
	@Override
	public ServerResponse selectCategoryAndChildrenById(Integer categoryId) {
		Set<Category> categorySet = Sets.newHashSet();
		findChildrenCategory(categorySet, categoryId);
		
		List<Integer> categoryIdList = Lists.newArrayList();
		if (categoryId != null) {
			for (Category categoryItem : categorySet) {
				categoryIdList.add(categoryItem.getId());
			}
		}
		return ServerResponse.createBySuccess(categoryIdList);
	}
	
	// 递归算法，算出子id
	private Set<Category> findChildrenCategory(Set<Category> categorySet, Integer categoryId) {
		
		Category category = categoryMapper.selectByPrimaryKey(categoryId);
		if (category != null) {
			categorySet.add(category);
		}
		// 查找子节点，递归算法一定要有结束条件
		List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
		for (Category categoryItem : categoryList) {
			findChildrenCategory(categorySet,categoryItem.getId());
		}
		return categorySet;
	} 
}
