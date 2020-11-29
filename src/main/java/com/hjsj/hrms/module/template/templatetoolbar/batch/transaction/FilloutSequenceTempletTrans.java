package com.hjsj.hrms.module.template.templatetoolbar.batch.transaction;

import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:生成序号</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2016-6-12</p> 
 *@author dengc
 *@version 7.x
 */
public class FilloutSequenceTempletTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
			TemplateFrontProperty frontProperty = new TemplateFrontProperty(this.getFormHM());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
		    /** 模板使用的模块
			 * 1、人事异动
			 * 2、薪资管理 
			 * 3、劳动合同
			 * 4、保险管理
			 * 5、出国管理
			 * 6、资格评审
			 * 7、机构管理
			 * 8、岗位管理 
			 * 9、业务申请（自助）
			 * 10、考勤管理
			 * 11、职称评审 
			*/
	        String moduleId = frontProperty.getModuleId();
	        String tabId = frontProperty.getTabId();
	        if(!PubFunc.validateNum(tabId,3))
	        	return; 
	        String taskId = frontProperty.getTaskId();
	        /** 信息群类型
	         * 1：人员 2： 单位 3： 岗位 后台根据模板类型判断
	        */ 
	        String inforType = frontProperty.getInforType();
	        String tabname="";
	        StringBuffer sql=new StringBuffer("");
	        if("9".equals(moduleId))
	        {
	        	sql.append("select a0100,nbase,0 as  ins_id from g_templet_"+tabId+" where submitflag=1 ");
	        	tabname="g_templet_"+tabId;
	        }
	        else
	        {
	        	if("1".equals(inforType)){ //人员
	        		sql.append("select basepre,a0100  ");
				}else if("2".equals(inforType)){
					sql.append("select b0110 ");
				}else if("3".equals(inforType)){
					sql.append("select e01a1  ");
				} 
	        	if("0".equalsIgnoreCase(taskId))
	        	{
	        		sql.append(", 0 as ins_id  from "+this.userView.getUserName()+"templet_"+tabId+" where  submitflag=1  "); 
	        		tabname=this.userView.getUserName()+"templet_"+tabId;
	        	}
	        	else
	        	{
	        		if(taskId.charAt(0)!=',')
	        			taskId=","+taskId;
	        		sql.append(", ins_id from templet_"+tabId+" where 1=1 ");
	        		sql.append(" and  exists (select null from t_wf_task_objlink where templet_"+tabId+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabId+".ins_id=t_wf_task_objlink.ins_id ");
					sql.append(" and t_wf_task_objlink.submitflag=1 and t_wf_task_objlink.task_id in ("+taskId.substring(1)+")  and t_wf_task_objlink.state<>3   ) order by ins_id");  
					tabname="templet_"+tabId;
	        	}
	        	
	        }
	        
	        TemplateParam param=new TemplateParam(this.getFrameconn(),this.userView,Integer.parseInt(tabId));
			TemplateBo tablebo=new TemplateBo(this.getFrameconn(),this.userView,param);
			 if("1".equals(param.getId_gen_manual())){  //关联序号的变化后指标是否手工生成序号, 0加人时自动生成(默认值),1手工生成
				 
		        this.frowset =dao.search(sql.toString());
		        String a0100="";
		        String basepre="";
				while(this.frowset.next()){ 
					if(inforType!=null){   //1：人员 2： 单位 3： 岗位 后台根据模板类型判断
						if("1".equals(inforType)){
							a0100 = this.frowset.getString("a0100");
							basepre = this.frowset.getString("basepre");
						}else if("2".equals(inforType)){
							a0100 = this.frowset.getString("b0110");
						}else if("3".equals(inforType)){
							a0100 = this.frowset.getString("e01a1");
						}
					} 
					String ins_id=this.frowset.getString("ins_id");
				    tablebo.filloutSequence(a0100,basepre , tabname,ins_id);  	 
				} 
			 }
	         
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		

	}

}
