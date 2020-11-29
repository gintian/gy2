package com.hjsj.hrms.transaction.general.template.goabroad.collect;

import com.hjsj.hrms.businessobject.general.template.collect.CollectStat;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SelectStatCodeTrans  extends IBusiness {


	public void execute() throws GeneralException 
	{
		String subset=(String)this.getFormHM().get("subset");
//		String nbase=(String)this.getFormHM().get("nbase");
//		String code = (String) this.getFormHM().get("code");
//		String kind = (String) this.getFormHM().get("kind");  
		String fileset=(String)this.getFormHM().get("fileset");
		String flag=(String)this.getFormHM().get("flag");
		String select_stat=(String)this.getFormHM().get("select_stat");
//		ArrayList columnlist=(ArrayList)this.getFormHM().get("columnlist");
//		String columns=(String)this.getFormHM().get("columns");
//		String where =(String)this.getFormHM().get("where");
		String isWhere="";
		if(flag==null||flag.length()<=0)
		{
			flag="1";
		}
		if("3".equals(flag))
		{
			String childset=(String)this.getFormHM().get("childset");
			isWhere=codeType(fileset,childset);
			CollectStat collectStat= new CollectStat(this.getFrameconn());
			ArrayList filelist = DataDictionary.getFieldList(subset,Constant.USED_FIELD_SET);
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
			if ("e0122".equals(fileset.toLowerCase())) {
				codesetid = "UM";
			} else if ("b0110".equals(fileset.toLowerCase())) {
				codesetid = "UN";
			}
			ArrayList codelist=collectStat.getList(codesetid);
			String selecthtml=collectStat.getSelectHtml(codelist,childset);
			this.getFormHM().put("selecthtml",selecthtml);
			this.getFormHM().put("childset",childset);
	           /***代码型**/		
		}else if("2".equals(flag))
		{
			/***日期型**/
			String start_date=(String)this.getFormHM().get("start_date");
			String end_date=(String)this.getFormHM().get("end_date");
			isWhere=dataType(fileset,start_date,end_date);
			this.getFormHM().put("start_date",start_date);
			this.getFormHM().put("end_date",end_date);
		}else if ("1".equals(flag))
		{
			/****数字类型****/
			String childset=(String)this.getFormHM().get("childset");
			if((childset==null||childset.length()<1)&&!"a4070".equals(fileset))
				isWhere = " and "+fileset+" is null";
			else
				isWhere=codeType(fileset,childset);
			this.getFormHM().put("childset",childset);
		} else {
			/****字符型****/
			String childset=(String)this.getFormHM().get("childset");
			isWhere=fuzzyQuerry(fileset,childset);
			this.getFormHM().put("childset",childset);
		}		
		this.getFormHM().put("isWhere",isWhere);
		this.getFormHM().put("select_stat",select_stat);
		
	}
	/**
	 * 代码条件
	 * @param fileset
	 * @param childset
	 * @return
	 */
    public String codeType(String fileset,String childset)
    {
    	StringBuffer where_str =new StringBuffer();
    	//判断停留天数是否为空(或0)
    	if("a4070".equals(fileset) && ("0".equals(childset) || childset.length()<=0)){
    		where_str.append(" and ("+fileset+"=0 or "+fileset+" is null)");
    	}
    	//判断下拉列表是否是全部(如：出国国家、出国目的)
    	else if("all".equals(childset)){
    		//选择 全选 all 不用加条件
    	}else{
    		where_str.append(" and "+fileset+"='"+childset+"'");
    	}
    	return where_str.toString();
    }
    
    /**
	 * 模糊查询
	 * @param fileset
	 * @param childset
	 * @return
	 */
    public String fuzzyQuerry(String fileset,String childset)
    {
    	StringBuffer where_str =new StringBuffer();
    	//判断查询条件是否为空
    	if(childset==null || childset.length()<=0)
    		where_str.append(" and "+fileset+" is null");
    	else
    		where_str.append(" and "+fileset+" like '%"+childset+"%'");
    	return where_str.toString();
    }
    /**
     * 普通条件
     * @param fileset
     * @param childset
     * @return
     * @throws GeneralException
     */
    public String baseType(String fileset,String childset)throws GeneralException 
    {
    	if(childset==null||childset.length()<=0)
    		childset="";    	
    	StringBuffer where_str =new StringBuffer();
    	if(childset.indexOf("and")!=-1)
    	{
    		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("goabroad.collect.select.lawless_str"),"",""));
    	}else if(childset.indexOf("like")!=-1)
    	{
    		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("goabroad.collect.select.lawless_str"),"",""));
    	}else if(childset.indexOf("=")!=-1)
    	{
    		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("goabroad.collect.select.lawless_str"),"",""));
    	}else if(childset.indexOf("or")!=-1)
    	{
    		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("goabroad.collect.select.lawless_str"),"",""));
    	}else
    	{
    		where_str.append(" and "+fileset+"='"+childset+"'");
    	}    	
    	return where_str.toString();
    }
    /**
     * 日期条件
     */
    public String dataType(String fileset,String start_date,String end_date)throws GeneralException 
    {
    	StringBuffer where_str =new StringBuffer();
    	if(start_date!=null||start_date.length()>0)
    	{
    		where_str.append(" and "+fileset+">="+Sql_switcher.dateValue(start_date));
    	}
    	if(end_date!=null||end_date.length()>0)
    	{
    		where_str.append(" and "+fileset+"<="+Sql_switcher.dateValue(end_date));
    	}
    	return where_str.toString();	
    }   
}
