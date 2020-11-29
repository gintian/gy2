package com.hjsj.hrms.transaction.dutyinfo;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SaveChangeOrgMsgTrans  extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try {
			String POST_HISTORY_SET = SystemConfig
					.getPropertyValue("POST_HISTORY_SET");
			if (POST_HISTORY_SET != null
					&& POST_HISTORY_SET.trim().length() > 1&&DataDictionary.getFieldSetVo(POST_HISTORY_SET)!=null) {
				ArrayList msgb0110= (ArrayList) this.getFormHM().get("msgb0110");
				if(msgb0110==null){
					return;
				}
				DbWizard dw = new DbWizard(this.frameconn);
				if(!dw.isExistTable(POST_HISTORY_SET.toLowerCase()))
					return;
				ArrayList fieldlist = (ArrayList) this.getFormHM().get(
						"childfielditemlist");
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				for (int n = 0; n < msgb0110.size(); n++) {
					String e01a1 = (String)msgb0110.get(n);
					RecordVo vo = new RecordVo(POST_HISTORY_SET.toLowerCase());
					vo.setString("e01a1", e01a1);
					vo.setString("i9999", geti9999(e01a1, POST_HISTORY_SET
							.toLowerCase(), dao));
					for (int i = 0; i < fieldlist.size(); i++) {
						FieldItem fieldItem = (FieldItem) fieldlist.get(i);

						String itemid = fieldItem.getItemid();
						String value = fieldItem.getValue();

						if ("".equals(value.trim()))
							continue;
						if ("D".equals(fieldItem.getItemtype())) {
							vo.setDate(itemid, PubFunc.DateStringChangeValue(value));
						} else if ("N".equals(fieldItem.getItemtype()))// 对于数值类型，在前后台都要进行控制,前台验证是整数还是小数类型，后台修正小数位数
						{
							value = PubFunc.round(value, fieldItem
									.getDecimalwidth());
							vo.setString(itemid, value);
						} else
							vo.setString(itemid, value);
					}
					vo.setString("createusername", this.userView.getUserName());
					vo.setString("modusername", this.userView.getUserName());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar calendar = Calendar.getInstance();
					String date = sdf.format(calendar.getTime());
					vo.setDate("createtime", date);
					vo.setDate("modtime", date);
					dao.addValueObject(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String geti9999(String e01a1, String table, ContentDAO dao)
			throws SQLException {
		String sql = "select ("+Sql_switcher.sqlNull("max(i9999)",0)+"+1) as i9999 from " + table
				+ " where e01a1='" + e01a1 + "'";
		this.frowset = dao.search(sql);
		while (this.frowset.next()) {
			return String.valueOf(this.frowset.getInt("i9999"));
		}
		return "1";
	}

}
