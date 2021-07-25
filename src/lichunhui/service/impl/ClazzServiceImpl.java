package lichunhui.service.impl;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lichunhui.dao.ClazzDao;
import lichunhui.service.ClazzService;

@Service("clazzService")//注入dao,用于标注服务层，主要用来进行业务的逻辑处理
public class ClazzServiceImpl<T> implements ClazzService<T>{
	
	@Autowired
	private ClazzDao<T> dao;

	public List<T> findClazz(T t) throws Exception {
		return dao.findClazz(t);
	}

	public int countClazz(T t) throws Exception {
		return dao.countClazz(t);
	}

	public void addClazz(T t) throws Exception {
		dao.addClazz(t);
	}

	public void updateClazz(T t) throws Exception {
		dao.updateClazz(t);
	}

	public void deleteClazz(Integer id) throws Exception {
		dao.deleteClazz(id);
	}

	//通过班级名判断是否存在，（新增时不能重名）
	public T existClazzWithName(String name) throws Exception {
		return dao.existClazzWithName(name);
	}
}

