package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class OptAllRecordsTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String    filterStr=(String)this.getFormHM().get("filterStr");                     //查询过滤条件串  and XXXXXX
			String    tabid=(String)this.getFormHM().get("tabid");
			String    codeid=(String)this.getFormHM().get("codeid");
			String    isSelectAll=(String)this.getFormHM().get("isSelectAll");
			String    tasklist_str=(String)this.getFormHM().get("tasklist_str");
			String    table_name=(String)this.getFormHM().get("table_name");
			/**业务类型
			 * 对人员调入的业务单独处理
			 * =0人员调入,=1调出（须指定目标人员库）,=2离退(须指定目标人员库),=3调动,
			 * =10其它不作特殊处理的业务
			 * 如果目标库未指定的话，则按源库进行处理
			 */
			String    operationtype=(String)this.getFormHM().get("operationtype");
			TemplateTableBo bo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid.trim()),this.getUserView());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer sql=new StringBuffer(" update  "+table_name+"  set  submitflag="+isSelectAll+" where 1=1 ");

			if(tasklist_str!=null&&tasklist_str.trim().length()>0)
			{
				sql.setLength(0);
				sql.append("update t_wf_task_objlink set submitflag="+isSelectAll+" where exists (select null from "+table_name+" where t_wf_task_objlink.seqnum="+table_name+".seqnum  ");
			}
			/**查询过滤条件进行了加密处理,要解密回来**/
			filterStr = PubFunc.decrypt(filterStr);
			filterStr = PubFunc.keyWord_reback(filterStr);

			if(filterStr!=null&&filterStr.trim().length()>0)
				sql.append(" and "+filterStr);
			if(codeid!=null&&codeid.trim().length()>2&&!"0".equals(operationtype))
			{
				String value=codeid.substring(2);
				if(bo.getInfor_type()==1)
				{
					if("UN".equalsIgnoreCase(codeid.substring(0,2)))
					{
						sql.append(" and b0110_1 like '"+value+"%'");
					}
					else if("UM".equalsIgnoreCase(codeid.substring(0,2)))
					{
						sql.append(" and e0122_1 like '"+value+"%'");
					}
					else if("@K".equalsIgnoreCase(codeid.substring(0,2)))
					{
						sql.append(" and e01a1_1 like '"+value+"%'");
					}
				}
				else if(bo.getInfor_type()==2)
				{
					if("5".equals(operationtype))
						sql.append(" and parentid_2 like '"+value+"%'");
					else
						sql.append(" and b0110 like '"+value+"%'");
				}
				else if(bo.getInfor_type()==3)
				{
					if("5".equals(operationtype))
						sql.append(" and parentid_2 like '"+value+"%'");
					else
						sql.append(" and e01a1 like '"+value+"%'");
				}
			}
			
			if(tasklist_str!=null&&tasklist_str.trim().length()>0)
			{
				ArrayList tasklist=new ArrayList();
				if(tasklist_str.length()>0)
				{
					String[] temp=tasklist_str.split(",");
					for(int i=0;i<temp.length;i++)
					{
						if(temp[i]==null||temp[i].length()==0)
							continue;
						tasklist.add(temp[i]);
						
					}
				}
				StringBuffer strins=new StringBuffer();
				for(int i=0;i<tasklist.size();i++)//按任务号查询需要审批的对象20080418
				{
									if(i!=0)
									  strins.append(",");
									strins.append((String)tasklist.get(i));
				}
				sql.append(" and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) and task_id in ("+strins.toString()+")");
				/*
				sql.append(" and ( task_id in(");
				sql.append(strins.toString());
				sql.append(")");				
				//角色属性是否为汇报关系 “直接领导”、“主管领导”，“第三级领导”、“第四级领导”、“全部领导”，
				sql.append(" or exists (select null from t_wf_task_datalink where templet_"+tabid+".seqnum=t_wf_task_datalink.seqnum ");
				sql.append("  and task_id in ("+strins.toString()+") and state=0 ) ) ");
				*/
			}
			dao.update(sql.toString());
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
