package com.hjsj.hrms.businessobject.performance.showkhresult;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class DirectionAnalyseBo {
	Connection conn=null;
	
	public DirectionAnalyseBo(Connection con)
	{
		this.conn=con;
	}
	
	
	
	public String getNum(int num)
	{
		String temp="";
		try
		{
			switch (num)
			{
				case 1: temp="一";break;
				case 2: temp="二";break;
				case 3: temp="三";break;
				case 4: temp="四";break;
				case 5: temp="五";break;
				case 6: temp="六";break;
				case 7: temp="七";break;
				case 8: temp="八";break;
				case 9: temp="九";break;
				case 0: temp="零";break;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return temp;
	}
	/**
	 * 取得模版层级
	 * @param template_id
	 * @return
	 */
	public ArrayList getItemLevelList(String template_id)
	{
		
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer itemids=new StringBuffer("");
			RowSet rowSet=dao.search("select * from per_template_item where template_id='"+template_id+"' and parent_id is null ");
			while(rowSet.next())
			{
				itemids.append(","+rowSet.getString("item_id"));
			}
			if(itemids.length()>0)
				list.add(new CommonData("1",getNum(1)+"级指标"));
			int num=2;
			while(true)
			{
				rowSet=dao.search("select * from per_template_item where template_id='"+template_id+"' and parent_id in ("+itemids.substring(1)+") ");
				itemids.setLength(0);
				while(rowSet.next())
				{
					itemids.append(","+rowSet.getString("item_id"));
				}
				
				if(itemids.length()>0)
					list.add(new CommonData(String.valueOf(num),getNum(num)+"级指标"));
				else 
					break;
				num++;
			}
			list.add(new CommonData(String.valueOf(num),getNum(num)+"级指标"));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	
	
	/**
	 * 从list中得到第一个值
	 * @param list
	 * @return
	 */
	public String getFirstValueOfList(ArrayList list)
	{
		String value="";
		if(list.size()>0)
		{
			CommonData data=(CommonData)list.get(0);
			value=data.getDataValue();
		}
		return value;
	}
	
	
	/**
	 * 取得归档的考核模版列表
	 * @return
	 */
	public ArrayList  getTemplateList(String objectid)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("select distinct pt.template_id,pt.name from per_plan pp,per_history_result phr,per_template pt ");
			sql.append(" where pp.plan_id=phr.plan_id and pp.template_id=pt.template_id and phr.object_id='"+objectid+"' ");
			RowSet rowSet=dao.search(sql.toString());
			while(rowSet.next())
			{
				String id=rowSet.getString(1);
				String name=rowSet.getString(2);
				CommonData data=new CommonData(id,name);
				list.add(data);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	
	
	
	public String getSql(String object_id,String template_id,String values,String showType,String totalscoreName)
	{
		String[] pointids=values.split(",");
		StringBuffer whl=new StringBuffer("");
		if("totalscore".equals(pointids[0]))  //如果包含总分
		{
			whl.append(" or phr.point_id='"+totalscoreName+"'");
		}
		else
		{
			
			String[] temp=pointids[0].split("`");
			whl.append(" or phr.point_id='"+temp[1]+"'");
			
		}
		for(int i=1;i<pointids.length;i++)
		{
			if(pointids[i].trim().length()==0)
				continue;
			String[] temp=pointids[i].split("`");
			whl.append(" or phr.point_id='"+temp[1]+"'");
		}
		StringBuffer sql=new StringBuffer("select phr.score,phr.point_id,");
		if("1".equals(showType))
			sql.append(" year(archive_date) a_date");
		else if("2".equals(showType))
			sql.append(Sql_switcher.numberToChar("year(archive_date)")+Sql_switcher.concat()+"'.'"+Sql_switcher.concat()+Sql_switcher.numberToChar("month(archive_date)")+" a_date");
		sql.append(",phr.status from per_history_result phr,per_plan pp where phr.plan_id=pp.plan_id and pp.template_id='"+template_id+"' and object_id='"+object_id+"'");
		sql.append("  and ( "+whl.substring(3)+" ) ");
		sql.append(" order by phr.point_id,a_date ");
		
		return sql.toString();
	}
	
	/**
	 * 取得指标的名称
	 * @param values
	 * @return
	 */
	public HashMap getPointNameMap(String values,String totalscoreName)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String[] pointids=values.split(",");
			StringBuffer itemStr=new StringBuffer("");
			StringBuffer pointStr=new StringBuffer("");
			
			
			if("totalscore".equals(pointids[0]))  //如果包含总分
			{
				map.put(totalscoreName,"总分");
			}
			else
			{
				
				String[] temp=pointids[0].split("`");
				if("1".equals(temp[0]))  //项目
				{
					itemStr.append(" or item_id='"+temp[1]+"'");
				}	
				else if("2".equals(temp[0])) //指标
				{
					pointStr.append(" or point_id='"+temp[1]+"'");
				}	
			}
			for(int i=1;i<pointids.length;i++)
			{
				if(pointids[i].trim().length()==0)
					continue;
				String[] temp=pointids[i].split("`");
				if("1".equals(temp[0]))  //项目
				{
					itemStr.append(" or item_id='"+temp[1]+"'");
				}	
				else if("2".equals(temp[0])) //指标
				{
					pointStr.append(" or point_id='"+temp[1]+"'");
				}	
			}
			if(pointStr.length()>0)
			{
				RowSet rowset=dao.search("select point_id,pointname from per_point where "+pointStr.substring(3));
				while(rowset.next())
				{
					map.put(rowset.getString("point_id"),rowset.getString("pointname"));
				}
			}
			if(itemStr.length()>0)
			{
				RowSet rowset=dao.search("select item_id,itemdesc from per_template_item where "+itemStr.substring(3));
				while(rowset.next())
				{
					map.put(rowset.getString("item_id"),rowset.getString("itemdesc"));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	/**
	 * 生成趋势分析数据
	 * @param object_id     考核对象
	 * @param template_id   考核模版
	 * @param values        分析指标
	 * @param showType      展现形式  1:按年  2：按月
	 * @return
	 */
	public HashMap getDataMap(String object_id,String template_id,String values,String showType)
	{
		HashMap datamap=new HashMap();
		try
		{
			String totalscoreName="aaa";   // 总分的point_id
			ContentDAO dao = new ContentDAO(this.conn);
			String sql=getSql(object_id,template_id,values,showType,totalscoreName);
			HashMap pointNameMap=getPointNameMap(values,totalscoreName);
			RowSet rowset=dao.search(sql);
			String point_id="";
			ArrayList list=new ArrayList();
			int i=0;
			while(rowset.next())
			{
				
				String a_point_id=rowset.getString("point_id");
				if(i==0)
					point_id=a_point_id;
				
				String a_date=rowset.getString("a_date");
				String score=rowset.getString("score");
				if(!point_id.equals(a_point_id))
				{
					datamap.put((String)pointNameMap.get(point_id),list);
					point_id=a_point_id;
					list=new ArrayList();
				}
				if("1".equals(showType))
					list.add(new CommonData(score,a_date+"年"));
				else if("2".equals(showType))
					list.add(new CommonData(score,a_date+"月"));
				
				i++;
			}
			datamap.put((String)pointNameMap.get(point_id),list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return datamap;
	}
	
	
	
	public String  getPointIDs(String templateid,String itemLevelID,String isTotalScore,ArrayList itemLevelList)
	{
		StringBuffer whl=new StringBuffer("");
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			if(Integer.parseInt(itemLevelID)!=itemLevelList.size())
			{
				
				StringBuffer itemids=new StringBuffer("");
				RowSet rowSet=dao.search("select * from per_template_item where template_id='"+templateid+"' and parent_id is null ");
				while(rowSet.next())
				{
					itemids.append(","+rowSet.getString("item_id"));
					if("1".equals(itemLevelID))
						whl.append(",'"+rowSet.getString("item_id")+"'");
				}
				int num=2;
				
				while(true&&whl.length()==0)
				{
						if(num==itemLevelList.size())
							break;
						else
						{
							rowSet=dao.search("select * from per_template_item where template_id='"+templateid+"' and parent_id in ("+itemids.substring(1)+") ");
							itemids.setLength(0);
							while(rowSet.next())
							{
								if(Integer.parseInt(itemLevelID)==num)
									whl.append(",'"+rowSet.getString("item_id")+"'");
								itemids.append(","+rowSet.getString("item_id"));
							}
							num++;
						}
				}
				if("1".equals(isTotalScore))  //包括总分
				{
					whl.append(",'total_value'");
				}
				
			}
			else
			{
				String sql="select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status from per_template_item pi,per_template_point pp,per_point po"
						+" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='"+templateid+"'  order by pp.seq ";
				RowSet rowSet=dao.search(sql);
				while(rowSet.next())
				{
					whl.append(",'"+rowSet.getString("point_id")+"'");
				}
				if("1".equals(isTotalScore))  //包括总分
				{
					whl.append(",'total_value'");
				}

			}
			

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return whl.substring(1);
	}
	
	
	/**
	 * 
	 * @param templateID
	 * @param itemLevelID
	 * @param isTotalScore
	 * @param object_id
	 * @return
	 */
	public HashMap getDataMap2(String templateID,String itemLevelID,String isTotalScore,String object_id,ArrayList itemLevelList)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String whl=getPointIDs(templateID,itemLevelID,isTotalScore,itemLevelList);
			String sql="select phr.*,pp.name from per_history_result phr,per_plan pp "
					+" where phr.plan_id=pp.plan_id and pp.template_id='"+templateID+"' and object_id='"+object_id+"'  and phr.point_id in ("+whl+") order by  phr.plan_id,phr.point_id";
		
			HashMap pointNameMap=getPointNameMap(whl,itemLevelID,itemLevelList,isTotalScore);
			ArrayList list=new ArrayList();
			String    planID="";
			String    planDesc="";
			RowSet rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String a_planID=rowSet.getString("plan_id");
				String a_planName=rowSet.getString("name");
				String pointid=rowSet.getString("point_id");
				String score=rowSet.getString("score");
			
				if(!planID.equals(a_planID)&&!"".equals(planID))
				{
					map.put(planDesc,list);
					list=new ArrayList();	
				}
				list.add(new CommonData(score,(String)pointNameMap.get(pointid.toLowerCase())));
				planID=a_planID;
				planDesc=a_planName;
				
			}
			map.put(planDesc,list);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	public HashMap getPointNameMap(String whl,String itemLevelID,ArrayList itemLevelList,String isTotalScore)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			if("1".equals(isTotalScore))
				whl=whl.replaceAll(",'total_value'","");
			
			if(Integer.parseInt(itemLevelID)==itemLevelList.size())
			{
				RowSet rowSet=dao.search("select point_id,pointname from per_point where point_id in ("+whl+")");
				while(rowSet.next())
				{
					map.put(rowSet.getString("point_id").toLowerCase(),rowSet.getString("pointname"));
				}
			}
			else
			{
				RowSet rowSet=dao.search("select * from per_template_item where item_id in  ("+whl+")");
				while(rowSet.next())
				{
					map.put(rowSet.getString("item_id").toLowerCase(),rowSet.getString("itemdesc"));
				}
			}
			if("1".equals(isTotalScore))
				map.put("total_value", "总分");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	
	
	
}
