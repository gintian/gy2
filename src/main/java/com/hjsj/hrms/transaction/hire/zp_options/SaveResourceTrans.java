package com.hjsj.hrms.transaction.hire.zp_options;


import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
/**
 * <p>Title:SaveAssignPrivTrans</p>
 * <p>Description:保存用户权限信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 9, 2005:10:28:51 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveResourceTrans extends IBusiness {
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
	 /**根据传过的的指标串，分解成对应的指标对象*/
    private ArrayList splitField(String strfields)
    {
        ArrayList list=new ArrayList(); 
        if(!",".equals(strfields.substring(strfields.length()))){
        	strfields = strfields + ",";
        }
        int pos=0;
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            String fieldname=st.nextToken();
            list.add(fieldname);
        }
        return list;
    }
    private ArrayList splitField2(String strfields)
    {
        ArrayList list=new ArrayList(); 
        if(!",".equals(strfields.substring(strfields.length()))){
        	strfields = strfields + ",";
        }
        int pos=0;
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            String fieldname=st.nextToken();
            if(fieldname.indexOf("[")!=-1)
            	fieldname=fieldname.substring(0,fieldname.indexOf("["));
            list.add(fieldname);
        }
        return list;
    }
    /**根据传过的的子集串，分解成对应的子集字符串数组 wanglonghua修改*/
    private String[] splitSet(String strsets)
    {    
        StringTokenizer st = new StringTokenizer(strsets, ",");
        String[] strSet=new String[st.countTokens()];
        for(int i=0;st.hasMoreTokens();i++)
        {
        	String str=st.nextToken();
        	if(str.indexOf("[")!=-1)
        		str=str.substring(0,3);
           strSet[i]=str + "{";
        }
        return strSet;
    }
    public void execute() throws GeneralException { 		
        String tab_name=(String)this.getFormHM().get("tab_name");
        try
        {
	        if(tab_name==null|| "".equals(tab_name))
	            return;
	        /**
	         * 保存人员库
	         */
	        if("dbpriv".equals(tab_name))
	        {
	            saveDbPriv();
	        }
	        /**
	         * 保存子集
	         */
	        if("tablepriv".equals(tab_name))
	        {
	            saveTablePriv();
	        }
	        /**
	         * 保存指标
	         */
	        if("fieldpriv".equals(tab_name))
	        {
	            saveFieldPriv2();
	            saveFunc_only();
	        }  
	        
	        EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
	        employNetPortalBo.refreshStaticAttribute();
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ee);
        }
    }
    public void saveFunc_only()
    {
    	try
    	{
    		String func_only=(String)this.getFormHM().get("func_only");
    		ContentDAO dao = new ContentDAO(this.getFrameconn());
    		/*RecordVo vo = new RecordVo("constant");
    		vo.setString("constant", "ZP_ONLY_FIELD");
    		vo=dao.findByPrimaryKey(vo);*/
    		String sql="select * from constant where UPPER(constant)='ZP_ONLY_FIELD'";
    		RowSet rs=dao.search(sql);
    		if(rs.next())
    		{
    	    	String sqlsql = "update constant set str_value = '"+(func_only==null?"":func_only)+"' where constant='ZP_ONLY_FIELD'";
    	    	dao.update(sqlsql,new ArrayList());
        	}
    		else
    		{
    			String sqlsql = "insert into constant(constant,type,describe,str_value) values('ZP_ONLY_FIELD','A','招聘唯一性校验指标','"+(func_only==null?"":func_only)+"')";
    	    	dao.insert(sqlsql, new ArrayList());
    		}
    	 	RecordVo avo=new RecordVo("constant");
    	 	avo.setString("constant","ZP_ONLY_FIELD");
    	 	avo.setString("describe","招聘唯一性校验指标");
    	 	avo.setString("str_value",func_only==null?"":func_only);
    	 	ConstantParamter.putConstantVo(avo,"ZP_ONLY_FIELD");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    
    
    
    /**
     * 保存采集指标
     *2007-4-14  dengcan
     */
    public void saveFieldPriv2()
    {
    	String field_str=(String)this.getFormHM().get("field_set_str");
    	String show_field_str=(String)this.getFormHM().get("show_field_str");
    	String mustFill_field_str=(String)this.getFormHM().get("mustFill_field_str");
    	
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		List list=new ArrayList();
		RecordVo vo= ConstantParamter.getConstantVo("ZP_FIELD_LIST");
		String fieldlist=vo.getString("str_value");
		RecordVo rv= ConstantParamter.getConstantVo("ZP_SUBSET_LIST");
		String setliststr = "";
		if(rv != null && !"".equals(rv)){
			setliststr=rv.getString("str_value");
		}
		String[] setlist=splitSet(setliststr);
		StringBuffer fielditemstr = new StringBuffer(); 
		ArrayList fieldList = new ArrayList();
		String fieldsetid = "";
		boolean[] ishavefield=new boolean[setlist.length];
	    try
		{
	    	for(int i=0;i<setlist.length;i++)
	    	{
	    		ishavefield[i]=false;
	    	}
	    	if(field_str != null && !"".equals(field_str)){
	    	   fieldList = splitField(field_str);
	    	   for(int i=0;i<fieldList.size();i++)
		 	   {
	    	   	   String substr = (String)fieldList.get(i);
	    	   	   int index = substr.indexOf(".");
	    	   	   fieldsetid = substr.substring(0,index);
	    	   	   for(int j=0;j<setlist.length;j++)
	    	   	   {
	    	   	   	if(setlist[j].indexOf(fieldsetid)!=-1)
					{
	    	   	   		String d=substr.substring(index+1,substr.length());
	    	   	   		StringBuffer temp=new StringBuffer(d);
	    	   	   		if(show_field_str.indexOf("."+d+",")!=-1)
	    	   	   			temp.append("[1");
	    	   	   		else
	    	   	   			temp.append("[0");
		    	   	   	if(mustFill_field_str.indexOf("."+d+",")!=-1)
	    	   	   			temp.append("#1]");
	    	   	   		else
	    	   	   			temp.append("#0]");
	    	   	   		setlist[j]+=temp.toString().toUpperCase()+ ",";						
						ishavefield[j]=true;
						break;
					}
	    	   	   }	    		  
		 	   }
	    	} 
	    	for(int i=0;i<setlist.length;i++)
	    	{
	    		if(ishavefield[i])
	    		{
	    			setlist[i]+="},";
	    			fielditemstr.append(setlist[i]);
	    		}
	    	}
	    	String sqlsql = "update constant set str_value = '"+fielditemstr.toString()+"' where constant = 'ZP_FIELD_LIST'";
    	 	dao.update(sqlsql,list);
    	 	RecordVo avo=new RecordVo("constant");
    	 	avo.setString("constant","ZP_FIELD_LIST");
    	 	avo.setString("describe","招聘子集字段列表");
    	 	avo.setString("str_value",fielditemstr.toString());
    	 	ConstantParamter.putConstantVo(avo,"ZP_FIELD_LIST");
    	 	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
    	
    	
    	
    }
    
    
    
    
    /**
     * 保存采集指标
     * 2006-1-4 wang 修改
     */
	  private void saveFieldPriv()
	  {
	    String field_str=(String)this.getFormHM().get("field_set_str");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		List list=new ArrayList();
		RecordVo vo= ConstantParamter.getConstantVo("ZP_FIELD_LIST");
		String fieldlist=vo.getString("str_value");
		RecordVo rv= ConstantParamter.getConstantVo("ZP_SUBSET_LIST");
		String setliststr = "";
		if(rv != null && !"".equals(rv)){
			setliststr=rv.getString("str_value");
		}
		String[] setlist=splitSet(setliststr);
		StringBuffer fielditemstr = new StringBuffer(); 
		ArrayList fieldList = new ArrayList();
		String fieldsetid = "";
		boolean[] ishavefield=new boolean[setlist.length];
	    try
		{
	    	for(int i=0;i<setlist.length;i++)
	    	{
	    		ishavefield[i]=false;
	    	}
	    	if(field_str != null && !"".equals(field_str)){
	    	   fieldList = splitField(field_str);
	    	   for(int i=0;i<fieldList.size();i++)
		 	   {
	    	   	   String substr = (String)fieldList.get(i);
	    	   	   int index = substr.indexOf(".");
	    	   	   fieldsetid = substr.substring(0,index);
	    	   	   for(int j=0;j<setlist.length;j++)
	    	   	   {
	    	   	   	if(setlist[j].indexOf(fieldsetid)!=-1)
					{
						setlist[j]+=substr.substring(index+1,substr.length()).toUpperCase()+ ",";						
						ishavefield[j]=true;
						break;
					}
	    	   	   }	    		  
		 	   }
	    	} 
	    	for(int i=0;i<setlist.length;i++)
	    	{
	    		if(ishavefield[i])
	    		{
	    			setlist[i]+="},";
	    			fielditemstr.append(setlist[i]);
	    		}
	    	}
	    	String sqlsql = "update constant set str_value = '"+fielditemstr.toString()+"' where constant = 'ZP_FIELD_LIST'";
    	 	dao.update(sqlsql,list);	    
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
    /**
     * 保存子集
     */
    private void saveTablePriv()
    {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
        List list = new ArrayList();
    	StringBuffer func_str=new StringBuffer("");   	
        StringBuffer strsql=new StringBuffer();  
        String func=(String)this.getFormHM().get("field_set_str");
        func = PubFunc.hireKeyWord_filter_reback(func);
        if(func!=null&&func.indexOf(",A01[01#1`02#1],")==-1){//zzk 若没有选择基本信息 保存自动加上基本信息
        	func=",A01[01#1`02#1]"+func;
        }
        if(func!=null && func.length()>0&&!",".equals(func))
        {
        	String[] arr=func.split(",");
	        for(int i=0;i<arr.length;i++)
	        {
	            if("".equals(arr[i].trim()))
	                continue;
	            if(i<arr.length-1){
	            	func_str.append(arr[i]);
	                func_str.append(",");
	            }else{
	            	func_str.append(arr[i]);
	            }
	        }
        }
        try{
           RecordVo rv= ConstantParamter.getConstantVo("ZP_SUBSET_LIST");
           if(rv != null){
              strsql.append("delete from constant where constant='ZP_SUBSET_LIST'");
		      dao.delete(strsql.toString(),list);    //删除常量表中的查询设值得的项
		      strsql.delete(0,strsql.length());
		      this.getFrameconn().commit();
           }
           
           list.clear();
           list.add(func_str.toString());
		   String sql = "insert into constant(constant,type,str_value,Describe) values('ZP_SUBSET_LIST','',?,'子集')";
		   dao.insert(sql,list); //添加纪录在常量表中
		   list.clear();
		   
		    RecordVo avo=new RecordVo("constant");
	   	 	avo.setString("constant","ZP_SUBSET_LIST");
	   	 	avo.setString("describe","子集");
	   	 	avo.setString("str_value",func_str.toString());
	   	 	ConstantParamter.putConstantVo(avo,"ZP_SUBSET_LIST");
		   
		   
		   
		   this.getFrameconn().commit();
		   RecordVo vo= ConstantParamter.getConstantVo("ZP_FIELD_LIST");
	       if(vo == null){
	          String sqlsql = "insert into constant(constant,type,str_value,Describe) values('ZP_FIELD_LIST','','','采集指标')";
	          dao.insert(sqlsql,list);
	          this.getFrameconn().commit();
	          
	          	RecordVo avo1=new RecordVo("constant");
		   	 	avo1.setString("constant","ZP_FIELD_LIST");
		   	 	avo1.setString("describe","采集指标");
		   	 	avo1.setString("str_value","");
		   	 	ConstantParamter.putConstantVo(avo1,"ZP_FIELD_LIST");
	          
	       }
	       String fieldlist = "";
	       if(vo != null){
		      fieldlist=vo.getString("str_value");
	       }
		   if(fieldlist != null && !"".equals(fieldlist)){
		      ArrayList infoSetList = splitField2(func_str.toString());
		      StringBuffer subfieldList = new StringBuffer();
			  for(int i=0;i<infoSetList.size();i++){
				 String setvalue = (String)infoSetList.get(i);
				 int setindex = fieldlist.indexOf(setvalue);
				 if(setindex != -1){
					String substr = fieldlist.substring(setindex,fieldlist.length());
					int subindex = substr.indexOf("},");
					subfieldList.append(fieldlist.substring(setindex,setindex+subindex+2));
				}
			  }
			  this.getFrameconn().commit();
			  String sssql = "update constant set str_value = '"+subfieldList.toString()+"' where constant = 'ZP_FIELD_LIST'";
			  dao.update(sssql,list);
			  
			    RecordVo avo2=new RecordVo("constant");
	    	 	avo2.setString("constant","ZP_FIELD_LIST");
	    	 	avo2.setString("describe","招聘子集字段列表");
	    	 	avo2.setString("str_value",subfieldList.toString());
	    	 	ConstantParamter.putConstantVo(avo2,"ZP_FIELD_LIST");
		   }
       }catch(SQLException e)
	   {
          e.printStackTrace();
       }
    }
    /**
     * 保存人员库
     */
    private void saveDbPriv() {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
        List paramList=new ArrayList();
        StringBuffer func_str=new StringBuffer(""); 
        StringBuffer strsql=new StringBuffer();  
        String[] func=(String[])this.getFormHM().get("func");
        if(func!=null)
        {
          for(int i=0;i<func.length;i++)
          {
               if("".equals(func[i].trim()))
                     continue;
               if(i<func.length-1){
                	func_str.append(func[i]);
                    func_str.append(",");
               }else{
            	    func_str.append(func[i]);
               }
           }
        }
        try{
           strsql.append("delete from constant where constant='ZP_DBNAME'");
		   dao.delete(strsql.toString(),paramList);    //删除常量表中的查询设值得的项
		   String sql = "insert into constant(constant,type,str_value,Describe) values('ZP_DBNAME','','"+func_str+"','人才库')";
		   dao.insert(sql,paramList); //添加纪录在常量表中
		   
		   RecordVo avo2=new RecordVo("constant");
	   	 	avo2.setString("constant","ZP_DBNAME");
	   	 	avo2.setString("describe","人才库");
	   	 	avo2.setString("str_value",func_str.toString());
	   	 	ConstantParamter.putConstantVo(avo2,"ZP_DBNAME");
       }catch(SQLException e)
	   {
          e.printStackTrace();
       }
    }
}
