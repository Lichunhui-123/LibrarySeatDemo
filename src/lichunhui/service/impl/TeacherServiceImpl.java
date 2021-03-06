package lichunhui.service.impl;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lichunhui.dao.TeacherDao;
import lichunhui.service.TeacherService;

@Service("teacherService")//注入dao,用于标注服务层，主要用来进行业务的逻辑处理
public class TeacherServiceImpl<T> implements TeacherService<T>{
	
	@Autowired
	private TeacherDao<T> dao;

	public List<T> findTeacher(T t) throws Exception {
		return dao.findTeacher(t);
	}

	public int countTeacher(T t) throws Exception {
		return dao.countTeacher(t);
	}

	public T findOneTeacher(Integer id) throws Exception {
		return dao.findOneTeacher(id);
	}

	public void addTeacher(T t) throws Exception {
		dao.addTeacher(t);
	}

	public void updateTeacher(T t) throws Exception {
		dao.updateTeacher(t);
	}

	public void deleteTeacher(Integer id) throws Exception {
		dao.deleteTeacher(id);
	}
	
	public T existTeacherWithNo(String no) throws Exception {
		return dao.existTeacherWithNo(no);
	}

}

