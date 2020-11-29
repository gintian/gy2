package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 条件选择考核对象
 * 
 * @author: JinChunhai
 */

public class InsertObjectTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");
			String  planid=(String)this.getFormHM().get("planid");
			String object_type=(String)this.getFormHM().get("object_type");
			PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn(),this.userView,planid);
			String plan_b0110 = (String)this.getFormHM().get("plan_b0110");
			ContentDAO dao = new ContentDAO(this.frameconn);
			if("handselect".equals(opt))
			{
				HashMap map = new HashMap();
				if(!"hjsj".equalsIgnoreCase(plan_b0110))//考核对象受计划所属机构的限制
				{
					StringBuffer buf = new StringBuffer();
					if("2".equals(object_type))
					{
						buf.append("select a0100 from usra01 where 1=1 ");
						if(AdminCode.getCode("UM",plan_b0110)!=null)
							buf.append(" and e0122 like '"+plan_b0110+"%' ");
						else if(AdminCode.getCode("UN",plan_b0110)!=null)
							buf.append(" and b0110 like '"+plan_b0110+"%' ");
					}else 
					{
						buf.append("select codeitemid from organization  where codesetid in ('UM','UN') ");
						buf.append(" and codeitemid like '"+plan_b0110+"%' ");
					}
					
					this.frowset=dao.search(buf.toString());
					while(this.frowset.next())
						map.put(this.frowset.getString(1), "");
				}
			
				String right_fields=(String)hm.get("right_fields");
				String[] temps=right_fields.replaceAll("／", "/").split("/");
				StringBuffer a0100s=new StringBuffer("");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].length()>0)
					{
						if(!"hjsj".equalsIgnoreCase(plan_b0110))
						{
							if(map.get(temps[i])!=null)
								a0100s.append(",'"+temps[i]+"'");
						}else
							a0100s.append(",'"+temps[i]+"'");
						
					}
				}
				if(a0100s.length()>0)
					bo.handInsertObjects(a0100s.substring(1), planid,object_type);
				
			}
			else if("conditionselect".equals(opt))//只有人员计划有条件选择
			{
				String delFlag=(String)hm.get("delFlag");//是否删除当前计划中已有的考核对象?
				hm.remove("delFlag");
				delFlag=delFlag==null?"0":delFlag;
				if("1".equals(delFlag))
				{
					ArrayList list = (ArrayList) this.getFormHM().get("perObjectDataList");
					ArrayList objList = new ArrayList();
					for(int i=0;i<list.size();i++)
					{
						ArrayList temp = new ArrayList();
					    LazyDynaBean abean = (LazyDynaBean)list.get(i);
					    String id = (String)abean.get("id");
					    temp.add(new Integer(id));
					    objList.add(temp);
					}
					String sql = "delete from per_object where id=?";
					dao.batchUpdate(sql, objList);
					//做删除考核对象的相关操作
					String delWhlSql = " not in (SELECT object_id FROM per_object WHERE plan_id = "+planid+")";
					dao.delete("delete from per_mainbody where plan_id="+planid+" AND object_id "+delWhlSql, new ArrayList());
					
					DbWizard dbWizard=new DbWizard(this.getFrameconn());
					if(dbWizard.isExistTable("per_pointpriv_"+planid,false))
						dao.delete("delete from per_pointpriv_"+planid+" where object_id  "+delWhlSql,new ArrayList());
					if(dbWizard.isExistTable("per_table_"+planid,false))
						dao.delete("delete from per_table_"+planid+" where object_id "+delWhlSql,new ArrayList());
					if(dbWizard.isExistTable("per_result_"+planid,false))
						dao.delete("delete from per_result_"+planid+" where object_id "+delWhlSql,new ArrayList());
					if(dbWizard.isExistTable("per_gather_score_"+planid,false))//业绩数据录入里用到的表
						dao.delete("delete from per_gather_score_"+planid+" where gather_id in (select gather_id  from per_gather_"+planid+" where object_id  "+delWhlSql+") ",new ArrayList());
					if(dbWizard.isExistTable("per_gather_"+planid,false))
						dao.delete("delete from per_gather_"+planid+" where  object_id "+delWhlSql,new ArrayList());
					 dao.delete("DELETE FROM per_interview WHERE plan_id = "+planid+" AND NOT (object_id IN (SELECT object_id FROM per_object WHERE plan_id = "+planid+"))" ,new ArrayList());
						
					dao.delete("DELETE FROM per_target_evaluation WHERE plan_id = "+planid+" AND object_id "+delWhlSql,new ArrayList());
					dao.delete("DELETE FROM per_article WHERE plan_id = "+planid+" AND A0100 "+delWhlSql,new ArrayList());
					if("2".equals(bo.getPlanVo(planid).getString("method")))//目标管理计划
					{
						 dao.delete("DELETE FROM P04 WHERE plan_id = "+planid+" AND A0100 "+delWhlSql,new ArrayList());						   
						 dao.delete("DELETE FROM PER_ITEMPRIV_"+planid+" where object_id "+delWhlSql,new ArrayList());
					}	
					//删除动态项目权重表的数据
					sql = "DELETE FROM  per_dyna_item where plan_id="+planid+" and body_id not in (select body_id from per_object where plan_id = "+planid+")";
					dao.delete(sql, new ArrayList());					
				}
				
			    String str_sql = (String)this.getFormHM().get("str_sql");
			    str_sql = SafeCode.decode(str_sql);
			    str_sql = PubFunc.decrypt(str_sql);
			    plan_b0110 = (String)this.getFormHM().get("plan_b0110");
			    if(!"hjsj".equalsIgnoreCase(plan_b0110))//选择的考核对象保证在考核计划所属单位下
			    {
			    	if(AdminCode.getCode("UM",plan_b0110)!=null)
			    		str_sql+=" and e0122 like '"+plan_b0110+"%' ";	
					else if(AdminCode.getCode("UN",plan_b0110)!=null)
						str_sql+=" and b0110 like '"+plan_b0110+"%' ";	
			    }	
			    
			    String whl = bo.getPrivWhere(userView);//根据用户权限先得到一个考核对象的范围
				str_sql+=whl;
				
			    bo.handInsertObjects(str_sql, planid,"2");
			}			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
