package com.hjsj.hrms.transaction.mobileapp.salary;

import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title: SalaryTrans </p>
 * <p>Description: 薪酬交易类</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2014-9-24 下午2:51:10</p>
 * @author yangj
 * @version 1.0
 */
public class SalaryTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	/** 获得页签 */
	private final String INIT_PAGES = "1";
	
	public void execute() throws GeneralException {
		String message = "";
		String succeed = "false";
		HashMap hm = this.getFormHM();
		try {
			String transType = (String) hm.get("transType");
			hm.remove("message");
			// 不同业务流程分支点
			if (transType != null) {	
				if (INIT_PAGES.equals(transType)) {
					this.initPages(hm);
					succeed = "true";
				}
			} else {
				//异常
				message = ResourceFactory.getProperty("mobileapp.salary.error.transTypeError");
				hm.put("message", message);
			}
		} catch (Exception e) {
			succeed = "false";
            String errorMsg=e.toString();
            int index_i=errorMsg.indexOf("description:");
            message=errorMsg.substring(index_i+12);
            hm.put("message", message);
            e.printStackTrace();
            this.cat.error(e.getMessage());
		} finally {
			hm.put("succeed", succeed);
		}

	}

	/**
	 * 
	 * @Title: getPages   
	 * @Description: 获得薪酬表的页签  
	 * @param hm 
	 * @return void
	 */
	private void initPages(HashMap hm) {
		String selfInfoFlag = (String) hm.get("selfInfoFlag");
		String a0100 = (String) hm.get("a0100");
		String dbname = (String) hm.get("dbname");
		String userOrgId = (String) hm.get("userOrgId");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		CardConstantSet cardConstantSet = new CardConstantSet(this.userView, this.getFrameconn());
		String relating = cardConstantSet.getSearchRelating(dao);
		String b0110 = cardConstantSet.getRelatingValue(dao, a0100, dbname, relating, userOrgId);
		XmlParameter xml = new XmlParameter("UN", b0110, "00");
		xml.ReadOutParameterXml("SS_SETCARD", this.getFrameconn());
		String flag = xml.getFlag();
		ArrayList cardidlist = new ArrayList();
		if ("yes".equals(selfInfoFlag)) {
			cardidlist = cardConstantSet.setCardidSelectSelfinfo(this.getFrameconn(), this.userView, flag, dbname, a0100, b0110, true);
		} else {
			cardidlist = cardConstantSet.setCardidSelect(this.getFrameconn(), this.userView, flag, dbname, a0100, b0110, true);
		}
		hm.put("cardidlist", cardidlist);
	}

}
