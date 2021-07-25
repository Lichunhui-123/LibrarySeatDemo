package lichunhui.controller;



//学生信息控制类
import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lichunhui.entity.Score;
import lichunhui.entity.Student;
import lichunhui.entity.User;
import lichunhui.service.ScoreService;
import lichunhui.service.StudentService;
import lichunhui.service.UserService;
import lichunhui.util.StringUtil;
import lichunhui.util.WriterUtil;

@Controller
@RequestMapping("student")
public class StudentController {

	private int page;
	private int rows;
	private Student student;
	//从spring容器拿业务层对象，调用业务层的方法帮我们完成工作
	@Autowired
	private StudentService<Student> studentService;//注入业务层对象
	@Autowired
	private ScoreService<Score> scoreService;
	private Score score;
	private User user;
	@Autowired
	private UserService<User> userService;
	
	//学生信息管理操作界面
	@RequestMapping("studentIndex")
	public String index(){
		return "info/student";
	}
	
	
	//加载学生信息列表
	@RequestMapping("studentList")
	public void studentList(HttpServletRequest request,HttpServletResponse response) {
		try {
			//easyui datagrid 自身会通过 post 的形式传递 rows and page 
			page = Integer.parseInt(request.getParameter("page"));
			rows = Integer.parseInt(request.getParameter("rows"));
			student = new Student();
			if(request.getParameter("clazzid")!=null && request.getParameter("clazzid").length() > 0){
				student.setClazzid(Integer.parseInt(request.getParameter("clazzid")));
			}
			student.setName(request.getParameter("name"));
			student.setNo(request.getParameter("no"));
			student.setPage((page-1)*rows);//返回到前台的值必须按照如下的格式包括 total and rows 
			student.setRows(rows);
			List<Student> list = studentService.findStudent(student);//查询所有学生信息
			int total = studentService.countStudent(student);//查询学生总数
			
			JSONObject jsonObj = new JSONObject();//new一个JSON
			jsonObj.put("total",total );//total代表一共有多少数据
			jsonObj.put("rows", list);//row是代表显示的页的数据
	        WriterUtil.write(response,jsonObj.toString()); //将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	//新增或修改学生信息
	@RequestMapping("reserveStudent")
	public void reserveStudent(HttpServletRequest request,HttpServletResponse response,Student student) {
		String id = request.getParameter("id");//学生id
		JSONObject result = new JSONObject();
		result.put("success", true);
		try {
			// id不为空 ，说明选中的是修改
			if(StringUtil.isNotEmpty(id)){
				student.setId(Integer.parseInt(id));
				studentService.updateStudent(student);
			} else { //id为空，说明是 添加
				if(studentService.findOneStudentByno(student.getNo())==null){  // 没有重复可以添加
				    studentService.addStudent(student);
				    // 添加学生的时候同时添加默认的100信用分数
				    score = new Score();
				    score.setStudentname(student.getName());
				    score.setStudentno(student.getNo());
				    score.setTotal(100);
				    scoreService.addScore(score);
				
				    // 同时自动注册user表
				    user = new User();
				    user.setRoleId(3);             //角色为3表示学生
				    user.setPassword("123456");    //密码
				    user.setUserName(student.getNo()); //用户名
				    userService.addUser(user);
				}else {
					result.put("errorMsg", "该学号被使用！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起！保存失败");
		}
		WriterUtil.write(response,result.toString());//将上述Json输出，前台ajax接收
	}
	
	
	
	//删除学生信息
	@RequestMapping("deleteStudent")
	public void delete(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");//从页面提交的ids，然后分割
			String[] nos = request.getParameter("nos").split(",");
			for (int i=0;i<ids.length;i++) {
				
				//根据学生的id删除学生信息
				studentService.deleteStudent(Integer.parseInt(ids[i]));
				//根据学号删除学生的信用积分
				scoreService.deleteOneScore(nos[i]);
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());//将上述Json输出，前台ajax接收
	}
	
	
	
//	@RequestMapping("uploadPhoto")
//	public void uploadPhoto(HttpServletRequest request,HttpServletResponse response,@RequestParam MultipartFile photo){
//		String now = System.currentTimeMillis()+"";
//		if (!photo.isEmpty()) {
//			String filePath = request.getSession().getServletContext().getRealPath("/")+ "upload/student/" + now + ".jpg";
//			try {
//				photo.transferTo(new File(filePath));
//				student.setPhoto("upload/student/" + now + ".jpg");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		WriterUtil.write(response, "upload/student/" + now + ".jpg");
//	}
	
	
}
