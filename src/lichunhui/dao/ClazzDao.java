package lichunhui.dao;


import java.util.List;

import org.mybatis.spring.annotation.Mapper;

@Mapper("clazzDao")
public interface ClazzDao<T> {
	// 查询所有
	public abstract List<T> findClazz(T t) throws Exception;

	// 数量
	public abstract int countClazz(T t) throws Exception;

	// 新增
	public abstract void addClazz(T t) throws Exception;

	// 修改
	public abstract void updateClazz(T t) throws Exception;

	// 删除
	public abstract void deleteClazz(Integer id) throws Exception;
	
	//通过班级名判断是否存在，（新增时不能重名）
	public abstract T existClazzWithName(String name) throws Exception;
	
}

