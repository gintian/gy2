package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.options.SelfSalaryInfo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SelfSalaryInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String id = (String)hm.get("a0100"); //员工ID
		String strPre = (String)hm.get("pre"); //人员库前缀		
		ArrayList infoList = new ArrayList();
		ArrayList showColumnList = new ArrayList();
		ArrayList columnList = new ArrayList();
		String prv_flag=(String)this.getFormHM().get("prv_flag");
		if("infoself".equals(prv_flag)){
			id=this.userView.getA0100();
			strPre=this.userView.getDbname();
		}else{
			CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
			strPre=checkPrivSafeBo.checkDb(strPre);
			id=checkPrivSafeBo.checkA0100("", strPre,id, "");
		}
		//System.out.println("flag="+flag);
		//首次登录按当前 年/月 显示明细
		Calendar c = Calendar.getInstance();
		String yearflag=(String)this.getFormHM().get("yearflag");
		int year = c.get(Calendar.YEAR);
		if(yearflag!=null&&!"".equals(yearflag))
		   year = Integer.parseInt(yearflag);
		try
		{
			SelfSalaryInfo info = new SelfSalaryInfo(this.getFrameconn(),id,strPre,
					"1",String.valueOf(year),"","","","",this.userView,prv_flag);
			ArrayList  fieldSetList=info.getFieldSetList();
			/*String fieldsetid="";
			if(fieldSetList!=null&&fieldSetList.size()>0)
			{
				CommonData cvo=(CommonData)fieldSetList.get(0);
				fieldsetid=cvo.getDataValue();
			}
			info.setFieldsetid(fieldsetid);*/
			infoList = info.execute();
			showColumnList = info.showColumnList();
			columnList = info.columnList();
			String query_field=info.getQuery_field();//过滤项
			String changeflag=info.getChangeflag();
			String fieldsetid=info.getFieldsetid();
			String title=info.getTitle();
			ArrayList yearlist=info.getYearlist();
			String year_restrict=info.getYear_restrict();
			this.getFormHM().put("yearlist",yearlist);
			this.getFormHM().put("year_restrict", year_restrict);
			this.getFormHM().put("query_field",query_field);
			this.getFormHM().put("query_name",info.getQueryFieldName(query_field));
			this.getFormHM().put("changeflag",changeflag);
			this.getFormHM().put("fieldSetList",fieldSetList);
			this.getFormHM().put("fieldsetid",fieldsetid);
			this.getFormHM().put("title",title);
			
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
		/*
		System.out.println("infoList size =" + infoList.size());
		System.out.println("showColumnList size = " + showColumnList.size());
		System.out.println("columnList size=" + columnList.size());
		*/
		
		this.getFormHM().put("columnlist",columnList);
		this.getFormHM().put("showcolumnList",showColumnList);
		this.getFormHM().put("infoList",infoList);
		
		this.getFormHM().put("a0100",id);
		this.getFormHM().put("empPre",strPre);
		this.getFormHM().put("prv_flag", prv_flag);
		this.getFormHM().put("flag","1");
		this.getFormHM().put("yearflag",String.valueOf(year));
	}
	
}
