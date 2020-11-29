/**
 * 
 */
package com.hjsj.hrms.businessobject.info;

import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.*;

/**
 * @author Owner
 *
 */
public class SortFilter {
	public String getSortPersonField(Connection conn)
	{
		String fieldsort=null;
		try
		{
			 OtherParam param=new OtherParam(conn);
			 Map  setmap=param.serachAtrr("/param/employ_type");
			 if(setmap==null) {
                 return null;
             }
			 if("true".equalsIgnoreCase(setmap.get("valid").toString())) {
                 fieldsort=setmap.get("field").toString();
             } else {
                 fieldsort=null;
             }
			 //fieldsort=null;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return fieldsort;
	}
	public String getPersonDBValide(Connection conn)
	{
		String fieldsort=null;
		try
		{
			 OtherParam param=new OtherParam(conn);
			 Map  setmap=param.serachAtrr("/param/base_fields");
			 return setmap.get("valid").toString();				
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return fieldsort;
	}
	/**
	 * @按人员类型过滤子集
	 * @param infoSetList
	 * @return
	 */
	public List getPersonDBFilterSet(List infoSetList,String dbpre,Connection conn)
	{
		List resultList=new ArrayList();
		try{
			OtherParam param=new OtherParam(conn);
			Map  setmap=param.serachAtrr("/param/base_fields");
			if(setmap==null) {
                return infoSetList;
            }
			String valid=setmap.get("valid").toString();	
			
			// WJH　２０１３－６－２７，　人员库指标未设置时，走默认指标集范围 tableset 放到了循环外
		    setmap=param.serachatomElemetValue("/param/base_fields/base_field[@name=\"" + dbpre + "\"]/table");
	        String tableset = "";
	        if(setmap!=null) {
                tableset=setmap.get("table").toString();
            }
	        
			if(valid!=null && !"null".equalsIgnoreCase(valid) && "true".equalsIgnoreCase(valid)
					&& tableset!=null && !"".equalsIgnoreCase(tableset) )
			{
	          for(int i=0;i<infoSetList.size();i++)
	          {
	        	  FieldSet fieldset=(FieldSet)infoSetList.get(i);
	        	  if(fieldset!=null && tableset.toUpperCase().indexOf(fieldset.getFieldsetid().toUpperCase())!=-1)
	        	  {
	        		resultList.add(infoSetList.get(i));  
	        	  }
	          }
			}else
			{
				return infoSetList;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return resultList;
	}
	/**
	 * @按人员类型过滤指标
	 * @param infoSetList
	 * @return
	 */
	public List getPersonDBFilterField(List infoFieldList,String dbpre,Connection conn)
	{
		List resultList=new ArrayList();
		try{
			  OtherParam param=new OtherParam(conn);
			  Map  setmap=param.serachAtrr("/param/base_fields");
				 if(setmap==null) {
                     return infoFieldList;
                 }
			   String valid=setmap.get("valid").toString();	
			 if(valid!=null && !"null".equalsIgnoreCase(valid) && "true".equalsIgnoreCase(valid))
			 {
		      
		        setmap=param.serachatomElemetValue("/param/base_fields/base_field[@name=\"" + dbpre + "\"]/field");
		        if(setmap==null) {
                    return infoFieldList;
                }
	            String tableset=setmap.get("field").toString();
	            if(tableset==null||tableset.length()<=0) {
                    return infoFieldList;
                }
		          for(int i=0;i<infoFieldList.size();i++)
		          {
		        	  FieldItem fielditem=(FieldItem)infoFieldList.get(i);
		        	  if(fielditem!=null && tableset.toUpperCase().indexOf(fielditem.getItemid().toUpperCase())!=-1)
		        	  {
		        		resultList.add(infoFieldList.get(i));  
		        	  }
		          }
			}
			else
			{
				return infoFieldList;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			return infoFieldList;
		}
		return resultList;
	}
	
	/**
	 * @按人员类型过滤子集
	 * @param infoSetList
	 * @return
	 */
	public List getSortPersonFilterSet(List infoSetList,String personsort,Connection conn)
	{
		List resultList=new ArrayList();
		try{
			if(personsort!=null && !"null".equalsIgnoreCase(personsort) && personsort.length()>0&&!"All".equalsIgnoreCase(personsort))
			{
		      OtherParam param=new OtherParam(conn);
		      Map  setmap=param.serachatomElemetValue("/param/employ_type/type_field[@name=\"" + personsort + "\"]/table");
	          String tableset=setmap.get("table").toString();
	          for(int i=0;i<infoSetList.size();i++)
	          {
	        	  FieldSet fieldset=(FieldSet)infoSetList.get(i);
	        	  if(fieldset!=null && tableset.toUpperCase().indexOf(fieldset.getFieldsetid().toUpperCase())!=-1)
	        	  {
	        		resultList.add(infoSetList.get(i));  
	        	  }
	          }
			}else
			{
				return infoSetList;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return resultList;
	}
	/**
	 * @按人员类型过滤指标
	 * @param infoSetList
	 * @return
	 */
	public List getSortPersonFilterField(List infoFieldList,String personsort,Connection conn)
	{
		List resultList=new ArrayList();
		try{
			if(personsort!=null && !"null".equalsIgnoreCase(personsort) && personsort.length()>0&& !"All".equalsIgnoreCase(personsort))
			{
			      OtherParam param=new OtherParam(conn);
			      Map  setmap=param.serachatomElemetValue("/param/employ_type/type_field[@name=\"" + personsort + "\"]/field");
		          String tableset=setmap.get("field").toString();
		          for(int i=0;i<infoFieldList.size();i++)
		          {
		        	  FieldItem fielditem=(FieldItem)infoFieldList.get(i);
		        	  if(fielditem!=null && tableset.toUpperCase().indexOf(fielditem.getItemid().toUpperCase())!=-1)
		        	  {
		        		resultList.add(infoFieldList.get(i));  
		        	  }
		          }
			}
			else
			{
				return infoFieldList;
			}
		}catch(Exception e)
		{
			
		}
		return resultList;
	}
	public String getBirthDay(String cardid)
	{
		if(cardid!=null && cardid.length()==18)
		{
			return cardid.substring(6,10)+ "." + cardid.substring(10,12) + "." + cardid.substring(12,14);
		}
		else if(cardid!=null && cardid.length()==15)
		{
			return "19" + cardid.substring(6,8)+ "." + cardid.substring(8,10) + "." + cardid.substring(10,12);
		}else
		{
			return "";
		}
	}
	public String getAge(String cardid)
	{
		if(cardid!=null && cardid.length()==18)
		{
			return String.valueOf(GetHisAge(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DATE),Integer.parseInt(cardid.substring(6,10)),Integer.parseInt(cardid.substring(10,12)),Integer.parseInt(cardid.substring(12,14))));
		}
		else if(cardid!=null && cardid.length()==15)
		{
			return String.valueOf(GetHisAge(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DATE),Integer.parseInt("19" + cardid.substring(6,8)),Integer.parseInt(cardid.substring(8,10)),Integer.parseInt(cardid.substring(10,12))));
		}else
		{
			return "0";
		}
	}
	public String getSex(String cardid)
	{
		if(cardid!=null && cardid.length()==18)
		{
			return String.valueOf((Integer.parseInt(cardid.substring(14,17))%2)==0?2:1);
		}
		else if(cardid!=null && cardid.length()==15)
		{
			return String.valueOf((Integer.parseInt(cardid.substring(12,15))%2)==0?2:1);
		}else
		{
			return "";
		}
	}
	public String getWorkAge(String datastr)
	{
	   StringTokenizer token=new StringTokenizer(datastr,".");
	   String year=String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	   String month=String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
	   String day=String.valueOf(Calendar.getInstance().get(Calendar.DATE));
	   if(token.hasMoreTokens()) {
           year=token.nextToken();
       }
	   if(token.hasMoreTokens()) {
           month=token.nextToken();
       }
	   if(token.hasMoreTokens()) {
           day=token.nextToken();
       }
	   return String.valueOf(GetHisAge(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DATE),Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day)));
	}
	/*
	 * get person age method
	 * */
	public int GetHisAge(int ncYear,int ncMonth,int ncDay,int nYear,int nMonth,int nDay)
	{
		/*
		 * 根据日期获得年龄的运算
		 * */
		int nAage,nMM,nDD,Result;
		nAage=ncYear-nYear;                              
		nMM=ncMonth-nMonth+1;
		nDD=ncDay-nDay;
		if(nMM>0)
		{
			Result=nAage;
		}
		else if(nMM<0)
		{
			Result=nAage-1;
			if(Result <0)
			{
				Result=0;
			}
		}
		else
		{
			if(nDD>=0)
			{
				Result=nAage;
			}
			else
			{
				Result=nAage-1;
				if(Result<0) {
                    Result=0;
                }
			}
		}
		return Result;
	}
	/**
	 * 过滤人员显示指标
	 * @param fieldstr
	 * @param infoFieldList
	 * @return
	 */
	public String getBrowseFields(String fieldstr,String userbase,Connection conn,UserView userView)
	{
		if(fieldstr==null||fieldstr.length()<=0) {
            return "";
        }
		List infoFieldList=userView.getPrivFieldList("A01");      //获得当前子集的所有属性
		infoFieldList=getSortPersonFilterField(infoFieldList,"",conn);
		infoFieldList=new SortFilter().getPersonDBFilterField(infoFieldList, userbase,conn);
		String[] fieldstrAry=fieldstr.split(",");
		StringBuffer buf=new StringBuffer();
		buf.append(",");
		for(int r=0;r<fieldstrAry.length;r++)
		{
			String str=fieldstrAry[r];
			if(str==null||str.length()<=0) {
                continue;
            }
			for(int i=0;i<infoFieldList.size();i++)
	         {
	       	    FieldItem fielditem=(FieldItem)infoFieldList.get(i);
	       	    if(fielditem!=null && fielditem.getItemid().equalsIgnoreCase(str))
	       	    {
	       	    	buf.append(fielditem.getItemid()+",");
	       	    	break;
	       	    }
	       	}
		}
		if(buf!=null&&buf.length()>0) {
            buf.setLength(buf.length()-1);
        }
		return buf.toString();
	}
}

