/**
 * 
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.businessobject.info.OrgInfoUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Administrator
 * 
 */
public class SaveChangeOrgMsgTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try {
			String UNIT_HISTORY_SET = (String)this.getFormHM().get("HISTORY_SET");
			//String UNIT_HISTORY_SET = SystemConfig.getPropertyValue("UNIT_HISTORY_SET");
			UNIT_HISTORY_SET = PubFunc.nullToStr(UNIT_HISTORY_SET);
			String orgitemid;
			
			 if(UNIT_HISTORY_SET.trim().length()>0 && "B".equalsIgnoreCase(UNIT_HISTORY_SET.substring(0,1)))
				 orgitemid = "b0110";
			 else
				 orgitemid = "e01a1";
			if (UNIT_HISTORY_SET != null
					&& UNIT_HISTORY_SET.trim().length() > 1&&DataDictionary
					.getFieldList(UNIT_HISTORY_SET.toUpperCase(),
							Constant.USED_FIELD_SET)!=null&&DataDictionary
							.getFieldList(UNIT_HISTORY_SET.toUpperCase(),
									Constant.USED_FIELD_SET).size()>0) {
				ArrayList msgb0110= (ArrayList) this.getFormHM().get("msgb0110");
				ArrayList fieldlist = (ArrayList) this.getFormHM().get(
						"fieldlist");
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				if(msgb0110==null||fieldlist==null)
					return;
				for (int n = 0; n < msgb0110.size(); n++) {
					String b0110 = (String)msgb0110.get(n);
					RecordVo vo = new RecordVo(UNIT_HISTORY_SET.toLowerCase());
					vo.setString(orgitemid, b0110);
					vo.setString("i9999", geti9999(b0110, UNIT_HISTORY_SET
							.toLowerCase(), dao,orgitemid));
					for (int i = 0; i < fieldlist.size(); i++) {
						FieldItem fieldItem = (FieldItem) fieldlist.get(i);

						String itemid = fieldItem.getItemid();
						String value = fieldItem.getValue();
						if ("".equals(value.trim()))
							continue;
						if ("D".equals(fieldItem.getItemtype())) {
							vo.setDate(itemid, PubFunc.DateStringChangeValue(value));// 休改了时间格式
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
					OrgInfoUtils orgInfoUtils=new OrgInfoUtils(this.getFrameconn());	
					orgInfoUtils.updateSequenceableValue(orgitemid, UNIT_HISTORY_SET.toLowerCase(), vo.getString("i9999"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String geti9999(String b0110, String table, ContentDAO dao,String orgitemid)
			throws SQLException {
		String sql = "select ("+Sql_switcher.sqlNull("max(i9999)",0)+"+1) as i9999 from " + table
				+ " where "+orgitemid+"='" + b0110 + "'";
		this.frowset = dao.search(sql);
		while (this.frowset.next()) {
			return String.valueOf(this.frowset.getInt("i9999"));
		}
		return "1";
	}

}
