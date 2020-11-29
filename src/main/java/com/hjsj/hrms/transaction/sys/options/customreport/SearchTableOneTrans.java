package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * <p>
 * Title:SearchTableOneTrans
 * </p>
 * <p>
 * Description:查询统计表
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-3-9
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class SearchTableOneTrans extends IBusiness {

	public void execute() throws GeneralException {
		
	}
	
	/**
	 * 查询所有业务模块（t_hr_subsys）
	 * @return
	 */
	private ArrayList getBusinessModuleList() {
		// 保存所有业务模块的集合
		ArrayList list = new ArrayList();
		// 查询语句
		String sql = "select id,name from t_hr_subsys where is_available='1'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			frowset = dao.search(sql);
			while (frowset.next()) {
				CommonData data = new CommonData();
				data.setDataName(frowset.getString("name"));
				data.setDataValue(frowset.getString("id"));
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获得报表类型
	 * =0(自定制),=1(统计报表)
	 *=2(登记表),=3(高级花名册)
	 * @return
	 */
	private ArrayList getReportTypeList() {
		
		ArrayList list = new ArrayList();
		
		CommonData data1 = new CommonData("0","自定制");
		CommonData data2 = new CommonData("1","统计报表");
		CommonData data3 = new CommonData("2","登记表");
		CommonData data4 = new CommonData("3","高级花名册");
		list.add(data1);
		list.add(data2);
		list.add(data3);
		list.add(data4);
		
		return list;
	}

}
