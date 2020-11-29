package com.hjsj.hrms.transaction.general.inform.emp.batch;

import com.hjsj.hrms.businessobject.general.inform.BatchBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
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
public class AddSingleIndTrans extends IBusiness {

	public void execute() throws GeneralException {
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
		
		String dbname = (String)reqhm.get("dbname");
		dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
		reqhm.remove("dbname");
		
		String infor = (String)reqhm.get("infor");
		infor=infor!=null&&infor.trim().length()>0?infor:"";
		reqhm.remove("infor");
		
		String inforflag = (String)reqhm.get("inforflag");
		inforflag = inforflag != null && inforflag.trim().length() > 0 ? inforflag : "";
		reqhm.remove("inforflag");
		
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		ArrayList fieldlist = this.userView.getPrivFieldList(setname,Constant.USED_FIELD_SET);
		String pri = this.userView.analyseTablePriv(setname);
		ArrayList list = new ArrayList();
		if("2".equals(pri)){
			for(int i=0;i<fieldlist.size();i++){
				FieldItem item = (FieldItem)fieldlist.get(i);
				if("2".equals(this.userView.analyseFieldPriv(item.getItemid()))&& "2".equals(pri)){
					if(!item.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
							||!"0".equals(fieldset.getChangeflag())){
						list.add(item);
					}
				}
			}
		}
		BatchBo batchbo = new BatchBo();
		String count = batchbo.countItem(this.frameconn,this.userView,setname,dbname,a_code
				,viewsearch,infor, inforflag)+"";
		hm.put("fieldlist",list);
		hm.put("a_code",a_code);
		hm.put("setname",setname);
		hm.put("dbname",dbname);
		hm.put("count",count);
		hm.put("infor",infor);
		hm.put("viewsearch",viewsearch);
		hm.put("inforflag", inforflag);
	}

}
