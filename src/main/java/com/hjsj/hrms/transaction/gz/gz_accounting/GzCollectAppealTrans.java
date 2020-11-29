package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:GzCollectAppealTrans.java
 * </p>
 * <p>
 * Description:工资审批报批 or 驳回 or 批准
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Sept 29, 2009
 * </p>
 * 
 * @author xujian
 * @version 4.0
 */
public class GzCollectAppealTrans extends IBusiness {

	public void execute() throws GeneralException {
		String count = null;
		String bosdate = null;
		ArrayList datelist = new ArrayList();
		ArrayList countlist = new ArrayList();
		try {
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String opt = (String) hm.get("opt");
			String approveObject = (String) this.getFormHM().get(
					"approveObject");
			String salaryid = (String) this.getFormHM().get("salaryid");
			bosdate = (String) this.getFormHM().get("bosdate"); // 业务日期(发放日期)
			count = (String) this.getFormHM().get("count"); // 发放次数
			String rejectCause = (String) this.getFormHM().get("rejectCause");
			rejectCause = rejectCause.replaceAll("\r\n", "\n");
			String selectGzRecords = (String) this.getFormHM().get("selectGzRecords");
			selectGzRecords=PubFunc.keyWord_reback(selectGzRecords);
			String sendMen=(String)this.getFormHM().get("sendMen");
			// String sendMen=(String)this.getFormHM().get("sendMen");
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),
					Integer.parseInt(salaryid), this.userView);
			gzbo.gzGradeAppeal_group(approveObject, bosdate, count,
					selectGzRecords, opt, rejectCause, sendMen);
			int index = selectGzRecords.indexOf("'");
			String str = selectGzRecords.substring(index, selectGzRecords
					.trim().length() - 1);
			str = str.replaceAll("like", "");
			str = str.replaceAll("org", "");
			str = str.replaceAll("%", "");
			str = str.replaceAll("or", "");
			str = str.replaceAll("    ", ",");
			str = str.trim();
			/*
			 * Pattern p =Pattern.compile("^.?('.?').?$"); Matcher m =
			 * p.matcher(selectGzRecords); for(int index=0;index<m.groupCount();index++){
			 * String str = m.group(index); }
			 */
			String sp_flag = "02";
			if ("reject".equalsIgnoreCase(opt)) {
				sp_flag = "07";
			} else if ("confirm".equalsIgnoreCase(opt)) {
				sp_flag = "03";
			}
			String sql = null;
			sql = "update gz_sp_report set sp_flag ='" + sp_flag
					+ "' where a00z2=" + Sql_switcher.dateValue(bosdate)
					+ " and a00z3=" + count + " and salaryid=" + salaryid
					+ " and userid='" + this.getUserView().getUserId()
					+ "' and b0110 in(" + str + ")";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(sql);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
