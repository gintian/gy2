package com.hjsj.hrms.interfaces.general.DBstep;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class iDBManager2000 {

  public String ClassString=null;
  public String ConnectionString=null;
  public String UserName=null;
  public String PassWord=null;

  public Connection Conn;
  public Statement Stmt;


  public iDBManager2000() {

	  SystemConfig.getPropertyValue("signature");
		
	  
    //For ODBC
    //ClassString="sun.jdbc.odbc.JdbcOdbcDriver";
    //ConnectionString=("jdbc:odbc:DBDemo");
    //UserName="dbdemo";
    //PassWord="dbdemo";


    //For Access Driver
    //ClassString="sun.jdbc.odbc.JdbcOdbcDriver";
    //ConnectionString=("jdbc:odbc:Driver={MicroSoft Access Driver (*.mdb)};DBQ=C:\\DBstep.mdb;ImplicitCommitSync=Yes;MaxBufferSize=512;MaxScanRows=128;PageTimeout=5;SafeTransactions=0;Threads=3;UserCommitSync=Yes;").replace('\\','/');

    //For SQLServer Driver
//    ClassString="com.microsoft.sqlserver.jdbc.SQLServerDriver";
//    
//    ConnectionString="jdbc:sqlserver://192.192.100.162:1433;DatabaseName=ykchr2;";
//    UserName="yksoft";
//    PassWord="yksoft1919";
    //For sqlserver  driver 首机场
    //ConnectionString="jdbc:sqlserver://192.168.101.174:1433;DatabaseName=test;"; 
	//   UserName="yksoft";
	 //  PassWord="yksoft1919";
	 //  ClassString="com.microsoft.sqlserver.jdbc.SQLServerDriver";
    //For Oracle Driver
//    ClassString="oracle.jdbc.driver.OracleDriver";
//    ConnectionString="jdbc:oracle:thin:@127.0.0.1:1521:ORCL";
//    UserName="system";
//    PassWord="tiger";
//	  ClassString="oracle.jdbc.driver.OracleDriver";
//	  ConnectionString="jdbc:oracle:thin:@192.192.100.160:1521:DBDemo";
//	  UserName="dbdemo";
//	  PassWord="dbdemo";
	  
    //For MySQL Driver
    //ClassString="org.gjt.mm.mysql.Driver";
    //ConnectionString="jdbc:mysql://localhost/softforum?user=...&password=...&useUnicode=true&characterEncoding=8859_1";
    //ClassString="com.mysql.jdbc.Driver";
    //ConnectionString="jdbc:mysql://localhost/dbstep?user=root&password=&useUnicode=true&characterEncoding=gb2312";

    //For Sybase Driver
    //ClassString="com.sybase.jdbc.SybDriver";
    //ConnectionString="jdbc:sybase:Tds:localhost:5007/tsdata"; //tsdata为你的数据库名
    //Properties sysProps = System.getProperties();
    //SysProps.put("user","userid");
    //SysProps.put("password","user_password");
    //If using Sybase then DriverManager.getConnection(ConnectionString,sysProps);
  }

  public boolean OpenConnection()
  {
   boolean mResult=true;
   try
   {
	   	UserName=SystemConfig.getPropertyValue("db_user");
	    PassWord=SystemConfig.getPropertyValue("db_user_pwd");
	    Conn= AdminDb.getConnection();
	    Stmt=Conn.createStatement();
	    mResult=true;
	    
	    /*
	    switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			  ClassString="com.microsoft.sqlserver.jdbc.SQLServerDriver";
			    ConnectionString="jdbc:sqlserver://"+SystemConfig.getPropertyValue("dbserver_addr")+":"+SystemConfig.getPropertyValue("dbserver_port")+";DatabaseName="+SystemConfig.getPropertyValue("dbname")+";";
			   
			    try{
			    Class.forName(ClassString);
			     if ((UserName==null) && (PassWord==null))
			     {
			       Conn= DriverManager.getConnection(ConnectionString);
			     }
			     else
			     {
			       Conn= DriverManager.getConnection(ConnectionString,UserName,PassWord);
			     }
			    }catch(Exception e2){
			    	Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
				     if ((UserName==null) && (PassWord==null))
				     {
				       Conn= DriverManager.getConnection("jdbc:microsoft:sqlserver://"+SystemConfig.getPropertyValue("dbserver_addr")+":"+SystemConfig.getPropertyValue("dbserver_port")+";DatabaseName="+SystemConfig.getPropertyValue("dbname")+";");
				     }
				     else
				     {
				       Conn= DriverManager.getConnection("jdbc:microsoft:sqlserver://"+SystemConfig.getPropertyValue("dbserver_addr")+":"+SystemConfig.getPropertyValue("dbserver_port")+";DatabaseName="+SystemConfig.getPropertyValue("dbname")+";",UserName,PassWord);
				     }
			    }
			     Stmt=Conn.createStatement();
			     mResult=true;
			  break;
		  }
		  case Constant.DB2:
		  {
			  
		  	break;
		  }
		  case Constant.ORACEL:
		  {
		    ClassString="oracle.jdbc.driver.OracleDriver";
		    ConnectionString="jdbc:oracle:thin:@"+SystemConfig.getPropertyValue("dbserver_addr")+":"+SystemConfig.getPropertyValue("dbserver_port")+":"+SystemConfig.getPropertyValue("dbname");
		    if ((UserName==null) && (PassWord==null))
		     {
		       Conn= DriverManager.getConnection(ConnectionString);
		     }
		     else
		     {
		       Conn= DriverManager.getConnection(ConnectionString,UserName,PassWord);
		     }
		    Stmt=Conn.createStatement();
		     mResult=true;
		  }
		}*/
	   //判断表是否存在，不存在就创建
	   Table table=new Table("HTMLDocument");
	   DBMetaModel dbmodel=new DBMetaModel(Conn);
		DbWizard dbWizard=new DbWizard(Conn);
		if(!dbWizard.isExistTable(table.getName(),false))
		{
			Field obj=new Field("DocumentID","DocumentID");	
				obj.setDatatype(DataType.STRING);
				obj.setVisible(false);
				obj.setLength(255);//改成255 同表HTMLSignature统一 20160820
				table.addField(obj);
				 obj=new Field("XYBH","XYBH");	
				obj.setDatatype(DataType.STRING);
				obj.setVisible(false);
				obj.setLength(64);
				table.addField(obj);
				 obj=new Field("BMJH","BMJH");	
					obj.setDatatype(DataType.STRING);
					obj.setVisible(false);
					obj.setLength(20);
					table.addField(obj);
					 obj=new Field("JF","JF");	
						obj.setDatatype(DataType.STRING);
						obj.setVisible(false);
						obj.setLength(128);
						table.addField(obj);
						 obj=new Field("YF","YF");	
							obj.setDatatype(DataType.STRING);
							obj.setVisible(false);
							obj.setLength(128);
							table.addField(obj);
							 obj=new Field("HZNR","HZNR");	
								obj.setDatatype(DataType.STRING);
								obj.setVisible(false);
								obj.setLength(500);
								table.addField(obj);
								 obj=new Field("QLZR","QLZR");	
									obj.setDatatype(DataType.STRING);
									obj.setVisible(false);
									obj.setLength(1000);
									table.addField(obj);
									 obj=new Field("CPMC","CPMC");	
										obj.setDatatype(DataType.STRING);
										obj.setVisible(false);
										obj.setLength(254);
										table.addField(obj);
										 obj=new Field("DGSL","DGSL");	
											obj.setDatatype(DataType.STRING);
											obj.setVisible(false);
											obj.setLength(254);
											obj.setAlign("left");
											table.addField(obj);
											 obj=new Field("DGRQ","DGRQ");	
												obj.setDatatype(DataType.STRING);
												obj.setVisible(false);
												obj.setLength(254);
												obj.setAlign("left");
												table.addField(obj);
			dbWizard.createTable(table); 
			dbmodel.reloadTableModel("HTMLDocument");
		}
		  table=new Table("HTMLSignature");
			
			if(!dbWizard.isExistTable(table.getName(),false))
			{
				Field obj=new Field("DocumentID","DocumentID");	
					obj.setDatatype(DataType.STRING);
					obj.setVisible(false);
					obj.setLength(255);
					obj.setAlign("left");
					table.addField(obj);
					 obj=new Field("SignatureID","SignatureID");	
					obj.setDatatype(DataType.STRING);
					obj.setVisible(false);
					obj.setLength(64);
					obj.setAlign("left");
					table.addField(obj);
					 obj=new Field("SignatureSize","SignatureSize");	
						obj.setDatatype(DataType.INT);
						obj.setVisible(false);
						obj.setAlign("left");
						table.addField(obj);
						 obj=new Field("Signature","Signature");	
							obj.setDatatype(DataType.BLOB);
							obj.setVisible(false);
							obj.setAlign("left");
							table.addField(obj);
				dbWizard.createTable(table);
				
				dbmodel.reloadTableModel("HTMLSignature");
			}
			if(!dbWizard.isExistField("HTMLSignature", "username",false))
			{
				table = new Table("HTMLSignature");
				Field obj=new Field("username","username");	
				obj.setDatatype(DataType.STRING);
				obj.setVisible(false);
				obj.setLength(100);
				obj.setAlign("left");
				table.addField(obj);
				dbWizard.addColumns(table);
				dbmodel.reloadTableModel("HTMLSignature");
			}
			
			
			 table=new Table("HTMLHistory");
				
				if(!dbWizard.isExistTable(table.getName(),false))
				{
					Field obj=new Field("DocumentID","DocumentID");	
						obj.setDatatype(DataType.STRING);
						obj.setVisible(false);
						obj.setLength(255);
						obj.setAlign("left");
						table.addField(obj);
						 obj=new Field("SignatureID","SignatureID");	
						obj.setDatatype(DataType.STRING);
						obj.setVisible(false);
						obj.setLength(255);
						obj.setAlign("left");
						table.addField(obj);
						 obj=new Field("SignatureName","SignatureName");	
							obj.setDatatype(DataType.STRING);
							obj.setVisible(false);
							obj.setLength(100);
							obj.setAlign("left");
							table.addField(obj);
							 obj=new Field("SignatureUnit","SignatureUnit");	
								obj.setDatatype(DataType.STRING);
								obj.setVisible(false);
								obj.setLength(100);
								obj.setAlign("left");
								table.addField(obj);
								 obj=new Field("SignatureUser","SignatureUser");	
									obj.setDatatype(DataType.STRING);
									obj.setVisible(false);
									obj.setLength(100);
									obj.setAlign("left");
									table.addField(obj);
									 obj=new Field("KeySN","KeySN");	
										obj.setDatatype(DataType.STRING);
										obj.setVisible(false);
										obj.setLength(100);
										obj.setAlign("left");
										table.addField(obj);
										 obj=new Field("SignatureSN","SignatureSN");	
											obj.setDatatype(DataType.STRING);
											obj.setVisible(false);
											obj.setLength(200);
											obj.setAlign("left");
											table.addField(obj);
											 obj=new Field("SignatureGUID","SignatureGUID");	
												obj.setDatatype(DataType.STRING);
												obj.setVisible(false);
												obj.setLength(50);
												obj.setAlign("left");
												table.addField(obj);
												 obj=new Field("IP","IP");	
													obj.setDatatype(DataType.STRING);
													obj.setVisible(false);
													obj.setLength(50);
													obj.setAlign("left");
													table.addField(obj);
													 obj=new Field("LogType","LogType");	
														obj.setDatatype(DataType.STRING);
														obj.setVisible(false);
														obj.setLength(20);
														obj.setAlign("left");
														table.addField(obj);
														 obj=new Field("LogTime","LogTime");	
															obj.setDatatype(DataType.DATE);
															obj.setVisible(false);
															obj.setAlign("right");
															table.addField(obj);
					dbWizard.createTable(table); 
					dbmodel.reloadTableModel("HTMLHistory");
				}
   }
  
   catch(Exception e)
   {
     System.out.println(e.toString());
     mResult=false;
   }
   return (mResult);
  }
 
  //关闭数据库连接
  public void CloseConnection()
  {
   try
   {
     Stmt.close();
     Conn.close();
   }
   catch(Exception e)
   {
     System.out.println(e.toString());
   }
  }
  public String GetDateTime()
  {
   Calendar cal  = Calendar.getInstance();
   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   String mDateTime=formatter.format(cal.getTime());
   return (mDateTime);
  }
  
  public  java.sql.Date  GetDate()
  {
    java.sql.Date mDate;
    Calendar cal  = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String mDateTime=formatter.format(cal.getTime());
    return (java.sql.Date.valueOf(mDateTime));
  }

	public int GetMaxID(String vTableName, String vFieldName) {
		int mResult = 0;
		String mSql = new String();
		mSql = "select max(" + vFieldName + ")+1 as MaxID from " + vTableName;
		if (OpenConnection()) {
			ResultSet result = null;
			try {
				result = ExecuteQuery(mSql);
				if (result.next()) {
					mResult = result.getInt("MaxID");
				}
				result.close();
			} catch (Exception e) {
				System.out.println(e.toString());
			}finally {
				PubFunc.closeDbObj(result);
			}
			CloseConnection();
		}
		return (mResult);
	}

	public ResultSet ExecuteQuery(String SqlString) {
		ResultSet result = null;
		try {
			result = Stmt.executeQuery(SqlString);
		} catch (Exception e) {
			System.out.println(e.toString());
		}  
		return (result);
	}

  public int ExecuteUpdate(String SqlString)
  {
    int result=0;
    try
    {
      result=Stmt.executeUpdate(SqlString);
    }
    catch(Exception e)
    {
      System.out.println(e.toString());
    }
    return (result);
  }
  
  //oracle 写BLOB类型
  public void PutAtBlob(Blob vField,byte[]SignatureBody) throws IOException{
	  BufferedOutputStream outstream=null;
    try{
      int vSize = SignatureBody.length;
       outstream =new BufferedOutputStream(vField.setBinaryStream(0)); 
      outstream.write(SignatureBody,0, vSize);
      outstream.flush();
      outstream.close();
    }catch(SQLException e){
    	System.out.println(e.toString());
    }finally {
    	PubFunc.closeIoResource(outstream);
    }
  }
  
  //读oracle BLOB类型
  public String GetAtBlob(java.sql.Blob vField, int vSize) throws
	SQLException,
	IOException {
	String mSignData = null;
	InputStream instream = null;
	try {
		byte[] mTmp = new byte[vSize];
		instream = vField.getBinaryStream();
		instream.read(mTmp, 0, vSize);
		mSignData = new String(mTmp);
		instream.close();
		mSignData = mSignData;
	}
	catch (IOException e) {
		System.out.println(e.toString());
	}finally{
	    if(instream!=null){
	        PubFunc.closeIoResource(instream);
	    }
	}
	return (mSignData);
}
  
}