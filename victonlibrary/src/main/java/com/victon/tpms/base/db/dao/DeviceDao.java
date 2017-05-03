package com.victon.tpms.base.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.victon.tpms.base.db.DatabaseHelper;
import com.victon.tpms.base.db.entity.Device;
import com.victon.tpms.base.db.entity.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeviceDao {
	private Dao<Device, Integer> articleDaoOpe;
	private DatabaseHelper helper;

	@SuppressWarnings("unchecked")
	public DeviceDao(Context context) {
		try {
			helper = DatabaseHelper.getHelper(context);
			articleDaoOpe = helper.getDao(Device.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加一个Article
	 * 
	 * @param article
	 */
	public void add(Device article) {
		try {
			articleDaoOpe.create(article);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 更新一个Article
	 *
	 * @param device
	 */
	public void update(Device device) {
//		try {
//			articleDaoOpe.updateRaw("UPDATE tb_device set right_BD = ?,left_BD = ?",new String[]{"sd","ege"});
////			Device device1 = new Device();
////			device1.setRight_BD("aaaa");
////			articleDaoOpe.update(device1);
////			updateBuilder.where().eq("id", id);
////			updateBuilder.updateColumnValue("left_FD","zzzz");//.where().eq("id", id);//.where().eq(columnName,value);
////			updateBuilder.update();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	/**
	 * 更新一个Article
	 *
	 * @param values
	 */
	public void update(int state,int id,String values) {
		if (articleDaoOpe == null) return;
		try {
			switch (state) {
				case 1:
					articleDaoOpe.updateRaw("UPDATE tb_device set left_FD = ? where id = ?",new String[]{values,String.valueOf(id)});
					break;
				case 2:
					articleDaoOpe.updateRaw("UPDATE tb_device set right_FD = ? where id = ?",new String[]{values,String.valueOf(id)});
					break;
				case 3:
					articleDaoOpe.updateRaw("UPDATE tb_device set left_BD = ? where id = ?",new String[]{values,String.valueOf(id)});
					break;
				case 4:
					articleDaoOpe.updateRaw("UPDATE tb_device set right_BD = ? where id = ?",new String[]{values,String.valueOf(id)});
					break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 更新一个Article
	 *
	 * @param values
	 */
	public void update(int state,String deviceName,String values) {
		if (articleDaoOpe == null) return;
		try {
			switch (state) {
				case 0:
					articleDaoOpe.updateRaw("UPDATE tb_device set left_FD = ? where deviceName = ?",new String[]{values,deviceName});
					break;
				case 1:
					articleDaoOpe.updateRaw("UPDATE tb_device set right_FD = ? where deviceName = ?",new String[]{values,deviceName});
					break;
				case 2:
					articleDaoOpe.updateRaw("UPDATE tb_device set left_BD = ? where deviceName = ?",new String[]{values,deviceName});
					break;
				case 3:
					articleDaoOpe.updateRaw("UPDATE tb_device set right_BD = ? where deviceName = ?",new String[]{values,deviceName});
					break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 更新一个Article
	 *
	 * @param values
	 */
	public void updateDefult(int id,String values) {
		try {
			articleDaoOpe.updateRaw("UPDATE tb_device set isDefult = ? ", new String[]{"false"});
			articleDaoOpe.updateRaw("update tb_device set isDefult = ? where id = ?",new String[]{values,String.valueOf(id)});
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 通过Id得到一个Article
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Device getArticleWithUser(int id) {
		Device article = null;
		try {
			article = articleDaoOpe.queryForId(id);
			helper.getDao(User.class).refresh(article.getUser());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return article;
	}

	/**
	 * 通过Id得到一篇文章
	 * 
	 * @param id
	 * @return
	 */
	public Device get(int id) {
		Device article = null;
		try {
			article = articleDaoOpe.queryForId(id);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return article;
	}
	/**
	 * 通过deviceName得到一篇文章
	 *
	 * @param deviceName
	 * @return
	 */
	public List<Device> get(String deviceName) {
		List<Device> article = null;
		try {
			article = articleDaoOpe.queryBuilder().where().eq("deviceName", deviceName)
					.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return article;
	}
	/**
	 * 通过UserId获取所有的文章
	 * 
	 * @param userId
	 * @return
	 */
	public List<Device> listByUserId(int userId) {
		try {
			/*QueryBuilder<Article, Integer> articleBuilder = articleDaoOpe
					.queryBuilder();
			QueryBuilder userBuilder = helper.getDao(User.class).queryBuilder();
			articleBuilder.join(userBuilder);
			
			
			Where<Article, Integer> where = queryBuilder.where();
			where.eq("user_id", 1);
			where.and();
			where.eq("name", "xxx");

			// 或者
			articleDaoOpe.queryBuilder().//
					where().//
					eq("user_id", 1).and().//
					eq("name", "xxx");
			//
			articleDaoOpe.updateBuilder().updateColumnValue("name","zzz").where().eq("user_id", 1);
			where.or(
					//
					where.and(//
							where.eq("user_id", 1), where.eq("name", "xxx")),
					where.and(//
							where.eq("user_id", 2), where.eq("name", "yyy")));*/

			return articleDaoOpe.queryBuilder().where().eq("user_id", userId)
					.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询所有的device
	 * @return
     */
	public List<Device> listByAll() {
		List<Device> list = new ArrayList<>();
		try {
			list = articleDaoOpe.queryForAll();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void delete(Device article) {
		try {
			articleDaoOpe.delete(article);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
