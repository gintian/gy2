package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.TnameExtendBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;

public class ReportPigeonholeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao=new ContentDAO(this.frameconn);
		
		try
		{
			ArrayList resultList=(ArrayList)this.getFormHM().get("results");
		   	String    paramValue=(String)this.getFormHM().get("param");
		    String    tabid=(String)this.getFormHM().get("tabid");
			 String username = SafeCode.decode((String) this.getFormHM().get("username"));
				if(username==null|| "".equals(username)){
					username = this.userView.getUserName();
				}
				userView=new UserView(username, this.frameconn); 
				userView.canLogin();
		    if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
		    
		   	int    rows=Integer.parseInt((String)this.getFormHM().get("rows"));
		   	int    cols=Integer.parseInt((String)this.getFormHM().get("cols"));
		   	String year=(String)this.getFormHM().get("year");
		    String selfType=(String)this.getFormHM().get("selfType");
		    String reportType=(String)this.getFormHM().get("reportType");
		    String operateObject=(String)this.getFormHM().get("operateObject");
		    String count="";
		    if(Integer.parseInt(reportType)>2)
		    	count=(String)this.getFormHM().get("count");
		    String week="";
		    if(Integer.parseInt(reportType)==6)
		    	week=(String)this.getFormHM().get("week");
		    paramValue = SafeCode.decode(paramValue);
		   	String unitcode="";
		   	if("1".equals((String)this.getFormHM().get("operateObject")))
		   	{
			   	this.frowset=dao.search("select unitcode from operUser where userName='"+this.getUserView().getUserName()+"'");
				if(this.frowset.next())
					unitcode=this.frowset.getString(1);
		   	}
		   	else
		   		unitcode=(String)this.getFormHM().get("appealUnitcode");
		   	TnameExtendBo   tnameExtendBo=new TnameExtendBo(this.frameconn);
		   	TnameBo tnameBo=new TnameBo(this.getFrameconn(),tabid);
		   	String scopeid = (String) this.getFormHM().get("scopeid");
			if(scopeid==null)
				scopeid="0";
			if(!"0".equals(scopeid)){
				String sql = "select * from tscope where scopeid="+scopeid;
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					String scopeownerunitid = this.frowset.getString("owner_unit");
					if(scopeownerunitid.indexOf("UM")!=-1||scopeownerunitid.indexOf("UN")!=-1)
						unitcode = scopeownerunitid.substring(2,scopeownerunitid.length());
				}
			}
			tnameBo.setScopeid(scopeid);
			tnameExtendBo.setScopeid(scopeid);
		   	String    info=tnameExtendBo.ReportPigeonholeTrans(tnameBo,resultList,paramValue,tabid,rows,cols,this.getUserView().getUserId(),this.getUserView().getUserName(),unitcode,year,count,selfType,reportType,operateObject,week);
			this.getFormHM().put("info",info);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
	}

}
