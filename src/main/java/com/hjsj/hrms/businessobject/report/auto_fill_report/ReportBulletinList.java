/**
 * 
 */
package com.hjsj.hrms.businessobject.report.auto_fill_report;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * <p>Title:系统登陆后公告栏显示报表信息</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 5, 2006:9:53:20 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportBulletinList {
	private Connection conn;
	
	public ReportBulletinList(Connection conn){
		this.conn = conn;
	}
	
	public ArrayList getReportList(UserView userView) throws GeneralException{
		String userName = userView.getUserName();
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		// 用户负责的报表分类
		String unitcode="";
		Calendar d=Calendar.getInstance();
		int yy=d.get(Calendar.YEAR);
		int mm=d.get(Calendar.MONTH)+1;
		int dd=d.get(Calendar.DATE);
		StringBuffer ext_sql = new StringBuffer();
		ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
		ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
		ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
		ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
		ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
		ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
		sql.delete(0, sql.length());
		sql.append("select reporttypes,unitcode from tt_organization where unitcode = (select unitcode from operuser where username = '");
		sql.append(userName);
		sql.append("') "+ext_sql+"");

		String reportTypes = "";
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				//reportTypes = (String) rs.getString("reporttypes");
				reportTypes = Sql_switcher.readMemo(rs, "reporttypes");
				unitcode=(String) rs.getString("unitcode");
				if (reportTypes == null||reportTypes.trim().length()==0) {
					// 用户没有权限操作任何报表
					
					return null;
				} else {
					if (reportTypes.charAt(reportTypes.length() - 1) == ',') {
						reportTypes = reportTypes.substring(0, reportTypes.length() - 1);
					}

				}

			} else {
				
				//从资源里找
			sql.delete(0, sql.length());
			sql.append("select TSortId sortid,Name sortname from TSort");
			rs = dao.search(sql.toString());
			while(rs.next())
    		{
    			reportTypes+=rs.getString("sortid")+",";
    		}
			if (reportTypes!=null && reportTypes.trim().length()>0 && reportTypes.charAt(reportTypes.length() - 1) == ',') {
				reportTypes = reportTypes.substring(0, reportTypes.length() - 1);
			}
			}
			if (reportTypes == null|| "".equals(reportTypes.trim())) {
				return null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		boolean isCorrect=true;
        while(isCorrect)
        {
        	if (reportTypes.charAt(reportTypes.length() - 1) == ',') 
        	{
        		reportTypes = reportTypes.substring(0, reportTypes.length() - 1);
        	}else
        	{
        		isCorrect=false;
        	}
        }
		sql.delete(0, sql.length());
		HashMap map = new HashMap();
		if(unitcode.trim().length()==0){
			sql.append("select tabid,name from tname order by tabid");//不负责表那么就只能看到自己权限范围下的表，跟单位无关，即和上报标识无关  zhaoxg 2013-11-8
			//sql.append("select tname.tabid ,tname.name,treport_ctrl.status  from tname,treport_ctrl where  tname.tabid=treport_ctrl.tabid and  tname.tsortid in ("+ reportTypes + ") order by tname.tabid");
		}else{
		sql.append("select tname.tabid ,tname.name,treport_ctrl.status  from tname,treport_ctrl where  tname.tabid=treport_ctrl.tabid and  tname.tsortid in ("+ reportTypes + ") and treport_ctrl.unitcode='"+unitcode+"' order by tname.tabid");
		}
		rs = null;
		try {
			rs = dao.search(sql.toString());
			while (rs.next()) {
				RecordVo vo = new RecordVo("tname");
				vo.setString("tabid", String.valueOf(rs.getInt("tabid")));
				if(map.get(""+rs.getInt("tabid"))!=null) {
                    continue;
                }
				map.put(""+rs.getInt("tabid"), ""+rs.getInt("tabid"));
				vo.setString("name", rs.getString("name"));
				if(unitcode.trim().length()==0){
					vo.setInt("paper", -1);   //不负责表那么所有表默认为正在编辑状态，zhaoxg add 2013-11-8
				}else{
					vo.setInt("paper", rs.getInt("status"));   //报表状态，因历史原因，将其放入paper字段
				}

				if(userView.isHaveResource(com.hrms.hjsj.sys.IResourceConstant.REPORT ,String.valueOf(rs.getInt("tabid")))){
				list.add(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}

		
		return list;
		
	}
	public ArrayList getCustomReportList(UserView userView) throws GeneralException{
		
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		HashMap fmap = new HashMap();
		 sql.append(" SELECT tabid,flaga FROM rname ");
		 try {
				rs = dao.search(sql.toString());
				while (rs.next()) {
					fmap.put("2:"+rs.getInt("tabid"), rs.getString("flaga"));
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			sql.delete(0, sql.length());
		HashMap hmap = new HashMap();
		 sql.append(" SELECT tabid,nmodule FROM muster_name ");
		 try {
				rs = dao.search(sql.toString());
				while (rs.next()) {
					hmap.put("3:"+rs.getInt("tabid"), rs.getString("nmodule"));
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			sql.delete(0, sql.length());
			HashMap tmap = new HashMap();
			 sql.append(" select id,name from t_hr_subsys where is_available='1' ");
			 try {
					rs = dao.search(sql.toString());
					while (rs.next()) {
						tmap.put(rs.getString("id"), ""+rs.getString("name"));
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
		sql.delete(0, sql.length());
		sql.append("select id,name,report_type,moduleid,ext,link_tabid from t_custom_report where flag=1 order by moduleid,id ");
		LazyDynaBean bean = null; 
		try {
			rs = dao.search(sql.toString());
			while (rs.next()) {
				if(userView.isHaveResource(IResourceConstant.CUSTOM_REPORT,rs.getString("id"))){
					bean = new LazyDynaBean();
					bean.set("id", String.valueOf(rs.getInt("id")));
					bean.set("name", rs.getString("name"));
					bean.set("report_type", rs.getString("report_type"));
					bean.set("moduleid", rs.getString("moduleid"));
					bean.set("ext", rs.getString("ext"));
					bean.set("link_tabid", rs.getString("link_tabid"));
					String flaga="";
					if(fmap!=null&&fmap.get(rs.getString("report_type")+":"+rs.getString("link_tabid"))!=null){
						if("A".equals(fmap.get(rs.getString("report_type")+":"+rs.getString("link_tabid")))) {
                            flaga="1";
                        } else if("B".equals(fmap.get(rs.getString("report_type")+":"+rs.getString("link_tabid")))) {
                            flaga="2";
                        } else if("K".equals(fmap.get(rs.getString("report_type")+":"+rs.getString("link_tabid")))) {
                            flaga="4";
                        }
						bean.set("flaga", flaga);
					}else{
						bean.set("flaga", "");
					}
					if(hmap!=null&&hmap.get(rs.getString("report_type")+":"+rs.getString("link_tabid"))!=null){
						
						bean.set("module", hmap.get(rs.getString("report_type")+":"+rs.getString("link_tabid")));
						if("3".equals(hmap.get(rs.getString("report_type")+":"+rs.getString("link_tabid")))){
							bean.set("a_inforkind", "1");
						}else if("21".equals(hmap.get(rs.getString("report_type")+":"+rs.getString("link_tabid")))){
							bean.set("a_inforkind", "2");
						}else if("41".equals(hmap.get(rs.getString("report_type")+":"+rs.getString("link_tabid")))){
							bean.set("a_inforkind", "3");
						}else {
                            bean.set("a_inforkind", "");
                        }
					}else{
						bean.set("module", "");
						bean.set("a_inforkind", "");
						
					}
					if(tmap!=null&&tmap.get(rs.getString("moduleid"))!=null){
						bean.set("moduleid", tmap.get(rs.getString("moduleid")));
					}else{
						bean.set("moduleid", "");
					}
					
					list.add(bean);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally{
			if(rs!=null) {
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

