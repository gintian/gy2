package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteFilterCondTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String cond_str="";
			String opt=(String)this.getFormHM().get("opt");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String condid=(String)this.getFormHM().get("condid");
			String model=this.getFormHM().get("model")!=null?(String)this.getFormHM().get("model"):"";
			if(!"history".equalsIgnoreCase(model)) {//history 表示为薪资历史数据分析进入
				String str = "select lprogram from salarytemplate where salaryid=" + salaryid;
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.frowset = dao.search(str);
				while (this.frowset.next()) {
					cond_str = this.frowset.getString("lprogram");
				}
				SalaryLProgramBo lbo = new SalaryLProgramBo(cond_str);
				if ("1".equals(opt)) {
					if (condid.indexOf(",") != -1) {
						String[] temp = condid.split(",");
						for (int i = 0; i < temp.length; i++) {
							lbo.removeItem(temp[i]);
						}
					} else {
						lbo.removeItem(condid);
					}
				} else {
					String name = SafeCode.decode((String) this.getFormHM().get("name"));
					lbo.reName(condid, name);
				}
				String newXml = lbo.outPutContent();
				updateLprogram(salaryid, newXml);
			}else{
				HistoryDataBo hbo=new HistoryDataBo(this.getFrameconn(),this.getUserView());
				if ("1".equals(opt)) {
					hbo.deletePersionFilter(condid);
				}else{
					String name = SafeCode.decode((String) this.getFormHM().get("name"));
					hbo.reNamePersionFilter(name,condid);
				}
			}
			this.getFormHM().put("salaryid",salaryid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	private void updateLprogram(String salaryid,String xml)
	{
		try
		{
			String sql = "update salarytemplate set lprogram=? where salaryid="+salaryid;
			ArrayList list = new ArrayList();
			list.add(xml);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(sql,list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
