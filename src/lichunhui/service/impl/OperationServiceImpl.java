package lichunhui.service.impl;



import java.util.List;

import org.springframework.stereotype.Service;

import lichunhui.dao.OperationDao;
import lichunhui.service.OperationService;

@Service("operationService")
public class OperationServiceImpl<T> implements OperationService<T>{
	
	private OperationDao<T> dao;//注入dao,用于标注服务层，主要用来进行业务的逻辑处理
	public void setDao(OperationDao<T> dao) {
		this.dao = dao;
	}

	public void addOperation(T t) throws Exception {
		dao.addOperation(t);
	}

	public int countOperation(T t) throws Exception {
		return dao.countOperation(t);
	}

	public void deleteOperation(Integer operationId) throws Exception {
		dao.deleteOperation(operationId);
	}

	public List<T> findOperation(T t) throws Exception {
		return dao.findOperation(t);
	}

	public void updateOperation(T t) throws Exception {
		dao.updateOperation(t);
	}

}

