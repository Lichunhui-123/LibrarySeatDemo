package lichunhui.service.impl;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lichunhui.dao.StudentDao;
import lichunhui.service.StudentService;

@Service("studentService")//注入dao,用于标注服务层，主要用来进行业务的逻辑处理
public class StudentServiceImpl<T> implements StudentService<T>{

	@Autowired
	private StudentDao<T> dao;
	
	public List<T> findStudent(T t) throws Exception {
		return dao.findStudent(t);
	}

	public int countStudent(T t) throws Exception {
		return dao.countStudent(t);
	}

	public void addStudent(T t) throws Exception {
		dao.addStudent(t);
	}

	public void updateStudent(T t) throws Exception {
		dao.updateStudent(t);
	}

	public void deleteStudent(Integer id) throws Exception {
		dao.deleteStudent(id);
	}

	public T findOneStudentByno(String no) throws Exception {
		return dao.findOneStudentByno(no);
	}
	
	public T existStudentWithClazzId(Integer clazzid) throws Exception {
		return dao.existStudentWithClazzId(clazzid);
	}

}

