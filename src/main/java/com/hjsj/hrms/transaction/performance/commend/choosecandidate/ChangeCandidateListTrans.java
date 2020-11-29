package com.hjsj.hrms.transaction.performance.commend.choosecandidate;

import com.hjsj.hrms.businessobject.performance.commend.CommendSetBo;
import com.hjsj.hrms.businessobject.performance.commend.CommendXMLBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class ChangeCandidateListTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			HashMap map=(HashMap)this.getFormHM().get("requestPamaHM");
			if(map==null)
				return;
			String p0201=(String)map.get("p0201");
			if(p0201==null || p0201.trim().length()<=0)
				return;
			ArrayList alist = DataDictionary.getFieldList("P03",Constant.USED_FIELD_SET);
			CommendXMLBo xmlBo = new CommendXMLBo(this.getFrameconn());
			CommendSetBo setBo = new CommendSetBo(this.getFrameconn());
			String codesetid="";
			String ctrl_param = xmlBo.getCtrl_paraValue(p0201,CommendXMLBo.vote_count);
			String logon_id = this.getUserView().getUserName();
		    String dbpre=this.getUserView().getDbname();
			String a0100=this.getUserView().getA0100();
			 String isAdmin="0";
			 LazyDynaBean infoBean=null;
			if(this.getUserView().isAdmin()&& "1".equals(this.getUserView().getGroupId())){
				isAdmin="1";
				infoBean = new LazyDynaBean();
			}else
			{		
			  infoBean= setBo.getUserInfo(dbpre,a0100);
			}
			ArrayList list =new ArrayList();
			ArrayList commendList = new ArrayList();
			commendList=setBo.getInsupportCommendList();
			String commend_field=xmlBo.getCtrl_paraValue(p0201,CommendXMLBo.commend_field);
			codesetid=setBo.getCommendFieldCodesetid(alist,commend_field);
			list=setBo.getCandidateList(isAdmin,p0201,(String)infoBean.get("b0110"),(String)infoBean.get("e0122"),logon_id,commend_field,codesetid);
			String isSubmit="";
			boolean flag = setBo.isSelected(logon_id,p0201);
			if(flag){
				isSubmit ="1";
				
			}else{
				isSubmit="2";
			}
			String isNull="";
			if(setBo.isNull(commend_field))
				isNull="yes";
			else
				isNull="no";
				
			this.getFormHM().put("candidateList",list);
			this.getFormHM().put("ctrl_param",ctrl_param);
			this.getFormHM().put("isSubmit",isSubmit);
			this.getFormHM().put("p0201",p0201);
			this.getFormHM().put("commendList",commendList);
			this.getFormHM().put("onlyOne","2");
			this.getFormHM().put("codesetid",codesetid);
			this.getFormHM().put("size",String.valueOf(list.size()));
			this.getFormHM().put("isNull",isNull);
			map.remove("p0201");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
