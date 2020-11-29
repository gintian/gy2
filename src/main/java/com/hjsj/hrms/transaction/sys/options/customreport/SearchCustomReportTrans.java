package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hjsj.hrms.businessobject.board.BoardBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * <p>
 * Title:SearchCustomReportTrans
 * </p>
 * <p>
 * Description:查询所有自定制报表信息
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
public class SearchCustomReportTrans extends IBusiness {

	public void execute() throws GeneralException {
		// 模块id
		String businessModuleValue = (String) this.getFormHM().get("businessModuleValue");
		// 模块列表
		ArrayList businessModuleList = this.getBusinessModuleList();
		// 模块id默认为第一个
		if (businessModuleValue == null || businessModuleValue.length() <= 0) {
			CommonData data = (CommonData) businessModuleList.get(0);
			businessModuleValue = data.getDataValue();
		}
		// 获得该模块下的自定制报表
		ArrayList infoList = getCustomReportList(businessModuleValue);
		
		
		// 保存
		this.getFormHM().put("businessModuleValue", businessModuleValue);
		this.getFormHM().put("businessModuleList", businessModuleList);
		this.getFormHM().put("infoList", infoList);
		this.getFormHM().put("hMap", this.getModelFlagHashMap());
		this.getFormHM().put("fMap", getFlagaHashMap());
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
	 * 查询指定模块下的自定制报表
	 * @param businessModuleValue 模块id
	 * @return
	 */
	private ArrayList getCustomReportList(String businessModuleValue) {
		ArrayList list = new ArrayList();
		//sql语句
		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append("select * from t_custom_report ");
		sqlBuff.append("where moduleid='");
		sqlBuff.append(businessModuleValue);
		sqlBuff.append("' ");
		if (!this.userView.isSuper_admin()) {
			// 添加用户权限
			BoardBo boardBo = new BoardBo(this.getFrameconn(),this.userView);
			String content = boardBo.getPrivByUser(IResourceConstant.CUSTOM_REPORT, "0");
			String []st = content.split(",");
			if (content.length() > 0 && st.length > 0) {
				sqlBuff.append(" and id in (");
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < st.length; i++) {
					buf.append(",'");
					buf.append(st[i]);
					buf.append("'");
				}
				sqlBuff.append(buf.substring(1));
				sqlBuff.append(") and flag = 1 ");
				
			}
		}
		sqlBuff.append(" order by id");
		
		//查询操作
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			frowset = dao.search(sqlBuff.toString());
			while (frowset.next()) {
				RecordVo vo = new RecordVo("t_custom_report");
				vo.setInt("id", frowset.getInt("id"));
				vo.setString("name", frowset.getString("name"));
				vo.setString("description", frowset.getString("description"));
				vo.setInt("flag", frowset.getInt("flag"));
				vo.setInt("link_tabid", frowset.getInt("link_tabid"));
				vo.setString("moduleid", frowset.getString("moduleid"));
				
				// 上传的sql条件（xml模板的内容）
				String sqlfile = frowset.getString("sqlfile");
				if (sqlfile != null && sqlfile.trim().length() > 0) {
					vo.setString("sqlfile", "true");
				} else {
					vo.setString("sqlfile", "");
				}
				
				// 扩展名
				String ext = frowset.getString("ext");
				if (ext != null) {
					vo.setString("ext", ext.toLowerCase());
				} else {
					vo.setString("ext", "");
				}
				vo.setInt("report_type", frowset.getInt("report_type"));
				list.add(vo);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 高级花名册的id对应的
	 * @param businessModuleValue 模块id
	 * @return
	 */
	private HashMap getModelFlagHashMap() {
		HashMap map = new HashMap();
		//sql语句
		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append("SELECT tabid,nmodule FROM muster_name ");
		
		//查询操作
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			frowset = dao.search(sqlBuff.toString());
			while (frowset.next()) {
				map.put("3:"+frowset.getInt("tabid"), frowset.getInt("nmodule")+"");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 登记表id对应的flaga字段
	 * @return
	 */
	private HashMap getFlagaHashMap() {
		HashMap map = new HashMap();
		//sql语句
		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append("SELECT tabid,flaga FROM rname ");
		
		//查询操作
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			frowset = dao.search(sqlBuff.toString());
			while (frowset.next()) {
				map.put("2:"+frowset.getInt("tabid"), frowset.getString("flaga")+"");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

}
