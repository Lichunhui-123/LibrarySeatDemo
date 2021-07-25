package lichunhui.entity;



import java.io.Serializable;

/**
 * 按钮管理
 */
public class Operation extends BaseEntity implements Serializable{

	/**
	 * 定义程序序列化ID
	 * 作用：相当于java类的身份证。主要用于版本控制。
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer operationId;  //按钮ID
	private Integer menuId;       //所属哪一个页面菜单的ID
	private String operationName;  //按钮名称
	private String menuName;//按钮所属菜单的名称
	
	
	
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public Integer getOperationId() {
		return operationId;
	}
	public void setOperationId(Integer operationId) {
		this.operationId = operationId;
	}
	public Integer getMenuId() {
		return menuId;
	}
	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}
	public String getOperationName() {
		return operationName;
	}
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	
}
