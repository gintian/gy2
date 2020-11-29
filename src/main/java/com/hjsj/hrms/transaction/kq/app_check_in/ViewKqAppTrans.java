/*
 * Created on 2006-2-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author wxh
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ViewKqAppTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
//			String flag = (String) this.getFormHM().get("flag");
			String table = (String) this.getFormHM().get("table");
			String ta = table.toLowerCase();
//			String tcodeid = "";
			ArrayList infolist = (ArrayList) this.getFormHM().get(
					"selectedinfolist");
			String infoStr = "";

			if (infolist.size() == 0)
				throw GeneralExceptionHandler.Handle(new GeneralException("",
						ResourceFactory.getProperty("error.kq.notselect"), "",
						""));

			this.getFormHM().put("infolist", infolist);
			for (int i = 0; i < infolist.size(); i++) {
				LazyDynaBean selectrec = (LazyDynaBean) infolist.get(i);
				String nbase = (String) selectrec.get("nbase");
				String a0100 = (String) selectrec.get("a0100");
				String b0110 = (String) selectrec.get("b0110");
				String e0122 = (String) selectrec.get("e0122");
				String a0101 = (String) selectrec.get("a0101");
				String e01a1 = (String) selectrec.get("e01a1");
				infoStr += "nbase=" + nbase + ",a0100=" + a0100 + ",b0110=" + b0110 + ",e0122="
						+ e0122 + ",a0101=" + a0101 + ",e01a1=" + e01a1 + "'";
			}
			this.getFormHM().put("infoStr", infoStr);
			SearchAllApp searchAllApp = new SearchAllApp();
			
			//szk取默认时间 考勤结束时间小于当前时间 ，取考勤结束时间
			String strDate = RegisterDate.getDefaultDay(this.frameconn);
			
			this.formHM.put("app_start_date", strDate);
			this.formHM.put("app_end_date", "");
//			String ttime[] = tdate[1].split(":");
			this.formHM.put("start_time_h", "00");
			this.formHM.put("start_time_m", "00");
			this.formHM.put("mess", "");
			this.formHM.put("scope_start_time", strDate+" 00:00");
			this.formHM.put("scope_end_time", strDate+" 23:59");
//			if (ta.equalsIgnoreCase("Q11")) {
//				this.getFormHM().put("salist",
//						searchAllApp.getOneList("1", this.getFrameconn()));
//			} else if (ta.equalsIgnoreCase("Q13")) {
//				this.getFormHM().put("salist",
//						searchAllApp.getOneList("3", this.getFrameconn()));
//			} else if (ta.equalsIgnoreCase("Q15")) {
//				this.getFormHM().put("salist",
//						searchAllApp.getOneList("0", this.getFrameconn()));
//			}
			this.getFormHM().put("salist",searchAllApp.getTableList(table, this.getFrameconn()));
			this.formHM.put("table", table);
			String nbase = (String) this.getFormHM().get("dbpre");
			this.formHM.put("dbpre", nbase);

			KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
//			ArrayList list = kqUtilsClass.getKqClassList();
			ArrayList list = kqUtilsClass.getKqClassListInPriv();
			ArrayList class_list = new ArrayList();
			LazyDynaBean ldb = new LazyDynaBean();
			CommonData da = new CommonData();
	        da.setDataName("<无>");
	        da.setDataValue("#");
	        class_list.add(da);
	        for (int i = 0; i < list.size(); i++) {
	            String onduty = "";
	            String offduty = "";
	            ldb = (LazyDynaBean) list.get(i);
	            if("0".equals((String)ldb.get("classId"))){
	                continue;
	            }
	            da = new CommonData();
	            onduty = (String) ldb.get("onduty_1");
	            for (int j = 3; j > 0; j--) {
	                offduty = (String) ldb.get("offduty_" + j);
	                if (offduty != null && offduty.length() == 5)
	                    break;
	            }
	            if (onduty != null && onduty.trim().length() > 0 && offduty != null && offduty.trim().length() > 0) {
	                da.setDataName((String) ldb.get("name") + "(" + onduty + "~" + offduty + ")");
	                da.setDataValue((String) ldb.get("classId"));
	                class_list.add(da);
	            }
	            //29404增加过滤 班次时间不完整直接过滤掉，不显示
//	            else{
//	                da.setDataName((String) ldb.get("name") + "()");
//	                da.setDataValue((String) ldb.get("classId"));
//	                class_list.add(da);
//	            }
	        }
			this.getFormHM().put("class_list", class_list);
			
			this.getAppReaCode();
			
			String isExistIftoRest = KqUtilsClass.getFieldByDesc(table, ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
			if(isExistIftoRest != null && isExistIftoRest.length() > 0)
				this.getFormHM().put("isExistIftoRest", "1");
			
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	private void getAppReaCode(){
		StringBuffer sb = new StringBuffer();
		sb.append("select * from t_hr_busifield");
		sb.append(" where fieldsetid = 'Q11'");
		sb.append(" and itemdesc like '%加班原因%'");
		ContentDAO dao = new ContentDAO(frameconn);
		try {
			this.frecset = dao.search(sb.toString());
			if (this.frecset.next()) 
			{
				String itemid = this.frecset.getString("itemid");
				String codesetid = this.frecset.getString("codesetid");

				if (codesetid != null && codesetid.length() > 0) 
				{
					DbWizard dbWizard = new DbWizard(frameconn);
					boolean isExist = dbWizard.isExistField("Q11", itemid, false);
					if (isExist) 
					{
						this.getFormHM().put("appReaCodesetid", codesetid);
						this.getFormHM().put("appReaField", itemid);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
