package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.CashListBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SelectTemplateTrans.java</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-10-7 8:54:38</p>
 * @author LiZhenWei
 * @version 4.0
 */
public class SelectTemplateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String templateId=(String)map.get("templateId");
			String salaryid=(String)map.get("salaryid");
			String code=(String)map.get("code");
			/**type=1是按选择的人=0所有*/
			String type=(String)map.get("type");
			String nbase="#";
			String selectedid=(String)this.getFormHM().get("selectedid");
			String tableName=this.userView.getUserName()+"_salary_"+salaryid;
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			String columns="personid,id,a0100,b0110,e0122,subject,send_ok,a0101,nbase,address";
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			String priv_mode=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
			if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
				tableName=this.userView.getUserName()+"_salary_"+salaryid;
		    else
				tableName=manager+"_salary_"+salaryid;
			CashListBo clb = new CashListBo(this.getFrameconn(),"0",salaryid);
			clb.setUserview(this.userView);
			String privSql=clb.getPrivSql(this.userView, gzbo);
			String order_by=(String)this.getFormHM().get("order_by");
			String codesql = clb.getCodeSql("s", code);
			String filterId=(String)this.getFormHM().get("filterId");
			BankDiskSetBo bbo = new BankDiskSetBo(this.getFrameconn(),this.getUserView());
			ArrayList filterList = bbo.getFilterCondList(salaryid);
			String beforeSql=(String)this.getFormHM().get("beforeSql");
			String _beforeSql=PubFunc.decrypt(SafeCode.decode(beforeSql));
			String sendok = (String)this.getFormHM().get("sendok");
			String queryvalue=(String)this.getFormHM().get("queryvalue");
			String queryYearValue=(String)this.getFormHM().get("queryYearValue");
			String queryName=(String)this.getFormHM().get("queryName");
			String timesql = "";
			if(!("0".equals(queryvalue)||queryvalue==null))
			{
				timesql = Sql_switcher.month("e.send_time")+"="+queryvalue;
				
			}	
			

			if(StringUtils.isNotBlank(queryYearValue)){
				if(timesql.length()>0)
					timesql=timesql+" and "+Sql_switcher.year("e.send_time")+"="+queryYearValue;
				else
					timesql=Sql_switcher.year("e.send_time")+"="+queryYearValue;
			}
			String sql=bo.getSearchPersonByCondSql("send_ok",sendok,templateId,codesql,tableName,timesql,priv_mode,privSql,queryName,order_by,_beforeSql);//此处调用切换状态那的sql语句，方便保持一致  zhaoxg update
//			String sql = bo.getSelect_sqlAndWhere_sqlAndOrder_sql(codesql,templateId,tableName,"",this.userView.getUserName(),"",priv_mode,privSql,"",order_by,_beforeSql);
			String[] sql_arr=sql.split("#");
			RecordVo avo = new RecordVo("salarytemplate");
			avo.setInt("salaryid", Integer.parseInt(salaryid));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			avo = dao.findByPrimaryKey(avo);
			bo.setSalaryTemplateVo(avo);
		    LazyDynaBean bean = bo.getTemplateInfo(templateId);
		    /**得邮件指标*/
	        String emailfield =(String)bean.get("address");//bo.getEmailField(templateId);f
	    	/**得模板标题*/
	        String subject=(String)bean.get("subject");//bo.getEmailTemplateSubject(templateId);f
	        /**将基本信息导入emailcontent中*/
	        bo.exportPersonBaseIntoContent(templateId,subject,tableName,code,emailfield,this.userView,type,selectedid,_beforeSql);
	        /**得包含邮件指标的主集，以便取得实际的邮件地址*/
	        FieldItem item = DataDictionary.getFieldItem(emailfield.toLowerCase());
	        String emailfieldset =item.getFieldsetid();
	        /**得模板项目列表*/
	        ArrayList list=bo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
	        /**得邮件模板下满足部门或单位条件限制的人员*/
	        ArrayList a0100=bo.getEmailContentA0100(templateId,code,tableName,type,selectedid,codesql,privSql,_beforeSql);
	        /**取邮件模板内容*/
	        String content=(String)bean.get("content");//bo.getEmailContent(Integer.parseInt(templateId));ff
	        /**将实际的发送内容更新到emailcontent*/
		    bo.updateEmailContent(null,list,templateId,a0100,content,this.userView,1,salaryid,tableName);
		    /**将实际邮件地址更新到emailcontent*/
		    if(!"A01".equalsIgnoreCase(emailfieldset))
    		    bo.getEmailValue(emailfield,emailfieldset,tableName,a0100,templateId);
		   // System.out.println(sql_arr[0]+" "+sql_arr[1]+" "+sql_arr[2]);
			ArrayList querylist=bo.getMonthList();
			ArrayList queryListYear=bo.getYearList();
			if(queryListYear.size()>0&&StringUtils.isBlank(queryYearValue))
				queryYearValue=((CommonData)queryListYear.get(0)).getDataValue();
			this.getFormHM().put("querylist",querylist);
			this.getFormHM().put("nbase",nbase);
			this.getFormHM().put("sendok","3");
			this.getFormHM().put("queryvalue","0");
			this.getFormHM().put("select_sql",sql_arr[0]);
			this.getFormHM().put("where_sql",sql_arr[1]);
			this.getFormHM().put("order_sql",sql_arr[2]);
			this.getFormHM().put("queryvalue",queryvalue);
			this.getFormHM().put("queryListYear",queryListYear);
			this.getFormHM().put("queryYearValue",queryYearValue);
			this.getFormHM().put("columns",columns);
			this.getFormHM().put("code",code);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("id",templateId);
			this.getFormHM().put("templateId",templateId);
			this.getFormHM().put("queryName","");
			this.getFormHM().put("order_by", order_by);
			this.getFormHM().put("filterId",filterId);
			this.getFormHM().put("filterList",filterList);
			this.getFormHM().put("beforeSql",beforeSql);
			this.getFormHM().put("tableName",tableName);
			//this.getFormHM().put("privdbpre",privdbpre);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
