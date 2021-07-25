package lichunhui.controller;



//阅览室类型控制类
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lichunhui.entity.Room;
import lichunhui.entity.Type;
import lichunhui.service.RoomService;
import lichunhui.service.TypeService;
import lichunhui.util.StringUtil;
import lichunhui.util.WriterUtil;

@Controller
@RequestMapping("type")
public class TypeController {

	private int page;
	private int rows;
	private Type type;
	//从spring容器拿业务层对象，调用业务层的方法帮我们完成工作
	@Autowired
	private TypeService<Type> typeService;//注入业务层对象

	@Autowired
	private RoomService<Room> roomService;//注入业务层对象
	//阅览室类型管理界面
	@RequestMapping("typeIndex")
	public String index() {
		return "readingroom/type";
	}

	//加载阅览室类型列表
	@RequestMapping("typeList")
	public void typeList(HttpServletRequest request,HttpServletResponse response) {
		try {
			//easyui datagrid 自身会通过 post 的形式传递 rows and page
			page = Integer.parseInt(request.getParameter("page")); // 获取当前页
			rows = Integer.parseInt(request.getParameter("rows")); // 每页记录条数

			// 构造查询条件
			type = new Type();
			type.setPage((page - 1) * rows);
			type.setRows(rows);

			// 调用service层的查询方法查出记录和数量
			List<Type> list = typeService.findType(type);//查询所有阅览室信息
			int total = typeService.countType(type);//查询阅览室类型总数

			JSONObject jsonObj = new JSONObject();// new一个JSON
			jsonObj.put("total", total);// total代表一共有多少数据
			jsonObj.put("rows", list);// row是代表显示的页的数据
			WriterUtil.write(response, jsonObj.toString()); // 将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//新增或修改阅览室类型
	@RequestMapping("reserveType")
	public void reserve(HttpServletRequest request,HttpServletResponse response, Type type) {
		JSONObject result = new JSONObject(); // new一个json对象
		result.put("success", true);
		String id = request.getParameter("id");  //阅览室类型的id
		try {
			if (StringUtil.isNotEmpty(id)) { // 不为空，说明是修改操作
				type.setId(Integer.parseInt(id));
				typeService.updateType(type); // 调用service的修改方法
			} else { // 添加操作
				if(typeService.existTypeWithName(type.getName())==null){  // 没有重复可以添加
				typeService.addType(type);
				}else {
					result.put("errorMsg", "该类型被使用！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());// 将上述Json输出，前台ajax接收
	}

	//删除阅览室类型
	@RequestMapping("deleteType")
	public void deleteType(HttpServletRequest request,HttpServletResponse response) {
		JSONObject result = new JSONObject();
		try {
			//获取选中的所有行的id
			String[] ids = request.getParameter("ids").split(",");
			for (int i=0;i<ids.length;i++) {
				boolean b = roomService.existRoomWithTypeId(Integer.parseInt(ids[i]))==null; 
				if(!b){
					result.put("errorIndex", i);
					result.put("errorMsg", "有类型下面有阅览室，不能删除");
					WriterUtil.write(response, result.toString());
					return;
				}else{
				typeService.deleteType(Integer.parseInt(ids[i]));//根据id删除阅览室类型
				}
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());// 将上述Json输出，前台ajax接收
	}

	//当添加或修改阅览室信息时，加载的阅览室类型列表
	@RequestMapping("comboList")
	public void comboList(HttpServletRequest request,HttpServletResponse response) {
		try {
			List<Type> list = typeService.findType(new Type());
			JSONArray array = new JSONArray();
			array.addAll(list);
			WriterUtil.write(response, array.toString());// 将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

