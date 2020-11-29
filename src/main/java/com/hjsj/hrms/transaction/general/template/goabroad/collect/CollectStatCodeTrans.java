package com.hjsj.hrms.transaction.general.template.goabroad.collect;

import com.hjsj.hrms.businessobject.general.template.collect.CollectStat;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class CollectStatCodeTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		
			ArrayList list=new ArrayList();
			String fileset=(String)this.getFormHM().get("fileset");
			String subset=(String)this.getFormHM().get("subset");
			ArrayList filelist = DataDictionary.getFieldList(subset,Constant.USED_FIELD_SET);
			filelist = getList(filelist);
			String filetype="";
			String codesetid="0";
			for(int i=0;i<filelist.size();i++)
			{
				
				FieldItem fielditem=(FieldItem)filelist.get(i);				
				if(fileset.toLowerCase().equals(fielditem.getItemid().toLowerCase()))
				{
					filetype=fielditem.getItemtype();
					codesetid=fielditem.getCodesetid();
				}
			}
			CollectStat collectStat= new CollectStat(this.getFrameconn());
		    ArrayList codelist=collectStat.getList(codesetid);
		    if(codelist!=null&&codelist.size()>0)
		    {
		    	String selecthtml=getSelectHtml(codelist);
		    	this.getFormHM().put("selecthtml",selecthtml);
		    	/*CommonData vo=(CommonData)codelist.get(0);
		    	String childset=vo.getDataValue();
		    	this.getFormHM().put("childset",childset);*/
		    	this.getFormHM().put("flag","3");
		    }else if("D".equals(filetype.toUpperCase()))
		    {
		    	this.getFormHM().put("flag","2");
		    	this.getFormHM().put("start_date","");
		    	this.getFormHM().put("end_date","");
		    }else if("N".equals(filetype.toUpperCase()))
		    {
		    	this.getFormHM().put("flag","1");
		    	this.getFormHM().put("childset","");
		    } else {
		    	this.getFormHM().put("flag","0");
		    	this.getFormHM().put("childset","");
		    }
		    this.getFormHM().put("fileset",fileset);
	}
	
	public static String getSelectHtml(ArrayList codelist)
    {
    	StringBuffer selecthtml= new StringBuffer();
    	selecthtml.append("<select name='childset' size='1'>");
    	selecthtml.append("<option value=all>");    		
		selecthtml.append("全部");
		selecthtml.append("</option>"); 
    	for(int i=0;i<codelist.size();i++)
    	{
    		CommonData vo=(CommonData)codelist.get(i);
    		selecthtml.append("<option value="+vo.getDataValue()+">");    		
    		selecthtml.append(vo.getDataName());
    		selecthtml.append("</option>");    	
    	}
    	selecthtml.append("</select> ");
    	return selecthtml.toString();
    }

	/**
	 * 添加单位和部门字段
	 * @param fieldList
	 * @return
	 */
	private ArrayList getList (ArrayList fieldList) {
		FieldItem fielditem=new FieldItem();
		fielditem.setItemid("b0110");
		fielditem.setItemtype("A");
		fielditem.setItemdesc("单位名称");
		fielditem.setCodesetid("UN");
		fielditem.setVisible(true);
		fieldList.add(fielditem);
		 
		FieldItem fielditem2 = new FieldItem();
		fielditem2.setItemid("e0122");
		fielditem2.setItemtype("A");
		fielditem2.setItemdesc("部门名称");
		fielditem2.setCodesetid("UM");
		fielditem2.setVisible(true);
		fieldList.add(fielditem2);
		
		return fieldList;
	}
}
