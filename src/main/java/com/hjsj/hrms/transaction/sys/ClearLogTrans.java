/*
 * Created on 2005-7-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:ClearLogTrans</p>
 * <p>Description:按条件清空日志，fr_txlog</p>
 * <p>Company:hjsj</p>
 * <p>create time:July 25, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class ClearLogTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String commitor =(String)this.getFormHM().get("commitor");
		String name =(String)this.getFormHM().get("name");
		String beginexectime =(String)this.getFormHM().get("beginexectime");
		String endexectime =(String)this.getFormHM().get("endexectime");
		commitor=PubFunc.getStr(commitor);
		name=PubFunc.getStr(name);
		beginexectime=PubFunc.getStr(beginexectime);
		endexectime=PubFunc.getStr(endexectime);
		
		String bend = (String)this.getFormHM().get("beginexectimeend");
		String eend = (String) this.getFormHM().get("endexectimeend");
		bend = PubFunc.getStr(bend);
		eend = PubFunc.getStr(eend);
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList list=new ArrayList();
        String flag = "no";
        try
        {
        	/*if((commitor == null || commitor.equals("")) && (name == null || name.equals("")) && (beginexectime ==null || beginexectime.equals("")) && (endexectime == null || endexectime.equals(""))){
        		strsql.append("delete from fr_txlog");
        	}else if((commitor == null || commitor.equals("")) && (name == null || name.equals("")) && (beginexectime ==null || beginexectime.equals(""))){
        	   strsql.append("delete from fr_txlog where endexectime = '"+endexectime+"'");
        	}
        	else if((commitor == null || commitor.equals("")) && (name == null || name.equals("")) && (endexectime == null || endexectime.equals(""))){
         	   strsql.append("delete from fr_txlog where beginexectime = '"+beginexectime+"'");
        	}
        	else if((commitor == null || commitor.equals("")) && (beginexectime ==null || beginexectime.equals("")) && (endexectime == null || endexectime.equals(""))){
         	   strsql.append("delete from fr_txlog where name = '"+name+"'");
        	}
        	else if((name == null || name.equals("")) && (beginexectime ==null || beginexectime.equals("")) && (endexectime == null || endexectime.equals(""))){
         	   strsql.append("delete from fr_txlog where commitor = '"+commitor+"'");
         	}else if((commitor == null || commitor.equals("")) && (beginexectime ==null || beginexectime.equals(""))){
          	   strsql.append("delete from fr_txlog where name = '"+name+"' and endexectime = '"+endexectime+"'");
         	}else if((commitor == null || commitor.equals("")) && (endexectime == null || endexectime.equals(""))){
           	   strsql.append("delete from fr_txlog where name = '"+name+"' and beginexectime = '"+beginexectime+"'");
         	}else if((name == null || name.equals("")) && (beginexectime ==null || beginexectime.equals(""))){
           	   strsql.append("delete from fr_txlog where commitor = '"+commitor+"' and endexectime = '"+endexectime+"'");
         	}else if((name == null || name.equals("")) && (endexectime == null || endexectime.equals(""))){
           	   strsql.append("delete from fr_txlog where commitor = '"+commitor+"' and beginexectime = '"+beginexectime+"'");
         	}else if((beginexectime ==null || beginexectime.equals("")) && (endexectime == null || endexectime.equals(""))){
           	   strsql.append("delete from fr_txlog where commitor = '"+commitor+"' and name = '"+name+"'");
         	}else if((commitor == null || commitor.equals("")) && (name == null || name.equals(""))){
           	   strsql.append("delete from fr_txlog where beginexectime = '"+beginexectime+"' and endexectime = '"+endexectime+"'");
         	}else if((commitor == null || commitor.equals(""))){
         		strsql.append("delete from fr_txlog where name = '"+name+"' and beginexectime = '"+beginexectime+"' and endexectime = '"+endexectime+"'");
         	}else if((name == null || name.equals(""))){
         		strsql.append("delete from fr_txlog where commitor = '"+commitor+"' and beginexectime = '"+beginexectime+"' and endexectime = '"+endexectime+"'");
         	}else if((beginexectime ==null || beginexectime.equals(""))){
         		strsql.append("delete from fr_txlog where commitor = '"+commitor+"' and name = '"+name+"' and endexectime = '"+endexectime+"'");
         	}else if((endexectime == null || endexectime.equals(""))){
         		strsql.append("delete from fr_txlog where commitor = '"+commitor+"' and name = '"+name+"' and beginexectime = '"+beginexectime+"'");
         	}else{
        	   strsql.append("delete from fr_txlog where commitor = '"+commitor+"' and name = '"+name+"' and beginexectime = '"+beginexectime+"' and endexectime = '"+endexectime+"'");
         	}
        	if(strsql.indexOf("where")!=-1){
        		strsql.append(" and endexectime is not null");
        		if(Sql_switcher.searchDbServer()==1)
        			strsql.append(" and endexectime<>''");
        	}else{
        		strsql.append(" where endexectime is not null");
        		if(Sql_switcher.searchDbServer()==1)
        			strsql.append(" and endexectime<>''");
        	}*/
        	
        	//操作日志按日期查询  jingq upd 2014.09.15
        	StringBuffer timesql = new StringBuffer();
        	//开始时间
        	if(!"".equals(beginexectime)&&!"".equals(bend)&&!beginexectime.equals(bend)){
        		timesql.append(" and beginexectime between '"+beginexectime+" 00:00:00' and '"+bend+" 23:59:59'");
        	} else if("".equals(beginexectime)&&!"".equals(bend)){
        		timesql.append(" and beginexectime <= '"+bend+"'");
        	} else if(!"".equals(beginexectime)&&"".equals(bend)){
        		timesql.append(" and beginexectime >= '"+beginexectime+"'");
        	} else if(!"".equals(beginexectime)&&!"".equals(bend)&&beginexectime.equals(bend)){
        		timesql.append(" and beginexectime like '%"+beginexectime+"%'");
        	} else {
        		timesql.append("");
        	}
        	//结束时间
        	if(!"".equals(endexectime)&&!"".equals(eend)&&!endexectime.equals(eend)){
        		timesql.append(" and endexectime between '"+endexectime+" 00:00:00' and '"+eend+" 23:59:59'");
        	} else if("".equals(endexectime)&&!"".equals(eend)){
        		timesql.append(" and endexectime <= '"+eend+"'");
        	} else if(!"".equals(endexectime)&&"".equals(bend)){
        		timesql.append(" and endexectime >= '"+endexectime+"'");
        	} else if(!"".equals(endexectime)&&!"".equals(eend)&&endexectime.equals(eend)){
        		timesql.append(" and endexectime like '%"+endexectime+"%'");
        	} else {
        		timesql.append("");
        	}
        	
        	boolean haswhere = true;
        	StringBuffer strsql=new StringBuffer();
        	if("".equals(commitor)&&"".equals(name)&&"".equals(timesql)){
        		strsql.append("delete from fr_txlog where 1=1");
        		haswhere =false;
        	} else if(!"".equals(commitor)&&!"".equals(name)){
        		strsql.append("delete from fr_txlog where commitor like '%"+commitor+"%' and name like '%"+name+"%'");
        	} else if(!"".equals(commitor)&&"".equals(name)){
        		strsql.append("delete from fr_txlog where commitor like '%"+commitor+"%'");
        	} else if("".equals(commitor)&&!"".equals(name)){
        		strsql.append("delete from fr_txlog where name like '%"+name+"%'");
        	} else {
        		strsql.append("delete from fr_txlog where 1=1");
        	}
        	strsql.append(timesql);
        	this.getFormHM().clear();
        	SearchLogListTrans slt = new SearchLogListTrans();
        	strsql.append(" and "+slt.getPrivSQL(userView, this.frameconn,"clear").substring(6));
    	    this.getFormHM().put("strsql",strsql.toString()); 
        	ArrayList viewlst = new ArrayList();
            dao.update(strsql.toString(),viewlst);
            flag = "ok";
        }
        catch(SQLException sqle)
        {
        	flag="no";
  	      sqle.printStackTrace();
	      //throw GeneralExceptionHandler.Handle(sqle);     
        }finally{
        	this.getFormHM().put("flag", flag);
        }


	}

}
