package com.hjsj.hrms.transaction.train.trainexam.exam.mytest;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;

/**
 * <p>
 * Title:CheckExamStateTrans
 * </p>
 * <p>
 * Description:检测是否已交卷
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-13 13:28:00
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class CheckExamStateTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		Map map = this.getFormHM();
		// 计划编号
		String r5400 = (String) map.get("planid");
		r5400 = PubFunc.decrypt(SafeCode.decode(r5400));
		// 试卷状态
		String state = "05";
		
		
		// 查询考试模式
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "select r5411 from r54 where r5400="+r5400;
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				state = this.frowset.getString("r5411");	
				state = state == null ? "05" : state;
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.getFormHM().put("state", state);
		
	}

}
