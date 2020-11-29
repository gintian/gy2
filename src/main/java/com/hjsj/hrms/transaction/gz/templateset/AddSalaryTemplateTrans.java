package com.hjsj.hrms.transaction.gz.templateset;

import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;


/**
 * 
 *<p>Title:AddSalaryTemplateTrans</p> 
 *<p>Description:新增工资类别</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 24, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class AddSalaryTemplateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String salarySetName=SafeCode.decode((String)this.getFormHM().get("salarySetName"));
		String isAdd=(String)this.getFormHM().get("isAdd");//判断是否是新增
		if(isAdd==null|| "".equals(isAdd)){
			isAdd="2";
		}
		this.getFormHM().put("isAdd",isAdd);
		try
		{
			FieldItem fielditem=DataDictionary.getFieldItem("a01z0");
			if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag()))
			{
				throw GeneralExceptionHandler.Handle(new Exception("人员主集停发标识（A01Z0）指标没有构库，不能新建工资类别!"));
			}
			
			
			int salaryid=DbNameBo.getPrimaryKey("salarytemplate","salaryid",this.frameconn);  // 取得主键值
			String gz_module=(String)this.getFormHM().get("gz_module");
			SalaryPkgBo pgkbo=new SalaryPkgBo(this.getFrameconn(),this.userView,Integer.parseInt(gz_module));
			pgkbo.addSalaryTemplate(salarySetName);
			//------------------------------新增帐套记入日志  zhaoxg add 2015-4-28--------------------
			StringBuffer context = new StringBuffer();
			context.append("新增（帐套）:"+salarySetName+"("+salaryid+")");
			this.getFormHM().put("@eventlog", context.toString());
			//-------------------------------------------------------------------------------------
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
