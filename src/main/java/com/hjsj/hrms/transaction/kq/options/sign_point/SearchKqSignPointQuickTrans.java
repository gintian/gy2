package com.hjsj.hrms.transaction.kq.options.sign_point;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <p>Title: SearchKqSignPointQuickTrans </p>
 * <p>Description: 快速模糊查询考勤点数据 </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2014-6-23 下午5:12:05</p>
 * @author yangj
 * @version 1.0
 */
public class SearchKqSignPointQuickTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String oldname = (String) this.getFormHM().get("oldname");
		// 处理特殊字符 begin
		oldname = SafeCode.decode(oldname);
		List list = new ArrayList();
		// 显示list，点击list、修改树，调出地图。
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer();
		sql.append("select name, city from kq_sign_point k ");
		sql.append("where (k.name like '%" + oldname + "%' or k.city like '%"
				+ oldname + "%')");
		
		String privCode = RegisterInitInfoData.getKqPrivCode(userView);
		String codeValue = RegisterInitInfoData.getKqPrivCodeValue(userView);
		if (privCode != null && privCode.length() > 0) {
			sql.append(" and ( b0110 like '" + codeValue + "%'");
			sql.append(" or b0110 is null or b0110 ='' )");// 公共考勤点
		}
		try {
			frowset = dao.search(sql.toString());
			String dataValue, dataName;
			while (frowset.next()) {
				dataValue = frowset.getString("city") + ":" +frowset.getString("name");
				CommonData temp = new CommonData();	
				// 字符串长度过长时，截取一部分显示
				if (dataValue.length() > 14) {
					dataName = dataValue.substring(0, 8)
							+ "..."
							+ dataValue.substring(dataValue.length() - 10,
									dataValue.length());
					// 数据处理
					dataValue = SafeCode.encode(dataValue);
					dataName = SafeCode.encode(dataName);
					temp = new CommonData(dataValue, dataName);
				} else {
					dataValue = SafeCode.encode(dataValue);
					temp = new CommonData(dataValue, dataValue);
				}
				list.add(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("pointList", list);
	}
}
