package lichunhui.service.impl;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lichunhui.dao.TypeDao;
import lichunhui.service.TypeService;

@Service("typeService")//注入dao,用于标注服务层，主要用来进行业务的逻辑处理
public class TypeServiceImpl<T> implements TypeService<T>{
	
	@Autowired
	private TypeDao<T> dao;

	public List<T> findType(T t) throws Exception {
		return dao.findType(t);
	}

	public int countType(T t) throws Exception {
		return dao.countType(t);
	}

	public void addType(T t) throws Exception {
		dao.addType(t);
	}

	public void updateType(T t) throws Exception {
		dao.updateType(t);
	}

	public void deleteType(Integer id) throws Exception {
		dao.deleteType(id);
	}
	// 根据名称查询，判断是否重用
	public  T  existTypeWithName(String name) throws Exception{
		return dao.existTypeWithName(name);
	}

}

