package lichunhui.service.impl;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lichunhui.dao.SeatDao;
import lichunhui.service.SeatService;

@Service("seatService")//注入dao,用于标注服务层，主要用来进行业务的逻辑处理
public class SeatServiceImpl<T> implements SeatService<T>{
	
	@Autowired
	private SeatDao<T> dao;

	public List<T> findSeat(T t) throws Exception {
		return dao.findSeat(t);
	}

	public int countSeat(T t) throws Exception {
		return dao.countSeat(t);
	}

	public void insertSeat(T t) throws Exception {
		dao.insertSeat(t);
	}

	public void modifySeat(T t) throws Exception {
		dao.modifySeat(t);
	}

	public void deleteSeat(T t) throws Exception {
		dao.deleteSeat(t);
	}

	public void occupySeat(T t) throws Exception {
		dao.occupySeat(t);
	}

	public void cancelSeat(String keyword) throws Exception {
		dao.cancelSeat(keyword);
	}

	public int findBlock(T t) throws Exception {
		return dao.findBlock(t);
	}
	
	// 通过座位判断是否存在
	public T existSeatWithRoomId(Integer roomid) throws Exception{
		return dao.existSeatWithRoomId(roomid);
	}
}

