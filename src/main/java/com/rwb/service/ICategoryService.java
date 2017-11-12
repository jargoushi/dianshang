package com.rwb.service;

import com.rwb.common.ServerResponse;

public interface ICategoryService {

	public ServerResponse addCategory(String categoryName, Integer parentId);
	
	public ServerResponse updateCategoryName(Integer categoryId, String categoryName);
}
