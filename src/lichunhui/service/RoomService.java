package lichunhui.service;



import java.util.List;

public interface RoomService<T> {
	// 查询所有
	public abstract List<T> findRoom(T t) throws Exception;

	// 数量
	public abstract int countRoom(T t) throws Exception;

	// 新增
	public abstract void addRoom(T t) throws Exception;

	// 修改
	public abstract void updateRoom(T t) throws Exception;

	// 删除
	public abstract void deleteRoom(Integer id) throws Exception;
	
	public abstract int findScoreByRoomid(int roomid) throws Exception;
	
	// 根据名称查询，判断是否重用
	public abstract T  existRoomWithName(String name) throws Exception;
	
	// 通过阅览室判断是否存在
	public abstract T existRoomWithTypeId(Integer typeid) throws Exception;
}

