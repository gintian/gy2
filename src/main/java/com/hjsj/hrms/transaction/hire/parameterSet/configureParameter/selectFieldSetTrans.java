package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.ResultSet;
import java.util.ArrayList;

public class selectFieldSetTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList selectedList = new ArrayList();
        ParameterXMLBo xbo= new ParameterXMLBo(this.getFrameconn());
		ParameterSetBo sbo= new ParameterSetBo(this.getFrameconn());
		try
		{
			ArrayList fieldSetList=new ArrayList();
			String flag=(String)this.getFormHM().get("flag");;
			String size="1";
			if("1".equals(flag))
			{
					StringBuffer sql=new StringBuffer("select fieldSetId,customdesc from fieldSet where useFlag=1 ");
					String fieldSetIDs="";
					this.frowset=dao.search("select * from constant where constant='ZP_SUBSET_LIST'");
					if(this.frowset.next())
					{
						fieldSetIDs=Sql_switcher.readMemo(this.frowset,"str_value");
					}else
					{
						size="0";
						//throw GeneralExceptionHandler.Handle(new Exception("没有设置招聘管理/参数配置/应聘人才库信息"));
					}
					if(!"".equals(fieldSetIDs))
					{
						if(fieldSetIDs.indexOf(",")==-1)
						{
							if(fieldSetIDs.indexOf("[")==-1)
						    	sql.append(" and fieldSetId='"+fieldSetIDs+"'");
							else
							{
								sql.append(" and fieldSetId='"+fieldSetIDs.substring(0,fieldSetIDs.indexOf("["))+"'");
							}
						}
						else
						{
							String[] fielsSetID=fieldSetIDs.split(",");
							sql.append(" and fieldSetId in ( ");
							StringBuffer whl=new StringBuffer("");
							for(int i=0;i<fielsSetID.length;i++)
							{
								if(fielsSetID[i].indexOf("[")==-1)
						    		whl.append(",'"+fielsSetID[i]+"'");
								else
									whl.append(",'"+fielsSetID[i].substring(0,fielsSetID[i].indexOf("["))+"'");
							}
							sql.append(whl.substring(1)+" ) ");
						}
						
						this.frowset=dao.search(sql.toString());
						while(this.frowset.next())
						{
							 CommonData dataobj = new CommonData(this.frowset.getString("fieldSetId"), this.frowset.getString("customdesc"));
							 fieldSetList.add(dataobj);
						}
						String ssql="select customdesc from t_hr_busiTable where fieldsetid='Z03'";
						ResultSet res=dao.search(ssql);
						while(res.next()){
							 CommonData obj1 =  new CommonData("Z03",res.getString("customdesc"));
							 fieldSetList.add(obj1);
						}
					}
					String ids = xbo.getParaValues("out_fields","Fields");
					if(ids != null && ids.trim().length()>0){
					    selectedList=sbo.getParaNameListByParaValue(ids,0);
					}
			}	
			else
			{
				String ssql="select customdesc from t_hr_busiTable where fieldsetid='Z03'";
				ResultSet res=dao.search(ssql);
				while(res.next()){
					 CommonData obj1 =  new CommonData("Z03",res.getString("customdesc"));
					 fieldSetList.add(obj1);
				}
				 if("2".equals(flag)){
				    String ids = xbo.getParaValues("pos_query");
				    if(ids != null && ids.trim().length()>0){
				         selectedList = sbo.getParaNameListByParaValue(ids,1);
				    }
				 }
				 if("3".equals(flag)){
					 String ids = xbo.getParaValues("view_pos");
					 if(ids != null && ids.trim().length()>0){
					      selectedList = sbo.getParaNameListByParaValue(ids,1);
					 }
				 }
				 if("4".equals(flag))
				 {
					 String ids = xbo.getParaValues("pos_listfield");
					    if(ids != null && ids.trim().length()>0){
					         selectedList = sbo.getParaNameListByParaValue(ids,1);
					    }
				 }
				 if("5".equals(flag)){// dml 2011-6-22 10:54:21
					    String ids = xbo.getParaValues("pos_com_query");
					    if(ids != null && ids.trim().length()>0){
					         selectedList = sbo.getParaNameListByParaValue(ids,1);
					    }
				}
					 
			}
			this.getFormHM().put("setlist",fieldSetList);
			this.getFormHM().put("selectedList",selectedList);
			this.getFormHM().put("size",size);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	

}
