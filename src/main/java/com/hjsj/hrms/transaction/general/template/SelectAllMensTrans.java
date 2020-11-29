package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SelectAllMensTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String setname=(String)this.getFormHM().get("setname");
			String submitflag=(String)this.getFormHM().get("submitflag");
			String ins_ids=(String)this.getFormHM().get("ins_ids");
			String ins_id=(String)this.getFormHM().get("ins_id");
			String task_id=(String)this.getFormHM().get("task_id");
			String ids=(String)this.getFormHM().get("ids");
			String filterStr = (String)this.getFormHM().get("filterStr");
			String sp_batch = (String)this.getFormHM().get("sp_batch");
			String infor_type=(String)this.getFormHM().get("infor_type");
			String batch_task = (String)this.getFormHM().get("batch_task");
			if(infor_type==null)
				infor_type="1";
			if(filterStr==null)
				filterStr="";
			else
				filterStr = PubFunc.keyWord_reback(filterStr);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if(!"0".equalsIgnoreCase(ins_id))
			{
				if(ids!=null&&ins_ids!=null&&ins_ids.trim().length()>0)
				{
					if("1".equals(sp_batch))
					{ 
						task_id ="";
						String[] temps=batch_task.split(",");
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i]!=null&&temps[i].trim().length()>0)
								task_id+=","+temps[i];
						}
						if(task_id.length()>0)
						task_id = "task_id in("+task_id.substring(1)+")";
					}else{
						task_id = "task_id="+task_id;
					}
					String[] id_s=ids.split("/");
					String sql="update t_wf_task_objlink set submitflag=? where ins_id in ("+ins_ids.substring(1)+") and "+task_id+" and exists ( select null from "+setname;
					sql+=" where "+setname+".seqnum=t_wf_task_objlink.seqnum ";
					String sub_sql=" and lower(basepre)=? and a0100=? ";
					if("2".equals(infor_type))
						sub_sql=" and b0110=? ";
					else if("3".equals(infor_type))
						sub_sql=" and e01a1=? ";
					
					if("".equals(filterStr)){
					 sql+=" and ins_id in ("+ins_ids.substring(1)+") "+sub_sql;
					}else{
					 sql+=" and ins_id in ("+ins_ids.substring(1)+") "+sub_sql+" and "+filterStr;
					}
					sql+=" ) ";
					ArrayList list=new ArrayList();
					for(int i=0;i<id_s.length;i++)
					{
						ArrayList tempList=null;
						if(id_s[i].trim().length()>0)
						{
							tempList=new ArrayList();
							tempList.add(new Integer(submitflag));
							if("1".equals(infor_type))
							{
								tempList.add(id_s[i].substring(0,3).toLowerCase());
								tempList.add(id_s[i].substring(3));
							}
							else
								tempList.add(id_s[i]);
							list.add(tempList);
						}
					}
					if(list.size()>0)
						dao.batchUpdate(sql,list);
				}
			}
			else{
				if("".equals(filterStr)){
				dao.update("update "+setname+" set submitflag="+submitflag);
				}else
					dao.update("update "+setname+" set submitflag="+submitflag+" where  "+filterStr);	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
