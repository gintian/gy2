package com.hjsj.hrms.module.recruitment.parameter.transaction;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveOrgBriefContentTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException{
		String B0101=(String)this.getFormHM().get("codeitemid");
		String orgFieldIDs ="";
		String contentType="";
		StringBuffer str= new StringBuffer();
		String contentTypeValue="";
		if(((String)this.getFormHM().get("contentTypeValue"))!=null&&((String)this.getFormHM().get("contentTypeValue")).trim().length()>0)
			contentTypeValue = (String)this.getFormHM().get("contentTypeValue");
		String content="";
		if(((String)this.getFormHM().get("content"))!=null && ((String)this.getFormHM().get("content")).trim().length()>0){
			content = (String)this.getFormHM().get("content");
			content=com.hjsj.hrms.utils.PubFunc.keyWord_reback(content);
		}
		String url =""; 
		if(((String)this.getFormHM().get("url"))!=null && ((String)this.getFormHM().get("url")).trim().length()>0)
			url = (String)this.getFormHM().get("url");
		if (url != null) {
			url = url.replace('／', '/');
		}
		//【8187】单位介绍中输入地址，保存后自动会多出空格，导致网页打不开  jingq upd 2015.03.13
		url = PubFunc.keyWord_reback(url);
		ParameterXMLBo bo = new ParameterXMLBo(this.getFrameconn());
		String org_brief=bo.getBriefParaValue();
		if(org_brief != null&& org_brief.trim().length()>0){
			String[] org_brief_Arr=org_brief.split(",");
			orgFieldIDs = org_brief_Arr[0];
			contentType = org_brief_Arr[1];
		}
		str.append("update b01 set ");
		str.append(orgFieldIDs);
		str.append("=?");
		
		str.append(",");
		str.append(contentType+"='");
		str.append(contentTypeValue+"'");
		str.append("where b0110 ='");
		str.append(B0101+"'");
		ArrayList list = new ArrayList();
		if("1".equals(contentTypeValue))
		{
			content=content.replaceAll("&sup1;", "1");
			content=content.replaceAll("&sup2;", "2");
			content=content.replaceAll("&sup3;","3");
			content=content.replaceAll("&ordm;","o");
			content=content.replaceAll("&acirc;","a");
			content=content.replaceAll("&eth;","d");
			content=content.replaceAll("&yacute;","y");
			content=content.replaceAll("&thorn;","t");	
			content=content.replaceAll("&ETH;","D");
			content=content.replaceAll("&THORN;","T");
			content=content.replaceAll("&Yacute;","Y");
            list.add(content);
		}
        if("0".equals(contentTypeValue))
		      list.add(url);
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(str.toString(),list);
		}catch(Exception e){
			e.printStackTrace();
		}
		this.getFormHM().put("isClose","2");
		
	}

}
