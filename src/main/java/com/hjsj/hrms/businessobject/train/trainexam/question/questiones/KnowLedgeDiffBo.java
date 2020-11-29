package com.hjsj.hrms.businessobject.train.trainexam.question.questiones;
/**
 * 知识点和难度的比例值
 */

import com.hrms.frame.dao.ContentDAO;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class KnowLedgeDiffBo {
	
	private Connection conn;
	private String r5300;
	private static HashMap knowLedgeMap;
	
	public KnowLedgeDiffBo(Connection conn,String r5300){
		this.conn = conn;
		this.r5300 = r5300;
		knowLedgeMap = new HashMap();
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		RowSet rs = null;
		String sql = "select * from tr_knowledge_diff where r5300="+r5300;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			rs = dao.search(sql);
			while(rs.next()){
				String type_id = rs.getString("type_id");//类型
				String know_id = rs.getString("know_id");//知识点
				for (int j = 1; j < 9; j++) {
					int difficulty = rs.getInt("diff"+j);
					if(difficulty>0) {
                        knowLedgeMap.put(type_id+"_"+know_id+"_"+(j-3), String.valueOf(difficulty));//难度键值正好和列的位置相差为3
                    }
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(rs!=null){
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * typeid_knowid_diff
	 * @param key
	 * @return
	 */
	public static String getValue(String key){
		return (String) knowLedgeMap.get(key);
	}
	
	public static int getMapSize(){
		//System.out.println(knowLedgeMap.size());
		return knowLedgeMap.size();
	}
}
