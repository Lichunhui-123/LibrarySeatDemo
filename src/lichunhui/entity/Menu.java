package lichunhui.entity;



import java.io.Serializable;

/**
菜单管理
 */
public class Menu implements Serializable{
	/**
	 * 定义程序序列化ID
	 * 作用：相当于java类的身份证。主要用于版本控制。
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer menuId;  //菜单id
	private String menuName;  //菜单名称
	private String menuUrl;  //点击菜单访问的方法名
	private Integer parentId;  //上级菜单ID
	private String menuDescription;  //菜单描述
	private String state;       //状态，是否是叶子节点
	private String iconCls;    //菜单图标
	private Integer seq;     //顺序序号
	private Integer[] menuIds;//菜单ID组成的集合
	
	// 操作按钮名称合集
	private String operations;
	
	
	
	
	public String getOperations() {
		return operations;
	}
	public void setOperations(String operations) {
		this.operations = operations;
	}
	public Integer[] getMenuIds() {
		return menuIds;
	}
	public void setMenuIds(Integer[] menuIds) {
		this.menuIds = menuIds;
	}
	public Integer getMenuId() {
		return menuId;
	}
	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getMenuUrl() {
		return menuUrl;
	}
	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public String getMenuDescription() {
		return menuDescription;
	}
	public void setMenuDescription(String menuDescription) {
		this.menuDescription = menuDescription;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getIconCls() {
		return iconCls;
	}
	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
}

