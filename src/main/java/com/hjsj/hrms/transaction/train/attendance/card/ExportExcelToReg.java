package com.hjsj.hrms.transaction.train.attendance.card;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.attendance.ExportExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExportExcelToReg extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String classplan = (String) this.getFormHM().get("classplan");
        if(classplan != null && classplan.length() > 0)
            classplan = PubFunc.decrypt(SafeCode.decode(classplan));
		String columns = (String) this.userView.getHm().get("train_columns");
		String sql = (String) this.userView.getHm().get("train_sql");
		sql=PubFunc.keyWord_reback(sql);
		String title = "培训学员签到记录";
		DbWizard db=new DbWizard(this.getFrameconn());
		boolean isno = db.isExistField("r31", "r3133", false);
		StringBuffer colStr = new StringBuffer();
        ConstantXml constantbo = new ConstantXml(this.getFrameconn(), "TR_PARAM");
		String card_no = constantbo.getTextValue("/param/attendance/card_no");// 获得考勤卡字段名称
		sql=sql.replaceAll("b0110,e0122,", "'' r3130,b0110,e0122,'' r3126,");
		sql=sql.replaceAll(",card_type,",",CASE card_type WHEN 1 THEN '签到' WHEN 2 THEN '签退' WHEN 3 THEN '补签到' WHEN 4 THEN '补签退' ELSE '' END card_type,");
		if(isno){
			sql=sql.replaceAll("leave_early,late_for,", "CASE WHEN leave_early > 0 THEN '早退' WHEN late_for > 0 THEN '迟到' ELSE '正常' END state,'' r3133,");
			columns = "r3130,b0110,e0122,r3126,a0101," + card_no + ",card_time,carddate,card_type,state,r3133";
		}else{
			sql=sql.replaceAll("leave_early,late_for,", "CASE WHEN leave_early > 0 THEN '早退' WHEN late_for > 0 THEN '迟到' ELSE '正常' END state,");
			columns = "r3130,b0110,e0122,r3126,a0101," + card_no + ",card_time,carddate,card_type,state";
		}
		String[] cols = columns.split(",");
		if (cols != null && cols.length > 1) {
			for (int i = 0; i < cols.length; i++) {
				if ("b0110".equalsIgnoreCase(cols[i])) {
					colStr.append("培训班,单位,");
				} else if ("e0122".equalsIgnoreCase(cols[i])) {
					colStr.append("部门,培训地点,");
				} else if ("a0101".equalsIgnoreCase(cols[i])) {
					colStr.append("姓名,");
				} else if (card_no.equalsIgnoreCase(cols[i])) {
					colStr.append("卡号,");
				} else if ("card_time".equalsIgnoreCase(cols[i])) {
					colStr.append("日期,");
					colStr.append("时间,");
				} else if ("card_type".equalsIgnoreCase(cols[i])) {
					colStr.append("类别,");
				} else if ("state".equalsIgnoreCase(cols[i])) {
					if(isno)
						colStr.append("状态,是否计划内");
					else
						colStr.append("状态");
				}
			}
			
		}
		ExportExcelBo exl = new ExportExcelBo(classplan);
		try {
			String filename = exl.ExportExcel(this.getFrameconn(), title, columns, colStr.toString(), sql, this.userView);
			this.getFormHM().put("filename", PubFunc.encrypt(filename));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}