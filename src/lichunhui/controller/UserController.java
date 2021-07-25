package lichunhui.controller;



import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lichunhui.util.StringUtil;

import lichunhui.entity.User;
import lichunhui.service.UserService;
import lichunhui.util.WriterUtil;

/**
 * 用户管理
 */
//定义一个控制层的类，分发处理器将会扫描使用了该注解的类的方法，并检测该方法是否使用了@RequestMapping 注解
@Controller
@RequestMapping("user")
public class UserController {

	private int page;
	private int rows;
	private User user;
	//从spring容器拿业务层对象，调用业务层的方法帮我们完成工作
	@Autowired
	private UserService<User> userService;//注入业务层对象
	
	
	//RequestMapping是一个用来处理请求地址映射的注解，可用于类或方法上
	@RequestMapping("userIndex")
	public String index(){
		return "sys/user";
	}
	
	//加载用户列表
	@RequestMapping("userList")
	public void userList(HttpServletRequest request,HttpServletResponse response){
		try {
			//easyui datagrid 自身会通过 post 的形式传递 rows and page
			page = Integer.parseInt(request.getParameter("page"));//==null?1:Integer.parseInt(request.getParameter("page"));
			rows = Integer.parseInt(request.getParameter("rows"));//==null?10:Integer.parseInt(request.getParameter("rows"));
			//构造查询条件
			user = new User();
			user.setPage((page-1)*rows);
			user.setRows(rows);
			user.setUserName(request.getParameter("userName"));
			String roleId = request.getParameter("roleId");
			if (StringUtil.isNotEmpty(roleId)) {
				user.setRoleId(Integer.parseInt(roleId));
			} else {
				user.setRoleId(null);
			}
			// 调用service层的查询方法查出记录和数量
			List<User> list = userService.findUser(user);//查询所有用户信息
			int total = userService.countUser(user);//查询用户信息总数
			JSONObject jsonObj = new JSONObject();//new一个JSON
			//返回到前台的值必须按照如下的格式包括 total and rows 
			jsonObj.put("total",total );//total代表一共有多少数据
			jsonObj.put("rows", list);//row是代表显示的页的数据
	        WriterUtil.write(response,jsonObj.toString());//将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// 新增或修改
	@RequestMapping("reserveUser")
	public void reserveUser(HttpServletRequest request,User user,HttpServletResponse response){
		String userId = request.getParameter("userId");
		//创建一个空的JSONObject对象
		JSONObject result=new JSONObject();
		try {
			// userId不为空 ，说明选中了用户是修改
			if (StringUtil.isNotEmpty(userId)) {   
				user.setUserId(Integer.parseInt(userId));
				userService.updateUser(user);
				result.put("success", true);
			}else {   //userId为空，说明是 添加
				if(userService.existUserWithUserName(user.getUserName())==null){  // 没有重复可以添加
					userService.addUser(user);
					result.put("success", true);//JSONObject对象中添加键值对 ，success设置为ture
				} else {
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());//将上述Json输出，前台ajax接收
	}
	
	//删除用户信息
	@RequestMapping("deleteUser")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			//从页面提交的ids，然后分割
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				//通过userId查询单个用户信息
				user = userService.findOneUser(Integer.parseInt(id));
				//删除用户信息
				userService.deleteUser(Integer.parseInt(id));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());//将上述Json输出，前台ajax接收
	}
}

