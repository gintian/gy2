package com.hjsj.hrms.module.questionnaire.template.transaction;

import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: QueryTemplateLibraryTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-10-10 下午2:44:52</p>
 * @author guodd
 * @version 1.0
 */
public class QueryTemplateLibraryTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	@Override
    public void execute() throws GeneralException {
          String subModuleId = "qn_template_00002";
          TableDataConfigCache cache = (TableDataConfigCache)userView.getHm().get(subModuleId);
          StringBuffer querySql = new StringBuffer();
          if(this.formHM.containsKey("state")){
        	      String state = (String)this.formHM.get("state");
        	      if("all".equals(state)){
        	    	      querySql.append(" and (1=1) ");
        	      }else{
        	    	  	  querySql.append(" and (qntype='"+state+"') ");
        	      }
        	      cache.setQuerySql(querySql.toString());
        	      return;
          }
          
          if(this.formHM.containsKey("type")){
        	  	List values = (ArrayList) this.getFormHM().get("inputValues");
        	  	if(values==null || values.isEmpty()){
					 cache.setQuerySql("");
					 return;
			}
        	  	querySql.append("and (");
			for(int i=0;i<values.size();i++){
				String value =SafeCode.decode(values.get(i).toString());
				querySql.append(" qnname like '%"+value+"%' or ");
			}
			querySql.append(" 1=2 )");
			cache.setQuerySql(querySql.toString());
          }
	}

}
