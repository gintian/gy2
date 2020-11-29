package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ShPersonsTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String condid=(String)this.getFormHM().get("condid");
			String a_code=(String)this.getFormHM().get("a_code");
			String reportSQL=(String)this.getFormHM().get("reportSQL");
			reportSQL = PubFunc.decrypt(reportSQL);
			String aisAppealData=(String)this.getFormHM().get("aisAppealData");  //dengcan  报批时传的参数
			String auserid=(String)this.getFormHM().get("auserid");
			String selectID=(String)this.getFormHM().get("selectID");
			String opt=(String)this.getFormHM().get("opt");
			String opt2=(String)this.getFormHM().get("opt2");   //sh_group  汇总审核
			String userid=(String)this.getFormHM().get("userid");
			
			String gz_module=(String)this.getFormHM().get("gz_module");
			String bosdate=(String)this.getFormHM().get("bosdate");
			String count=(String)this.getFormHM().get("count");
			
			String gzSpCollect = (String) this.getFormHM().get("gzSpCollect");//薪资汇总审批
			String collectPoint = (String) this.getFormHM().get("collectPoint");//薪资汇总审批
			if(selectID!=null)
				selectID=selectID.replaceAll("＃", "#").replaceAll("／", "/");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String collectSpSql = "";
			if(gzSpCollect!=null&& "1".equals(gzSpCollect)&&!"sum".equalsIgnoreCase(selectID)){
				collectSpSql = gzbo.getCollectSPPriv(bosdate, count, selectID, collectPoint);
			}
			
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			 
			String msg="";
			if(!getFormula(salaryid))
			{
				msg="0";
				this.getFormHM().put("msg",msg);
				return;
			}
			String type=(String)this.getFormHM().get("type");
			String a00z0="";
			String a00z1="";
			if("1".equals(type))
			{
				a00z0=(String)this.getFormHM().get("a00z0");
				a00z1=(String)this.getFormHM().get("a00z1");
			
				if(opt2!=null&& "sh_group".equalsIgnoreCase(opt2)&&selectID!=null&&selectID.length()>0) //汇总审核
				{
					reportSQL=" and exists ( "+selectID.replaceAll("salaryhistory", "T#"+this.userView.getUserName()+"_gz")+" )";
					
				}
			
			}
			reportSQL+=collectSpSql;
			SalaryTemplateBo bo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.getUserView());
			HashMap  hm = bo.getShPersonsInfo(salaryid, this.getUserView(), condid, a_code,type,a00z1,a00z0,reportSQL);
			ArrayList list = (ArrayList)hm.get("list");
			String filename=(String)hm.get("filename");
			/* 安全问题：文件下载 薪资发放-审核 xiaoyun 2014-9-29 start */
			filename = SafeCode.encode(PubFunc.encrypt(filename));
			/* 安全问题：文件下载 薪资发放-审核 xiaoyun 2014-9-29 end */
			this.getFormHM().put("fileName",filename);
			msg=(String)hm.get("msg");
			this.getFormHM().put("msg",msg);
			
			this.getFormHM().put("aisAppealData",aisAppealData);
			this.getFormHM().put("auserid",auserid);
			
			this.getFormHM().put("selectID",selectID);
			this.getFormHM().put("opt",opt);
			this.getFormHM().put("userid",userid);
			
			this.getFormHM().put("gz_module",gz_module);
			this.getFormHM().put("bosdate",bosdate);
			this.getFormHM().put("count",count);
			this.getFormHM().put("salaryid", salaryid);
			
			//this.getFormHM().put("shPersonList", list);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public boolean getFormula(String salaryid)
	{
		boolean flag=false;
		StringBuffer buf = new StringBuffer("");
		try
		{
			String sql = "select count(chkid)  from hrpchkformula where flag=1 and validflag=1 and tabid='"+salaryid+"'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				if(this.frowset.getInt(1)>0)
					flag=true;
			}
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}

}
