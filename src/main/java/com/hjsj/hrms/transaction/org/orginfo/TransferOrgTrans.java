/*
 * Created on 2006-1-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.SqlDifference;
import com.hjsj.hrms.valueobject.common.OrganizationView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.tablemodel.ModelField;
import com.hrms.frame.dao.tablemodel.TableModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 机构划转
 */
public class TransferOrgTrans extends IBusiness {
	//用于保存多媒体子集中的的附件的变量
	HashMap<String, InputStream> multiMap = new HashMap<String, InputStream>();
	boolean version =false;
	ArrayList msgb0110 = new ArrayList();
	
	public void execute() throws GeneralException {
		if(this.getUserView().getVersion()>=50){
			version = true;
		}
		String transfercodeitemid=(String)this.getFormHM().get("transfercodeitemid");
		transfercodeitemid=transfercodeitemid.toUpperCase();
		
		
		ArrayList delorglist=(ArrayList)this.getFormHM().get("transferorglist");
		 checkorg();
		if(delorglist==null||delorglist.size()==0)
           return;
        try
        {
        	//msgb0110.add(transfercodeitemid);
        	String tarcodesetid="";
        	ContentDAO dao=new ContentDAO(this.getFrameconn());	
        	this.frowset=dao.search("select codesetid from organization where codeitemid='" + transfercodeitemid + "'");
            if(this.frowset.next())
            {
            	tarcodesetid=this.frowset.getString("codesetid");
            }
        	ArrayList combineorg=new ArrayList();
        	ArrayList codelist = new ArrayList();
        	String transfercodeitemidall = tarcodesetid+transfercodeitemid;
        	ArrayList peopleOrgList = new ArrayList();//人员变动前的机构 xuj 2010-4-28
        	//System.out.println("------GetNextId----->");
            for(int i=0;i<delorglist.size();i++){
      	        RecordVo vo=(RecordVo)delorglist.get(i);
      	        OrganizationView orgview=new OrganizationView();
	    		orgview.setCodesetid(vo.getString("codesetid"));
	    		orgview.setCodeitemid(vo.getString("codeitemid"));
	    		if(transfercodeitemid.equals(vo.getString("codeitemid")) || transfercodeitemid.equals(vo.getString("parentid")))
	    			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.notransself"),"",""));
	    		//if("@K".equalsIgnoreCase(vo.getString("codesetid"))&& "UN".equalsIgnoreCase(tarcodesetid))
	    		//	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.kknottoun"),"",""));
	    		combineorg.add(orgview);
	    		peopleOrgList.add(orgview);
	    		codelist.add(vo.getString("codesetid")+vo.getString("codeitemid"));
	    		msgb0110.add(vo.getString("codeitemid"));
      	     }
            this.getFormHM().put("peopleOrg", "transfer");
            this.getFormHM().put("peopleOrgList", peopleOrgList);
            this.peopleOrgChange();
            //System.out.println("------GetNextId----->1");
           /*{ A0000 大排序

                CodeItemId    A0000
                -------------------
                1             1
                  11          2
                  12          3
                2             4
                  22          5
                  21          6
                3             7
                  31          8
                  32          9
                4             10
                  42          11
                  45          12


          机构合并时，A0000 调整:
            1. 计算源节点子节点数 srcChildCount
            2. 得到 NewA0000 即：目的节点最后一个子节点的 A0000 + 1
            3. 将 A0000 >= NewA0000 的节点的 A0000 增加 srcChildCount
            4. 更新源节点所有子节点的 A0000 从 NewA0000 开始编号，并保持原 A0000 顺序

          机构划转时，A0000 调整:
            1. 计算源节点子节点数 srcCount (包括源节点)
            2. 得到 NewA0000 即：目的节点最后一个子节点的 A0000 + 1
            3. 将 A0000 >= NewA0000 的节点的 A0000 增加 srcCount
            4. 更新源节点及所有子节点的 A0000 从 NewA0000 开始编号，并保持原 A0000 顺序

          调整顺序, 将源机构插入到目的机构之前:
            1. 计算源节点子节点数 srcCount (包括源节点)
            2. 得到 DestA0000 即：目的节点的 A0000
            3. 将 A0000 >= DestA0000 的节点的 A0000 增加 srcCount
            4. 更新源节点及所有子节点的 A0000 从 DestA0000 开始编号，并保持原 A0000 顺序


          }*/
           // updateA0000_transfer(combineorg,transfercodeitemid); 
       			//removeCodeitem(combineorg,dao);
                transferOrg(combineorg,transfercodeitemid,tarcodesetid);
                checkorg();
   		        addCodeitem(transfercodeitemid,dao);
	   		    String issuperuser ="";
	   			String manageprive ="";
	   			if(userView.isSuper_admin()){
	   				issuperuser ="1";
	   				manageprive=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
	   			}
	   			else if(userView.getStatus()==4||userView.getStatus()==0){
	   				issuperuser="0";
	   				manageprive=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
	   			}
	   			else{
	   				issuperuser="0";
	   				manageprive=userView.getManagePrivCode()+"no";
	   			}
	   			this.getFormHM().put("issuperuser",issuperuser);
	   			this.getFormHM().put("manageprive",manageprive);
   		        this.getFormHM().put("codelist",codelist);
   		        this.getFormHM().put("isrefresh","transfer");
   		        this.getFormHM().put("transfercodeitemidall",transfercodeitemidall);
	 			this.getFormHM().put("msgb0110",msgb0110);
          
        }
	    catch(Exception sqle)
	    {
	    	this.getFormHM().put("isrefresh","notransfer");
	       sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	   
	}
	private void addCodeitem(String combineorg,ContentDAO dao) throws Exception 
	{
		try{
			this.frowset=dao.search("select * from organization where codeitemid like '" + combineorg + "%'");
			while(this.frowset.next())
			{
				CodeItem item=new CodeItem();
				item.setCodeid(this.frowset.getString("codesetid"));
				item.setCodename(this.frowset.getString("codeitemdesc"));
				item.setPcodeitem(this.frowset.getString("parentid"));
				item.setCcodeitem(this.frowset.getString("childid"));
				item.setCodeitem(this.frowset.getString("codeitemid"));
				item.setCodelevel(String.valueOf(this.frowset.getInt("grade")));
				AdminCode.addCodeItem(item);
				//AdminCode.updateCodeItemDesc(this.frowset.getString("codesetid"),this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	private void removeCodeitem(ArrayList combineorg,ContentDAO dao) throws Exception
	{
		try{
			for(int i=0;i<combineorg.size();i++)
			{
			    OrganizationView orgview=(OrganizationView)combineorg.get(i);
				this.frowset=dao.search("select * from organization where codeitemid like '" + orgview.getCodeitemid() + "%'");
				while(this.frowset.next())
				{
					CodeItem item=new CodeItem();
					item.setCodeid(this.frowset.getString("codesetid"));
					item.setCodename(this.frowset.getString("codeitemdesc"));
				   	item.setPcodeitem(this.frowset.getString("parentid"));
					item.setCcodeitem(this.frowset.getString("childid"));
					item.setCodeitem(this.frowset.getString("codeitemid"));
					item.setCodelevel(String.valueOf(this.frowset.getInt("grade")));
					AdminCode.removeCodeItem(item);
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	private void  checkorg()
	{
		 StringBuffer sql =new StringBuffer();
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try{
			 sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
		     sql.append("organization d");
		     sql.append(" WHERE d.parentid = ");
			 sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid and d.codesetid=organization.codesetid)");
		     sql.append(" WHERE  EXISTS (SELECT * FROM ");
		     sql.append("organization c");
		     sql.append(" WHERE c.parentid = ");
		     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid and c.codesetid=organization.codesetid)");
		 //  System.out.println(sql.toString());
		     dao.update(sql.toString());
		     //清除掉没有子节点childid不正确的
		     StringBuffer updateParentcode=new StringBuffer();
	     		updateParentcode.delete(0,updateParentcode.length());
	     		updateParentcode.append("UPDATE ");
	     		updateParentcode.append("organization SET childid =codeitemid  ");
	     		updateParentcode.append(" WHERE not EXISTS (SELECT * FROM ");
	     		updateParentcode.append("organization c");
	     		updateParentcode.append(" WHERE c.parentid = ");
	     		updateParentcode.append("organization.codeitemid and c.parentid<>c.codeitemid) and organization.childid <> organization.codeitemid");
	           // System.out.println(updateParentcode.toString());
			     dao.update(updateParentcode.toString());
	     }catch(Exception e)
	     {
	    	 e.printStackTrace();
	     }
	}
	
	private void updateA0000_transfer(ArrayList transferorg,String destOrgId) throws GeneralException
	{
		for(int i=0;i<transferorg.size();i++)
		{
			OrganizationView orgview=(OrganizationView)transferorg.get(i);
			String ss=orgview.getCodeitemid();
			 // 计算源节点节点数
			int srcChildCount = getOrgChildCount(orgview.getCodeitemid());
			 // newA0000 = 目的节点最后一个子节点的 A0000 + 1
			int  NewA0000 = getOrgChildA0000_Max(destOrgId) + 1;
			 // 后面节点序号后移
			IncOrgA0000(NewA0000, srcChildCount);
			 //更新源节点所有子节点的 A0000 从 NewA0000 开始编号包括原节点
			updateOrgA0000(orgview.getCodeitemid(), NewA0000, true);
		}
	}
	
	private void updateA0000_transfer(String codeitemid,String destOrgId) throws GeneralException
	{
		//for(int i=0;i<transferorg.size();i++)
		//{
			//OrganizationView orgview=(OrganizationView)transferorg.get(i);
			//String ss=orgview.getCodeitemid();
			 // 计算源节点节点数
			int srcChildCount = getOrgChildCount(codeitemid);
			 // newA0000 = 目的节点最后一个子节点的 A0000 + 1
			int  NewA0000 = getOrgChildA0000_Max(destOrgId) + 1;
			 // 后面节点序号后移
			IncOrgA0000(NewA0000, srcChildCount);
			 //更新源节点所有子节点的 A0000 从 NewA0000 开始编号包括原节点
			updateOrgA0000(codeitemid, NewA0000, true);
		//}
	}
	private void updateOrgA0000(String orgId,int StartA0000,boolean IncludeRoot) throws GeneralException
	{
	    DbWizard dbw = new DbWizard(this.getFrameconn()); 
       String s;
       String strOn;
       String strWhere;
       String strSet;
       String strSelect;
	   String tempTable = "t#org_order_temp";  // 临时表
       
       dbw.dropTable(tempTable);
       
       StringBuffer sql=new StringBuffer();
	    //创建排序临时表
		switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			  sql.append("CREATE TABLE ");
			  sql.append(tempTable);
			  sql.append(" (orgId varchar(50), seqId Int IDENTITY(1,1), OrgA0000 Int)");
			  break;
		  }
		  case Constant.DB2:
		  {
			  sql.append("CREATE TABLE ");
			  sql.append(tempTable);
			  sql.append(" (OrgId varchar(50),seqId INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),OrgA0000 INTEGER)");
		  	 break;
		  }
		  case Constant.ORACEL:
		  {
			  sql.append("CREATE TABLE ");
			  sql.append(tempTable);
			  sql.append(" (orgId varchar2(50), seqId int, OrgA0000 int)");
			  break;
		  }
		}
		try{
			  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	    strSelect = "select CodeItemId from Organization " +
	                 " where CodeItemId Like '" + orgId + "%'";
	    if(!IncludeRoot && (!"".equals(orgId)))  // 不包括根节点
	      strSelect = strSelect + " and CodeItemId <> '" + orgId + "'";
        strSelect = strSelect + " Order by A0000 ";

        sql.delete(0,sql.length());
        //设置 SeqId
        switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			  sql.append("Insert into ");
			  sql.append(tempTable);
			  sql.append("(orgId) ");
			  sql.append(strSelect);
			  break;
		  }
		  case Constant.DB2:
		  {
			  sql.append("Insert into ");
			  sql.append(tempTable);
			  sql.append("(orgId) ");
			  sql.append(strSelect);
		      break;
		  }
		  case Constant.ORACEL:
		  {
			  sql.append("Insert into ");
			  sql.append(tempTable);
			  sql.append(" (orgId, SeqId) ");
			  sql.append(" select a.CodeItemId, RowNum from (");
			  sql.append("   ");
			  sql.append(strSelect);
			  sql.append("   ) a");  // 别名			
			  break;
		  }
		}
        try{
        	ContentDAO dao=new ContentDAO(this.getFrameconn());	
			dao.update(sql.toString()); 
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	    // 设置orgA0000 = seqId
		sql.delete(0,sql.length());
	    sql.append("update ");
	    sql.append(tempTable);
	    sql.append(" set orgA0000 = SeqId");
	    try{
        	ContentDAO dao=new ContentDAO(this.getFrameconn());	
			dao.update(sql.toString()); 
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		sql.delete(0,sql.length());
	    //现在 orgA0000 从 1 开始, 更新 orgA0000 从 startA0000 开始
	    if(StartA0000 > 1){ 
	   
	      // MSSQL 中，不能直接更新标识列
	      // s := 'update ' + tempTable + ' set SeqId = SeqId + ' + IntToStr(startA0000 - 1);
	      sql.append("update ");
	      sql.append(tempTable);
	      sql.append(" set orgA0000 = orgA0000 + ");
	      sql.append(StartA0000 - 1);
	      try{
	        	ContentDAO dao=new ContentDAO(this.getFrameconn());	
				dao.update(sql.toString()); 
			}catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
	    }
	   
	    //更新 A0000
	    strOn = "organization.CodeItemId = " + tempTable + ".orgId";
	    strSet = "organization.A0000 = " + tempTable + ".orgA0000";
	    strWhere = "organization.CodeItemId like '" + orgId + "%'";
	    if(!IncludeRoot && (!"".equals(orgId))){   // 不包括根节点
	      strWhere = strWhere + " and organization.CodeItemId <> '" + orgId + "'";
	    }
	    sql.delete(0,sql.length());
	    //设置 SeqId
	    /*例：
	    SQLSERVER:
	      Update destTable
	      Set destTable.F1 = srcTable.FA
	        From DestTable Left Join srcTable
	          On DestTable.FB = srcTable.FB
	        WHERE srcWhere
	    ACCESS:
	      Update destTable
	        Left Join srcTable
	          On DestTable.FB = srcTable.FB
	        Set destTable.F1 = srcTable.FA
	        WHERE srcWhere
	      WHERE destWhere*/
	    /*例:
	    	ORACLE, DB2:
	    	  Update destTable
	    	  Set (destTable.F1, destTable.F2) =
	    	    (SELECT srcTable.F1, srcTable.F2
	    	     FROM srcTable
	    	     WHERE strOn and srcWhere
	    	    )
	    	  WHERE destWhere*/
	    //getDBOper.RecordUpdate("organization", tempTable, strOn, strSet, strWhere, strWhere);
        switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			  sql.append("Update organization Set ");
			  sql.append("organization.A0000 = " + tempTable + ".orgA0000");
		      sql.append(" from organization left join ");
			  sql.append(tempTable);
			  sql.append(" on organization.CodeItemId = " + tempTable + ".orgId");
	          sql.append(" where ");
			  sql.append(strWhere);
			  break;
		  }
		  case Constant.DB2:
		  { 
			  sql.append("Update organization set ");
			  sql.append("(organization.A0000)=(SELECT ");
			  sql.append(tempTable);
			  sql.append(".orgA0000 from ");
			  sql.append(tempTable);
			  sql.append(" where ");
			  sql.append(strOn);
			  sql.append(" and ");
			  sql.append(strWhere);
			  sql.append(")");
			  sql.append(" where ");
			  sql.append(strWhere);			
		      break;
		  }
		  case Constant.ORACEL:
		  {
			  sql.append("Update organization set ");
			  sql.append("(organization.A0000)=(SELECT ");
			  sql.append(tempTable);
			  sql.append(".orgA0000 from ");
			  sql.append(tempTable);
			  sql.append(" where ");
			  sql.append(strOn);
			  sql.append(" and ");
			  sql.append(strWhere);
			  sql.append(")");
			  sql.append(" where ");
			  sql.append(strWhere);		
			  break;
		  }
		}      	   
        try{
        	ContentDAO dao=new ContentDAO(this.getFrameconn());	
			dao.update(sql.toString()); 
  		}catch(Exception e)
  		{
  			e.printStackTrace();
  			throw GeneralExceptionHandler.Handle(e);
  		}	
	    // 删除临时表
  		dbw.dropTable(tempTable);
	}
    //后面节点序号后移
	private void IncOrgA0000(int StartA0000, int Increment)throws GeneralException
	{
        String strSet="";
	    if(Increment == 0)
	    	return;
	    if(Increment > 0)
	       strSet = "A0000 = A0000 + " + Increment;
	    else
	       strSet = "A0000 = A0000 - " + Math.abs(Increment);
 	    strSet= "update Organization set " + strSet + " where A0000 >= "  + StartA0000;
 	     try{ 
 			    ContentDAO dao=new ContentDAO(this.getFrameconn());	
 				dao.update(strSet); 		       
 	        }
 		    catch(Exception sqle)
 		    {
 		       sqle.printStackTrace();
 		      throw GeneralExceptionHandler.Handle(sqle);
 		    }
	}
	 // newA0000 = 目的节点最后一个子节点的 A0000
	private int getOrgChildA0000_Max(String parentId)throws GeneralException 
	{
		int n=0;
        try{ 
        	String s="SELECT MAX(A0000) as a0000 FROM Organization  WHERE codeitemid LIKE '" + parentId + "%'";
       	// if(parentId!="")
   	      //   s = s + " AND CodeItemId <> '" + parentId + "'";
    	    ContentDAO dao=new ContentDAO(this.getFrameconn());	
			this.frowset=dao.search(s);
	        if(this.frowset.next())
	        {
	        	n=this.frowset.getInt("a0000");
	        }
        }
	    catch(Exception sqle)
	    {
	       sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
        return n;
	}
   //返回节点数
	private int getOrgChildCount(String parentId) throws GeneralException
	{
		int n=0;
		try{
			String s="SELECT count(*) as count FROM Organization WHERE codeitemid LIKE '" + parentId + "%'";
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			this.frowset=dao.search(s);
	        if(this.frowset.next())
	        {
	        	n=this.frowset.getInt("count");
	        }
		}
	    catch(Exception sqle)
	    {
	       sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
        return n;
	}
	private void transferOrg(ArrayList combineorg,String transfercodeitemid,String targetset) throws GeneralException
	{
	    DbWizard dbw = new DbWizard(this.getFrameconn());
	    
		ArrayList IniOrgList=new ArrayList();
		ArrayList tarOrgList=new ArrayList();
		String codesetid="";
		String codeitemid="";
		String parentid="";
		String childid="";
		String Level="0";
		boolean ishavechild=false;
		StringBuffer sqlstr=new StringBuffer();
	    ContentDAO dao=new ContentDAO(this.getFrameconn());	  
	    try{
	    	sqlstr.delete(0,sqlstr.length());
			sqlstr.append("select codesetid,codeitemid,parentid,childid,grade,");
			sqlstr.append(" (select max(codeitemid) childid from ");
			sqlstr.append( " (select codeitemid from organization where parentid='"+transfercodeitemid+"' ");
			sqlstr.append(" union select codeitemid from vorganization where parentid='"+transfercodeitemid+"') A) maxchild " );
			sqlstr.append(" from organization where codeitemid='");
			sqlstr.append(transfercodeitemid);
			sqlstr.append("'");
			sqlstr.append(" union select codesetid,codeitemid,parentid,childid,grade,(select max(codeitemid) from vorganization where parentid='"+transfercodeitemid+"') maxchild from vorganization where codeitemid='");
			sqlstr.append(transfercodeitemid);
			sqlstr.append("'");
		    this.frowset=dao.search(sqlstr.toString());      //父结点的信息
			 if(this.frowset.next())
			 {
			 	codesetid=this.frowset.getString("codesetid");
			 	codeitemid=this.frowset.getString("codeitemid");
			 	parentid=this.frowset.getString("parentid");
			 	childid=this.frowset.getString("childid");
			 	String maxchild = this.frowset.getString("maxchild");
			 	if(maxchild!=null && maxchild.length()>0){
			 		childid = maxchild;
			 	}
			 	if(codeitemid.equals(childid)) 
			 		ishavechild=false;
			 	else
			 		ishavechild=true;
			 	
			 	if(this.frowset.getString("grade")!=null && this.frowset.getString("grade").length()>0)
			 		Level=String.valueOf(Integer.parseInt(this.frowset.getString("grade")));
			 } 
			ArrayList dblist=DataDictionary.getDbpreList();
			String gradeori="0";
			ArrayList newidlist = new ArrayList();
			
			RecordVo vo = new RecordVo("organization");
			boolean flag = vo.hasAttribute("guidkey");
			
			for(int i=0;i<combineorg.size();i++)
			{
				OrganizationView orgview=(OrganizationView)combineorg.get(i);
				updateA0000_transfer(orgview.getCodeitemid(),transfercodeitemid);   
				//System.out.println("fasdfdsf" + transfercodeitemid + "F" +ishavechild);
				String GetNextId=GetNextId(dao,childid,transfercodeitemid,ishavechild,Level,orgview.getCodeitemid());
				if(isExistCodeItemId(GetNextId)){//如果子机构已经有了，则生成下一个编号。chent 20170518
					GetNextId=GetNext(GetNextId, transfercodeitemid);
				}
				childid=GetNextId;
				newidlist.add(GetNextId);
				msgb0110.add(GetNextId);
				//System.out.println("------GetNextId----->" + GetNextId);
				
				 this.frowset=dao.search("select grade from organization where codeitemid='" + orgview.getCodeitemid() + "'");      //父结点的信息
				 if(this.frowset.next())
				 {
					 if(this.frowset.getString("grade")!=null && this.frowset.getString("grade").length()>0)
							gradeori=String.valueOf(Integer.parseInt(this.frowset.getString("grade")));
				 } 
				
				
				String temptable="t#"+this.userView.getUserName()+"_hr_org_t";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, -1);
				String end_date = (String)this.getFormHM().get("end_date");
				if(end_date==null){
                	SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd");
                	end_date = now.format(new Date());
                	end_date = end_date.replaceAll("-", ".");
                	end_date = end_date!=null&&end_date.length()>9?end_date:sdf.format(calendar.getTime());
                	
                	calendar.setTime(sdf.parse(end_date));
                	calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-1);
                	end_date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
                }
				end_date = end_date.replaceAll("-", ".");
				end_date = end_date!=null&&end_date.length()>9?end_date:sdf.format(calendar.getTime());
				
				calendar.setTime(sdf.parse(end_date));
				calendar.add(Calendar.DATE,+1);
				String start_date = sdf.format(calendar.getTime());
				
				if(version){
					// 删除临时表
					dbw.dropTable(temptable);
					
					sqlstr.delete(0, sqlstr.length());				
					
					switch (Sql_switcher.searchDbServer()) {//复制数据
						case Constant.MSSQL: {
							sqlstr.append("select * into "+temptable);
							sqlstr.append(" from organization where ");
							sqlstr.append("codeitemid like '"+orgview.getCodeitemid()+"%'");
							sqlstr.append(" and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date");
							break;
						}
						case Constant.DB2: {
							
							break;
						}
						case Constant.ORACEL: {
							sqlstr.append("create table "+temptable);
							sqlstr.append(" as select * from organization where ");
							sqlstr.append("codeitemid like '"+orgview.getCodeitemid()+"%'");
							sqlstr.append(" and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date");
							break;
						}
					}
					dao.update(sqlstr.toString());
					
					sqlstr.delete(0,sqlstr.length());
					sqlstr.append("update "+temptable+" set end_date="+Sql_switcher.dateValue(end_date));
					dao.update(sqlstr.toString());
				}
				
				
				sqlstr.delete(0,sqlstr.length());
				sqlstr.append("update organization set codeitemid='");
				sqlstr.append(GetNextId);
				sqlstr.append("',parentid='");
				sqlstr.append(transfercodeitemid);
				sqlstr.append("',childid='");
				sqlstr.append(GetNextId);
				sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("childid",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("childid") + "-" + orgview.getCodeitemid().length()));
				sqlstr.append(",grade= grade + 1 - ");
				sqlstr.append(gradeori);
				sqlstr.append(" + ");
				sqlstr.append(Level);
				sqlstr.append(",start_date="+Sql_switcher.dateValue(start_date)+",end_date="+Sql_switcher.dateValue("9999-12-31"));
				//临时表更新levelA0000为正确值 wangb 20170807 
				sqlstr.append(",levelA0000=" + getMaxLevelA0000(transfercodeitemid));
//				if(flag){
//					sqlstr.append(",GUIDKEY=null");
//				}
				sqlstr.append(" where codeitemid='");
				sqlstr.append(orgview.getCodeitemid());
				sqlstr.append("'");
				//System.out.println(sqlstr.toString());
				dao.update(sqlstr.toString());
				
				sqlstr.delete(0,sqlstr.length());
				sqlstr.append("update organization set codeitemid='");
				sqlstr.append(GetNextId);
				sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("codeitemid",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("codeitemid") + "-" + orgview.getCodeitemid().length()));
				sqlstr.append(",parentid='");				
				sqlstr.append(GetNextId);
				sqlstr.append("'" + SqlDifference.getJoinSymbol() + " " +  Sql_switcher.substr("parentid",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("parentid") + "-" + orgview.getCodeitemid().length()));
				sqlstr.append(",childid='");
				sqlstr.append(GetNextId);
				sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " +  Sql_switcher.substr("childid",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("childid") + "-" + orgview.getCodeitemid().length()));
				sqlstr.append(",grade= grade + 1 - ");
				sqlstr.append(gradeori);
				sqlstr.append(" + ");
				sqlstr.append(Level);
				sqlstr.append(",start_date="+Sql_switcher.dateValue(start_date)+",end_date="+Sql_switcher.dateValue("9999-12-31"));
//				if(flag){
//					sqlstr.append(",GUIDKEY=null");
//				}
				sqlstr.append(" where codeitemid<>'");
				sqlstr.append(orgview.getCodeitemid());
				sqlstr.append("' and codeitemid like '");
				sqlstr.append(orgview.getCodeitemid());
				sqlstr.append("%' and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date");
				dao.update(sqlstr.toString());
				
				/*sqlstr.delete(0,sqlstr.length());
				sqlstr.append("update organization set start_date="+Sql_switcher.dateValue(sdf.format(new Date()))+",end_date="+Sql_switcher.dateValue("9999-12-31"));
				sqlstr.append(" where codeitemid='");
				sqlstr.append(transfercodeitemid);
				sqlstr.append("'");
				//System.out.println(sqlstr.toString());
				dao.update(sqlstr.toString());*/
				
				if(version){
					sqlstr.delete(0, sqlstr.length());//考回复制到临时表中的数据
					if(flag){
						sqlstr.append("update "+temptable+" set GUIDKEY=null ");
						ExecuteSQL.createTable(sqlstr.toString(), this.frameconn);
					}
					
					sqlstr.setLength(0);
					sqlstr.append("insert into organization select * from "+temptable);
					dao.update(sqlstr.toString());
					// 删除临时表
					//sqlstr.delete(0, sqlstr.length());
					//sqlstr.append("drop table ");
					//sqlstr.append(temptable);
					//try {
					//	ExecuteSQL.createTable(sqlstr.toString(), this.getFrameconn());
					//} catch (Exception e) {
						// e.printStackTrace();
					//}
				}
				
				if(!ishavechild)
				{
					sqlstr.delete(0,sqlstr.length());
					sqlstr.append("update organization set childid='");
					sqlstr.append(GetNextId);
					sqlstr.append("' where codeitemid='");
					sqlstr.append(transfercodeitemid);
					sqlstr.append("'");
					childid=GetNextId;
					ishavechild=true;
					//System.out.println(sqlstr.toString());
					dao.update(sqlstr.toString());
				}	
				for(int j=0;j<dblist.size();j++)
				{
					
					
					if("UN".equalsIgnoreCase(orgview.getCodesetid()))
					{
						sqlstr.delete(0,sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("B0110='");
						sqlstr.append(GetNextId);
						sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("B0110",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("B0110") + "-" + orgview.getCodeitemid().length()));
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where B0110 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%'");
						dao.update(sqlstr.toString());	
						
						sqlstr.delete(0,sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("E0122='");
						sqlstr.append(GetNextId);
						sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("E0122",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("E0122") + "-" + orgview.getCodeitemid().length()));
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E0122 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%'");
						dao.update(sqlstr.toString());	
						
						sqlstr.delete(0,sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("E01A1='");
						sqlstr.append(GetNextId);
						sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("E01A1",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("E01A1")  + "-" + orgview.getCodeitemid().length())) ;
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E01A1 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%'");
						dao.update(sqlstr.toString());	
						
						sqlstr.delete(0,sqlstr.length());
						sqlstr.append("update t_hr_mydata_chg set b0110='" + GetNextId);
						sqlstr.append("', e0122='" + GetNextId);
						sqlstr.append("', E01A1='" + GetNextId);
						sqlstr.append("' where B0110 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%' and nbase='" + dblist.get(j) + "' and sp_flag<>'03'");
						dao.update(sqlstr.toString());
					}else if("UM".equalsIgnoreCase(orgview.getCodesetid()))
					{
						sqlstr.delete(0,sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("B0110=");
						if("UN".equalsIgnoreCase(targetset))
						{
						   sqlstr.append("'");
						   sqlstr.append(transfercodeitemid);	
						   sqlstr.append("'");
						}else if("UM".equalsIgnoreCase(targetset))
						{
							sqlstr.append("'");
							sqlstr.append(getTargetUNCodeitemid(transfercodeitemid));
							sqlstr.append("'");
						}
						
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E0122 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%' and B0110 IS NOT NULL");
						dao.update(sqlstr.toString());	
						
						sqlstr.delete(0,sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("E0122='");
						sqlstr.append(GetNextId);
						sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("E0122",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("E0122") + "-" + orgview.getCodeitemid().length()));
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E0122 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%'");
						dao.update(sqlstr.toString());	
						
						sqlstr.delete(0,sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("E01A1='");
						sqlstr.append(GetNextId);
						sqlstr.append("' " + SqlDifference.getJoinSymbol() + " " +  Sql_switcher.substr("E01A1",String.valueOf(orgview.getCodeitemid().length() +1),Sql_switcher.length("E01A1") + "-" + orgview.getCodeitemid().length()));
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E01A1 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%'");
						dao.update(sqlstr.toString());	
						
						sqlstr.delete(0,sqlstr.length());
						sqlstr.append("update t_hr_mydata_chg set b0110='");
						if("UN".equalsIgnoreCase(targetset)) {
						   sqlstr.append(transfercodeitemid);	
						} else if("UM".equalsIgnoreCase(targetset)) {
							sqlstr.append(getTargetUNCodeitemid(transfercodeitemid));
						}
						sqlstr.append("', e0122='" + GetNextId);
						sqlstr.append("', E01A1='" + GetNextId);
						sqlstr.append("' where E0122 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%' and b0110 is not null and nbase='" + dblist.get(j) + "' and sp_flag<>'03'");
						dao.update(sqlstr.toString());
					}else
					{
						sqlstr.delete(0,sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("B0110=");
						if("UN".equalsIgnoreCase(targetset))
						{
						   sqlstr.append("'");
						   sqlstr.append(transfercodeitemid);	
						   sqlstr.append("'");
						}else if("UM".equalsIgnoreCase(targetset))
						{
							sqlstr.append("'");
							sqlstr.append(getTargetUNCodeitemid(transfercodeitemid));
							sqlstr.append("'");
						}
						
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E01A1 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%' and B0110 IS NOT NULL");
						dao.update(sqlstr.toString());	
						
						sqlstr.delete(0,sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("E0122=");
						sqlstr.append("'");
						if("UN".equalsIgnoreCase(targetset)) {
                           sqlstr.append(getTargetUMCodeitemid(GetNextId));
                        } else if("UM".equalsIgnoreCase(targetset)) {
                            sqlstr.append(transfercodeitemid);	
                        }
						
						sqlstr.append("'");
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E01A1='");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("'");
						dao.update(sqlstr.toString());	
						
						sqlstr.delete(0,sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("E01A1='");
						sqlstr.append(GetNextId);
						sqlstr.append("',modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E01A1 ='");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("'");
						dao.update(sqlstr.toString());	
						
						sqlstr.delete(0,sqlstr.length());
						sqlstr.append("update t_hr_mydata_chg set b0110='");
						if("UN".equalsIgnoreCase(targetset)) {
						   sqlstr.append(transfercodeitemid);	
						} else if("UM".equalsIgnoreCase(targetset)) {
							sqlstr.append(getTargetUNCodeitemid(transfercodeitemid));
						}
						
						sqlstr.append("',e0122='");
						if("UN".equalsIgnoreCase(targetset)) {
                           sqlstr.append(getTargetUMCodeitemid(GetNextId));
	                    } else if("UM".equalsIgnoreCase(targetset)) {
                            sqlstr.append(transfercodeitemid);	
                        }
						
						sqlstr.append("',E01A1='" + GetNextId);
						sqlstr.append("' where E01A1='");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("' and nbase='" + dblist.get(j) + "' and sp_flag<>'03'");
						dao.update(sqlstr.toString());
					}
				}
				SysnK(orgview.getCodeitemid(),GetNextId,dao,temptable);
				//机构划转时更新OKR的表 目前环球租赁使用，20160805  产品不启用此代码，客户需要则单独放开
				//updateOKRTable(dao,orgview.getCodeitemid(),GetNextId);
				sqlstr.setLength(0);
				calendar = Calendar.getInstance();
				SimpleDateFormat modtimeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = modtimeSdf.format(calendar.getTime());
				Timestamp timesTamp=DateUtils.getTimestamp(date, "yyyy-MM-dd HH:mm:ss");
				ArrayList sqlvalue=new ArrayList();
				if("@K".equalsIgnoreCase(orgview.getCodesetid())){
						sqlstr.append("select e01a1 from K01 where e01a1='"+GetNextId+"'");
						  this.frecset = dao.search(sqlstr.toString());
						  sqlstr.setLength(0);
						  if(this.frecset.next()){
							  sqlstr.append("update K01 set e0122=?,createusername=?,modusername=?,modtime=? where e01a1 like ?");
							  sqlvalue.add(parentid);
							  sqlvalue.add(this.userView.getUserName());
							  sqlvalue.add(this.userView.getUserName());
							  if(Sql_switcher.searchDbServer() != Constant.MSSQL) {
								  sqlvalue.add(timesTamp);
							  }else {
								  sqlvalue.add(date);
							  }
							  sqlvalue.add(GetNextId+"%");
						  }else{
							  sqlstr.append("insert into K01(e0122,e01a1,createusername,modusername,createtime,modtime) values (?,?,?,?,?,?)");
							  sqlvalue.add(parentid);
							  sqlvalue.add(GetNextId);
							  sqlvalue.add(this.userView.getUserName());
							  sqlvalue.add(this.userView.getUserName());
							  if(Sql_switcher.searchDbServer() != Constant.MSSQL) {
								  sqlvalue.add(timesTamp);
								  sqlvalue.add(timesTamp);
							  }else {
								  sqlvalue.add(date);
								  sqlvalue.add(date);
							  }
						  }
						  dao.update(sqlstr.toString(),sqlvalue);
						  //修改原岗位modtime
						  sqlstr.setLength(0);
						  sqlvalue.clear();
						  sqlstr.append("update K01 set modusername=?,modtime=? where e01a1=?");
						  sqlvalue.add(this.userView.getUserName());
						  if(Sql_switcher.searchDbServer() != Constant.MSSQL) {
							  sqlvalue.add(timesTamp);
						  }else {
							  sqlvalue.add(date);
						  }
						  sqlvalue.add(orgview.getCodeitemid());
						  dao.update(sqlstr.toString(),sqlvalue);
				}else {
						 sqlstr.setLength(0);
						 sqlstr.append("select b0110 from B01 where b0110='"+GetNextId+"'");
						  this.frecset = dao.search(sqlstr.toString());
						  sqlstr.setLength(0);
						  if(this.frecset.next()){
							  sqlstr.append("update B01 set createusername=?,modusername=?,modtime=?  where b0110 like ?");
							  sqlvalue.add(this.userView.getUserName());
							  sqlvalue.add(this.userView.getUserName());
							  if(Sql_switcher.searchDbServer() != Constant.MSSQL) {
								  sqlvalue.add(timesTamp);
							  }else {
								  sqlvalue.add(date);
							  }
							  sqlvalue.add(GetNextId+"%");
						  }else{
							  sqlstr.append("insert into B01(b0110,createusername,modusername,createtime,modtime)  values (?,?,?,?,?)");
							  sqlvalue.add(GetNextId);
							  sqlvalue.add(this.userView.getUserName());
							  sqlvalue.add(this.userView.getUserName());
							  if(Sql_switcher.searchDbServer() != Constant.MSSQL) {
								  sqlvalue.add(timesTamp);
								  sqlvalue.add(timesTamp);
							  }else {
								  sqlvalue.add(date);
								  sqlvalue.add(date);
							  }
						  }
						  dao.update(sqlstr.toString(),sqlvalue);
						  //修改原部门modtime
						  sqlstr.setLength(0);
						  sqlvalue.clear();
						  sqlstr.append("update B01 set modusername=?,modtime=?  where b0110 like ?");
						  sqlvalue.add(this.userView.getUserName());
						  if(Sql_switcher.searchDbServer() != Constant.MSSQL) {
							  sqlvalue.add(timesTamp);
						  }else {
							  sqlvalue.add(date);
						  }
						  sqlvalue.add(orgview.getCodeitemid()+"%");
						  dao.update(sqlstr.toString(),sqlvalue);
						  //修改原岗位modtime
						  sqlstr.setLength(0);
						  sqlvalue.clear();
						  sqlstr.append("update K01 set modusername=?,modtime=? where e01a1 like ?");
						  sqlvalue.add(this.userView.getUserName());
						  if(Sql_switcher.searchDbServer() != Constant.MSSQL) {
							  sqlvalue.add(timesTamp);
						  }else {
							  sqlvalue.add(date);
						  }
						  sqlvalue.add(orgview.getCodeitemid()+"%");
						  dao.update(sqlstr.toString(),sqlvalue);
						  
				}
				if(version){
					// 删除临时表
				    dbw.dropTable(temptable);				    
				}
				this.getFormHM().put("newidlist",newidlist);
			}
			initLayer();
		}catch(Exception e){
		  e.printStackTrace();
		  throw GeneralExceptionHandler.Handle(e);
		}		
	}	
	

  /**   
 * @Title: updateOKRTable   
 * @Description: 同步OKR的机构 、岗位   
 * @param @param dao
 * @param @param codeSetid
 * @param @param oldCode
 * @param @param newCode 
 * @return void 
 * @throws   
*/
    private void updateOKRTable(ContentDAO dao,String oldCode,String newCode)
    {
         try {
            String sql = "";
            String srcFldValue="'"+oldCode+"%'";
            String OldSetSql =" set ? = "+"'"+newCode+ "' " + SqlDifference.getJoinSymbol() + " " 
                + Sql_switcher.substr("?",String.valueOf(oldCode.length() +1),Sql_switcher.length("?") 
                + "-" + oldCode.length())
                + " where ? like "+ srcFldValue;
            String setSql="";
            // 更新计划表
            setSql= OldSetSql.replace("?", "p0707") ;
            sql = "update p07 "+setSql;
            dao.update(sql);                
            setSql= OldSetSql.replace("?", "b0110") ;
            sql = "update p07 "+setSql;
            dao.update(sql);                
            setSql= OldSetSql.replace("?", "e0122") ;
            sql = "update p07 "+setSql;
            dao.update(sql);

            // 更新总结表
            setSql= OldSetSql.replace("?", "b0110") ;
            sql = "update p01 "+setSql;
            dao.update(sql);
            
            setSql= OldSetSql.replace("?", "e0122") ;
            sql = "update p01 "+setSql;
            dao.update(sql);

            // 更新映射表
            setSql= OldSetSql.replace("?", "org_id") ;
            sql = "update per_task_map "+setSql;
            dao.update(sql);
            
            setSql= OldSetSql.replace("?", "org_id") ;
            sql = "update p09 "+setSql;
            dao.update(sql);
            
            //更新个人计划及个人总结的岗位及上级岗位
            setSql= OldSetSql.replace("?", "e01a1") ;
            sql = "update p07 "+setSql;
            dao.update(sql);  
            
            setSql= OldSetSql.replace("?", "supere01a1") ;
            sql = "update p07 "+setSql;
            dao.update(sql); 
            
            setSql= OldSetSql.replace("?", "e01a1") ;
            sql = "update p01 "+setSql;
            dao.update(sql);  
            
            setSql= OldSetSql.replace("?", "supere01a1") ;
            sql = "update p01 "+setSql;
            dao.update(sql);  
            
            //原机构的负责岗位清空，不然工作计划会出现两个同名的部门出现            
            sql = "update b01 set b0199 =null  where b0110 like "+ srcFldValue;;
            dao.update(sql);  

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void SysnK(String fromcode,String tocode,ContentDAO dao,String temptable) throws Exception
	{
		StringBuffer orgsql=new StringBuffer();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat modtimeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = modtimeSdf.format(calendar.getTime());
		Timestamp timesTamp=DateUtils.getTimestamp(date, "yyyy-MM-dd HH:mm:ss");
		ArrayList voList = new ArrayList();
		List infoSetList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
		for(int k=0;k<infoSetList.size();k++)
		{
			FieldSet fieldset=(FieldSet)infoSetList.get(k);
			//String temptable = "temp_"+fieldset.getFieldsetid()+"_"+this.userView.getUserName();
			if(version){
				/*// 删除临时表
				orgsql.delete(0, orgsql.length());
				orgsql.append("drop table ");
				orgsql.append(temptable);
				try {
					ExecuteSQL.createTable(orgsql.toString(), this.getFrameconn());
				} catch (Exception e) {
					// e.printStackTrace();
				}
				//创建临时表存过度数据 
				orgsql.delete(0, orgsql.length());
					switch (Sql_switcher.searchDbServer()) {//复制数据
						case Constant.MSSQL: {
							orgsql.append("select * into "+temptable);
							orgsql.append(" from "+fieldset.getFieldsetid()+" where ");
							orgsql.append("b0110 like '"+fromcode+"%'");
							break;
						}
						case Constant.DB2: {
							
							break;
						}
						case Constant.ORACEL: {
							orgsql.append("create table "+temptable);
							orgsql.append(" as select * from "+fieldset.getFieldsetid()+" where ");
							orgsql.append("b0110 like '"+fromcode+"%'");
							break;
						}
					}
					dao.update(orgsql.toString());*/
				
				//xuj 2010-4-9 改进的方式 频繁的创建表再删除的方式倒换数据效率慢
				StringBuffer sql = new StringBuffer(); 
				sql.append("select * from "+fieldset.getFieldsetid());
				sql.append(" where b0110 in (");
				sql.append("select codeitemid from "+temptable+" where ");
				sql.append(" codeitemid like '");
				sql.append(fromcode);
				sql.append("%' and codesetid<>'@K')");
				voList = this.getRecordVoList(sql.toString(), dao, fieldset.getFieldsetid());
				
				/*orgsql.append("select * ");
				orgsql.append(" from "+fieldset.getFieldsetid()+" where ");
				orgsql.append("b0110 like '"+fromcode+"%'");
				this.frecset = dao.search(orgsql.toString());*/
			}
			orgsql.delete(0,orgsql.length());
			orgsql.append("update  ");
			orgsql.append(fieldset.getFieldsetid());
			orgsql.append(" set B0110='");
			orgsql.append(tocode);
			orgsql.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("b0110",String.valueOf(fromcode.length() +1),Sql_switcher.length("b0110")  + "-" + fromcode.length())) ;

			//orgsql.append(" where b0110 like '");
			//orgsql.append(fromcode);
			//orgsql.append("%'");
			orgsql.append(", modtime="+PubFunc.DoFormatSystemDate(true));
			orgsql.append(" where b0110 in (");
			orgsql.append("select codeitemid from "+temptable+" where ");
			orgsql.append("  codeitemid like '");
			orgsql.append(fromcode);
			orgsql.append("%' and codesetid<>'@K')");
	   		dao.update(orgsql.toString()); 
	   		
	   		if(version){
				/*orgsql.delete(0, orgsql.length());//考回复制到临时表中的数据
				orgsql.append("insert into "+fieldset.getFieldsetid()+" select * from "+temptable);
				dao.update(orgsql.toString());
				// 删除临时表
				orgsql.delete(0, orgsql.length());
				orgsql.append("drop table ");
				orgsql.append(temptable);
				try {
					ExecuteSQL.createTable(orgsql.toString(), this.getFrameconn());
				} catch (Exception e) {
					// e.printStackTrace();
				}*/
	   			
	   		//xuj 2010-4-9 改进的方式 频繁的创建表再删除的方式倒换数据效率慢
				for(int i=0;i<voList.size();i++){
					RecordVo vo = (RecordVo)voList.get(i);
					dao.addValueObject(vo);
					String tableName = vo.getModelName();
					if(Sql_switcher.searchDbServer() == Constant.ORACEL
							&& ("b00".equalsIgnoreCase(tableName) || "k00".equalsIgnoreCase(tableName))) {
						String keyValue = "";
						String keyId = "";
						if("b00".equalsIgnoreCase(tableName)) {
							keyId = "b0110";
						} else if("k00".equalsIgnoreCase(tableName)) {
							keyId = "e01a1";
						}
						
						keyValue = vo.getString(keyId);
						String i9999 = vo.getString("i9999");
						updateMultimedia(dao, keyValue, i9999, tableName);
					}
				}
				
				voList.clear();
				this.multiMap.clear();
	   			
				/*ResultSetMetaData rsmd = this.frecset.getMetaData();
				orgsql.append("insert into "+fieldset.getFieldsetid()+" (");
				StringBuffer sb = new StringBuffer();
				sb.append("(");
				for(int i=1;i<=rsmd.getColumnCount();i++){
					orgsql.append(rsmd.getColumnName(i)+",");
					sb.append("?,");
				}
				orgsql.setLength(orgsql.length()-1);
				sb.setLength(sb.length()-1);
				orgsql.append(") values "+sb.toString()+")");
				while(this.frecset.next()){
					ArrayList arr = new ArrayList();
					for(int i=1;i<=rsmd.getColumnCount();i++){
							switch (rsmd.getColumnType(i)) {
							
							case Types.TINYINT:
							case Types.SMALLINT:
							case Types.INTEGER:
							case Types.BIGINT:
							case Types.FLOAT:
							case Types.DOUBLE:
							case Types.DECIMAL:
							case Types.NUMERIC:
							case Types.REAL:
							case Types.CHAR:
							case Types.VARCHAR:
							case Types.LONGVARCHAR:	
							case Types.CLOB:{
								arr.add(this.frecset.getString(i));
								break;
							}
							case Types.DATE:{
								arr.add(this.frecset.getDate(i));
								break;
							}
							case Types.TIME:{
								arr.add(this.frecset.getTime(i));
								break;
							}
							case Types.TIMESTAMP:{
								arr.add(this.frecset.getTimestamp(i));
								break;
							}
							default:
								arr.add(this.frecset.getObject(i));
								break;
							}
						
					}
					dao.insert(orgsql.toString(), arr);
				}*/
			}
		}
		List infoSetListPos=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
		for(int k=0;k<infoSetListPos.size();k++)
		{
			FieldSet fieldset=(FieldSet)infoSetListPos.get(k);
			//String temptable = "temp_"+fieldset.getFieldsetid()+"_"+this.userView.getUserName();
			if(version){
				/*// 删除临时表
				orgsql.delete(0, orgsql.length());
				orgsql.append("drop table ");
				orgsql.append(temptable);
				try {
					ExecuteSQL.createTable(orgsql.toString(), this.getFrameconn());
				} catch (Exception e) {
					// e.printStackTrace();
				}
				//创建临时表存过度数据 
				orgsql.delete(0, orgsql.length());
					switch (Sql_switcher.searchDbServer()) {//复制数据
						case Constant.MSSQL: {
							orgsql.append("select * into "+temptable);
							orgsql.append(" from "+fieldset.getFieldsetid()+" where ");
							orgsql.append("e01a1 like '"+fromcode+"%'");
							break;
						}
						case Constant.DB2: {
							
							break;
						}
						case Constant.ORACEL: {
							orgsql.append("create table "+temptable);
							orgsql.append(" as select * from "+fieldset.getFieldsetid()+" where ");
							orgsql.append("e01a1 like '"+fromcode+"%'");
							break;
						}
					}
					dao.update(orgsql.toString());
					*/
				
				//xuj 2010-4-9 改进的方式 频繁的创建表再删除的方式倒换数据效率慢
				//String sql = "select * from "+fieldset.getFieldsetid()+" where e01a1 like '"+fromcode+"%'";
				StringBuffer sql = new StringBuffer(); 
				sql.append("select * from "+fieldset.getFieldsetid());
				sql.append(" where e01a1 in (");
				sql.append("select codeitemid from "+temptable+" where ");
				sql.append(" codeitemid like '");
				sql.append(fromcode);
				sql.append("%' and codesetid='@K')");
				voList = this.getRecordVoList(sql.toString(), dao, fieldset.getFieldsetid());
				
				/*orgsql.append("select * ");
				orgsql.append(" from "+fieldset.getFieldsetid()+" where ");
				orgsql.append("e01a1 like '"+fromcode+"%'");
				this.frecset = dao.search(orgsql.toString());*/
			}
			if("K01".equalsIgnoreCase(fieldset.getFieldsetid())){
				
				orgsql.delete(0,orgsql.length());
				orgsql.append("update ");
				orgsql.append(fieldset.getFieldsetid());
				orgsql.append(" set e01a1='");
				orgsql.append(tocode);
				orgsql.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("e01a1",String.valueOf(fromcode.length() +1),Sql_switcher.length("e01a1")  + "-" + fromcode.length())) ;
				orgsql.append(" ,e0122='"+getTargetUMCodeitemid(tocode) +"'");
				//orgsql.append(" where e01a1 like '");
				//orgsql.append(fromcode);
				//orgsql.append("%'");
			}else{
				orgsql.delete(0,orgsql.length());
				orgsql.append("update ");
				orgsql.append(fieldset.getFieldsetid());
				orgsql.append(" set e01a1='");
				orgsql.append(tocode);
				orgsql.append("' " + SqlDifference.getJoinSymbol() + " " + Sql_switcher.substr("e01a1",String.valueOf(fromcode.length() +1),Sql_switcher.length("e01a1")  + "-" + fromcode.length())) ;
	
				//orgsql.append(" where e01a1 like '");
				//orgsql.append(fromcode);
				//orgsql.append("%'");
			}
			orgsql.append(", modtime="+PubFunc.DoFormatSystemDate(true));
			orgsql.append(" where e01a1 in (");
			orgsql.append("select codeitemid from "+temptable+" where ");
			orgsql.append(" codeitemid like '");
			orgsql.append(fromcode);
			orgsql.append("%' and codesetid='@K')");
			dao.update(orgsql.toString());
       		if(version){
				/*orgsql.delete(0, orgsql.length());//考回复制到临时表中的数据
				orgsql.append("insert into "+fieldset.getFieldsetid()+" select * from "+temptable);
				dao.update(orgsql.toString());
				// 删除临时表
				orgsql.delete(0, orgsql.length());
				orgsql.append("drop table ");
				orgsql.append(temptable);
				try {
					ExecuteSQL.createTable(orgsql.toString(), this.getFrameconn());
				} catch (Exception e) {
					// e.printStackTrace();
				}*/
       			
       		//xuj 2010-4-9 改进的方式 频繁的创建表再删除的方式倒换数据效率慢
				for(int i=0;i<voList.size();i++){
					RecordVo vo = (RecordVo)voList.get(i);
					dao.addValueObject(vo);
					String tableName = vo.getModelName();
					if(Sql_switcher.searchDbServer() == Constant.ORACEL
							&& ("b00".equalsIgnoreCase(tableName) || "k00".equalsIgnoreCase(tableName))) {
						String keyValue = "";
						String keyId = "";
						if("b00".equalsIgnoreCase(tableName)) {
							keyId = "b0110";
						} else if("k00".equalsIgnoreCase(tableName)) {
							keyId = "e01a1";
						}
						
						keyValue = vo.getString(keyId);
						String i9999 = vo.getString("i9999");
						updateMultimedia(dao, keyValue, i9999, tableName);
					}
				}
				voList.clear();
				this.multiMap.clear();
       			
       			/*orgsql.delete(0, orgsql.length());
				ResultSetMetaData rsmd = this.frecset.getMetaData();
				orgsql.append("insert into "+fieldset.getFieldsetid()+" (");
				StringBuffer sb = new StringBuffer();
				sb.append("(");
				for(int i=1;i<=rsmd.getColumnCount();i++){
					orgsql.append(rsmd.getColumnName(i)+",");
					sb.append("?,");
				}
				orgsql.setLength(orgsql.length()-1);
				sb.setLength(sb.length()-1);
				orgsql.append(") values "+sb.toString()+")");
				while(this.frecset.next()){
					ArrayList arr = new ArrayList();
					for(int i=1;i<=rsmd.getColumnCount();i++){
						arr.add(this.frecset.getObject(i));
					}
					dao.insert(orgsql.toString(), arr);
				}*/
			}
		}
	}

	/**
	 * 更新多媒体子集的大字段指标的值
	 * 
	 * @param dao
	 *            数据库链接
	 * @param keyValue
	 *            关键指标的值
	 * @param i9999
	 *            多媒体子集中某机构下的第几条记录
	 * @param tableName
	 *            多媒体子集id
	 */
	private void updateMultimedia(ContentDAO dao, String keyValue, String i9999, String tableName) {
		InputStream ole = null;
		try {
			String keyId = "";
			if("b00".equalsIgnoreCase(tableName)) {
				keyId = "b0110";
			} else if("k00".equalsIgnoreCase(tableName)) {
				keyId = "e01a1";
			}
			
			StringBuffer strSearch=new StringBuffer();
			strSearch.append("select ole from ");
			strSearch.append(tableName);
			strSearch.append(" where " + keyId + "='" + keyValue + "'");
			strSearch.append(" and i9999=" + i9999);
			strSearch.append(" FOR UPDATE");
			
			StringBuffer strInsert=new StringBuffer();
			strInsert.append("update ");
			strInsert.append(tableName);
			strInsert.append(" set ole=EMPTY_BLOB() where " + keyId + "='" + keyValue + "'");
			strInsert.append(" and i9999=" + i9999);
			
			ole = this.multiMap.get(keyValue + ":" + i9999);
			OracleBlobUtils blobutils=new OracleBlobUtils(this.frameconn);
			Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),ole);
			String sql = "update " + tableName + " set ole=? where " + keyId + "=? and i9999=?";
			ArrayList<Object> param = new ArrayList<Object>();
			param.add(blob);
			param.add(keyValue);
			param.add(i9999);
			dao.update(sql, param);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(ole);
		}
	}
	private String GetNextId(ContentDAO dao,String SrcCode,String DesCode,boolean ishavechild,String Level,String tranferorgid)
	{
		String strDesMaxChild="";
		String result="";
		if(ishavechild)
		{
	      strDesMaxChild=getMaxChildid(dao,DesCode);
		  cat.debug("------strDesMaxChild1------>" + strDesMaxChild);
     	}
		else
		{
		   strDesMaxChild=DesCode+BackLevLenStr(dao,tranferorgid,Integer.parseInt(Level));
		}
		 result=GetNextIdStr(SrcCode,DesCode,strDesMaxChild);
		 cat.debug("------strDesMaxChild result------>" + result);
		return result;
	}
	private String getMaxChildid(ContentDAO dao,String codeitemid)
	{
		 String maxchildid="";
		 StringBuffer sqlstr=new StringBuffer();
		  try{
	    	sqlstr.append("select codeitemid from organization where parentid='");
	    	sqlstr.append(codeitemid);
	    	sqlstr.append("' and codeitemid<>parentid");
	    	
	    	sqlstr.append(" union select codeitemid from vorganization where parentid='");
	    	sqlstr.append(codeitemid);
	    	sqlstr.append("' and codeitemid<>parentid order by codeitemid");
	    	
	    	this.frowset=dao.search(sqlstr.toString());
	    	while(this.frowset.next())
	    	{
	    		if(this.frowset.getString("codeitemid").compareTo(maxchildid)>0)
	    		{
	    			maxchildid=this.frowset.getString("codeitemid");	    			
	    		}
	    	}
	    }catch(Exception e)
		{
	    	e.printStackTrace();	    	
	    }
	    return maxchildid;
	}
	private String BackLevLenStr(ContentDAO dao,String SrcCode,int nLev)
	{
		//改变编码规则 2014-04-16 guodd 此方法被重写
		int I;
		String Result="";
		try{
			if(nLev==1)
			{
			  String strsql="select codeitemid from organization where Grade="+nLev;
			  this.frowset=dao.search(strsql);
		     if(this.frowset.next())
			  for(I=0;I<this.frowset.getString("codeitemid").length();I++)
			  {
			  	Result="0" + Result;
			  }
			}
			else
			{
			  String strsql="select parentid from organization where codeitemid='"+SrcCode+"'";
		      this.frowset=dao.search(strsql);
		      String StrParentId="";
		      if(this.frowset.next())
			    StrParentId=this.frowset.getString("parentid");
		      for(I=0;I<SrcCode.length()-StrParentId.length();I++)
		      	Result="0"+Result;	
			}
		}catch(Exception e){
		  e.printStackTrace();
		}
      return Result.length()==0?"01":Result;
	}	
	private String GetNextIdStr(String src,String des,String desMaxChild)
	{
		if(desMaxChild=="")  //如果是第一个子结点
		{
			return GetNext(src,des);
		}
		else
		{
			cat.debug("des max child --->" + desMaxChild +  "  des----->" + des);
			return GetNext(desMaxChild,des);
		}
	}	
	private String GetNext(String src,String des)
	{
		int nI,nTag;
		String ch;
		String result="";
		nTag=1;    //进位为1
		src=src.toUpperCase();
		for(nI=src.length();nI>des.length();nI--)
		{
			ch=src.substring(nI-1,nI);
			if(nTag==1)
			   ch=GetNextChar(ch);
			result=ch+result;
			if("0".equals(ch) && !"0".equals(src.subSequence(nI-1,nI)))
			{
				nTag=1;
			}
			else
			{
				nTag=0;
			}
			
		}	
		cat.debug("------ GetNext ----> "+ result);
		return des + result;
	}
	
	private String  GetNextChar(String ch)                   //获得下一个进位
	{
		String result="";
		switch(ch.charAt(0))
		{
			case '0':
			{
				result="1";
				break;
			}
			case '1':
			{
				result="2";
				break;
			}
			case '2':
			{
				result="3";
				break;
			}
			case '3':
			{
				result="4";
				break;
			}
			case '4':
			{
				result="5";
				break;
			}
			case '5':
			{
				result="6";
			   break;
			}
			case '6':
			{
				result="7";
				break;
			}
			case '7':
			{
				result="8";
				break;
			}
			case '8':
			{
				result="9";
				break;
			}
			case '9':
			{
				result="A";
				break;
			}
			case 'A':
			{
				result="B";
				break;
			}
			case 'B':
			{
				result="C";
				break;
			}
			case 'C':
			{
				result="D";
				break;
			}
			case 'D':
			{
				result="E";
				break;
			}
			case 'E':
			{
				result="F";
				break;
			}
			case 'F':
			{
				result="G";
				break;
			}
			case 'G':
			{
				result="H";
				break;
			}
			case 'H':
			{
				result="I";
				break;
			}
			case 'I':
			{
				result="J";
				break;
			}
			case 'J':
			{
				result="K";
				break;
			}
			case 'K':
			{
				result="L";
				break;
			}
			case 'L':
			{
				result="M";
				break;
			}
			case 'M':
			{
				result="N";
				break;
			}
			case 'N':
			{
				result="O";
				break;
			}
			case 'O':
			{
				result="P";
				break;
			}
			case 'P':
			{
				result="Q";
				break;
			}
			case 'Q':
			{
				result="R";
				break;
			}
			case 'R':
			{
				result="S";
				break;
			}
			case 'S':
			{
				result="T";
				break;
			}
			case 'T':
			{
				result="U";
				break;
			}
			case 'U':
			{
				result="V";
				break;
			}
			case 'V':
			{
				result="W";
				break;
			}
			case 'W':
			{
				result="X";
				break;
			}
			case 'X':
			{
				result="Y";
				break;
			}
			case 'Y':
			{
				result="Z";
				break;
			}
			case 'Z':
			{
				result="0";
				break;
			}
		}
	  return result;	
	}
	
    private String getTargetUNCodeitemid(String code) {
        String pre = "UM";
        String uncodeitemid = "";
        StringBuffer strsql = new StringBuffer();
        try {
            ContentDAO db = new ContentDAO(this.getFrameconn());
            while (!"UN".equalsIgnoreCase(pre)) {
                strsql.delete(0, strsql.length());
                strsql.append("select * from organization");
                strsql.append(" where codeitemid='");
                strsql.append(code);
                strsql.append("'");
                // 执行当前查询的sql语句
                this.frowset = db.search(strsql.toString()); 
                if (this.frowset.next()) {
                    pre = this.frowset.getString("codesetid");
                    code = this.frowset.getString("parentid");
                    if (!"UN".equalsIgnoreCase(pre))
                        uncodeitemid = code;
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return uncodeitemid;
    }

    private String getTargetUMCodeitemid(String code) {
        String pre = "@K";
        String uncodeitemid = "";
        StringBuffer strsql = new StringBuffer();
        try {
            ContentDAO db = new ContentDAO(this.getFrameconn());
            while ("@K".equalsIgnoreCase(pre)) {
                strsql.delete(0, strsql.length());
                strsql.append("select * from organization");
                strsql.append(" where codeitemid='");
                strsql.append(code);
                strsql.append("'");
                this.frowset = db.search(strsql.toString()); // 执行当前查询的sql语句
                if (this.frowset.next()) {
                    pre = this.frowset.getString("codesetid");
                    code = this.frowset.getString("parentid");
                    if ("@K".equalsIgnoreCase(pre))
                        uncodeitemid = code;
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        
        return uncodeitemid;
    }
	
	private ArrayList getRecordVoList(String sql,ContentDAO dao,String tablename) throws Exception {
		ArrayList voList=new ArrayList();
		try
	    {
	      DBMetaModel dbmeta = new DBMetaModel();
	      TableModel tableModel = dbmeta.searchTable(tablename.toLowerCase());
	      RowSet oSet = dao.search(sql);
	      ModelField[] fields = tableModel.getFields();
	      while(oSet.next()){
		      HashMap oMap = new HashMap();
		      for (int i = 0; i < fields.length; ++i)
		      {
		        if (fields[i].getFieldType() == 0)
		        {
		          if (oSet.getObject(fields[i].getTableField()) != null)
		          {
		            if (oSet.getObject(fields[i].getTableField()) instanceof Clob)
		            {
		              oMap.put(fields[i].getAttribute(), Sql_switcher.readMemo(oSet, fields[i].getTableField()));
		            }
		            else
		              oMap.put(fields[i].getAttribute(), oSet.getObject(fields[i].getTableField()));
		          }
		        }
		        else if ((fields[i].getFieldType() == 1) && 
		          (oSet.getObject(fields[i].getAttribute()) != null))
		        {
		          if (oSet.getObject(fields[i].getAttribute()) instanceof Clob)
		          {
		            oMap.put(fields[i].getAttribute(), Sql_switcher.readMemo(oSet, fields[i].getAttribute()));
		          }
		          else
		            oMap.put(fields[i].getAttribute(), oSet.getObject(fields[i].getAttribute()));
		        }
		      }
		      //orcle数据库中附件指标需要特殊处理
		      if(Sql_switcher.searchDbServer() == Constant.ORACEL
		    		  && ("b00".equalsIgnoreCase(tablename) || "k00".equalsIgnoreCase(tablename))) {
		    	  String keyId = "";
		    	  if("b00".equalsIgnoreCase(tablename)) {
		    		  keyId = "b0110";
		    	  } else if("k00".equalsIgnoreCase(tablename)) {
		    		  keyId = "e01a1";
		    	  }
		    	  
		    	  String keyValue = oSet.getString(keyId);
		    	  InputStream ole = oSet.getBinaryStream("ole");
		    	  String i9999 = oSet.getString("i9999");
		    	  this.multiMap.put(keyValue + ":" + i9999, ole);
		    	  oMap.remove("ole");
		    	  PubFunc.closeResource(ole);
			  }
		      
		      RecordVo ret = new RecordVo(tablename.toLowerCase());
		      ret.setValues(oMap);
		      voList.add(ret);
	      }
	    }
	    catch (SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw new SQLException("instantiate model class[" + tablename.toLowerCase() + "] failed");
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	      throw GeneralExceptionHandler.Handle(e);
	    }
		return voList;
	}
	
	/**
	 * 人员变动前的机构记录到选择的模板
	 * @throws GeneralException
	 */
	private void peopleOrgChange() throws GeneralException{
		try{
			String peopleOrg = (String) this.getFormHM().get("peopleOrg");
			ArrayList peopleOrgList = (ArrayList) this.getFormHM().get(
					"peopleOrgList");
			if (peopleOrg == null || "".equals(peopleOrg)
					|| peopleOrgList == null || peopleOrgList.size() == 0) {
				return;
			}
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
			String tempid = "";
			if ("combine".equalsIgnoreCase(peopleOrg)) {
				tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
						"combine");
				if (tempid == null || "".equals(tempid))
					return;
			} else if ("transfer".equalsIgnoreCase(peopleOrg)) {
				tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
						"transfer");
				if (tempid == null || "".equals(tempid))
					return;
			} else if ("bolish".equalsIgnoreCase(peopleOrg)) {
				tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
						"bolish");
				if (tempid == null || "".equals(tempid))
					return;
			}
			StringBuffer sql = new StringBuffer();
			ArrayList dblist = DataDictionary.getDbpreList();
			ContentDAO dao = new ContentDAO(this.frameconn);
			int nyear = 0;
			int nmonth = 0;
			nyear = DateUtils.getYear(new Date());
			nmonth = DateUtils.getMonth(new Date());
			RecordVo vo = new RecordVo("tmessage");
			vo.setString("username", "");
			vo.setInt("state", 0);
			vo.setInt("nyear", nyear);
			vo.setInt("nmonth", nmonth);
			vo.setInt("type", 0);
			vo.setInt("flag", 0);
			vo.setInt("sourcetempid", 0);
			vo.setInt("noticetempid", Integer.parseInt(tempid));
			StringBuffer changepre = new StringBuffer();
			StringBuffer change = new StringBuffer();
			for (int i = 0; i < peopleOrgList.size(); i++) {
				OrganizationView orgview = (OrganizationView) peopleOrgList
						.get(i);
				String codesetid = orgview.getCodesetid();
				String codeitemid = orgview.getCodeitemid();
				for (int n = 0; n < dblist.size(); n++) {
					String pre = (String) dblist.get(n);
					sql.setLength(0);
					sql.append("select a0100,a0101,b0110,e0122,e01a1 from "
							+ pre + "A01 where ");
					if ("UN".equalsIgnoreCase(codesetid)) {
						sql.append("b0110 like '" + codeitemid + "%'");
					} else if ("UM".equalsIgnoreCase(codesetid)) {
						sql.append("e0122 like '" + codeitemid + "%'");
					} else if ("@K".equalsIgnoreCase(codesetid)) {
						sql.append("e01a1 ='" + codeitemid + "'");
					}
					this.frowset = dao.search(sql.toString());
					vo.setString("db_type", pre);
					while (this.frowset.next()) {
						String a0100 = this.frowset.getString("a0100");
						String a0101 = this.frowset.getString("a0101");
						a0101 = a0101 != null ? a0101 : "";
						String b0110 = this.frowset.getString("b0110");
						String e0122 = this.frowset.getString("e0122");
						String e01a1 = this.frowset.getString("e01a1");
						vo.setString("a0100", a0100);
						vo.setString("a0101", a0101);
						changepre.setLength(0);
						change.setLength(0);
						if (b0110 != null && !"".equals(b0110)) {
							changepre.append("B0110=" + b0110 + ",");
							change.append("B0110,");
						}
						if (e0122 != null && !"".equals(e0122)) {
							changepre.append("E0122=" + e0122 + ",");
							change.append("E0122,");
						}
						if (e01a1 != null && !"".equals(e01a1)) {
							changepre.append("E01A1=" + e01a1 + ",");
							change.append("E01A1,");
						}
						if (a0101 != null && !"".equals(a0101)) {
							changepre.append("A0101=" + a0101 + ",");
							change.append("A0101,");
						}
						vo.setString("changepre", changepre.toString());
						vo.setString("change", change.toString());
						/** max id access mssql此字段是自增长类型 */
						if (Sql_switcher.searchDbServer() != Constant.MSSQL) {
							int nid = DbNameBo.getPrimaryKey("tmessage", "id",
									this.frameconn);
							vo.setInt("id", nid);
						}
						dao.addValueObject(vo);
					}
				}
			}
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	private void initLayer(){
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(SetLayerNull("organization"));
			dao.update(sql.toString());
			sql.delete(0,sql.length());
			sql.append(InitLayer("organization"));
			dao.update(sql.toString());
			sql.delete(0,sql.length());
			int i=1;
			while(true){
				sql.append(NextLayer("organization",i));
				int j = dao.update(sql.toString());
				if(j==0)
					break;
				i++;
				sql.delete(0,sql.length());
			}
			sql.delete(0,sql.length());
			sql.append(SetLayerNull("vorganization"));
			dao.update(sql.toString());
			sql.delete(0,sql.length());
			sql.append(InitLayer("vorganization"));
			dao.update(sql.toString());
			sql.delete(0,sql.length());
			i=1;
			while(true){
				sql.append(NextLayer("vorganization",i));
				int j = dao.update(sql.toString());
				if(j==0)
					break;
				i++;
				sql.delete(0,sql.length());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String SetLayerNull(String tbname){
		String sql = "update "+tbname+" set layer = null";
		return sql;
	}
	private String InitLayer(String tbname){
		String sql = "update "+tbname+" set layer=1 where (codeitemid=parentid) or "+
	    " codesetid<>(select codesetid  from "+tbname+" B where B.codeitemid="+tbname+".parentid)";
		return sql;
	}
	private String NextLayer(String tbname,int lay){
		String sql = "update "+tbname+" set layer='"+(lay+1)+"' where codeitemid<>parentid and "+
	       " parentid in (select codeitemid from "+tbname+" B where "+tbname+".codesetid=B.codesetid and B.layer='"+lay+"')";;
	    return sql;
	}
	/**
	 * 查询机构下有没有子机构
	 * @param codeitemid
	 * @return
	 */
	private boolean isExistCodeItemId(String codeitemid){
		boolean exist = false;
		
		ContentDAO dao = null;
		RowSet rs = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select COUNT(codeitemid) from organization where codeitemid like '"+codeitemid+"%'");
			
			dao = new ContentDAO(this.frameconn);
			rs = dao.search(sql.toString());
			while(rs.next() && rs.getInt(1) > 0){
				exist = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return exist;
	}
	
	/**
	 * 接收机构下（第一级也就是parendid是接收机构）子机构的最大levelA0000值   wangb    20170708
	 * @param transfercodeitemid 接收机构 codeitemid
	 * @return
	 * @throws GeneralException
	 */
	private String getMaxLevelA0000(String transfercodeitemid)throws GeneralException{
		String levelA0000 = "1";
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frowset=dao.search("SELECT MAX(LEVELA0000) as levelA0000 FROM organization where PARENTID='"+transfercodeitemid+"'");
			if(this.frowset.next())
				levelA0000 = String.valueOf(this.frowset.getInt("levelA0000") + 1);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return levelA0000;
	}
	
	
}
