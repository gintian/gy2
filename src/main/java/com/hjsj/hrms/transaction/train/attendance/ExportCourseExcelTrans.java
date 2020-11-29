package com.hjsj.hrms.transaction.train.attendance;

import com.hjsj.hrms.businessobject.train.attendance.ExportExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>ExportExcelTrans.java</p>
 * <p>Description:培训考勤导出Excel</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-03-09 上午14:07:55</p>
 * @author LiWeichao
 * @version 5.0
 */
public class ExportCourseExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		String columns=(String)this.userView.getHm().get("train_columns");
		String sql=(String)this.userView.getHm().get("train_sql");
		sql=PubFunc.keyWord_reback(sql);
		String title="培训排课记录";
		columns=columns.replaceAll("id,", "");
		sql=sql.replaceAll("id,", "");
		sql=sql.replaceAll("begin_card,end_card", "CASE begin_card WHEN 1 THEN '是' ELSE '否' END begin_card,CASE end_card WHEN 1 THEN '是' ELSE '否' END end_card");
		StringBuffer colStr=new StringBuffer();
		String[] cols=columns.split(",");
		if(cols!=null&&cols.length>1){
			for (int i = 0; i < cols.length; i++) {
				if("r4101".equalsIgnoreCase(cols[i]))
					colStr.append("培训项目(课程),");
				else if("train_date".equalsIgnoreCase(cols[i]))
					colStr.append("培训日期,");
				else if("begin_time".equalsIgnoreCase(cols[i]))
					colStr.append("上课时间,");
				else if("end_time".equalsIgnoreCase(cols[i]))
					colStr.append("下课时间,");
				else if("class_len".equalsIgnoreCase(cols[i]))
					colStr.append("课时,");
				else if("begin_card".equalsIgnoreCase(cols[i]))
					colStr.append("上课签到,");
				else if("end_card".equalsIgnoreCase(cols[i]))
					colStr.append("下课签退");
			}
		}
		ExportExcelBo exl=new ExportExcelBo();
		try {
			String filename = exl.ExportExcel(this.getFrameconn(), title, columns, colStr.toString(), sql, this.userView);
			this.getFormHM().put("filename", PubFunc.encrypt(filename));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
