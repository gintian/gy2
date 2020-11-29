
package com.hjsj.hrms.transaction.report.auto_fill_report;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.AnalyseParams;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:设置扫描库</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 18, 2006:9:43:13 AM</p>
 * @author zhangfengjin
 * @version 1.0
 *
 */
public class ReportOptionUpdateTrans extends IBusiness {

	//保存扫描库及截止日期
	public void execute() throws GeneralException {
        HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
        String tabid = (String)hm.get("code");
	    TnameBo tnameBo = null;
	    if(tabid != null) {
	        tnameBo = new TnameBo(this.getFrameconn(), tabid, userView.getUserId(), userView.getUserName(), "view");
	        //hm.remove("code");
	    }
	    if(tnameBo != null && tnameBo.isDataScopeEnabled())  // 指定取数范围，不更新全局设置
	        return;
		ArrayList list = (ArrayList)this.getFormHM().get("selectedlist");
		String result="flase";
		String temp = (String)this.getFormHM().get("checked");
		if("1".equals(temp)){
			result="true";
		}
		String appdate = (String)this.getFormHM().get("appdate");
		String startdate=(String)this.getFormHM().get("startdate");
		StringBuffer dblist = new StringBuffer();
		for(int i=0; i< list.size(); i++){
			RecordVo vo = (RecordVo) list.get(i);
			dblist.append(vo.getString("pre"));
			dblist.append(",");
		}
		
		String userName = userView.getUserName().toLowerCase();
		String userStatus = String.valueOf(userView.getStatus());

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String xxml = "";
		try{	
			//常量表中查找rp_param常量
			this.frowset=dao.search("select STR_VALUE  from CONSTANT where CONSTANT='RP_PARAM'");
			if(this.frowset.next()){
				int dbserver = Sql_switcher.searchDbServer();
				if(dbserver == 2){//oracle
					/*System.out.println("oracle");*/
					xxml = Sql_switcher.readMemo(this.frowset,"STR_VALUE");
				}else{ //mssql
					//System.out.println("mssql");
					//获取XML文件
					xxml = Sql_switcher.readMemo(this.frowset,"STR_VALUE");
				}
				
			/*	System.out.println("数据库中的XML文件。。。。。。");
				System.out.println(xxml);
				System.out.println("数据库中的XML文件。。。。。。");*/
			}
			String aap="";
			if(appdate!=null&&appdate.length()!=0&&appdate.indexOf("-")!=-1){
				String []pa=appdate.split("-");
				
				for(int i=0;i<pa.length;i++){
					if(i!=pa.length-1)
						aap+=pa[i]+".";
					else
						aap+=pa[i];
				}
				
			}
			ConstantParamter.putAppdate(this.userView.getUserName(), aap);

		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
		
		//xml文件分析类
		AnalyseParams ap = new AnalyseParams(xxml);
		
		//xml空错误
		//AnalyseParams ap = new AnalyseParams();
		
		String xml = ap.paramSet(userName,userStatus,dblist.toString(),result,appdate,startdate);
		
		//System.out.println("xml文件=" + xml );
		
		StringBuffer sql = new StringBuffer();
		sql.append("update CONSTANT set STR_VALUE='");
		sql.append(xml);
		sql.append("' where CONSTANT = 'RP_PARAM'");
	
	
		
		
		dao = new ContentDAO(this.getFrameconn());
		try{
			/*System.out.println("sql=" + sql.toString());*/
			//dao.update(sql.toString());
			RecordVo vo=new RecordVo("constant");
			vo.setString("constant", "RP_PARAM");
			vo=dao.findByPrimaryKey(vo);
			vo.setString("str_value",xml);
			dao.updateValueObject(vo);
			ConstantParamter.putConstantVo(vo, "RP_PARAM");//liuy 2015-4-22 8887 8890
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
	}

}
