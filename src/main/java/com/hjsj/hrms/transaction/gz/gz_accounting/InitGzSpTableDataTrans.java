package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class InitGzSpTableDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		this.getFormHM().put("bosdate","");
		this.getFormHM().put("count","");
		String salaryid=(String)this.getFormHM().get("salaryid");
		SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),Integer.parseInt(salaryid));
		String priv=ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
		String gz_module = ctrlparam.getCstate(salaryid);//判断是薪资还是保险  zhaoxg add
		this.getFormHM().put("priv",priv);
		this.getFormHM().put("appUser","");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		
		if(hm.get("fromModel")!=null&& "wdxx".equals((String)hm.get("fromModel")))
		{
		//	this.getFormHM().put("sp_flag","02");
		//	hm.remove("fromModel");
		}
		else
			this.getFormHM().put("sp_flag","");
		
		String returnflag=(String)hm.get("returnflag");
		 
		//如果用户没有当前薪资类别的资源权限   20140903  dengcan
		CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
		safeBo.isSalarySetResource(salaryid,gz_module);
		
		
		this.getFormHM().put("returnflag",returnflag);
		ArrayList datelist=getOperationDateListSP(Integer.parseInt(salaryid));
		
		this.getFormHM().put("condid", "all");
		this.getFormHM().put("empfiltersql", "");
		
		
		this.getFormHM().put("datelist", datelist);
		this.getFormHM().put("sp_ori",(String)hm.get("ori"));
		this.getFormHM().put("gz_module", gz_module);
	}
	
	/**
	 * 获取薪资审批的业务日期  zhaoxg add 2014-5-28
	 * @return
	 */
	public ArrayList getOperationDateListSP(int salaryid)
	{
		ArrayList list=new ArrayList();
		try
		{
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),salaryid,this.userView);
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct A00Z2 from salaryhistory where ");
			buf.append(" (  ( ( (AppUser is null  "+gzbo.getPrivWhlStr("")+"  )  or AppUser Like '%;"+this.userView.getUserName()+";%' ) and  ( sp_flag='06' or  sp_flag='03' ) ) or curr_user='"+this.userView.getUserName()+"') and salaryid='"+salaryid+"'  order by A00Z2 desc");
			ContentDAO dao=new ContentDAO(this.frameconn);
			RowSet rset=dao.search(buf.toString());
			CommonData temp=null;
			while(rset.next())
			{
				String strdate=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy.MM.dd");
				temp=new CommonData(strdate,strdate);
				list.add(temp);
			}
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}

}
