
package com.hjsj.hrms.transaction.report.auto_fill_report;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.AnalyseParams;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * @author 
 *
 */
public class ReportOptionTrans extends IBusiness {

	//设置扫描库及截止日期
	public void execute() throws GeneralException {	
        HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String xml = "";
		String userName = userView.getUserName();
		try{	
			//常量表中查找rp_param常量
			this.frowset=dao.search("select STR_VALUE  from CONSTANT where CONSTANT='RP_PARAM'");
			if(this.frowset.next()){
				//System.out.println("存在该记录");
				int dbserver = Sql_switcher.searchDbServer();
				if(dbserver == 2){//oracle
					//System.out.println("oracle");
					xml = Sql_switcher.readMemo(this.frowset,"STR_VALUE");
				}else{ //mssql
					//System.out.println("mssql");
					//获取XML文件
					xml = Sql_switcher.readMemo(this.frowset,"STR_VALUE");
				}
				
			/*	System.out.println("数据库中的XML文件。。。。。。");
				System.out.println("XML=" + xml);
				System.out.println("数据库中的XML文件。。。。。。");*/
			}else{
				System.out.println(ResourceFactory.getProperty("auto_fill_report.batchFillData.info8"));
				//常量表中没有RP_PARAM字段,常量表中添加一条记录RP_PARAM
				dao.insert("insert into CONSTANT (CONSTANT) values('RP_PARAM')",new ArrayList());
				xml = null ;
			}
			
		String tabid = (String)hm.get("code");
		TnameBo tnameBo = null;
		if(tabid != null) {
		    tnameBo = new TnameBo(this.getFrameconn(), tabid, userView.getUserId(), userName, "view");
		    hm.remove("code");
		}
		
		//xml文件分析类
		AnalyseParams aps = new AnalyseParams(xml);
		
		//String sql="select * from dbname";
		
		ArrayList list = new ArrayList();	//
		
		String result = "";  //扫描查询结果
		String appdate =""; //截止日期	
		String startdate=""; //启始日期
		if(ConstantParamter.getAppdate(this.userView.getUserName())!=null)
		{
			String value=ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
			appdate=value;
			startdate=value;
		}
		//授权当前用户的应用库列表
		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		ArrayList dblist=this.userView.getPrivDbList();
		
		dblist=dbvo.getDbNameVoList(dblist);
		
		if(tnameBo != null && tnameBo.isDataScopeEnabled()) {
		    result = "false";
		    
		    if("update2".equals(hm.get("b_update2"))) {
	            appdate = (String)this.getFormHM().get("appdate");
	            startdate = (String)this.getFormHM().get("startdate");
	            ArrayList sel = (ArrayList)this.getFormHM().get("selectedlist");
                for(int i = 0 ; i< dblist.size(); i++){
                    RecordVo vo = (RecordVo)dblist.get(i);
                    vo.setString("flag", "0");
                    for(int j=0; j< sel.size(); j++){
                        RecordVo selvo = (RecordVo) sel.get(j);
                        if(selvo.getString("pre").equals(vo.getString("pre"))) {
                            vo.setString("flag","1");
                            break;
                        }
                    }
                    list.add(vo);
                }
	            hm.remove("b_update2");
		    }
		    else {
	            appdate = tnameBo.getAppdate();
	            startdate = tnameBo.getStartdate();
                for(int i = 0 ; i< dblist.size(); i++){
                    RecordVo vo = (RecordVo)dblist.get(i);
                    if(tnameBo.isSelNbase(vo.getString("pre")))
                        vo.setString("flag","1");
                    else
                        vo.setString("flag","0");
                    list.add(vo);
                }
            }
		}
		else if(aps.checkUserid(userName)){//DB中存在当前用户的扫描库配置信息
			//用户配置信息封装在MAP内
			HashMap pub = aps.getAttributeValues(userName);
			String databaselist = (String)pub.get("databaselist");//DB列表
			result = (String)pub.get("result");  //扫描查询结果
			appdate = (String)pub.get("appdate");//截止日期
			startdate=(String)pub.get("startdate"); //起始日期
	/*		
			//无设置扫描库则默认为在职人员库
			if(databaselist == null || databaselist.equals("")){
				for(int i = 0 ; i< dblist.size(); i++){
					RecordVo vo = (RecordVo)dblist.get(i);
					String pre = vo.getString("pre");
					if(pre.equals("Usr")){
						vo.setString("flag","1");
					}else{
						vo.setString("flag","0");
					}
					list.add(vo);
				}
			}else{*/
				for(int i = 0 ; i< dblist.size(); i++){
					RecordVo vo = (RecordVo)dblist.get(i);
					String pre = vo.getString("pre");
					if(dblist.size()==1)
						vo.setString("flag","1");
					else
					{
						if(databaselist.indexOf(pre)!=-1){
							vo.setString("flag","1");
						}else{
							vo.setString("flag","0");
						}
					}
					list.add(vo);
				}
		/*	}*/
		
				
		}else{//DB中不存在	
			for(int i = 0 ; i< dblist.size(); i++){
				RecordVo vo = (RecordVo)dblist.get(i);
				if(dblist.size()==1)
					vo.setString("flag","1");
				else
					vo.setString("flag","0");
				list.add(vo);
			}
			
		}//end if
		
		if("true".equals(result)){
			this.getFormHM().put("result","1");
		}else{
			this.getFormHM().put("result","0");
		}
		if("".equals(appdate)||appdate == null){
			this.getFormHM().put("appdate",this.getDate());
		}else{
			this.getFormHM().put("appdate",appdate);
		}
		if("".equals(startdate)||startdate == null){
			this.getFormHM().put("startdate",this.getDate());
		}else{
			this.getFormHM().put("startdate",startdate);
		}
		this.getFormHM().put("dbnamelist" , list);
		this.getFormHM().put("dbprelist" , list);
		String dxt = (String)hm.get("returnvalue");
		String operateObject = (String)hm.get("operateObject");
		String home = (String)hm.get("home");
		if(hm.get("b_query2")!=null&& "link".equals(hm.get("b_query2"))){
			hm.remove("b_query2");
			dxt=null;
		}
		if(dxt!=null&&!"dxt".equals(dxt))
		hm.remove("returnvalue");
		hm.remove("home");
		hm.remove("operateObject");
		if(dxt==null)
			dxt="";
		this.getFormHM().put("returnflag", dxt);
		this.getFormHM().put("home", home);
		this.getFormHM().put("operateObject", operateObject);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
	}

	
	public String getDate(){
		Date currentTime = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss"); 
		return sdf.format(currentTime).substring(0,10); 
	}
}
