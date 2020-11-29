package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeEvaluationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/***
 * 添加评价信息
 * <p>Title: AddEvaluationTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-8-11 下午05:00:33</p>
 * @author xiexd
 * @version 1.0
 */
public class AddEvaluationTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			
			String nbase_o = (String)this.getFormHM().get("nbase");
			String a0100_o = (String)this.getFormHM().get("a0100");
			String content = (String)this.getFormHM().get("content");
			String score = (String)this.getFormHM().get("score");
			HashMap map = new HashMap();
			map.put("nbase_object",PubFunc.decrypt(nbase_o ));
			map.put("a0100_object",PubFunc.decrypt(a0100_o ));
			map.put("score", score);
			map.put("content", "".equals(content)?"未填写评语":content );
			map.put("nbase", this.userView.getDbname());
			map.put("a0100", this.userView.getA0100());
			
			ResumeEvaluationBo bo = new ResumeEvaluationBo(this.frameconn, this.userView);
			ArrayList list = bo.getEvaluationList(PubFunc.decrypt(nbase_o), PubFunc.decrypt(a0100_o), this.userView.getDbname(), this.userView.getA0100(),0);//我的评价
			boolean flg = false;
			//添加评价信息
			if(list.size()>0)
			{
				flg = bo.updateEvaluation(map);
			}else{
				flg = bo.addEvaluation(map);
			}
			this.getFormHM().put("flg", flg);
			this.getFormHM().put("content", content);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
