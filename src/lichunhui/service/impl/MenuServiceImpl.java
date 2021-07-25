package lichunhui.service.impl;



import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lichunhui.dao.MenuDao;
import lichunhui.service.MenuService;

@Service("menuService")//注入dao,用于标注服务层，主要用来进行业务的逻辑处理
public class MenuServiceImpl<T> implements MenuService<T>{

	private MenuDao<T> dao;
	public void setDao(MenuDao<T> dao) {
		this.dao = dao;
	}
	
	public void addMenu(T t) throws Exception {
		dao.addMenu(t);
	}

	public int countMenu(T t) throws Exception {
		return dao.countMenu(t);
	}

	public void deleteMenu(Integer menuId) throws Exception {
		dao.deleteMenu(menuId);
	}

	public List<T> findMenu(T t) throws Exception {
		return dao.findMenu(t);
	}

	@SuppressWarnings("unchecked")
	public List<T> menuTree(Map map) throws Exception {
		return dao.menuTree(map);
	}

	public void updateMenu(T t) throws Exception {
		dao.updateMenu(t);
	}
	public T existMenuWithMenuName(String menuName) throws Exception {
		return dao.existMenuWithMenuName(menuName);
	}
	

}

