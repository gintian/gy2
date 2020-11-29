package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>
 * Title:SearchLawBaseTrans
 * </p>
 * 
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchLawBaseTrans extends IBusiness {

	public SearchLawBaseTrans() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String a_base_id = (String) hm.get("a_base_id");
		if(!"root".equals(a_base_id)&&a_base_id!="root")
		{
			a_base_id = PubFunc.decrypt(SafeCode.decode(a_base_id));
		}
		if (a_base_id == null || "".equals(a_base_id)) {
			a_base_id = "root";
		}
		String flag = (String) this.getFormHM().get("flag");
		RecordVo vo = new RecordVo("law_base_struct");
		/**
		 * 添加子级目录操作
		 */
		if ("3".equals(flag)) {
			vo.setString("up_base_id", a_base_id);
			vo.setString("status", "1");
			this.getFormHM().put("law_base_vo", vo);
			return;
		}
		/**
		 * 修改子级目录操作
		 */
		else if ("4".equals(flag)) {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				vo.setString("base_id", a_base_id);
				vo = dao.findByPrimaryKey(vo);
				this.getFormHM().put("law_base_vo", vo);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
