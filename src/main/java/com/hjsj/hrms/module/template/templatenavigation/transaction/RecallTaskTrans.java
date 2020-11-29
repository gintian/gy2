package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.utils.TemplateInterceptorAdapter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class RecallTaskTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		ArrayList recallList=(ArrayList) this.getFormHM().get("recallList");	
		String module_id=(String) this.getFormHM().get("module_id");
		String ischeck=(String) this.getFormHM().get("ischeck");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList inslist=new ArrayList();
		ArrayList dellist=new ArrayList();
		ArrayList taskIdList=new ArrayList();
		try {
			StringBuffer delsqlfortask=new StringBuffer();
			StringBuffer delsqlforobjlink=new StringBuffer();
			StringBuffer delsqlforins=new StringBuffer();
			StringBuffer middlesql = new StringBuffer();
			//删除任务 将insid对应的t_wf_task以及t_wf_task_objlink 中数据清除
			delsqlfortask.append("delete from t_wf_task where ins_id in(-1");
			delsqlforobjlink.append("delete from t_wf_task_objlink where ins_id in(-1");
			delsqlforins.append("delete from t_wf_instance where ins_id in(-1");
			String recallname = "";
			int j=1;
			if("1".equals(ischeck)){
				//判断任务是否可撤回
				String notRecallName = this.checkRecallOrNot(module_id,recallList);
				if(StringUtils.isNotBlank(notRecallName)){
					this.getFormHM().put("notRecallName", notRecallName);
					return;
				}
			}
			Pattern pattern = Pattern.compile("[0-9]+");
			for(int i=0;i<recallList.size();i++)
			{
				MorphDynaBean rec=(MorphDynaBean)recallList.get(i); 
				HashMap map = new HashMap();
				String ins_id=(String)rec.get("ins_id");
				if(!pattern.matcher(ins_id).matches())//判断是不是纯数字，防止注入
					continue;
				String tab_id = (String)rec.get("tabid");
				if(!pattern.matcher(tab_id).matches())//判断是不是纯数字，防止注入
					continue;
				String task_id = PubFunc.decrypt((String)rec.get("task_id_e"));
				//判断此ins_id对应的人员或者单位在起草临时表中是否存在正在起草的人
				String recallname_ = this.getStartTask(ins_id,tab_id,module_id);
				if(StringUtils.isNotBlank(recallname_)&&recallname.indexOf(recallname_)==-1){
					if(i==0)
						recallname+=recallname_;
					else
						recallname+=","+recallname_;
				}
				
				middlesql.append(",");
				middlesql.append(ins_id);
				
				if(i==900*j){//oracle in 限制1000之内
					j++;
					middlesql.append(")");
					middlesql.append(" or ins_id in (-1");
				}
				map.put("ins_id", ins_id);
				map.put("tab_id", tab_id);
				if(StringUtils.isNotBlank(recallname))
					map.put("ishaverecall", "1");
				else
					map.put("ishaverecall", "0");
				dellist.add(map);
				inslist.add(ins_id);
				taskIdList.add(task_id);//用于删除其他系统的代办
			}
			if(StringUtils.isNotBlank(recallname)&&"1".equals(ischeck)){
				if(recallname.startsWith(","))
					recallname = recallname.substring(1,recallname.length());
				this.getFormHM().put("recallname", recallname);
				return;
			}
			for(int i = 0;i<dellist.size();i++){
				HashMap map = (HashMap)dellist.get(i);
				String tab_id = (String)map.get("tab_id");
				String ins_id = (String)map.get("ins_id");
				String ishaverecall = (String)map.get("ishaverecall");
				if("0".equals(ishaverecall)){
					String recallname_ = this.getStartTask(ins_id,tab_id,module_id);
					if(StringUtils.isNotBlank(recallname_))
						ishaverecall="1";
				}
				TemplateInterceptorAdapter.afterHandle(0,Integer.parseInt(ins_id),Integer.parseInt(tab_id),null,"recall",this.userView);
				//对选中任务对应的临时表的数据先迁移到起草的临时表然后进行删除templet_tabid
				this.deleteTempletData(ins_id,tab_id,module_id,ishaverecall);
				
			}
			/**对选中的任务t_wf_task进行删除*/
			dao.delete(delsqlfortask.toString()+middlesql+" ) ", new ArrayList());
			/**对选中的任务关联表t_wf_task_objlink进行删除*/
			dao.delete(delsqlforobjlink.toString()+middlesql+" ) ", new ArrayList());
			/**对选中的任务关联表t_wf_instance进行删除*/
			dao.delete(delsqlforins.toString()+middlesql+" ) ", new ArrayList());
			/** 删除其它系统的待办任务 */
			this.deletePendingTask(taskIdList);
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 判断任务是否可撤回
	 * @param module_id
	 * @param recallList
	 * @return
	 */
	private String checkRecallOrNot(String module_id, ArrayList recallList) throws GeneralException {
		RowSet rowset=null;
		RowSet rowset1=null;
		String notRecallName = "";
		try {
			ContentDAO dao=new ContentDAO(this.frameconn);
			for(int i=0;i<recallList.size();i++)
			{
				Boolean hasRevoke=true;
				MorphDynaBean rec=(MorphDynaBean)recallList.get(i); 
				HashMap map = new HashMap();
				String ins_id=(String)rec.get("ins_id");
				String tab_id = (String)rec.get("tabid");
				String task_id = PubFunc.decrypt((String)rec.get("task_id_e"));
				//判断是不是发起人
				boolean isStart = false;
				StringBuffer strsql=new StringBuffer();
				strsql.append("select 1 from t_wf_instance twi where ");
				strsql.append("((twi.actor_type=4 and lower(twi.actorid)='"+this.userView.getUserName().toLowerCase()+"') ");
				if(this.userView.getA0100()!=null&&!"".equals(this.userView.getA0100()))
					strsql.append(" or (twi.actor_type=1 and lower(twi.actorid)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"') ");
				strsql.append(" ) and  twi.ins_id = (select tt.ins_id from t_wf_task tt where tt.task_id = '"+task_id+"') ");
				rowset1=dao.search(strsql.toString());
				if(rowset1.next())
					isStart = true;
				if(isStart){
					strsql.setLength(0);
					strsql.append("select count(*) num from t_wf_task twt where twt.task_type=2 ");
					strsql.append("and twt.bread=1 and twt.ins_id = (select tt.ins_id from t_wf_task tt where tt.task_id = "+task_id+") ");
					rowset = dao.search(strsql.toString());
					if(rowset.next())
					{
						int num = rowset.getInt("num");
						if(num>0)
						{
							hasRevoke=false;
						}
					}
				}else
					hasRevoke = false;
				if(!hasRevoke){
					//查出模板名字
					RecordVo vo = new RecordVo("template_table");
					vo.setInt("tabid", Integer.parseInt(tab_id));
					vo=dao.findByPrimaryKey(vo);
					String tabname=vo.getString("name");
					if(StringUtils.isNotBlank(tabname)&&notRecallName.indexOf(tabname)==-1){
						if(i==0)
							notRecallName+=tabname;
						else
							notRecallName+=","+tabname;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowset);
			PubFunc.closeDbObj(rowset1);
		}
		return notRecallName;
	}
	/**
	 * 判断此ins_id对应的人员或者单位在起草临时表中是否存在正在起草的人
	 * @param ins_id
	 * @param tab_id
	 * @param module_id
	 * @param dao
	 * @throws GeneralException
	 */
    private String getStartTask(String ins_id, String tab_id, String module_id) throws GeneralException{
    	String recallname = "";
    	try {
			TemplateTableBo tablebo = new TemplateTableBo(this.getFrameconn(), 
	                Integer.parseInt(tab_id), this.userView);
			boolean bSelfApply = false;
			if("9".equals(module_id))
				bSelfApply = true;
			tablebo.setBEmploy(bSelfApply);
			recallname = tablebo.getRecallStartTask(this.userView.getUserName(),Integer.parseInt(ins_id));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	return recallname;
	}
	/**
     * 删除其它系统的待办任务
     * @param taskIdList
     */
	private void deletePendingTask(ArrayList taskIdList) {
		try {
			PendingTask imip=new PendingTask();
			String pendingType="业务模板";
			for(int i=0;i<taskIdList.size();i++)
			{
				String task_id=(String)taskIdList.get(i);
				task_id=PubFunc.encrypt(task_id);
				imip.updatePending("T","HRMS-"+task_id,100,pendingType,this.userView);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 对选中任务对应的临时表的数据先迁移到起草的临时表然后进行删除templet_tabid
	 * @param ins_id
	 * @param tab_id
	 * @param module_id
	 * @param ishaverecall 
	 * @throws GeneralException
	 */
	private void deleteTempletData(String ins_id, String tab_id, String module_id, String ishaverecall) throws GeneralException {
		//对选中任务对应的临时表的数据先迁移到起草的临时表
		try {
			TemplateTableBo tablebo = new TemplateTableBo(this.getFrameconn(), 
	                Integer.parseInt(tab_id), this.userView);
			boolean bSelfApply = false;
			if("9".equals(module_id))
				bSelfApply = true;
			tablebo.setBEmploy(bSelfApply);
			//调考勤接口
			String sql = "select * from templet_"+tab_id+" t where seqnum not in (select seqnum from t_wf_task_objlink where ins_id="+
					ins_id+" and state=3  ) and ins_id="+ins_id;
			String tablename="templet_"+tab_id;
	   	    WF_Instance wf_ins=new WF_Instance(tablebo, this.getFrameconn());
	   	    wf_ins.insertKqApplyTable(sql,tab_id,"","10",tablename); //往考勤申请单中写
			tablebo.saveRecallTemplatedata(this.userView.getUserName(),Integer.parseInt(ins_id),"",ishaverecall);
			TempletChgLogBo chgLogBo=new TempletChgLogBo(this.frameconn,this.userView);
			chgLogBo.recallTaskUpdateInsidToZero(ins_id);//把流程中的变动日志改回起草状态
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
