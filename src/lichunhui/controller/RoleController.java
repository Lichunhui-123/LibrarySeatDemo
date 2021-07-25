package lichunhui.controller;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lichunhui.entity.Operation;
import lichunhui.service.OperationService;
import lichunhui.entity.Menu;

import lichunhui.entity.Role;
import lichunhui.entity.User;
import lichunhui.service.MenuService;

import lichunhui.service.RoleService;
import lichunhui.service.UserService;
import lichunhui.util.StringUtil;
import lichunhui.util.WriterUtil;

/**
 * 角色管理控制类
 */
@Controller
@RequestMapping("role")
@SuppressWarnings("unchecked")
public class RoleController {

	private int page;
	private int rows;
	private Role role;
	private Operation operation;
	//从spring容器拿业务层对象，调用业务层的方法帮我们完成工作
	@Autowired
	private UserService<User> userService;//注入业务层对象
	@Autowired
	private RoleService<Role> roleService;
	private Map map;
	private Menu menu;
	@Autowired
	private MenuService<Menu> menuService;
	@Autowired
	private OperationService<Operation> operationService;
	
	
	//角色管理操作界面
	@RequestMapping("roleIndex")
	public String index(){
		return "sys/role";
	}
	//角色信息列表
	@RequestMapping("roleList")
	public void userList(HttpServletRequest request,HttpServletResponse response){
		try {
			//easyui的datagrid会自动以post的方式发送page和row
			page = Integer.parseInt(request.getParameter("page"));//==null?1:Integer.parseInt(request.getParameter("page"));
			rows = Integer.parseInt(request.getParameter("rows"));//==null?10:Integer.parseInt(request.getParameter("rows"));
			// 构造查询条件
			role = new Role();
			role.setPage((page-1)*rows);
			role.setRows(rows);
			role.setRoleName(request.getParameter("roleName"));
			// 调用service层的查询方法查出记录和数量
			List<Role> list = findAllRole(role);//查询所有角色信息
			int total = roleService.countRole(role);//查询角色总数
			JSONObject jsonObj = new JSONObject();//new一个JSON
			jsonObj.put("total",total );//total代表一共有多少数据
			jsonObj.put("rows", list);//row是代表显示的页的数据
	        WriterUtil.write(response,jsonObj.toString());//将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//查询所有角色信息
	private List<Role> findAllRole(Role role){
		try {
			return roleService.findRole(role);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//加载所有角色信息
	@RequestMapping("roleCombobox")
	public void roleCombobox(HttpServletRequest request,HttpServletResponse response){
		try {
			JSONArray jsonArray=new JSONArray();//创建一个空的json数组
			//查询所有的角色信息
			List<Role> list = findAllRole(new Role());
			jsonArray.addAll(list);//将list添加到Json数组中 
			WriterUtil.write(response, jsonArray.toString());//将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//新增或修改角色信息
	@RequestMapping("reserveRole")
	public void addUser(HttpServletRequest request,Role role,HttpServletResponse response){
		String roleId = request.getParameter("roleId");//获取选中的角色roleId
		JSONObject result=new JSONObject();//创建空json对象
		try {
			// roleId不为空 ，说明选中了是修改角色
			if (StringUtil.isNotEmpty(roleId)) {
				role.setRoleId(Integer.parseInt(roleId));
				roleService.updateRole(role);
				result.put("success", true);
			}else {//roleId为空，说明是 添加
				if(roleService.existRoleWithRoleName(role.getRoleName())==null){  // 没有重复可以添加
					roleService.addRole(role);
					result.put("success", true);//JSONObject对象中添加键值对 ，success设置为ture
				} else {
					result.put("success", true);
					result.put("errorMsg", "该角色名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());//将上述Json输出，前台ajax接收
	}
	
	
	//删除角色信息
	@RequestMapping("deleteRole")
	public void delRole(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			//从页面提交的ids，然后分割
			String[] roleIds=request.getParameter("ids").split(",");
			for (int i=0;i<roleIds.length;i++) {
				//判断该角色下面有没有用户
				boolean b = userService.existUserWithRoleId(Integer.parseInt(roleIds[i]))==null; 
				if(!b){
					result.put("errorIndex", i);
					result.put("errorMsg", "有角色下面有用户，不能删除");
					WriterUtil.write(response, result.toString());
					return;
				}
			}
			//只有一个角色直接删除
			if (roleIds.length==1) {
				roleService.deleteRole(Integer.parseInt(roleIds[0]));
			} else {
				map = new HashMap();
				map.put("roleIds", roleIds);
				//多个角色，批量删除
				roleService.deleteRoleByRoleIds(map);
			}
			result.put("success", true);
			result.put("delNums", roleIds.length);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());//将上述Json输出，前台ajax接收
	}
	
	
	//加载权限菜单列表
	@RequestMapping("chooseMenu")
	public void chooseMenu(HttpServletRequest request,HttpServletResponse response){
		try {
			String parentId=request.getParameter("parentId");//获取根节点parentId=-1
			String roleId=request.getParameter("roleId");//获取角色Id
			//根据id查询角色信息
			role = roleService.findOneRole(Integer.parseInt(roleId));
			String menuIds = role.getMenuIds();//获取角色的权限菜单id组
			String operationIds = role.getOperationIds();//获取按钮组
			//根据根节点和该角色的权限菜单id集合，获取所有权限菜单列表
			JSONArray jsonArray=getCheckedMenusByParentId(parentId, menuIds,operationIds);
			WriterUtil.write(response, jsonArray.toString());//将上述Json输出，前台ajax接收
			System.out.println(jsonArray.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	/*
	 * 根据父节点id和权限菜单id集合获取所有权限菜单列表
	 * 先获取同一层级的菜单集合，然后遍历这个jsonArray,得到每个jsonObject,也就是每个当前层级的每个菜单，如果这个菜单是父节点，再添加它的子菜单
      */	
	public JSONArray getCheckedMenusByParentId(String parentId,String menuIds,String operationIds)throws Exception{
		//获取同一层的权限菜单集合
		JSONArray jsonArray=this.getCheckedMenuByParentId(parentId,menuIds,operationIds);
		//然后遍历这个jsonArray,得到每个jsonObject,也就是每个当前层级的每个菜单
		for(int i=0;i<jsonArray.size();i++){
			JSONObject jsonObject=jsonArray.getJSONObject(i);
			//判断是不是叶子节点
			if("open".equals(jsonObject.getString("state"))){
				continue;
			}else{
				//如果该节点下还有子节点，就把该jsonObject的id作为父节点，递归遍历它的子节点
				//然后把它的所有子节点，添加到它里面
				jsonObject.put("children", getCheckedMenusByParentId(jsonObject.getString("id"),menuIds,operationIds));
			}
		}
		return jsonArray;
	}
	
	/*
	 * 根据父节点id和权限菜单id集合获取一层权限菜单
	 */
	public JSONArray getCheckedMenuByParentId(String parentId,String menuIds,String operationIds)throws Exception{
		JSONArray jsonArray=new JSONArray();//new一个json集合
		//构造查询条件
		menu = new Menu();
		menu.setParentId(Integer.parseInt(parentId));
		//调用业务层方法，根据parentId查询同一层的菜单信息
		List<Menu> list = menuService.findMenu(menu);
		for(Menu menu : list){
			JSONObject jsonObject = new JSONObject();//new一个json对象
			int menuId = menu.getMenuId();
			jsonObject.put("id", menuId);//节点id
			jsonObject.put("text", menu.getMenuName());
			jsonObject.put("iconCls", menu.getIconCls());
			jsonObject.put("state", menu.getState());
			//判断是否有设置权限
			if (StringUtil.isNotEmpty(menuIds)) {
				//判断一个menuIds中是否包含menuId
				if (lichunhui.util.StringUtil.existStrArr(menuId+"", menuIds.split(","))) {
					jsonObject.put("checked", true);//有设置权限就设置勾选
				} 	
			}
			//获取菜单的按钮组，然后添加到菜单信息里
			jsonObject.put("children", getOperationJsonArray(menuId,operationIds));
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}
	
	
	//根据menuId查询该菜单的所有按钮
	public JSONArray getOperationJsonArray(int menuId,String operationIds){
		JSONArray jsonArray=new JSONArray();
		try {
			//构造查询条件
			operation = new Operation();
			operation.setMenuId(menuId);
			//根据menuid查询该菜单的所有按钮组
			List<Operation> list = operationService.findOperation(operation);
			for(Operation operation : list){//遍历每个按钮
				JSONObject jsonObject = new JSONObject();
				//设置前端easyui解析的格式
				int operationId = operation.getOperationId();//获取按钮id
				jsonObject.put("id", operationId);
				jsonObject.put("text", operation.getOperationName());
				jsonObject.put("iconCls", "");
				jsonObject.put("state", "open");
				//判断该角色是否有这个权限
				if (StringUtil.isNotEmpty(operationIds)) {
					if (lichunhui.util.StringUtil.existStrArr(operationId+"", operationIds.split(","))) {
						jsonObject.put("checked", true);//假如有，选中它
					} 	
				}
				jsonArray.add(jsonObject);
			}
			return jsonArray;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	//修改权限
	@RequestMapping("updateRoleMenu")
	public void updateRoleMenu(HttpServletRequest request,HttpServletResponse response){
		JSONObject result = new JSONObject();
		try {
			String roleId = request.getParameter("roleId");//获取该角色的id
			String[] ids = request.getParameter("menuIds").split(",");//menuIds;2,10000,3,4,7,10004,10006,45 这样的菜单ID和按钮ID的混合形式
			String menuIds = "";
			String operationIds = "";
			/**
			 * 采用的方案是在菜单递归之后，再加上各菜单下的按钮
			 * 采用easyui的解析方式所以字段都用的是id和text。
			 * 为了区别两者，我们规定operationId自增从10000开始
			 * menuId从1开始，在上传过来的ids中是这样的形式
			 * 2,10000,3,4,7,10004,10006,45 这样的菜单ID和按钮ID的混合形式
			 * 所以通过与10000的比较来确定哪些是菜单的哪些是按钮的
			 * 
			 */
			//以字符串存放
			for (int i = 0; i < ids.length; i++) {
				int id = Integer.parseInt(ids[i]);
				if (id>=10000) {
					operationIds += (","+id);
				} else {
					menuIds += (","+id);
				}
			}
			//构造查询条件
			role = new Role();
			role.setRoleId(Integer.parseInt(roleId));
			role.setMenuIds(menuIds.substring(1));//把前面的逗号去掉
			System.out.println(operationIds);
			role.setOperationIds(operationIds.substring(1));
			//修改角色权限
			roleService.updateRole(role);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，授权失败");
		}
		WriterUtil.write(response, result.toString());//将上述Json输出，前台ajax接收
	}
	
	
	
}

