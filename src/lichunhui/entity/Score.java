package lichunhui.entity;



import lichunhui.entity.BaseEntity;

//信用积分实体类
public class Score extends BaseEntity{

	private Integer id;          // 信用积分主键
	private String studentno;   //学生学号
	private String studentname;//学生的姓名
	private Integer total;      //总分。 在添加学生的时候这个表也对应添加数据，total默认满分100
	
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
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	
}

