package com.hjsj.hrms.transaction.train.trainexam.question.type;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>
 * Title:SearchQuesTypeTrans
 * </p>
 * <p>
 * Description:查询题型列表
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-10-20
 * </p> 
 * @author zxj
 *
 */
public class SearchQuesTypeTrans extends IBusiness {

	public void execute() throws GeneralException
	{ 			
		StringBuffer columns = new StringBuffer();
		columns.append("type_id,type_name,ques_type,norder");
		
		StringBuffer sql = new StringBuffer();
		sql.append("select "+columns.toString());		

		String where = "from tr_question_type";	
		
		this.getFormHM().put("sqlstr",sql.toString());		
		this.getFormHM().put("column",columns.toString());
		this.getFormHM().put("where",where);
		
		//记录最大顺序号nOrder
		try
		{
			String maxOrder = "0";
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search("SELECT MAX(nOrder) AS maxOrder FROM tr_question_type");
			if (this.frowset.next())
			{
				maxOrder = this.frowset.getString(1);
				if (maxOrder==null)
					maxOrder = "0";
			}
			this.getFormHM().put("maxOrder", maxOrder);				
		    
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		

	}

}
