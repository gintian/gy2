package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Hashtable;

public class SearchKhFieldTreeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
		
			String b0110 ="HJSJ";
			KhFieldBo bo = new KhFieldBo(this.getFrameconn());
			if(this.userView.getStatus()==0)
			{
				if(this.userView.getManagePrivCodeValue()!=null&&!"".equals(this.userView.getManagePrivCode()))
				{
					b0110 = this.userView.getManagePrivCodeValue();
				}
			}
			else if(this.userView.getStatus()==4)
			{
				String a0100=this.userView.getA0100();
				String pre = this.userView.getDbname();
				String unit=bo.getB0110(pre, a0100);
				if(unit!=null&&!"".equals(unit))
				{
					b0110 = unit;
				}
			}
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String returnflag="";
			if(map.get("returnflag")!=null)
			{
				returnflag=(String)map.get("returnflag");
			}else
			{
				returnflag=(String)map.get("returnflag");
			}
			this.getFormHM().put("returnflag", returnflag==null?"":returnflag);
			String subsys_id = (String)map.get("subsys_id");
			TreeItemView treeItem=new TreeItemView();
			treeItem.setName("root");
			treeItem.setRootdesc("root");
			treeItem.setTitle("root");
			treeItem.setIcon("/images/add_all.gif");	
			treeItem.setTarget("mil_body");
			String rootdesc=ResourceFactory.getProperty("kh.field.class");
			//root.setAttribute("href","/performance/kh_system/kh_field/init_kh_iframe.do?b_query=link&pointsetid=-1");
		   
		    treeItem.setRootdesc(rootdesc);
			treeItem.setText(rootdesc); 
		    treeItem.setLoadChieldAction("/servlet/performance/KhFieldTree?pointsetid=-1&subsys_id="+subsys_id+"&b0110="+b0110);
		    treeItem.setAction("/performance/kh_system/kh_field/init_kh_iframe.do?b_query=link&pointsetid=-1&entery=1");
		    
		    AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
		    appb.init();
		    appb.setReturnHt(null);
		    Hashtable ht=appb.analyseParameterXml();
		    String pointset_menu=(String)ht.get("pointset_menu");
		    String pointcode_menu=(String)ht.get("pointcode_menu");
		    String pointname_menu=(String)ht.get("pointname_menu");
		    DbWizard dbwizard=new DbWizard(this.getFrameconn());
		    if(dbwizard.isExistTable(pointset_menu,false)){
		    	  this.getFormHM().put("orgpoint", pointset_menu);
		    }else{
		    	 this.getFormHM().put("orgpoint", "");
		    }
		    this.getFormHM().put("khpid", pointcode_menu);
		    this.getFormHM().put("khpname", pointname_menu);
		    this.getFormHM().put("tree",treeItem.toJS());
		    this.getFormHM().put("subsys_id",subsys_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
