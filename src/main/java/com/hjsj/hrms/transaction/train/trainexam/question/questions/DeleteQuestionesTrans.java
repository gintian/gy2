package com.hjsj.hrms.transaction.train.trainexam.question.questions;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>
 * Title:SearchQuestionesTrans
 * </p>
 * <p>
 * Description:保存试题信息
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-10-18
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class DeleteQuestionesTrans extends IBusiness {

	public void execute() throws GeneralException {//知识点、试题分类、试题类型、难度、分数为必填项。
		HashMap map = (HashMap) this.getFormHM().get("requestPamaHM");		
		
		String ids = (String) map.get("ids");
		if(ids == null || "".equalsIgnoreCase(ids))
		    return;
		String[] qids = ids.split(",");
		ids = "";
		for(int i = 0; i< qids.length; i++){
		    String qid = PubFunc.decrypt(SafeCode.decode(qids[i]));
		    ids += "," + qid;
		}
		ids = ids.substring(1);
		String sql = "delete from tr_test_knowledge where r5200 in ("+ids+")";
		String sql2 = "delete from r52 where r5200 in ("+ids+")";
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		try {
			dao.update(sql2);
			dao.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
