package lichunhui.controller;


//查看余坐控制类
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lichunhui.entity.Choice;
import lichunhui.entity.Room;
import lichunhui.entity.Seat;
import lichunhui.entity.Student;
import lichunhui.entity.User;
import lichunhui.service.ChoiceService;
import lichunhui.service.RoomService;
import lichunhui.service.SeatService;
import lichunhui.service.StudentService;
import lichunhui.util.WriterUtil;

@Controller
@RequestMapping("choice")
public class ChoiceController {
	
	private int page;
	private int rows;
	//从spring容器拿业务层对象，调用业务层的方法帮我们完成工作
	@Autowired
	private RoomService<Room> roomService;//注入业务层对象
	private Seat seat;
	@Autowired
	private SeatService<Seat> seatService;
	private Student student;
	@Autowired
	private StudentService<Student> studentService;
	private Choice choice;
	@Autowired
	private ChoiceService<Choice> choiceService;
	
	
	//RequestMapping是一个用来处理请求地址映射的注解，可用于类或方法上
	@RequestMapping("choiceIndex")
	public String index(){
		return "seat/choice";
	}
	
	//加载余座信息列表
	@RequestMapping("choiceList")
	public void list(HttpServletResponse response,HttpServletRequest request) {
		try {
			//easyui datagrid 自身会通过 post 的形式传递 rows and page
			page = Integer.parseInt(request.getParameter("page"));
			rows = Integer.parseInt(request.getParameter("rows"));
			
			User currentUser = (User)request.getSession().getAttribute("currentUser");//当前用户
			if(currentUser.getRoleId() == 1  || currentUser.getRoleId()==2){  //超管和教师不能选座
				WriterUtil.write(response, "对不起，该功能只对学生开放");
				return;
			}
			String studentno = currentUser.getUserName();//获取学生学号
			//构造查询条件
			Choice c = new Choice();
			c.setStudentno(studentno);
			c.setPage((page-1)*rows);
			c.setRows(rows);
			

			//调用service层方法查询所有的选中座位信息
			List<Choice> list = choiceService.findChoice(c);
			int total = choiceService.countChoice(c);
			JSONObject jsonObj = new JSONObject();//new一个JSON
			//返回到前台的值必须按照如下的格式包括 total and rows 
			jsonObj.put("total",total );//total代表一共有多少数据
			jsonObj.put("rows", list);//row是代表显示的页的数据
			WriterUtil.write(response, jsonObj.toString());//将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	//获取明天的时间
	public static Date getNextDay(Date date,int day) {
		Calendar calendar = Calendar.getInstance();//定义Calendar 类的实例对象
		calendar.setTime(date);//设置时间
		calendar.add(Calendar.DAY_OF_MONTH, day);//明天的时间
		date = calendar.getTime();//获取时间
		return date;
	}
	
	
}