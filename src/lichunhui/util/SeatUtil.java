package lichunhui.util;



//两个定时器控制类
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lichunhui.entity.Choice;
import lichunhui.entity.Illegal;
import lichunhui.entity.Room;
import lichunhui.entity.Score;
import lichunhui.entity.Seat;
import lichunhui.entity.Student;
import lichunhui.service.ChoiceService;
import lichunhui.service.IllegalService;
import lichunhui.service.RoomService;
import lichunhui.service.ScoreService;
import lichunhui.service.SeatService;
import lichunhui.service.StudentService;
/*@Component
* 把普通pojo实例化到spring容器中，相当于配置文件中的<bean id="" class=""/>
* */
@Component 
@Controller
public class SeatUtil {
	@Autowired
	private SeatService<Seat> seatService;
	@Autowired
	private RoomService<Room> roomService;
	@Autowired
	private ChoiceService<Choice> choiceService;
	@Autowired
	private StudentService<Student> studentService;
	@Autowired
	private ScoreService<Score> scoreService;
	@Autowired
	private IllegalService<Illegal> illegalService;
/*	@Scheduled:定时任务注解
* cron按顺序依次为
	秒（0~59）
	分钟（0~59）
	小时（0~23）
	天（月）（0~31，但是你需要考虑你月的天数）
	月（0~11）
	天（星期）（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT）
	7.年份（1970－2099）*/
	
	//每天晚上11点生成后天的所有座位信息
	@Scheduled(cron = "0 0 23 * * ?")  //每天晚上23点运行一次  生成所有新的座位
	public void generateNextDay(){
		addNewSeat(1);
	}
	
	//生成座位
	public void addNewSeat(int offset){
		try {
			String times[] = {"08点-12点","14点-18点","18点-22点"};   //三个时间段
			Date today = new Date();  //今天日期Date类型
			Date dayAfterTomorrow = getNextNextDay(today,offset); //后面的参数表示与今天的间隔，如0表示今天，1表示明天，2表示后天
			String date = new SimpleDateFormat("yyyy-MM-dd").format(dayAfterTomorrow);  //后天日期yyy-MM-dd string类型
			//查询所有的阅览室信息
			List<Room> roomList = roomService.findRoom(new Room());
			for(int i=0;i<times.length;i++){               // 三个时间段
				String time = times[i];
				for(int j=0;j<roomList.size();j++){         //所有的房间
					Room room = roomList.get(j);
					int roomid = room.getId(); //获取阅览室的id
					int row = room.getRow();   //获取阅览室的行数  
					int col = room.getCol();   //获取阅览室的列数
					for(int k=1;k<=row;k++){                 //行
						for(int l=1;l<=col;l++){             //列
							//设置座位信息
							Seat seat = new Seat();
							seat.setCol(l); //设置座位所在的列
							seat.setDate(date); //设置所属的日期
							seat.setRoomid(roomid); //设置所属的阅览室
							seat.setRow(k);  //设置所在的行
							seat.setTime(time); //设置所属的时间段
							seat.setKeyword(date + "-" + time + "-" + roomid + "-" + k + "-" + l);
							
							//调用service层方法，添加座位
							seatService.insertSeat(seat);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// 在浏览器输入http://127.0.0.1:8080/LibrarySeat/newSeat.htm即可手动生成座位数
	@RequestMapping("newSeat")
	@ResponseBody  //返回 json 数据
	public String today(){
		Thread thread = new Thread(new Task());//使用匿名内部类的方法来创建线程，并将对象作为参数传进Thread中，然后调用start方法
		thread.start();//线程启动
		return "开始创建当日座位信息，请不要重复运行，观察控制台运行停止后可以登录后台查看座位信息！";
	}
	
	// 获取后天日期
	public static Date getNextNextDay(Date date,int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, day);
		date = calendar.getTime();
		return date;
	}
	
	// 每天的08，14，18即三个时间段的开始时间 运行一次，将之前的判断学生是否迟到，迟到就扣分
	@Scheduled(cron = "0 0 8,14,18 * * ?") 
	public void updateChoice(){
		try {
			String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//当前时间段
			String hour=now.substring(11,13);//小时
			Choice choice = new Choice();
			choice.setStatus("2");
			choice.setSeatkeyword(hour);
			List<Choice> list =choiceService.findChoiceByStatus(choice);
			for (Choice choice2 : list) {
			String studentno=choice2.getStudentno();
			Score score = scoreService.findOneScore(studentno);//根据学号查找学生的信誉积分记录
			//构造查询条件
			Illegal illegal=new Illegal();
			illegal.setStudentno(studentno);//学号
			illegal.setScore(5);//迟到一次扣5分
			illegal.setTime(now);//占座的时间
			illegal.setRemarks("迟到");
			int total = score.getTotal();                  //获得原始积分
			int thisScore = illegal.getScore();             //本次扣除积分
			if(thisScore > total){//判断信用积分是否可以扣除
				return;
			} else {
				illegalService.addIllegal(illegal);  //加入违章记录
				// 更新分数
				score.setTotal(total - thisScore);
				scoreService.updateScore(score);
			}
			
			//设置status为3表示迟到
			choice2.setStatus("3");
			choice2.setStudentno(studentno);
			choice2.setSeatkeyword(hour);//keyword.substring(0, 20)
			choiceService.modifyOneChoiceByStatus(choice2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	//每天的12点01秒，16点01秒，22点01秒三个时间段运行程序，把对应时间段的座位清空并清空选座记录,占座扣分
	//浏览器输入http://127.0.0.1:8080/LibrarySeat/deleteSeat.htm删除座位信息
	@Scheduled(cron = "1 0 12,18,22 * * ?") 
	public void banSeat(){
		try {
			String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//当前时间段
			String hour=now.substring(11,13);//小时
			//构造查询条件
			Seat seat=new Seat();
			seat.setTime(hour);
			Choice choice = new Choice();
			choice.setSeatkeyword(hour);
			
			//查询所有占座的记录
			List<Choice> list=choiceService.findChoiceByTime(choice);
			
			for (Choice choice2 : list) {
			String studentno=choice2.getStudentno();
			Score score = scoreService.findOneScore(studentno);//根据学号查找学生的信誉积分记录
			//构造查询条件
			Illegal illegal=new Illegal();
			illegal.setStudentno(studentno);//学号
			illegal.setScore(8);//占座一次扣8分
			illegal.setTime(now);//占座的时间
			illegal.setRemarks("占座");
			int total = score.getTotal();                  //获得原始积分
			int thisScore = illegal.getScore();             //本次扣除积分
			if(thisScore > total){//判断信用积分是否可以扣除
				return;
			} else {
				illegalService.addIllegal(illegal);  //加入违章记录
				// 更新分数
				score.setTotal(total - thisScore);
				scoreService.updateScore(score);
			}
			}
			//删除过去预约的座位记录
			choiceService.deleteChoiceByTime(choice);
			//删除过去时间段的座位
			seatService.deleteSeat(seat);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	//实现Runnable接口，重写run方法
	public class Task implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			addNewSeat(0);
			//banSeat();
			//updateChoice();
		}
		
	}

	
}

