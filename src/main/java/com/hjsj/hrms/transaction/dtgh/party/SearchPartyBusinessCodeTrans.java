/*
 * Created on 2005-12-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.dtgh.party;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author xujian
 *Jan 14, 2010
 */
public class SearchPartyBusinessCodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String param = (String) hm.get("param");
		param = param != null && param.length() > 0 ? param : "Y";
		
		if(hm.containsKey("backdate"))
			 this.getFormHM().put("backdate", hm.get("backdate"));
		else
			this.getFormHM().put("backdate",DateStyle.getSystemTime().substring(0,10));
		
		try {
			
			String codesetid = "64";
			if("Y".equalsIgnoreCase(param)){
				codesetid = "64";
			}
			if("V".equalsIgnoreCase(param)){//团务
				codesetid = "65";
			}
			if("W".equalsIgnoreCase(param)){//工会
				codesetid = "66";
			}
			if("H".equalsIgnoreCase(param)){//基准岗位
				RecordVo constantuser_vo = ConstantParamter
				.getRealConstantVo("PS_C_CODE");
				if (constantuser_vo == null) {
					String temp=ResourceFactory.getProperty("pos.posbusiness.nosetposccode");
					throw GeneralExceptionHandler.Handle(new GeneralException("",temp,"", ""));
				}
				codesetid = constantuser_vo.getString("str_value");
				if("".equals(codesetid)|| "#".equals(codesetid)){
					String temp=ResourceFactory.getProperty("pos.posbusiness.nosetposccode");
					throw GeneralExceptionHandler.Handle(new GeneralException("",temp,"", ""));
				}
				RecordVo ps_c_job_vo=ConstantParamter.getRealConstantVo("PS_C_JOB",this.getFrameconn());
				if(ps_c_job_vo!=null)
				{
				  String  ps_c_job=ps_c_job_vo.getString("str_value");
				  ps_c_job=ps_c_job.replaceAll("#", "");
				  this.getFormHM().put("ps_c_job",ps_c_job);
				}else{
					this.getFormHM().put("ps_c_job","");
				}
				
				String backdate = this.getFormHM().get("backdate").toString();
				if("".equals(backdate))
					this.getFormHM().put("backdate",DateStyle.getSystemTime().substring(0,10));
			}
			this.getFormHM().put("codesetid", codesetid);
			String sql = "select codesetdesc from codeset where codesetid='"
					+ codesetid + "'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			if (this.frowset.next()){
				this.getFormHM().put("codesetdesc",
						this.frowset.getString("codesetdesc"));
			}else{
				String codesetdesc = ResourceFactory.getProperty("dtgh.party."+param+".codesetdesc");
				codesetdesc = PubFunc.splitString(codesetdesc, 50);
				sql = "insert into  codeSet  (CodeSetId, CodeSetDesc, MaxLength, status,validateflag) values ('"+codesetid+"', '"+codesetdesc+"', 30, '1',1)";
				dao.insert(sql, new ArrayList());
			}
			
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
			String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
	    	if(uplevel==null||uplevel.length()==0)
	    		uplevel="0";
	    	this.getFormHM().put("uplevel", uplevel);
	    	this.getFormHM().put("expr", "");
			this.getFormHM().put("factor", "");
			this.getFormHM().put("likeflag", "");
			
			
			
			/**
			 * 是否显示岗位说明书
			 */
			String Csql="select str_value from constant where upper(constant)='PS_C_CARD_ATTACH'";
			/*RecordVo vo = new RecordVo("CONSTANT");
			vo.setString("constant", "PS_CARD_ATTACH");*/
			dao=new ContentDAO(this.getFrameconn());
			String value="";
			try {
				this.frowset=dao.search(Csql);
				if(this.frowset.next())
				{
					value=this.frowset.getString("str_value");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			 if(value != null && value.trim().length()>0){
				 String zp_job_template=null;
				 Csql = "select str_value from constant where upper(constant)='ZP_JOB_TEMPLATE'";
				 this.frowset = dao.search(Csql);
				 if(this.frowset.next())
					  zp_job_template=this.frowset.getString("str_value");
				 zp_job_template = zp_job_template == null?"":zp_job_template;
				 this.getFormHM().put("zp_job_template", zp_job_template);
			 }else
				 value="false";
			 
		    this.getFormHM().put("ps_c_card_attach", value);
		    
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
}
