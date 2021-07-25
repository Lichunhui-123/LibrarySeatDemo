package lichunhui.controller;



import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lichunhui.entity.Room;
import lichunhui.entity.Seat;
import lichunhui.service.RoomService;
import lichunhui.service.SeatService;
import lichunhui.util.StringUtil;
import lichunhui.util.WriterUtil;
// 阅览室基本信息控制类

@RequestMapping("room")
@Controller
public class RoomController {

	private int page;
	private int rows;
	private Room room;
	//从spring容器拿业务层对象，调用业务层的方法帮我们完成工作
	@Autowired
	private RoomService<Room> roomService;//注入业务层对象
	@Autowired
	private SeatService<Seat> seatService;//注入业务层对象
	
	//阅览室信息管理界面
	@RequestMapping("roomIndex")
	public String index(){
		return "readingroom/room";
	}
	
	
	//加载阅览室信息列表
	@RequestMapping("roomList")
	public void roomList(HttpServletRequest request,HttpServletResponse response) {
		try {
			//easyui datagrid 自身会通过 post 的形式传递 rows and page
			page = Integer.parseInt(request.getParameter("page"));
			rows = Integer.parseInt(request.getParameter("rows"));
			//构建查询条件
			room = new Room();
			if(request.getParameter("typeid")!=null && request.getParameter("typeid").length() > 0){
				room.setTypeid(Integer.parseInt(request.getParameter("typeid")));
			}
			room.setPage((page-1)*rows);
			room.setRows(rows);
			//调用业务层的方法查询阅览室信息和总数
			List<Room> list = roomService.findRoom(room);
			int total = roomService.countRoom(room);
			
			JSONObject jsonObj = new JSONObject();//new一个JSON
			jsonObj.put("total",total );//total代表一共有多少数据
			jsonObj.put("rows", list);//row是代表显示的页的数据
	        WriterUtil.write(response,jsonObj.toString()); //将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	//新增或修改阅览室信息
	@RequestMapping("reserveRoom")
	public void reserveroom(HttpServletRequest request,HttpServletResponse response,Room room) {
		String id = request.getParameter("id");//阅览室id
		room.setTotal(room.getCol() * room.getRow());  //总座位数=列数     X 行数
		JSONObject result = new JSONObject();//new一个json对象
		result.put("success", true);
		try {//id不为空，则选择的是修改
			if(StringUtil.isNotEmpty(id)){
				room.setId(Integer.parseInt(id));
				roomService.updateRoom(room);//修改阅览室信息
			} else {//id为空，选择的是添加
				if(roomService.existRoomWithName(room.getName())==null){  // 没有重复可以添加
				roomService.addRoom(room);
				}
				else {
					result.put("errorMsg", "该阅览室名称被使用！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起！保存失败");
		}
		WriterUtil.write(response,result.toString());//将上述Json输出，前台ajax接收
	}
	
	
	
	//删除阅览室信息
	@RequestMapping("deleteRoom")
	public void delete(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");//分割出所有选中的阅览室信息id
			for (int i=0;i<ids.length;i++) {
				boolean b = seatService.existSeatWithRoomId(Integer.parseInt(ids[i]))==null; 
				if(!b){
					result.put("errorIndex", i);
					result.put("errorMsg", "有阅览室下面有座位，不能删除");
					WriterUtil.write(response, result.toString());
					return;
				}else{
				roomService.deleteRoom(Integer.parseInt(ids[i]));//根据id删除阅览室信息
				}
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	
}
