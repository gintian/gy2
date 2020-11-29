package com.hjsj.hrms.transaction.general.inform.synthesisbrowse;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchSynthesisInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	   String a_code=(String)this.getFormHM().get("a_code");
	   ContentDAO dao=new ContentDAO(this.getFrameconn());
		/*初始化人员库*/
	   getSyn_bound(a_code);
		if(a_code==null|| "".equals(a_code))
			return;
		
		String inforkind="2";
		String pre=a_code.substring(0,2).toLowerCase();
		/**对人员信息群时，过滤单位、部门及职位*/
		if("UN".equalsIgnoreCase(pre)|| "UM".equalsIgnoreCase(pre)|| "@K".equalsIgnoreCase(pre))
		{
			if("UN".equalsIgnoreCase(pre))
				inforkind="2";
			else if("UM".equalsIgnoreCase(pre))
				inforkind="2";
			else if("@K".equalsIgnoreCase(pre))
				inforkind="3";
			CheckPrivSafeBo checkPrivSafeBo = new CheckPrivSafeBo(this.frameconn,userView);
			String orgid=a_code.substring(2);
			orgid=checkPrivSafeBo.checkOrg(orgid, "");
			this.getFormHM().put("a0100",orgid);
			this.getFormHM().put("browse_dbpre",pre);
		}else
		{
			pre=a_code.substring(0,3).toLowerCase();
			CheckPrivSafeBo checkPrivSafeBo = new CheckPrivSafeBo(this.frameconn,userView);
			String a0100=a_code.substring(3);
			pre=checkPrivSafeBo.checkDb(pre);
			a0100=checkPrivSafeBo.checkA0100("", pre, a0100, "");
			this.getFormHM().put("browse_dbpre",pre);
			this.getFormHM().put("a0100",a0100);
			inforkind="1";
		}
		this.getFormHM().put("tabid",getTabid(inforkind,dao));
		this.getFormHM().put("inforkind",inforkind);		
	}
	private String getTabid(String inforkind,ContentDAO dao) throws GeneralException
	{
		String tabid="0";
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT tabid,name FROM Rname where flagA='");
		if("1".equals(inforkind))
			sql.append("A'");
		else if("2".equals(inforkind))
			sql.append("B'");
		else 
			sql.append("K'");
		try{
	      this.frowset=dao.search(sql.toString());
	        while(this.frowset.next())
			{
				tabid=this.frowset.getString("tabid");
				if(this.userView.isHaveResource(IResourceConstant.CARD, tabid))
				{
					break;
				}else
				{
					tabid="";
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
		return tabid;
		
	}
	 public void getSyn_bound(String a_code)
	 {
		    if(a_code==null|| "".equals(a_code))
		    	a_code="";
	    	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
	    	String syn_bound=sysbo.getValue(Sys_Oth_Parameter.SYNTHETIZE_BOUND);
	    	String syn_flag="false";
	    	if(a_code==null||a_code.length()<=0)
	    		a_code="UN";
	    	if(syn_bound==null||syn_bound.length()<=0)
	    		syn_bound="all";
	    	if("all".equalsIgnoreCase(syn_bound))
	    		syn_flag="true";
	    	else if(a_code.indexOf("UN")!=-1&& "b0110".equalsIgnoreCase(syn_bound))
	    		syn_flag="true";
	    	else if(a_code.indexOf("UM")!=-1&& "b0110".equalsIgnoreCase(syn_bound))
	    		syn_flag="true";
	    	else if(a_code.indexOf("@K")!=-1&& "e01a1".equalsIgnoreCase(syn_bound))
	    		syn_flag="true";
	    	else if("a0100".equalsIgnoreCase(syn_bound)){
	    		try
	    		{
	    			ArrayList list=this.userView.getPrivDbList();
	    			for(int i=0;i<list.size();i++)
	    			{
	    				if(a_code.toUpperCase().indexOf(list.get(i).toString().toUpperCase())!=-1)
	    				{
	    					syn_flag="true";
	    					break;
	    				}
	    			}
	    		}catch(Exception e)
	    		{
	    			e.printStackTrace();
	    		}
	    	}
	    	this.getFormHM().put("syn_flag",syn_flag);
	 }

}
