package lichunhui.entity;



import lichunhui.entity.BaseEntity;

//选座管理
public class Choice extends BaseEntity{

	private Integer id;
	private String studentno;  //学号
	private String seatkeyword;  //关键字 由日期-时间-阅览室ID-行号-列号组成
	private String time;         //预约时间
	private String status;     //0表示处于预约状态。1表示时间过了。这件事情完成了
	
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getStudentno() {
		return studentno;
	}
	public void setStudentno(String studentno) {
		this.studentno = studentno;
	}
	public String getSeatkeyword() {
		return seatkeyword;
	}
	public void setSeatkeyword(String seatkeyword) {
		this.seatkeyword = seatkeyword;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}

