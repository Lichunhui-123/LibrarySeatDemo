package lichunhui.service.impl;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lichunhui.dao.ScoreDao;
import lichunhui.service.ScoreService;

@Service("scoreService")//注入dao,用于标注服务层，主要用来进行业务的逻辑处理
public class ScoreServiceImpl<T> implements ScoreService<T>{
	
	@Autowired
	private ScoreDao<T> dao;

	public List<T> findScore(T t) throws Exception {
		return dao.findScore(t);
	}

	public int countScore(T t) throws Exception {
		return dao.countScore(t);
	}

	public void addScore(T t) throws Exception {
		dao.addScore(t);
	}

	public void updateScore(T t) throws Exception {
		dao.updateScore(t);
	}

	public void deleteScore(Integer id) throws Exception {
		dao.deleteScore(id);
	}

	public T findOneScore(String studentno) throws Exception {
		return dao.findOneScore(studentno);
	}

	public void deleteOneScore(String studentno) throws Exception {
		dao.deleteOneScore(studentno);
	}

}

