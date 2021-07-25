package lichunhui.dao;



import java.util.List;

import org.mybatis.spring.annotation.Mapper;

@Mapper("scoreDao")
public interface ScoreDao<T>{
	// 查询所有
	public abstract List<T> findScore(T t) throws Exception;

	// 数量
	public abstract int countScore(T t) throws Exception;

	// 新增
	public abstract void addScore(T t) throws Exception;
	
	//根据学号查询
	public abstract T findOneScore(String studentno) throws Exception;
	//根据学号删除
	public abstract void deleteOneScore(String studentno) throws Exception;

	// 修改
	public abstract void updateScore(T t) throws Exception;

	// 删除
	public abstract void deleteScore(Integer id) throws Exception;
}
