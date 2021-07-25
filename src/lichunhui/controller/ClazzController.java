
package lichunhui.controller;



//班级管理控制器
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lichunhui.entity.Clazz;
import lichunhui.entity.Student;
import lichunhui.service.ClazzService;
import lichunhui.service.StudentService;
import lichunhui.util.StringUtil;
import lichunhui.util.WriterUtil;

@Controller
@RequestMapping("clazz")
public class ClazzController {

	private int page;
	private int rows;
	@Autowired
	private ClazzService<Clazz> clazzService;
	private Clazz clazz;
	@Autowired
	private StudentService<Student> studentService;
	
	@RequestMapping("clazzIndex")
	public String index(){
		return "info/clazz";
	}
	
	
	//加载班级信息列表
	@RequestMapping("clazzList")
	public void list(HttpServletRequest request ,HttpServletResponse response) {
		try {
			//easyui datagrid 自身会通过 post 的形式传递 rows and page 
			page = Integer.parseInt(request.getParameter("page"));
			rows = Integer.parseInt(request.getParameter("rows"));
			// 构造查询条件
			clazz = new Clazz();
			clazz.setPage((page-1)*rows);
			clazz.setRows(rows);
			// 调用service层的查询方法查出记录和数量
			List<Clazz> list = clazzService.findClazz(clazz);//查询班级信息
			int total = clazzService.countClazz(clazz);//查询班级信息的总数
			
			JSONObject jsonObj = new JSONObject();//new一个JSON
			jsonObj.put("total",total );//total代表一共有多少数据
			jsonObj.put("rows", list);//row是代表显示的页的数据
	        WriterUtil.write(response,jsonObj.toString()); //将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//新增或修改班级信息
	@RequestMapping("reserveClazz")
	public void reserveClazz(HttpServletRequest request,HttpServletResponse response,Clazz clazz) {
		JSONObject result = new JSONObject();
		result.put("success", true);
		String id = request.getParameter("id");//班级id
		try {//id不为空，选择的是修改
			if(StringUtil.isNotEmpty(id)){
				clazz.setId(Integer.parseInt(id));
				clazzService.updateClazz(clazz);//修改班级信息
			} else {//id为空，选择的是添加
				if(clazzService.existClazzWithName(clazz.getName())==null){  // 没有重复可以添加
				clazzService.addClazz(clazz);//添加班级信息
				}else{
					result.put("errorMsg", "该班级名被使用！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起！保存失败");
		}
		WriterUtil.write(response, result.toString());//将上述Json输出，前台ajax接收
	}
	
	
	
	//删除班级信息
	@RequestMapping("deleteClazz")
	public void deleteClazz(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			//从页面提交的ids，然后分割，ids是班级id组
			String[] ids=request.getParameter("ids").split(",");
			for (int i=0;i<ids.length;i++) {
				boolean b = studentService.existStudentWithClazzId(Integer.parseInt(ids[i]))==null; 
				if(!b){
					result.put("errorIndex", i);
					result.put("errorMsg", "有班级下面有学生，不能删除");
					WriterUtil.write(response, result.toString());
					return;
				}else{
					clazzService.deleteClazz(Integer.parseInt(ids[i]));//根据id删除班级信息
				}
				
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());//将上述Json输出，前台ajax接收
	}
	
 //添加学生信息的班级选择组合框里的班级信息列表
	@RequestMapping("comboList")
	public void comboList(HttpServletRequest request,HttpServletResponse response){
		try {
			//获取班级信息列表
			List<Clazz> list = clazzService.findClazz(new Clazz());
			JSONArray array = new JSONArray();//创建一个空的json数组
			array.addAll(list);//将list添加到Json数组中 
			WriterUtil.write(response, array.toString());//将上述Json输出，前台ajax接收
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

