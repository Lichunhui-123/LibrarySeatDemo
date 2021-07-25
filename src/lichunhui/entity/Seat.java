package lichunhui.entity;



import lichunhui.entity.BaseEntity;

//座位信息实体类
public class Seat extends BaseEntity{

	private Integer id;     //座位ID
	private Integer roomid;     //阅览室ID
	private String  roomname;   //所属阅览室的名称
	private String studentno;  //所属的学生学号
	private String studentname; //学生姓名
	private Integer col;   //列位置
	private Integer row;   // 行位置
	private String time;   //时间段
	private String date;   //日期
	private String keyword;     //由roomid,date,time,row,col组成
	
	

	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getRoomid() {
		return roomid;
	}
	public void setRoomid(Integer roomid) {
		this.roomid = roomid;
	}
	public String getRoomname() {
		return roomname;
	}
	public void setRoomname(String roomname) {
		this.roomname = roomname;
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
	public Integer getCol() {
		return col;
	}
	public void setCol(Integer col) {
		this.col = col;
	}
	public Integer getRow() {
		return row;
	}
	public void setRow(Integer row) {
		this.row = row;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
