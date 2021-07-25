package lichunhui.dao;



import java.util.List;

import org.mybatis.spring.annotation.Mapper;

@Mapper("studentDao")
public interface StudentDao<T> {
	// 查询所有
	public abstract List<T> findStudent(T t) throws Exception;

	// 数量
	public abstract int countStudent(T t) throws Exception;

	// 新增
	public abstract void addStudent(T t) throws Exception;

	// 修改
	public abstract void updateStudent(T t) throws Exception;

	// 删除
	public abstract void deleteStudent(Integer id) throws Exception;
	//根据学号查询单学生信息
	public abstract T findOneStudentByno(String no) throws Exception;
	
	// 通过角色判断是否存在
	public abstract T existStudentWithClazzId(Integer clazzid) throws Exception;
	
	
}
