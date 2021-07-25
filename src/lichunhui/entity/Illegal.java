package lichunhui.entity;



import lichunhui.entity.BaseEntity;

//违章信息实体类
public class Illegal extends BaseEntity{
	private Integer id;       //主键
	private String studentno;  //学号  
	private String studentname;  //姓名
	private String time;         // 违章时间
	private Integer score;       //扣分
	private String remarks;     //违章描述信息
	
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
	public String getStudentname() {
		return studentname;
	}
	public void setStudentname(String studentname) {
		this.studentname = studentname;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
