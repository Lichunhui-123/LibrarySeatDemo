package lichunhui.service.impl;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lichunhui.dao.ChoiceDao;
import lichunhui.service.ChoiceService;

@Service("choiceService")//注入dao,用于标注服务层，主要用来进行业务的逻辑处理
public class ChoiceServiceImpl<T> implements ChoiceService<T>{
	
	@Autowired
	private ChoiceDao<T> dao;

	public List<T> findChoice(T t) throws Exception {
		return dao.findChoice(t);
	}

	// 根据时间查询所有过去的选座记录
	public  List<T> findChoiceByTime(T t) throws Exception{
		return dao.findChoiceByTime(t);
	}
	
	// 根据时间查询所有过去的选座记录
		public  List<T> findChoiceByStatus(T t) throws Exception{
			return dao.findChoiceByStatus(t);
		}
	public int countChoice(T t) throws Exception {
		return dao.countChoice(t);
	}

	public void addChoice(T t) throws Exception {
		dao.addChoice(t);
	}

	public void modifyChoice(T t) throws Exception {
		dao.modifyChoice(t);
	}

	public void modifyOneChoice(T t) throws Exception {
		dao.modifyOneChoice(t);
	}
	
	public void modifyOneChoiceByStatus(T t) throws Exception {
		dao.modifyOneChoiceByStatus(t);
	}
	
	public void deleteChoice(Integer id) throws Exception {
		dao.deleteChoice(id);
	}
	
	public void deleteChoiceByTime(T t) throws Exception {
		dao.deleteChoiceByTime(t);
	}

	public T findOneChoice(T t) throws Exception {
		return dao.findOneChoice(t);
	}


	public void cancelChoice(T t) throws Exception {
		dao.cancelChoice(t);
	}

	


}

