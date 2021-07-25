package lichunhui.service.impl;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lichunhui.dao.RoomDao;
import lichunhui.service.RoomService;

@Service("roomService")//注入dao,用于标注服务层，主要用来进行业务的逻辑处理
public class RoomServiceImpl<T> implements RoomService<T>{
	
	@Autowired
	private RoomDao<T> dao;

	public List<T> findRoom(T t) throws Exception {
		return dao.findRoom(t);
	}

	public int countRoom(T t) throws Exception {
		return dao.countRoom(t);
	}

	public void addRoom(T t) throws Exception {
		dao.addRoom(t);
	}

	public void updateRoom(T t) throws Exception {
		dao.updateRoom(t);
	}

	public void deleteRoom(Integer id) throws Exception {
		dao.deleteRoom(id);
	}

	public int findScoreByRoomid(int roomid) throws Exception {
		return dao.findScoreByRoomid(roomid);
	}
	// 根据名称查询，判断是否重用
	public  T  existRoomWithName(String name) throws Exception{
		return dao.existRoomWithName(name);
	}
	
	// 通过阅览室判断是否存在
	public  T existRoomWithTypeId(Integer typeid) throws Exception{
		return dao.existRoomWithTypeId(typeid);
	}

}

