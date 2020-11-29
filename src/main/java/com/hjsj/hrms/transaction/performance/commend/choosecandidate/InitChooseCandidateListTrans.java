package com.hjsj.hrms.transaction.performance.commend.choosecandidate;

import com.hjsj.hrms.businessobject.performance.commend.CommendSetBo;
import com.hjsj.hrms.businessobject.performance.commend.CommendXMLBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class InitChooseCandidateListTrans extends IBusiness{
	public void execute() throws GeneralException{
		
		        if(this.userView.getA0100()==null|| "".equals(this.userView.getA0100()))
		        	throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
				CommendSetBo bo = new CommendSetBo(this.getFrameconn());
				CommendXMLBo xmlBo=new CommendXMLBo(this.getFrameconn());
				ArrayList list = DataDictionary.getFieldList("P03",Constant.USED_FIELD_SET);
				 ArrayList commendList = new ArrayList();
				 ArrayList candidateList = new ArrayList();
				 String logon_id = this.getUserView().getUserName();
				 String dbpre=this.getUserView().getDbname();
				 String a0100=this.getUserView().getA0100();
				 /*if(this.userView.getStatus()==0)
					 throw GeneralExceptionHandler.Handle(new Exception("业务用户不能参与后备推荐！"));*/
				 String isAdmin="0";
				 LazyDynaBean infoBean=null;
				if(this.getUserView().isAdmin()&& "1".equals(this.getUserView().getGroupId())){
					isAdmin="1";
					infoBean = new LazyDynaBean();
				}else
				{		
				  infoBean= bo.getUserInfo(dbpre,a0100);
				}
				 String onlyOne="";
				 boolean flag=false;
				 String ctrl_param="";
				 String p0203="";
				 String p0201="";
				 String commend_field="";
				 String commend_field_codesetid="";
				try{
					
					int i=bo.haveOneOrMoreRecord("05");
					if(i==0 || i==1){
						if(i==0){
							onlyOne="0";
						}
						if(i==1){
							LazyDynaBean abean=bo.getOnlyOneRecord();
							onlyOne="1";
							p0203=(String)abean.get("p0203");
							p0201=(String)abean.get("p0201");
							ctrl_param = xmlBo.getCtrl_paraValue((String)abean.get("p0201"),CommendXMLBo.vote_count);
							commend_field=xmlBo.getCtrl_paraValue(p0201,CommendXMLBo.commend_field);
							commend_field_codesetid=bo.getCommendFieldCodesetid(list,commend_field);
							candidateList = bo.getCandidateList(isAdmin,(String)abean.get("p0201"),(String)infoBean.get("b0110"),(String)infoBean.get("e0122"),logon_id,commend_field,commend_field_codesetid);
							 flag = bo.isSelected(logon_id,p0201);
							
						}
						
					}else{
						onlyOne="2";
						p0201=bo.getFirstRecord("05");
						ctrl_param=xmlBo.getCtrl_paraValue(p0201,CommendXMLBo.vote_count);
					    commendList = bo.getInsupportCommendList();
						commend_field=xmlBo.getCtrl_paraValue(p0201,CommendXMLBo.commend_field);
						commend_field_codesetid=bo.getCommendFieldCodesetid(list,commend_field);
					    candidateList=bo.getCandidateList(isAdmin,p0201,(String)infoBean.get("b0110"),(String)infoBean.get("e0122"),logon_id,commend_field,commend_field_codesetid);
					    flag = bo.isSelected(logon_id,p0201);
					}
					String isSubmit="";
					if(flag){
						isSubmit="1";
					}else{
						isSubmit="2";
					}
					String isNull="";
					if(bo.isNull(commend_field))
						isNull="yes";
					else
						isNull="no";
					this.getFormHM().put("isNull",isNull);
					this.getFormHM().put("candidateList",candidateList);
					this.getFormHM().put("commendList",commendList);
					this.getFormHM().put("p0203",p0203);
					this.getFormHM().put("p0201",p0201);
					this.getFormHM().put("isSubmit",isSubmit);
					this.getFormHM().put("onlyOne",onlyOne);
					this.getFormHM().put("ctrl_param",ctrl_param);
					this.getFormHM().put("size",String.valueOf(candidateList.size()));
					this.getFormHM().put("codesetid",commend_field_codesetid);
					
				}catch(Exception e){
					e.printStackTrace();
				}
				
	}
	


}
