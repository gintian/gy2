package com.hjsj.hrms.transaction.general.inform.emp.batch;

import com.hjsj.hrms.businessobject.general.info.BatchHandBo;
import com.hjsj.hrms.businessobject.general.inform.BatchBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class AlertSingleIndTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		
		String field_name = (String)reqhm.get("field_name");
		field_name=field_name!=null?field_name:"";
		
		String setname = (String)reqhm.get("setname");
		setname=setname!=null&&setname.trim().length()>0?setname:"";
		reqhm.remove("setname");
		
		String a_code = (String)reqhm.get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		reqhm.remove("a_code");
		
		String viewsearch = (String)reqhm.get("viewsearch");
		viewsearch=viewsearch!=null&&viewsearch.trim().length()>0?viewsearch:"0";
		reqhm.remove("viewsearch");
		
		String strid = (String)reqhm.get("strid");
		strid=strid!=null&&strid.trim().length()>0?strid:"";
		reqhm.remove("strid");
		
		String inforflag = (String)reqhm.get("inforflag");//1:员工管理BS表格录入 2：外部培训     null或"" :其它模块
        inforflag = inforflag != null && inforflag.trim().length() > 0 ? inforflag : "";
        reqhm.remove("inforflag");
        
		String history = "1";
		FieldSet field = DataDictionary.getFieldSetVo(setname);
		if(field.isMainset()){
			history = "0";
		}
		hm.put("history",history);
		
		
		String infor = (String)reqhm.get("infor");
		infor=infor!=null&&infor.trim().length()>0?infor:"";
		reqhm.remove("infor");
		
		BatchHandBo batch = new BatchHandBo(this.userView);

		if("select".equals(field_name))
		    hm.put("itemid","");
		else
		    hm.put("itemid",batch.getDefaultField(setname,field_name));
		hm.put("indlist",batch.indList(setname,infor));
		
		hm.put("refvalue","");
		hm.put("refvaluelist",batch.refList(""));
		
		hm.put("a_code",a_code);
		hm.put("setname",setname);
		hm.put("viewsearch",viewsearch);
		hm.put("infor",infor);
		
		String dbname = (String)reqhm.get("dbname");
		dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
		reqhm.remove("dbname");
		
		hm.put("dbname",dbname);
		BatchBo batchbo = new BatchBo();
		String count = batchbo.countDelItem(this.frameconn,this.userView,setname,dbname,a_code
				,viewsearch,infor,inforflag)+"";
//		String countall = batchbo.countItemall(this.frameconn,this.userView,setname,dbname,infor)+"";
		String countall = batchbo.countItemall(this.frameconn,this.userView,setname,dbname,infor,a_code)+"";

		String[] arr = strid.split("`");
		
		int secount=0;
		if(arr.length>0){
			for(int i=0;i<arr.length;i++){
				if(arr[i]!=null&&arr[i].trim().length()>1)
					secount++;
			}
		}

		hm.put("count",count);
		hm.put("countall",countall);
		hm.put("strid", strid);
		hm.put("secount", secount+"");
	}

}
