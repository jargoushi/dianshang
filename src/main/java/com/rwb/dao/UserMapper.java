package com.rwb.dao;

import org.apache.ibatis.annotations.Param;

import com.rwb.pojo.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    
    int checkUserName(String username);

	User selectLogin(@Param("username")String username, @Param("password")String password);

	int checkEmail(String email);
	
	String selectQuestionByUserName(String username);
	
	int checkAnswer(@Param("username")String username, @Param("question")String question, @Param("answer")String answer);
	
	int updatePasswordByUserName(@Param("username")String username, @Param("passwordNew")String passwordNew);
	
	int checkPassword(@Param("password")String password, @Param("userId")Integer userId);
	
	int checkEmailByUserId(@Param("email")String email, @Param("userId")Integer userId);
}