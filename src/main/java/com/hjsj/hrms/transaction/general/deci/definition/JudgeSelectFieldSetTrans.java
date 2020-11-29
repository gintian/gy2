/**
 * 
 */
package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>
 * Title:JudgeSelectFieldSetTrans
 * </p>
 * <p>
 * Description:判断选择指标集是否已添加
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class JudgeSelectFieldSetTrans extends IBusiness {

	/**
	 * 
	 */
	public JudgeSelectFieldSetTrans() {
	}


	public void execute() throws GeneralException {
		String sequenceName = (String) this.getFormHM().get("sequence_name");
		String flag = "0";
		String sql = "select * from id_factory where upper(sequence_name)='"+sequenceName.toUpperCase()+"'";
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				flag ="1";
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("flag", flag);
		}
	}

}
