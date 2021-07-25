package lichunhui.controller;




import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



import lichunhui.entity.Menu;
import lichunhui.entity.Operation;
import lichunhui.service.MenuService;
import lichunhui.service.OperationService;
import lichunhui.util.StringUtil;
import lichunhui.util.WriterUtil;

/**
 * 菜单管理控制类
 */
@RequestMapping("menu")
@Controller
public class MenuController {

	private Menu menu;
	private Operation operation;
	//从spring容器拿业务层对象，调用业务层的方法帮我们完成工作
	@Autowired
	private MenuService<Menu> menuService;//注入业务层对象
	@Autowired
	private OperationService<Operation> operationService;
	
	
	//菜单管理操作界面
	@RequestMapping("menuIndex")
	public String index(){
		return "sys/menu";
	}
	
	//加载树形菜单表格信息
	@RequestMapping("treeGridMenu")
	public void treeGridMenu(HttpServletRequest request,HttpServletResponse response){
		try {
			String parentId=request.getParameter("parentId");//获取根节点id：parentId=-1
			//根据父节点id获取子菜单列表
			JSONArray jsonArray = getListByParentId(parentId);
			WriterUtil.write(response, jsonArray.toString());//将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/*
	 * 根据父节点id获取所有菜单列表
	 * 先获取同一层级的菜单集合，然后遍历这个jsonArray,得到每个jsonObject,也就是每个当前层级的每个菜单，如果这个菜单是根节点，再添加它的子菜单
      */
	public JSONArray getListByParentId(String parentId)throws Exception{
		//根据父节点id获取同一层的菜单集合
		JSONArray jsonArray=this.getTreeGridMenuByParentId(parentId);
		//然后遍历这个jsonArray,得到每个jsonObject,也就是每个当前层级的每个菜单
		for(int i=0;i<jsonArray.size();i++){
			JSONObject jsonObject=jsonArray.getJSONObject(i);
			//判断该节点是否有子节点，我设置有子节点的state:closed,没子节点的为open
			if("open".equals(jsonObject.getString("state"))){
				continue;
			}else{
				//如果该节点下还有子节点，就把该jsonObject的id作为父节点，递归遍历它的子节点
				//然后把它的所有子节点，添加到它里面
				jsonObject.put("children", getListByParentId(jsonObject.getString("id")));
			}
		}
		return jsonArray;
	}
	
	
	/*
	 * 根据父节点id获取一层的菜单集合
	 */
	public JSONArray getTreeGridMenuByParentId(String parentId)throws Exception{
		JSONArray jsonArray=new JSONArray();//new一个json集合
		//构造查询条件
		menu = new Menu();
		menu.setParentId(Integer.parseInt(parentId));
		//调用业务层方法，根据parentId查询同一层的菜单信息
		List<Menu> list = menuService.findMenu(menu);
		for(Menu menu : list){
			JSONObject jsonObject = new JSONObject();//new一个json对象
			//设置菜单的信息
			Integer menuId = menu.getMenuId();
			jsonObject.put("id", menuId);   
			jsonObject.put("text", menu.getMenuName());
			jsonObject.put("iconCls", menu.getIconCls());
			jsonObject.put("state", menu.getState());
			jsonObject.put("seq", menu.getSeq());
			jsonObject.put("menuUrl", menu.getMenuUrl());
			jsonObject.put("menuDescription", menu.getMenuDescription());
			
			// 加上该页面菜单下面的按钮
			operation = new Operation();
			operation.setMenuId(menuId);
			List<Operation> operaList = operationService.findOperation(operation);
			if (operaList!=null && operaList.size()>0) {
				String string = "";
				for (Operation o : operaList) {
					string += o.getOperationName() + ",";
				}
				jsonObject.put("operationNames", string.substring(0,string.length()-1));
			} else {
				jsonObject.put("operationNames", "");
			}


			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}
	
	
	//新增或修改菜单信息
	@RequestMapping("reserveMenu")
	public void reserveMenu(HttpServletRequest request,HttpServletResponse response,Menu menu){
		String menuId = request.getParameter("menuId");//获取所选节点的id
		JSONObject result=new JSONObject();//new一个json对象
		try {
			if (StringUtil.isNotEmpty(menuId)) {  //id不为空，则是更新操作
				menu.setMenuId(Integer.parseInt(menuId));
				menuService.updateMenu(menu);//根据id修改菜单信息
			} else {//id为空，选的是添加
				String parentId = request.getParameter("parentId");//把选中的节点作为父节点
				menu.setParentId(Integer.parseInt(parentId));//为添加的菜单信息设置父节点
				if(menuService.existMenuWithMenuName(menu.getMenuName())==null){  // 没有重复可以添加
				//判断该节点是否为叶子节点
				  if (isLeaf(parentId)) {//是叶子节点
					  // 添加操作
					  menuService.addMenu(menu);  
					
					  // 更新他上级状态。变成CLOSED
					  menu = new Menu();
					  menu.setMenuId(Integer.parseInt(parentId));
					  menu.setState("closed");//更新状态
					  menuService.updateMenu(menu);
				  } else {//不是叶子节点直接添加
					  menuService.addMenu(menu);
				}
			}else {
				result.put("errorMsg", "该菜单名被使用！");
			}
		 }
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败！");
		}
		WriterUtil.write(response, result.toString());//将上述Json输出，前台ajax接收
	}
	
	
	
	// 判断是不是叶子节点
	public boolean isLeaf(String menuId){
		boolean flag = false;
		try {
			//构造查询条件
			menu = new Menu();
			menu.setParentId(Integer.parseInt(menuId));
			//调用业务层方法，根据parentid查询该节点下的子节点列表
			List<Menu> list = menuService.findMenu(menu);
			if (list==null || list.size()==0) {//列表为空，证明该节点为叶子节点
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	
	
	//删除菜单信息
	@RequestMapping("deleteMenu")
	public void deleteMenu(HttpServletRequest request,HttpServletResponse response){
		JSONObject result = new JSONObject();
		try {
			String menuId = request.getParameter("menuId");//获取该节点的menuid
			String parentId = request.getParameter("parentId");//获取该节点的parentid
			if (!isLeaf(menuId)) {  //不是叶子节点，说明有子菜单，不能删除
				result.put("errorMsg", "该菜单下面有子菜单，不能直接删除");
			} else {//是叶子节点
				//构造查询条件
				menu = new Menu();
				menu.setParentId(Integer.parseInt(parentId));
				//查询父节点下的子节点总数
				int sonNum = menuService.countMenu(menu);
				if (sonNum == 1) {  
					// 只有一个孩子，删除该孩子，父节点变子节点了，且把父亲状态置为open
					menu = new Menu();
					menu.setMenuId(Integer.parseInt(parentId));
					menu.setState("open");
					menuService.updateMenu(menu);
					
					menuService.deleteMenu(Integer.parseInt(menuId));
				} else {
					//不只一个孩子，直接删除
					menuService.deleteMenu(Integer.parseInt(menuId));
				}
				result.put("success", true);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，删除失败！");
		}
		WriterUtil.write(response, result.toString());
	}
	
	 

}

