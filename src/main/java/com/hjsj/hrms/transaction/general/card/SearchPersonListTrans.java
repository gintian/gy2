/*
 * Created on 2006-5-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.card;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchPersonListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList persons=(ArrayList)this.getFormHM().get("persons");	
		String dbname=(String)this.getFormHM().get("dbname");
		String inforkind=(String)this.getFormHM().get("inforkind");
		StringBuffer sql=new StringBuffer();
		StringBuffer delsql=new StringBuffer();
		StringBuffer insersql=new StringBuffer();
		ArrayList personlist=new ArrayList();
		DbWizard db=new DbWizard(this.frameconn);
		boolean dbflag=true;//判断临时表是否存在
		String comSearch=(String)this.getFormHM().get("comSearch");//输入内容查询标记
		if(comSearch!=null&&"1".equals(comSearch)){
			String A0101=(String)this.getFormHM().get("A0101");
			persons=getPersonList(inforkind,A0101.trim(),dbname);
		}
		try{
		   boolean sysResultTable=userView.getStatus()==4||"6".equals(inforkind)/*基准岗位*/;	
		   if(!sysResultTable){
			if("1".equals(inforkind)){
				dbflag=db.isExistTable(this.userView.getUserName()+dbname+"Result", false);//查询结果插入临时表之前判断业务用户临时表是否存在 不存在时根据表规则创建临时表 changxy 25268  
				delsql.append("delete from ");
				delsql.append(this.userView.getUserName());	
				delsql.append(dbname);
				delsql.append("Result");
	        	insersql.append("insert into ");
	        	insersql.append(this.userView.getUserName());	
	        	insersql.append(dbname);
	        	insersql.append("Result");
	        	insersql.append("(a0100)");
	        	insersql.append("select ");
	        	insersql.append(dbname);
	        	insersql.append("A01.a0100 from ");
	        	insersql.append(dbname);
	        	insersql.append("A01 where ");
	        	insersql.append(dbname);
	        	insersql.append("a01.a0100 in (''");
	
				if(persons!=null && !persons.isEmpty())
				   for(int i=0;i<persons.size();i++)
				   {
					   insersql.append(",'");
					   insersql.append(persons.get(i).toString().substring(3));
					   insersql.append("'");	
				   }
				insersql.append(") order by ");
				insersql.append(dbname);
				insersql.append("a01.a0000");
				   
	        	
	        	
	        	
	        	
	        	
	        	
				sql.append("select ");
	        	sql.append(dbname);
	        	sql.append("A01.a0100,");
	        	sql.append(dbname);
	        	sql.append("A01.a0101 from ");
	        	sql.append(dbname);
	        	sql.append("A01 where ");
	        	sql.append(dbname);
	        	sql.append("a01.a0100 in (''");
	
				if(persons!=null && !persons.isEmpty())
				   for(int i=0;i<persons.size();i++)
				   {
				  		sql.append(",'");
				   		sql.append(persons.get(i).toString().substring(3));
				   		sql.append("'");	
				   }
				sql.append(") order by ");
	        	sql.append(dbname);
	        	sql.append("a01.a0000");
			}else if("2".equals(inforkind)) {
				dbflag=db.isExistTable(this.userView.getUserName()+"BResult", false);//查询结果插入临时表之前判断业务用户临时表是否存在 不存在时根据表规则创建临时表 changxy 25268  
				delsql.append("delete from ");
				delsql.append(this.userView.getUserName());	
				delsql.append("BResult");
	        	insersql.append("insert into ");
	        	insersql.append(this.userView.getUserName());	
	        	insersql.append("BResult");
	        	insersql.append("(b0110)");
	        	insersql.append("select organization.codeitemid from organization where ");
	        	insersql.append("organization.codeitemid in (''");
				if(persons!=null && !persons.isEmpty())
				   for(int i=0;i<persons.size();i++)
				   {
					   insersql.append(",'");
					   insersql.append(persons.get(i));
					   insersql.append("'");	
				   }
				insersql.append(")");
				insersql.append(" order by organization.codeitemid");
				
	        	sql.append("select organization.codeitemid,organization.codeitemdesc from organization where ");
	        	sql.append("organization.codeitemid in (''");
				if(persons!=null && !persons.isEmpty())
				   for(int i=0;i<persons.size();i++)
				   {
				  		sql.append(",'");
				   		sql.append(persons.get(i));
				   		sql.append("'");	
				   }
				sql.append(")");
				sql.append(" order by organization.codeitemid");//查询按照codeitemid 顺序排序【26286】
	        }    	
		    else if("4".equals(inforkind)){
				dbflag=db.isExistTable(this.userView.getUserName()+"KResult", false);//查询结果插入临时表之前判断业务用户临时表是否存在 不存在时根据表规则创建临时表 changxy 25268  
		    	delsql.append("delete from ");
				delsql.append(this.userView.getUserName());	
				delsql.append("KResult");
	        	insersql.append("insert into ");
	        	insersql.append(this.userView.getUserName());	
	        	insersql.append("KResult");
	        	insersql.append("(E01A1)");
	        	insersql.append("select organization.codeitemid from organization where ");       
	        	insersql.append("organization.codeitemid in (''");
			    if(persons!=null && !persons.isEmpty())
			    for(int i=0;i<persons.size();i++)
			    {
			    	insersql.append(",'");
			    	insersql.append(persons.get(i));
			    	insersql.append("'");	
			    }
			    insersql.append(")");
		    	
		    	
		    	sql.append("select organization.codeitemid,organization.codeitemdesc from organization where ");       
		        sql.append("organization.codeitemid in (''");
			    if(persons!=null && !persons.isEmpty())
			    for(int i=0;i<persons.size();i++)
			    {
			  		sql.append(",'");
			   		sql.append(persons.get(i));
			   		sql.append("'");	
			    }
			    sql.append(")");		  
			}
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			/***
			 * 业务用户如果没有临时表创建临时表
			 * changxy
			 * 
			 * */
			Table table=new Table("");
			if("1".equals(inforkind)&&!dbflag){
				table.setName(this.userView.getUserName()+dbname+"Result");
				Field a01fild=new Field("a0100","a0100");
				a01fild.setDatatype(DataType.STRING);
				a01fild.setLength(10);
				
				Field b01fild=new Field("b0110","b0110");
				b01fild.setDatatype(DataType.STRING);
				b01fild.setLength(30);
				table.addField(a01fild);
				table.addField(b01fild);
				db.createTable(table);
			}else if("2".equals(inforkind)&&!dbflag){
				table.setName(this.userView.getUserName()+"BResult");
				Field b01fild=new Field("b0110","b0110");
				b01fild.setDatatype(DataType.STRING);
				b01fild.setLength(30);
				table.addField(b01fild);
				db.createTable(table);
			}else if("4".equals(inforkind)&&!dbflag){
				table.setName(this.userView.getUserName()+"KResult");
				Field e01fild=new Field("e01a1","e01a1");
				e01fild.setDatatype(DataType.STRING);
				e01fild.setLength(30);
				table.addField(e01fild);
				db.createTable(table);
			}
				//if(dbflag)//没有临时表不执行删除
				dao.delete(delsql.toString(),new ArrayList());
				dao.insert(insersql.toString(),new ArrayList());
			this.frowset=dao.search(sql.toString());
		    int num=0;
			if("1".equals(inforkind))
	        {
	    	   while(this.frowset.next())
	    	  {
	    		 String person = this.frowset.getString("a0101");
	    		 if (person == null || person.length() == 0) {
	    			 person = "";
	    		 }
	    		 CommonData dataobj = new CommonData(this.frowset.getString("a0100"),person);
	    		 personlist.add(dataobj);
	    		 num++;
				 if(num>500)
					 break;
	    	  } 
	        }else if("2".equals(inforkind)) {
	        	 while(this.frowset.next())
	       	  {
	       		 CommonData dataobj = new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
	       		 personlist.add(dataobj);
	       		num++;
				if(num>500)
					break;
	       	  } 
	       }    	
		    else if("4".equals(inforkind)){
		    	String uplevel ="0";
			    if ("4".equals(inforkind)){
			    	Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
			    	uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
			    }
			    if(uplevel==null||uplevel.length()==0)
		    		uplevel="0";
		      while(this.frowset.next())
	         {
		    	  String codedesc=this.frowset.getString("codeitemdesc");
					String itemid=this.frowset.getString("codeitemid");
					if ("4".equals(inforkind)){
						codedesc=com.hrms.frame.utility.AdminCode.getCode("@K", itemid, Integer.parseInt(uplevel)).getCodename();
					}
		           CommonData dataobj = new CommonData(itemid,codedesc);
	       	   personlist.add(dataobj);
	           num++;
			   if(num>500)
				   break;
	         } 
		    }
		}else
		{   
			//自助用户
			String tabldName = "t_sys_result";
			Table table = new Table(tabldName);
			DbWizard dbWizard = new DbWizard(this.getFrameconn());
			if (!dbWizard.isExistTable(table)) {
				return;
			}
			/**flag=0人员, 1单位, 2岗位, 5基准岗位*/

			String flag="0";
			if("2".equalsIgnoreCase(inforkind))
			{
				flag="1";
			}
			else if("4".equalsIgnoreCase(inforkind))
			{
				flag="2";
			}
			else if("6".equalsIgnoreCase(inforkind))  // 基准岗位
			{
				flag="5";
			}			
			// 删除上次查询结果
			String str = "delete from " + tabldName+" where flag="+flag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'";
			if("1".equalsIgnoreCase(inforkind))
			{
				str+=" and UPPER(nbase)='"+dbname.toUpperCase()+"'";
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.delete(str, new ArrayList());
			StringBuffer buf_sql = new StringBuffer("");
			if ("1".equals(inforkind)) {
				//插入
				buf_sql.append("insert into " + tabldName);
				buf_sql.append("(username,nbase,obj_id,flag) ");
				buf_sql.append("select '"+userView.getUserName()+"' as username,'"+dbname.toUpperCase()+"' as nbase,");
				buf_sql.append(dbname+"A01.a0100,0 from ");				
				buf_sql.append(dbname);
				buf_sql.append("A01 where ");
				buf_sql.append(dbname);
				buf_sql.append("a01.a0100 in (''");	
				if(persons!=null && !persons.isEmpty())
				   for(int i=0;i<persons.size();i++)
				   {
					   buf_sql.append(",'");
					   buf_sql.append(persons.get(i).toString().substring(3));
					   buf_sql.append("'");	
				   }
				buf_sql.append(") order by ");
				buf_sql.append(dbname);
				buf_sql.append("a01.a0000");
				dao.insert(buf_sql.toString(),new ArrayList());
				//浏览
				sql.append("select ");
	        	sql.append(dbname);
	        	sql.append("A01.a0100 as codeitemid,");
	        	sql.append(dbname);
	        	sql.append("A01.a0101 as codeitemdesc from ");
	        	sql.append(dbname);
	        	sql.append("A01 where ");
	        	sql.append(dbname);
	        	sql.append("a01.a0100 in (");	
				sql.append("select obj_id from "+tabldName+" where ");
				sql.append(" flag="+flag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'");
				sql.append(" and UPPER(nbase)='"+dbname.toUpperCase()+"'");
				sql.append(") order by ");
	        	sql.append(dbname);
	        	sql.append("a01.a0000");
			} else if ("2".equals(inforkind)) {
				buf_sql.append("insert into " + tabldName + " (username,nbase,obj_id,flag) ");
				buf_sql.append("select '"+userView.getUserName()+"' as username,'B',organization.codeitemid,1 from organization where ");
				buf_sql.append("organization.codeitemid in (''");
				if(persons!=null && !persons.isEmpty())
				   for(int i=0;i<persons.size();i++)
				   {
					   buf_sql.append(",'");
					   buf_sql.append(persons.get(i));
					   buf_sql.append("'");	
				   }
				buf_sql.append(")");
				dao.insert(buf_sql.toString(),new ArrayList());
				sql.append("select organization.codeitemid,organization.codeitemdesc from organization where");	        	
	        	sql.append(" codeitemid in (");	
				sql.append("select obj_id from "+tabldName+" ");
				sql.append(" where flag="+flag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'");
				sql.append(" and UPPER(nbase)='B'");
				sql.append(") order by ");
	        	sql.append("organization");
	        	sql.append(".codeitemid");//登记表第一次进入时默认按照codeitemid 不是a0000 应与查询显示一致
			} else if ("4".equals(inforkind)) {
				buf_sql.append("insert into " + tabldName + " (username,nbase,obj_id,flag) ");
				buf_sql.append("select '"+userView.getUserName()+"' as username,'K',organization.codeitemid,2 from organization where ");
				buf_sql.append("organization.codeitemid in (''");
				if(persons!=null && !persons.isEmpty())
				   for(int i=0;i<persons.size();i++)
				   {
					   buf_sql.append(",'");
					   buf_sql.append(persons.get(i));
					   buf_sql.append("'");	
				   }
				buf_sql.append(")");
				dao.insert(buf_sql.toString(),new ArrayList());
				sql.append("select organization.codeitemid,organization.codeitemdesc from organization where");	        	
	        	sql.append(" codeitemid in (");	
				sql.append("select obj_id from "+tabldName+" ");
				sql.append(" where flag="+flag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'");
				sql.append(" and UPPER(nbase)='K'");
				sql.append(") order by ");
	        	sql.append("organization");
	        	sql.append(".a0000");
			} else if ("6".equals(inforkind)) {  // 基准岗位
				//插入
				buf_sql.append("insert into " + tabldName+"(username,nbase,obj_id,flag) ");
				buf_sql.append("select '"+userView.getUserName()+"' as username,'H' as nbase,H01.H0100,"+flag+" from H01 ");				
				buf_sql.append("where H01.H0100 in (''");	
				if(persons!=null && !persons.isEmpty())
				   for(int i=0;i<persons.size();i++)
				   {
					   buf_sql.append(",'");
					   buf_sql.append(persons.get(i));
					   buf_sql.append("'");	
				   }
				buf_sql.append(") order by H01.H0100");
				dao.insert(buf_sql.toString(),new ArrayList());
				//浏览
				String codeset=new CardConstantSet(userView, getFrameconn()).getStdPosCodeSetId();
				sql.append("select codeitemid,codeitemdesc from codeitem ");	        	
	        	sql.append("where codesetid='"+codeset+"' and codeitemid in (");	
				sql.append("select obj_id from "+tabldName+" ");
				sql.append(" where flag="+flag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'");
				sql.append(" and UPPER(nbase)='H'");
				sql.append(") order by a0000,codeitemid");
			}
			this.frowset=dao.search(sql.toString());
		    int num=0;
		    String uplevel ="0";
		    if ("4".equals(inforkind)){
		    	Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		    	uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
		    }
		    if(uplevel==null||uplevel.length()==0)
	    		uplevel="0";
			while(this.frowset.next())
	        {
				String codedesc=this.frowset.getString("codeitemdesc");
				String itemid=this.frowset.getString("codeitemid");
				if ("4".equals(inforkind)){
					codedesc=com.hrms.frame.utility.AdminCode.getCode("@K", itemid, Integer.parseInt(uplevel)).getCodename();
				}
	           CommonData dataobj = new CommonData(itemid,codedesc);
	       	   personlist.add(dataobj);
	           num++;
			   if(num>500)
				   break;
	        }
		}
		this.getFormHM().put("personlist",personlist);
		}catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);  
		}

	}
	public ArrayList getPersonList(String inforkind,String a0101,String userbase){
		String sql="";
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(this.frameconn);
		ArrayList objList=new ArrayList();
		try {
			String privsql=this.userView.getPrivSQLExpression(userbase, true).toUpperCase();
			privsql=privsql.substring(privsql.indexOf("WHERE")+6);
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一标识 唯一标识关联人员
			
			String backdate=DateUtils.format(new Date(), "yyyy-MM-dd");//单位 岗位 基准岗位 关联日期
			String codeValue=userView.getUnitPosWhereByPriv("codeitemid");
			if(a0101==null||a0101.length()<1)
				return null;
			if("1".equals(inforkind)){//人员
					if(onlyname!=null&&onlyname.length()>0){
					sql="select A0100,A0101,b0110,"+onlyname+" from "+userbase+"A01 where "+privsql+" and (A0101 like '%"+a0101+"%' or "+onlyname+" like '%"+a0101+"%' )" ;
				}else{
					sql="select A0100,A0101,b0110 from "+userbase+"A01 where "+privsql+" and A0101 like '%"+a0101+"%'"  ;
				}
			}else if("2".equals(inforkind)){//单位
				 sql = " select codeitemid,codeitemdesc from organization  where ( codesetid='UN' or codesetid='UM') and codeitemdesc like '%"+a0101+"%'" +
						" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and  "
				+ codeValue;
			}else if("4".equals(inforkind)){//岗位
				sql=" select codeitemid,codeitemdesc from organization  where codesetid='@K' and codeitemdesc like '%"+a0101+"%'" +
				" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and  "+ codeValue;
			}else if("6".equals(inforkind)){//基准岗位
				String codesetid="";
				com.hrms.frame.dao.RecordVo constantuser_vo = ConstantParamter.getRealConstantVo("PS_C_CODE");
	            if (constantuser_vo != null)
	                 codesetid = constantuser_vo.getString("str_value");
	            sql=" select codeitemid ,codeitemdesc" +
	            	"  from H01 right join codeitem  on codeitem.codeitemid=H01.H0100  " +
	            	"where codesetid='"+codesetid+"' and "+Sql_switcher.dateValue(backdate)+
	            	"between start_date and end_date  and codeitem.codeitemdesc like '%"+a0101+"%'";
				
			}
			rs=dao.search(sql);
			if("1".equals(inforkind)){//人员
				String A0100="";
				while(rs.next()){
					A0100=rs.getString("A0100");
					objList.add(userbase+A0100);
				}
				
			}else{
				String codeitemid="";
				while(rs.next()){
					codeitemid=rs.getString("codeitemid");
					objList.add(codeitemid);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return objList;
	}

}
