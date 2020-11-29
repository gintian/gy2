package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.options.SelfSalaryInfo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class QuerySalaryInfoTrans  extends IBusiness {

	public void execute() throws GeneralException {
		
		String id = (String)this.getFormHM().get("a0100"); //员工ID
		String strPre = (String)this.getFormHM().get("empPre"); //人员库前缀
		/*String fieldsetid=(String)this.getFormHM().get("fieldsetid");
		String title=(String)this.getFormHM().get("title");*/
		String salary=(String)this.getFormHM().get("salary");
		String prv_flag=(String)this.getFormHM().get("prv_flag");
		if("infoself".equals(prv_flag)){
			id=this.userView.getA0100();
			strPre = this.userView.getDbname();
		}else{
			CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
			strPre=checkPrivSafeBo.checkDb(strPre);
			id=checkPrivSafeBo.checkA0100("", strPre, id, "");
		}
		String one_Array[]=salary.split("`");
		String fieldsetid=one_Array[0];
		String title="";
		if(one_Array.length>1)
			title=one_Array[1];
		ArrayList infoList = new ArrayList();
		ArrayList showColumnList = new ArrayList();
		ArrayList columnList = new ArrayList();
		
		//首次登录按当前 年/月 显示明细
		/*Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);*/
		HashMap map =((HashMap)(this.getFormHM().get("requestPamaHM")));
		String year = (String)map.get("year");
//		if(year==null||year.length()<=0)  修改：这是得到系统当前年，有可能当前年没有数据；修改为得到下拉框中最大年份；
//		{
//			Calendar c = Calendar.getInstance();
//			int yearint = c.get(Calendar.YEAR);
//			year=String.valueOf(yearint);
//		}
		if(year==null||year.length()<=0){
			SelfSalaryInfo info = new SelfSalaryInfo(this.getFrameconn(),id,strPre,
					"1",year,"","","","",this.userView,fieldsetid,title,prv_flag);
			info.init_Fieldsetid(this.getFrameconn());
			ArrayList yearlist=info.getYearlist();
			CommonData da = new CommonData();
			int maxn=0;
			for(int i=0;i<yearlist.size();i++){
				da = (CommonData)yearlist.get(i);
				int codeV =Integer.parseInt(da.getDataValue());
				if(maxn<codeV){
					maxn=codeV;
					year=String.valueOf(maxn);
				}
			}
			SelfSalaryInfo infos = new SelfSalaryInfo(this.getFrameconn(),id,strPre,
					"1",year,"","","","",this.userView,fieldsetid,title,prv_flag);
			infos.init_Fieldsetid(this.getFrameconn());
			infoList = infos.execute();
			showColumnList = infos.showColumnList();
			columnList = infos.columnList();
			String query_field=infos.getQuery_field();//过滤项
			String changeflag=infos.getChangeflag();	
			this.getFormHM().put("query_field",query_field);
			this.getFormHM().put("query_name",infos.getQueryFieldName(query_field));
			this.getFormHM().put("changeflag",changeflag);		
			this.getFormHM().put("columnlist",columnList);
			this.getFormHM().put("showcolumnList",showColumnList);
			this.getFormHM().put("infoList",infoList);
			this.getFormHM().put("fieldsetid",fieldsetid);
			this.getFormHM().put("title",title);
			this.getFormHM().put("a0100",id);
			this.getFormHM().put("empPre",strPre);
//			ArrayList yearlist=info.getYearlist();
			
			this.getFormHM().put("yearlist",yearlist);
			this.getFormHM().put("flag","1");
			this.getFormHM().put("yearflag",String.valueOf(year));
			this.getFormHM().put("salary",salary);
		}else{
			SelfSalaryInfo info = new SelfSalaryInfo(this.getFrameconn(),id,strPre,
					"1",year,"","","","",this.userView,fieldsetid,title,prv_flag);
			info.init_Fieldsetid(this.getFrameconn());
			infoList = info.execute();
			showColumnList = info.showColumnList();
			columnList = info.columnList();
			String query_field=info.getQuery_field();//过滤项
			String changeflag=info.getChangeflag();	
			this.getFormHM().put("query_field",query_field);
			this.getFormHM().put("query_name",info.getQueryFieldName(query_field));
			this.getFormHM().put("changeflag",changeflag);		
			this.getFormHM().put("columnlist",columnList);
			this.getFormHM().put("showcolumnList",showColumnList);
			this.getFormHM().put("infoList",infoList);
			this.getFormHM().put("fieldsetid",fieldsetid);
			this.getFormHM().put("title",title);
			this.getFormHM().put("a0100",id);
			this.getFormHM().put("empPre",strPre);
			ArrayList yearlist=info.getYearlist();
			
			this.getFormHM().put("yearlist",yearlist);
			this.getFormHM().put("flag","1");
			this.getFormHM().put("yearflag",String.valueOf(year));
			this.getFormHM().put("salary",salary);
		}
		
	}

}
