package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * @author lizhenwei
 *
 */
public class DataChangeCompareTrans extends IBusiness{
	
	public void execute() throws GeneralException {
		try
		{
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String salaryid = (String)hm.get("salaryid");
			String flow_flag=(String)hm.get("flow_flag");
			//PubFunc.FormatDate(rowSet.getDate("A00z2"), "yyyy-MM-dd");
			String gz_module=(String)hm.get("gz_module");
			String isVisible=(String)hm.get("isVisible");
			if(gz_module==null|| "".equalsIgnoreCase(gz_module))
				gz_module="0";
			int imodule=Integer.parseInt(gz_module);
			
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,gz_module);
			
			SalaryPkgBo pgkbo=new SalaryPkgBo(this.getFrameconn(),this.userView,imodule);
			//兼容 薪资类别共享功能  （dengcan 2008/6/26）
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),Integer.parseInt(salaryid));
			String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			String priv_mode=ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
			LazyDynaBean abean=null;
			if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
				abean=pgkbo.searchCurrentDate2(salaryid,this.userView.getUserName());
			else
				abean=pgkbo.searchCurrentDate2(salaryid,manager);
			
			String strYm=(String)abean.get("strYm");//业务日期
			String strC=(String)abean.get("strC");//次数
			//String field = getSelectField(salaryid);//比对指标串
			ArrayList fieldlist = this.getField(salaryid);//比对指标
			ArrayList fixedlist = this.getFixedField(salaryid);//固定指标
		    String tableName=this.createCompareTable(fieldlist, fixedlist);
		    String sutable ="";
		    if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
			    sutable=this.userView.getUserName()+"_salary_"+salaryid;
			else
			    sutable=manager+"_salary_"+salaryid;
		   // synchronized 是否要用同步操作
		    this.insertData(fieldlist, tableName, sutable, Integer.parseInt(strC), strYm, salaryid,isVisible,manager,priv_mode);
			this.getFormHM().put("changeflag", "0");
			this.getFormHM().put("isVisible",isVisible);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public String getSelectField(String salaryid){
		SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
		return ctrl_par.getValue(SalaryCtrlParamBo.COMPARE_FIELD);
	}
	public String createCompareTable(ArrayList fieldlist,ArrayList fixedlist)
	{
		String tableName = this.userView.getUserName()+"_gz_his_chg";
		try
		{
			DbWizard dbWizard=new DbWizard(this.getFrameconn());
			Table table=new Table(tableName);
    		table.setCreatekey(false);
    		Field temp = null;
    		for(int j=0;j<fixedlist.size();j++)
    		{
    			FieldItem item=(FieldItem)fixedlist.get(j);
    			temp = new Field(item.getItemid(),item.getItemdesc());
    			temp.setVisible(true);
    			temp.setKeyable(false);
    			temp.setNullable(true);
    			if("D".equalsIgnoreCase(item.getItemtype()))
    			{
    				temp.setDatatype(DataType.DATE);
    			}
    			else if("N".equalsIgnoreCase(item.getItemtype()))
    			{
    				if(item.getDecimalwidth()>0)
    				{
    					temp.setDatatype(DataType.FLOAT);
    				}
    				else
    				{
    					temp.setDatatype(DataType.INT);
    				}
    			}
    			/*else if(item.getItemtype().equalsIgnoreCase("M"))
    			{
    				temp_1.setDatatype(DataType.t)
    			}*/
    			else
    			{
    				temp.setDatatype(DataType.STRING);
    			}
    			temp.setLength(item.getItemlength());
    			temp.setSortable(true);
    			table.addField(temp);
    		}
    		Field temp_1=null;
    		Field temp_2=null;
    		for(int i=0;i<fieldlist.size();i++)
    		{
    			FieldItem item=(FieldItem)fieldlist.get(i);
    			temp_1 = new Field(item.getItemid()+"_1",item.getItemdesc());
    			temp_1.setVisible(true);
    			temp_1.setKeyable(false);
    			temp_1.setNullable(true);
    			temp_2 = new Field(item.getItemid()+"_2",item.getItemdesc());
    			temp_2.setVisible(true);
    			temp_2.setKeyable(false);
    			temp_2.setNullable(true);
    			if("D".equalsIgnoreCase(item.getItemtype()))
    			{
    				temp_1.setDatatype(DataType.DATE);
    				temp_2.setDatatype(DataType.DATE);
    			}
    			else if("N".equalsIgnoreCase(item.getItemtype()))
    			{
    				if(item.getDecimalwidth()>0)
    				{
    					temp_1.setDatatype(DataType.FLOAT);
    			    	temp_2.setDatatype(DataType.FLOAT);
    				}
    				else
    				{
    					temp_1.setDatatype(DataType.INT);
    					temp_2.setDatatype(DataType.INT);
    				}
    				temp_1.setDecimalDigits(item.getDecimalwidth());
    				temp_2.setDecimalDigits(item.getDecimalwidth());
    			}
    			/*else if(item.getItemtype().equalsIgnoreCase("M"))
    			{
    				temp_1.setDatatype(DataType.t)
    			}*/
    			else
    			{
    				temp_1.setDatatype(DataType.STRING);
    				temp_2.setDatatype(DataType.STRING);
    			}
    			temp_1.setLength(item.getItemlength());
    			temp_1.setSortable(true);
    			table.addField(temp_1);
    			temp_2.setLength(item.getItemlength());
    			temp_2.setSortable(true);
    			table.addField(temp_2);
    		}
    		temp_1 = new Field("changeflag","变化标识");
    		temp_1.setVisible(true);
			temp_1.setKeyable(false);
			temp_1.setNullable(true);
			temp_1.setDatatype(DataType.INT);
			table.addField(temp_1);
			if(dbWizard.isExistTable(table.getName(),false))
			{
				dbWizard.dropTable(table);
			}
			
			dbWizard.createTable(table);// table created
    		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return tableName;
	}
	/**
	 * 取得选择的比对指标列表
	 * @param salaryid
	 * @return
	 */
	public ArrayList getField(String salaryid){
		String rightvalue = getSelectField(salaryid);
		ArrayList list = new ArrayList();
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select itemid,itemdesc,itemlength,decwidth,codesetid,itemtype");
		sqlstr.append(" from salaryset where itemid in('");
		sqlstr.append(rightvalue.replaceAll(",","','").toUpperCase());
		sqlstr.append("') and salaryid ="+salaryid);
		
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sqlstr.toString());
			while(this.frowset.next()){
				FieldItem fielditem = new FieldItem();
				fielditem.setItemid(this.frowset.getString("itemid"));
				fielditem.setItemdesc(this.frowset.getString("itemdesc"));
				fielditem.setItemlength(this.frowset.getInt("itemlength"));
				fielditem.setDecimalwidth(this.frowset.getInt("decwidth"));
				fielditem.setCodesetid(this.frowset.getString("codesetid"));
				fielditem.setItemtype(this.frowset.getString("itemtype"));
				list.add(fielditem);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	public ArrayList getFixedField(String salaryid)
	{
		ArrayList list = new ArrayList();
		try
		{
			String fixedfield = "'NBASE','A0100','B0110','E0122','A00Z0','A0101','A0000','A00Z1','A00Z2','A00Z3'";
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select itemid,itemdesc,itemlength,decwidth,codesetid,itemtype");
			sqlstr.append(" from salaryset where upper(itemid) in(");
			sqlstr.append(fixedfield.toUpperCase());
			sqlstr.append(") and salaryid ="+salaryid);
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sqlstr.toString());
			while(this.frowset.next()){
				FieldItem fielditem = new FieldItem();
				fielditem.setItemid(this.frowset.getString("itemid"));
				fielditem.setItemdesc(this.frowset.getString("itemdesc"));
				fielditem.setItemlength(this.frowset.getInt("itemlength"));
				fielditem.setDecimalwidth(this.frowset.getInt("decwidth"));
				fielditem.setCodesetid(this.frowset.getString("codesetid"));
				fielditem.setItemtype(this.frowset.getString("itemtype"));
				list.add(fielditem);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public void insertData(ArrayList fieldlist,String tableName,String sutable,int strC,String strYM,String salaryid,String isVisible,String manager,String priv_mode)
	{
		RowSet rs = null;
	   try
		{
		    StringBuffer buf_1 = new StringBuffer();
		    StringBuffer buf_2 = new StringBuffer();
		    StringBuffer tempbuf_1 = new StringBuffer();
		    StringBuffer tempbuf_2 = new StringBuffer();
		    StringBuffer _buf_1 = new StringBuffer();
		    StringBuffer _buf_2 = new StringBuffer();
		    StringBuffer temp_buf_1 = new StringBuffer();
		    StringBuffer temp_buf_2 = new StringBuffer();
		    StringBuffer _temp = new StringBuffer();
		    StringBuffer noNum = new StringBuffer();
		    StringBuffer temp_buf = new StringBuffer();
		    StringBuffer _temp_buf = new StringBuffer();
		    StringBuffer chang_flag_1 = new StringBuffer();
		    StringBuffer chang_flag_2 = new StringBuffer();
		    StringBuffer column_buf_1 = new StringBuffer();
		    StringBuffer column_buf_2 = new StringBuffer();
		    StringBuffer temp_column_buf_1 = new StringBuffer();
		    StringBuffer temp_column_buf_2 = new StringBuffer();
		    for(int i=0;i<fieldlist.size();i++)
		    {
		    	FieldItem item = (FieldItem)fieldlist.get(i);

		    	if("N".equalsIgnoreCase(item.getItemtype())){//一个人发多次工资，数值型的求和后对比   zhaoxg 2013-11-28
			    	buf_1.append(",s."+item.getItemid()+" as "+item.getItemid()+"_1");
			    	buf_2.append(",t."+item.getItemid()+" as "+item.getItemid()+"_2");
			    	_buf_1.append(",sum("+item.getItemid()+") as "+item.getItemid()+"");
			    	_buf_2.append(",sum("+item.getItemid()+") as "+item.getItemid()+"");
		    	}else{
		    		if(Sql_switcher.searchDbServer()==2){
				    	tempbuf_1.append(",s."+item.getItemid()+" as "+item.getItemid()+"_1");
				    	tempbuf_2.append(",t."+item.getItemid()+" as "+item.getItemid()+"_2");
		    		}else{
				    	tempbuf_1.append(",s."+item.getItemid()/**+" as "+item.getItemid()+"_1"*/);
				    	tempbuf_2.append(",t."+item.getItemid()/**+" as "+item.getItemid()+"_2"*/);
		    		}

		    		temp_buf_1.append(","+item.getItemid()+"");
		    		temp_buf_2.append(","+item.getItemid()+"");
			    	_temp.append(","+item.getItemid()+"");//非求和的要加入group by字句中
		    	}

		    	if("N".equalsIgnoreCase(item.getItemtype()))
		    	{
		    		temp_buf.append(" or "+Sql_switcher.isnull("s."+item.getItemid(), "0")+" <> "+Sql_switcher.isnull("t."+item.getItemid(), "0"));
		    	}
		    	else{
		        	_temp_buf.append(" or s."+item.getItemid()+" <> t."+item.getItemid());
		    	}
		    	chang_flag_1.append(",'0' as "+item.getItemid()+"_1");
		    	chang_flag_2.append(",'0' as "+item.getItemid()+"_2");
		    	if("N".equalsIgnoreCase(item.getItemtype())){
			    	column_buf_1.append(","+item.getItemid()+"_1");
			    	column_buf_2.append(","+item.getItemid()+"_2");
		    	}else{
			    	temp_column_buf_1.append(","+item.getItemid()+"_1");
			    	temp_column_buf_2.append(","+item.getItemid()+"_2");
		    	}
		    }
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer buf = new StringBuffer();
//			导入信息变动的人员
			String flag =this.isHaveHistroy(salaryid, strYM, strC);
			String temp="";
			String atableName="salaryhistory";
			if(flag!=null&&!"".equals(flag))
			{
				String dd=flag.split("`")[0];
		    	String nn=flag.split("`")[1];
				temp=dd.substring(5,7);
				StringBuffer psq = new StringBuffer("select salaryid from salaryHistory where salaryid="+salaryid);
				psq.append(" and "+Sql_switcher.year("a00z2")+"="+dd.substring(0,4));
		    	if(temp.length()>0)
		    		 psq.append(" and "+Sql_switcher.month("a00z2")+"="+temp);
		    	psq.append(" and a00z3 ="+nn);
		    	rs = dao.search(psq.toString());
		    	if(rs.next())
		    	{
		    		 
		    	}else{
		    		 atableName="salaryarchive";
		    		 HistoryDataBo bo = new HistoryDataBo(this.getFrameconn());
					 bo.syncSalaryarchiveStrut();//同步表结构
		    	}
		    	StringBuffer sql1=new StringBuffer();//历史表或归档表的求和比对
		    	StringBuffer sql2=new StringBuffer();//当前薪资临时表的求和比对  zhaoxg 2013-11-28
		    	StringBuffer sql3=new StringBuffer();
		    	StringBuffer sql4=new StringBuffer();
		    	sql1.append("select A0100,A0101,A0000,a00z2,a00z3,salaryid,nbase,userflag");
		    	sql1.append(_buf_1.toString());
		    	sql1.append(" from "+atableName);
		    	sql1.append(" where salaryid = ");
		    	sql1.append(salaryid);
		    	sql1.append(" and "+Sql_switcher.year("a00z2")+"="+dd.substring(0,4));
		        if(temp.length()>0)
		        	sql1.append(" and "+Sql_switcher.month("a00z2")+"="+temp);
		        sql1.append(" and a00z3 ="+nn);
		        sql1.append(" group by A0100,A0101,A0000,a00z2,a00z3,salaryid,nbase,userflag");  //20140915 dengcan 少写了NBASE

		        //--------------------------------------------华丽的分割线-------------------------------
		    	sql2.append("select A0100,nbase");
		    	sql2.append(_buf_2.toString());
		    	sql2.append(" from "+sutable);
		    	sql2.append(" group by A0100,nbase"); //20140915 dengcan 少写了NBASE
		    	//-------------------------------------------------------------------------------------
		    	sql3.append("select NBASE,A0100,B0110,E0122,A0101,A0000,a00z2,a00z3,salaryid");
		    	sql3.append(temp_buf_1.toString());
		    	sql3.append(" from "+atableName+" a ");
		    	sql3.append(" where salaryid = ");
		    	sql3.append(salaryid);
		    	sql3.append(" and "+Sql_switcher.year("a00z2")+"="+dd.substring(0,4));
		        if(temp.length()>0)
		        	sql3.append(" and "+Sql_switcher.month("a00z2")+"="+temp);
		        sql3.append(" and a00z3 ="+nn);
		        sql3.append(" and a00z1=(select max(a00z1) from "+atableName+" b ");
		    	sql3.append(" where salaryid = ");
		    	sql3.append(salaryid);
		    	sql3.append(" and "+Sql_switcher.year("a00z2")+"="+dd.substring(0,4));
		        if(temp.length()>0)
		        	sql3.append(" and "+Sql_switcher.month("a00z2")+"="+temp);
		        sql3.append(" and a00z3 ="+nn);
		        sql3.append(" and a.a0100=b.a0100)");
		        //-------------------------------------------------------------------------------------
		    	sql4.append("select NBASE,A0100,B0110,E0122,A0101,A0000");
		    	sql4.append(temp_buf_2.toString());
		    	sql4.append(" from "+sutable+" a ");
		    	sql4.append(" where a00z1=(select max(a00z1) from "+sutable+" b where a.a0100=b.a0100)");

		    	
		    	buf.append(" insert into ");
		    	buf.append(tableName);
		    	buf.append("(A0100,A0101,changeflag,nbase"+column_buf_1.toString()+column_buf_2.toString()+")");
		    	buf.append(" select s.A0100,s.A0101,0 as changeflag,s.nbase as nbase ");//,s.A00Z1
		    	buf.append(buf_1.toString());
		    	buf.append(buf_2.toString());
	    		buf.append(" from ("+sql1+") s,("+sql2+") t where ");
		    	buf.append(" s.salaryid = ");
		    	buf.append(salaryid);
			    buf.append(" and "+Sql_switcher.year("s.a00z2")+"="+dd.substring(0,4));
		        if(temp.length()>0)
	                   buf.append(" and "+Sql_switcher.month("s.a00z2")+"="+temp);
	            buf.append(" and s.a00z3 ="+nn);			 
	            buf.append(" and s.a0100=t.a0100 and  lower(s.nbase)=lower(t.nbase) "); //20140915 dengcan 少写了NBASE
	            buf.append(" and (");
	            buf.append(temp_buf.toString().trim().substring(2));
    	        buf.append(")");
    	        

    	       
	            if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
	            {	 	        	 
	            }
	             /**非共享类别或者是管理员没有权限控制*/
	            else if(manager.trim().length()==0|| this.userView.getUserName().equalsIgnoreCase(manager))
	            {
	            	buf.append(" and s.userflag='"+this.userView.getUserName()+"' ");
	            }
	            else/**共享的类别，但是不是管理员*/
	            {
	            	 if("1".equalsIgnoreCase(priv_mode))
	            	 {
	                 	 String code=this.userView.getManagePrivCode();
	                 	 String value=this.userView.getManagePrivCodeValue();
	                	 if(code==null)
	                	 {
	                		 buf.append(" and 1=2 ");
	                	 }
	                	 else if("UN".equalsIgnoreCase(code))
	                	 {
	                		 buf.append(" and (s.b0110 like '");
	                		 buf.append((value==null?"":value)+"%'");
	            	    	 if(value==null)
	            		     {
	            	    		 buf.append(" or s.b0110 is null ");
	            	    	 }
	                    	 buf.append(")");
	                	 }
	                 	 else if("UM".equalsIgnoreCase(code))
	                	 {
	            	    	 buf.append(" and (s.e0122 like '");
	            	     	 buf.append((value==null?"":value)+"%'");
	            	    	 if(value==null)
	            	    	 {
	            	    		 buf.append(" or s.e0122 is null ");
	                 		 }
	                		 buf.append(")");
	                	 }
	                  }
	            }
	           dao.update(buf.toString());  
	           
	           buf.setLength(0);
	           if(Sql_switcher.searchDbServer()==2){
		           buf.append("update "+tableName+" set ");
		           buf.append("(NBASE,B0110,E0122,A0000"+temp_column_buf_1.toString()+")");
		           buf.append(" =(select s.NBASE,s.B0110,s.E0122,s.A0000 ");//,s.A00Z1
		           buf.append(tempbuf_1.toString());
		           buf.append(" from ("+sql3+") s  where ");
		           buf.append("s.a0100="+tableName+".a0100 and  lower(s.nbase)=lower("+tableName+".nbase))");
		           dao.update(buf.toString()); 
		           
		           buf.setLength(0);
		           buf.append("update "+tableName+" set ");
		           buf.append("(NBASE,B0110,E0122"+temp_column_buf_2.toString()+")");
		           buf.append(" =(select t.NBASE,t.B0110,t.E0122 ");//,s.A00Z1
		           buf.append(tempbuf_2.toString());
		           buf.append(" from ("+sql4+") t where ");
		           buf.append("t.a0100="+tableName+".a0100 and  lower(t.nbase)=lower("+tableName+".nbase))");
		           dao.update(buf.toString()); 
	           }else{		           
		           buf.append("update "+tableName+" set ");
		           buf.append(" NBASE=s.NBASE,B0110=s.B0110,E0122=s.E0122,A0000=s.A0000");
		           if(temp_column_buf_1.toString().length()>0){
		        	   String[] aa=temp_column_buf_1.toString().split(",");
		        	   String[] bb=tempbuf_1.toString().split(",");
		        	   for(int i=1;i<aa.length;i++){
		        		   buf.append(","+aa[i]+"="+bb[i]);
		        	   }
		           }
		           buf.append(" from ("+sql3+") s,"+tableName+" t  where ");
		           buf.append("s.a0100=t.a0100 and  lower(s.nbase)=lower(t.nbase)");
		           dao.update(buf.toString()); 
		           
		           buf.setLength(0);
		           buf.append("update "+tableName+" set ");
		           buf.append(" NBASE=s.NBASE,B0110=s.B0110,E0122=s.E0122,A0000=s.A0000");
		           if(temp_column_buf_2.toString().length()>0){
		        	   String[] aa=temp_column_buf_2.toString().split(",");
		        	   String[] bb=tempbuf_2.toString().split(",");
		        	   for(int i=1;i<aa.length;i++){
		        		   buf.append(","+aa[i]+"="+bb[i]);
		        	   }
		           }
		           buf.append(" from ("+sql4+") t,"+tableName+" s  where ");
		           buf.append("s.a0100=t.a0100  and  lower(s.nbase)=lower(t.nbase)");
		           dao.update(buf.toString()); 
	           }

			}
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * 历史数据中是否有该工资套的数据
	 * @param salaryid
	 * @return
	 */
	public String isHaveHistroy(String salaryid,String ywdate,int number)
	{
		String date = "";
		try
		{
			
			/**相同业务日期的上一次*/
			if(number>1)
			{
				String d=ywdate;
				int n= number-1;
				date = d+"`"+n;
				return date;
			}
			/**取上个业务日期的最后一次*/
			else
			{
				ywdate.replaceAll(".", "-");
				String[] date_arr = ywdate.split("-");
		    	StringBuffer sql = new StringBuffer();
		    	sql.append(" select "+Sql_switcher.dateToChar("a00z2","YYYY-MM-DD")+" as a00z2,a00z3 from gz_extend_log where id=");
			    sql.append(" (select max(id) from gz_extend_log where salaryid="+salaryid);
		    	sql.append(" and a00z2=(select MAX(a00z2) from gz_extend_log where salaryid="+salaryid+" and ");
			    sql.append(Sql_switcher.year("a00z2")+"="+date_arr[0]+" and "+Sql_switcher.month("a00z2")+"<"+date_arr[1]+") and ");
			    sql.append(" a00z3=(select max(a00z3) from gz_extend_log where salaryid="+salaryid+"");
			    sql.append(" and a00z2=(select MAX(a00z2) from gz_extend_log where salaryid="+salaryid+" and ");
			    sql.append(Sql_switcher.year("a00z2")+"="+date_arr[0]+" and "+Sql_switcher.month("a00z2")+"<"+date_arr[1]+")))");
			   
			    ContentDAO dao = new ContentDAO(this.getFrameconn());
			    this.frowset = dao.search(sql.toString());
			    boolean flag=false;
			    while(this.frowset.next())
			    {
			    	String d=this.frowset.getString("a00z2");
			    	String n=this.frowset.getString("a00z3");
			    	date=d+"`"+n;
			    	flag=true;
		    	}
			    if(!flag)
			    {
			    	sql.setLength(0);
			    	sql.append(" select "+Sql_switcher.dateToChar("a00z2","YYYY-MM-DD")+" as a00z2,a00z3 from gz_extend_log where id=");
				    sql.append(" (select max(id) from gz_extend_log where salaryid="+salaryid);
			    	sql.append(" and a00z2=(select MAX(a00z2) from gz_extend_log where salaryid="+salaryid+" and ");
				    sql.append(Sql_switcher.year("a00z2")+"<"+date_arr[0]+") and ");
				    sql.append(" a00z3=(select max(a00z3) from gz_extend_log where salaryid="+salaryid+"");
				    sql.append(" and a00z2=(select MAX(a00z2) from gz_extend_log where salaryid="+salaryid+" and ");
				    sql.append(Sql_switcher.year("a00z2")+"<"+date_arr[0]+")))");
				    this.frowset = dao.search(sql.toString());
				    while(this.frowset.next())
				    {
				    	String d=this.frowset.getString("a00z2");
				    	String n=this.frowset.getString("a00z3");
				    	date=d+"`"+n;
				    }
			    }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return date;
	}
	public String analyseDate(String date)
	{
		String str = "";
		try
		{
    		int year = Integer.parseInt(date.substring(0,4));
    		int month = Integer.parseInt(date.substring(5,7));
    		if(month==1)
    		{
	    		year = year-1;
    			month = 12;
    		}
	    	str = year+"-"+month;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public String getSalaryBd(String salaryid)
	{
		String str="";
		try
		{
			String sql = "select cbase from salarytemplate where salaryid = "+salaryid;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				str=rs.getString("cbase");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	

}
