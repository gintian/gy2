package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class OpenRosterTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");		
		treeItem.setIcon("/images/add_all.gif");	
		treeItem.setTarget("nil_body");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        /**各模块调用花名册的太多，此参数判断是显示返回按钮还是显示关闭按钮=1的时候，是弹出窗口，不显示返回按钮，显示关闭按钮=0显示返回按钮*/
	    String closeWindow=(String)hm.get("closeWindow");
	    if(closeWindow==null)
	    {
	    	closeWindow="0";
	    }
	    else
	    {
	    	hm.remove("closeWindow");
	    }
	    String returnflag="";
	    if(hm.get("returnflag")!=null)
	    {
	    	returnflag=(String)hm.get("returnflag");
	    }
	    else
	    {
	    	returnflag=(String)this.getFormHM().get("returnflag");
	    }
	    this.getFormHM().put("returnflag", returnflag==null?"":returnflag);
	    String a_inforkind=(String)hm.get("a_inforkind");
	    a_inforkind=a_inforkind!=null&&a_inforkind.trim().length()>0?a_inforkind:"1";
	    hm.remove("a_inforkind");
	    
	    String a_hflag=(String)hm.get("a_hflag");
	    a_hflag=a_hflag!=null&&a_hflag.trim().length()>0?a_hflag:"0";
	    hm.remove("a_hflag");
	    
	    this.getFormHM().put("inforkind",a_inforkind);
	    this.getFormHM().put("infor_Flag",a_inforkind);
	    this.getFormHM().put("hflag",a_hflag);
	    this.getFormHM().put("closeWindow", closeWindow);
		String rootdesc=ResourceFactory.getProperty("infor.menu.outmuster");
	    treeItem.setRootdesc(rootdesc);
	    treeItem.setLoadChieldAction("/general/muster/hmuster/searchrostertree?flag=1&moduleflag=&flaga="+a_inforkind);
	    treeItem.setAction("javascript:void(0)");	   
	    this.getFormHM().put("treeCode",treeItem.toJS());
	    
	    String a_code = this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
	    a_code=a_code!=null?a_code:"";
	    
	    this.getFormHM().put("a_code",a_code);
	    this.getFormHM().put("dbpre","Usr");
	    
	    String result=(String)hm.get("result");
	    result=result!=null&&result.trim().length()>0?result:"0";
	    hm.remove("result");
	    this.getFormHM().put("result",result);
	    
	    String temp=this.userView.getResourceString(4);
	    if(temp==null||temp.length()==0)
	       temp="-1";
	    StringBuffer sqlstr = new StringBuffer();
	    sqlstr.append("SELECT  tabid from lname where Flag='");
	    sqlstr.append(a_inforkind);
	    if(!(this.userView.isAdmin()&& "1".equals(this.userView.getGroupId()))){
	    	sqlstr.append("' and tabid in (");   
			sqlstr.append(temp); 
			sqlstr.append(") order by  norder");
		    //if(temp==null||temp.length()==0)
		    	//throw new GeneralException(ResourceFactory.getProperty("workdiary.message.roster.not.comp")+"!");				
		}
	    else
		{
			
	    	sqlstr.append("' order by  norder"); 
		}
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try {
    	
			this.frowset=dao.search(sqlstr.toString());
			boolean istabid=false;
			String[] tabid = new String[1];
			if(this.frowset.next()){
        			 tabid[0] = this.frowset.getString("tabid");
        		     this.getFormHM().put("tabid",tabid);
        		     istabid=true;
        	}
			if(!istabid){
				tabid[0] = "-1";
        		this.getFormHM().put("tabid",tabid);
        	}
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

}
