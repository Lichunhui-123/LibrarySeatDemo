package lichunhui.dao;



import java.util.List;
import java.util.Map;

import org.mybatis.spring.annotation.Mapper;

/*@Mapper注解的的作用:
 * 1.为了把mapper这个DAO交給Spring管理
 * 2.为了不再写mapper映射文件
 * 3:为了给mapper接口 自动根据一个添加@Mapper注解的接口生成一个实现类 */
@Mapper("userDao")
public interface UserDao<T> {

	// 查询所有
	public abstract List<T> findUser(T t) throws Exception;
	
	// 数量
	public abstract int countUser(T t) throws Exception;
	
	// 通过ID查询
	public abstract T findOneUser(Integer id) throws Exception;
	
	// 新增
	public abstract void addUser(T t) throws Exception;
	
	// 修改
	public abstract void updateUser(T t) throws Exception;
	
	// 删除
	public abstract void deleteUser(Integer id) throws Exception;
	
	// 登录
	public abstract T loginUser(Map<String, String> map) throws Exception;
	
	//通过用户名判断是否存在，（新增时不能重名）
	public abstract T existUserWithUserName(String userName) throws Exception;
	
	// 通过角色判断是否存在
	public abstract T existUserWithRoleId(Integer roleId) throws Exception;
	
}

