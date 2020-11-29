package com.hjsj.hrms.transaction.general.query;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class HandworkSelectTrans extends IBusiness {
	
	
	
	

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList list=new ArrayList();
		try
		{
			String codeID=(String)this.getFormHM().get("codeid");			//1为选人 2为选单位 3为选部门 4为选择职位 5 单位`部门
			String codeItemID=(String)this.getFormHM().get("codeItemID");	//选取的值	
			String dbpre_arr=(String)this.getFormHM().get("dbpre_arr");
			boolean isUsr=false;   //是否有人员库的权限
			if("1".equals(codeID))
			{
				ArrayList dblist=userView.getPrivDbList(); 
				for(Iterator t=dblist.iterator();t.hasNext();)
				{
					String temp=((String)t.next()).toLowerCase();
					if(temp.equals(dbpre_arr.toLowerCase()))
					{
						isUsr=true;
						break;
					}	
				}
			}
			StringBuffer sql=new StringBuffer("");	
			if("1".equals(codeID))
			{		
					String codesetid="";
					String selectedCodeid="";
					if(codeItemID!=null&&!"root".equals(codeItemID)&&codeItemID.length()>=2){
						codesetid=codeItemID.substring(0,2);
						selectedCodeid=codeItemID.substring(2);
					}
					sql.append("select A0100,A0101  from "+dbpre_arr+"A01 where 1=1 and A0101 is not null ");
					if("UN".equalsIgnoreCase(codesetid))       //单位
					{
						sql.append(" and B0110 like '"+selectedCodeid+"%'");
					}
					else if("UM".equalsIgnoreCase(codesetid))  //部门
					{
						sql.append(" and E0122 like '"+selectedCodeid+"%'");
					}
					else if("@K".equalsIgnoreCase(codesetid))  //职位
					{
						sql.append(" and E01A1='"+selectedCodeid+"'");
					}
					//  权限控制 
					if(!userView.isSuper_admin()&&isUsr)
				     {
						String conditionSql=" select "+dbpre_arr+"A01.A0100 "+userView.getPrivSQLExpression(dbpre_arr,true);
						sql.append(" and "+dbpre_arr+"A01.A0100 in ("+conditionSql+" )");
				     }
					else if(!userView.isSuper_admin()&&!isUsr)
					{
						sql.append(" and 1=2 ");
					}
			}
			else 
			{
				String codeValue=this.userView.getUnitPosWhereByPriv("codeitemid"); //this.userView.getManagePrivCodeValue();
				//codeitemid like '
				
				/**
				 * cmq changed at 20121001 for 单位或岗位的权限范围控制
				 */
				String codeSetid="";
				if("2".equals(codeID))
					codeSetid=" ( codesetid='UN' or codesetid='UM' )  and  "+ codeValue; 
				else if("3".equals(codeID))
					codeSetid=" codesetid='UM'  and  "+ codeValue; 
				else if("4".equals(codeID))
					codeSetid=" codesetid='@K'  and  "+ codeValue; 
				else if("5".equals(codeID))
					codeSetid=" ( codesetid='UN' or codesetid='UM' )  and  "+ codeValue; 
				if("root".equals(codeItemID))
				{
					sql.append("select codeitemid,codeitemdesc,codesetid from organization  where "+codeSetid+" and codeitemdesc is not null");
					if("2".equals(codeID)|| "3".equals(codeID)|| "5".equals(codeID))
						sql.append(" and codeitemid in (select B0110 from B01 )");
					if("4".equals(codeID))
						sql.append(" and codeitemid in (select E01a1 from K01 )");
				}
				else
				{
					if(codeItemID.length()>=2&&("UN".equalsIgnoreCase(codeItemID.substring(0,2))|| "UM".equalsIgnoreCase(codeItemID.substring(0,2))|| "@K".equalsIgnoreCase(codeItemID.substring(0,2))))
						codeItemID=codeItemID.substring(2);
					sql.append("select codeitemid,codeitemdesc,codesetid from organization  where "+codeSetid+"  and codeitemdesc is not null and codeitemid like '"+codeItemID+"%'");
					if("2".equals(codeID)|| "3".equals(codeID)|| "5".equals(codeID))
						sql.append(" and codeitemid in (select B0110 from B01 where B0110 like '"+codeItemID+"%')");
					if("4".equals(codeID))
						sql.append(" and codeitemid in (select E01A1 from K01 where E01A1 like '"+codeItemID+"%')");
				}
				String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
				sql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
				sql.append(" order by a0000");
				
				
				
			}	
			
			this.frowset=dao.search(sql.toString());
			int countNum=0;
			while(this.frowset.next())
			{
				String codeid=this.frowset.getString(1);
				String codeName=this.frowset.getString(2)!=null?this.frowset.getString(2):"";
				if("5".equals(codeID))
				{
					if("UN".equalsIgnoreCase(this.frowset.getString(3)))
						codeName+="(单位)";
					else
						codeName+="(部门)";
				}
				CommonData dataobj = new CommonData(codeid,codeName);
				list.add(dataobj);
				if(countNum>1500)
					break;
				countNum++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);  
		}
		finally
		{
			this.getFormHM().clear();	
			this.getFormHM().put("fieldlist",list);
		}
		
	}

}
