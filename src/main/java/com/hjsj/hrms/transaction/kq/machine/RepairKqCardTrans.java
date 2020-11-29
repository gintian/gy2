package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.machine.RepairKqCard;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.KqClassArray;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 补刷卡纪录
 * <p>
 * Title:RepairKqCardTrans.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jan 18, 2007 5:10:43 PM
 * </p>
 * 
 * @author sunxin
 * @version 1.0
 * 
 */
public class RepairKqCardTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String a_code = (String) this.getFormHM().get("a_code");
		     //zxj 20150825 安全漏洞
	        if (null != a_code && !"".equals(a_code)
	                && !a_code.startsWith("UN") && !a_code.startsWith("UM") && !a_code.startsWith("@K") 
	                && !a_code.startsWith("GP") && !a_code.startsWith("EP"))
	            a_code = PubFunc.decryption(a_code);
	        
			String nbase = (String) this.getFormHM().get("nbase");
			String cur_session = (String) this.getFormHM().get("cur_session");
			//linbz 增加未刷卡人员页面进入标识
			String noCardFlag = (String) this.getFormHM().get("noCardFlag");
			String sql_where = (String) this.getFormHM().get("sql_where");
			if(sql_where != null && sql_where.length() > 0){
				this.getFormHM().put("sql_where", "");
			}else{
				sql_where = "";
			}
			RepairKqCard repairKqCard = new RepairKqCard(this.getFrameconn(),
					this.userView);
			String checkEm = (String) this.getFormHM().get("checkEm");
			
			if (a_code == null || a_code.length() <= 0) {
				a_code = RegisterInitInfoData.getKqPrivCode(userView)
						+ RegisterInitInfoData.getKqPrivCodeValue(userView);
			}
			String code = "";
			String kind = "";
			if (a_code.indexOf("UN") != -1) {
				kind = "2";
			} else if (a_code.indexOf("UM") != -1) {
				kind = "1";
			} else if (a_code.indexOf("@K") != -1) {
				kind = "0";
			} else if (a_code.indexOf("EP") != -1) {
				kind = "a01";
			} else {
				kind = "0";
			}
			if (a_code.length() > 2) {
				code = a_code.substring(2);
			}
			ArrayList dblist = new ArrayList();
			if (a_code.toUpperCase().indexOf("EP") != -1) {
				dblist.add(nbase);
			} else {
				dblist = RegisterInitInfoData.getDbList(code, kind, this
						.getFormHM(), this.userView, this.getFrameconn());
			}
			String card_causation = KqParam.getInstance().getCardCausation();
			if (card_causation == null || card_causation.length() <= 0) {
				card_causation = "";
				// throw GeneralExceptionHandler.Handle(new
				// GeneralException("","没有定义补刷卡原因代码项，请到考勤参数-结构参数-其他参数中设置对应的代码项！","",""));
			}
			this.getFormHM().put("card_causation", card_causation);// //补刷卡原因代码项
			KqParameter para = new KqParameter(this.userView, code, this
					.getFrameconn());
			HashMap hashmap = para.getKqParamterMap();
			String kq_type = (String) hashmap.get("kq_type");
			String kq_cardno = (String) hashmap.get("cardno");

			String temp_emp_table = repairKqCard.ceaterRepairTempEmp(
					this.userView, kq_cardno);
			String temp_emp_column = repairKqCard
					.getRepairTempEmpColumn1(kq_cardno);
			String g_no = (String) hashmap.get("g_no");
			// String cardno = (String) hashmap.get("cardno");
			String sqlstr = "select " + temp_emp_column;
			String column = temp_emp_column;
			String where = "from " + temp_emp_table + " WHERE 1=1";
			if (checkEm == null || checkEm.length() <= 0) {//未刷卡人员>补刷卡
				String select_name = (String) this.formHM.get("select_name");
				String select_type = (String) this.formHM.get("select_type");
				if("1".equalsIgnoreCase(noCardFlag)){
					select_name = "";
					select_type = "";
				}
				KqUtilsClass kqUtilsClass = new KqUtilsClass();
				if ("0".equals(select_type)) {
					where += kqUtilsClass.getWhere_C("1", "a0101", select_name);
				} else if ("1".equals(select_type)) {
					where += kqUtilsClass.getWhere_C("1", "g_no", select_name);
				} else {
					where += kqUtilsClass.getWhere_C("1", "card_no", select_name);
				}
			}
			where += sql_where;
			this.getFormHM().put("sqlstr", sqlstr);
			this.getFormHM().put("column", column);
			this.getFormHM().put("where", where);
			this.getFormHM().put("a_code", a_code);
			this.getFormHM().put("nbase", nbase);
			String work_date = (String) this.getFormHM().get("work_date");
			if (work_date != null && work_date.length() > 0)
				work_date = work_date.replaceAll("-", "\\.");
			else
				work_date = DateUtils.format(new Date(), "yyyy.MM.dd");
			
			String start_date = "";
			String end_date = "";
			
			if (work_date != null && "all".equals(work_date)) {
				ArrayList datelist = RegisterDate.getOneDurationDateList(code,
						this.userView, this.getFrameconn(), cur_session);
				CommonData vo = (CommonData) datelist.get(0);
				start_date = vo.getDataValue();
				vo = (CommonData) datelist.get(datelist.size() - 1);
				end_date = vo.getDataValue();
			} else {
				HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
				start_date=(String)hm.get("start_date"); //开始时间
				end_date=(String)hm.get("end_date");  //结束时间
				start_date=start_date.replaceAll("-","\\.");
				end_date=end_date.replaceAll("-","\\.");
			}
			
			if (checkEm == null || checkEm.length() <= 1) {//补刷卡
				repairKqCard.insertRepairTempEmpData(kq_cardno, g_no, kq_type,
						temp_emp_table, a_code, dblist, start_date, end_date, noCardFlag);
			} else {//未刷卡人员>补刷卡
				String[] emplys = checkEm.split(",");
				ArrayList selectedinfolist = new ArrayList();
				LazyDynaBean rec = null;
				for (int i = 0; i < emplys.length; i++) {
					String emply = emplys[i];
					if (emply == null || emply.length() <= 3)
						continue;
					String mess[] = emply.split(":");
					if (mess.length != 2)
						continue;
					rec = new LazyDynaBean();
					rec.set("nbase", mess[0]);
					rec.set("a0100", mess[1]);
					selectedinfolist.add(rec);
				}
				repairKqCard.insertRepairTempEmpData(kq_cardno, g_no, kq_type,
						temp_emp_table, selectedinfolist);
			}
			
			KqCardData kqCardData = new KqCardData(this.userView, this
					.getFrameconn());
			String end_date12 = DateUtils.format(new Date(), "yyyy.MM.dd");
			boolean isInout_flag = kqCardData.isViewInout_flag();
			//获取第一个上班时间  easy_hh参数没有用到往前台传参，先用该参数传
			String onduty_1 = getFirstOndutyTime(start_date, sqlstr, where);
			this.getFormHM().put("easy_hh", onduty_1);
			
			this.getFormHM().put("cycle_date", start_date);
			this.getFormHM().put("easy_date",
					DateUtils.format(new Date(), "yyyy.MM.dd"));
			this.getFormHM().put("statr_date", start_date);
			this.getFormHM().put("end_date", end_date);
			this.getFormHM().put("repair_flag", "0");
			this.getFormHM().put("class_flag", "0");
			this.getFormHM().put("cycle_num", "1");
			this.getFormHM().put("cycle_hh", "00");
			this.getFormHM().put("cycle_mm", "00");
			this.getFormHM().put("temp_emp_table", temp_emp_table);
			this.getFormHM().put("checkEm", "");
			this.getFormHM().put("isInout_flag", isInout_flag + "");
			this.getFormHM().put("end_date12", end_date12);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 获取第一个人的当天班次的上班时间
	 * @param start_date 开始日期
	 * @param sqlstr	如：select nbase,a0100,b0110,e0122,e01a1,a0101,g_no,card_no,flag
	 * @param where		如：from t#_kq_rep_su WHERE 1=1
	 * @return
	 */
	private String getFirstOndutyTime(String start_date, String sqlstr, String where){
		
		String onduty_1 = "";
		if(StringUtils.isNotEmpty(sqlstr) && StringUtils.isNotEmpty(where)){
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rs = null;
            try {
            	rs = dao.search(sqlstr+" "+where);
                if(rs.next()){
                	String nbaseFirst = rs.getString("nbase");
                    String a0100First = rs.getString("a0100"); 
                    
                    KqClassArray kqClassArray = new KqClassArray(this.getFrameconn());
                	String classId = kqClassArray.getClassId(start_date, a0100First, nbaseFirst);
                	RecordVo vo = kqClassArray.getClassMessage(classId);
                	onduty_1 = vo.getString("onduty_1");//上班1
                }
            } catch(Exception e){
                e.printStackTrace();
            } finally {
                KqUtilsClass.closeDBResource(rs);
            }
		}
		
		return onduty_1;
	}

}
