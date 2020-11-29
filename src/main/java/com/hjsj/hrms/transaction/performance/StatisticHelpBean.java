/*
 * 创建日期 2005-6-29
 *
 */
package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;


/**
 * @author luangaojiong 成绩统计帮助类
 */
public class StatisticHelpBean {

	void test() {

	}
	/**
	 * 
	 * 得到所有项目id与名称Hashtable
	 * 
	 */
	HashMap getAllItem(Connection con,String tempNum) {
		HashMap ht = new HashMap();
		ContentDAO dao = new ContentDAO(con);
		ResultSet rs=null;
		try 
		{
			rs = dao.search("select item_id,itemdesc from per_template_item where template_id='"+tempNum+"'");
			while (rs.next()) 
				ht.put(rs.getString("item_id"), rs.getString("itemdesc"));
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeResource(rs);
		}
		return ht;		
	}
	/**
	 * 得到要素ArrayList
	 * @param ht
	 * @param con
	 * @return
	 */
	ArrayList getAllPoint(HashMap ht,Connection con,String tempNum)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(con);
		ResultSet rs=null;
		try
		{
			String sql="select per_point.point_id,per_point.pointname,per_template_point.item_id from per_template_point,per_point where per_point.point_id=per_template_point.point_id";
			sql=sql+" and per_template_point.item_id in (select item_id from per_template_item where template_id='"+tempNum+"')";
			String temp="";
			rs=dao.search(sql);
			while(rs.next())
			{
				StatisticBean sb=new StatisticBean();
				sb.setPointId(rs.getString("point_id"));
				sb.setPointName(Sql_switcher.readMemo(rs,"pointname"));
				temp=rs.getString("item_id");
				sb.setItemId(temp);
				if(ht.get(temp)!=null)
					sb.setItemName(ht.get(temp).toString());
				list.add(sb);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeResource(rs);
		}
		return list;
	}
	
	
	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public String round(String v, int scale) {

		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(v);
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	
	/**
	 * 得到测试级别分类
	 * @author luangaojiong
	 *
	 */
	ArrayList getMainBody(Connection con,String userId,String planId)
	{
		ArrayList list=new ArrayList();
		StringBuffer sb=new StringBuffer();
		sb.append("select count(per_mainbody.body_id) as userCount,per_mainbody.body_id,per_mainbodyset.name,pobs.score from per_mainbody "+Sql_switcher.left_join("per_mainbody","per_mainbodyset","per_mainbody.body_id","per_mainbodyset.body_id"));
		sb.append("  left join (select * from per_objectbody_score where plan_id="+planId+" and object_id='"+userId+"') pobs on per_mainbody.body_id=pobs.body_id ");
		sb.append( " where per_mainbody.plan_id="+planId+" and per_mainbody.object_id='" );
		sb.append(userId);
		sb.append("' group by per_mainbody.body_id,per_mainbodyset.name,pobs.score ");
		ContentDAO dao = new ContentDAO(con);
		ResultSet rs=null;
		try
		{
			LoadXml loadxml=new LoadXml(con,planId);
			Hashtable htxml=new Hashtable();		
			htxml=loadxml.getDegreeWhole();
			String KeepDecimal=(String)htxml.get("KeepDecimal");
			
			rs=dao.search(sb.toString());
			while(rs.next())
			{
				StatisticBean sttb=new StatisticBean();
				if(rs.getString("score")!=null&&rs.getString("score").trim().length()>0)
				{
					String score=rs.getString("score");
					
					score=round(score,Integer.parseInt(KeepDecimal));
					sttb.setBodySetScore(score);
				}
				else
					sttb.setBodySetScore(rs.getString("score"));
				sttb.setUserCount(rs.getString("userCount"));
				sttb.setBodyId(rs.getString("body_id"));
				sttb.setBodyName(rs.getString("name"));
				list.add(sttb);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeResource(rs);
		}
		return list;
	}
	
	/**
	 * 得到上级测评数量
	 * @author luangaojiong
	 */
	public int getLeaderExamCount(ArrayList list)
	{
		int count=0;
		
		if(list.size()<0)
		{
			return 0;
		}
		else
		{
			for(int i=0;i<list.size();i++)
			{
				StatisticBean sttb=(StatisticBean)list.get(i);
				count=count+Integer.parseInt(sttb.getUserCount());
			}
		}
		
		return count;
	}
	
	
	
	/**
	 * 处理上级测评数量的空项并且只显示该计划下的主体类别
	 * @author dengcan
	 * createTime  2007.3.20
	 *
	 */
	public ArrayList doNullLeaderExamCount(ArrayList list,Connection con,String plan_id)
	{
		ArrayList alllist=new ArrayList();
		HashMap ht=new HashMap();
		ArrayList lst=new ArrayList();
		ContentDAO dao = new ContentDAO(con);
		ResultSet rs=null;
		/**
		 * 得到所有的测评级别
		 */
		try
		{
			String sql="select pmb.body_id,pmb.name from per_plan_body ppb ,per_mainbodyset pmb where ppb.body_id=pmb.body_id and pmb.status=1  and  plan_id="+plan_id;
			rs=dao.search(sql);
			while(rs.next())
			{
				StatisticBean sttb=new StatisticBean();
				sttb.setBodyId(rs.getString("body_id"));
				sttb.setBodyName(rs.getString("name"));
				sttb.setUserCount("0");
				alllist.add(sttb);
			}
			/**
			 * 得到当前HashMap
			 */
			for(int i=0;i<list.size();i++)
			{
				StatisticBean sttb=(StatisticBean)list.get(i);
				ht.put(sttb.getBodyId(),sttb.getBodyName());
			}
			/**
			 * 补充空缺的测评级别对象
			 */
			for(int i=0;i<alllist.size();i++)
			{
				StatisticBean sttb=(StatisticBean)alllist.get(i);
				if(ht.containsKey(sttb.getBodyId()))
				{					
					sttb=getMainBodySet(list,sttb.getBodyId());
				}
				lst.add(sttb);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeResource(rs);
		}			
		return lst;
		
	}
	
	
	
	
	/**
	 * 处理上级测评数量的空项
	 * @author luangaojiong
	 *
	 */
	public ArrayList doNullLeaderExamCount(ArrayList list,Connection con)
	{
		ArrayList alllist=new ArrayList();
		HashMap ht=new HashMap();
		ArrayList lst=new ArrayList();
		ContentDAO dao = new ContentDAO(con);
		ResultSet rs=null;
		/**
		 * 得到所有的测评级别
		 */
		try
		{
			rs=dao.search("select body_id,name from per_mainbodyset where status=1");
			while(rs.next())
			{
				StatisticBean sttb=new StatisticBean();
				sttb.setBodyId(rs.getString("body_id"));
				sttb.setBodyName(rs.getString("name"));
				sttb.setUserCount("0");
				alllist.add(sttb);
			}
			/**
			 * 得到当前HashMap
			 */
			for(int i=0;i<list.size();i++)
			{
				StatisticBean sttb=(StatisticBean)list.get(i);
				ht.put(sttb.getBodyId(),sttb.getBodyName());
			}
			/**
			 * 补充空缺的测评级别对象
			 */
			for(int i=0;i<alllist.size();i++)
			{
				StatisticBean sttb=(StatisticBean)alllist.get(i);
				if(ht.containsKey(sttb.getBodyId()))
				{					
					sttb=getMainBodySet(list,sttb.getBodyId());
				}
				lst.add(sttb);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeResource(rs);
		}
		return lst;
		
	}
	/**
	 * 得到查找的测评对象
	 * @param list
	 * @param bodyId
	 * @return
	 */
	public StatisticBean getMainBodySet(ArrayList list,String bodyId)
	{
		StatisticBean sttb=new StatisticBean();
		for(int i=0;i<list.size();i++)
		{
			 sttb=(StatisticBean)list.get(i);
			if(sttb.getBodyId().equals(bodyId))
			{
				break;
			}
		}
		return sttb;
	}
}