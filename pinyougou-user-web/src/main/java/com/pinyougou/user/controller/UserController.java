package com.pinyougou.user.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pinyougou.user.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;

import entity.PageResult;
import entity.Result;
import utils.PhoneFormatCheckUtils;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private UserDetailServiceImpl userDetailService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return userService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,String checkCode){
		String checkCodeInRedis = (String)redisTemplate.boundHashOps("checkCode").get(user.getPhone());
		if(checkCodeInRedis==null){
			return new Result(false,"验证码已过期");
		}
		if(!checkCodeInRedis.equals(checkCode)){
			return new Result(false,"验证码错误");
		}
		try {
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param user
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);		
	}


	@RequestMapping("/createCheckCode")
	public Result createCheckCode(String phone){
		if(!PhoneFormatCheckUtils.isChinaPhoneLegal(phone)){
			return new Result(false,"非法手机号");
		}
		try {
			userService.createCheckCode(phone);
			return new Result(true,"验证码已发送至手机");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"内部错误");
		}
	}


	@RequestMapping("/setInfo")
	public Result SetInfo(@RequestBody TbUser user){
		try {
			user.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
			userService.setInfo(user);
			return new Result(true,"设置成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"设置失败!");
		}
	}

	@RequestMapping("/findByUsername")
	public TbUser findByUsername(){

			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			return userService.findByUsername(username);

	}

}
