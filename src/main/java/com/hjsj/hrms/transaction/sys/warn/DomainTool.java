package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * 预警对象：角色、组织机构中文名称转换工具
 */
public class DomainTool implements IConstant {
	
	private  static HashMap mapOrg = null; //组织机构集合
	private  static HashMap mapRole = null;//角色集合
	
   /**
    * 转换csv的预警对象编号为中文显示
    * @param strXMLDomain
    * @return
    */
    public String getDomainNames(String strXMLDomain){
    	if(strXMLDomain == null || strXMLDomain.trim().length() < 1){
    		return ResourceFactory.getProperty("label.sys.warn.domain.default");
    	}
    	
    	StringTokenizer st = new StringTokenizer(strXMLDomain,",");
    	StringBuffer sbRet = new StringBuffer();
    	Map tempMap = null;
    	String strXMLvalue;
    	//TransTool tools = new TransTool( this );
    	while( st.hasMoreElements()){
    		strXMLvalue = st.nextToken();
    		if( "UN".equals(strXMLvalue)){
    			sbRet.append( "所有组织" );     
    			sbRet.append(",");
    			continue;
        	}
    		if(strXMLvalue.startsWith("RL")){//角色标识列表
    			tempMap = getRoleMap();//map中存放角色列表
    			String desc = (String)tempMap.get( strXMLvalue );
            	if(desc==null)
            		continue;
            	sbRet.append(desc);
        		sbRet.append(",");
    		}else{
//    			tempMap = getOrgMap();//单位/职位/部门/人员/信息
    			String desc = getOrgAndPersonName(strXMLvalue);//单位/职位/部门/人员/信息
    			if(desc == null || desc.trim().length()==0)
    				continue;
    			sbRet.append(desc);
    			sbRet.append(",");
    		}
    	}
    	if( sbRet.length()>0 ){//删除最后一个逗号
    		sbRet.deleteCharAt( sbRet.length()-1);
    	}
    	if(sbRet==null||sbRet.length()<=0|| "null".equals(sbRet.toString()))
    		return "";
    	return sbRet.toString();    	
    }
    /**
     * 得到定义人员库汉字描述
     * @param nbases
     * @param dao
     * @return
     */
    public String getNbases(String nbases,ContentDAO dao)
    {
        if(nbases==null||nbases.length()<=0)
        	return "";
        StringBuffer  name=new StringBuffer();;
        StringBuffer buf=new StringBuffer();
        buf.append("(");
        String array[]=nbases.split(",");
        for(int i=0;i<array.length;i++)
        {
        	buf.append(" Upper(pre)='"+array[i].toString().toUpperCase()+"'");
        	if(i!=array.length-1)
        		buf.append(" or ");
        }
        buf.append(")");
        StringBuffer sql=new StringBuffer();
        sql.append("select dbname,pre from dbname where 1=1  ");
        if(buf!=null&&buf.toString().length()>0)
            sql.append("and "+buf.toString()); 
        try
        {
       	 RowSet rs=dao.search(sql.toString());       	
       	 while(rs.next())
       	 {
       		
       		name.append(rs.getString("dbname")+",");
       		 
       	 }
        }catch(Exception e)
        {
       	 e.printStackTrace();
        }
        String personDbName="";
        if(name.toString().length()>0)
        	personDbName=name.toString().substring(0, name.toString().length()-1);
        return personDbName;
    }
    /**
     * 模版的汉字描述
     * @param tabids
     * @param dao
     * @return
     */
    public String getTemplate(String tabids,ContentDAO dao)
    {
    	if(tabids==null||tabids.length()<=0)
    		return "";
    	StringBuffer name=new StringBuffer();
    	StringBuffer buf=new StringBuffer();
        buf.append("(");
        String array[]=tabids.split(",");
        for(int i=0;i<array.length;i++)
        {
        	buf.append(" tabid='"+array[i]+"'");
        	if(i!=array.length-1)
        		buf.append(" or ");
        }
        buf.append(")");
        StringBuffer sql=new StringBuffer();
        sql.append("select tabid,name from template_table where 1=1  ");
        if(buf!=null&&buf.toString().length()>0)
            sql.append("and "+buf.toString());    
        
        try
        {
       	 RowSet rs=dao.search(sql.toString());       	
       	 while(rs.next())
       	 {
       		
       		name.append(rs.getString("tabid")+":"+rs.getString("name")+"\r\n");
       		 
       	 }
        }catch(Exception e)
        {
       	 e.printStackTrace();
        }
    	return name.toString();
    	
    }
    /**
	 *获取机构名称和人员姓名   wangb 20180518
	 * @return
	 */
    private String getOrgAndPersonName(String value){
    	String strName = "";
    	StringBuffer sbOrgSql = new StringBuffer();
    	if( mapOrg == null ) 
        	mapOrg = new HashMap();
    	if(mapOrg.containsKey(value.toUpperCase()))// mapOrg 中存在该机构
    		return (String)mapOrg.get(value.toUpperCase());
    	Connection conn = null;
		Statement stmt = null;
		ResultSet rs=null;
		ArrayList list =new ArrayList();
		if(value.toUpperCase().startsWith("UN") || value.toUpperCase().startsWith("UM") || value.toUpperCase().startsWith("@K")){
			String codeitemid = value.substring(2);
			sbOrgSql.append("select codeitemdesc from organization where codeitemid=?");
			list.add(codeitemid);
			value = value.toUpperCase();
		}else{
			String codesetid = value.substring(0, 3).toUpperCase();
			String codeitemid = value.substring(3);
			RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
	        	String A01="";
	        if(login_vo!=null) 
	        	A01 = login_vo.getString("str_value").toUpperCase();
	        if(A01.indexOf(codesetid) ==-1)
	        	return "";
			sbOrgSql.append("select A0101 as codeitemdesc from "+ codesetid +"A01 where A0100=?");
			list.add(codeitemid);
			value = value.substring(0,1).toUpperCase()+value.substring(1).toLowerCase();
		}
    	try {
			conn=AdminDb.getConnection();//getBussTrans().getFrameconn();
			ContentDAO db=new ContentDAO(conn);
			rs =db.search(sbOrgSql.toString(),list);
			while(rs.next()){
				strName = rs.getString("codeitemdesc");
				mapOrg.put( value, strName );
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(stmt);
			PubFunc.closeDbObj(conn);
		}	
    	return strName;
    }
    
	/**
	 * 获得单位/职位/部门及用户权限范围内的人员库的人员 信息总和(codesetid,codeitemid,codeitemdesc)
	 * @return
	 */
	public Map getOrgMap(){
        if( mapOrg == null ){
        	mapOrg = new HashMap();
        	
		    Connection conn = null;
			Statement stmt = null;
			ResultSet rs=null;
			StringBuffer sbOrgSql = new StringBuffer("select codesetid,codeitemid,codeitemdesc from organization");
			
			// 因为机构组织树可能列出人员，所以添加人员显示。sql原形：
			//select codesetid,codeitemid,codeitemdesc from organization
			//union select 'Usr' as codesetid,a0100 as codeitemid,a0101 as codeitemdesc from usra01
			//union select 'Ret' as codesetid,a0100 as codeitemid,a0101 as codeitemdesc from reta01
			
			//ArrayList alPre = DataDictionary.getDbpreList();			 
			try{
				conn=AdminDb.getConnection();//getBussTrans().getFrameconn();
				ContentDAO db=new ContentDAO(conn);
				RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
		         String A01="";
		         if(login_vo!=null) 
		           A01 = login_vo.getString("str_value").toLowerCase();
		         ArrayList alPre =new ArrayList();
		        if(A01!=null&&A01.length()>0){
		        	alPre=getNbaseCommonList(A01,db);
		        } 
				for(int i=0; i< alPre.size(); i++){
					CommonData da=(CommonData)alPre.get(i);
					sbOrgSql.append(" union select '");
					sbOrgSql.append(da.getDataValue());
					sbOrgSql.append("' as codesetid,a0100 as codeitemid,a0101 as codeitemdesc from ");
					sbOrgSql.append(da.getDataValue());
					sbOrgSql.append( "a01 ");
				}				
				rs =db.search(sbOrgSql.toString());	
				String strPre = "";
				String strId = "";
				String strName = "";
				while(rs.next()){
					strPre = rs.getString("codesetid");
					strId = rs.getString("codeitemid");
					strName = rs.getString("codeitemdesc");
					mapOrg.put( strPre+strId, strName );
				}
			}catch (Exception sqle){
				sqle.printStackTrace();
			}		
			finally{
				closeAll(rs,stmt,conn);
			}
        }
		return mapOrg;
    }
    
	/**
	 * 系统角色集合
	 * @return
	 */
    public Map getRoleMap(){
        // 开始处理预警对象的中文名称
        if( mapRole == null ){
        	mapRole = new HashMap();
        	
		    Connection conn = null;
			Statement stmt = null;
			ResultSet rs=null;
			String strOrgSql = "select role_id,role_name from t_sys_role";
			try{
				conn=AdminDb.getConnection();//getBussTrans().getFrameconn();
				ContentDAO db=new ContentDAO(conn);
				rs =db.search(strOrgSql);	
				String strPre = "RL";
				String strId = "";
				String strName = "";
				while(rs.next()){
//					strPre = rs.getString("codesetid");
					strId = rs.getString("role_id");
					strName = rs.getString("role_name");
					mapRole.put( strPre+strId, strName );
				}
			}catch (Exception sqle){
				sqle.printStackTrace();
			}		
			finally{
				closeAll(rs,stmt,conn);
			}
        }
		return mapRole;
    }


    
    /**
     * 关闭链接
     * @param rs
     * @param stmt
     * @param conn
     */
    private static void closeAll(ResultSet rs, Statement stmt, Connection conn){
//		if (rs != null) {
//			try {
//				rs.close();
//			} catch (SQLException e) {
//				;
//			}
//			rs = null;
//		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				;
			}
			stmt = null;
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				;
			}
			conn = null;
		}		
	}
	
    /**
     * 得到定义人员库汉字描述
     * @param nbases
     * @param dao
     * @return
     */
    public ArrayList getNbaseCommonList(String nbases,ContentDAO dao)
    {
        ArrayList list=new ArrayList();
    	if(nbases==null||nbases.length()<=0)
        	return list;
        
        StringBuffer buf=new StringBuffer();
        buf.append("(");
        String array[]=nbases.split(",");
        for(int i=0;i<array.length;i++)
        {
        	buf.append(" Upper(pre)='"+array[i].toString().toUpperCase()+"'");
        	if(i!=array.length-1)
        		buf.append(" or ");
        }
        buf.append(")");
        StringBuffer sql=new StringBuffer();
        sql.append("select dbname,pre from dbname where 1=1  ");
        if(buf!=null&&buf.toString().length()>0)
            sql.append("and "+buf.toString()); 
        try
        {
         CommonData da=new CommonData();	
       	 RowSet rs=dao.search(sql.toString());       	
       	 while(rs.next())
       	 {
       		da=new CommonData();
       		da.setDataName(rs.getString("dbname"));
       		da.setDataValue(rs.getString("pre"));
       		list.add(da);
       	 }
        }catch(Exception e)
        {
       	 e.printStackTrace();
        }        
        return list;
    }
    
    /**
     * 得到定义人员库汉字描述
     * @param nbases
     * @param dao
     * @return
     */
    public ArrayList getNbaseList(String nbases,ContentDAO dao)
    {
        ArrayList list=new ArrayList();
    	if(nbases==null||nbases.length()<=0)
        	return list;
        
        StringBuffer buf=new StringBuffer();
        buf.append("(");
        String array[]=nbases.split(",");
        for(int i=0;i<array.length;i++)
        {
        	buf.append(" Upper(pre)='"+array[i].toString().toUpperCase()+"'");
        	if(i!=array.length-1)
        		buf.append(" or ");
        }
        buf.append(")");
        StringBuffer sql=new StringBuffer();
        sql.append("select dbname,pre from dbname where 1=1  ");
        if(buf!=null&&buf.toString().length()>0)
            sql.append("and "+buf.toString()); 
        try
        {
         RowSet rs=dao.search(sql.toString());       	
       	 while(rs.next())
       	 {
       		list.add(rs.getString("pre"));
       	 }
        }catch(Exception e)
        {
       	 e.printStackTrace();
        }        
        return list;
    }
    /**
     * 得到选定的自己权限范围内的nbaselist，返回的list包含CommonData
     * @param list
     * @param nbases
     * @param dao
     * @return
     */
    public ArrayList getKqNbaseCommonList(ArrayList list,String nbases,ContentDAO dao)
	{
	     ArrayList kq_list=new ArrayList();
       if(list==null||list.size()<=0)
       	return kq_list;
       if(nbases==null||nbases.length()<=0)
    	   return kq_list;
       StringBuffer buf=new StringBuffer();
       buf.append("(");
       for(int i=0;i<list.size();i++)
       {
       	buf.append(" Upper(pre)='"+list.get(i).toString().toUpperCase()+"'");
       	if(i!=list.size()-1)
       		buf.append(" or ");
       }
       buf.append(")");
       StringBuffer sql=new StringBuffer();
       sql.append("select dbname,pre from dbname where 1=1 and ");
       if(buf!=null&&buf.toString().length()>0)
           sql.append(buf.toString());   
       try
       {
      	 RowSet rs=dao.search(sql.toString());
      	 CommonData da=new CommonData();   
      	 while(rs.next())
      	 {
      		 da=new CommonData();
      		 String dbpre=rs.getString("pre").toLowerCase();
			 if((nbases.toLowerCase()).indexOf(dbpre)!=-1)
			 {
				 da.setDataName(rs.getString("dbname"));
	      		 da.setDataValue(rs.getString("pre"));
	      		 kq_list.add(da);
			 }
	         else
	            	continue;
      		
      	 }
       }catch(Exception e)
       {
      	 e.printStackTrace();
       }      
       return kq_list;
   }
    /**
     * 得到选定的自己权限范围内的nbaselist
     * @param list
     * @param nbases
     * @param dao
     * @return
     */
    public ArrayList getKqNbaseList(ArrayList list,String nbases,ContentDAO dao)
	{
	   ArrayList kq_list=new ArrayList();
       if(list==null||list.size()<=0)
       	return kq_list;
       if(nbases==null||nbases.length()<=0)
    	   return kq_list;
       StringBuffer buf=new StringBuffer();
       buf.append("(");
       for(int i=0;i<list.size();i++)
       {
       	buf.append(" Upper(pre)='"+list.get(i).toString().toUpperCase()+"'");
       	if(i!=list.size()-1)
       		buf.append(" or ");
       }
       buf.append(")");
       StringBuffer sql=new StringBuffer();
       sql.append("select dbname,pre from dbname where 1=1 and ");
       if(buf!=null&&buf.toString().length()>0)
           sql.append(buf.toString());   
       try
       {
      	 RowSet rs=dao.search(sql.toString());
      	 while(rs.next())
      	 {
      		 
      		 String dbpre=rs.getString("pre").toLowerCase();
			 if((nbases.toLowerCase()).indexOf(dbpre)!=-1)
			 {
	      		 kq_list.add(rs.getString("pre"));
			 }
	         else
	            	continue;
      		
      	 }
       }catch(Exception e)
       {
      	 e.printStackTrace();
       }      
       return kq_list;
   } 
   
}
