package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;


/**
 * <p>
 * Title:EditCustomReportTrans
 * </p>
 * <p>
 * Description:修改自定制报表信息
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-3-6
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class EditCustomReportTrans extends IBusiness {

	public void execute() throws GeneralException {
		// 记录id
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		String id = (String) map.get("id");
		// 模块id
		String businessModuleValue = (String) this.getFormHM().get("businessModuleValue");
		// 模块列表
		ArrayList businessModuleList = this.getBusinessModuleList();
		// 获得报表类型
		ArrayList reportTypeList = getReportTypeList();
		
		//查询该记录
		StringBuffer sql = new StringBuffer();
		sql.append("select id,name,description,flag,moduleid,report_type,link_tabid,ext,sqlfile");
		sql.append(" from t_custom_report where id='");
		sql.append(id);
		sql.append("'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo = new RecordVo("t_custom_report");
		try {
			frowset = dao.search(sql.toString());
			if (frowset.next()) {
				this.getFormHM().put("id", frowset.getInt("id")+"");
				this.getFormHM().put("name", frowset.getString("name"));
				this.getFormHM().put("description", frowset.getString("description"));
				this.getFormHM().put("flag", Integer.valueOf(frowset.getInt("flag")));
				this.getFormHM().put("businessModuleValue", frowset.getString("moduleid"));
				this.getFormHM().put("reportType", Integer.valueOf(frowset.getInt("report_type")));
				this.getFormHM().put("link_tabid", Integer.valueOf(frowset.getInt("link_tabid")));
				
				// 上传的sql条件（xml模板的内容）
				String sqlfile = frowset.getString("sqlfile");
				if (sqlfile != null && sqlfile.trim().length() > 0) {
					this.getFormHM().put("sqlfileExist", "true");
				} else {
					this.getFormHM().put("sqlfile", "");
				}
				
				// 扩展名
				String ext = frowset.getString("ext");
				if (ext != null && ext.length() > 0) {
					this.getFormHM().put("ext", ext.toLowerCase());
				} else {
					this.getFormHM().put("ext", "");
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// 保存
		this.getFormHM().put("businessModuleValue", businessModuleValue);
		this.getFormHM().put("businessModuleList", businessModuleList);
		this.getFormHM().put("reportTypeList", reportTypeList);
		this.getFormHM().put("isEdit", "1");
		
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
		
		CommonData data1 = new CommonData("0","特殊报表");
		CommonData data2 = new CommonData("1","统计报表");
		CommonData data3 = new CommonData("2","登记表");
		CommonData data4 = new CommonData("3","高级花名册");
		CommonData data5 = new CommonData("4","简单名册报表");
		
		list.add(data2);
		list.add(data3);
		list.add(data4);
		list.add(data1);
		list.add(data5);
		
		return list;
	}
}
