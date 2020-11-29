package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class BatchAccreditReportSortSaveTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		ArrayList addreporttypelist = (ArrayList) this.getFormHM().get("selectedrtlist");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		String unitCodes = (String)hm.get("uc");
		//System.out.println("unitCodes=" + unitCodes);
		String [] ucs = unitCodes.split(",");
		String unitcode = ucs[0];
		String analysereportflag = (String)this.getFormHM().get("analysereportflag");
		TTorganization ttorganization=new TTorganization(this.getFrameconn());
		HashMap reportmap =  ttorganization.getReportTsort();
		String tempreporttypes =this.getReportTypes2(addreporttypelist); 
		if(addreporttypelist == null || addreporttypelist.size()==0){
			//为空则保存原有状态
		}else{
			try{
			for(int k = 0; k< ucs.length; k++){//选中的要授权的填报单位
				String ucss = (String)ucs[k];
				/*
				StringBuffer addsql = new StringBuffer();			
				//报表分类为空(不做任何处理)(是否取消选中的填报单位的授权信息?)
				if (addreporttypelist == null || addreporttypelist.size() == 0){	
					
					addsql.append("update tt_organization set reporttypes = null where unitcode like '");
					addsql.append(ucss);
					addsql.append("%'");
					
					break;
				}else{
					addsql.append("update tt_organization set reporttypes  ='");
					addsql.append(this.getReportTypes(addreporttypelist));
					addsql.append("' where unitcode like'");
					addsql.append(ucss);
					addsql.append("%'");
				}
				
				System.out.println("sql=" + addsql.toString());
				try {
					dao.update(addsql.toString());
				} catch (Exception sqle) {
					sqle.printStackTrace();
					throw GeneralExceptionHandler.Handle(sqle);
				}
				
				System.out.println("选中的表类大小="+this.getReportTypes(addreporttypelist));
				System.out.println("授权的填报单位为:" + unitCodes);
				*/
				
				ArrayList ucList = this.getBatchUnitCode(ucss);
				for(int g = 0; g<ucList.size(); g++){ //每一个填报单位的附属集合
					String unitCode = (String)ucList.get(g);//特定的一个填报单位
					
					ArrayList ttt = this.getReportType(addreporttypelist);
					
					String rts = this.getUnitCodeReportTypes(ttt,unitCode);
					
					StringBuffer addsql = new StringBuffer();
					addsql.append("update tt_organization set reporttypes  ='");
					addsql.append(rts);
					addsql.append("' where unitcode ='");
					addsql.append(unitCode);
					addsql.append("'");
					 if(analysereportflag!=null&& "1".equals(analysereportflag)){
						 String analysereports="";
						 this.frowset=dao.search("select * from tt_organization where unitcode='"+unitCode+"'");
							if(this.frowset.next())
							{
								analysereports=Sql_switcher.readMemo(this.frowset,"analysereports");
								
							}
							if(analysereports!=null&&analysereports.length()>0){
								String reports [] =	analysereports.split(",");
								String temptypes =",";
								for(int i=0;i<reports.length;i++){
									if(reports[i].trim().length()>0&&temptypes.indexOf(","+reportmap.get(reports[i].trim())+",")==-1&&reportmap.get(reports[i].trim())!=null){
										temptypes+=reportmap.get(reports[i].trim())+",";
									}
//									if(reports[i].trim().length()>0&&(","+tempreporttypes+",").indexOf(","+reportmap.get(reports[i].trim())+",")==-1){
//										analysereports = analysereports.replace(","+reports[i]+",", ",");
//									}
								}
							String[] temp=tempreporttypes.split(",");
							for(int i=0;i<temp.length;i++)
							{
								if(temp[i]!=null&&temp[i].length()>0)
								{
									if(temptypes.indexOf(","+temp[i]+",")==-1){
									String searchSql = "select tabid  from tname where tsortid = ' " + temp[i] +"'";
									this.frowset= dao.search(searchSql);
									while(this.frowset.next())
									{
											String tabid=this.frowset.getString("tabid");
											if(analysereports.indexOf(","+tabid+",")==-1)
											{
												analysereports+=tabid+",";
											}
									}
								}
								}
							}
							}else{
							String[] temp=tempreporttypes.split(",");
							analysereports=",";
							for(int i=0;i<temp.length;i++)
							{
								if(temp[i]!=null&&temp[i].length()>0)
								{
									
									String searchSql = "select tabid  from tname where tsortid = ' " + temp[i] +"'";
									this.frowset = dao.search(searchSql);
									while(this.frowset.next())
									{
											String tabid=this.frowset.getString("tabid");
											if(analysereports.indexOf(","+tabid+",")==-1&&analysereports.indexOf(","+tabid+",")==-1)
											{
												analysereports+=tabid+",";
											}
									}
								}
							}
							}
						 addsql.setLength(0);
						 addsql.append("update tt_organization set analysereports  ='");
							addsql.append(analysereports);
							addsql.append("' where unitcode ='");
							addsql.append(unitCode);
							addsql.append("'");
					 }
					//System.out.println(addsql.toString());
					
					try {
						dao.update(addsql.toString());
					} catch (Exception sqle) {
						sqle.printStackTrace();
						throw GeneralExceptionHandler.Handle(sqle);
					}
					
					ArrayList list = this.getUnitCodeReportType(ttt,unitCode);
					this.executeSQ(dao,list,unitCode);
					
				}
				
			}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		//添加要跳转到页面的参数
		this.getFormHM().put("unitCodeFalg",unitcode);
	}
	
	/**
	 * 填报单位表类授权
	 * @param dao
	 * @param addreporttypelist
	 * @param unitCode
	 * @throws GeneralException
	 */
	public void executeSQ(ContentDAO dao ,ArrayList  addreporttypelist , String unitCode ) throws GeneralException{
		//保留原由相同设置
		//设置填报单位上报标识信息
		StringBuffer t = new StringBuffer();
		for(int i = 0; i<addreporttypelist.size();i++){
			/*
			RecordVo vo = (RecordVo) addreporttypelist.get(i);
			String tsortid = vo.getString("tsortid");
			*/
			String tsortid = (String)addreporttypelist.get(i);
			
			//报表类中所有报表ID
			String searchSql = "select tabid  from tname where tsortid = ' " + tsortid +"'";
			RowSet rss = null;
			try {
				rss = dao.search(searchSql);						
				//sql中not in部分	
				while(rss.next()){						
					String tid = rss.getString("tabid");//某一个报表
					String checkSql = "select * from treport_ctrl where unitcode  = '" +unitCode+ "' and tabid=" + tid;						
					if(this.isExistDB(checkSql)){
						//存在，保持原有状态 用户追加信息						
					}else{
						//不存在，添加新记录
						String insertSql = "insert into treport_ctrl (unitcode , tabid ,status) values( '"+ unitCode +"' , "+ tid +" , -1 )";
						this.insertDB(insertSql);
					}
					t.append(tid);
					t.append(",");
				}				
			} catch (Exception sqle) {
				sqle.printStackTrace();
				throw GeneralExceptionHandler.Handle(sqle);
			}
		}//end for
		
		//未选中任何分类
		if(t == null || "".equals(t.toString())){
			String deleteSql = "delete from treport_ctrl where unitcode = '"+unitCode+"' ";
			this.deleteDB(deleteSql);
		}else{
			String tt = t.toString();
			tt = tt.substring(0,tt.length()-1);
			String deleteSql = "delete from treport_ctrl where unitcode = '"+unitCode+"' and tabid not in("+tt+")";
			this.deleteDB(deleteSql);
		}
		
	}
	/**
	 * 获得批量的填报单位
	 * @param baseUnitCode
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getBatchUnitCode(String baseUnitCode) throws GeneralException{
		ArrayList list = new ArrayList();
		String sql="select unitcode from tt_organization where unitcode like'"+baseUnitCode+"%' ";// and unitcode <> '" + baseUnitCode+"'";
		Calendar d=Calendar.getInstance();
		int yy=d.get(Calendar.YEAR);
		int mm=d.get(Calendar.MONTH)+1;
		int dd=d.get(Calendar.DATE);
		StringBuffer ext_sql = new StringBuffer();
		ext_sql.append("  and ( "+Sql_switcher.year("end_date")+">"+yy);
		ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
		ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
		ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
		ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
		ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
		sql+=ext_sql.toString();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rs = null;
		try {
			rs  = dao.search(sql);
			while(rs.next()){
				String unitCode = rs.getString("unitcode");
				list.add(unitCode);
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
		//System.out.println("size=" + list.size());
		return list;
		
	}
	

	/**
	 * 验证上报信息表数据是否存在
	 * @param sql
	 * @return
	 * @throws GeneralException
	 */
	public boolean isExistDB(String sql) throws GeneralException{
		boolean b = false;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rs = null;
		try {
			rs  = dao.search(sql);
			if(rs.next()){
				b=true;
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
		return b;
	}
	/**
	 * 删除填报单位上报信息
	 * @param sql
	 * @throws GeneralException
	 */
	public void deleteDB(String sql) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.delete(sql,new ArrayList());
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}
	
	
	/**
	 * 设置填报单位上报信息
	 * @param sql
	 * @throws GeneralException
	 */
	public void insertDB(String sql) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.insert(sql , new ArrayList());
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}
	
	/**
	 * 特定填报单位授权表类集合
	 * @param list
	 * @param unitCode
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getUnitCodeReportType(ArrayList list , String unitCode) throws GeneralException{
		ArrayList ucrt = new ArrayList();
		String sql="select reporttypes  from tt_organization  where unitcode = '"+unitCode+"'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			RowSet rs = dao.search(sql);
			String rts ="";
			if(rs.next()){
				rts = rs.getString("reporttypes");
			}
			if(rts == null || "".equals(rts)){
				ucrt = list;
			}else{
				String [] temp = rts.split(",");
				for(int i=0; i<temp.length;i++){
					String t = temp[i];
					if(list.contains(t)){
					}else{
						list.add(t);
					}
				}
				ucrt = list;
			}
			
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
		return ucrt;
	}
	
	
	/**
	 * 获得某一个特定填报单位的实际报表授权类别(批量+表类划分之和)
	 * @param list
	 * @param unitCode
	 * @return
	 * @throws GeneralException
	 */
	public String getUnitCodeReportTypes(ArrayList list , String unitCode) throws GeneralException{
		String reportTypes = "";
		String sql="select reporttypes  from tt_organization  where unitcode = '"+unitCode+"'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			RowSet rs = dao.search(sql);
			String rts ="";
			if(rs.next()){
				rts = rs.getString("reporttypes");
			}
			if(rts == null || "".equals(rts)){
				reportTypes=this.getReportTypes(list);
			}else{
				String [] temp = rts.split(",");
				for(int i=0; i<temp.length;i++){
					String t = temp[i];
					if(list.contains(t)){
					}else{
						list.add(t);
					}
				}
				reportTypes=this.getReportTypes(list);
			}
			
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
		
		return reportTypes;
	}
	
	/**
	 * 选中的表类集合
	 * @param list
	 * @return
	 */
	public ArrayList getReportType(ArrayList list){
		ArrayList temp = new ArrayList();
		for (int i = 0; i < list.size(); i++) {	
			RecordVo vo = (RecordVo) list.get(i);
			temp.add(vo.getString("tsortid"));
		}
		return temp;
	}
	/**
	 * 报表类别字符串（逗号分割）
	 * @param list
	 * @return
	 */
	public String getReportTypes(ArrayList list) {
		
		StringBuffer temp = new StringBuffer();	
		for (int i = 0; i < list.size(); i++) {	
			String te = (String) list.get(i);
			temp.append(te);
			temp.append(",");
		}
		return temp.toString();
		
	}
	/**
	 * 报表类别字符串（逗号分割）
	 * @param list
	 * @return
	 */
	public String getReportTypes2(ArrayList list) {
		
		StringBuffer temp = new StringBuffer();	
		for (int i = 0; i < list.size(); i++) {	
			RecordVo vo = (RecordVo) list.get(i);
			temp.append(vo.getString("tsortid"));
			temp.append(",");
		}
		return temp.toString();
		
	}
	
	
	
	

}
