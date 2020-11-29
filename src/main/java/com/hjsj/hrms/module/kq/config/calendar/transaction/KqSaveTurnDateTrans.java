package com.hjsj.hrms.module.kq.config.calendar.transaction;

import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * 保存公休日倒休
 * @author xuanz
 *
 */
public class KqSaveTurnDateTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		JSONObject returnJson = new JSONObject();
		String return_code="success";
		String return_msg = "success";
		JSONObject return_data = new JSONObject();
		 try {
			 	String turnDate = (String)this.getFormHM().get("turnDate");
		 		JSONObject jsonObj = JSONObject.fromObject(turnDate);
		 		String rdate = (String) jsonObj.get("week_date");
		 		String tdate = (String) jsonObj.get("turn_date");
		 		Integer fid = (Integer) jsonObj.get("id");
	            if (rdate != null && rdate.length() > 0) {
	                rdate = rdate.substring(0, 10);
	            }
	            if (tdate != null && tdate.length() > 0) {
	                tdate = tdate.substring(0, 10);
	            }
	            
	            ArrayList restList = IfRestDate.search_RestOfWeek("UN", userView, this.getFrameconn());
	            String rest_date = restList.get(0).toString();

	            String date = is_RestDate2(rdate.replaceAll("-", "\\."), userView, rest_date);
	            String rest_state = ResourceFactory.getProperty("kq.date.work");

	            String dates = IfRestDate.is_RestDate2(tdate.replaceAll("-", "\\."), userView, rest_date, "UN", this.getFrameconn());
	            String rest = ResourceFactory.getProperty("kq.date.rest");
	            if (dates.indexOf(rest) != -1) {
	            	return_code="fail";
	        		return_msg = "5";
	        		returnJson.put("return_code", return_code);
	    			returnJson.put("return_msg", return_msg);
	    			returnJson.put("return_data", return_data);
	    			this.formHM.put("returnStr", returnJson.toString());
	    			return;
	            }

	            if (date.indexOf(rest_state) != -1) {
	            	return_code="fail";
	        		return_msg = "2";
	            } else {
	                ContentDAO dao = new ContentDAO(this.getFrameconn());
	                StringBuffer sb = new StringBuffer();
	                String work = "";
	                sb.append("select week_date from kq_turn_rest where week_date=");
	                sb.append(Sql_switcher.dateValue(rdate));
	                sb.append(" and b0110='UN'");
	                if (fid != null && fid > 0) {
	                    sb.append(" and turn_id not in('" + fid + "')");
	                }
	                this.frowset = dao.search(sb.toString());
	                if (this.frowset.next()) {
	                    Object obj = this.frowset.getObject("week_date");
	                    if (obj instanceof Date) {
	                        work = OperateDate.dateToStr((Date) obj, "yyyy.MM.dd");
	                    } else if (obj instanceof String) {
	                        work = (String) obj;
	                    }
	                }

	                String turn = "";
	                sb.delete(0, sb.length());
	                sb.append("select turn_date from kq_turn_rest where turn_date=");
	                sb.append(Sql_switcher.dateValue(tdate));
	                sb.append(" and b0110='UN'");
	                if (fid != null && fid > 0) {
	                    sb.append(" and turn_id not in('" + fid + "')");
	                }
	                this.frowset = dao.search(sb.toString());
	                if (this.frowset.next()) {
	                    Object obj = this.frowset.getObject("turn_date");
	                    if (obj instanceof Date) {
	                        turn = OperateDate.dateToStr((Date) obj, "yyyy.MM.dd");
	                    } else if (obj instanceof String) {
	                        turn = (String) obj;
	                    }
	                }

	                if ((work == null || "".equals(work)) && (turn == null || "".equals(turn))) {
	                    
	                } else if (work.length() > 0 && (turn == null || "".equals(turn))) {
	                	return_code="fail";
		        		return_msg = "3";
	                } else {
	                	return_code="fail";
		        		return_msg = "4";
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw GeneralExceptionHandler.Handle(e);
	        }

		 	returnJson.put("return_code", return_code);
			returnJson.put("return_msg", return_msg);
			returnJson.put("return_data", return_data);
			this.formHM.put("returnStr", returnJson.toString());
	    }
	   
	    public static String is_RestDate2(String cur_date, UserView userView, String rest_date) {
	        String rest_state = ResourceFactory.getProperty("kq.date.work");
	        String restdate = ResourceFactory.getProperty("kq.date.rest");
            if (IfRestDate.if_Rest(cur_date, userView, rest_date))// 判断公休日
            {
                rest_state = restdate;
            }
	        return rest_state;
	    }

}
