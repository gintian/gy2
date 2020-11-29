/*
 * Created on 2005-7-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SearchLogTrans</p>
 * <p>Description:按条件查询日志，fr_txlog</p>
 * <p>Company:hjsj</p>
 * <p>create time:July 25, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchLogTrans extends IBusiness {

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
	
        try
        {
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
        		strsql.append("select * from fr_txlog");
        		haswhere =false;
        	} else if(!"".equals(commitor)&&!"".equals(name)){
        		strsql.append("select * from fr_txlog where commitor like '%"+commitor+"%' and name like '%"+name+"%'");
        	} else if(!"".equals(commitor)&&"".equals(name)){
        		strsql.append("select * from fr_txlog where commitor like '%"+commitor+"%'");
        	} else if("".equals(commitor)&&!"".equals(name)){
        		strsql.append("select * from fr_txlog where name like '%"+name+"%'");
        	} else {
        		strsql.append("select * from fr_txlog where 1=1");
        	}
        	/*if((commitor == null || commitor.equals("")) && (name == null || name.equals("")) && (beginexectime ==null || beginexectime.equals("")) && (endexectime == null || endexectime.equals(""))){
        		strsql.append("select * from fr_txlog");
        		haswhere =false;
        	}else if((commitor == null || commitor.equals("")) && (name == null || name.equals("")) && (beginexectime ==null || beginexectime.equals(""))){
        	   strsql.append("select * from fr_txlog where endexectime like '%"+endexectime+"%'");
        	}
        	else if((commitor == null || commitor.equals("")) && (name == null || name.equals("")) && (endexectime == null || endexectime.equals(""))){
         	   strsql.append("select * from fr_txlog where beginexectime like '%"+beginexectime+"%'");
        	}
        	else if((commitor == null || commitor.equals("")) && (beginexectime ==null || beginexectime.equals("")) && (endexectime == null || endexectime.equals(""))){
         	   strsql.append("select * from fr_txlog where name like '%"+name+"%'");
        	}
        	else if((name == null || name.equals("")) && (beginexectime ==null || beginexectime.equals("")) && (endexectime == null || endexectime.equals(""))){
         	   strsql.append("select * from fr_txlog where commitor like '%"+commitor+"%'");
         	}else if((commitor == null || commitor.equals("")) && (beginexectime ==null || beginexectime.equals(""))){
          	   strsql.append("select * from fr_txlog where name like '%"+name+"%' and endexectime like '%"+endexectime+"%'");
         	}else if((commitor == null || commitor.equals("")) && (endexectime == null || endexectime.equals(""))){
           	   strsql.append("select * from fr_txlog where name like '%"+name+"%' and beginexectime like '%"+beginexectime+"%'");
         	}else if((name == null || name.equals("")) && (beginexectime ==null || beginexectime.equals(""))){
           	   strsql.append("select * from fr_txlog where commitor like '%"+commitor+"%' and endexectime like '%"+endexectime+"%'");
         	}else if((name == null || name.equals("")) && (endexectime == null || endexectime.equals(""))){
           	   strsql.append("select * from fr_txlog where commitor like '%"+commitor+"%' and beginexectime like '%"+beginexectime+"%'");
         	}else if((beginexectime ==null || beginexectime.equals("")) && (endexectime == null || endexectime.equals(""))){
           	   strsql.append("select * from fr_txlog where commitor like '%"+commitor+"%' and name like '%"+name+"%'");
         	}else if((commitor == null || commitor.equals("")) && (name == null || name.equals(""))){
           	   strsql.append("select * from fr_txlog where beginexectime like '%"+beginexectime+"%' and endexectime like '%"+endexectime+"%'");
         	}else if((commitor == null || commitor.equals(""))){
         		strsql.append("select * from fr_txlog where name like '%"+name+"%' and beginexectime like '%"+beginexectime+"%' and endexectime like '%"+endexectime+"%'");
         	}else if((name == null || name.equals(""))){
         		strsql.append("select * from fr_txlog where commitor like '%"+commitor+"%' and beginexectime like '%"+beginexectime+"%' and endexectime like '%"+endexectime+"%'");
         	}else if((beginexectime ==null || beginexectime.equals(""))){
         		strsql.append("select * from fr_txlog where commitor like '%"+commitor+"%' and name like '%"+name+"%' and endexectime like '%"+endexectime+"%'");
         	}else if((endexectime == null || endexectime.equals(""))){
         		strsql.append("select * from fr_txlog where commitor like '%"+commitor+"%' and name like '%"+name+"%' and beginexectime like '%"+beginexectime+"%'");
         	}else{
        	   strsql.append("select * from fr_txlog where commitor like '%"+commitor+"%' and name like '%"+name+"%' and beginexectime like '%"+beginexectime+"%' and endexectime like '%"+endexectime+"%'");
         	}*/
        	strsql.append(timesql);
        	this.getFormHM().clear();
        	SearchLogListTrans slt = new SearchLogListTrans();
        	strsql.append(haswhere?(" and "+slt.getPrivSQL(userView, this.frameconn,"query").substring(6)):slt.getPrivSQL(userView, this.frameconn,"query"));
    	    this.getFormHM().put("strsql",strsql.toString());
    	    this.getFormHM().put("beginexectimeend", bend);
    	    this.getFormHM().put("endexectimeend", eend);
        }
        catch(Exception sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
	}

}
