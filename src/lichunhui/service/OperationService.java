package lichunhui.service;



import java.util.List;

public interface OperationService<T> {

	//查询所有按钮
	public abstract List<T> findOperation(T t) throws Exception;
	
	//查询总数
	public abstract int countOperation(T t) throws Exception;
	
	//添加
	public abstract void addOperation(T t) throws Exception;
	
	//修改
	public abstract void updateOperation(T t) throws Exception;
	
	//删除
	public abstract void deleteOperation(Integer operationId) throws Exception;
	
}
