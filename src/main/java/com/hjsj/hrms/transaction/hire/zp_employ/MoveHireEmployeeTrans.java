/*
 * Created on 2005-9-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_employ;

import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:MoveHireEmployeeTrans</p>
 * <p>Description:人员库移动</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 02, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class MoveHireEmployeeTrans extends IBusiness {
	public void execute() throws GeneralException {
		  ContentDAO dao=new ContentDAO(this.getFrameconn()); 
		  RecordVo rv= ConstantParamter.getRealConstantVo("ZP_DBNAME");
		  String userbase = rv.getString("str_value");
	      String touserbase=(String)this.getFormHM().get("userBase");
	      StructureExecSqlString sql=new StructureExecSqlString();
	      ArrayList moveinfodata=(ArrayList)this.getFormHM().get("selectedlist");
		  Connection conn = null;
		  Statement stmt = null;
		  StringBuffer strsql=new StringBuffer();
		  List fieldsetlist=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
	      try{
	    	conn = this.getFrameconn();
			//stmt = conn.createStatement();
			StringBuffer fieldstr=new StringBuffer();
			if(!moveinfodata.isEmpty()){
				for(int i=0;i<moveinfodata.size();i++)
				{				
					LazyDynaBean rec=(LazyDynaBean)moveinfodata.get(i);
					String A0100=rec.get("a0100").toString();
					String toTable=touserbase+"A01";
					String toA0100 = getToA0100(dao, A0100, toTable);	
					UpdateExistPersons(dao,rec.get("pos_id").toString());
					cat.debug("A0100--->" + A0100);
					if(!fieldsetlist.isEmpty()){
						for(int j=0;j<fieldsetlist.size();j++)
						{
							FieldSet fieldset=(FieldSet)fieldsetlist.get(j);
							List fields=DataDictionary.getFieldList(fieldset.getFieldsetid(),Constant.EMPLOY_FIELD_SET);
							fieldstr.delete(0,fieldstr.length());
							if(!fields.isEmpty())
							{
							  for(int n=0;n<fields.size();n++)
							  {
							  	FieldItem fielditem=(FieldItem)fields.get(n);
							  	fieldstr.append("," + fielditem.getItemid());
							  }
							 }
							strsql=sql.transferInformation(userbase+ fieldset.getFieldsetid(),touserbase + fieldset.getFieldsetid(),A0100,toA0100,fieldset.getFieldsetid(),fieldstr.toString(),this.getFrameconn());
							dao.update(strsql.toString());
							//stmt.executeUpdate(strsql.toString());
							strsql.setLength(0);
							//String ssql = "delete from zp_pos_tache where a0100 = '"+A0100+"'";
							//ArrayList useList = new ArrayList();
							//dao.update(ssql,useList);
						}
					}
					//conn.commit();
					this.getFormHM().put("toTable",toTable); 
					this.getFormHM().put("toA0100",toA0100); 
				}	
			}
	      }catch(Exception e)
			{
	      		e.printStackTrace();
	      	}finally{
	      		try{
	      			if(stmt != null){
	      				stmt.close();
	      			}
	      		}catch (SQLException cnfe) {
	      			System.out.println("SQLException:Exception in freeConn() ");
	      		}catch (Exception e) {
	      			e.printStackTrace();
	      		}   	  
	      	}
	   }
	
		/**
		 * @param stmt
		 * @param A0100
		 * @param toTable
		 * @return
		 * @throws SQLException
		 */
		//获得移库的目标id号
		private String getToA0100(ContentDAO dao, String A0100, String toTable) throws SQLException {
			String toA0100;
			String tempNumber;
			String tempsql =
				"select A0100 from "
					+ toTable
					+ " where A0100='"
					+ A0100
					+ "'";
			this.frowset = dao.search(tempsql);			
			if (this.frowset.next()) {
				String strsql = "select max(A0100) as a0100 from " + toTable + " order by A0100";
				this.frowset=dao.search(strsql);
				int userPlace = 0;
				if (this.frowset.next()) {
					userPlace =Integer.parseInt(this.frowset.getString("a0100")) + 1;
				} else{
					userPlace = 1;
				}
				tempNumber = Integer.toString(userPlace);
				for (int n = 0; n < 8 - (Integer.toString(userPlace)).length(); n++){
					tempNumber = "0" + tempNumber;
				}
			}	
			else {
				tempNumber = A0100;
			}
			toA0100=tempNumber;
			return toA0100;
		}
		/**
		 * @param conn
		 * @param posexistpersons
		 * @param 
		 * @return
		 * @throws SQLException
		 */
		//改变编制的实际人数id号
		private void UpdateExistPersons(ContentDAO dao,String pos_id) throws SQLException {	
			/*更改实有人员数*/
	    	RecordVo rv= ConstantParamter.getRealConstantVo("PS_WORKOUT");  
			if(rv == null){
				return;
			}else
			{
				StringBuffer sqlstr=new StringBuffer();
				String posWork = rv.getString("str_value");
				int strIndex = posWork.indexOf("|");
				if(strIndex != -1){
					 String setstr = posWork.substring(0,strIndex);
					 int fieldIndex = posWork.indexOf(",");
					 if(fieldIndex != -1){
					      String lastfieldstr = posWork.substring(fieldIndex+1,posWork.length());
				   	   	  this.frowset=dao.search("select " + lastfieldstr + " from " + setstr + " where e01a1='" + pos_id + "'");
				   	   	  sqlstr.delete(0,sqlstr.length());
				   	      sqlstr.append("update ");
				   	      sqlstr.append(setstr);
				   	      sqlstr.append(" set ");
				   	      sqlstr.append(lastfieldstr);
				   	      sqlstr.append("=");
				   	      if( this.frowset.next()) 
				   	      	if(this.frowset.getString(lastfieldstr)!=null)
				   	      	{
				   	          sqlstr.append(lastfieldstr);
		       	              sqlstr.append(" + ");		
				   	        }
				   	      sqlstr.append(1);
				   	      sqlstr.append(" where e01a1='");
				   	      sqlstr.append(pos_id);
				   	      sqlstr.append("'");
				   	      if(setstr.indexOf("01") == -1)
				   	      {
				   	        sqlstr.append(" and i9999=(select max(i9999) as i9999 from ");
				   	        sqlstr.append(setstr);
				   	        sqlstr.append(" where e01a1='");
				   	        sqlstr.append(pos_id);
				   	        sqlstr.append("')");
				   	      }
				   	      dao.update(sqlstr.toString());				   	  
				   	   }	
              }
		}
     }
}
