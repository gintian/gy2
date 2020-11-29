/*
 * Created on 2006-4-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ReportTypeListTrans extends IBusiness {

	//报表分类表显示
	public void execute() throws GeneralException {	
		//String rtunitcode = (String)(((HashMap)(this.getFormHM().get("requestPamaHM"))).get("rtunitcode"));
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		//查询填报单位表中的报表类别信息//get方式传递的参数，填报单位编码
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String unitCode = (String) hm.get("rtunitcode");
		//System.out.println("unitCode=" + unitCode);
	
	/*	if(unitCode !=null){
			try {
				byte [] str = unitCode.getBytes("ISO8859-1");
				unitCode = new String(str);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}*/
		
		//System.out.println("unitCode=" + unitCode);
		
		ArrayList list = new ArrayList();
		HashMap allSelectMap = new HashMap();
		String analysereportflag = (String)this.getFormHM().get("analysereportflag");
			
		String []reporttypes = this.getReportTypes(unitCode,analysereportflag);
		String sql = "select name , tsortid ,sdes  from tsort order by tsortid";
		try{
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				RecordVo vo = new RecordVo("tsort");
				vo.setString("name" , this.frowset.getString("name"));
				
				String temp = String.valueOf(this.frowset.getInt("tsortid"));
				vo.setString("tsortid",temp);
				
				vo.setString("sdes" ,this.frowset.getString("sdes"));
			
				if(reporttypes == null){
					vo.setString("sid" ,"0");
				}else{
					if(this.checkTSortid(reporttypes,temp)){//DB中有选中的记录		
						vo.setString("sid","1");					
					}else{
						vo.setString("sid" ,"0");
					}					
				}
				if("1".equals(vo.getString("sid"))){
					allSelectMap.put(vo, "");
				}
				list.add(vo);			
			}		
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		this.getFormHM().put("reporttypelist",list);
		this.getFormHM().put("allSelectMap", allSelectMap);
		this.getFormHM().put("rtunitcode",unitCode);
		this.getFormHM().put("unitCodeFalg",unitCode);
	}
	
	//判断填报单位表中的报表类别字符串中是否包括要显示的特定报表分类号
	private boolean checkTSortid(String [] reporttypes , String sortid){
		boolean b = false;
		for(int i = 0 ; i<reporttypes.length; i++){
			if(reporttypes[i].equals(sortid)){
				b=true;
				break;
			}
		}	
		return b;
	}
	
	//返回填报单位表中的报表类别字符串（数组形式）
	private String [] getReportTypes(String unitCode,String analysereportflag)throws GeneralException{	
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String temp = null;
		String [] reporttypes =null;
		TTorganization ttorganization=new TTorganization(this.getFrameconn());
		HashMap reportmap =  ttorganization.getReportTsort();
		StringBuffer reportsql = new StringBuffer();
		reportsql.append("select reporttypes,analysereports  from tt_organization where unitcode = '");
		reportsql.append(unitCode);
		reportsql.append("'");
		
		try {
			this.frowset = dao.search(reportsql.toString());
			if (this.frowset.next()) {
				 temp = Sql_switcher.readMemo(this.frowset, "reporttypes");
				 if(analysereportflag!=null&& "1".equals(analysereportflag)){
						String analysereports =Sql_switcher.readMemo(this.frowset, "analysereports");
						if(analysereports!=null&&analysereports.length()>0){
						String reports [] =	analysereports.split(",");
						String temptypes =",";
						for(int i=0;i<reports.length;i++){
							if(reports[i].trim().length()>0&&temptypes.indexOf(","+reportmap.get(reports[i].trim())+",")==-1&&reportmap.get(reports[i].trim())!=null){
								temptypes+=reportmap.get(reports[i].trim())+",";
							}
						}
						if(temptypes.length()>1)
							temp= temptypes.substring(1,temptypes.length());
						}else{//一个表都没选  赵旭光 2013-4-10
							temp = null;
						}
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		if(temp != null){
			StringTokenizer st = new StringTokenizer(temp , ",");
			reporttypes = new String[st.countTokens()];	
			for(int i = 0; i < reporttypes.length ; i++){
				reporttypes[i] = (String)st.nextElement();
			}
		}
		return reporttypes;
	}

	
}

