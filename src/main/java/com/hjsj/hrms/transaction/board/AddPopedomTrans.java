package com.hjsj.hrms.transaction.board;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *<p>Title:AddPopedomTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jun 5, 2009:1:31:08 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class AddPopedomTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String usrnames = (String)this.getFormHM().get("usrnames");
		ArrayList list = (ArrayList)this.getFormHM().get("selectedlist");
		if(usrnames==null||list==null)
			return;
		if(usrnames.length()<=0||list.size()<=0)
			return;
		String usrname[] = usrnames.split(",");
		StringBuffer str_value = new StringBuffer();
		for(int i=0;i<usrname.length;i++){
			if("@K".equalsIgnoreCase(usrname[i].substring(0,2))|| "UN".equalsIgnoreCase(usrname[i].substring(0,2))|| "UM".equalsIgnoreCase(usrname[i].substring(0,2)))
				continue;
			String roleid = usrname[i];
			//System.out.println(roleid);
			SysPrivBo privbo=new SysPrivBo(roleid,GeneralConstant.EMPLOYEE,this.getFrameconn(),"warnpriv");
			String res_str=privbo.getWarn_str();
			//System.out.println(res_str);
			ResourceParser parser=new ResourceParser(res_str,IResourceConstant.ANNOUNCE);
			String content=parser.getContent();
			String ct="";
			if(content==null||content.length()<=0)
			{
				content="";
			}else
			{
				 str_value.append(content+",");
			}
			ct=","+content+",";
			for(int j=0;j<list.size();j++){
				RecordVo vo = (RecordVo)list.get(j);
				String id = vo.getString("id");
				if(ct.indexOf(id+",")==-1)
				  str_value.append(id+",");
			}
			
			if(str_value.length()!=0)
				str_value.setLength(str_value.length()-1);
			parser.reSetContent(str_value.toString());
			res_str=parser.outResourceContent();
			//System.out.println(res_str);
			privbo.saveResourceString(roleid,GeneralConstant.EMPLOYEE,res_str);
			str_value.delete(0,str_value.length());
		}
		
	}
}
