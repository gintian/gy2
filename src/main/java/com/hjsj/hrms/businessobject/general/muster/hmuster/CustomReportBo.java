package com.hjsj.hrms.businessobject.general.muster.hmuster;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
/**
 * 用户自定义报表
 * <p>Title:CustomReportBo.java</p>
 * <p>Description>:CustomReportBo.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 11, 2010 4:16:38 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class CustomReportBo {
	
	/**模块标志34	薪资管理35薪资分析*/
	private String nmodule;
	private Connection conn;
	private UserView userView;
	/**
	 * =0(自定制)
       =1(统计报表)
       =2(登记表)
       =3(高级花名册)
	 */
	private String report_type;
	public CustomReportBo(Connection con,UserView userView,String nmodule)
	{
		this.conn=con;
		this.userView=userView;
		this.nmodule=nmodule;
	}
	public CustomReportBo(){}
	public ArrayList getCustomReportList()
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append("select * from t_custom_report where flag=1 and Moduleid=");//t_hr_subsys:模块分类表
			sql.append("'"+this.nmodule+"' order by id");
		//	sql.append(" and (report_type=0 or report_type="+this.report_type+")");
			ContentDAO dao = new ContentDAO(this.conn);
			
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				if(this.userView.isHaveResource(IResourceConstant.CUSTOM_REPORT, rs.getString("id")))
				{
			     	LazyDynaBean bean = new LazyDynaBean();
			    	bean.set("id", String.valueOf(rs.getInt("id")));
			    	bean.set("description", rs.getString("description")==null?"":rs.getString("description"));
			    	bean.set("name", rs.getString("name")==null?"":rs.getString("name"));
			    	bean.set("ext", rs.getString("ext")==null?"":rs.getString("ext"));
			    	String link_tabid="";
			    	int rtype=rs.getInt("report_type");
			    	if(rtype==3) {
                        link_tabid=rs.getString("link_tabid");
                    }
			    	bean.set("link_tabid",link_tabid);
				    /**高级花名册*/
				    if(rtype==3)
				    {
				    	RecordVo  vo = new RecordVo("muster_name");
				    	vo.setInt("tabid",Integer.parseInt(link_tabid));
				    	vo=dao.findByPrimaryKey(vo);
				    	bean.set("vo", vo);
				    }
				    bean.set("report_type", String.valueOf(rtype));
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
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
//			if(this.conn!=null)
//			{
//				try
//				{
//					this.conn.close();
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
		}
		return list;
	}
	

}
