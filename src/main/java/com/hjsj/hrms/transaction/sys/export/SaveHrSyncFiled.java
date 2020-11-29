package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.transaction.sys.export.syncFrigger.CreateSyncFrigger;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveHrSyncFiled extends IBusiness {

  
    public void execute() throws GeneralException {
    	HrSyncBo hsb = new HrSyncBo(this.frameconn);
    	String type = (String)this.getFormHM().get("type");
    	/*
    	HashMap oldfieldshm = hsb.getFieldsMap();// 原有的指标字段  	
    	String oldfields = hsb.getTextValue(hsb.FIELDS);// 原有的指标字段  
        HashMap savefieldshm = hsb.getSaveFieldsMap(code_fields); // 要保存的指标字段
    	*/
        ArrayList code_fields=new ArrayList();
        code_fields = (ArrayList)this.getFormHM().get("code_fields");
        //String appfield = (String)this.getFormHM().get("appfield");
        //String savefieldstr = hsb.getSaveFieldsStr(code_fields); // 要保存的指标字段
        /*
         * 
        String[] addFields = this.getAddFields(oldfieldshm,savefieldshm,savefieldstr,oldfields); 
        this.addFields(addFields);
        String[] dorpFields = this.getDropFields(oldfieldshm,savefieldshm,oldfields,savefieldstr);
        this.dropFields(dorpFields);
        */
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        this.saveFields(type,code_fields,dao);
        String mess ="";
        mess=getCardMesslist(code_fields);
		this.getFormHM().put("types","ok");
		this.getFormHM().put("mess",mess);
		String codefieldstr=hsb.getTextValue(HrSyncBo.CODE_FIELDS);
		ArrayList debarlist = this.debarFields(code_fields,codefieldstr);
		this.saveCodeFields(debarlist,dao);
		String mess2 = "";
		mess2=getCardMesslist(debarlist);
		this.getFormHM().put("mess2",mess2);
		hsb = new HrSyncBo(this.frameconn);
		//if(type.equalsIgnoreCase("a"))
	    hsb.creatHrTable("t_hr_view");
		//else if(type.equalsIgnoreCase("b"))
		//	hsb.creatHrTable("t_org_view");
		
		//String dbnamestr = hsb.getTextValue(hsb.BASE);
		//hsb.HrSync(dbnamestr,savefieldstr);
	    //新增字段时更新触发器 wangb 20170615
	    boolean fieldAndCode = false;
		String fieldAndCodeSeq = ":";
		if ("1".equals(hsb.getAttributeValue(HrSyncBo.FIELDANDCODE))){
			fieldAndCode = true;
			fieldAndCodeSeq = hsb.getAttributeValue(HrSyncBo.FIELDANDCODESEQ);
		}
		
		CreateSyncFrigger csf = new CreateSyncFrigger(this.frameconn,
				this.userView, fieldAndCode, fieldAndCodeSeq);
		//先删除触发器 wangb 20170615
		csf.delFrigger(CreateSyncFrigger.HR_FLAG);
		//后添加触发器 wangb 20170615
		csf.createFrigger(CreateSyncFrigger.HR_FLAG);
		
	}
    
    public void addFields(String[] arr)throws GeneralException
    {
    	for(int i=0;i<arr.length;i++)
    	{
    		if(!(arr[i]==null || "".equals(arr[i])))
    		{
    			if(!("a0100".equalsIgnoreCase(arr[i]) || "b0110".equalsIgnoreCase(arr[i])
    					|| "e0122".equalsIgnoreCase(arr[i]) || "a0101".equalsIgnoreCase(arr[i])
    					|| "e01A1".equalsIgnoreCase(arr[i]) || "username".equalsIgnoreCase(arr[i])
    					|| "userpassword".equalsIgnoreCase(arr[i]) || "flag".equalsIgnoreCase(arr[i])))
    			{
    				this.addField(arr[i]);
    			}
    		}
    	}
    }
    public void dropFields(String[] arr)throws GeneralException
    {
    	for(int i=0;i<arr.length;i++)
    	{
    		if(!(arr[i]==null || "".equals(arr[i])))
    		{   		
    			if(!("a0100".equalsIgnoreCase(arr[i]) || "b0110".equalsIgnoreCase(arr[i])
    					|| "e0122".equalsIgnoreCase(arr[i]) || "a0101".equalsIgnoreCase(arr[i])
    					|| "e01A1".equalsIgnoreCase(arr[i]) || "username".equalsIgnoreCase(arr[i])
    					|| "userpassword".equalsIgnoreCase(arr[i]) || "flag".equalsIgnoreCase(arr[i])))
    			{
    				this.dropField(arr[i]);   	
    			}
    					
    		}
    	}
    }
    public void dropField(String fieldstr)throws GeneralException
    {	
		DbWizard dbw=new DbWizard(this.getFrameconn());		
		if(dbw.isExistField("t_hr_view", fieldstr))
		{
			Table table=new Table("t_hr_view");
			FieldItem fi = DataDictionary.getFieldItem(fieldstr);
			if(fi!=null)
			{
				Field field = fi.cloneField();
				table.addField(field);
				dbw.dropColumns(table);
			}			
		}			
    }
    public void addField(String fieldstr)throws GeneralException
    {	
		DbWizard dbw=new DbWizard(this.getFrameconn());		
//		if(!dbw.isExistField("t_hr_view", fieldstr))
//		{
			Table table=new Table("t_hr_view");
			FieldItem fi = DataDictionary.getFieldItem(fieldstr);
			if(fi!=null)
			{
				String codesetid = fi.getCodesetid();
				Field field = fi.cloneField();
				if(!("0".equals(codesetid)))
				{
					field.setLength(50);
				}
				table.addField(field);
				dbw.addColumns(table);
			}			
//		}			
    }
    /**
     * 获得要添加的字段
     * @param 
     * @param oldfields 原有的指标字段
     * @param fields 要保存的指标字段
     * @return
     */
    public String[] getDropFields(HashMap oldfieldshm,HashMap savefieldshm,String oldfields,String savefieldstr)
    {	
    	String[] retNull= new String[0];
    	if(oldfieldshm.isEmpty())
    		return retNull;   
    	String[] ret;
    	int t=0;
    	if(savefieldstr==null || "".equals(savefieldstr))
    	{
    		ret = oldfields.split(",");
    		return ret;
    	}else
    	{
    		String[] fieldsArr = oldfields.split(",");
        	ret= new String[100];	   		       	
        	for(int i=0;i<fieldsArr.length;i++)
        	{
        		if(!(fieldsArr[i]==null || "".equals(fieldsArr[i])))
        		{
        			if(!(savefieldshm.containsKey(fieldsArr[i])))
        			{
        				ret[t]=fieldsArr[i];
        				t++;
	
        			}	
        		}
        	}
    	}   	
    	if(t>0)
    		return ret;
    	else 
    		return retNull;
    }
    /**
     * 获得要删除的字段
     * @param 
     * @param savefieldstr 要保存的指标字段
     * @param fields 原有的指标字段
     * @return
     */
    public String[] getAddFields(HashMap oldfieldshm,HashMap savefieldshm,String savefieldstr,String oldfields)
    {	
    	int t=0;
    	String[] retNull= new String[0];
    	if(savefieldshm.isEmpty())
    		return retNull;   
    	String[] ret;
    	// fields为空，就是删除所有活动字段
    	if(oldfields==null || "".equals(oldfields))
    	{
    		ret = savefieldstr.split(",");
    		return ret;
    	}else
    	{
    		String[] fieldsArr = savefieldstr.split(",");
        	ret= new String[100];	   		
        	for(int i=0;i<fieldsArr.length;i++)
        	{
        		if(!(fieldsArr[i]==null || "".equals(fieldsArr[i])))
        		{
        			if(!(oldfieldshm.containsKey(fieldsArr[i])))
        			{
        				ret[t]=fieldsArr[i];
        				t++;
        			}	
        		}
        	}
    	}    	
    	if(t>0)
    		return ret;
    	else 
    		return retNull;
    }
    /**
	  * 通过表编号的到表信息
	  * @param cardno
	  * @return String
	  */
	 public String getCardMesslist(ArrayList code_fields)
	 {
	    	StringBuffer mess=new StringBuffer();
	    	if(code_fields==null||code_fields.size()<=0)
	    		return "";	
	    	String sql="";
	    	try
	    	{
	    		ContentDAO dao=new ContentDAO (this.getFrameconn());
	    		mess.append("<br>");
	    		int r=1;
	    		for(int i=0;i<code_fields.size();i++)
		    	{
		    		if(code_fields.get(i).toString()!=null&& "b0110".equals(code_fields.get(i).toString()))
		    			mess.append("单位名称("+code_fields.get(i).toString()+")");
		    		if(code_fields.get(i).toString()!=null&& "e01a1".equals(code_fields.get(i).toString()))
		    			mess.append("职位名称("+code_fields.get(i).toString()+")");		
		    		sql="select itemid,itemdesc from fielditem where Upper(itemid)='"+code_fields.get(i).toString().toUpperCase()+"'";
		    		RowSet rs=dao.search(sql);
		    		if(rs.next())
			    	{
			    		   mess.append(rs.getString("itemdesc")+"("+code_fields.get(i).toString()+")");
			    	}
		    		if(r%5==0)
		    			   mess.append("<br>");
		    		   else
		    			 mess.append(",");  
		    	    r++;
		    	}
	    		 mess.append("<br>");
	    	}catch(Exception e)
	    	{
	    	  e.printStackTrace();	
	    	}
	    	return mess.toString();
	  }
	  /**
	   * 保存
	   * @param code_fields
	   * @param dao
	   * @return
	   * @throws GeneralException
	   */
	  private boolean saveFields(String type,ArrayList code_fields,ContentDAO dao)throws GeneralException 
	  {
		  boolean isCorrect=false;
		  StringBuffer buf=new StringBuffer();
		  if(code_fields==null||code_fields.size()<=0)
			  buf.append("");
		  else
		  {
			  for(int i=0;i<code_fields.size();i++)
			  {
				  buf.append(""+code_fields.get(i).toString()+",");
			  }
			  buf.setLength(buf.length()-1);
		  }
		  HrSyncBo hsb = new HrSyncBo(this.frameconn);
		  if("A".equalsIgnoreCase(type)){
			  String fields = hsb.getTextValue(HrSyncBo.FIELDS);
			//  if(fields.equalsIgnoreCase("")){
			//	  fields = buf.toString();
			//	  hsb.setTextValue(hsb.FIELDS,fields);
			//  }else{
			//	  if(fields.indexOf(buf.toString())==-1){
			//		  fields += ","+buf.toString();
			//		  hsb.setTextValue(hsb.FIELDS,fields);
			//	  }
			//  }
			  for(int i=0;i<code_fields.size();i++){//add
				  if(fields.indexOf(code_fields.get(i).toString())==-1)
					  hsb.setAppAttributeValue(HrSyncBo.A,code_fields.get(i).toString(),code_fields.get(i).toString());
			  }
			  String[] oldfields = fields.split(",");
			  for(int i=0;i<oldfields.length;i++){//del
				  if(buf.toString().indexOf(oldfields[i])==-1){
					  hsb.delAppAttributeValue(HrSyncBo.A,oldfields[i]);
					  hsb.deleteColumn("t_hr_view",oldfields[i]);
				  }
			  }
			  hsb.setTextValue(HrSyncBo.FIELDS,buf.toString());
		  }else if("B".equalsIgnoreCase(type)){
			  String fields = hsb.getTextValue(HrSyncBo.ORG_FIELDS);
			  //if(fields.indexOf(buf.toString())==-1){
			//	  fields += ","+buf.toString();
			//	  hsb.setTextValue(hsb.ORG_FIELDS,fields);
			//  }
			  for(int i=0;i<code_fields.size();i++){
				  if(fields.indexOf(code_fields.get(i).toString())==-1)
				  hsb.setAppAttributeValue(HrSyncBo.B,code_fields.get(i).toString(),code_fields.get(i).toString());
			  }
			  String[] oldfields = fields.split(",");
			  for(int i=0;i<oldfields.length;i++){
				  if(buf.toString().indexOf(oldfields[i])==-1){
					  hsb.delAppAttributeValue(HrSyncBo.B,oldfields[i]);
					  hsb.deleteColumn("t_hr_view",oldfields[i]);
				  }
			  }
			  hsb.setTextValue(HrSyncBo.ORG_FIELDS,buf.toString());
		  }
		  hsb.saveParameter(dao);
		  return isCorrect;
	  }
	  
	  private boolean saveCodeFields(ArrayList code_fields,ContentDAO dao)throws GeneralException 
		{
			boolean isCorrect=false;
			StringBuffer buf=new StringBuffer();
			if(code_fields==null||code_fields.size()<=0)
				buf.append("");
			else
			{
				for(int i=0;i<code_fields.size();i++)
				{
					buf.append(""+code_fields.get(i).toString()+",");
				}
				buf.setLength(buf.length()-1);
			}
			HrSyncBo hsb = new HrSyncBo(this.frameconn);
			hsb.setTextValue(HrSyncBo.CODE_FIELDS,buf.toString());
			hsb.saveParameter(dao);
			return isCorrect;
		}
	  
	  private ArrayList debarFields(ArrayList fileds,String codefileds){
		  ArrayList codefiledlist = new ArrayList();
		  if(fileds==null||fileds.size()<=0)
			  return codefiledlist;
		  String[] codefiled = codefileds.split(",");
		  for(int i=0;i<codefiled.length;i++){
			  if(fileds.indexOf(codefiled[i])!=-1){
				  codefiledlist.add(codefiled[i]);
			  }
		  }
		  return codefiledlist;
		  
	  }

}
