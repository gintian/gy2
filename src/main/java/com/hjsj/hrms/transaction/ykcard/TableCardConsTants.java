package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.sys.options.SearchTableCardConstantSet;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

public class TableCardConsTants extends IBusiness {

	public void execute() throws GeneralException 
	{
		SearchTableCardConstantSet tableCardConstantSet=new SearchTableCardConstantSet(this.userView,this.getFrameconn());
	    String mysalarys="";
		if(tableCardConstantSet.check()){
			Sys_Oth_Parameter sop = new Sys_Oth_Parameter(this.getFrameconn());
			mysalarys = sop.getValueS(Sys_Oth_Parameter.MYSALARYS_SALARY,"setname","title");		
		}
		/*if(mysalarys==null||mysalarys.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","没有以保存的指标项！","",""));*/
		ArrayList fieldSetTempList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		ArrayList fieldSetList = new ArrayList();//主集和按月变化子集(人员)
		int one=0;
		String frist_setid="";
		String frist_title="";
		/*for(int i=0; i< fieldSetTempList.size(); i++){
			FieldSet fieldSet = (FieldSet) fieldSetTempList.get(i);
			String setid = fieldSet.getFieldsetid();
			String setdesc = fieldSet.getFieldsetdesc();
			String setChangeFlag = fieldSet.getChangeflag();
			if(setChangeFlag == null){			
			}else{
				if(!setid.equalsIgnoreCase("A01"))
				{
					if(mysalarys.toLowerCase().indexOf(setid.toLowerCase())!=-1)
					{
						CommonData dataobj = new CommonData(setid, setdesc);
						fieldSetList.add(dataobj);
						if(one==0)
							frist_setid=setid;	
						one++;
					}
				}
			}
		}*/
        String mysalaryArray[]=mysalarys.split(",");
		if(mysalaryArray!=null&&mysalaryArray.length>0)
		{
			for(int i=0;i<mysalaryArray.length;i++)
			{
				String one_salary=mysalaryArray[i];
				String[] one_Array=one_salary.split("`");
				String setname=one_Array[0];
				if(setname==null||setname.length()<=0)
					continue;
				for(int r=0; r< fieldSetTempList.size(); r++)
				{
					FieldSet fieldSet = (FieldSet) fieldSetTempList.get(r);
					String setid = fieldSet.getFieldsetid();
					String setdesc = fieldSet.getCustomdesc();
					if(setid.indexOf(setname)!=-1)
					{
						CommonData dataobj = new CommonData(one_salary,setdesc);
						fieldSetList.add(dataobj);
						if(i==0)
						{
							frist_setid=setid;	
							if(one_Array.length>1)
								frist_title=one_Array[1];
						}
						break;
					}
					
				}
				
			}
		}
		ArrayList fielditemlist=getCodeList(frist_setid);
		getSelectList(frist_setid,frist_title); 
		this.getFormHM().put("setid",frist_setid+"`"+frist_title);
		this.getFormHM().put("title",frist_title);
		this.getFormHM().put("fieldSetList" , fieldSetList);
		this.getFormHM().put("fielditemlist" , fielditemlist);
		String changeflag=tableCardConstantSet.getChangeFlag(frist_setid);
		ArrayList dateitemlist=new ArrayList();
		if(changeflag!=null&&!"0".equals(changeflag))
		{
			dateitemlist=tableCardConstantSet.getDateSelectSetList(frist_setid);			
		}
		this.getFormHM().put("changeflag",changeflag);
		this.getFormHM().put("dateitemlist",dateitemlist);
	}
	/**
	 * 得到指标的指标集
	 * @param itemID
	 * @return
	 */
    public ArrayList getCodeList(String itemID)
    {
    	ArrayList fielditemlist = new ArrayList();//全部指标
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		//String sql="select * from  fielditem where useflag=1 and fieldsetid='"+itemID+"' ";
		String sql="select * from  fielditem where useflag=1 and fieldsetid='"+itemID+"'";
		//sql=sql+"  and itemid <>'"+itemID+"Z1'";
		//sql=sql+" and itemid <>'"+itemID+"Z0'  and itemid <>'"+itemID+"Z1'";
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
		return fielditemlist;
    }
    /**
     * 得到属性
     * @param itemID
     */
    public void  getSelectList(String itemID,String title) 
    {
//    	选中的默认值
    	SearchTableCardConstantSet constantSet=new SearchTableCardConstantSet(this.userView,this.getFrameconn());
		ArrayList selectedList = new ArrayList();
		String query_field="";
		String salary_text="";
		String sumitemValue="";
		if(constantSet.check()){
			Sys_Oth_Parameter sop = new Sys_Oth_Parameter(this.getFrameconn());
			query_field=sop.getValue(Sys_Oth_Parameter.MYSALARYS_SALARY,"setname",itemID,"title",title,"query_field");
			salary_text=sop.getValue(Sys_Oth_Parameter.MYSALARYS_SALARY,"setname",itemID,"title",title,"");
			sumitemValue=sop.getChildText(Sys_Oth_Parameter.MYSALARYS_SALARY,"setname",itemID,"title",title);
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
		this.getFormHM().put("selectedList" , selectedList);
		this.getFormHM().put("query_field",query_field);		
    }    
	
}
