package lichunhui.entity;



import java.io.Serializable;

/**
 * 角色管理
 */
public class Role extends BaseEntity implements Serializable{

	/**
	 * 定义程序序列化ID
	 * 作用：相当于java类的身份证。主要用于版本控制。
	 */
	private static final long serialVersionUID = 1L;

	private Integer roleId;  //角色id
	private String roleName;  //角色名称
	private String menuIds;   //菜单ID组成的集合
	private String operationIds;  //按钮组成的集合
	private String roleDescription;  //角色描述
	
	
	
	public String getOperationIds() {
		return operationIds;
	}
	public void setOperationIds(String operationIds) {
		this.operationIds = operationIds;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getMenuIds() {
		return menuIds;
	}
	public void setMenuIds(String menuIds) {
		this.menuIds = menuIds;
	}
	public String getRoleDescription() {
		return roleDescription;
	}
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}
	
}
