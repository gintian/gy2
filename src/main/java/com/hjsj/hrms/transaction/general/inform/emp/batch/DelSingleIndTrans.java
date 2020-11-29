package com.hjsj.hrms.transaction.general.inform.emp.batch;

import com.hjsj.hrms.businessobject.general.inform.BatchBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class DelSingleIndTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String setname = (String)reqhm.get("setname");
		setname=setname!=null&&setname.trim().length()>0?setname:"";
		reqhm.remove("setname");
		
		String a_code = (String)reqhm.get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		reqhm.remove("a_code");
		
		String viewsearch = (String)reqhm.get("viewsearch");
		viewsearch=viewsearch!=null&&viewsearch.trim().length()>0?viewsearch:"0";
		reqhm.remove("viewsearch");
		//是否是外部培训调用的参数：2：是外部培训调用      其他值为人员管理或1其他模块调用
		String inforflag = (String)reqhm.get("inforflag");
        inforflag = inforflag != null && inforflag.trim().length() > 0 ? inforflag : "";
        reqhm.remove("inforflag");
		
		String infor = (String)reqhm.get("infor");
		infor=infor!=null&&infor.trim().length()>0?infor:"";
		reqhm.remove("infor");
		
		hm.put("a_code",a_code);
		hm.put("setname",setname);
		hm.put("viewsearch",viewsearch);
		
		String dbname = (String)reqhm.get("dbname");
		dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
		reqhm.remove("dbname");

		hm.put("dbname",dbname);
		hm.put("infor",infor);
		String prive = this.userView.analyseTablePriv(setname);
		
		BatchBo batchbo = new BatchBo();
		String count = batchbo.countDelItem(this.frameconn,this.userView,setname,dbname,a_code
				,viewsearch,infor,inforflag)+"";
//		String countall = batchbo.countItemall(this.frameconn,this.userView,setname,dbname,infor)+"";
		String countall = batchbo.countItemall(this.frameconn,this.userView,setname,dbname,infor,a_code)+"";
		hm.put("count",count);
		hm.put("countall",countall);
		hm.put("prive",prive);
		if(checkMonth(setname)){
			hm.put("flag","1");
		}else{
			hm.put("flag","0");
		}
	}
	private boolean checkMonth(String setname){
		ArrayList list = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.ALL_FIELD_SET);
		boolean check=false;
		for(int i=0;i<list.size();i++){
			FieldSet fieldset = (FieldSet)list.get(i);
			if(fieldset.getFieldsetid().equalsIgnoreCase(setname)){
				if(fieldset.getChangeflag()!=null){
					if("2".equals(fieldset.getChangeflag())|| "1".equals(fieldset.getChangeflag())){
						check=true;
						break;
					}
				}	
			}
		}
		return check;
	}

}
