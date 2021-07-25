package lichunhui.controller;



//查看余坐控制类
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

import lichunhui.entity.Room;
import lichunhui.entity.Seat;
import lichunhui.service.RoomService;
import lichunhui.service.SeatService;
import lichunhui.util.WriterUtil;

@Controller
@RequestMapping("block")
public class BlockController {
	//从spring容器拿业务层对象，调用业务层的方法帮我们完成工作
	@Autowired
	private RoomService<Room> roomService;//注入业务层对象
	private Seat seat;
	@Autowired
	private SeatService<Seat> seatService;
	
	//加载余座信息列表
	@RequestMapping("blockList")
	public void list(HttpServletResponse response,HttpServletRequest request) {
		try {
			String[] times = new String[]{"08点-12点","14点-18点","18点-22点"};//时间段
			Date today = new Date();  //今天日期对象
			Date tomorrow = getNextDay(today, 1);  //明天日期对象
			Date[] dates = new Date[]{today,tomorrow};
			
			//调用业务层方法查询所有阅览室信息
			List<Room> rooms = roomService.findRoom(new Room());
			List<Seat> list = new ArrayList<Seat>();
			for(int j=0;j<dates.length;j++){//每天
				Date date = dates[j];
			for(int i=0;i<times.length;i++){//每个时间段
				String time = times[i];
					for(int k=0;k<rooms.size();k++){//每个阅览室
						int roomid = rooms.get(k).getId();//阅览室信息id
						//构造查询条件
						seat = new Seat();
						seat.setRoomid(roomid); //设置id
						seat.setDate(new SimpleDateFormat("yyyy-MM-dd").format(date)); //设置日期
						seat.setTime(time);  //时间段
						seat.setRoomname(rooms.get(k).getName()); //阅览室名称
						//调用业务层方法查询余座数量
						int yuzuo = seatService.findBlock(seat);
						seat.setPage(yuzuo);        //将page字段临时存放余坐
						list.add(seat);
					}
				}
			}
			JSONArray array = new JSONArray();
			array.addAll(list);
			WriterUtil.write(response, array.toString());//将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//剩余座位主界面
	@RequestMapping("blockIndex")
	public String block(){
		return "seat/block";
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
