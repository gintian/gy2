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
import java.util.HashMap;
public class AddReportTypeListTrans extends IBusiness {

	//表类划分
	public void execute() throws GeneralException {
		try {
		HashMap allSelectMap = (HashMap)this.getFormHM().get("allselectedlist");
		ArrayList allselectedlist = new ArrayList();
		ArrayList addreporttypelist = (ArrayList) this.getFormHM().get("selectedrtlist");
	    ArrayList addreporttypelistall = (ArrayList) this.getFormHM().get("selectedrtlistall");//当前页所有值
		//显示报表类别列表所需要的参数
		String unitCode = (String)this.getFormHM().get("rtunitcode");
		//System.out.println("unitcode=" + unitCode);
		StringBuffer addsql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String analysereportflag = (String)this.getFormHM().get("analysereportflag");
		String report="";
		String reportTypes="";
		String now_reportTypes="";
		String analysereports ="";
		for(int i = 0;i<allSelectMap.keySet().toArray().length;i++){
			allselectedlist.add(allSelectMap.keySet().toArray()[i]);
		}
		this.frowset=dao.search("select * from tt_organization where unitcode='"+unitCode+"'");
		if(this.frowset.next())
		{
			report=Sql_switcher.readMemo(this.frowset,"report");
			reportTypes=","+Sql_switcher.readMemo(this.frowset,"reporttypes");
			analysereports=Sql_switcher.readMemo(this.frowset,"analysereports");
		}
		
		if(analysereportflag!=null&& "1".equals(analysereportflag)){
				
		
				if(analysereports!=null&&analysereports.length()>0){

					
					TTorganization ttorganization=new TTorganization(this.getFrameconn());
					HashMap reportmap =  ttorganization.getReportTsort();
					String reports [] =	analysereports.split(",");
					String temptypes =",";
					for(int i=0;i<reports.length;i++){
						if(reports[i].trim().length()>0&&temptypes.indexOf(","+reportmap.get(reports[i].trim())+",")==-1&&reportmap.get(reports[i].trim())!=null){
							temptypes+=reportmap.get(reports[i].trim())+",";
						}
					}
					String tempreporttypes =this.getReportTypes(allselectedlist,temptypes,addreporttypelistall); 
					for(int i=0;i<reports.length;i++){
						if(reports[i].trim().length()>0&&(","+tempreporttypes+",").indexOf(","+reportmap.get(reports[i].trim())+",")==-1){
							analysereports = analysereports.replace(","+reports[i]+",", ",");
						}
					}
				String[] temp=tempreporttypes.split(",");
				for(int i=0;i<temp.length;i++)
				{
					if(temp[i]!=null&&temp[i].length()>0)
					{
						if(temptypes.indexOf(","+temp[i]+",")==-1){
						String searchSql = "select tabid  from tname where tsortid = ' " + temp[i] +"'";
						RowSet rss = dao.search(searchSql);
						while(rss.next())
						{
								String tabid=rss.getString("tabid");
								if(analysereports.indexOf(","+tabid+",")==-1)
								{
									analysereports+=tabid+",";
								}
						}
					}
					}
				}
				}else{
					String tempreporttypes =this.getReportTypes(allselectedlist,"",addreporttypelistall); 
				String[] temp=tempreporttypes.split(",");
				analysereports=",";
				for(int i=0;i<temp.length;i++)
				{
					if(temp[i]!=null&&temp[i].length()>0)
					{
						
						String searchSql = "select tabid  from tname where tsortid = ' " + temp[i] +"'";
						RowSet rss = dao.search(searchSql);
						while(rss.next())
						{
								String tabid=rss.getString("tabid");
								if(report.indexOf(","+tabid+",")==-1&&analysereports.indexOf(","+tabid+",")==-1)
								{
									analysereports+=tabid+",";
								}
						}
					}
				}
				}
				if(analysereports.length()==1)
					analysereports="";
				addsql.append("update tt_organization set analysereports = '");
				addsql.append(analysereports);
				addsql.append("' where unitcode= '");
				addsql.append(unitCode);
				addsql.append("'");
			
		}else{
		//报表分类为空
	
			addsql.append("update tt_organization set reporttypes = '");
			addsql.append(this.getReportTypes(allselectedlist,reportTypes,addreporttypelistall));
			addsql.append("' where unitcode= '");
			addsql.append(unitCode);
			addsql.append("'");
		
		}
		
		dao.update(addsql.toString());
		
		if(analysereportflag==null||!"1".equals(analysereportflag)){
		//report=","+report;
		
		//保留原由相同设置
		//设置填报单位上报标识信息
		StringBuffer t = new StringBuffer();
		for(int i = 0; i<allselectedlist.size();i++){
			RecordVo vo = (RecordVo) allselectedlist.get(i);
			String tsortid = vo.getString("tsortid");
			
			now_reportTypes+=","+tsortid;
			//报表类中所有报表ID
			String searchSql = "select tabid  from tname where tsortid = ' " + tsortid +"'";
			RowSet rss = null;
			try {
				rss = dao.search(searchSql);
				
				//sql中not in部分	
				while(rss.next()){
					
					String tid = rss.getString("tabid");//某一个报表
					String checkSql = "select * from treport_ctrl where unitcode = '" +unitCode+ "' and tabid=" + tid;
					
					if(this.isExistDB(checkSql)){
						//存在，保持原有状态 用户追加信息						
					}else{
						//不存在，添加新记录
						if(report.indexOf(","+tid+",")==-1)
						{
							String insertSql = "insert into treport_ctrl (unitcode , tabid ,status) values( '"+ unitCode +"' , "+ tid +" , -1 )";
							this.insertDB(insertSql);
						}
					}
						
					if(report.indexOf(","+tid+",")==-1)
					{
						t.append(tid);
						t.append(",");
					}
					
				}
				/*
				if(report.length()>1){
					this.frowset=dao.search("select * from tt_organization where unitcode like '"+unitCode+"%' and unitcode<>'"+unitCode+"'");
					while(this.frowset.next())
					{
						String unitcode=this.frowset.getString("unitcode");
						String temp=Sql_switcher.readMemo(this.frowset,"report");
						if(temp.trim().length()==0)
						{
							report=report.replaceAll(",,",",");
							dao.update("update tt_organization set report='"+report+"' where unitcode='"+unitcode+"'");
						
						}
						else
						{
							temp=","+temp;
							String[] temps=report.split(",");
							for(int j=0;j<temps.length;j++)
							{
								if(temp.indexOf(","+temps[j]+",")==-1)
									temp+=temps[j]+",";
							}
							temp=temp.replaceAll(",,",",");
							dao.update("update tt_organization set report='"+temp+"' where unitcode='"+unitcode+"'");
						}
					}
				//	System.out.println("delete from  treport_Ctrl where unitcode like 'a%' and tabid in ("+report.substring(1,report.length()-1)+") ");
					dao.delete("delete from  treport_Ctrl where unitcode like '"+unitCode+"%' and tabid in ("+report.substring(1,report.length()-1)+") ",new ArrayList());
					
				}	*/
				
			
			} catch (Exception sqle) {
				sqle.printStackTrace();
				throw GeneralExceptionHandler.Handle(sqle);
			}
		}//end for
		
		
		
		//删除 不用的表类对应的report字段里的值
		String[] temp=reportTypes.split(",");
		now_reportTypes+=",";
		for(int i=0;i<temp.length;i++)
		{
			if(temp[i]!=null&&temp[i].length()>0&&now_reportTypes.indexOf(","+temp[i]+",")==-1)
			{
				
				String searchSql = "select tabid  from tname where tsortid = ' " + temp[i] +"'";
				RowSet rss = dao.search(searchSql);
				while(rss.next())
				{
						String tabid=rss.getString("tabid");
						if(report.indexOf(","+tabid+",")!=-1)
						{
								report=report.replaceAll(","+tabid+",",",");
						}
				}
			}
		}
		dao.update("update tt_organization set report='"+report+"' where unitcode='"+unitCode+"'");
		
		
		
		
		
		
		//未选中任何分类
		if(t == null || "".equals(t.toString())){
			String deleteSql = "delete from treport_ctrl where unitcode = '"+unitCode+"' ";
			this.deleteDB(deleteSql);
		}
	/*	else{
			String tt = t.toString();
			tt = tt.substring(0,tt.length()-1);
			String deleteSql = "delete from treport_ctrl where unitcode = '"+unitCode+"' and tabid not in("+tt+")";
			this.deleteDB(deleteSql);
		}*/
		}
		this.getFormHM().put("unitCodeFalg",unitCode);
		
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
		
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
		}/*finally{
			   if(rs!=null){
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
						throw GeneralExceptionHandler.Handle(e1);
					}
			   }

			}*/
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
	 * 报表类别字符串（逗号分割）
	 * @param list
	 * @param analysereports
	 * @param addreporttypelistall
	 * @return 报表分类
	 */
	public String getReportTypes(ArrayList list, String analysereports,ArrayList addreporttypelistall) {
		TTorganization ttorganization=new TTorganization(this.getFrameconn());
		ArrayList sortidList=ttorganization.getTsortId();
		String  temp = ",";	
		for (int i = 0; i < list.size(); i++) {	
			RecordVo vo = (RecordVo) list.get(i);
			temp +=vo.getString("tsortid")+",";
		}
		String[] split = analysereports.split(",");
		analysereports = "";
		//剔除已删除表类
		for(int i = 0; i< sortidList.size(); i++) {
			String object = (String) sortidList.get(i);
			for(int j = 0;j<split.length;j++) {
				String tabid = split[j];
				if(object.equals(tabid))
					analysereports += ","+tabid;
			}
		}
		if(analysereports.length()>0)
			analysereports += ",";
		//库中的值去掉当前页
		if(analysereports!=null&&analysereports.trim().length()>0){
			analysereports = ","+analysereports+",";
			for (int i = 0; i < addreporttypelistall.size(); i++) {	
				RecordVo vo = (RecordVo) addreporttypelistall.get(i);
				analysereports = analysereports.replace(","+vo.getString("tsortid")+",", ",");
				if(temp.indexOf(","+vo.getString("tsortid")+",")!=-1){
					analysereports+=vo.getString("tsortid")+",";
				}
			}
		}else{
			analysereports+=temp;
		}
		//当前页中选中的值
		while(analysereports.indexOf(",,")!=-1)
		analysereports = analysereports.replace(",,", ",");
		if(analysereports.startsWith(","))
			analysereports = analysereports.substring(1);
		return analysereports;
		
	}
	
}