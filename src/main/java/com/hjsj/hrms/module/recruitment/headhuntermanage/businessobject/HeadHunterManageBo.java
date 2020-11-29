package com.hjsj.hrms.module.recruitment.headhuntermanage.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import javax.sql.RowSet;
import java.sql.Connection;

public class HeadHunterManageBo {

	/**
	 * 通过 猎头（机构）信息表z60 猎头id查询是否可以删除
	 * 规则：陪了账号的不可用删除
	 * @param groupid
	 * @return 
	 */
	public boolean isHunterGroupCanBeDelete(String z6000){
		String sql = "select '1' from zp_headhunter_login where z6000="+z6000;
		Connection conn=null;
	    RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			rs = new ContentDAO(conn).search(sql);
			if(rs.next())
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(conn);
		}
		
		return true;
	}
	
	/**
	 *通过 候选人应聘职位对应表（zp_pos_tache） 查询推荐用户（recusername）是否录入了简历
	 *录入简历不允许删除
	 * @param username
	 * @return
	 */
	public boolean isHunterUserCanBeDelete(String username){
		String sql = "select '1' from zp_pos_tache where recusername='"+username+"'";
		Connection conn=null;
	    RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			rs = new ContentDAO(conn).search(sql);
			if(rs.next())
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(conn);
		}
		
		return true;
	}
	
}
