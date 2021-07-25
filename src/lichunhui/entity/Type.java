package lichunhui.entity;



import lichunhui.entity.BaseEntity;

//阅览室类型实体类
public class Type extends BaseEntity{
	
	private Integer id;    //阅览室类型ID
	private String name;  //类型名称
	private Integer score;   // 最低分数
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	
}

