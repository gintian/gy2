package com.hjsj.hrms.transaction.performance.commend.choosecandidate;

import com.hjsj.hrms.businessobject.performance.commend.CommendSetBo;
import com.hjsj.hrms.businessobject.performance.commend.CommendXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveChooseCandidateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String ids="";
			HashMap map=(HashMap)this.getFormHM().get("requestPamaHM");
			if(map!=null)
				ids=(String)map.get("ids");
			String opt=(String)map.get("opt");
			String p0201=(String)this.getFormHM().get("p0201");
			String codesetid=(String)this.getFormHM().get("codesetid");
			String logon_id=this.userView.getUserName();
			CommendSetBo bo = new CommendSetBo(this.getFrameconn());
			String dbpre=this.userView.getDbname();
			if("select".equals(opt))
			{
		      String[] choose_per=null;
		      if(ids.indexOf(",") !=-1){
			      choose_per=ids.substring(1).split(",");
		      }
			   bo.saveUserChooseCandidate(choose_per,logon_id,p0201);
			}else if("disselect".equals(opt))
			{
				bo.insertDisclaim(logon_id,p0201);
			}
			
			//--------------------------------------------------------------------
			CommendXMLBo xmlBo=new CommendXMLBo(this.getFrameconn());
			 ArrayList commendList = new ArrayList();
			 ArrayList candidateList = new ArrayList();
			 String a0100=this.getUserView().getA0100();
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
			 String commend_field="";
				int i=bo.haveOneOrMoreRecord("05");
				if(i==0 || i==1){
					if(i==0){
						onlyOne="0";
					}
					if(i==1){
						LazyDynaBean abean=bo.getOnlyOneRecord();
						onlyOne="1";
						p0203=(String)abean.get("p0203");
						//p0201=(String)abean.get("p0201");
						ctrl_param = xmlBo.getCtrl_paraValue(p0201,CommendXMLBo.vote_count);
						commend_field=xmlBo.getCtrl_paraValue(p0201,CommendXMLBo.commend_field);
						//codesetid=bo.getCommendFieldCodesetid(list,commend_field);
						candidateList = bo.getCandidateList(isAdmin,p0201,(String)infoBean.get("b0110"),(String)infoBean.get("e0122"),logon_id,commend_field,codesetid);
						 flag = bo.isSelected(logon_id,p0201);
						
					}
					
				}else{
					onlyOne="2";
					ctrl_param=xmlBo.getCtrl_paraValue(p0201,CommendXMLBo.vote_count);
				    commendList = bo.getInsupportCommendList();
					commend_field=xmlBo.getCtrl_paraValue(p0201,CommendXMLBo.commend_field);
					//codesetid=bo.getCommendFieldCodesetid(list,commend_field);
				    candidateList=bo.getCandidateList(isAdmin,p0201,(String)infoBean.get("b0110"),(String)infoBean.get("e0122"),logon_id,commend_field,codesetid);
				    flag = bo.isSelected(logon_id,p0201);
				}
				String isSubmit="";
				if(flag){
					isSubmit="1";
				}else{
					isSubmit="2";
				}
				this.getFormHM().put("candidateList",candidateList);
				this.getFormHM().put("commendList",commendList);
				this.getFormHM().put("p0203",p0203);
				this.getFormHM().put("p0201",p0201);
				this.getFormHM().put("isSubmit",isSubmit);
				this.getFormHM().put("onlyOne",onlyOne);
				this.getFormHM().put("ctrl_param",ctrl_param);
				this.getFormHM().put("size",String.valueOf(candidateList.size()));

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
