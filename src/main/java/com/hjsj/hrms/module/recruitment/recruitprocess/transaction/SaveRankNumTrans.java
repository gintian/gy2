package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.RecruitProcessBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveRankNumTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			RecruitProcessBo bo = new RecruitProcessBo(this.frameconn, this.userView);
			String flag = (String) this.getFormHM().get("flag");
			String outName = "";
			String z0301 = (String) this.getFormHM().get("z0301");

			if ("1".equalsIgnoreCase(flag)) {
				String linkId = (String) this.getFormHM().get("link_id");
				String check = (String) this.getFormHM().get("Check");
				String a0100 = (String) this.getFormHM().get("a0100");
				if("0".equalsIgnoreCase(check)){
					outName = bo.createExcel(linkId, z0301, "false");
				}else{
					outName = bo.createExcel(linkId, z0301, a0100);
				}
				
				outName = PubFunc.encrypt(outName);
				this.getFormHM().put("outName", outName);
				if("false".equalsIgnoreCase(PubFunc.decrypt(outName))){
					this.getFormHM().put("success", false);
					this.getFormHM().put("messages", "请设置简历唯一性指标");
				}else
					this.getFormHM().put("success", true);
			} else if ("2".equalsIgnoreCase(flag)) {
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String fileId = (String) getFormHM().get("fileId");

				// 导入excel，如果报错返回报错信息
				ArrayList<Object> msglist = bo.importRanktion(fileId, z0301);
				ArrayList<Object> msg = (ArrayList<Object>) msglist.get(0);
				String succNumber = (String) msglist.get(1);
				if (msg.size() == 0) {
					this.getFormHM().put("msg", "false");
					if (msglist.size() > 1) {
					    succNumber = (String) msglist.get(1);
						this.getFormHM().put("succNumber", succNumber.toString());
					}
				} else {
					StringBuffer msgs = new StringBuffer("[");
					for (int i = 0; i < msg.size(); i++) {
						msgs.append("{data:'" + (String) msg.get(i) + "'},");
					}

					if (msgs.toString().endsWith(","))
						msgs.setLength(msgs.length() - 1);

					msgs.append("]");
					this.getFormHM().put("msg", msgs.toString());
					if ("false".equalsIgnoreCase(succNumber)) {
						this.getFormHM().put("dataNumber", "0");
					} else
						this.getFormHM().put("dataNumber", succNumber);

				}
			} else if ("3".equalsIgnoreCase(flag)) {
				ArrayList rankNums = (ArrayList) this.userView.getHm().get("rankNums");
				ArrayList a0100s = (ArrayList) this.userView.getHm().get("a0100s");
				z0301 = PubFunc.decrypt(z0301);
				
				// 导入excel数据进入数据库
				String msgs = bo.importExcel(rankNums, a0100s, z0301);
				this.getFormHM().put("succNumber", msgs.toString());
			} else {
				String a0100_es = (String) this.getFormHM().get("a0100_es");
				String rank_nums = (String) this.getFormHM().get("rank_nums");
				bo.saveRankNum(z0301, a0100_es, rank_nums);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
