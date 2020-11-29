package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.businessobject.sys.options.SearchTableCardConstantSet;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SelectChangeFieldSetTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String fieldsetidss = (String) this.getFormHM().get("fieldsetid");
		String one_Array[]=fieldsetidss.split("`");
		String fieldsetid=one_Array[0];
		ArrayList fielditemlist = new ArrayList();//全部指标
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql="select * from  fielditem where useflag=1 and fieldsetid='"+fieldsetid+"'";
		//sql=sql+"  and itemid <>'"+fieldsetid+"Z1'";
		//sql=sql="  and itemid <>'"+fieldsetid+"Z0'  and itemid <>'"+fieldsetid+"Z1'";
		//String sql="select * from  fielditem where useflag=1 and fieldsetid='"+fieldsetid+"' ";
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				String itemid = this.frowset.getString("itemid");
				String itemdesc = this.frowset.getString("itemdesc");
				CommonData dataobj = new CommonData(itemid, itemdesc);
				fielditemlist.add(dataobj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("fielditemlist",fielditemlist);
		//getSelectList(fieldsetid);
		try
		{
			SearchTableCardConstantSet constantSet=new SearchTableCardConstantSet(this.userView,this.getFrameconn());
			ArrayList selectedList = new ArrayList();
			String query_field="";
			String salary_text="";
			String sumitemValue="";
			String title="";
			if(one_Array.length>1)
				title=one_Array[1];
			if(constantSet.check()){
				Sys_Oth_Parameter sop = new Sys_Oth_Parameter(this.getFrameconn());
				query_field=sop.getValue(Sys_Oth_Parameter.MYSALARYS_SALARY,"setname",fieldsetid,"title",title,"query_field");
				salary_text=sop.getValue(Sys_Oth_Parameter.MYSALARYS_SALARY,"setname",fieldsetid,"title",title,"");
				sumitemValue=sop.getChildText(Sys_Oth_Parameter.MYSALARYS_SALARY,"setname",fieldsetid,"title",title);
				String [] salary = salary_text.split(",");
				String [] sumItem = sumitemValue.split(",");
				for(int i=0; i<salary.length; i++)
				{
					String itemid = salary[i].trim();
					String itemdesc = constantSet.getItemDesc(itemid).trim();
					if("".equalsIgnoreCase(itemid)){
						continue;
					}
					if(itemdesc.length()==0){
						continue;
					}
					if(constantSet.checkFieldItem(itemid,sumItem)){
						itemid +="$";
						itemdesc +="(∑)";
					}
					CommonData dataobj = new CommonData(itemid, itemdesc);
					selectedList.add(dataobj);
				}
			}
			if(query_field==null||query_field.length()<=0)
				query_field="";			
			this.getFormHM().put("selectedList",selectedList);
			this.getFormHM().put("query_f",query_field);
			String changeflag=constantSet.getChangeFlag(fieldsetid);
			ArrayList dateitemlist=new ArrayList();
			if(changeflag!=null&&!"0".equals(changeflag))
			{
				dateitemlist=constantSet.getDateSelectSetList(fieldsetid);				
			}
			this.getFormHM().put("dateitemlist",dateitemlist);
			this.getFormHM().put("changeflag",changeflag);
			this.getFormHM().put("title",title);
		}catch(Exception e)
		{
             e.printStackTrace();			
		}
		
	}	  
}
