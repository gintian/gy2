/*
 * Created on 2005-5-31
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.structuresql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.RowSet;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

/**
 * @author Administrator
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StructureExecSqlString{
	//private String userName; //用户名--A01子集
	//private String password; //密码--A01子集
	public String redundantInfo="";//超编信息（只记录批量批准时，强制时超编后不入库信息提示到前台）
	public DbNameBo db;

	
	//insertType表示是人员、单位、部门、职位
	//tableName表示表名
	//fields表示字段的名称
	//fieldValues表示所对应的字段的值
	private String []fieldcode;//表示所对应的字段的值
	public  String InfoInsert(String insertType,String tableName,String fields, String fieldValues,String userid,String createUserName,Connection conn) throws GeneralException {
		String id="";
		try {
			if(tableName.length()==3 && "01".equals(tableName.substring(1,3)) || tableName.length()==6 && "01".equals(tableName.substring(4,6)))
			{
				if("2".equals(insertType) || "3".equals(insertType) || "4".equals(insertType))
					maininfodelete(insertType,tableName,userid,conn);
				id=maininfoInsert(insertType,tableName,fields,fieldValues,userid,createUserName,conn);//主集  
			}
			else
			{
				id=detailinfoInsert(insertType,tableName,fields,fieldValues,userid,createUserName,conn);//子集
			}		
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new GeneralException(e.getMessage());
		}
		return id;
	}
	public  String InfoInsertVo(String insertType,String tableName,RecordVo vo,String userid,String createUserName,Connection conn) {
		String id="";
		try {
			if(tableName.length()==3 && "01".equals(tableName.substring(1,3)) || tableName.length()==6 && "01".equals(tableName.substring(4,6)))
			{
				if("2".equals(insertType) || "3".equals(insertType) || "4".equals(insertType))
					maininfodelete(insertType,tableName,userid,conn);
				id=maininfoInsertVo(insertType,tableName,vo,userid,createUserName,conn);//主集  
			}
			else
			{
				id=detailinfoInsertVo(insertType,tableName,vo,userid,createUserName,conn);//子集
			}		
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return id;
	}
	/**
	 * 判断如果为子集新增记录，但是主集中没有记录的，给主集也加上一跳记录
	 * @param setname
	 */
	public void insertZJRecord(String setname,String code,UserView uv,Connection conn)
	{
		try{
			if(setname==null|| "".equals(setname))
				return;
			if("k01".equalsIgnoreCase(setname)|| "b01".equalsIgnoreCase(setname))
				return;
			String c = setname.substring(0,1);
			String tableName="";
			String cloumn="";
			if("k".equalsIgnoreCase(c))
			{
				cloumn="e01a1";
			}
			else if("B".equalsIgnoreCase(c))
			{
				cloumn="b0110";
			}
			tableName=c+"01";
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buf = new StringBuffer();
			buf.append("select * from "+tableName);
			buf.append(" where "+cloumn+"='"+code+"'");
			RowSet rs = dao.search(buf.toString());
			boolean flag = false;
			while(rs.next())
			{
				flag=true;
			}
			if(!flag)
			{
		    	StringBuffer sql = new StringBuffer();
		    	sql.append(" insert into "+tableName+"("+cloumn+",createtime,modtime,createusername,modusername)");
		    	sql.append(" values ");
		    	sql.append("('"+code+"',");
		     	sql.append(PubFunc.DoFormatSystemDate(true)+",");	
		    	sql.append(PubFunc.DoFormatSystemDate(true)+",");		
		    	sql.append("'"+uv.getUserName()+"','"+uv.getUserName()+"')");
		     	dao.insert(sql.toString(), new ArrayList());
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private  void maininfodelete(String insertType,String tableName,String userid,Connection conn)
	{
		StringBuffer strsql=new StringBuffer();
		strsql.append("delete from ");
		strsql.append(tableName);
		strsql.append(" where ");
		if("2".equals(insertType))
			strsql.append(" B0110='");
		else if("3".equals(insertType))
			strsql.append(" B0110='");
		else
			strsql.append(" E01A1='");
		strsql.append(userid);
		strsql.append("'");
		try
		{
		   new ExecuteSQL().execUpdate(strsql.toString(),conn);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private  String detailinfoInsert(String insertType,String tableName,String fields, String fieldValues,String userid,String createUserName,Connection conn) {
		String id = "";
		String personname=createUserName;//this.getUsername(tableName,userid,conn); chenmengqing added 
		StringBuffer strsql=new StringBuffer();
		ArrayList sqlParams = new ArrayList();
		String[] fieldNames = fields.split(",");
		try {
			strsql.append("insert into ");
			strsql.append(tableName);
			strsql.append("(");
            strsql.append(fields);
            if("1".equals(insertType))
			   strsql.append("State,CreateTime,ModTime,CreateUserName,ModUserName,A0100,I9999) values(");
		    else if("2".equals(insertType))
		       strsql.append("State,CreateTime,ModTime,CreateUserName,ModUserName,B0110,I9999) values(");
            else if("3".equals(insertType))
            	strsql.append("State,CreateTime,ModTime,CreateUserName,ModUserName,B0110,I9999) values(");
            else 
            	strsql.append("State,CreateTime,ModTime,CreateUserName,ModUserName,E01A1,I9999) values(");

            // 20200506 zxj jazz59720
            for (int i = 0; i < fieldNames.length; i++) {
            	if (StringUtils.isBlank(fieldNames[i]))
            		continue;

            	strsql.append("?,");
				FieldItem item = DataDictionary.getFieldItem(fieldNames[i]);
				if(item == null)
					continue;

				if(fieldcode[i]!=null && !"null".equals(fieldcode[i]) && !fieldcode[i].equals(fieldNames[i])) {
					if (!"D".equalsIgnoreCase(item.getItemtype())) {
						if ("N".equalsIgnoreCase(item.getItemtype()))
							sqlParams.add(fieldcode[i]);
						else
							sqlParams.add(fieldcode[i].substring(1, fieldcode[i].length() - 1));
					} else {
						if (!fieldcode[i].contains("TO_DATE(")) {
							sqlParams.add(fieldcode[i].substring(1, fieldcode[i].length() - 1));
						} else {
							sqlParams.add(formatDate(fieldcode[i]));
						}
					}
				} else {
					sqlParams.add(null);
				}
			}

            if(createUserName.equalsIgnoreCase(personname)) //?什么意思
				strsql.append("'0',");
			else{
				strsql.append("'3',");
			}
            
			strsql.append("");
		    strsql.append(PubFunc.DoFormatSystemDate(true));				
			strsql.append(",");
			strsql.append(PubFunc.DoFormatSystemDate(true));

			strsql.append(",'");
			strsql.append(createUserName);
			strsql.append("',");
			strsql.append("null");
			strsql.append(",'");
			strsql.append(userid);
			strsql.append("',");
			if("1".equals(insertType))
			{
				id=getUserI9999(tableName,userid,"A0100",conn);
			 	strsql.append(id);
			 }
    	    else if("2".equals(insertType))
    	    {
    	    	id=getUserI9999(tableName,userid,"B0110",conn);
    	    	strsql.append(id);
    	    }
	        else if("3".equals(insertType))
	        {
	        	id=getUserI9999(tableName,userid,"B0110",conn);
	        	strsql.append(id);	
	        }
	        else 
	        {
	        	id=getUserI9999(tableName,userid,"E01A1",conn);
			    strsql.append(id);
	        }
			strsql.append(")");

	        ContentDAO dao = new ContentDAO(conn);
	        dao.insert(strsql.toString(), sqlParams);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return id;
	}
	
	private  String detailinfoInsertVo(String insertType,String tableName,RecordVo vo,String userid,String createUserName,Connection conn) {
		String id = "";
		String personname=createUserName;//this.getUsername(tableName,userid,conn); chenmengqing added 
		try {
            if(createUserName.equalsIgnoreCase(personname)) //?什么意思
            	vo.setString("state", "0");          
			else{
				vo.setString("state", "3");
			}
			vo.setDate("createtime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss"));
			vo.setDate("modtime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss"));
			vo.setString("createusername".toLowerCase(), createUserName);
			vo.setString("modusername".toLowerCase(), null);
			if("1".equals(insertType))
			{
				vo.setString("a0100".toLowerCase(), userid);
				id=getUserI9999(tableName,userid,"A0100",conn);
				vo.setString("i9999".toLowerCase(), id);
			}	   
			else if("2".equals(insertType))
			{
				vo.setString("b0110".toLowerCase(), userid);
				id=getUserI9999(tableName,userid,"B0110",conn);
				vo.setString("i9999".toLowerCase(), id);
			}    
	        else if("3".equals(insertType))
	        {
	        	vo.setString("b0110".toLowerCase(), userid);
	        	id=getUserI9999(tableName,userid,"B0110",conn);
				vo.setString("i9999".toLowerCase(), id);
	        }  	
	        else 
	        {
	        	id=getUserI9999(tableName,userid,"E01A1",conn);
	        	vo.setString("e01a1".toLowerCase(), userid);
				vo.setString("i9999".toLowerCase(), id);
	        }
			ContentDAO dao=new ContentDAO(conn);		
			dao.addValueObject(vo);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return id;
	}
	
	/**
	 * xuj 机构管理子集中添加插入功能
	 * @param insertType
	 * @param tableName
	 * @param vo
	 * @param userid
	 * @param createUserName
	 * @param conn
	 * @return
	 */
	public  String detailinfoForInsertVo(String insertType,String tableName,RecordVo vo,String userid,String createUserName,Connection conn,String i9999) {
		String id = i9999;
		String personname=createUserName;//this.getUsername(tableName,userid,conn); chenmengqing added 
		try {
            if(createUserName.equalsIgnoreCase(personname)) //?什么意思
            	vo.setString("state", "0");          
			else{
				vo.setString("state", "3");
			}
			vo.setDate("createtime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss"));
			vo.setDate("modtime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss"));
			vo.setString("createusername".toLowerCase(), createUserName);
			vo.setString("modusername".toLowerCase(), null);
			if("1".equals(insertType))
			{
				vo.setString("a0100".toLowerCase(), userid);
				//id=getUserI9999(tableName,userid,"A0100",conn);
				vo.setString("i9999".toLowerCase(), id);
			}	   
			else if("2".equals(insertType))
			{
				vo.setString("b0110".toLowerCase(), userid);
				//id=getUserI9999(tableName,userid,"B0110",conn);
				vo.setString("i9999".toLowerCase(), id);
			}    
	        else if("3".equals(insertType))
	        {
	        	vo.setString("b0110".toLowerCase(), userid);
	        	//id=getUserI9999(tableName,userid,"B0110",conn);
				vo.setString("i9999".toLowerCase(), id);
	        }  	
	        else 
	        {
	        	//id=getUserI9999(tableName,userid,"E01A1",conn);
	        	vo.setString("e01a1".toLowerCase(), userid);
				vo.setString("i9999".toLowerCase(), id);
	        }
			ContentDAO dao=new ContentDAO(conn);	
			this.updateInsertI9999(insertType, tableName, i9999, conn, userid);
			dao.addValueObject(vo);
		} catch (Exception e) {

			System.out.println(e.getMessage());
		}
		return id;
	}
	public static void updateInsertI9999(String insertType,String tablename,String i9999,Connection conn,String codeitemid)throws GeneralException{
		if(i9999==null||i9999.length()<=0)
			return;
		if(tablename==null||tablename.length()<1)
			return;
		String primarykey="";
		if("1".equals(insertType))
		{
			primarykey="a0100";
		}	   
		else if("2".equals(insertType))
		{
			primarykey="b0110";
		}    
        else if("3".equals(insertType))
        {
        	primarykey="b0110";
        }  	
        else 
        {
        	primarykey="e01a1";
        }
		ContentDAO dao = new ContentDAO(conn);
		String sql = "update "+tablename+" set i9999=i9999+1 where "+primarykey+""+"='"+codeitemid+"' and i9999>="+i9999;
		try {
			dao.update(sql);
		} catch (SQLException e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			
		}
	}
	private  String maininfoInsert(String insertType,String tableName,String fields, String fieldValues,String userid,String createUserName,Connection conn) throws Exception
	{
		StringBuffer strsql=new StringBuffer();
			
		strsql.append("insert into ");
		strsql.append(tableName);
		strsql.append("(");
		strsql.append(fields);
		strsql.append("CreateTime,ModTime,CreateUserName,ModUserName,");
 		if ("1".equals(insertType))                  //人员
			strsql.append("UserName,UserPassword,A0100,A0000) values(");
		else if ("2".equals(insertType))             //单位
			strsql.append("B0110) values(");
		else if ("3".equals(insertType))             //部门
			strsql.append("B0110) values(");
		else //(insertType.equals("4"))              //职位
			strsql.append("E01A1) values(");
        strsql.append(fieldValues);
        
		strsql.append("");
		strsql.append(PubFunc.DoFormatSystemDate(true));

		strsql.append(",");
		strsql.append(PubFunc.DoFormatSystemDate(true));

		strsql.append(",'");
		strsql.append(createUserName);
		strsql.append("',");
		strsql.append(createUserName);			
		if ("1".equals(insertType))            //人员
		{
		    //userA0000 = getUserA0000(tableName,conn);
			//strsql.append(",'");
			//strsql.append(userName);
			/*strsql.append("','");
			//strsql.append(password);
			strsql.append("','");
			strsql.append(userid);
			strsql.append("',");
			strsql.append(userA0000);
			strsql.append(")");*/
			maininfoInsertA01(tableName,fields,fieldValues,userid,createUserName,conn);	
		}else if ("2".equals(insertType))     //单位
	    {
			strsql.append(",'");
			strsql.append(userid);
			strsql.append("')");
			new ExecuteSQL().execUpdate(strsql.toString(),conn);
		}else if ("3".equals(insertType))     //部门
		{
			strsql.append(",'");
			strsql.append(userid);
			strsql.append("')");
			new ExecuteSQL().execUpdate(strsql.toString(),conn);
		}else //(insertType.equals("4"))      //职位
		{
			strsql.append(",'");
			strsql.append(userid);
			strsql.append("')");
			new ExecuteSQL().execUpdate(strsql.toString(),conn);
		}			
		return userid;
	}
	
	private  String maininfoInsertVo(String insertType,String tableName,RecordVo vo,String userid,String createUserName,Connection conn)
	{
		ContentDAO dao=new ContentDAO(conn);			
		try {
						
			if ("1".equals(insertType))            //人员
			{
    		    maininfoInsertA01Vo(tableName,vo,userid,createUserName,conn);				
			}else if ("2".equals(insertType))     //单位
		    {
				vo.setDate("CreateTime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
				vo.setDate("ModTime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
				vo.setString("CreateUserName".toLowerCase(), createUserName);
				vo.setString("ModUserName".toLowerCase(), null);
				vo.setString("B0110".toLowerCase(), userid);
				dao.addValueObject(vo);				
			}else if ("3".equals(insertType))     //部门
			{
				vo.setDate("CreateTime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
				vo.setDate("ModTime".toLowerCase(),PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
				vo.setString("CreateUserName".toLowerCase(), createUserName);
				vo.setString("ModUserName".toLowerCase(), null);
				vo.setString("B0110".toLowerCase(), userid);
				dao.addValueObject(vo);	
			}else //(insertType.equals("4"))      //职位
			{
				vo.setDate("CreateTime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
				vo.setDate("ModTime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
				vo.setString("CreateUserName".toLowerCase(), createUserName);
				vo.setString("ModUserName".toLowerCase(), null);
				vo.setString("E01A1".toLowerCase(), userid);
				dao.addValueObject(vo);	
			}			
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println(e.getMessage());
		}
		return userid;
	}
	/**
	 * 修改人员主集表
	 * @param tableName
	 * @param fields
	 * @param fieldValues
	 * @param userid
	 * @param createUserName
	 * @param conn
	 * @throws SQLException 
	 */
	private void maininfoInsertA01(String tableName,String fields, String fieldValues,String userid,String createUserName,Connection conn) throws SQLException
	{
		String[] tempstrs=fields.toString().split(",");
		String[] tempValuestrs=fieldValues.toString().split(",");
		String[] values = new String[tempstrs.length];
		// WJH 2013-4-23,  ORACLE 库，日期值是用的 TO_DATE('XXXX-XX-XX', 'YYYY-MM-DD') 等方式，  按 , 分隔时出问题。
		int numTurn = tempValuestrs.length - tempstrs.length;
		if (numTurn != 0) {
			int i = 0;   int j = 0; 
			while (j < tempValuestrs.length) {
				values[i] = tempValuestrs[j];
				if (values[i].indexOf("TO_DATE(") == 0) {
					j++;
					values[i] = values[i] + "," + tempValuestrs[j];
				}
				i++; j++;				
			}
		} else {
			values = tempValuestrs;
		}
		
		// WJH 2013-4-23  修改两人同时改一条记录：  fieldcode 是全部字段的。 不明白此处为什么用 fieldcode， fieldvalues 跟他一样啊。
		// 加个判断个数吧 && values.length==this.fieldcode.length
		if(this.fieldcode!=null && this.fieldcode.length>0 && values.length==this.fieldcode.length)
			tempValuestrs=this.fieldcode;
		StringBuffer strsql=new StringBuffer();
		strsql.append("update ");
		strsql.append(tableName+" set ");
		if(tempstrs!=null&&tempstrs.length>0)
		{
			for(int i=0;i<tempstrs.length;i++)
			{
				if(tempstrs[i]!=null&&tempstrs[i].length()>0)
				  strsql.append(tempstrs[i]+"="+values[i]+",");				
			}
		}
		strsql.append("CreateTime="+PubFunc.DoFormatSystemDate(true)+",");
		strsql.append("ModTime="+PubFunc.DoFormatSystemDate(true)+",");
		strsql.append("CreateUserName='"+createUserName+"',");
		strsql.append("ModUserName='"+createUserName+"',");
		/*strsql.append("UserName='"+userName+"',");
		strsql.append("UserPassword='"+password+"',");*/
		strsql.append("A0000='"+getUserA0000(tableName,conn)+"'");
		strsql.append(" where a0100='"+userid+"'");
			//System.out.println(strsql);
		ContentDAO dao=new ContentDAO(conn);
		dao.update(strsql.toString());
        
	}
	/**
	 * 修改人员主集表
	 * @param tableName
	 * @param fields
	 * @param fieldValues
	 * @param userid
	 * @param createUserName
	 * @param conn
	 */
	private void maininfoInsertA01Vo(String tableName,RecordVo vo,String userid,String createUserName,Connection conn)
	{
		vo.setDate("CreateTime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss"));
		vo.setDate("ModTime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss"));		
		vo.setString("CreateUserName".toLowerCase(), createUserName);
		vo.setString("A0000".toLowerCase(), getUserA0000(tableName,conn));
		vo.setString("a0100".toLowerCase(),userid);
		
		try
		{
			//System.out.println(strsql);
			ContentDAO dao=new ContentDAO(conn);
			dao.updateValueObject(vo);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
        
	}
	//获得I9999的最大顺序号
	public synchronized String getUserI9999(String strTableName,String userid,String fieldtype,Connection conn){
		StringBuffer strsql=new StringBuffer();
		strsql.append("select max(I9999) as I9999 from ");
		strsql.append(strTableName);
		strsql.append(" where ");
		strsql.append(fieldtype);
		strsql.append("='");
		strsql.append(userid);
		strsql.append("'");
		int id=1;
		try
		{
			List rs = ExecuteSQL.executeMyQuery(strsql.toString(),conn);
			if(rs!=null && rs.size()>0)
			{
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				id=Integer.parseInt(String.valueOf(rec.get("i9999")!=null&&rec.get("i9999").toString().length()>0?rec.get("i9999"):"0")) + 1;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		  //new ExecuteSQL().freeConn();
		}
		return String.valueOf(id);
	}
	
	//获得部门的I9999的最大顺序号
	public synchronized String getOrgI9999(String strTableName,String b0110,String fieldtype,Connection conn){
		StringBuffer strsql=new StringBuffer();
		strsql.append("select max(I9999) as I9999 from ");
		strsql.append(strTableName);
		strsql.append(" where ");
		strsql.append(fieldtype);
		strsql.append("='");
		strsql.append(b0110);
		strsql.append("'");
		int id=1;
		try
		{
			List rs = ExecuteSQL.executeMyQuery(strsql.toString(),conn);
			if(rs!=null && rs.size()>0)
			{
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				id=Integer.parseInt(String.valueOf(rec.get("i9999")!=null&&rec.get("i9999").toString().length()>0?rec.get("i9999"):"0")) + 1;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		  //new ExecuteSQL().freeConn();
		}
		return String.valueOf(id);
	}
	//获得主集的A0000的最大排序号
	public synchronized String getUserA0000(String strTableName,Connection conn){
		String strsql = "select max(A0000) as A0000 from " + strTableName;
		int userId=1;
		try
		{
			List rs = ExecuteSQL.executeMyQuery(strsql.toString(),conn);
			if(rs!=null && rs.size()>0)
			{
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				userId=Integer.parseInt(String.valueOf(rec.get("a0000")!=null&&rec.get("a0000").toString().length()>0?rec.get("a0000"):"0")) + 1;
			}		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		  //new ExecuteSQL().freeConn();
		}
		return String.valueOf(userId);
	}
	public  boolean InfoUpdate(String updateType,String tableName,
			String[] fields, String[] fieldValues,String userid,
			String I9999,String username,Connection conn) throws GeneralException {
		boolean flag = false;

		if(tableName.length()==3 && "01".equals(tableName.substring(1,3)) || tableName.length()==6 && "01".equals(tableName.substring(4,6)))
		{
			flag=MainInfoUpdate(updateType,fields,fieldValues,tableName,userid,username,conn);
		}else
		{
			flag=DetailInfoUpdate(updateType,fields,fieldValues,tableName,userid,I9999,username,conn);
		}
		return flag;
	}
	public boolean MainInfoUpdate(String updateType,String[] fields,
			String[] fieldValues,String tableName,String userid,String username,
			Connection conn) throws GeneralException {
		boolean flag = false;
		String personname=username;//  this.getUsername(tableName,userid,conn); //chenmengqing added.
		//StringBuffer strsql=new StringBuffer();
		checkDataExist(updateType,tableName,userid,username,conn);//检查是否存在主集记录，如果不存在添加进去   gdd
		
		//zxj 20170720 更新数据全部改为预处理模式，避免出现单引号等导致sql拼接错误，以及sql注入风险 
		String B0110 = "";
		String E0122 = "";
		String E01A1 = "";
		// 单位和部门有没有变化,默认无变化
		boolean change = false;
		boolean mflag=false;
		RecordVo vo = new RecordVo(tableName);
		for (int i = 0; i < fields.length; i++) {
			if(fields[i]!=null)
			{	
				FieldItem item = DataDictionary.getFieldItem(fields[i]);
				if(item!=null){
					if(fieldValues[i]!=null&&!"null".equals(fieldValues[i])&&!fieldValues[i].equals(fields[i])){//
						mflag=true;
						if(!"D".equalsIgnoreCase(item.getItemtype())) {
						    if("N".equalsIgnoreCase(item.getItemtype()))
						        vo.setString(fields[i].toLowerCase(), fieldValues[i]);
						    else 
						        vo.setString(fields[i].toLowerCase(), fieldValues[i].substring(1,fieldValues[i].length()-1));
						} else {
						    if(!fieldValues[i].contains("TO_DATE(")) {
						        vo.setString(fields[i].toLowerCase(), fieldValues[i].substring(1,fieldValues[i].length()-1));
						    } else {
						        vo.setDate(fields[i].toLowerCase(), formatDate(fieldValues[i]));
						    }
						}
					}else 
					    if(!fieldValues[i].equals(fields[i])){
						mflag=true;
						vo.setString(fields[i].toLowerCase(), null);
					}
				}
				if("1".equals(updateType)){
					if("B0110".equalsIgnoreCase(fields[i])){
						B0110=fieldValues[i];
						B0110=B0110.replaceAll("'","");
						change = true;
					}
					if("E0122".equalsIgnoreCase(fields[i])){
						E0122=fieldValues[i];
						E0122=E0122.replaceAll("'","");
						change = true;
					}
					if("E01A1".equalsIgnoreCase(fields[i])){
						E01A1=fieldValues[i];
						E01A1=E01A1.replaceAll("'","");
					}
				}
			}
		}

        vo.setDate("modtime", new java.util.Date());
        vo.setString("modusername", username);
        
		if ("1".equals(updateType)) //人员
		{
			if(username.equalsIgnoreCase(personname)){
			    vo.setString("state", "0");
			} else {
				vo.setString("state", "3");
			}
			
			vo.setString("a0100", userid);
		} else if ("2".equals(updateType)) //单位
		{
			vo.setString("b0110", userid);
		} else if ("3".equals(updateType)) //部门
		{
			vo.setString("b0110", userid);
		} else //(queryType.equals("4"))   //职位
		{
			vo.setString("e01a1", userid);
		}
		
		try {
			if("1".equals(updateType)){
				 db=new DbNameBo(conn);
				if(!isChangeOrg(tableName,userid,B0110,E0122,conn) && change){
					db.appendMainSetA0100(tableName,B0110,E0122);
				}
				String unitdesc=AdminCode.getCodeName("UN",B0110);
				if("".equals(unitdesc)){
					unitdesc=AdminCode.getCodeName("UM",E0122);
				}
				/*其他地方控制超编 wangrd 2013-09-27 
				if(E01A1!=null&&E01A1.trim().length()>0&&!E01A1.equalsIgnoreCase("null")&&!isChangeOrg(tableName,userid,E01A1,conn)){
					boolean inflag=db.overWorkOut(tableName,E01A1,1);
					unitdesc=unitdesc+">>"+AdminCode.getCodeName("@K",E01A1);
					if(inflag){
						Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
						String mode=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"mode");
						if("warn".equals(mode)){
						}else{
							redundantInfo=unitdesc+"人数超编！\\n";//记录异常超编信息（强制时）
							 throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+"人数超编！","",""));
							}
						}
				}
				*/
				
			}
			if(mflag){
				ContentDAO dao =new ContentDAO(conn);
				dao.updateValueObject(vo);
			}
			flag = true;
		} catch (Exception e) {if(db!=null)
			redundantInfo=db.redundantInfo;//记录异常超编信息（强制时）
		    throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}

	private void checkDataExist(String updateType,String tableName,String userid,String creator,Connection conn){
		String indexid;
		StringBuffer strsql= new StringBuffer("select count(*) counts from ");
		strsql.append(tableName);
		if ("1".equals(updateType)) //人员
		{
			strsql.append(" where A0100='");
			strsql.append(userid);
			strsql.append("'");
			indexid = "A0100";
		} else if ("2".equals(updateType)) //单位
		{
			strsql.append(" where B0110='");
			strsql.append(userid);
			strsql.append("'");	
			indexid = "B0110";
		} else if ("3".equals(updateType)) //部门
		{
			strsql.append(" where B0110='");
			strsql.append(userid);
			strsql.append("'");
			indexid = "B0110";
		} else //(queryType.equals("4"))   //职位
		{
			strsql.append(" where E01A1='");
			strsql.append(userid);
			strsql.append("'");
			indexid = "E01A1";
		}
		
		ResultSet rs = null;
		try{
			ContentDAO dao =new ContentDAO(conn);
			rs = dao.search(strsql.toString());
			int counts=0;
			if(rs.next()){
				counts = rs.getInt("counts");
			}
			
			if(counts<1){
				strsql.delete(0, strsql.length());
			    strsql.append("insert into ");
			    strsql.append(tableName+" ("+indexid+",CreateTime,CreateUserName) values('");
			    strsql.append(userid+"',"+Sql_switcher.dateValue(DateStyle.getSystemTime())+",'"+creator+"')");
			    dao.update(strsql.toString());
			}
			
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			if(rs!= null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}
	
	public boolean isChangeOrg(String tablename,String A0100,String B0110,String E0122,
			Connection conn){
		boolean flag=false;
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select B0110,E0122 from ");
		sqlstr.append(tablename);
		sqlstr.append(" where A0100='");
		sqlstr.append(A0100);
		sqlstr.append("'");
		ContentDAO dao  = new ContentDAO(conn);
		B0110=B0110!=null?B0110:"";
		E0122=E0122!=null?E0122:"";
		String oldB0110="";
		String oldE0122="";		
		try {
			RowSet rs = dao.search(sqlstr.toString());
			while(rs.next()){
				oldB0110 = rs.getString("B0110");
				oldB0110=oldB0110!=null?oldB0110:"";
				oldE0122 = rs.getString("E0122");
				oldE0122=oldE0122!=null?oldE0122:"";
			}
			if(B0110.equalsIgnoreCase(oldB0110)&&E0122.equalsIgnoreCase(oldE0122)){
				flag=true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	public boolean isChangeOrg(String tablename,String A0100,
			String E01A1,Connection conn){
		boolean flag=false;
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select E01A1 from ");
		sqlstr.append(tablename);
		sqlstr.append(" where A0100='");
		sqlstr.append(A0100);
		sqlstr.append("'");
		ContentDAO dao  = new ContentDAO(conn);
		String oldE01A1="";
		try {
			RowSet rs = dao.search(sqlstr.toString());
			while(rs.next()){
				oldE01A1 = rs.getString("E01A1");
				oldE01A1=oldE01A1!=null?oldE01A1:"";
			}
			if(E01A1.equalsIgnoreCase(oldE01A1)){
				flag=true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	public boolean DetailInfoUpdate(String updateType,String[] fields, String[] fieldValues,String tableName,String userid,String I9999,String username,Connection conn) {
		boolean flag = false;
		String personname=username;//this.getUsername(tableName,userid,conn); //chenmengqing added
		StringBuffer strsql=new StringBuffer();
		ArrayList sqlParams = new ArrayList();
		strsql.append("update ");
		strsql.append(tableName);
		strsql.append(" set ");
		for (int i = 0; i < fields.length; i++) {
			if(fields[i]==null)
			    continue;

			FieldItem item = DataDictionary.getFieldItem(fields[i]);
			if(item==null)
				continue;

			if(fieldValues[i]!=null&&!"null".equals(fieldValues[i])&&!fields[i].equals(fieldValues[i])){
			    strsql.append(fields[i] + "=?,");
				if(!"D".equalsIgnoreCase(item.getItemtype())) {
					if("N".equalsIgnoreCase(item.getItemtype()))
						sqlParams.add(fieldValues[i]);
					else
						sqlParams.add(fieldValues[i].substring(1,fieldValues[i].length()-1));
				} else {
					if(!fieldValues[i].contains("TO_DATE(")) {
						sqlParams.add(fieldValues[i].substring(1,fieldValues[i].length()-1));
					} else {
						sqlParams.add(formatDate(fieldValues[i]));
					}
				}
			} else {
				if (!fields[i].equals(fieldValues[i])) {
					strsql.append(fields[i] + "=?,");
					sqlParams.add(null);
				}
			}
		}
		if(username.equals(personname))
			strsql.append("state='0',");
		else
			strsql.append("state='3',");
	    strsql.append("ModTime=");
	    strsql.append(PubFunc.DoFormatSystemDate(true));		
	    strsql.append(",ModUserName=?");
	    sqlParams.add(username);

		if ("1".equals(updateType)) //人员
		{
			strsql.append(" where A0100=?");
		}
		else if ("2".equals(updateType)) //单位
	    {
			strsql.append(" where B0110=?");
		}
		else if ("3".equals(updateType)) //部门
		{
			strsql.append(" where B0110=?");
		}
		else //(queryType.equals("4"))   //职位
		{
			strsql.append(" where E01A1=?");
		}

		sqlParams.add(userid);
		strsql.append(" and I9999=?");
		sqlParams.add(I9999);

		try {
			ContentDAO dao =new ContentDAO(conn);
			dao.update(strsql.toString(), sqlParams);
			flag = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return flag;
	}

	private String getA0000(String toTable,Connection conn) {
		String strsql = "select max(A0000) as a0000 from " + toTable;
		int userId=10;			
		try
		{
			List rs=ExecuteSQL.executeMyQuery(strsql,conn);
			if(!rs.isEmpty())
			{
				DynaBean rec=(DynaBean)rs.get(0); 
				if(rec.get("a0000")!=null)
				userId=Integer.parseInt(rec.get("a0000").toString()==null|| "".equals(rec.get("a0000").toString())?"0":rec.get("a0000").toString()) + 10;
			}	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		  //new ExecuteSQL().freeConn();
		}
		return String.valueOf(userId);		
	}
	public StringBuffer transferInformation(String fromTable,String toTable,String fromNumber,String toNumber,String tabletype,String fieldstr,Connection conn){
			StringBuffer strsql =new  StringBuffer();
			try {
				if ("A01".equals(tabletype)) {
					String strA0000 = getA0000(toTable,conn);
					strsql.append("insert into ");
					strsql.append(toTable);
					strsql.append("(A0000,A0100,State,CreateUserName,CreateTime,ModUserName,ModTime,UserName,UserPassword");
					strsql.append(fieldstr);
					strsql.append(") select ");
					strsql.append(strA0000);
					strsql.append(",'");
					strsql.append(toNumber);
					strsql.append("',State,CreateUserName,CreateTime,ModUserName,ModTime,UserName,UserPassword");
					strsql.append(fieldstr);
					strsql.append(" from ");
					strsql.append(fromTable);
					strsql.append(" where A0100='" + fromNumber + "'");
					//new ExecuteSQL().execUpdate(strsql.toString());
				} else if ("A00".equals(tabletype)) {
					try{
					    new ExecuteSQL().execUpdate("delete from " + toTable + " where A0100='" + toNumber + "'",conn);
						}
					catch(Exception e)
					{
					}					
					strsql.append("insert into ");
					strsql.append(toTable);
					strsql.append("(A0100,I9999,Title,Ole,Flag,State,Id,ext,CreateTime,ModTime,CreateUserName,ModUserName) select '");
					strsql.append(toNumber);
					strsql.append("',I9999,Title,Ole,Flag,State,Id,ext,CreateTime,ModTime,CreateUserName,ModUserName from ");
					strsql.append(fromTable);
					strsql.append(" where A0100='" + fromNumber + "'");
					//new ExecuteSQL().execUpdate(strsql.toString());
				} else {
					try{
					    new ExecuteSQL().execUpdate("delete from " + toTable + " where A0100='" + toNumber + "'",conn);
						}
					catch(Exception e)
					{
					}	
					strsql.append("insert into ");
					strsql.append(toTable);
					strsql.append("(A0100,I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
					strsql.append(fieldstr);
					strsql.append(") select '");
					strsql.append(toNumber);
					strsql.append("',I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
					strsql.append(fieldstr);
					strsql.append(" from ");
					strsql.append(fromTable);
					strsql.append(" where A0100='" + fromNumber + "'");
					//new ExecuteSQL().execUpdate(strsql.toString());
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return strsql;
		}
	public String[] getFieldcode() {
		return fieldcode;
	}
	public void setFieldcode(String[] fieldcode) {
		this.fieldcode = fieldcode;
	}
	
	/**
	 * orcle 日期格式化
	 * @param date 转换日期的sql片段
	 * @return
	 */
	private Timestamp formatDate(String date) {
		Timestamp timestamp = null;
		try {
			String dateStr = date.substring(date.indexOf("TO_DATE('")+9);
			//【60999】【60855】orcle参数化保存日期数据不能直接保存字符串格式的日期
			String pattern = dateStr.substring(dateStr.indexOf(",") + 1, dateStr.length() -2).trim();
			pattern = pattern.substring(1);
			dateStr = dateStr.substring(0, dateStr.indexOf("'"));
			if(16 == dateStr.length())
				dateStr = dateStr + ":00";
			//解决SimpleDateFormat 不认YYYY-MM-DD HH24:MI:SS格式的问题
			//SimpleDateFormat日期格式化时，年月日必须是yyyy-MM-dd，年和日的不能是大写,小时和秒必须是小写!!!!!
			pattern = pattern.replace("YYYY","yyyy").replace("DD", "dd").replace("HH24:MI","HH:mm").replace(":SS", ":ss");
			timestamp = DateUtils.getTimestamp(dateStr, pattern);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return timestamp;
	}
}
