package com.hjsj.hrms.module.jobtitle.committee.transaction;

import com.hjsj.hrms.module.jobtitle.committee.businessobject.CommitteeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * 资格评审_专家选择控件检索
 * 
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 */
@SuppressWarnings("serial")
public class RandomSelectionEnterTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		int totalNum = Integer.parseInt((String)this.getFormHM().get("totalNum")) ;//总数
		int insideNum = Integer.parseInt((String) this.getFormHM().get("insideNum"));//内部专家数
		int outsideNum = 0;//外部专家数
		if(totalNum > insideNum){
			outsideNum = totalNum - insideNum;
		}
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		
		try {
			ArrayList<String> list = new ArrayList<String>(); 
			CommitteeBo committeeBo = new CommitteeBo(this.getFrameconn(), this.userView);// 工具类
			/** 筛选内部专家 */
			ArrayList<String> insideList = new ArrayList<String>(); 
			insideList = committeeBo.getPersonForRandomSelection("1", insideNum);
			
			/** 筛选外部专家 */
			ArrayList<String> outsideList = new ArrayList<String>(); 
			outsideList = committeeBo.getPersonForRandomSelection("2", outsideNum);
			
			/** 汇总 */
			list.addAll(insideList);
			list.addAll(outsideList);
			this.getFormHM().put("randomSelectionList", list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
