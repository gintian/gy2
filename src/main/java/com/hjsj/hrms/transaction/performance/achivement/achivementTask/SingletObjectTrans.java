package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.AchivementTaskBo;
import com.hjsj.hrms.businessobject.performance.achivement.Permission;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:业绩任务书单一对象查询</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 3, 2010</p> 
 *@author JinChunhai
 *@version 5.0
 */

public class SingletObjectTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");		
		String object_id = (String) hm.get("object_id");
		String target_id = (String) hm.get("target_id");
		String nbase = (String) hm.get("nbase");
		
		AchivementTaskBo bo=new AchivementTaskBo(this.getFrameconn(),this.userView);		
		ArrayList pointList=bo.getTargetPointList(target_id);
		
		RowSet rowSet = null;
		ArrayList objectCycleList=new ArrayList();
		String cycle_str="";
		try {			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql="select cycle,theyear,object_type from per_target_list where target_id="+target_id;			
			rowSet=dao.search(sql);
			int cycle=0;
			String theyear="";
			String object_type="";
			while(rowSet.next())
			{
				cycle=rowSet.getInt("cycle");
				theyear=rowSet.getString("theyear");
				object_type=rowSet.getString("object_type");
			}			
			String strSql=("select *  from per_target_mx where object_id='"+object_id+"' and target_id="+target_id+" and nbase='"+nbase+"'");
			rowSet=dao.search(strSql);
			
			
			DecimalFormat myformat1 = new DecimalFormat("########.###");
			LazyDynaBean abean=null;
			int n=0;
			Permission p=new Permission(this.frameconn,this.userView);
			while(rowSet.next())
			{
				cycle_str=bo.getCycle_str(rowSet.getString("kh_cyle"),cycle,theyear);
				String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
				String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
				abean=new LazyDynaBean();
				abean.set("cycle_str",cycle_str);
				abean.set("object_id",rowSet.getString("object_id"));
				abean.set("kh_cyle",rowSet.getString("kh_cyle"));
				for(int i=0;i<pointList.size();i++)
				{
					CommonData d=(CommonData)pointList.get(i);
					boolean right = true;
					if(!"2".equals(object_type))  // 非2 团队
						right = p.getPrivPoint("", object_id, d.getDataValue());
					else if("2".equals(object_type))  // 2 人员
						right = p.getPrivPoint(b0110, e0122, d.getDataValue());
					if(right==true)
					{
						if(rowSet.getString("T_"+d.getDataValue())!=null&&rowSet.getFloat("T_"+d.getDataValue())==0)
							abean.set(d.getDataValue(),"0");
						else
							abean.set(d.getDataValue(),rowSet.getString("T_"+d.getDataValue())!=null&&rowSet.getFloat("T_"+d.getDataValue())!=0?myformat1.format(rowSet.getDouble("T_"+d.getDataValue())):"");					
					}else{
						abean.set(d.getDataValue(),"no");
					}
															
				}						
				abean.set("index",String.valueOf(n));
				objectCycleList.add(abean);
				n++;
			}		
			if(rowSet!=null)
				rowSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}						
		this.getFormHM().put("objectCycleList", objectCycleList);
		this.getFormHM().put("objectPointList", pointList);
	}	
}
