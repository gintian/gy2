package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchDataTableTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
			ArrayList tableList=bo.getTableList();
			ArrayList fieldSetList = bo.getFieldSetList();
			int tableListSize=0;
			int fieldSetListSize=0;
			if(tableList!=null)
			{
				tableListSize=tableList.size();
			}
			if(fieldSetList!=null)
			{
				fieldSetListSize=fieldSetList.size();
			}
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";
			if(vo!=null)
				dbname=vo.getString("str_value");
			this.getFormHM().put("tableNames",dbname);
			this.getFormHM().put("tableList", tableList);
			this.getFormHM().put("fieldSetList", fieldSetList);
			this.getFormHM().put("tableListSize", tableListSize+"");
			this.getFormHM().put("fieldSetListSize", fieldSetListSize+"");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
