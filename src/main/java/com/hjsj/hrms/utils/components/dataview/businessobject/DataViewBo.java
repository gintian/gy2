package com.hjsj.hrms.utils.components.dataview.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataViewBo {
	private Connection conn = null;
	private UserView userview;
	private String nmodule = "";
	
	public DataViewBo(Connection conn,UserView userView,String nmodule) {
		this.conn = conn;
		this.userview = userView;
		this.nmodule = nmodule;
	}
	/**
	 * 不同模块生成简单报表数据
	 * @return
	 */
	public ArrayList<LazyDynaBean> createDataUrl() {
		RowSet rs = null;
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		ArrayList<String> listParam = new ArrayList<String>();
		StringBuffer sql = new StringBuffer("");
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			//通用报表
			sql.append("select * from t_custom_report where flag=1 and Moduleid=? and report_type=4 order by id");
			listParam.add(this.nmodule);
			rs=dao.search(sql.toString(),listParam);
			while(rs.next())
			{
				if(this.userview.isHaveResource(IResourceConstant.CUSTOM_REPORT, rs.getString("id")))
				{
			     	LazyDynaBean bean = new LazyDynaBean();
			    	bean.set("id", String.valueOf(rs.getInt("id")));
			    	bean.set("description", rs.getString("description")==null?"":rs.getString("description"));
			    	bean.set("name", rs.getString("name")==null?"":rs.getString("name"));
			    	bean.set("link_tabid", rs.getString("link_tabid")==null?"":rs.getString("link_tabid"));
			    	bean.set("ext", rs.getString("ext")==null?"":rs.getString("ext"));
				    bean.set("report_type", "4");
				    bean.set("url", "/components/dataview/dataview.jsp?encryptParam="+PubFunc.encrypt("reportid="+rs.getInt("id")));
			    	list.add(bean);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs!=null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}
