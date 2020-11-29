package com.hjsj.hrms.transaction.hire.zp_options;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;



/**
 * @author fengxin
 */
public class SearchResourceTrans extends IBusiness {
    /**根据传过的的指标串，分解成对应的指标对象*/
    private ArrayList splitField(String strfields)throws GeneralException
    {
        ArrayList list=new ArrayList();
        if(!",".equals(strfields.substring(strfields.length()))){
        	strfields=strfields+",";
        } 
        StringTokenizer st = new StringTokenizer(strfields, ",");
        HashMap map=new HashMap();
        String str="";
        while (st.hasMoreTokens())
        {
            String fieldname=st.nextToken();
            String fieldSetName="";
            if(3<=fieldname.length()){
               fieldSetName=fieldname.substring(0, 3);
            }
            map.put(fieldSetName, fieldname);
            str+="'"+fieldSetName+"',";
        }
        if(str.length()>0){
           str=str.substring(0, str.length()-1);
        }
        else{
        	str="''";
        }
        String sql=" select * from fieldSet where fieldSetId in("+str+") order by Displayorder";
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try {//为了子集顺序与库结构中的顺序一致
			this.frowset=dao.search(sql);
	        while( this.frowset.next()){
	        	if(map.containsKey(this.frowset.getString("fieldSetid"))){
	        		list.add(map.get(this.frowset.getString("fieldSetid")));
	        	}
	        }

		} catch (SQLException e) {
		      e.printStackTrace();
		      throw GeneralExceptionHandler.Handle(e);
		}

        return list;
    }
    /**
     * 生成选库前台界面
     * @return
     * @throws GeneralException
     */
    private String searchDbNameHtml()throws GeneralException
    {
        StringBuffer db_str=new StringBuffer();
        StringBuffer strsql=new StringBuffer();;
        strsql.append("select dbid,dbname,pre from dbname order by dbid");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
	      this.frowset = dao.search(strsql.toString());

	      while(this.frowset.next())
	      {
	      	  db_str.append("<div id='");
	          db_str.append("div");
	          db_str.append(this.frowset.getString("dbid"));
	          db_str.append("'>");
	          db_str.append("<span>");  
	      	  db_str.append("<input type='radio' name='func' value='");
	          db_str.append(this.frowset.getString("pre"));
	          db_str.append("' id='input");
	          db_str.append(this.frowset.getString("dbid"));
	          db_str.append("','input");
	          db_str.append(this.frowset.getString("dbid"));
	          db_str.append("'");
	          RecordVo vo= ConstantParamter.getConstantVo("ZP_DBNAME");
	          if(vo != null){
	             String dbpre=vo.getString("str_value");
	             if(dbpre.equals(this.frowset.getString("pre")))
	             {
	                 db_str.append(" checked");
	             }
	          }
	          db_str.append(">");
	          db_str.append(this.frowset.getString("dbname"));
	          db_str.append("</span>");
	          db_str.append("</div>");
	      }
	      return db_str.toString();	      
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }
    
    /**
     * 增加表头
     * @return
     */
    private String addTableHeader(String label,String fieldsetid)
    {
        StringBuffer str_header=new StringBuffer();
        str_header.append("<table width='90%'  align='center' border='0' cellpadding='0' cellspacing='0' class='ListTable'>");
        str_header.append("<tr>");  
        str_header.append("<td align='center' class='TableRow' width='40%' nowrap>");
        str_header.append(ResourceFactory.getProperty(label));
        str_header.append("</td>");
        str_header.append("<td align='center' class='TableRow' width='15%' nowrap>");
        if(label.equals(GeneralConstant.Field_LABLE))
        	str_header.append("有效指标");
        else
        	str_header.append("&nbsp;&nbsp;&nbsp;&nbsp;<input type='checkbox' name='allcheck' onclick='allSelect(this);'/>&nbsp;&nbsp;&nbsp;&nbsp;");//ResourceFactory.getProperty(GeneralConstant.FIELD_SET)
        str_header.append("</td>");
        if(label.equals(GeneralConstant.Field_LABLE))
        {
	        //前台显示指标
	        str_header.append("<td align='center' class='TableRow' width='15%' nowrap>");
	        str_header.append("前台显示指标");
	        str_header.append("</td>");
	        //必填项
	        str_header.append("<td align='center' class='TableRow' width='15%' nowrap>");
	        str_header.append("必填指标");
	        str_header.append("</td>");
	        if("a01".equalsIgnoreCase(fieldsetid))
	        {
	        	str_header.append("<td align='center' class='TableRow' width='15%' nowrap>");
		        str_header.append("唯一性校验指标");
		        str_header.append("</td>");
	        } else {
	            str_header.append("<td align='center' width='15%' style='border:none;' nowrap>");
                str_header.append("</td>");
	        }
        }else
        {
        	str_header.append("<td align='center' class='TableRow' nowrap>招聘渠道</td>");
        }
        str_header.append("</tr>");        
        return str_header.toString();
    }
    /**
     * 增加表尾
     * @return
     */
    private String addTableFoot()
    {
        StringBuffer str_footer=new StringBuffer();
        str_footer.append("</table>");
        return str_footer.toString();
    }
    
    /**
     * 指标权限
     * @param setid
     * @param setdesc
     * @param flag
     * @return
     */
    private String addTableRow(String setid,String setdesc,HashMap fieldMap,FieldItem item)
    {
    	StringBuffer str_row=new StringBuffer();
        str_row.append("<tr>");
        str_row.append("<td align='right' class='RecordRow' nowrap>");
        str_row.append(setdesc);
        str_row.append("</td>");
    	str_row.append("<td align='center' class='RecordRow' nowrap>");
        str_row.append("<input type='checkbox' name='func");//null_str' value='");
        str_row.append("' value='");
        str_row.append(setid);
        str_row.append("'");
       
        String[] temp=setid.split("\\.");
        if(fieldMap!=null&&fieldMap.get(temp[1].toLowerCase())!=null)
        		str_row.append(" checked");
        
        {/*
          
           int setstrindex = setid.indexOf(".");
           String subsetid = "";
           if(setstrindex != -1){
           	  subsetid = setid.substring(0,setstrindex);
           	  int setindex = fieldlist.indexOf(subsetid);
           	  if(setindex != -1){
           	     String strfieldlist = fieldlist.substring(setindex+4,fieldlist.length());
           	     int fieldindex = strfieldlist.indexOf("},");
           	     if(fieldindex != -1){
           	        String subfieldlist = strfieldlist.substring(0,fieldindex);
         	   	    ArrayList infoFieldList=splitField(subfieldlist);
         	   	    int fieldstrindex = setid.indexOf(".");
         	   	    if(fieldstrindex != -1){
         	   	    	String fieldstr = setid.substring(fieldstrindex+1,setid.length());
         	   	        for(int i=0;i<infoFieldList.size();i++){
          	   	           if(((String)infoFieldList.get(i)).toLowerCase().equals(fieldstr)){
     		                  flag = true;
     	                   }          	   	     
          	   	        }
         	   	        if(flag == true){
  	                       str_row.append(" checked");
                        }
         	   	    } 
           	     }
           	  }
           }      	   	  
        */}
        str_row.append(">");
        str_row.append("</td>");
        
        
        //前台显示指标
        str_row.append("<td align='center' class='RecordRow' nowrap>");
        str_row.append("<input type='checkbox' name='func_show");//null_str' value='");
        str_row.append("' value='");
        str_row.append(setid);
        str_row.append("'");
        String value="";
        if(fieldMap!=null&&fieldMap.get(temp[1].toLowerCase())!=null)
    		value=(String)fieldMap.get(temp[1].toLowerCase());
        if(!"".equals(value))
        {
        	String[] value_arr=value.split("#");
        	if("1".equals(value_arr[0]))
        		str_row.append(" checked");
        }
        str_row.append(">");
        str_row.append("</td>");
        
        //必填项
        str_row.append("<td align='center' class='RecordRow' nowrap>");
        str_row.append("<input type='checkbox' name='func_must");//null_str' value='");
        str_row.append("' value='");
        str_row.append(setid);
        str_row.append("'");
        if(!"".equals(value))
        {
        	String[] value_arr=value.split("#");
        	if("1".equals(value_arr[1]))
        		str_row.append(" checked");
        }
        str_row.append(">");
        str_row.append("</td>");
        /**唯一性校验指标*/
        if("a01".equalsIgnoreCase(item.getFieldsetid()))
        {
        	 str_row.append("<td align='center' class='RecordRow' nowrap>");
             str_row.append("<table ");
             if("a".equalsIgnoreCase(item.getItemtype())&& "0".equals(item.getCodesetid()))
             {
            	 
             }
             else
             {
            	 str_row.append(" style='display:none'");
             }
             str_row.append("><tr><td>");
             str_row.append("<input type='checkbox' name='func_onlys' value='");
             str_row.append(setid.toUpperCase());
             str_row.append("'> </td></tr></table>");
            /* if(!value.equals(""))
             {
             	String[] value_arr=value.split("#");
             	if(value_arr.length==3)
             	{
                	if(value_arr[2].equals("1"))
                  		str_row.append(" checked");
             	}
             }*/
             str_row.append("</td>");
        }
        str_row.append("</tr>");
        return str_row.toString();      
    }
    /**
     * 子集权限
     * @param setid
     * @param setdesc
     * @param flag
     * @return
     * @throws GeneralException 
     */
    private String addSetRow(String setid,String setdesc,HashMap map,int index) throws GeneralException
    {
    	StringBuffer str_row=new StringBuffer();
    	boolean flag = false;
        str_row.append("<tr>");
        str_row.append("<td align='right' class='RecordRow' nowrap>");
        str_row.append(setdesc);
        str_row.append("</td>");
    	str_row.append("<td align='center' class='RecordRow' nowrap>");
        str_row.append("<input type='checkbox' name='func");//null_str' value='");
        str_row.append("' value='");
        str_row.append(setid);
        str_row.append("'");
        RecordVo vo= ConstantParamter.getConstantVo("ZP_SUBSET_LIST");
        HashMap mm=null;
        if(vo != null){
           String setlist=vo.getString("str_value");
          
           ArrayList infoSetList=splitField(setlist);
           for(int i=0;i<infoSetList.size();i++){
        	   String set=(String)infoSetList.get(i);
        	   String id="";
        	   if(set.indexOf("[")!=-1)
        		   id=set.substring(0,set.indexOf("["));
        	   else
        		   id=set;
        	   if(id.equals(setid)){
        		   if(set.indexOf("[")!=-1)
        		   {
        			   mm=this.analyse(set.substring(set.indexOf("[")+1,set.length()-1));
        			   
        		   }
        		   flag = true;
        	   }
           }
           if(flag == true|| "A01".equalsIgnoreCase(setid)){
        	   str_row.append(" checked");
           }
        }
        str_row.append(">");
        str_row.append("</td>");
        Set keySet=map.keySet();
        Iterator t=keySet.iterator();
        str_row.append("<td align='center' class='RecordRow' nowrap>");
        int i=0;
        while(t.hasNext())
        { 
        	String key=(String)t.next();
        	String desc=(String)map.get(key);
        	 str_row.append(desc+"<input type='checkbox' name='func"+index+"'");
        	 str_row.append(" value='"+key+"'");
        	 if("A01".equalsIgnoreCase(setid))
        	 {
        		 str_row.append(" checked disabled");
        	 }
        	 else
        	 {
            	if(mm!=null)
            	{
            		if(mm.get(key)!=null)
        	    	{
        	    		str_row.append(" checked ");
        	    	}
            	}
        	 }
        	str_row.append("/>&nbsp;&nbsp;");
        	i++;
        }
        str_row.append("</td>");
        str_row.append("</tr>");
        return str_row.toString();      
    }
    public HashMap analyse(String tt)
    {
    	HashMap map = new HashMap();
    	String [] ss=tt.split("`");
    	for(int i=0;i<ss.length;i++)
    	{
    		String str=ss[i];
    		String[] arr_str=str.split("#");
    		if(arr_str.length>1){
    			map.put(arr_str[0].toLowerCase(), arr_str[1]);
    		}
    		
    	}
    	return map;
    }
    /**
     * 查询子集权限信息
     * @return
     */
    private String searchTablePriv()throws GeneralException
    {
        StringBuffer table_str=new StringBuffer();
        ArrayList infoSetList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
	    try
	    {
	    	ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
	    	HashMap map  = bo.getHireChannelList();
	    	table_str.append(addTableHeader(GeneralConstant.FIELD_SET,"a"));
	    	for(int i=0;i<infoSetList.size();i++)
   	        {
   	   	      FieldSet fieldset=(FieldSet)infoSetList.get(i); 
   	   	      if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
   	   	          continue;
   	   	      
	          table_str.append(addSetRow(fieldset.getFieldsetid(),fieldset.getCustomdesc(),map,i));
	      }
	      
	      table_str.append(addTableFoot());
	      return table_str.toString();	      
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }
    
    
    //A01{A0101[0#0],A0107[1#0],},A04{C0401[0#1],},
    public HashMap getZpFieldList(String strValue) throws GeneralException
	{
    	HashMap   fieldSetMap=new HashMap();
    	int idx=-1;
		try
		{
				String temp=strValue;
				if(temp!=null&&temp.trim().length()>0)
				{
					String[] temps=temp.split(",},");
					for(int i=0;i<temps.length;i++)
					{			
						String setid=temps[i].substring(0,temps[i].indexOf("{"));
						String fieldstr=temps[i].substring((temps[i].indexOf("{")+1));
						HashMap fieldItemMap=new HashMap();
						String[] fields=fieldstr.split(",");
						for(int n=0;n<fields.length;n++)
						{
							//fielditemList.add(fields[n]);
							/**考虑兼容性，以后定义过参数*/
							idx=fields[n].indexOf("[");
							if(idx!=-1)
							{
								String a=fields[n].substring(0,fields[n].indexOf("[")).toLowerCase();
								String b=fields[n].substring((fields[n].indexOf("[")+1),fields[n].indexOf("]"));
								fieldItemMap.put(a,b);
							}
							else
							{
								fieldItemMap.put(fields[n],"0");
							}
						}
						fieldSetMap.put(setid.toLowerCase(),fieldItemMap);
					}	
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return fieldSetMap;
	}
    
    /**
     * 生成指标授权界面
     * @param fieldsetid
     * @return
     * @throws GeneralException
     */
    private String addFieldDomain(String fieldsetid,HashMap fieldMap)throws GeneralException
    {
        StringBuffer domain_str=new StringBuffer();
        ArrayList infofieldlist=DataDictionary.getFieldList(fieldsetid,Constant.EMPLOY_FIELD_SET);
	    try
	    {
	      String sql="select * from fielditem where fieldsetid='"+fieldsetid+"' order by displayid";
	      ContentDAO dao=new ContentDAO(this.frameconn);
	      ResultSet rs =null;
	      rs=dao.search(sql);
	      HashMap map=new HashMap();
	      ArrayList infofieldlist_order=new ArrayList();
	      for(int i=0;i<infofieldlist.size();i++)
	      {
	      	  FieldItem fielditem=(FieldItem)infofieldlist.get(i);
	      	  map.put(fielditem.getItemid().toLowerCase(), fielditem);
	      }	  
	      while(rs.next()){
	    	  String itemid=rs.getString("itemid").toLowerCase();
	    	  if(map.get(itemid)!=null){
	    		  infofieldlist_order.add(map.get(itemid));//按指标设置顺序存放
	    	  }
	      }
	    	
	      domain_str.append("<div style='display:none' id='");
	      domain_str.append("div");
	      domain_str.append(fieldsetid+"_1");
	      domain_str.append("'>");
	      domain_str.append(addTableHeader(GeneralConstant.Field_LABLE,fieldsetid));    

	      for(int i=0;i<infofieldlist_order.size();i++)
	      {
	      	  FieldItem fielditem=(FieldItem)infofieldlist_order.get(i);
	      	  /**
	      	   * 未解决bug 0025115 因为在前台即使设置上也不显示*/
	      	  if("b0110".equalsIgnoreCase(fielditem.getItemid())|| "e0122".equalsIgnoreCase(fielditem.getItemid())|| "e01A1".equalsIgnoreCase(fielditem.getItemid())){
	      		  continue;
	      	  }
	      	 /***/
	          domain_str.append(addTableRow(fieldsetid+"."+fielditem.getItemid(),fielditem.getItemdesc(),fieldMap,fielditem));
	      }
	      
	      domain_str.append(addTableFoot());
	      domain_str.append("</div>");
	      
	      return domain_str.toString();	      
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }
    /**
     * 查询指标权限
     * @return
     */
    private String searchFieldPriv()throws GeneralException
    {
        StringBuffer field_str=new StringBuffer();
        RecordVo vo= ConstantParamter.getConstantVo("ZP_SUBSET_LIST");
        String setlist="";
        if(vo!=null) {
        	setlist=vo.getString("str_value");
        	setlist = com.hjsj.hrms.utils.PubFunc.hireKeyWord_filter_reback(setlist);
        }
        ArrayList infoSetList=splitField(setlist);
  
        RecordVo vo2= ConstantParamter.getConstantVo("ZP_FIELD_LIST");
        String fieldStr="";
        if(vo2!=null)
        	fieldStr=vo2.getString("str_value");
        HashMap fieldsetMap=getZpFieldList(fieldStr);
        
        for(int i=0;i<infoSetList.size();i++){
           StringBuffer strsql=new StringBuffer();
           String setid=(String)infoSetList.get(i);
           if(setid.indexOf("[")!=-1)
        	   setid=setid.substring(0,3);
           strsql.append("select fieldsetid,customdesc from fieldset where fieldsetid = '"+setid+"'");
           ContentDAO dao=new ContentDAO(this.getFrameconn());
	       try
	       {
	         this.frowset = dao.search(strsql.toString());
	         while(this.frowset.next())
	         {
	             field_str.append("<div id='");
	             field_str.append("div");
	             field_str.append(this.frowset.getString("fieldsetid"));
	             field_str.append("' style='padding:3 0 3 3'>");
	          
	             field_str.append("<span style='cursor:hand' title='单击展开子集' onclick=show('");
	             field_str.append("div");
	             field_str.append(this.frowset.getString("fieldsetid"));
	             field_str.append("')>");
	             field_str.append("<img src='/images/table.gif' border=0 align='absmiddle' style='margin-right:5px;'>");
	             field_str.append(this.frowset.getString("customdesc"));
	             field_str.append("</span>");
	             HashMap fieldMap=(HashMap)fieldsetMap.get(this.frowset.getString("fieldsetid").toLowerCase());
	             field_str.append(addFieldDomain(this.frowset.getString("fieldsetid"),fieldMap));
	          
	             field_str.append("</div>");
	         }
	       }catch(SQLException sqle)
		    {
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
		    }
        }
	    return field_str.toString();	        
    }
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");         
        String	tab_name=(String)hm.get("a_tab");
        if(tab_name==null|| "".equals(tab_name))
            return;
        String str="";
        String str_only="";
        try
        {

	        /**
	         * 人员库授权
	         */
	
	        if("dbpriv".equals(tab_name))
	        {	     
	            str=searchDbNameHtml();
	        }
	       
	        /**
	         * 子集授权
	         */
	        if("tablepriv".equals(tab_name))
	        {        	
	            str=searchTablePriv();
	        }
	        /**
	         * 指标授权
	         */
	        if("fieldpriv".equals(tab_name))
	        {	        	
	           str=searchFieldPriv();
	           str_only=getFunc_only();
	        }
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ee);
        }
        /**
         * save the role_id.
         */
        this.getFormHM().put("func_only",str_only);
        this.getFormHM().put("script_str",str);         
        this.getFormHM().put("tab_name",tab_name);
    }
    public String getFunc_only()
    {
    	String str="";
    	try
    	{
    		 RecordVo vo= ConstantParamter.getConstantVo("ZP_ONLY_FIELD");
    	     if(vo!=null)
    	    	 str=vo.getString("str_value").toUpperCase();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return str;
    }

}
