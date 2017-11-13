package com.rwb.service;

import java.util.List;

import com.rwb.common.ServerResponse;
import com.rwb.pojo.Category;

public interface ICategoryService {

	public ServerResponse addCategory(String categoryName, Integer parentId);
	
	public ServerResponse updateCategoryName(Integer categoryId, String categoryName);
	
	public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);
	
	public ServerResponse selectCategoryAndChildrenById(Integer categoryId);
}
