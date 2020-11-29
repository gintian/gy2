package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.achivement.AchivementTaskBo;
import com.hjsj.hrms.businessobject.performance.achivement.Permission;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
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
 * <p>Title:ShowBigNumAchivementTaskTrans.java</p>
 * <p>Description>:业绩任务书</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 17, 2010 14:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class ShowBigNumAchivementTaskTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");			
		String hjsoft = (String) hm.get("hjsoft");
		hm.remove("hjsoft");
		String target_id = "";
		String object_types = "";
		String orgCode = "";
		String nbase = "";		
		
		target_id = (String) hm.get("target_id");
		object_types = (String)this.getFormHM().get("obj_type");
		orgCode = (String) hm.get("a_code");
		hm.remove("a_code");
		String paramd = (String) hm.get("paramd");
		hm.remove("paramd");
		
		if(orgCode!=null && orgCode.length()>0)
		{
			// 过虑机构编码前面的字母标识	
			String sign=orgCode.substring(0, 2);
			orgCode = orgCode.substring(2, orgCode.length());				
			if("2".equals(object_types))
			{
				if(!"ps".equalsIgnoreCase(sign))
					return;
			}else{
				if(!"UM".equalsIgnoreCase(sign))
					return;
			}			
		}		
		if(paramd!=null && paramd.length()>0)		
			if("1".equals(paramd))
				orgCode=(String)this.getFormHM().get("orgCode");
		
		if(hjsoft!=null && hjsoft.length()>0)
		{
			if("hjsj".equalsIgnoreCase(hjsoft))
			{
				target_id = (String) hm.get("target_id");
				object_types = (String) hm.get("object_type");
				orgCode = (String) hm.get("object_id");
				nbase = (String) hm.get("nbase");
			}
		}

		AchivementTaskBo bo=new AchivementTaskBo(this.getFrameconn(),this.userView);	
		ArrayList selectedPointList=bo.getTargetPointList(target_id);
		ArrayList pointList=bo.getBigTargetPointList(target_id,orgCode,object_types);
		ArrayList nullpointList=new ArrayList();

		String queryDesc=(String)hm.get("b_query");
		String cycle="-1";
		if(queryDesc!=null && queryDesc.length()>0)
		{
			if("query1".equals(queryDesc))
				cycle=(String)this.getFormHM().get("cycle");
		}
		RecordVo perTargetVo=bo.getPerTargetVo(target_id);
		ArrayList cycleList=bo.getCycleList(perTargetVo.getInt("cycle"));	
		String sql_whl=(String)this.getFormHM().get("sql_whl");
		sql_whl = PubFunc.keyWord_reback(sql_whl);
		
		RowSet rowSet=null;
		ArrayList objectCycleList=new ArrayList();
		StringBuffer strSql=new StringBuffer();
		String cycle_str="";
		String sqlStr="";
		int have=0;
		try {			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			String sql="select cycle,theyear,object_type from per_target_list where target_id="+target_id;			
			rowSet=dao.search(sql);
			int cycle1=0;
			String theyear="";
			String object_type="";
			while(rowSet.next())
			{
				cycle1=rowSet.getInt("cycle");
				theyear=rowSet.getString("theyear");
				object_type=rowSet.getString("object_type");
			}	
			
			// 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
			String operOrg = this.userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if("2".equals(object_type))
			{
				StringBuffer buf = new StringBuffer();				
				if (operOrg!=null && operOrg.length() > 3)
				{					 
					StringBuffer tempSql = new StringBuffer("");
					String[] temp = operOrg.split("`");
					for (int i = 0; i < temp.length; i++)
					{
						if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
						    tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
						else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						    tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");
					}
					buf.append(" select usra01.A0100 from usra01 where  ( " + tempSql.substring(3) + " ) ");
					 
				}
				else if((!this.userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
				{
					String priStrSql = InfoUtils.getWhereINSql(this.userView,"Usr");
					if(priStrSql.length()>0)
					{
						buf.append("select usra01.A0100 ");
						buf.append(priStrSql);
					}
				}
				if(buf.length()>0)
				{
					strSql.append(" and per_target_mx.object_Id in ("+buf.toString()+") ");
				}
			}
			else
			{
				 if (operOrg.length() > 3)
				 {
					StringBuffer tempSql = new StringBuffer("");
					String[] temp = operOrg.split("`");
					for (int i = 0; i < temp.length; i++)
					{
					    tempSql.append(" or per_target_mx.object_Id like '" + temp[i].substring(2) + "%'");
					}
					strSql.append(" and ( " + tempSql.substring(3) + " ) ");
				 }
				 else if(!this.userView.isAdmin())
				 {
					String codeid=userView.getManagePrivCode();
					String codevalue=userView.getManagePrivCodeValue();
					String a_code=codeid+codevalue;
					
					if(a_code.trim().length()<=0)
					{
						strSql.append(" and 1=2 ");
					}
					else if(!("UN".equals(a_code)))
					{
						strSql.append(" and per_target_mx.object_Id like '"+codevalue+"%' "); 							
					}
				 }				
			}						
									
			if("2".equals(object_types))
			{				
				if(orgCode!=null && orgCode.length()>0)										
					sqlStr="select a0101 from per_target_mx where a0100='"+orgCode+"' and target_id="+target_id;
				else	
					sqlStr=("select DISTINCT a0101 from per_target_mx where A0000=(select min(A0000) A0000 from per_target_mx where target_id="+target_id+" "+strSql.toString()+") and target_id="+target_id);								
			}else{
				if(orgCode!=null && orgCode.length()>0)				
					sqlStr="select a0101 from per_target_mx where object_id='"+orgCode+"' and target_id="+target_id;
				else
					sqlStr=("select DISTINCT a0101 from per_target_mx where A0000=(select min(A0000) A0000 from per_target_mx where target_id="+target_id+" "+strSql.toString()+") and target_id="+target_id);
			}
			rowSet=dao.search(sqlStr);
			String codeitemdesc="";
			while(rowSet.next())
			{				
				codeitemdesc=rowSet.getString("a0101");				
			}
			this.getFormHM().put("codeitemdesc",codeitemdesc);						
			
			StringBuffer sql0=new StringBuffer("select per_target_mx.* from per_target_mx ");
			if("2".equals(object_type))
				sql0.append(",UsrA01 where per_target_mx.object_Id=UsrA01.a0100   ");
			else 
				sql0.append(",organization where per_target_mx.object_Id=organization.codeitemid   ");
			
			sql0.append(" and  per_target_mx.target_id="+target_id);
			if(orgCode!=null && orgCode.length()>0)
				sql0.append(" and per_target_mx.object_id='"+orgCode+"' ");
			else
				sql0.append(" and per_target_mx.A0000=(select min(A0000) A0000 from per_target_mx where target_id="+target_id+" "+strSql.toString()+") ");
			if(cycle!=null&&!"-1".equals(cycle))
				sql0.append(" and per_target_mx.kh_cyle='"+cycle+"' ");
			if(sql_whl!=null&&sql_whl.length()>0)
				sql0.append(" and ( "+sql_whl+" ) ");
			
			sql0.append(strSql.toString());
			sql0.append(" order by per_target_mx.kh_cyle");
			if("2".equals(object_type))
				sql0.append(",Usra01.a0000");
			else
				sql0.append(",organization.a0000");
			rowSet=dao.search(sql0.toString());
			
/*			StringBuffer strSql=new StringBuffer("select *  from per_target_mx where 1=1 ");			
			if(cycle!=null&&!cycle.equals("-1"))
				strSql.append(" and kh_cyle='"+cycle+"'");	
			if(sql_whl!=null&&sql_whl.length()>0)
				strSql.append(" and ( "+sql_whl+" )");
			if(orgCode!=null && orgCode.length()>0)
				strSql.append(" and object_id='"+orgCode+"' and target_id="+target_id);
			else if(object_types.equals("2"))  // 2 人员
				strSql.append(" and A0000=(select min(A0000) A0000 from per_target_mx where target_id="+target_id+") and target_id="+target_id);
			else 
				strSql.append(" and A0000=(select min(A0000) A0000 from per_target_mx where target_id="+target_id+") and target_id="+target_id);
			rowSet=dao.search(strSql.toString());
*/			
			DecimalFormat myformat1 = new DecimalFormat("########.###");
			LazyDynaBean abean=null;
			int n=0;			
			Permission p=new Permission(this.frameconn,this.userView);
			while(rowSet.next())
			{
				String object_id=rowSet.getString("object_id");
				cycle_str=bo.getCycle_str(rowSet.getString("kh_cyle"),cycle1,theyear);
				String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
				String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
				abean=new LazyDynaBean();
				abean.set("cycle_str",cycle_str);
				abean.set("object_id",rowSet.getString("object_id"));
				abean.set("kh_cyle",rowSet.getString("kh_cyle"));
				int m=0;
				for(int i=0;i<pointList.size();i++)
				{
					CommonData d=(CommonData)pointList.get(i);					
					if(rowSet.getString("T_"+d.getDataValue())!=null&&rowSet.getFloat("T_"+d.getDataValue())==0)
						abean.set(d.getDataValue(),"0");
					else
						abean.set(d.getDataValue(),rowSet.getString("T_"+d.getDataValue())!=null&&rowSet.getFloat("T_"+d.getDataValue())!=0?myformat1.format(rowSet.getDouble("T_"+d.getDataValue())):"");					
					m++;
				}
				this.getFormHM().put("zbnumber",new Integer(m));
				abean.set("index",String.valueOf(n));
				objectCycleList.add(abean);
				n++;
				have=1;
			}			
			
			if(rowSet!=null)
				rowSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("orgCode",orgCode);
		this.getFormHM().put("cycle",cycle);
		this.getFormHM().put("target_id", target_id);
		this.getFormHM().put("object_type", object_types);
		this.getFormHM().put("perTargetVo",perTargetVo);
		this.getFormHM().put("objectCycleList", objectCycleList);
		this.getFormHM().put("selectedPointList",selectedPointList);
		this.getFormHM().put("cycleList",cycleList);
		if(have==1)
		{			
			this.getFormHM().put("objectPointList", pointList);
			
		}else
		{						
			this.getFormHM().put("objectPointList", nullpointList);			
			this.getFormHM().put("zbnumber",new Integer(have));
		}
	}
}
