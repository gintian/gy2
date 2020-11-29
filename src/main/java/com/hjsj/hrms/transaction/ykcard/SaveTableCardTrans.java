package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 保存人员库子集
 * <p>Title:SaveTableCardTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 14, 2007 10:04:07 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SaveTableCardTrans  extends IBusiness {

	public void execute() throws GeneralException
	{
		HashMap hm=(HashMap)this.getFormHM();	
		//String[] fields = (String[]) this.getFormHM().get("right_fields");	
		/*if (fields == null || fields.length == 0) {
			throw new GeneralException(ResourceFactory.getProperty("errors.query.notexistfield"));
		}*/
		ArrayList salary_names=(ArrayList)this.getFormHM().get("right_fields");	
		/*for (int i = 0; i < fields.length; i++) {
			String fieldname = fields[i];
			salary_names.add(fieldname);
		}*/
		//ArrayList salary_names=(ArrayList)hm.get("salary_names");
		String old_mysalarys=(String)this.getFormHM().get("old_mysalarys");
		String types="ok";
		try
		{
			if(salary_names==null||salary_names.size()<=0)
			{
				//删除所有的
				dellAllMysalarys(old_mysalarys);
			}
			else
			{
				saveMysalarys(salary_names,old_mysalarys);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			types="false";
		}
		this.getFormHM().put("types",types);
	}
	public void dellAllMysalarys(String old_mysalarys)throws GeneralException
	{
		String[] strS=old_mysalarys.split(",");
		ArrayList del_list=new ArrayList();
		if(strS!=null||strS.length>=0)
		{
			/*for(int i=0;i<strS.length;i++)
			{
				
					del_list.add(strS[i]);
			}*/
			try
			{
			  Sys_Oth_Parameter sop = new Sys_Oth_Parameter(this.getFrameconn());
			  //sop.removeContent(Sys_Oth_Parameter.MYSALARYS,Sys_Oth_Parameter.SALARY,"setname",del_list);
			  sop.removeAllContent(Sys_Oth_Parameter.MYSALARYS);
			  sop.saveParameter();
			}catch(Exception ex)
			{
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			} 
		}
		
	}
	/**
	 * 删除以前有现在没有选定的，添加原来没有现在选定的，
	 * @param salary_names
	 * @param old_mysalarys
	 */
    private void saveMysalarys(ArrayList salary_names,String old_mysalarys)throws GeneralException
    {
    	ArrayList save_list=new ArrayList();
		ArrayList add_list=new ArrayList();
		ArrayList del_list=new ArrayList();
		StringBuffer strN=new StringBuffer();
		String n1="";
		String n2="";		
		for(int i=0;i<salary_names.size();i++)
		{
			if(old_mysalarys.indexOf(salary_names.get(i).toString())!=-1)
			{
				save_list.add(salary_names.get(i).toString());
				int addS=old_mysalarys.indexOf(salary_names.get(i).toString());
				int adde=old_mysalarys.indexOf(",",addS);
				n1=old_mysalarys.substring(0,addS);
				if(adde<old_mysalarys.length()&&adde!=-1)
				{
					n2=old_mysalarys.substring(adde+1);
					old_mysalarys=n1+n2;
				}else
				{
					old_mysalarys=n1;
				}	
			}	
			else
				add_list.add(salary_names.get(i).toString());
			strN.append(salary_names.get(i).toString()+",");
		}
		if(strN==null||strN.length()<=0)
			strN.append("");
		String[] strS=old_mysalarys.split(",");
		if(strS!=null||strS.length>=0)
		{
			for(int i=0;i<strS.length;i++)
			{
				if(strN.toString().indexOf(strS[i])==-1)
					del_list.add(strS[i]);
			}
		}
		Sys_Oth_Parameter sop = new Sys_Oth_Parameter(this.getFrameconn());
		try
		{
			sop.setValueS(Sys_Oth_Parameter.MYSALARYS,Sys_Oth_Parameter.SALARY,"setname",add_list);
			sop.removeContent(Sys_Oth_Parameter.MYSALARYS,Sys_Oth_Parameter.SALARY,"setname",del_list);
			sop.setOrderChildText(Sys_Oth_Parameter.MYSALARYS_SALARY,"setname","title",salary_names);
			sop.saveParameter();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
    }
   
}
