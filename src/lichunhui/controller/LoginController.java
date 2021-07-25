package lichunhui.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lichunhui.util.StringUtil;
import lichunhui.util.WriterUtil;
import lichunhui.entity.Menu;
import lichunhui.entity.Role;
import lichunhui.entity.User;
import lichunhui.service.MenuService;
import lichunhui.service.RoleService;
import lichunhui.service.UserService;

/**
 * 登录
 */
//登陆控制类
@Controller
@SuppressWarnings("unchecked")//抑制没有进行类型检查操作的警告
public class LoginController {

	private User user;
	private User currentUser;//当前用户
	//从spring容器拿业务层对象，调用业务层的方法帮我们完成工作
	@Autowired
	private UserService<User> userService;//注入业务层对象
	@Autowired
	private MenuService<Menu> menuService;
	private Role role;
	@Autowired
	private RoleService<Role> roleService;
	private Map map;
	
	//实现登陆功能
	//RequestMapping是一个用来处理请求地址映射的注解
	@RequestMapping(value={"login","aLogin"})
	public void login(HttpServletRequest request,HttpServletResponse response){
		
		try {
			//获取session对象
			HttpSession session = request.getSession();
			String userName=request.getParameter("userName");//获取表单提交的用户名
			String password=request.getParameter("password");//获取表单提交的密码
			String imageCode=request.getParameter("imageCode");//验证码
			String roleId=request.getParameter("roleId");//角色id
			request.setAttribute("userName", userName);
			request.setAttribute("password", password);
			request.setAttribute("imageCode", imageCode);
			
			//判断用户名或者密码是否为空
			if(StringUtil.isEmpty(userName)||StringUtil.isEmpty(password)){
				
				request.setAttribute("error", "账户或密码为空");
				if (StringUtil.isNotEmpty(roleId)) {//通过roleId判断用户是管理员还是普通用户
					request.getRequestDispatcher("adminLogin.htm").forward(request, response);
				} else {
					request.getRequestDispatcher("login.jsp").forward(request, response);
				}
				return;
			}
			//判断验证码是否为空
			if(StringUtil.isEmpty(imageCode)){
				request.setAttribute("error", "验证码为空");
				if (StringUtil.isNotEmpty(roleId)) {//通过roleId判断用户是管理员还是普通用户
					response.sendRedirect("adminLogin.htm");
				} else {
					request.getRequestDispatcher("login.jsp").forward(request, response);
				}
				return;
			}
			//判断验证码是否错误
			if(!imageCode.equals(session.getAttribute("sRand"))){
				request.setAttribute("error", "验证码错误");
				if (StringUtil.isNotEmpty(roleId)) {//通过roleId判断用户是管理员还是普通用户
					response.sendRedirect("adminLogin.htm");
				} else {
					request.getRequestDispatcher("login.jsp").forward(request, response);
				}
				return;
			}
			map = new HashMap<String, String>();
			map.put("userName", userName);
			map.put("password", password);
			currentUser = userService.loginUser(map);//获取当前用户
			//用户不存在
			if(currentUser==null){
				request.setAttribute("error", "用户名或密码错误");
				if (currentUser.getRoleId()==1) {//通过roleId判断用户是管理员还是普通用户
					response.sendRedirect("adminLogin.htm");
				} else {
					request.getRequestDispatcher("login.jsp").forward(request, response);
				}
				
			}else{
				//验证成功
				role = roleService.findOneRole(currentUser.getRoleId());//获取当前用户的角色ID，根据角色ID查询当前用户的角色信息
				String roleName=role.getRoleName();//获取角色名
				currentUser.setRoleName(roleName);//设置当前用户的角色名
				session.setAttribute("currentUser", currentUser);//设置当前用户
				//设置当前用户的按钮组
				session.setAttribute("currentOperationIds", role.getOperationIds());
				//跳转到主页
				response.sendRedirect("main.htm");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
	// 管理员在浏览器输入http://127.0.0.1:8080/LibrarySeat/adminLogin.htm进入登陆界面
	@RequestMapping("adminLogin")
	public String toAdminLogin(){
		return "AdminLogin";
	}
	
	// 进入系统主界面
	@RequestMapping("main")
	public String toMain(HttpServletRequest request){
		Object attribute = request.getSession().getAttribute("currentUser");
		if(attribute == null){
			return "redirect:login.htm";
		}
		//return "main";
		return "sys/main";
	}
	
	
	// 加载最上级左菜单树
	@RequestMapping("menuTree")
	public void getMenuTree(HttpServletRequest request,HttpServletResponse response){
		try {
			//获取页面提交的parentId，parentId=-1,即为根节点
			String parentId = request.getParameter("parentId");
			//获取当前用户
			currentUser = (User) request.getSession().getAttribute("currentUser");
			//调用业务层方法获取当前用户的角色信息
			role = roleService.findOneRole(currentUser.getRoleId());
			//获取角色信息中的menuIds属性，并分割
			String[] menuIds = role.getMenuIds().split(",");
			//构造查询条件
			map = new HashMap();
			map.put("parentId",parentId);
			map.put("menuIds", menuIds);
			//递归加载所有菜单列表
			JSONArray jsonArray = getMenusByParentId(parentId, menuIds);
			WriterUtil.write(response, jsonArray.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// 根据父节点和menuIds递归加载所有的菜单列表
	public JSONArray getMenusByParentId(String parentId,String[] menuIds)throws Exception{
		//将所有的树菜单放入easyui要求格式的json
		//获取同一层的权限菜单集合
		JSONArray jsonArray=this.getMenuByParentId(parentId,menuIds);
		//然后遍历这个jsonArray,得到每个jsonObject,也就是每个当前层级的每个菜单
		for(int i=0;i<jsonArray.size();i++){
			JSONObject jsonObject=jsonArray.getJSONObject(i);
			//判断该节点是否有子节点，我设置有子节点的state:closed,没子节点的为open
			if("open".equals(jsonObject.getString("state"))){
				continue;
			}else{//如果该节点下还有子节点，就把该jsonObject的id作为父节点，递归遍历它的子节点
				//然后把它的所有子节点，添加到它里面
				jsonObject.put("children", getMenusByParentId(jsonObject.getString("id"),menuIds));
			}
		}
		return jsonArray;
	}
	
	
	// 根据父节点id和menuIds查询同一层的菜单
	public JSONArray getMenuByParentId(String parentId,String[] menuIds)throws Exception{
		JSONArray jsonArray=new JSONArray();//new一个json集合
		//构造查询条件
		map= new HashMap();
		map.put("parentId",parentId);
		map.put("menuIds", menuIds);
		//调用业务层方法，根据parentId和menuIds查询同一层的菜单信息
		List<Menu> list = menuService.menuTree(map);
		for(Menu menu : list){
			JSONObject jsonObject = new JSONObject();//new一个json对象
			//设置easyui接收的json格式
			jsonObject.put("id", menu.getMenuId());
			jsonObject.put("text", menu.getMenuName());
			jsonObject.put("iconCls", menu.getIconCls());
			JSONObject attributeObject = new JSONObject();
			attributeObject.put("menuUrl", menu.getMenuUrl());
			//判断是不是有子孩子，人工结束递归树
			if(!hasChildren(menu.getMenuId(), menuIds)){
				jsonObject.put("state", "open");
			}else{
				jsonObject.put("state", menu.getState());				
			}
			jsonObject.put("attributes", attributeObject);
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}
	
	
	// 判断是不是有子孩子，人工结束递归树
	public boolean hasChildren(Integer parentId,String[] menuIds){
		boolean flag = false;
		try {
			map= new HashMap();
			map.put("parentId",parentId);
			map.put("menuIds", menuIds);
			List<Menu> list = menuService.menuTree(map);
			if (list == null || list.size()==0) {
				flag = false;
			}else {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	
	
	
	// 修改密码
	@RequestMapping("updatePassword")
	public void updatePassword(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();//创建一个空的json对象
		try {
			String userId=request.getParameter("userId");//获取表单提交的userId
			String newPassword=request.getParameter("newPassword");//获取表单提交的新密码
			user=new User();
			user.setUserId(Integer.parseInt(userId));
			user.setPassword(newPassword);
			//更新密码
			userService.updateUser(user);
			result.put("success", "true");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", "true");
			result.put("errorMsg", "对不起！密码修改失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	//安全退出
	@SuppressWarnings("unused")//抑制没被使用过的代码的警告
	@RequestMapping("logout")
	private void logout(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Integer roleId = Integer.parseInt(request.getParameter("roleId"));
		if(roleId==1){
			//清除session的所有信息
			request.getSession().invalidate();
			//通过response对象的sendRedirect方法重定向，跳转到登陆界面
			response.sendRedirect("adminLogin.htm");
		}else {
			//清除session的所有信息
			request.getSession().invalidate();
			//通过response对象的sendRedirect方法重定向，跳转到登陆界面
			response.sendRedirect("login.jsp");
		}
		
	}
	
}
