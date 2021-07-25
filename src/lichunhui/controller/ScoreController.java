package lichunhui.controller;


//信用积分控制器

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lichunhui.entity.Illegal;
import lichunhui.entity.Room;
import lichunhui.entity.Score;
import lichunhui.entity.User;
import lichunhui.service.IllegalService;
import lichunhui.service.ScoreService;
import lichunhui.util.StringUtil;
import lichunhui.util.WriterUtil;

@RequestMapping("score")
@Controller
public class ScoreController {

	private int page;
	private int rows;
	private Score score;
	//从spring容器拿业务层对象，调用业务层的方法帮我们完成工作
	@Autowired
	private ScoreService<Score> scoreService;//注入业务层对象
	@Autowired
	
	//信用积分管理界面
	@RequestMapping("scoreIndex")
	public String index(){
		return "jifen/score";
	}
	
	//加载信用积分列表
	@RequestMapping("scoreList")
	public void scoreList(HttpServletRequest request,HttpServletResponse response) {
		try {
			//easyui datagrid 自身会通过 post 的形式传递 rows and page
			page = Integer.parseInt(request.getParameter("page"));
			rows = Integer.parseInt(request.getParameter("rows"));
			//构造查询条件
			score = new Score();
			score.setPage((page-1)*rows);
			score.setRows(rows);
			score.setStudentname(request.getParameter("studentname"));
			score.setStudentno(request.getParameter("studentno"));
			User currentUser = (User)request.getSession().getAttribute("currentUser");//当前用户
			if(currentUser.getRoleId()==3){        // 角色id为3说明是学生
				score.setStudentno(currentUser.getUserName());
			}
			
			// 调用service层的查询方法查出记录和数量
			List<Score> list = scoreService.findScore(score);//查询所有学生信用积分信息
			int total = scoreService.countScore(score);//查询所有学生信用积分总数
			JSONObject jsonObj = new JSONObject();//new一个JSON
			jsonObj.put("total",total );//total代表一共有多少数据
			jsonObj.put("rows", list);//row是代表显示的页的数据
	        WriterUtil.write(response,jsonObj.toString()); //将上述Json输出，前台ajax接收
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//新增或修改学生信用积分
	@RequestMapping("reserveScore")
	public void reserveroom(HttpServletRequest request,HttpServletResponse response,Score score) {
		String id = request.getParameter("id");//获取信用积分id
		JSONObject result = new JSONObject();
		result.put("success", true);
		try {//id不为空 ，说明选中了是修改
			if(StringUtil.isNotEmpty(id)){
				score.setId(Integer.parseInt(id));
				scoreService.updateScore(score);
			} else {//id为空，说明是 添加
				scoreService.addScore(score);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起！保存失败");
		}
		WriterUtil.write(response,result.toString());//将上述Json输出，前台ajax接收
	}
	
	
	
	//删除学生信用积分
	@RequestMapping("deleteScore")
	public void delete(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");//分割，获取所选的信用积分id组
			for (String id : ids) {
				scoreService.deleteScore(Integer.parseInt(id));//根据id删除学生信用积分
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

