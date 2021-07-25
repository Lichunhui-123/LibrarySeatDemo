package lichunhui.service;



import java.util.List;
import java.util.Map;


public interface MenuService<T> {

	//查询所有菜单信息
	public abstract List<T> findMenu(T t) throws Exception;
	
	//查询菜单信息总数
	public abstract int countMenu(T t) throws Exception;
	
	//添加菜单
	public abstract void addMenu(T t) throws Exception;
	
	//修改菜单
	public abstract void updateMenu(T t) throws Exception;
	
	//删除菜单
	public abstract void deleteMenu(Integer menuId) throws Exception;
	
	//通过菜单名判断是否存在，（新增时不能重名）
    public abstract T existMenuWithMenuName(String menuName) throws Exception;
	//加载权限菜单树
	@SuppressWarnings("unchecked")
	public abstract List<T> menuTree(Map map) throws Exception;
}
