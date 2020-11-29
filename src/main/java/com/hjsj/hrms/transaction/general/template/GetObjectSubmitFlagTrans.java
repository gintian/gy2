package com.hjsj.hrms.transaction.general.template;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 仅用于打印登记表
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 17, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class GetObjectSubmitFlagTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		String setname=(String)this.getFormHM().get("setname");
		ArrayList objarr=(ArrayList)this.getFormHM().get("objarr");
		String  infor_type = (String)this.getFormHM().get("infor_type");
		String ins_id =(String)this.getFormHM().get("ins_id");
		String task_id=(String)this.getFormHM().get("task_id");
		String sp_batch=(String)this.getFormHM().get("sp_batch");
		String batch_task=(String)this.getFormHM().get("batch_task");
		String task_sp_flag=(String)this.getFormHM().get("task_sp_flag");
		String businessModel_yp=(String)this.getFormHM().get("businessModel_yp");
		ArrayList personlist=new ArrayList();
		String sql="select a0100,basepre,a0101_1 from "+setname+" ";
		if(infor_type!=null&& "2".equals(infor_type))
			sql = "select b0110 a0100,codeitemdesc_1 a0101_1 from "+setname+" ";
		if(infor_type!=null&& "3".equals(infor_type))
			sql = "select e01a1 a0100,codeitemdesc_1 a0101_1 from "+setname+" ";
		if("0".equals(ins_id)){
			sql+=" where submitflag='1' ";
		}else{
			sql+=" where  exists (select null from t_wf_task_objlink where "+setname+".seqnum=t_wf_task_objlink.seqnum and "+setname+".ins_id=t_wf_task_objlink.ins_id and submitflag =1  ";
			if(!"2".equals(task_sp_flag)&&!"3".equals(businessModel_yp)){
				sql+=" and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ";
			}
		}
		String str="";
		/** dengcan 20090731*/
//		if(objarr!=null&&objarr.size()>0)
//		{
//			StringBuffer sub_str=new StringBuffer("");
//			for(int i=0;i<objarr.size();i++)
//			{
//				str=(String)objarr.get(i);
//				if(str.trim().length()==0)
//					continue;
//				String[] temp=str.split("\\|");
//				if(infor_type!=null&&infor_type.equals("1")){
//				sub_str.append(" or (a0100='"+temp[1]+"' and lower(basepre)='"+temp[0].toLowerCase()+"')");
//				}
//				else if(infor_type!=null&&infor_type.equals("2")){
//					sub_str.append(" or( b0110='"+temp[1]+"') ");	
//				}else if(infor_type!=null&&infor_type.equals("3")){
//					sub_str.append(" or (e01a1='"+temp[1]+"') ");
//				} 
//				
//			}
//			if(sub_str.length()>0&&infor_type!=null&&infor_type.equals("1"))
//				sql="select distinct a0100,basepre,a0101_1 from "+setname+" where 1=1  and ( "+sub_str.substring(3)+" )";
//			else if(infor_type!=null&&infor_type.equals("2")){
//				sql = "select b0110 a0100,codeitemdesc_1 a0101_1 from "+setname+" where 1=1  and ( "+sub_str.substring(3)+" )";
//			}else if(infor_type!=null&&infor_type.equals("3")){
//				sql = "select e01a1 a0100,codeitemdesc_1 a0101_1 from "+setname+" where 1=1  and ( "+sub_str.substring(3)+" )";
//			}
//		}
		if("1".equals(sp_batch))
		{ 
			String[] lists=StringUtils.split(batch_task,",");
		 
			StringBuffer buf=new StringBuffer("");
			for(int i=0;i<lists.length;i++)
			{
				if(lists[i]==null||lists[i].trim().length()==0)
					 continue;
				buf.append(",");
				buf.append(lists[i]);
			}
			if(buf.length()>0){
				sql+=" and   task_id in ("+buf.substring(1)+" ) ";
			}
			sql+=" and (state is null or state<>3) ) ";
		}
		else
		{
			if(ins_id!=null&&!"0".equals(ins_id)){
			if(task_id!=null&&!"0".equalsIgnoreCase(task_id))
			{
				sql+=" and task_id="+task_id;
			}
			sql+=" and (state is null or state<>3) ) ";
			}
		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String tag="";
		Des des= new Des();//调用cs控件打印时采用这个对象对nbase和A0100进行加密
		try
		{
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				if(infor_type!=null&& "1".equals(infor_type)){
					tag="<NBASE>"+des.EncryPwdStr(this.frowset.getString("basepre"))+"</NBASE><ID>"+des.EncryPwdStr(this.frowset.getString("a0100"))+"</ID><NAME>"+this.frowset.getString("a0101_1")+"</NAME>";
					//tag="<NBASE>"+this.frowset.getString("basepre")+"</NBASE><ID>"+this.frowset.getString("a0100")+"</ID><NAME>"+this.frowset.getString("a0101_1")+"</NAME>";
				}else{
					tag="<NBASE>"+des.EncryPwdStr("")+"</NBASE><ID>"+des.EncryPwdStr(this.frowset.getString("a0100"))+"</ID><NAME>"+this.frowset.getString("a0101_1")+"</NAME>";
					//tag="<NBASE></NBASE><ID>"+this.frowset.getString("a0100")+"</ID><NAME>"+this.frowset.getString("a0101_1")+"</NAME>";
				}
				
				CommonData dataobj = new CommonData(tag,this.frowset.getString("a0100"));
				personlist.add(dataobj);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("personlist",personlist);
	}

}
