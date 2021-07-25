package lichunhui.service;



import java.util.List;

public interface ScoreService<T> {

	// 查询所有
	public abstract List<T> findScore(T t) throws Exception;

	// 数量
	public abstract int countScore(T t) throws Exception;

	// 新增
	public abstract void addScore(T t) throws Exception;
    
	//根据学号查询单个学生的信用积分
	public abstract T findOneScore(String studentno) throws Exception;
	
	//根据学号删除单个学生的信用积分
	public abstract void deleteOneScore(String studentno) throws Exception;
	
	// 修改
	public abstract void updateScore(T t) throws Exception;

	// 删除
	public abstract void deleteScore(Integer id) throws Exception;
}
