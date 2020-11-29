/*
 * 创建日期 2005-6-30
 *
 */
package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * 项目及要素展示类以及选票统计类
 * 
 * @author luangaojiong
 */
public class StatisticGradeBean {

	Hashtable itemHt = new Hashtable(); //分检出的项目Hashtable对象

	/**
	 * 取得项目要素列表
	 * 
	 * @param pointlist
	 * @param userpointlist
	 * @return
	 */

	ArrayList getItemwhilelist(ArrayList pointlist, ArrayList userpointlist,
			ArrayList itemscorelist) {
		ArrayList list = new ArrayList();
		ArrayList itemlist = new ArrayList();
		ArrayList pointTemp = new ArrayList();
		/**
		 * 处理pointlist清除无用项,添加对象内容
		 */

		pointTemp = doPointlist(pointlist, userpointlist);
		/**
		 * 分检项目
		 */
		itemlist = getItemIdArrayList(pointTemp);
		/**
		 * 得到输出页面ArrayList
		 */
		ArrayList lsttemp = new ArrayList();
		for (int i = 0; i < itemlist.size(); i++) {
			StatisticBean sttb = new StatisticBean();
			sttb.setItemId(itemlist.get(i).toString());
			sttb.setItemName(itemHt.get(itemlist.get(i).toString()).toString());
			sttb.setItemScore(getItemScore(itemscorelist, itemlist.get(i)
					.toString()));
			//System.out.println("---->com.hjsj.hrms.transaction.statistic-->getItemwhilelist-->ItemName-->"+sttb.getItemName());
			lsttemp = getItemPointlst(itemlist.get(i).toString(), pointTemp);
			sttb.setPointlist(lsttemp);
			list.add(sttb);

		}

		return list;
	}

	/**
	 * 得到项目分值
	 */
	public String getItemScore(ArrayList itemscorelist, String itemid) {
		String temp = "";
		//System.out.println("---->StatisticGradeBean-->itemscorelist.size"+itemscorelist.toString());
		//System.out.println("---->StatisticGradeBean-->itemid-->"+itemid);
		for (int i = 0; i < itemscorelist.size(); i++) {
			StatisticBean sttb = (StatisticBean) itemscorelist.get(i);
			if (sttb.getItemId().equals(itemid)) {
				temp = sttb.getItemScore();
				//System.out.println("---->StatisticGradeBean-->ItemScore-->"+temp);
				break;
			}
		}

		return temp;
	}

	/**
	 * 得到项目对应的要素数
	 * 
	 * @param itemid
	 * @param pointlist
	 * @return
	 */
	ArrayList getItemPointlst(String itemid, ArrayList pointlist) {
		ArrayList lst = new ArrayList();
		for (int i = 0; i < pointlist.size(); i++) {
			StatisticBean sttb = (StatisticBean) pointlist.get(i);

			if (sttb.getItemId().equals(itemid)) {

				lst.add(sttb);
			}
		}

		return lst;
	}

	/**
	 * 处理pointlist对象 目的添加项目信息
	 * 
	 * @param pointlist
	 * @return
	 */
	ArrayList doPointlist(ArrayList pointlist, ArrayList userpointlist) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < userpointlist.size(); i++) {
			StatisticBean sttb = (StatisticBean) userpointlist.get(i);
			//System.out.println("----->com.hjsj.hrms.transaction.statistic-->score
			// "+sttb.getScore());
			for (int j = 0; j < pointlist.size(); j++) {
				StatisticBean sttb2 = (StatisticBean) pointlist.get(j);
				if (sttb2.getPointId().equals(sttb.getPointId())) {
					sttb.setItemId(sttb2.getItemId());
					sttb.setItemName(sttb2.getItemName());
					sttb.setPointName(sttb2.getPointName());
				}
			}

			list.add(sttb);
		}

		return list;
	}

	/**
	 * 分检出item项目
	 * 
	 * @author luangaojiong
	 */
	ArrayList getItemIdArrayList(ArrayList pointlist) {
		ArrayList itemlst = new ArrayList();
		for (int i = 0; i < pointlist.size(); i++) {
			StatisticBean sttb = (StatisticBean) pointlist.get(i);
			if (!itemlst.contains(sttb.getItemId())) {
				itemlst.add(sttb.getItemId());
				itemHt.put(sttb.getItemId(), sttb.getItemName());
			}
		}

		return itemlst;
	}

	/**
	 * 处理总体评价
	 * 
	 * @param con
	 * @return
	 */
	ArrayList getSelectTick(Connection con, String objectId, String userId,
			String id) {
		ArrayList tickselectlst = new ArrayList();
		if("".equals(objectId.trim()))
		{
			objectId="0";
		}
		/**
		 * 
		 * 处理总体评价
		 */
		Hashtable htxml = new Hashtable();
		LoadXml loadxml = new LoadXml(con, id);
		htxml = loadxml.getDegreeWhole();
		ArrayList gradeHash = new ArrayList();
		if(htxml.get("GradeClass")==null)
		{
			return tickselectlst;
		}
		else if ("".equals(htxml.get("GradeClass").toString())) {
			return tickselectlst;
		} else {
			String gradeId = htxml.get("GradeClass").toString();
			  try
			  {
			  	int num=Integer.parseInt(gradeId);
			  }
			  catch(Exception ex)
			  {
			  	return tickselectlst;
			  }
			ResultSet rs = null;
			try {
				/**
				 * 得到所有等级的数量
				 */
				ContentDAO dao = new ContentDAO(con);
				StringBuffer sb = new StringBuffer();
				sb.append("select id,itemname from per_degreedesc where degree_id=");
				sb.append(gradeId);
				sb.append(" order by id asc");

				rs = dao.search(sb.toString());
				int totalCount = 0;
				while (rs.next()) {
					StatisticBean sttb = new StatisticBean();
					sttb.setGradeName(PubFunc.nullToStr(rs.getString("itemname")));
					sttb.setGradeId(PubFunc.nullToStr(rs.getString("id")).trim());
					gradeHash.add(sttb);
					}
				rs.close();
				/**
				 * 得到等级总数量
				 */
			//	System.out.println("------->StatisticGradeBean-objectId->"+objectId);
				String sql = "select count(*) as gcount from per_mainbody where object_id='"
						+ objectId + "' and whole_grade_id is not null";
				String gradeTotal = "0";
				rs = dao.search(sql);
				if (rs.next()) {
					gradeTotal = rs.getString("gcount");
				}
				rs.close();
				if ("0".equals(gradeTotal)) {
					return tickselectlst;
				} else {
					/**
					 * 得到每一个等级数量
					 */
					StringBuffer sb2 = new StringBuffer();
					sb2.append("select count(whole_grade_id) as wgcount,whole_grade_id from per_mainbody where object_id='");
					sb2.append(objectId);
					sb2.append("' and whole_grade_id is not null group by whole_grade_id");
					Hashtable everygradeHb = new Hashtable();
					rs = dao.search(sb2.toString());
					while (rs.next()) {
						everygradeHb.put(rs.getString("whole_grade_id").trim(),
								PubFunc.NullToZero(rs.getString("wgcount")));

					}
					rs.close();
					
					/**
					 * 得到总体评价list
					 */
					
					for(int i=0;i<gradeHash.size();i++)
					{
										
						StatisticBean stt1=(StatisticBean)gradeHash.get(i);
						StatisticBean sttb = new StatisticBean();
						String wholeid = stt1.getGradeId();
						sttb.setGradeId(wholeid);
						sttb.setGradeName(stt1.getGradeName());
						sttb.setGradeCount(getGradeScore(everygradeHb,wholeid));
						sttb.setGradePercent(getPercent(getGradeScore(everygradeHb,wholeid),Integer.parseInt(gradeTotal)));
						tickselectlst.add(sttb);
					}
					
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}finally {
				PubFunc.closeResource(rs);
			}

		}

		return tickselectlst;
	}

	/**
	 * 得到总体评价分数
	 * 
	 * @param userCount
	 * @param totalCount
	 * @return
	 */
	String getGradeScore(Hashtable everygradeHb, String wholeId) {
		String temp = "0";
		Enumeration et = everygradeHb.keys();
		while (et.hasMoreElements()) {
			String typeid = et.nextElement().toString();
			if (typeid.equals(wholeId)) {
				temp = everygradeHb.get(typeid).toString();
				break;
			}
		}
		return temp;
	}

	/**
	 * 计算百分比数
	 * 
	 * @author luangaojiong
	 *  
	 */
	public String getPercent(String userCount, int totalCount) {
		double perdbl = 0;
		double userCountdbl = Double.parseDouble(userCount);
		double toaldbl = (double) totalCount;
		perdbl = userCountdbl / toaldbl;
		perdbl = perdbl * 100;
		DecimalFormat df = new DecimalFormat("0");
		String strPer = df.format(perdbl) + "%";

		return strPer;
	}
	
	/**
	 * 了解程度统计
	 * @author luangaojiong
	 *
	 */
	public ArrayList getKnowlist(Connection con, String objectId, String userId,
			String id)
	{
		if("".equals(objectId.trim()))
		{
			objectId="0";
		}
		ArrayList knowlist=new ArrayList();
		ArrayList knowtplst=new ArrayList();
		/**
		 * 得到了解程度所有列表
		 */
		ResultSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(con);
			String sql="select know_id,name from per_know where status=1";
			rs=dao.search(sql);
			while(rs.next())
			{
				StatisticBean sttb = new StatisticBean();
				sttb.setKnowId(PubFunc.NullToZero(rs.getString("know_id")));
				sttb.setKnowName(PubFunc.nullToStr(rs.getString("name")));
				knowtplst.add(sttb);
			}
			rs.close();
			/**
			 * 得到了解程度总数量
			 */
			String sql2 = "select count(*) as gcount from per_mainbody where object_id='"
				+ objectId + "' and know_id is not null";
			String knowTotal="0";
			rs=dao.search(sql2);
			if(rs.next())
			{
				knowTotal=PubFunc.NullToZero(rs.getString("gcount"));
			}
			if(rs!=null)
			{
				rs.close();
			}
			if("0".equals(knowTotal))
			{
				return knowlist;
			}
			else
			{
				/**
				 * 得到每一个了解程度数量
				 */
				StringBuffer sb2 = new StringBuffer();
				sb2.append("select count(whole_grade_id) as wgcount,know_id from per_mainbody where object_id='");
				sb2.append(objectId);
				sb2.append("' and know_id is not null group by know_id");
				Hashtable everyknowHb = new Hashtable();
				rs = dao.search(sb2.toString());
				while (rs.next()) {
					everyknowHb.put(rs.getString("know_id").trim(),	PubFunc.NullToZero(rs.getString("wgcount")));

				}
				if(rs!=null)rs.close();
				
				/**
				 * 得到了解程度list
				 */
				
				for(int i=0;i< knowtplst.size();i++)
				{
									
					StatisticBean stt1=(StatisticBean) knowtplst.get(i);
					StatisticBean sttb = new StatisticBean();
					String knowid = stt1.getKnowId();
					sttb.setKnowId(knowid);
					sttb.setKnowName(stt1.getKnowName());
					sttb.setKnowCount(getGradeScore(everyknowHb,knowid));
					sttb.setKnowPercent(getPercent(getGradeScore(everyknowHb,knowid),Integer.parseInt(knowTotal)));
					knowlist.add(sttb);
				}
				
			}
			
		}
		catch(Exception ex)
		{
			System.out.println("-------->StatisticGradeBean--> error");
			ex.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
		/**
		 * 
		 */
		
		return knowlist;
	}

}