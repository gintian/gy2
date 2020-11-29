package com.hjsj.hrms.module.questionnaire.template.transaction;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;

/**
 * 查询代码类集合
 * @author guodd 2015-09-09
 *
 */
public class SearchCodeSetsTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {

		   String sql = "select codesetid,codesetdesc from codeset order by codesetid ";
		   List list = ExecuteSQL.executeMyQuery(sql);
		   
		   this.getFormHM().clear();
		   this.getFormHM().put("codeSetList", list);
 
	}

}
