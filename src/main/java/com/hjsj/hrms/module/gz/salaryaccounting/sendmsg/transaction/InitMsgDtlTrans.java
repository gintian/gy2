package com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.transaction;

import com.hjsj.hrms.businessobject.gz.CashListBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.businessobject.SendMsgBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：InitMsgDtlTrans 
 * 类描述：薪资发放-发放通知生成通知按钮
 * 创建人：sunming
 * 创建时间：2015-7-7
 * @version
 */
public class InitMsgDtlTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		try
		{
			String templateId=(String)this.getFormHM().get("templateId");
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			//业务日期，为"2015-05",存入数据库要拼接成"2015-05-01"
			String appdate = (String) this.getFormHM().get("appdate");
			appdate = PubFunc.decrypt(SafeCode.decode(appdate));
			if(appdate.split("-").length==2)
			appdate = appdate + "-01";
			String count=(String)this.getFormHM().get("count"); //发放次数
			count = PubFunc.decrypt(SafeCode.decode(count));
			/**type=1是按选择的人 =0所有*/
			String type=(String) this.getFormHM().get("type");
			//选中人员
			String selectedid=(String)this.getFormHM().get("selectedid");
			//表名
			String tableName="";
			SendMsgBo bo = new SendMsgBo(this.getFrameconn(),  userView);
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			//获取共享
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
				tableName=this.userView.getUserName()+"_salary_"+salaryid;
		    else
				tableName=manager+"_salary_"+salaryid;
			CashListBo clb = new CashListBo(this.getFrameconn(),"0",salaryid);
			clb.setUserview(this.userView);
			RecordVo avo = new RecordVo("salarytemplate");
			avo.setInt("salaryid", Integer.parseInt(salaryid));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			avo = dao.findByPrimaryKey(avo);
			bo.setSalaryTemplateVo(avo);
		    LazyDynaBean bean = bo.getTemplateInfo(templateId);
		    /**得邮件指标*/
	        String emailfield =(String)bean.get("address");
	    	/**得模板标题*/
	        String subject=(String)bean.get("subject");
	        /**取得基本信息*/
			ArrayList<HashMap<String,String>> a0100List=bo.exportPersonBaseIntoContent(templateId,subject,tableName,emailfield,this.userView,type,selectedid,appdate,salaryid,count);
	        /**得包含邮件指标的主集，以便取得实际的邮件地址*/
	        FieldItem item = DataDictionary.getFieldItem(emailfield.toLowerCase());
	        String emailfieldset =item.getFieldsetid();
	        /**得模板项目列表*/
	        ArrayList<LazyDynaBean> list=bo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
	        /**如果邮件地址不在a01中，取得其他子集的邮件地址*/
            if(!"A01".equalsIgnoreCase(emailfieldset))
                bo.getEmailValue(emailfield,emailfieldset,a0100List);
	        /**取邮件模板内容*/
	        String content=(String)bean.get("content");
	        /**插入邮件*/
		    bo.updateEmailContent(list,templateId,a0100List,content,this.userView,salaryid,tableName,appdate,count,subject,type,selectedid);
			this.getFormHM().put("init", "init2");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
