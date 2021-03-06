package lichunhui.service;



import java.util.List;

public interface TypeService<T> {

	// 查询所有
	public abstract List<T> findType(T t) throws Exception;

	// 数量
	public abstract int countType(T t) throws Exception;

	// 新增
	public abstract void addType(T t) throws Exception;

	// 修改
	public abstract void updateType(T t) throws Exception;

	// 删除
	public abstract void deleteType(Integer id) throws Exception;
	
	// 根据名称查询，判断是否重用
	public abstract T  existTypeWithName(String name) throws Exception;
}

