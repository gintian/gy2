package com.hjsj.hrms.transaction.kq.register.history;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.HistoryBrowse;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:历史数据浏览---日考勤数据浏览
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2006-6-19:15:10:06
 * </p>
 * 
 * @author kf-1
 * @version 1.0
 * 
 */
public class DailyBrowseTrans extends IBusiness {
	private String error_return1 = "/templates/menu/kq_m_menu.do?b_query=link&module=6";

	public void execute() throws GeneralException {
        String error_message = ResourceFactory.getProperty("kq.register.session.nohistory");
        this.getFormHM().put("error_message", error_message);
	    
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String landing = (String) hm.get("landing");
		if (landing != null && "myself".equals(landing))
			error_return1 = "/templates/menu/kq_dept_menu.do?b_query=link&module=22";
		String error_flag = "0";
		String error_stuts = (String) this.getFormHM().get("error_stuts");
		String error_return = (String) this.getFormHM().get("error_return");
		String select_flag = (String) this.getFormHM().get("select_flag");
		String select_name = (String) this.getFormHM().get("select_name");
		String select_pre = (String) this.getFormHM().get("select_pre");
		String select_type = (String) this.getFormHM().get("select_type");
		this.getFormHM().put("select_flag", select_flag);
		this.getFormHM().put("select_name", select_name);
		if (error_stuts != null && "1".equals(error_stuts)) {
			this.getFormHM().put("error_return", error_return);
			error_flag = (String) this.getFormHM().get("error_flag");
			error_stuts = "0";
		} else {
			error_stuts = "0";
		}
		// 转换小时 1=默认；2=HH:MM
		String selectys = (String) hm.get("selectys");
		if (selectys == null || "".equals(selectys)) {
			selectys = "1";
		}
		this.getFormHM().put("selectys", selectys);
		String year = (String) this.getFormHM().get("year");// 考勤期间
		String duration = (String) this.getFormHM().get("duration");
		ArrayList yearlist = (ArrayList) this.getFormHM().get("yearlist");
		ArrayList durationlist = (ArrayList) this.getFormHM().get(
				"durationlist");
		String coursedate = (String) this.getFormHM().get("coursedate");// 考勤期间

		if (coursedate == null || coursedate.length() < 1) {
			coursedate = (String) hm.get("coursedate");
		}
		if (coursedate != null && coursedate.indexOf("-") != -1) {
			if (year == null || year.length() < 1) {
				year = coursedate.split("-")[0];
			}
			if (duration == null || duration.length() < 1) {
				duration = coursedate.split("-")[1];
			}
		}
		String code = (String) this.getFormHM().get("code");
		String kind = (String) this.getFormHM().get("kind");
		ArrayList courselist = (ArrayList) this.getFormHM().get("courselist");
		ArrayList kq_dbase_list = (ArrayList) this.getFormHM().get("kq_dbase_list");
		HashMap map = new HashMap();
		String code_kind = "";
		if (kind == null || kind.length() <= 0) {
			kind = RegisterInitInfoData.getKindValue(kind, this.userView);
			code = "";
		}
		
		String kqPrivCodeValue = RegisterInitInfoData.getKqPrivCodeValue(userView);
		if ("2".equals(kind)) {
			code = kqPrivCodeValue;
		} else {
			code_kind = RegisterInitInfoData.getDbB0100(kqPrivCodeValue, kind, map, 
			        this.userView, this.getFrameconn());
		}
		
		if (code == null || code.length() <= 0)
			code = this.userView.getUserOrgId();
		
		if (kq_dbase_list == null || kq_dbase_list.size() <= 0) {
			kq_dbase_list = RegisterInitInfoData.getDase3(this.getFormHM(),
					this.userView, this.getFrameconn());
		} else {
			if (code != null && code.length() > 0) {
				if ("2".equals(kind)) {
					kq_dbase_list = RegisterInitInfoData.getB0110Dase(
					        this.getFormHM(), this.userView, this.getFrameconn(),
							code);
				} else if (code_kind != null && code_kind.length() > 0) {
					kq_dbase_list = RegisterInitInfoData.getB0110Dase(
					        this.getFormHM(), this.userView, this.getFrameconn(),
							code_kind);
				} else {
					kq_dbase_list = RegisterInitInfoData.getB0110Dase(
					        this.getFormHM(), this.userView, this.getFrameconn(),
							code);
				}
			} else {
				kq_dbase_list = RegisterInitInfoData.getDase3(this.getFormHM(),
						this.userView, this.getFrameconn());
			}
		}
		String cur_course = "";
		courselist = RegisterDate.sessionDate(this.frameconn, "1");
		if (courselist != null && courselist.size() > 0) {
			if (coursedate != null && coursedate.length() > 0) {
				cur_course = coursedate;
			} else {
				CommonData vo = (CommonData) courselist.get(0);
				cur_course = vo.getDataValue();
			}
		} else {
			this.getFormHM().put("error_return", this.error_return1);
			this.getFormHM().put("error_flag", "4");
			this.getFormHM().put("error_stuts", "1");
			return;
		}
		String cur_year = "";
		String cur_duration = "";
		if (cur_year == null || cur_year.length() <= 0) {
			yearlist = RegisterDate.yearDate(this.frameconn, "1");
			
			if (yearlist == null || yearlist.size() <= 0) 
			    throw GeneralExceptionHandler.Handle(new GeneralException("", error_message, "", ""));
			    
			if (year != null && year.length() > 0) {
				cur_year = year;
			} else {
				CommonData vy = (CommonData) yearlist.get(0);
				cur_year = vy.getDataValue();
			}
		} else {
			cur_year = year;
		}
		
		if (cur_duration == null || cur_duration.length() <= 0) {
			durationlist = RegisterDate.durationDate(this.frameconn, "1", cur_year);
			if (durationlist != null && durationlist.size() > 0) {
				if (duration != null && duration.length() > 0) {
					cur_duration = duration;
				} else {
					CommonData vd = (CommonData) durationlist.get(0);
					cur_duration = vd.getDataValue();
				}
			} else {

				throw GeneralExceptionHandler.Handle(new GeneralException("",
						ResourceFactory.getProperty("kq.register.session.nohistory"),
						"", ""));
			}
		} else {
			cur_duration = duration;
		}

		if (code == null || code.length() <= 0) {
			code = "";
		}
		
		String b0110 = "";
		if (code.length() <= 0) {
			ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
			b0110 = managePrivCode.getUNB0110();
		} else {
			b0110 = "UN" + code;
		}
		
		if (kind == null || kind.length() <= 0) {
			kind = "2";
		}
		ArrayList datelist = HistoryBrowse.registerdate(b0110, this.getFrameconn(), 
		        this.userView, cur_year + "-" + cur_duration, "1");
		String cur_date = "";
		String start_date = "";
		String end_date = "";
		if (datelist != null && datelist.size() > 0) {
			CommonData vo = (CommonData) datelist.get(0);
			cur_date = vo.getDataValue();
			start_date = vo.getDataValue();
			vo = (CommonData) datelist.get(datelist.size() - 1);
			end_date = vo.getDataValue();
		}
		
		ArrayList fieldlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
		ArrayList fielditemlist = RegisterInitInfoData.newFieldItemList(fieldlist,this.userView,this.frameconn);
    	//** -------------------------郑文龙---------------------- 加 工号、考勤卡号
		KqParameter para = new KqParameter(this.userView, "", this.frameconn);
		HashMap hashmap = para.getKqParamterMap();
		String g_no = ((String) hashmap.get("g_no")).toLowerCase();
		String cardno = ((String) hashmap.get("cardno")).toLowerCase();
		//** -------------------------郑文龙---------------------- 加 工号、考勤卡号
		fielditemlist = RegisterInitInfoData.isExistsG_noAndCardno("A0101","Q03",g_no,cardno,fielditemlist);
		ArrayList sql_db_list = new ArrayList();
		if (select_pre != null && select_pre.length() > 0 && !"all".equals(select_pre)) {
			sql_db_list.add(select_pre);
		} else {
			sql_db_list = kq_dbase_list;
		}
		
        if (sql_db_list == null || sql_db_list.size() <= 0)
            throw GeneralExceptionHandler.Handle(new GeneralException("", 
                    ResourceFactory.getProperty("kq.register.dbase.nosave"), 
                    "", ""));
        
		KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
		String where_c = null;
		if("0".equals(select_type)){
			where_c = kqUtilsClass.getWhere_C(select_flag, "a0101",
					select_name);
		} else if("1".equals(select_type)){
			where_c = kqUtilsClass.getWhere_C(select_flag, g_no,
					select_name);
		}else{
			where_c = kqUtilsClass.getWhere_C(select_flag, cardno,
					select_name);
		}
		DbWizard dbw = new DbWizard(this.frameconn);
//		ArrayList sqllist = RegisterInitInfoData.getSqlstr5(fielditemlist,
//				sql_db_list, cur_date, code, kind, "Q03", this.userView,
//				"all", where_c,this.frameconn);
		ArrayList sqllist = new ArrayList();
        if(dataInQ03(cur_date) || !dbw.isExistTable("Q03_arc",false)){
        	sqllist = RegisterInitInfoData.getSqlstr5(fielditemlist,
    				sql_db_list, cur_date, code, kind, "Q03", this.userView,
    				"all", where_c,this.frameconn);
        }else{
        	sqllist = com.hjsj.hrms.businessobject.kq.register.history.RegisterInitInfoData.getSqlstr6(fielditemlist, sql_db_list, cur_date,code,kind,"Q03_arc",this.userView,"all",where_c,this.frameconn);	
        }
		
		this.getFormHM().put("kq_list",
				kqUtilsClass.getKqNbaseList(kq_dbase_list));
		this.getFormHM().put("sqlstr", sqllist.get(0).toString());
		this.getFormHM().put("strwhere", sqllist.get(1).toString());
		this.getFormHM().put("orderby", sqllist.get(2).toString());
		this.getFormHM().put("columns", sqllist.get(3).toString());

		this.getFormHM().put("fielditemlist", fielditemlist);
		this.getFormHM().put("kq_dbase_list", kq_dbase_list);
		this.getFormHM().put("code", code);
		this.getFormHM().put("kind", kind);
		this.getFormHM().put("courselist", courselist);
		this.getFormHM().put("datelist", datelist);
		String workcalendar = RegisterInitInfoData.getDateSelectHtml(datelist, cur_date);
		this.getFormHM().put("workcalendar", workcalendar);
		this.getFormHM().put("coursedate", cur_course);
		this.getFormHM().put("yearlist", yearlist);
		this.getFormHM().put("durationlist", durationlist);
		this.getFormHM().put("year", cur_year);
		this.getFormHM().put("duration", cur_duration);
		this.getFormHM().put("registerdate", cur_date);
		this.getFormHM().put("kq_duration", cur_date);
		this.getFormHM().put("start_date", start_date);
		this.getFormHM().put("end_date", end_date);
		// 涉及SQL注入直接放进userView里
		this.userView.getHm().put("kq_condition", "3`" + sqllist.get(4).toString());
		this.getFormHM().put("relatTableid", "3");
		this.getFormHM().put("returnURL", "/kq/register/history/dailybrowsedata.do?b_search=link");
		this.getFormHM().put("error_flag", error_flag);
		this.getFormHM().put("error_stuts", error_stuts);
		// 显示部门层数
		Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		if (uplevel == null || uplevel.length() == 0)
			uplevel = "0";
		this.getFormHM().put("uplevel", uplevel);
	}
	/**
     * 判断某日期数据是否在Q03（员工日明细表）中
     * 
     * @return
     * @throws GeneralException
     */
    private boolean dataInQ03(String registerdate) throws GeneralException {
    	boolean bool = true;
    	RowSet rs = null;
    	if (registerdate == null) {
    		return false;
    	}
    	StringBuffer sql = new StringBuffer();
    	sql.append("select count(q03z0) num from Q03 ");
    	sql.append(" where q03z0=?");
    	ArrayList<String> list = new ArrayList<String>();
    	list.add(registerdate);
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
    	int num = 0;
        try {
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
            	num = rs.getInt("num");
            }
            if(num == 0){
            	bool = false; 
            }            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
        	PubFunc.closeDbObj(rs);
        }
        return bool;
    }
}
