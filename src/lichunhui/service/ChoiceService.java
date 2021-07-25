package lichunhui.service;



import java.util.List;

public interface ChoiceService<T> {
	// 查询所有
	public abstract List<T> findChoice(T t) throws Exception;

	
	// 根据时间查询所有过去的选座记录
	public abstract List<T> findChoiceByStatus(T t) throws Exception;
	// 根据时间查询所有过去的选座记录
	public abstract List<T> findChoiceByTime(T t) throws Exception;
	// 数量
	public abstract int countChoice(T t) throws Exception;

	// 新增
	public abstract void addChoice(T t) throws Exception;

	// 修改
	public abstract void modifyChoice(T t) throws Exception;

	// 修改单个
	public abstract void modifyOneChoiceByStatus(T t) throws Exception;
	
	// 修改
	public abstract void modifyOneChoice(T t) throws Exception;
	// 删除
	public abstract void deleteChoice(Integer id) throws Exception;
	
	// 删除
		public abstract void deleteChoiceByTime(T t) throws Exception;
	//查询单个学生选中的座位信息
	public abstract T findOneChoice(T t) throws Exception;
	
	//根据学生学号和seatkeyword删除
	public abstract void cancelChoice(T t) throws Exception;
	
	
}

