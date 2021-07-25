package lichunhui.service.impl;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lichunhui.dao.IllegalDao;
import lichunhui.service.IllegalService;

@Service("illegalService")//注入dao,用于标注服务层，主要用来进行业务的逻辑处理
public class IllegalServiceImpl<T> implements IllegalService<T>{
	
	@Autowired
	private IllegalDao<T> dao;

	public List<T> findIllegal(T t) throws Exception {
		return dao.findIllegal(t);
	}

	public int countIllegal(T t) throws Exception {
		return dao.countIllegal(t);
	}

	public void addIllegal(T t) throws Exception {
		dao.addIllegal(t);
	}

	public void updateIllegal(T t) throws Exception {
		dao.updateIllegal(t);
	}

	public void deleteIllegal(Integer id) throws Exception {
		dao.deleteIllegal(id);
	}

}

