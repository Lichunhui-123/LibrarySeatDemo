package lichunhui.entity;



import java.io.Serializable;





/**
 * 用户管理
 */
public class User extends BaseEntity implements Serializable{
	
	/**
	 * 定义程序序列化ID
	 * 作用：相当于java类的身份证。主要用于版本控制。
	 * 即序列化时保持版本的兼容性，即在版本升级时反序列化仍保持对象的唯一性。
	 * 有两种生成方式：
                一个是默认的1L，比如：private static final long serialVersionUID = 1L;
                一个是根据类名、接口名、成员方法及属性等来生成一个64位的哈希字段
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer userId;  //主键
	private String userName; //用户名
	private String password;  //密码
	private Integer userType;  //类型。不用管这个
	private Integer roleId;   //所属角色
	private String userDescription;//用户描述
	private String roleName;//角色名称
	
	
	
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getUserType() {
		return userType;
	}
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public String getUserDescription() {
		return userDescription;
	}
	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}
	
}

