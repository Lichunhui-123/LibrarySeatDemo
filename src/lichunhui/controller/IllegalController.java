package lichunhui.controller;



import java.text.SimpleDateFormat;
import java.util.Date;
//学生违规控制器
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lichunhui.entity.Choice;
import lichunhui.entity.Illegal;
import lichunhui.entity.Score;
import lichunhui.entity.Student;
import lichunhui.service.ChoiceService;
import lichunhui.service.IllegalService;
import lichunhui.service.ScoreService;
import lichunhui.util.WriterUtil;

@Controller
@RequestMapping("illegal")
public class IllegalController {

	private int page;
	private int rows;
	private Illegal illegal;
	//从spring容器拿业务层对象，调用业务层的方法帮我们完成工作
	@Autowired
	private IllegalService<Illegal> illegalService;//注入业务层对象
	@Autowired
	private ScoreService<Score> scoreService;
	private Score score;
	@Autowired
	private ChoiceService<Choice> choiceService;//注入业务层对象
	
	//加载积分违章详情列表
	@RequestMapping("illegalList")
	public void illegalList(HttpServletRequest request,HttpServletResponse response){
		try {
			//easyui datagrid 自身会通过 post 的形式传递 rows and page
			page = Integer.parseInt(request.getParameter("page"));
			rows = Integer.parseInt(request.getParameter("rows"));
			//构造查询条件
			illegal = new Illegal();
			illegal.setPage((page-1)*rows);
			illegal.setRows(rows);
			illegal.setStudentno(request.getParameter("studentno"));
			
			// 调用service层的查询方法查出记录和数量
			List<Illegal> list = illegalService.findIllegal(illegal);//查询该学生所有积分违章详情
			int total = illegalService.countIllegal(illegal);//查询该学生积分违章详情总数
			JSONObject jsonObj = new JSONObject();//new一个JSON
			jsonObj.put("total",total );//total代表一共有多少数据
			jsonObj.put("rows", list);//row是代表显示的页的数据
	        WriterUtil.write(response,jsonObj.toString()); //将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//新增积分违章详情
	@RequestMapping("reserveIllegal")
	public void reserveIllegal(HttpServletRequest request,HttpServletResponse response,Illegal illegal) {
		JSONObject result = new JSONObject();//new 一个json对象
		result.put("success", true);
		try {
			String studentno = illegal.getStudentno();     //获得学号
			score = scoreService.findOneScore(studentno);  //调用业务层方法，根据学号查询该学生的信用积分，获取socre对象
			int total = score.getTotal();                  //获得原始积分
			int thisScore = illegal.getScore();             //本次扣除积分
			if(thisScore > total){//判断信用积分是否可以扣除
				result.put("errorMsg", "对不起！扣除失败，要扣除的分数大于剩余积分！");
			} else {
				illegalService.addIllegal(illegal);  //加入违章记录
				// 更新分数
				score.setTotal(total - thisScore);
				scoreService.updateScore(score);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	//学生按时到达，添加加分记录
	@RequestMapping("addIllegal")
	public void addIllegal(HttpServletRequest request,HttpServletResponse response) {
		String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//当前时间段
		String studentno=request.getParameter("studentno");
		String keyword = request.getParameter("seatkeyword");
		String status = request.getParameter("status");
		JSONObject result = new JSONObject();//new 一个json对象
		
		try {
			if (status.equals("2")) {
				result.put("success", "您已经签到！");
			}
			else if(status.equals("3")){
				result.put("success", "您已经迟到！");
			}
			else {
				
				score = scoreService.findOneScore(studentno);  //调用业务层方法，根据学号查询该学生的信用积分，获取socre对象
				int total = score.getTotal();                  //获得原始积分
				
				Choice c = new Choice();
				c.setStatus("2");
				c.setStudentno(studentno);
				c.setSeatkeyword(keyword.substring(0, 20));//keyword.substring(0, 20)
				choiceService.modifyOneChoice(c);
				//构造查询条件
				Illegal illegal=new Illegal();
				illegal.setStudentno(studentno);//学号
				illegal.setScore(-5);//按时到达加5分
				illegal.setTime(now);//占座的时间
				illegal.setRemarks("按时到达");            
				int thisScore = illegal.getScore();             //本次扣除积分
				if(thisScore > total){//判断信用积分是否可以扣除
					return;
				} else {
					illegalService.addIllegal(illegal);  //加入违章记录
					// 更新分数
					score.setTotal(total - thisScore);
					scoreService.updateScore(score);
				}
				result.put("success", "签到成功！");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，签到失败！");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	//删除积分违章详情
	@RequestMapping("deleteIllegal")
	public void delete(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");//分割，获取所选的积分违章详情id组
			for (String id : ids) {
				illegalService.deleteIllegal(Integer.parseInt(id));//根据id删除积分违章详情
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

