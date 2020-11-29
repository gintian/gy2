package com.hjsj.hrms.transaction.kq.register.historical;


import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.history.KqReportInit;
import com.hjsj.hrms.businessobject.kq.register.history.KqViewDailyBo;
import com.hjsj.hrms.businessobject.kq.register.history.KqViewSumBo;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class KqReportViewTran extends IBusiness{
	public void execute()throws GeneralException
	{
		String userbase=(String)this.getFormHM().get("userbase");
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");
		String codesetid =getorg(code);
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String dbdbtype=(String)this.getFormHM().get("dbtype");
		String history = (String)hm.get("history");
  		if("1".equals(history)){
  			dbdbtype = (String)hm.get("userbase");
  			hm.remove("history");
  		}
		if(dbdbtype==null|| "".equals(dbdbtype))
			dbdbtype="all";
		
		if (codesetid == null || codesetid.length() <= 0) {
			if (!this.userView.isSuper_admin()) {
				String privCode=RegisterInitInfoData.getKqPrivCode(userView);
				if("UN".equals(privCode))
			    	kind="2";
			    else if("UM".equals(privCode))
			    	kind="1";
			    else if("@K".equals(privCode))
			    	kind="0";
			}	
		} else {
			if("UN".equals(codesetid))
		    	kind="2";
		    else if("UM".equals(codesetid))
		    	kind="1";
		    else if("@K".equals(codesetid))
		    	kind="0";
		}
//		if(codesetid.equalsIgnoreCase("@K"))
//		{
//			kind="0";
//		}
		
		String coursedate=(String)this.getFormHM().get("coursedate");
		String curpage=(String)this.getFormHM().get("curpage");
		String report_id=(String)this.getFormHM().get("report_id");	
		String self_flag=(String)this.getFormHM().get("self_flag");
		if(coursedate==null||coursedate.length()<=0)
		{
			coursedate=RegisterDate.getKqDuration(this.getFrameconn());
		}		
		if(!userView.isSuper_admin())
		{
			if(kind==null||kind.length()<=0)
			{
				LazyDynaBean bean=RegisterInitInfoData.getKqPrivCodeAndKind(userView);
				code=(String)bean.get("code");
				kind=(String)bean.get("kind");
			}			
		}else
		{
			if(code==null||code.length()<=0)
			{
				ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
				code=managePrivCode.getPrivOrgId(); 
				kind="2"; 
			}
			 
		}			
		if(curpage==null||curpage.length()<=0)
		{
			curpage="1";
		}
	   	if(kind==null||kind.length()<=0)
	   		kind="-2";
	   	if(!"-2".equals(kind)&&(code==null||code.length()<=0))
		{
	   		ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
			code=managePrivCode.getPrivOrgId(); 
			 		   
	    }
	   	if(!"-2".equals(kind)&&(code.length()<RegisterInitInfoData.getKqPrivCodeValue(userView).length()&&"UM".equals(RegisterInitInfoData.getKqPrivCode(userView))))
	    {
		   code=RegisterInitInfoData.getKqPrivCodeValue(userView);
		   kind="1";
	    }else if(!"-2".equals(kind)&&(code.length()<RegisterInitInfoData.getKqPrivCodeValue(userView).length()&&"@K".equals(RegisterInitInfoData.getKqPrivCode(userView))))
	    {
	    	code=RegisterInitInfoData.getKqPrivCodeValue(userView);
			kind="0";
	    }else if(kind==null||kind.length()<=0||code==null||code.length()<=0)
	    {
	    	if(this.userView.getUserDeptId()!=null&&this.userView.getUserDeptId().length()>0)
		    {
//		    	code=this.userView.getUserDeptId();
	    		code=RegisterInitInfoData.getKqPrivCodeValue(userView);
		    	kind="1";
		    }else if(this.userView.getUserOrgId()!=null&&this.userView.getUserOrgId().length()>0)
		    {
//		    	code=this.userView.getUserOrgId();
		    	code=RegisterInitInfoData.getKqPrivCodeValue(userView);
		    	kind="2";
		    }
	    }
		KqReportInit kqReportInit= new KqReportInit(this.getFrameconn());
		ReportParseVo parsevo =kqReportInit.getParseVo(report_id);
		ArrayList tablelist=new ArrayList();		
		if(self_flag==null||self_flag.length()<=0)
			self_flag="";
		KqParameter para=new KqParameter(this.getFormHM(),this.userView,code,this.getFrameconn());
	    String cardn=para.getG_no();
		if("q03".equals(parsevo.getValue().trim())&&!"select".equals(self_flag))
		{
			KqViewDailyBo kqViewDaily = new KqViewDailyBo(this.getFrameconn());
			kqViewDaily.setCardno(cardn);
			kqViewDaily.setSelf_flag(self_flag);
			kqViewDaily.setDbtype(dbdbtype);
			tablelist=kqViewDaily.getKqReportHtml(code,kind,coursedate,curpage,parsevo,userView,this.getFormHM());
		}else if("q03".equals(parsevo.getValue().trim())&& "select".equals(self_flag))
		{
			String whereIN=(String)this.getFormHM().get("wherestr_s");
			KqViewDailyBo kqViewDaily = new KqViewDailyBo(this.getFrameconn());
			kqViewDaily.setCardno(cardn);
			kqViewDaily.setSelf_flag(self_flag);
			kqViewDaily.setWhereIN(whereIN);
			kqViewDaily.setDbtype(dbdbtype);
			tablelist=kqViewDaily.getKqReportHtml(code,kind,coursedate,curpage,parsevo,userView,this.getFormHM());
			this.getFormHM().put("wherestr_s",whereIN);
		}else if("q05".equals(parsevo.getValue().trim()))
		{
			KqViewSumBo kqViewSumBo = new KqViewSumBo(this.getFrameconn());
			kqViewSumBo.setSelf_flag(self_flag);
			tablelist=kqViewSumBo.getKqReportHtml(code,kind,coursedate,curpage,parsevo,userView,this.getFormHM());
		}
		if(tablelist!=null&&tablelist.size()>0)
		{
			String tableHtml=tablelist.get(0).toString();		
			String turnTableHtml=tablelist.get(1).toString();
			this.getFormHM().put("tableHtml",tableHtml);
			this.getFormHM().put("turnTableHtml",turnTableHtml);
		}
		
			
		this.getFormHM().put("userbase",userbase);
		this.getFormHM().put("dbtype", dbdbtype);
		this.getFormHM().put("code",code);
		this.getFormHM().put("codeValue",code);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("coursedate",coursedate);
		this.getFormHM().put("curpage",curpage);
		this.getFormHM().put("report_id",report_id);		
		this.getFormHM().put("self_flag",self_flag);
		this.getFormHM().put("parsevo",parsevo);
		
	}
	public String getorg(String code)
	{
		String codesetid="";
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer();
		sql.append("select codesetid from organization where codeitemid='"+code+"'");
		try
		{
			rowSet=dao.search(sql.toString());
			if(rowSet.next())
			{
				codesetid = rowSet.getString("codesetid");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rowSet!=null)
				try {
					rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return codesetid;
	}
	
    /**
	 *判断是否code为空 
	 * */
	public String getCode(String code)throws GeneralException
	{
		
	   
	   return code;
    }
}
