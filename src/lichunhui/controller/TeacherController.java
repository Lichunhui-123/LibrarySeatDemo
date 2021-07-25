package lichunhui.controller;



//教师信息控制类
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import sun.misc.Perf.GetPerfAction;

import lichunhui.entity.Teacher;
import lichunhui.entity.User;
import lichunhui.service.TeacherService;
import lichunhui.service.UserService;
import lichunhui.util.StringUtil;
import lichunhui.util.WriterUtil;

@Controller
@RequestMapping("teacher")
public class TeacherController {
	
	private int page;
	private int rows;
	private Teacher teacher;
	//从spring容器拿业务层对象，调用业务层的方法帮我们完成工作
	@Autowired
	private TeacherService<Teacher> teacherService;//注入业务层对象
	private User user;
	@Autowired
	private UserService<User> userService;
	
	@RequestMapping("teacherIndex")
	public String index(){
		return "info/teacher";
	}
	
	//加载教师信息列表
	@RequestMapping("teacherList")
	public void teacherList(HttpServletRequest request,HttpServletResponse response){
		try {
			//easyui datagrid 自身会通过 post 的形式传递 rows and page 
			page = Integer.parseInt(request.getParameter("page"));   //获取当前页
			rows = Integer.parseInt(request.getParameter("rows"));   //每页记录条数
			
			// 构造查询条件
			teacher = new Teacher();
			teacher.setPage((page-1)*rows);
			teacher.setRows(rows);
			teacher.setName(request.getParameter("name"));
			teacher.setNo(request.getParameter("no"));
			//获取当前用户
			User currentUser = (User)request.getSession().getAttribute("currentUser");
			
			//角色id为2就是教师
			if(currentUser.getRoleId()==2){ 
				teacher.setNo(currentUser.getUserName());
			}
			
			// 调用service层的查询方法查出记录和数量
			List<Teacher> list = teacherService.findTeacher(teacher);  //查询所有教师信息
			int total = teacherService.countTeacher(teacher);//查询教师信息总数
			
			JSONObject jsonObj = new JSONObject();//new一个JSON
			jsonObj.put("total",total );//total代表一共有多少数据
			jsonObj.put("rows", list);//row是代表显示的页的数据
	        WriterUtil.write(response,jsonObj.toString()); //将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//新增或修改教师信息
	@RequestMapping("reserveTeacher")
	public void reserve(HttpServletRequest request,HttpServletResponse response,Teacher teacher) {
		JSONObject result = new JSONObject();    // new一个json对象
		result.put("success", true);
		String id = request.getParameter("id");//教师id
		try {
			if(StringUtil.isNotEmpty(id)){ //不为空，说明是修改操作
				teacher.setId(Integer.parseInt(id));
				teacherService.updateTeacher(teacher);  //调用service的修改方法
			} else {  //添加操作
				if(teacherService.existTeacherWithNo(teacher.getNo())==null){  // 没有重复可以添加
				    teacherService.addTeacher(teacher);
				    //自动注册
				    // 同时自动注册user表
				    user = new User();
				    user.setRoleId(2);             //角色为2表示教师
				    user.setPassword("123456");    //密码
				    user.setUserName(teacher.getNo()); //用户名
				    userService.addUser(user);//调用service的添加用户方法
				}else {
					result.put("errorMsg", "该教师工号被使用！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString()); //将上述Json输出，前台ajax接收
	}
	
	
	//删除教师信息
	@RequestMapping("deleteTeacher")
	public void deleteTeacher(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();//new一个json对象
		try {
			String[] ids=request.getParameter("ids").split(",");//获取选中的教师信息的id组
			for (String id : ids) {
				teacherService.deleteTeacher(Integer.parseInt(id));//调用service的删除教师信息方法
			}
			result.put("success", true);//设置键值对
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString()); //将上述Json输出，前台ajax接收
	}
	
 //添加班级信息时，教师选择的组合框里的教师列表
	@RequestMapping("comboList")
	public void comboList(HttpServletRequest request,HttpServletResponse response){
		try {
			List<Teacher> list = teacherService.findTeacher(new Teacher());
			JSONArray array = new JSONArray();//创建一个空的json数组
			array.addAll(list);//将list添加到Json数组中 
			WriterUtil.write(response, array.toString()); //将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

