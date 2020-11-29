package com.hjsj.hrms.businessobject.stat;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class InfoSetupBo {
	
	private Connection conn = null;
	
	public InfoSetupBo(Connection conn) {
		this.conn = conn;
	}
	/**
	 * 根据id获得常用统计列表中的常用条件id
	 * @param id 常用统计表id
	 * @return list RecordVo集合
	 */
	public ArrayList getCondList(String id) {
		ArrayList list = new ArrayList();
		// 根据id查询对应的常用条件condid
		StringBuffer buffid = new StringBuffer();
		buffid.append("select condid from sname where id='");
		buffid.append(id);
		buffid.append("'");
		
		ContentDAO dao = new ContentDAO(this.conn);
		ResultSet rs = null;
		try {
			rs = dao.search(buffid.toString());
			if (rs.next()) {
				String condid = rs.getString("condid");
				if (condid != null && condid.length() > 0&&!"null".equalsIgnoreCase(condid)){
					String []condids = condid.split(",");
					for (int i = 0; i < condids.length; i++) {
						String sql = "select id,name from lexpr where id='" + condids[i] + "'";
						rs = dao.search(sql);
						if (rs.next()) {
							RecordVo vo = new RecordVo("lexpr");
							vo.setString("id", rs.getInt("id")+"");
							vo.setString("name",rs.getString("name"));
							list.add(vo);
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	/**
	 * 根据id获得常用统计表的人员库
	 * @param id
	 * @return string,没有设置时，返回空字符窜
	 */
	public String getnbase (String id) {
		ResultSet rs = null;
		String dbname = "";
		String dbsql = "select nbase from sname where id='"+id+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			rs = dao.search(dbsql);
			if (rs.next()) {
				dbname = rs.getString("nbase");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		if (dbname == null) {
			dbname = "";
		}
		return dbname;
	}
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}

}
