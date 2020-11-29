package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:ShowScoreManualTrans.java</p>
 * <p>Description>:考核计划定义部门考核模板</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 16, 2011 14:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class ShowScoreManualTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
		ArrayList mainBodyList=new ArrayList();
		ArrayList sortList=new ArrayList();
		RowSet rowSet=null;
		RowSet rs=null;
		try
		{
			String plan_id = (String)this.getFormHM().get("planid");
			String object_id = (String)this.getFormHM().get("object_id");
			String object_type = (String)this.getFormHM().get("plan_objectType");
			String model = (String)this.getFormHM().get("model");
			String body_id = (String)this.getFormHM().get("body_id");
			String opt = (String)this.getFormHM().get("opt");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String sort_id = (String)map.get("sort_id");
			String sort = "";
			sortList.add(new CommonData("1","任务指标顺序"));
			sortList.add(new CommonData("2","考核主体顺序"));
			
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),plan_id,object_id,this.getUserView(),model,body_id,opt);
			ArrayList list=bo.getP04List();      // 取得 工作任务信息表中的记录
			ArrayList alist=bo.getLower_GradeList(Integer.parseInt(body_id),object_id,Integer.parseInt(plan_id)); // 取得登录人和下级考核主体的信息列表
						
			if(list!=null && list.size()>0)
			{
				LazyDynaBean abean=null;
				HashMap keyMap=new HashMap();
				StringBuffer buf = new StringBuffer();	
				for(int i=0;i<list.size();i++)
				{
					abean=(LazyDynaBean)list.get(i);
					String p0400=(String)abean.get("p0400");
					if(keyMap.get(p0400)==null)
					{											
						buf.append("," + p0400);
						keyMap.put(p0400,"");
					}else
						keyMap.put(p0400,"");
				}
				
				LazyDynaBean bean=null;	
				sort = (String)this.getFormHM().get("sort");
				StringBuffer sql=new StringBuffer("");				
				if("1".equalsIgnoreCase(sort_id))
				{
					sort="1";
					sql.append("select mainbody_id,p0400,score from per_target_evaluation where plan_id="+plan_id+" and object_id='"+object_id+"' and p0400 in(" + buf.substring(1) + ") order by p0400,mainbody_id desc ");			
				}
				else
				{
					if("1".equalsIgnoreCase(sort))
						sql.append("select mainbody_id,p0400,score from per_target_evaluation where plan_id="+plan_id+" and object_id='"+object_id+"' and p0400 in(" + buf.substring(1) + ") order by p0400,mainbody_id desc ");
					else
					{
						sql.append("select pte.mainbody_id,pte.p0400,pte.score ");	
						sql.append(" from per_target_evaluation pte,per_mainbody pm,per_mainbodyset pms ");
						sql.append(" where pm.body_id=pms.body_id and pm.plan_id=pte.plan_id and pm.mainbody_id=pte.mainbody_id and pm.object_id=pte.object_id ");
						sql.append(" and pte.plan_id="+plan_id+" and pte.object_id='"+object_id+"' and pte.p0400 in(" + buf.substring(1) + ") ");
						
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							sql.append(" order by pms.level_o desc,pte.mainbody_id desc,pte.p0400  ");
						else
							sql.append(" order by pms.level desc,pte.mainbody_id desc,pte.p0400  ");							
					}
				}
					
				rowSet=dao.search(sql.toString());								
				while(rowSet.next())
				{
					bean=new LazyDynaBean();
					String mainbody_id=rowSet.getString("mainbody_id");
					String a0101="";
					
					String sign = "false";
					for(int i=0;i<alist.size();i++)
					{
						LazyDynaBean dbean=(LazyDynaBean)alist.get(i);
						String a0100=(String)dbean.get("a0100");
						if(a0100.equalsIgnoreCase(mainbody_id))
						{
							sign = "true";	
							a0101=(String)dbean.get("a0101");
							break;
						}
					}
					if("false".equalsIgnoreCase(sign))
						continue;
					
					Float score=new Float(rowSet.getFloat("score"));
					String p0400=rowSet.getString("p0400");
					
					String strSql=("select p0401,p0407,item_id from p04 where p0400=" + p0400 );			
					rs=dao.search(strSql);
					String p0407="";
					String item_id="";
					while(rs.next())
					{
						p0407=rs.getString("p0407");
						item_id=rs.getString("item_id");
					}
					
					String str=("select itemdesc from per_template_item where item_id=" + item_id );			
					rs=dao.search(str);
					String itemdesc="";
					while(rs.next())					
						itemdesc=rs.getString("itemdesc");					
					
/*					
 					String strp="";
//					if(object_type.equalsIgnoreCase("2"))					
						strp=("select a0101 from usra01 where a0100='" + mainbody_id +"'" );	
//					else
//						strp=("select codeitemdesc from organization where codeitemid='" + mainbody_id +"'" );	
					rs=dao.search(strp);
					String a0101="";
					while(rs.next())
					{
//						if(object_type.equalsIgnoreCase("2"))
							a0101=rs.getString("a0101");
//						else
//							a0101=rs.getString("codeitemdesc");
					}
*/										
					bean.set("itemdesc",itemdesc);
					bean.set("p0407",p0407);
					bean.set("a0101",a0101);
					bean.set("score",score);
					mainBodyList.add(bean);
				}				
			}
			
			String strObject="";
			if("2".equalsIgnoreCase(object_type))
				strObject=("select a0101 from usra01 where a0100='" + object_id + "'" );
			else
				strObject=("select codeitemdesc from organization where codeitemid='" + object_id + "'" );
									
			rs=dao.search(strObject);
			String a0101="";
			while(rs.next())					
			{
				if("2".equalsIgnoreCase(object_type))
					a0101=rs.getString("a0101");
				else
					a0101=rs.getString("codeitemdesc");
			}
			
			this.getFormHM().put("objectName", a0101);	
			this.getFormHM().put("sort",sort);
			this.getFormHM().put("sortList", sortList);		
			this.getFormHM().put("mainBodyList", mainBodyList);			
			
			if(rowSet!=null)
				rowSet.close();
			if(rs!=null)
				rs.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
