package com.hjsj.hrms.module.recruitment.baseoptions.transaction;


import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
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
    @Override
    public void execute() throws GeneralException {
        String tab_name=(String)this.getFormHM().get("tab_name");
        ArrayList list = null;
        if("tablepriv".equalsIgnoreCase(tab_name)){
            list =(ArrayList)this.getFormHM().get("nbasestore");
        }
        String fields =(String)this.getFormHM().get("fields");
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
	            saveTablePriv(list,fields);
	        }
	        /**
	         * 保存指标
	         */
	        if("fieldpriv".equals(tab_name))
	        {
	            saveFieldPriv();
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
    
    /**
     * 保存采集指标
     *2007-4-14  dengcan
     */
    public void saveFieldPriv() {
        ArrayList list = new ArrayList();
        String store = (String)this.getFormHM().get("fieldStore");
        JSONObject jsonStore = JSONObject.fromObject(store);
        JSONArray  arrayStore =  (JSONArray) jsonStore.get("children");
        ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
        ArrayList<String> fieldlist = bo.getFieldList();
        Map<String,String> map = bo.getHireChannelList();
        HashMap fieldSetMap = new HashMap();
        String fieldSetJson = null;
        String emailField = ConstantParamter.getEmailField();
        try {
            if (arrayStore != null){
                for (Entry < String, String > entry: map.entrySet()) {
                    HashMap columnMap = new HashMap();
                    String textkey = entry.getKey();
                    for (int i = 0; i < arrayStore.size(); i++) {
                        JSONObject arrayJson = (JSONObject) arrayStore.get(i);
                        String ID = (String) arrayJson.get("id");
                        JSONArray childrenArray = (JSONArray) arrayJson.get("children");
                        ArrayList fieldList = new ArrayList();
                        for (int j = 0; j <childrenArray.size(); j++) {
                            HashMap fieldMap = new HashMap();
                            JSONObject childrenJson = (JSONObject) childrenArray.get(j);
                            String id = (String) childrenJson.get("id");
                            String name = (String) childrenJson.get("name");
                            String displayname = (String) childrenJson.get("displayname");
                            if(name.equalsIgnoreCase(displayname))
                                displayname = "";
                            
                            fieldMap.put("id", id);
                            fieldMap.put("name", displayname);
                            
                            if(id.equalsIgnoreCase(emailField)){
                            	fieldMap.put("in_list", "1");
								fieldMap.put("must", "1");
								fieldList.add(fieldMap);
								continue;
                            }
    
                            if (childrenJson.containsKey("a" + textkey)) {
                                String keyJson = (String) childrenJson.get("a" + textkey);
                                if ("11".equals(keyJson) || "5".equals(keyJson)) {
									if("A01".equalsIgnoreCase(ID)){
										fieldMap.put("in_list", "0");
										fieldMap.put("must", "1");
									}else{
										fieldMap.put("in_list", "1");
										fieldMap.put("must", "1");
									}
                                } else if ("3".equals(keyJson) || "6".equals(keyJson)) {
                                    fieldMap.put("in_list", "1");
                                    fieldMap.put("must", "0");
                                } else if ("2".equals(keyJson) || "4".equals(keyJson) || "10".equals(keyJson)) {
                                    fieldMap.put("in_list", "0");
                                    fieldMap.put("must", "1");
                                } else if ("9".equals(keyJson) || "8".equals(keyJson) || "7".equals(keyJson)) {
                                    fieldMap.put("in_list", "0");
                                    fieldMap.put("must", "0");
                                } else 
                                    continue;
                            }
                            fieldList.add(fieldMap);
                        }
    
                        if(fieldList.size() == 0) 
                            continue;
    
                        columnMap.put(ID, fieldList);
                    }
                    fieldSetMap.put(textkey, columnMap);
                }
            }
            fieldSetJson = JSON.toString(fieldSetMap);
            ContentDAO dao = new ContentDAO(this.frameconn);
        
			String sqlsql = "";
			sqlsql = "select Str_Value,Constant from constant where  constant='ZP_FIELD_LIST_JSON'";
			RowSet rs = dao.search(sqlsql);
			if (rs.next()) {
				list.add(fieldSetJson);
				if (1 == Sql_switcher.searchDbServer())
					sqlsql = "update constant set str_value = ? where constant = 'ZP_FIELD_LIST_JSON'";
				else if (2 == Sql_switcher.searchDbServer())
					sqlsql = "declare  content clob; begin  content :=?;  update constant set str_value = content  where constant = 'ZP_FIELD_LIST_JSON'; end;";

				dao.update(sqlsql, list);
			} else {
				list.add(fieldSetJson);
				if (1 == Sql_switcher.searchDbServer())
					sqlsql = "INSERT INTO constant (str_value , constant ) VALUES (?, 'ZP_FIELD_LIST_JSON')";
				else if (2 == Sql_switcher.searchDbServer())
					sqlsql = "declare  content clob; begin  content :=?;   INSERT INTO constant (str_value , constant ) VALUES (content, 'ZP_FIELD_LIST_JSON'); end;";

				dao.update(sqlsql, list);
			}
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        RecordVo avo=new RecordVo("constant");
        avo.setString("constant","ZP_FIELD_LIST_JSON");
        avo.setString("describe","招聘子集字段列表");
        avo.setString("str_value",fieldSetJson);
        ConstantParamter.putConstantVo(avo,"ZP_FIELD_LIST_JSON");
        
        /*RecordVo avo=new RecordVo("constant");
        avo.setString("constant","ZP_FIELD_LIST");
        avo.setString("describe","招聘子集字段列表");
        avo.setString("str_value",fielditemstr.toString());
        ConstantParamter.putConstantVo(avo,"ZP_FIELD_LIST");*/
        
    }
    
    
    
    
    /**
     * 保存采集指标
     * 2006-1-4 wang 修改
     */
    @Deprecated
	 /* private void saveFieldPriv()
	  {
	    String field_str=(String)this.getFormHM().get("field_set_str");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		List list=new ArrayList();
		RecordVo vo= ConstantParamter.getConstantVo("ZP_FIELD_LIST");
		String fieldlist=vo.getString("str_value");
		RecordVo rv= ConstantParamter.getConstantVo("ZP_SUBSET_LIST");
		String setliststr = "";
		if(rv != null && !rv.equals("")){
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
	    	if(field_str != null && !field_str.equals("")){
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
	}*/
    /**
     * 保存子集
     */
    private void saveTablePriv(ArrayList storelist,String fields)
    {
    	Map<String, Map<String, String>> linkMap = new LinkedHashMap<String,Map<String, String>>();
    	Map<String, String> sonMap = null;
    	String[] fieldarray =fields.replaceAll("'", "").split(",");
    	for(int i =0;i<storelist.size();i++){
    		sonMap = new LinkedHashMap<String, String>();
    		MorphDynaBean bean = (MorphDynaBean)storelist.get(i);
    		String itemid =""; 
    		for(int j=0;j<fieldarray.length;j++){
    			String field = fieldarray[j];
    			String value = (String)bean.get(field);
    			if(0==j){
    				itemid = value.split("'")[1];
    					continue;
    			}
    			if("a".equals((field.charAt(0)+"")))
    				field = field.substring(1);
    			sonMap.put(field, value);
    		}
    		linkMap.put(itemid, sonMap);
    	}
    	String jsonvalue = JSON.toString(linkMap);
  /*  	ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
    	ArrayList<String> fieldlist = bo.getFieldList();
    	Map<String, Map<String, String>> sortmap = bo.sortConstantMap(fieldlist, linkMap);*/
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
        List list = new ArrayList();
    	StringBuffer func_str=new StringBuffer("");   	
        StringBuffer strsql=new StringBuffer();  
        String func=(String)this.getFormHM().get("field_set_str");
        func = PubFunc.hireKeyWord_filter_reback(func);
        
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
           }
           
           list.clear();
           list.add(jsonvalue);
		   String sql = "insert into constant(constant,type,str_value,Describe) values('ZP_SUBSET_LIST','',?,'子集')";
		   dao.insert(sql,list); //添加纪录在常量表中
		   list.clear();
		   
		    RecordVo avo=new RecordVo("constant");
	   	 	avo.setString("constant","ZP_SUBSET_LIST");
	   	 	avo.setString("describe","子集");
	   	 	avo.setString("str_value",jsonvalue);
	   	 	ConstantParamter.putConstantVo(avo,"ZP_SUBSET_LIST");
		   
		   
		   
		   RecordVo vo= ConstantParamter.getConstantVo("ZP_FIELD_LIST");
	       if(vo == null){
	          String sqlsql = "insert into constant(constant,type,str_value,Describe) values('ZP_FIELD_LIST','','','采集指标')";
	          dao.insert(sqlsql,list);
	          
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
				 //避免在子集信息社会和校园都已取消选中（或初始社会未选校园取消选中；或初始社会选中校园未选，取消社会选中），但前台显示指标仍存在情况
				 if(func_str.toString().contains(setvalue+"[-1#1`-1#1]")
						 ||func_str.toString().contains(setvalue+"[01#1`-1#1`]")
						 ||func_str.toString().contains(setvalue+"[01#2`-1#1`]")
					 ||func_str.toString().contains(setvalue+"[-1#1`]"))
					 continue;
				 
				 if(StringUtils.isNotEmpty(setvalue) && setvalue.indexOf("#") > -1)
				     setvalue = setvalue.substring(0, setvalue.indexOf("#"));
				 
				//判断子集名称下标时加上"{"，防止指标中含有子集名称;例如子集AAA{AAAAC[0#0],},AAC{AACAA[0#0],},取AAC下标时就会取到错误的位置
				 int setindex = fieldlist.indexOf(setvalue + "{");
				 if(setindex != -1){
					String substr = fieldlist.substring(setindex,fieldlist.length());
					int subindex = substr.indexOf("},");
					subfieldList.append(fieldlist.substring(setindex,setindex+subindex+2));
				}
			  }
			 /* String sssql = "update constant set str_value = '"+subfieldList.toString()+"' where constant = 'ZP_FIELD_LIST'";
			  dao.update(sssql,list);
			  
			    RecordVo avo2=new RecordVo("constant");
	    	 	avo2.setString("constant","ZP_FIELD_LIST");
	    	 	avo2.setString("describe","招聘子集字段列表");
	    	 	avo2.setString("str_value",subfieldList.toString());
	    	 	ConstantParamter.putConstantVo(avo2,"ZP_FIELD_LIST");*/
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
