package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.general.statics.ShowExcel;
import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * 导出二维统计表
 * 
 * <p>Title: ExportExcelTrans </p>
 * <p>Company: hjsj</p>
 * <p>create time  Dec 16, 2014 11:08:47 AM</p>
 * @author liuy
 * @version 1.0
 */
public class ExportExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String statid=(String)this.getFormHM().get("statid");
			String result=(String)this.getFormHM().get("result");
			String a_code=(String)this.getFormHM().get("a_code");
			String lexprId=(String)this.getFormHM().get("lexprId");//常用查询条件
			String condid="";
			String userbase="";
			ArrayList dblist=new ArrayList();
			String name = "";
			String sql="select * from sname where id=" + statid;				
			this.frowset=dao.search(sql.toString());
         	if(this.frowset.next()){
         		name = this.frowset.getString("name");
				userbase=(String)this.getFormHM().get("userbases");
				if(userbase!=null&&userbase.length()>0){
					String [] baseS=userbase.split(",");
					for(int i=0;i<baseS.length;i++){
						if(baseS[i]!=null&&baseS[i].length()>0){
							if(!this.userView.isSuper_admin()){
			         			ArrayList nb_list=this.userView.getPrivDbList();
			         			for(int r=0;r<nb_list.size();r++){
		                    		String ubase=nb_list.get(r).toString();
		                    		if(baseS[i].equalsIgnoreCase(ubase)){
		                    			dblist.add(baseS[i]);
		                    		}
		                    	} 
			                }else
			                	dblist.add(baseS[i]);
						}
					}
				}
				condid = this.frowset.getString("condid");
      	    }
         	boolean isresult=true;
    	    if(result==null|| "".equals(result)|| "0".equals(result))
    	    	isresult=true; 
    	    else
    	    	isresult =false;
    	    StatDataEncapsulation simplestat=new StatDataEncapsulation();
    		ArrayList condlist=simplestat.getCondlist(condid,dao);
    	    if(lexprId==null||lexprId.length()<=0){
    			if(condlist!=null&&condlist.size()>0){
    				CommonData da=(CommonData)condlist.get(0);
    				lexprId=da.getDataValue();
    			}
    		}
    		String []curr_id=null;
    		String commlexr=null;
    	    String commfacor=null;
    	    String history="";
    		if(lexprId!=null&&lexprId.length()>0){
    			//加上常用查询进行的统计
    			curr_id=new String[1];
    			curr_id[0]=lexprId;			
    		}		
    	    if(curr_id!=null&&curr_id.length>0){
    	    	GeneralQueryStat generalstat=new GeneralQueryStat();
    			generalstat.getGeneralQueryLexrfacor(curr_id,userbase,"",this.getFrameconn());	    
    	    	commlexr=generalstat.getLexpr();
    	    	commfacor=generalstat.getLfactor();
    	    	history = generalstat.getHistory();
    	    }
			int[][] statValues;
			statValues=simplestat.getDoubleLexprData(Integer.parseInt(statid),dblist,a_code,userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,commlexr,commfacor,result,history);
			List varraylist=simplestat.getVerticalArray();
			List harraylist=simplestat.getHorizonArray();
			String snameplay=simplestat.getSNameDisplay();
			int totalvalue=simplestat.getTotalValue();
			ShowExcel show= new ShowExcel(this.getFrameconn());
			String filename = this.userView.getUserName()+"_"+name+".xls";
			filename = show.creatExcel(statValues, varraylist, harraylist, snameplay, totalvalue,filename);
			//filename=filename.replace(".xls","#");
			//20/3/17 xus vfs改造
			filename = PubFunc.encrypt(filename);
			this.getFormHM().put("filename", filename);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
