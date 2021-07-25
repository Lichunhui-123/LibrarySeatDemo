package lichunhui.dao;



import java.util.List;
import java.util.Map;

import org.mybatis.spring.annotation.Mapper;

@Mapper("menuDao")
public interface MenuDao<T> {
	//查询
	public abstract List<T> findMenu(T t) throws Exception;
	//总数
	public abstract int countMenu(T t) throws Exception;
	//添加
	public abstract void addMenu(T t) throws Exception;
	//修改
	public abstract void updateMenu(T t) throws Exception;
	//删除
	public abstract void deleteMenu(Integer menuId) throws Exception;
	
	//通过菜单名判断是否存在，（新增时不能重名）
    public abstract T existMenuWithMenuName(String menuName) throws Exception;
	
	@SuppressWarnings("unchecked")
	public abstract List<T> menuTree(Map map) throws Exception;
}
