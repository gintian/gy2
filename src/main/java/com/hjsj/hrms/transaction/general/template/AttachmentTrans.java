package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**业务逻辑如下
对修改附件的控制：username一致就可以删除。从已办任务、我的申请、任务监控进入，一定不能删除。
对上传附件的控制：任何时候都可以。从已办任务、我的申请、任务监控进入，一定不能上传。**/
public class AttachmentTrans extends IBusiness {
	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String attachmenttype = (String)hm.get("attachmenttype");//附件类型 =0公共附件 =1 个人附件
		hm.remove("attachmenttype");
		if(attachmenttype==null || "".equals(attachmenttype)){//左侧人员列表无人员
			this.getFormHM().put("uploadattach", "0");
			this.getFormHM().put("affixList", new ArrayList());
			return;
		}
		String tabid=(String)this.getFormHM().get("tabid");
		String username=this.userView.getUserName();
		String ins_id=(String)this.getFormHM().get("ins_id");//流程号
		String taskid=(String)this.getFormHM().get("taskid");//任务号
		String sp_flag = (String)this.getFormHM().get("sp_flag");//=1需要审批  =2不需要审批（目前程序是这样控制的，已办任务、我的申请、任务监控进入，sp_flag=2）
		String objectid = "";//a0100|b0110|e01a1
		String basepre = "";//nbase
		int peopleCount = 1;//左侧人员列表中人员的个数.一定有人。如果没有人，程序在上面就return了。
		basepre = (String)hm.get("basepre")==null?"":(String)hm.get("basepre");
		objectid = (String)hm.get("objectid")==null?"":(String)hm.get("objectid");
		basepre = SafeCode.decode(basepre);
		objectid = SafeCode.decode(objectid);
		hm.remove("basepre");
		hm.remove("objectid");
		String sp_batch = (String)hm.get("sp_batch_temp");//1:批量审批
		hm.remove("sp_batch_temp");
		if("1".equals(sp_batch)){//如果是批量审批，还要得到流程号（流程号就不能从form中获取了。）
			ins_id =(String)hm.get("ins_id");
			taskid = (String)hm.get("task_id");
			hm.remove("ins_id");
			hm.remove("task_id");
		}
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		try {
			String selfplatform=(String)this.getFormHM().get("selfplatform");
			/*if("1".equals(selfplatform)){//如果是自助平台
				peopleCount = 1;
			}else{
				peopleCount = getPeopleCount(tabid,ins_id,taskid,sp_flag);//首先获取peopleCount的值
			}*/
			StringBuffer sb = new StringBuffer("");
			if(peopleCount>0){//左侧列表有人

				if(ins_id!=null&&!"0".equals(ins_id)){//进入了审批流
					/**进入了审批流,ins_id决定了是能否查看的文件,如果ins_id正确那么就能查看到相应ins_id流程中的文件**/
					HashMap cardAttachMap = (HashMap) this.userView.getHm().get("cardAttachMap");
					if(cardAttachMap!=null&&!cardAttachMap.containsKey(ins_id)){
						throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.no.permission.ins_id"));
					}
					if("0".equals(attachmenttype)){//公共附件
						sb.append("select * from t_wf_file where ins_id=" + ins_id + " and tabid="+tabid+" and (attachmenttype=0 or attachmenttype is null) order by create_time");
					}else if("1".equals(attachmenttype)&&objectid.length()>0){//个人附件
						sb.append("select * from t_wf_file where ins_id=" + ins_id + " and tabid="+tabid+" and attachmenttype=1");
						sb.append(" and objectid='"+objectid+"'");
						if(!"".equals(basepre)){//infor_type=1
							sb.append(" and basepre='"+basepre+"'");
						}
						sb.append(" order by create_time");
					}
				}else{//还未进入审批流
					if("0".equals(attachmenttype)){//公共附件
						sb.append("select * from t_wf_file where ins_id=" + ins_id + " and tabid="+tabid+" and (attachmenttype=0 or attachmenttype is null) and create_user='"+username+"' order by create_time");
					}else if("1".equals(attachmenttype)&&objectid.length()>0){//个人附件
						sb.append("select * from t_wf_file where ins_id=" + ins_id + " and tabid="+tabid+" and attachmenttype=1 and create_user='"+username+"'");
						sb.append(" and objectid='"+objectid+"'");
						if(!"".equals(basepre)){//infor_type=1
							sb.append(" and basepre='"+basepre+"'");
						}
						sb.append(" order by create_time");
					}
				}
			}
			if(sb.length()>0){
				frowset = dao.search(sb.toString());
				while (frowset.next()) {
					LazyDynaBean bean = new LazyDynaBean();
					/**安全平台改造,将file_id进行加密处理**/
					bean.set("file_id", SafeCode.encode(PubFunc.encrypt(frowset.getString("file_id"))));
					bean.set("attachmentname", frowset.getString("name"));
					bean.set("ext", frowset.getString("ext"));
					bean.set("ins_id", frowset.getString("ins_id"));
					Date d_create=frowset.getDate("create_time");
					String d_str=DateUtils.format(d_create,"yyyy.MM.dd");
					bean.set("create_time", d_str);
					bean.set("create_user", frowset.getString("create_user"));
					if("1".equals(sp_flag)){//如果需要审批
						if(username!=null&&username.equals(frowset.getString("create_user"))){
							bean.set("candelete", "1");
						}else{
							bean.set("candelete", "0");
						}
					}
					list.add(bean);
				} //while loop end
			}
						
			if(("1".equals(sp_flag)|| "0".equals(ins_id)) && peopleCount>0){
				this.getFormHM().put("uploadattach", "1");
			}else{
				this.getFormHM().put("uploadattach", "0");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("affixList", list);
	}
	
	/**左侧人员列表中人员的个数*/
	private int getPeopleCount(String tabid,String ins_id,String taskid,String sp_flag){
		int count = 0;
		try{
			StringBuffer sb = new StringBuffer("");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(ins_id!=null&&!"0".equals(ins_id)){//进入了审批流
				if("1".equals(sp_flag)){
					sb.append("select count(*) from t_wf_task_objlink");
					sb.append(" where ins_id="+ins_id+" and task_id ="+taskid+" and tab_id="+tabid+" and (state is null or state=0 )");
					sb.append("and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
				}else if("2".equals(sp_flag)){//从已办任务、我的申请、任务监控进入时，只有已办任务可能没有人。其它两个一定有人
					String business_model = (String)this.getFormHM().get("businessModel_yp");
					if("3".equals(business_model)){//说明是从已办任务中进入的
						sb.append("select count(*) from templet_"+tabid);
						sb.append(" where exists (select null from t_wf_task_objlink where  templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id ");
						sb.append(" and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ");	
						sb.append(" and t_wf_task_objlink.tab_id="+tabid+" and t_wf_task_objlink.task_id="+taskid+"    and ( "+Sql_switcher.isnull("t_wf_task_objlink.state","0")+"<>3 )  )");
					}else{
						return 1;
					}
				}
			}else{//没有进入审批流
				sb.append("select count(*) from "+this.userView.getUserName()+"templet_"+tabid);
			}
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next()){
				count = this.frowset.getInt(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return count;
	}
}
