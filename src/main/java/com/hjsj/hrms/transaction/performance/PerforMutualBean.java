/*
 * 创建日期 2005-6-25
 *
 */
package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.actionform.performance.AppraiseselfForm;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * @author luangaojiong
 *
 * 
 * 互评用户权限控制Bean
 */
public class PerforMutualBean {
	
	
	
	/*******************************
	 * 处理权限控制
	 * 
	 *  
	 *******************************/
	public ArrayList getPurviewContral(String userId,String objectId,ArrayList pointlist,
			String planId,Connection con,String template_id) {
		ArrayList list = new ArrayList();
		
		ContentDAO dao = new ContentDAO(con);

		String tableName = "per_pointpriv_" + planId;
		String sqlTemp = "select * from " + tableName + " where object_id='"
				+ objectId + "' and mainbody_id='" + userId + "'";
		/**
		 * 过滤pointlist
		 */
		String sql2="select point_id from per_template_point where item_id in (select item_id from per_template_item where template_id='"+template_id+"')";
		
		//System.out.println("-------->com.hjsj.hrms.transaction.performance.PerforBean-->getPurviewContral-->sql2-->"+sql2);
		try(
			ResultSet rs2=dao.search(sql2);
		)
		{
			ArrayList newPointlist=new ArrayList();
			while(rs2.next())
			{
				newPointlist.add(PubFunc.NullToZero(rs2.getString("point_id")));
			}

			ArrayList secondpointlist=new ArrayList();
			for(int i=0;i<pointlist.size();i++)
			{
				AppraiseselfForm af=(AppraiseselfForm)pointlist.get(i);
				if(newPointlist.contains(af.getPointId()))
				{
					secondpointlist.add(af);
				}
			}
			pointlist=secondpointlist;
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		/**
		 * 过滤完毕
		 */
		/**
		 * 判断是否记录为空
		 */
		String flag = "0";
		ResultSet rs = null;
		try {
			rs = dao.search(sqlTemp);
			if (rs.next()) {
				flag = "1";
			} else {
				flag = "0";
			}
		} catch (Exception ex) {
			flag = "0";
		} finally {
			PubFunc.closeResource(rs);
		}

		if ("0".equals(flag)) {
			list = pointlist;
		} else {
			//System.out.println("-------->com.hjsj.hrms.transaction.performance.PerforBean-->getPurviewContral-->sql-->"+sqlTemp);
			for (int i = 0; i < pointlist.size(); i++) {
				AppraiseselfForm af = (AppraiseselfForm) pointlist.get(i);
				int num = getContralCode(af.getPointId(), sqlTemp,con);
				if (num != 0) {
					list.add(af);
					//System.out.println("-------->com.hjsj.hrms.transaction.performance.PerforBean-->getPurviewContral-->arraylist");
				}

			}
		}

		return list;
	}

	/**
	 * 处理权限控制的for循环调用函数
	 * 
	 * @author luangaojiong
	 */

	public int getContralCode(String pointid, String sql,Connection con) {
		int num = 0;
		
		ContentDAO dao = new ContentDAO(con);
		
		ResultSet rs = null;
		String pointColumnName = "C_" + pointid;

		try {
		
			rs = dao.search(sql);
			if (rs.next()) {
				
				num=Integer.parseInt(PubFunc.NullToZero(rs.getString(pointColumnName)));
									
			}

		} catch (Exception ex) {
			System.out.println("--------->com.hjsj.hrms.transaction.performance.PerforMutualBean-->getContralCode error");
			ex.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return num;

	}

}
