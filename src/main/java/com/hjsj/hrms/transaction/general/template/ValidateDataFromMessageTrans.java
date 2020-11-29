package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:验证人员是否来自消息</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 11, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class ValidateDataFromMessageTrans extends IBusiness {


	public void execute() throws GeneralException {
		String ins_id=(String)this.getFormHM().get("ins_id");
		ArrayList list=(ArrayList)this.getFormHM().get("a0100s");
		String setname=(String)this.getFormHM().get("setname");
		String tab_id=(String)this.getFormHM().get("tabid");
		String pageno=(String)this.getFormHM().get("pageno");
		String infor_type=(String)this.getFormHM().get("infor_type");
		HashMap taskmp=new HashMap();
		String from_msg="0";  
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if(this.getFormHM().get("returnflag")!=null&& "list".equals((String)this.getFormHM().get("returnflag")))  //列表方式
			{
				String task_id=(String)this.getFormHM().get("task_id");
				String selected=(String)this.getFormHM().get("selected");  //1:选中人  0：全部
				StringBuffer sql=new StringBuffer("select count(*) from "+setname+" where state=1");
				
				if(!"0".equals(task_id))
				{
					sql.append(" and (task_id="+task_id+" or exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id ");
					sql.append("  and task_id="+task_id+"  and state<>3 ) )");
				}else{
					if("1".equals(selected))
						sql.append(" and  submitflag=1 ");
				}
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next())
				{
					if(this.frowset.getInt(1)>0)
					{
						from_msg="1";
					}
				}
				this.getFormHM().put("selected",selected);
				this.getFormHM().put("task_id", task_id);
				this.getFormHM().put("from_msg",from_msg);
			}
			else
			{
				RecordVo vo=new RecordVo(setname);
				for(int i=0;i<list.size();i++)
				{
					String value=(String)list.get(i);
					if("".equals(value))
						continue;
					Object[] arr=PubFunc.split(value,"|");// StringUtils.split(value,'|');
					if("1".equals(infor_type))
					{
						vo.setString("basepre", (String)arr[0]);
						vo.setString("a0100", (String)arr[1]);
					}
					else if("2".equals(infor_type))
					{
						vo.setString("b0110", (String)arr[0]);
					}
					else if("3".equals(infor_type))
					{
						vo.setString("e01a1", (String)arr[0]);
					}
					if("0".equalsIgnoreCase(ins_id))
					{
						vo=dao.findByPrimaryKey(vo);
						/**
						 * 来自于消息,如果删除了此记录，则需要把tmessage中的消息
						 * 置为未处理状态
						 * */
						String state_flag=vo.getString("state");
						if(state_flag==null|| "".equalsIgnoreCase(state_flag))
							state_flag="0";
						if("1".equals(state_flag))
						{
							from_msg="1";
							break;
						}
					}
				}
				this.getFormHM().put("from_msg",from_msg);
				
				this.getFormHM().put("ins_id", ins_id);
				this.getFormHM().put("a0100s", list);
				this.getFormHM().put("setname", setname);
				this.getFormHM().put("tab_id", tab_id);
				this.getFormHM().put("pageno",pageno);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
