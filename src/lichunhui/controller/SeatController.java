package lichunhui.controller;



//座位管理控制类
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sun.org.apache.bcel.internal.generic.NEW;

import lichunhui.entity.Choice;
import lichunhui.entity.ComboValue;
import lichunhui.entity.Room;
import lichunhui.entity.Score;
import lichunhui.entity.Seat;
import lichunhui.entity.User;
import lichunhui.service.ChoiceService;
import lichunhui.service.RoomService;
import lichunhui.service.ScoreService;
import lichunhui.service.SeatService;
import lichunhui.util.WriterUtil;
@Controller
@RequestMapping("seat")
public class SeatController {

	private int page;
	private int rows;
	//从spring容器拿业务层对象，调用业务层的方法帮我们完成工作
	@Autowired
	private SeatService<Seat> seatService;//注入业务层对象
	private Seat seat;
	@Autowired
	private RoomService<Room> roomService;
	@Autowired
	private ChoiceService<Choice> choiceService;
	private Choice choice;
	@Autowired
	private ScoreService<Score> scoreService;
	private Score score;
	
	//座位管理主界面
	@RequestMapping("seatIndex")
	public String index(){
		return "seat/selectSeat";
	}
	
	//加载座位列表
	@RequestMapping("combolist")
	public void seatList(HttpServletRequest request,HttpServletResponse response) {
		try {
			//构造查询条件
			seat = new Seat();
			String date = request.getParameter("date");//获取前端发送的date，即学生选中的日期
			if(date==null || date.length()==0){//学生没有选日期，默认date设置为今天
				seat.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			}else {
				seat.setDate(date);
			}
			//判断学生是否选阅览室
			if(request.getParameter("roomid")!=null && request.getParameter("roomid").length() > 0){
				//选中阅览室，则设置阅览室id
				seat.setRoomid(Integer.parseInt(request.getParameter("roomid")));
			} else {
				//没有选中，则默认设置第一个阅览室
				seat.setRoomid(1);
			}
			String time = request.getParameter("time");//获取学生选中的时间段
			if(time == null || time.length()==0){
				seat.setTime("08点-12点");//学生没有选时间段，默认设置为8点-12点
			}else {
				seat.setTime(time);//有选，则设置选中的时间段
			}
			
			//调用业务层方法，查询所有座位信息
			List<Seat> list = seatService.findSeat(seat);
			JSONArray array = new JSONArray();
			array.addAll(list);//将查询的座位列表，添加到json集合
			WriterUtil.write(response, array.toString());//将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 加载组合框里今天和明天的日期
	@RequestMapping("dateCombo")
	public void dateCombo(HttpServletRequest request,HttpServletResponse response){
		try {
			// 获取今明两天时间的String值。格式是yyyy-MM-dd
			Date todayDate = new Date();
			Date tomorrowDate = getNextDay(todayDate);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String today = sdf.format(todayDate); //格式化日期
			String tomorrow = sdf.format(tomorrowDate);
			
			//设置组合框的value值为今明两天的日期
			List<ComboValue> list = new ArrayList<ComboValue>();
			ComboValue cv = new ComboValue(today, "今天  "+today);
			list.add(cv);
			ComboValue cv2 = new ComboValue(tomorrow, "明天  "+tomorrow);
			list.add(cv2);
			
			JSONArray array = new JSONArray();
			array.addAll(list);//添加到json集合中
			WriterUtil.write(response, array.toString());//将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 获取明天日期
	public static Date getNextDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		date = calendar.getTime();
		return date;
	}
	
	
	
	
	// 加载3个时间段组合框列表
	@RequestMapping("timeCombo")
	public void timeCombo(HttpServletRequest request,HttpServletResponse response) {
		try {
			//通过构造注入设置组合框的value值
			List<ComboValue> list = new ArrayList<ComboValue>();
			ComboValue cv =  new ComboValue("08点-12点","08点-12点");
			list.add(cv);
			ComboValue cv2 = new ComboValue("14点-18点","14点-18点");
			list.add(cv2);
			ComboValue cv3 = new ComboValue("18点-22点","18点-22点");
			list.add(cv3);
			JSONArray array = new JSONArray();
			array.addAll(list);
			WriterUtil.write(response, array.toString());//将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// 加载阅览室组合框列表
	@RequestMapping("roomCombo")
	public void roomCombo(HttpServletRequest request,HttpServletResponse response){
		try {
			//调用业务层方法，查询阅览室信息
			List<Room> list = roomService.findRoom(new Room());
			JSONArray array = new JSONArray();
			array.addAll(list);
			WriterUtil.write(response, array.toString());//将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	//查找自己的座位
	@RequestMapping("myselfSeat")
	public void myselfSeat(HttpServletRequest request,HttpServletResponse response){
		User currentUser = (User)request.getSession().getAttribute("currentUser");//当前用户
		try {
			String date = request.getParameter("date");//获取学生选中的日期
			if(date==null || date.length()==0){//学生没有选日期，默认date设置为今天
				date = (new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			}
			String roomid = request.getParameter("roomid");//获取学生选中的阅览室id
			//判断学生是否选阅览室
			if(roomid==null || roomid.length()==0){
				roomid = "1";//没有选中，则默认设置第一个阅览室
			} 
			String time = request.getParameter("time");//获取学生选中的时间段
			if(time == null || time.length()==0){
				time = "08点-12点";//学生没有选时间段，默认设置为8点-12点
			}
			//构造查询条件
			Choice c = new Choice();
			c.setSeatkeyword(date + "-" +time + "-" +roomid);//设置seatKeyword
			c.setStudentno(currentUser.getUserName());//设置studentno
			//调用业务层方法，查询学生选中的座位信息
			choice = choiceService.findOneChoice(c);
			if(choice == null){//没有选座
				WriterUtil.write(response, "no");
			} else {
				//有选，打印座位信息
				WriterUtil.write(response, choice.getSeatkeyword());//将上述Json输出，前台ajax接收
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	// 保存选中座位
	@RequestMapping("saveSeat")
	public void saveSeat(HttpServletRequest request,HttpServletResponse response) {
		String keyword = request.getParameter("keyword");//获取自己选中座位的keyword
		System.out.println("key---"+keyword);//打印在控制台
		User currentUser = (User)request.getSession().getAttribute("currentUser");//当前用户
		if(currentUser.getRoleId() == 1  || currentUser.getRoleId()==2){  //超管和教师不能选座
			WriterUtil.write(response, "对不起，该阅览室选座只对学生开放");
			return;
		}
		String studentno = currentUser.getUserName();//获取学生学号
		String nowDateHour = new SimpleDateFormat("yyyy-MM-dd-HH").format(new Date());  //当前小时数
		String selectedDate = keyword.substring(0,13);  //从下标0开始，截至到第13位，不包括第13位的字符串截取出来,yyyy-mm-dd-xx
		//System.out.println("selectedDate---"+selectedDate);
		try {
			//判断信用积分
			score = scoreService.findOneScore(studentno);//根据学号查询该学生的信用积分对象
			int myScore = score.getTotal();     //该学生分数
			
			int roomid = Integer.parseInt(keyword.substring(19,20));//截取出roomid
			int needScore = roomService.findScoreByRoomid(roomid);//查询该阅览室的限制积分，最低分
			//信用积分小于等于阅览室的积分，预约失败
			if(needScore >= myScore){
				WriterUtil.write(response, "预约失败！您的信用积分不允许在该阅览室选座");return;
			}
			String flag = "1";
			//构造查询条件
			Choice c = new Choice();
			c.setStudentno(studentno);
			c.setStatus("1");//1：表示可以预约，0表示已经选择，2表示签到，3表示迟到
			//调用service层方法查询所有的已经选中座位信息
			List<Choice> list = choiceService.findChoice(c);
			if(list==null || list.size()==0){
				// 无预约 OK的
			} else if(list.size()>=3){
				// 限预约3次
				flag = "3";
			}else {
				for(Choice choice : list){
					//判断同一天同一时间段是否重选
					if(choice.getSeatkeyword().substring(0,17).equals(keyword.substring(0,17))){//假如已经选中的座位的日期和时间段，和现在选的日期和时间段一致
						//重复了
						flag = "2";
						break;
					}
				}
				
			}
			//限制一天只能预约三次
			if("3".equals(flag)){
				WriterUtil.write(response, "预约失败！24小时之类已经预约3次了");return;
			}//限制每天每个时间段只能预约一个阅览室 
			else if ("2".equals(flag)) {
				WriterUtil.write(response, "预约失败！这个时间段已经预约过其他阅览室了");return;
			} else {
				//构建查询条件
				choice = new Choice();
				choice.setSeatkeyword(keyword);//设置选中的座位keyword
				choice.setStudentno(studentno);//学号
				choice.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//时间段
				choiceService.addChoice(choice);//添加选中座位信息
				
				seat = new Seat();
				seat.setKeyword(keyword);
				seat.setStudentno(studentno);
				seatService.occupySeat(seat);//预约座位
				
				WriterUtil.write(response, "ok");//保存成功
			}
		} catch (Exception e) {
			e.printStackTrace();
			WriterUtil.write(response, "对不起！系统错误，选座失败！");//将上述Json输出，前台ajax接收
		}
	}
	
	
	
	
	
	//取消选座
	@RequestMapping("cancelSeat")
	public void cancelSeat(HttpServletRequest request,HttpServletResponse response){
		User currentUser = (User)request.getSession().getAttribute("currentUser");//当前用户
		try {
			// 删除choice表中的记录
			String keyword = request.getParameter("seatkeyword");
			Choice choice = new Choice();
			choice.setSeatkeyword(keyword.substring(0, 20));//keyword.substring(0, 20)
			choice.setStudentno(currentUser.getUserName());
			choiceService.cancelChoice(choice);
			
			// 将seat表中该条记录学号变成1
			seatService.cancelSeat(keyword); 
			
			WriterUtil.write(response, "ok");
		} catch (Exception e) {
			e.printStackTrace();
			WriterUtil.write(response, "对不起！取消失败");//将上述Json输出，前台ajax接收
		}
	}
	
	
	
}

