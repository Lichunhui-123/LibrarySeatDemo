package lichunhui.controller;



import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lichunhui.entity.Operation;
import lichunhui.service.OperationService;
import lichunhui.util.StringUtil;
import lichunhui.util.WriterUtil;

/**
 * 按钮管理控制类
 */
@Controller
@RequestMapping("operation")
public class OperationController {
	
	private Operation operation;
	//从spring容器拿业务层对象，调用业务层的方法帮我们完成工作
	@Autowired
	private OperationService<Operation> operationService;//注入业务层对象
	private int rows = 10;
	private int page = 1;
	
	//加载按钮列表
	@RequestMapping("operationList")
	public void list(HttpServletRequest request,HttpServletResponse response){
		try {
			//easyui datagrid 自身会通过 post 的形式传递 rows and page
			page = Integer.parseInt(request.getParameter("page"));//==null?1:Integer.parseInt(request.getParameter("page"));
			rows = Integer.parseInt(request.getParameter("rows"));//==null?10:Integer.parseInt(request.getParameter("rows"));
			//构造查询条件
			operation = new Operation();
			operation.setPage((page-1)*rows);
			operation.setRows(rows);
			operation.setMenuId(Integer.parseInt(request.getParameter("menuId")));
			// 调用service层的查询方法查出记录和数量
			List<Operation> list = operationService.findOperation(operation);
			int total = operationService.countOperation(operation);
			JSONObject jsonObj = new JSONObject();//new一个JSON
			jsonObj.put("total",total );
			jsonObj.put("rows", list);
	        WriterUtil.write(response,jsonObj.toString());//将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	//新增或修改按钮信息
	@RequestMapping("reserveOperation")
	public void reserveMenu(HttpServletRequest request,HttpServletResponse response,Operation operation){
		String operationId = request.getParameter("operationId");//获取按钮的id
		JSONObject result=new JSONObject();//new一个json对象
		try {
			if (StringUtil.isNotEmpty(operationId)) {  //id不为空，更新操作
				operation.setMenuId(Integer.parseInt(operationId));
				operationService.updateOperation(operation);
			} else {//id为空，添加
				operationService.addOperation(operation);
			}
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败！");
		}
		WriterUtil.write(response, result.toString());//将上述Json输出，前台ajax接收
	}
	
	
	
    //删除按钮信息
	@RequestMapping("deleteOperation")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");//获取选中的所有按钮id组，分割存入集合中
			for (String id : ids) {//根据id删除
				operationService.deleteOperation(Integer.parseInt(id));
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

