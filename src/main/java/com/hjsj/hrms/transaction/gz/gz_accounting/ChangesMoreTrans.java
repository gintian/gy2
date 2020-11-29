package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lizhenwei
 *@version 4.0
  */
public class ChangesMoreTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
	
		String salaryid = (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		
		String bosdate = (String)hm.get("bosdate");
		bosdate=bosdate!=null&&bosdate.trim().length()>0?bosdate:"";
		
		String count = (String)hm.get("count");
		count=count!=null&&count.trim().length()>0?count:"1";
		
		ArrayList fieldlist = getField(salaryid);
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		if(createChange(dao,fieldlist,salaryid)){
			if(bosdate!=null&&count!=null&&!"".equals(bosdate)&&!"".equals(count))
			{
		    	String temp = this.getBeforeYWDate(salaryid, bosdate.replaceAll("\\.", "-"), Integer.parseInt(count));
		    	if(temp==null|| "".equals(temp))
		    	{
		    	   this.importAddPerson(this.userView.getUserName()+"_gz_his_chg",  bosdate, Integer.parseInt(count), fieldlist, salaryid);
		    	}
		    	else
		    	{
    		    	String dd=temp.split("`")[0];
    		    	String nn=temp.split("`")[1];
      		    	this.importDate(this.userView.getUserName()+"_gz_his_chg", dd, Integer.parseInt(nn), bosdate, Integer.parseInt(count), fieldlist, salaryid);
		    	}
		    }
			/*ArrayList listvalue = listValue(dao,fieldlist,salaryid,bosdate,count);
			insertChange(dao,fieldlist,listvalue);*/
		}
		
		
		hm.put("salaryid",salaryid);
		hm.put("changeflag","0");
		hm.put("isVisible","1");
	}
	/**
     * 新建表
     * @param dao 
     * @param fieldlist 新增字段
     * @param salaryid 薪资id
     */
	public boolean createChange(ContentDAO dao,ArrayList fieldlist,String salaryid){
		boolean check = true;
		try {
				    String tableName = this.userView.getUserName()+"_gz_his_chg";
					DbWizard dbWizard=new DbWizard(this.getFrameconn());
					Table table=new Table(tableName);
		    		table.setCreatekey(false);
		    		Field temp = null;
		    		ArrayList fixedlist=this.getFixedField(salaryid);
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return check;
	}
	/**
     * 新增信息
     * @param dao 
     * @param fieldlist 对比设置值
     * @param salaryid 薪资id
     * @param bosdate 日期
     * @param count 次数
     */
	public boolean insertChange(ContentDAO dao,ArrayList fieldlist,ArrayList listvalue){
		boolean check = true;
		StringBuffer insertstr = new StringBuffer();
		insertstr.append("insert into ");
		insertstr.append(this.userView.getUserName());
		insertstr.append("_gz_his_chg values(?,?,?,?,?,?,?,?,?,?,?");
		for(int i=0;i<fieldlist.size();i++){
			insertstr.append(",?");
			insertstr.append(",?");
		}
		insertstr.append(")");
		try {
			dao.batchInsert(insertstr.toString(),listvalue);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return check;
	}
	/**
     * 创建建表的sql语句
     * @param bosdate 日期
     * @param count 次数
     */
	public String createInfoSql(ContentDAO dao,ArrayList fieldlist,String salaryid){
		StringBuffer createstr = new StringBuffer();
		createstr.append("create table ");
		createstr.append(this.userView.getUserName());
		createstr.append("_gz_his_chg(");
		createstr.append("NBASE varchar(10),A0100 varchar(8),B0110 varchar(30),");
		createstr.append("E0122 varchar(40),A0101 varchar(12),A0000 int,");
		createstr.append("A00Z0 ");
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			createstr.append(" date ");
		else
			createstr.append(" datetime ");
		createstr.append(",A00Z1 int,A00Z2 ");
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			createstr.append(" date ");
		else
			createstr.append(" datetime ");
		createstr.append(",A00Z3 int,");
		createstr.append("ChangeFlag int,");
		for(int i=0;i<fieldlist.size();i++){
			FieldItem fielditem = (FieldItem)fieldlist.get(i);
			if("N".equalsIgnoreCase(fielditem.getItemtype())){
				String numtype = ""; 
				if(fielditem.getDecimalwidth()>0){
					numtype = "float";
				}else{
					numtype = "int";
				}
				createstr.append(fielditem.getItemid()+"_1 "+numtype+",");
				createstr.append(fielditem.getItemid()+"_2 "+numtype);
			}else if("A".equalsIgnoreCase(fielditem.getItemtype())){
				createstr.append(fielditem.getItemid()+"_1 ");
				createstr.append(" varchar(");
				createstr.append(fielditem.getItemlength()+"),");
				createstr.append(fielditem.getItemid()+"_2 ");
				createstr.append(" varchar(");
				createstr.append(fielditem.getItemlength()+")");
			}else if("D".equalsIgnoreCase(fielditem.getItemtype())){
				createstr.append(fielditem.getItemid()+"_1 ");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					createstr.append(" date,");
				else
					createstr.append(" datetime,");
				createstr.append(fielditem.getItemid()+"_2 ");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					createstr.append(" date,");
				else
					createstr.append(" datetime,");
			}else if("M".equalsIgnoreCase(fielditem.getItemtype())){
				createstr.append(fielditem.getItemid()+"_1 ");
				createstr.append(" text,");
				createstr.append(fielditem.getItemid()+"_2 ");
				createstr.append(" text ");
			}
			if(i+1<fieldlist.size()){
				createstr.append(",");
			}else{
				createstr.append(")");
			}
		}
		
		return createstr.toString();
	}
	/**
     * 获取插入值
     ** @param dao 
     * @param fieldlist 对比设置值
     * @param salaryid 薪资id
     * @param bosdate 日期
     * @param count 次数
     */
	public ArrayList listValue(ContentDAO dao,ArrayList fieldlist,String salaryid,String bosdate,String count){
		ArrayList list = new ArrayList();
		Date date = DateUtils.getSqlDate(bosdate,"yyyy.MM.dd");
		Date lastdate = DateUtils.getSqlDate(lastMonth(dao,bosdate),"yyyy.MM.dd");
		
		
		String checknum = checkNum(dao,bosdate,count);
		
		StringBuffer sqlstr = new StringBuffer();
		StringBuffer sqlstr1 = new StringBuffer();
		sqlstr.append("select a01z0,a0100,b0110,e0122,a0101,a0000,a00z0,a00z1,a00z2,a00z3");
		sqlstr1.append("select a01z0,a0100,b0110,e0122,a0101,a0000,a00z0,a00z1,a00z2,a00z3");
		for(int i=0;i<fieldlist.size();i++){
			FieldItem fielditem = (FieldItem)fieldlist.get(i);
			sqlstr.append(",(select ");
			sqlstr.append(fielditem.getItemid());
			sqlstr.append(" from salaryHistory a where ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				sqlstr.append(" to_char(a00z0,'YYYY.MM.DD')='");
			}
			else
			{
				sqlstr.append(" a00z0='");
			}
			
			sqlstr1.append(",(select ");
			sqlstr1.append(fielditem.getItemid());
			sqlstr1.append(" from salaryHistory a where ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				sqlstr1.append(" to_char(a00z0,'YYYY.MM.DD')='");
			}
			else
			{
				sqlstr1.append(" a00z0='");
			}
			if("1".equalsIgnoreCase(checknum)){
				sqlstr.append(date);
				sqlstr.append("'");
				sqlstr.append(" and a00z3=");
				sqlstr.append(Integer.parseInt(count)-1);
				
				sqlstr1.append(lastdate);
				sqlstr1.append("'");
				sqlstr1.append(" and a00z3=");
				sqlstr1.append(count);
			}else {
				sqlstr.append(lastdate);
				sqlstr.append("' and a00z3=(select max(a00z3) from salaryHistory where ");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sqlstr.append(" to_char(a00z0,'YYYY.MM.DD')='");
				}
				else
				{
					sqlstr.append(" a00z0='");
				}
				sqlstr.append(lastdate);
				sqlstr.append("' and a0101=a.a0101 and a0100=a.a0100 and salaryid='"+salaryid+"')");
				
				sqlstr1.append(date);
				sqlstr1.append("' and a00z3=(select max(a00z3) from salaryHistory where ");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sqlstr1.append(" to_char(a00z0,'YYYY.MM.DD')='");
				}
				else
				{
					sqlstr1.append(" a00z0='");
				}
				sqlstr1.append(date);
				sqlstr1.append("' and a0101=a.a0101 and a0100=a.a0100 and salaryid='"+salaryid+"')");
			}
			sqlstr.append(" and s.a0101=a.a0101 and s.a0100=a.a0100 and salaryid='"+salaryid+"') as ");
			sqlstr.append(fielditem.getItemid()+"_1");
			
			sqlstr.append(",");
			sqlstr.append(fielditem.getItemid());
			sqlstr.append("  as ");
			sqlstr.append(fielditem.getItemid()+"_2");
			
			sqlstr1.append(" and s.a0101=a.a0101 and s.a0100=a.a0100 and salaryid='"+salaryid+"') as ");
			sqlstr1.append(fielditem.getItemid()+"_1");
			
			sqlstr1.append(",");
			sqlstr1.append(fielditem.getItemid());
			sqlstr1.append("  as ");
			sqlstr1.append(fielditem.getItemid()+"_2");
		}
		sqlstr.append(",(select a0101 from salaryHistory a where ");
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		{
			sqlstr.append(" to_char(a00z0,'YYYY.MM.DD')='");
		}
		else
		{
			sqlstr.append(" a00z0='");
		}
		
		sqlstr1.append(",a0101 as a0101_1");
		sqlstr1.append(",(select a0101 from salaryHistory a where ");
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		{
			sqlstr1.append(" to_char(a00z0,'YYYY.MM.DD')='");
		}
		else
		{
			sqlstr1.append(" a00z0='");
		}
		if("1".equalsIgnoreCase(checknum)){
			sqlstr.append(date);
			sqlstr.append("'");
			sqlstr.append(" and a00z3=");
			sqlstr.append(Integer.parseInt(count)-1);
			
			sqlstr1.append(lastdate);
			sqlstr1.append("'");
			sqlstr1.append(" and a00z3=");
			sqlstr1.append(Integer.parseInt(count));
		}else {
			sqlstr.append(lastdate);
			sqlstr.append("' and a00z3=(select max(a00z3) from salaryHistory where ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				sqlstr.append(" to_char(a00z0,'YYYY.MM.DD')='");
			}
			else
			{
				sqlstr.append(" a00z0='");
			}
			sqlstr.append(lastdate);
			sqlstr.append("' and a0000=a.a0000 and salaryid='"+salaryid+"')");
			
			sqlstr1.append(date);
			sqlstr1.append("' and a00z3=(select max(a00z3) from salaryHistory where ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				sqlstr1.append(" to_char(a00z0,'YYYY.MM.DD')='");
			}
			else
			{
				sqlstr1.append(" a00z0='");
			}
			sqlstr1.append(date);
			sqlstr1.append("' and a0000=a.a0000 and salaryid='"+salaryid+"')");
		}
		sqlstr.append(" and s.a0100=a.a0100 and s.a0101=a.a0101 and salaryid='"+salaryid+"') as a0101_1");
		
		sqlstr.append(",a0101 as a0101_2");
		
		sqlstr.append(",(select a01z0 from salaryHistory a where");
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		{
			sqlstr.append(" to_char(a00z0,'YYYY.MM.DD')='");
		}
		else
		{
			sqlstr.append(" a00z0='");
		}
		
		sqlstr1.append(" and s.a0100=a.a0100 and s.a0101=a.a0101 and salaryid='"+salaryid+"') as a0101_2");
		
		
		
		sqlstr1.append(",(select a01z0 from salaryHistory a where ");
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		{
			sqlstr1.append(" to_char(a00z0,'YYYY.MM.DD')='");
		}
		else
		{
			sqlstr1.append(" a00z0='");
		}
		
		if("1".equalsIgnoreCase(checknum)){
			sqlstr.append(date);
			sqlstr.append("'");
			sqlstr.append(" and a00z3=");
			sqlstr.append(Integer.parseInt(count)-1);
			
			sqlstr1.append(lastdate);
			sqlstr1.append("'");
			sqlstr1.append(" and a00z3=");
			sqlstr1.append(Integer.parseInt(count));
		}else {
			sqlstr.append(lastdate);
			sqlstr.append("' and a00z3=(select max(a00z3) from salaryHistory where ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				sqlstr.append(" to_char(a00z0,'YYYY.MM.DD')='");
			}
			else
			{
				sqlstr.append(" a00z0='");
			}
			sqlstr.append(lastdate);
			sqlstr.append("' and a0000=a.a0000 and salaryid='"+salaryid+"')");
			
			
			sqlstr1.append(date);
			sqlstr1.append("' and a00z3=(select max(a00z3) from salaryHistory where ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				sqlstr1.append(" to_char(a00z0,'YYYY.MM.DD')='");
			}
			else
			{
				sqlstr1.append(" a00z0='");
			}
			sqlstr1.append(date);
			sqlstr1.append("' and a0000=a.a0000 and salaryid='"+salaryid+"')");
		}
		sqlstr.append(" and s.a0100=a.a0100 and s.a0101=a.a0101 and salaryid='"+salaryid+"') as a01z0_1");
		
		sqlstr.append(" from salaryHistory s where ");
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		{
			sqlstr.append(" to_char(a00z0,'YYYY.MM.DD')='");
		}
		else
		{
			sqlstr.append(" a00z0='");
		}
		sqlstr.append(date);
		sqlstr.append("'");
		sqlstr.append(" and a00z3=");
		sqlstr.append(count);
		sqlstr.append(" and salaryid='"+salaryid+"' ");
		
		sqlstr1.append(" and s.a0100=a.a0100 and s.a0101=a.a0101 and salaryid='"+salaryid+"') as a01z0_1");
		
		sqlstr1.append(" from salaryHistory s where ");
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		{
			sqlstr1.append(" to_char(a00z0,'YYYY.MM.DD')='");
		}
		else
		{
			sqlstr1.append(" a00z0='");
		}
		sqlstr1.append(lastdate);
		sqlstr1.append("'");
		sqlstr1.append(" and a00z3=");
		if("1".equalsIgnoreCase(checknum)){
			sqlstr1.append(Integer.parseInt(count)-1);
		} else {
			sqlstr1.append(Integer.parseInt(count));
		}
		sqlstr1.append(" and salaryid='"+salaryid+"' ");
		sqlstr1.append("and A0100 not in (select hou.A0100 from salaryHistory hou where ");
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		{
			sqlstr1.append(" to_char(a00z0,'YYYY.MM.DD')='");
		}
		else
		{
			sqlstr1.append(" a00z0='");
		}
		sqlstr1.append(""+date+"' and a00z3="+count+" and salaryid='"+salaryid+"')");
		
		String rightsql=" ( ( " + sqlstr + " ) UNION ( " + sqlstr1 + " ) ) order by a0000";
		try {
			this.frowset =  dao.search(rightsql.toString());
			int num=1;
			while(this.frowset.next()){
				
				//代表新增和减少人员，插入数据库标识。
				int biaoshi=0;
				
				ArrayList rslist = new ArrayList();
				rslist.add("Usr");
				rslist.add(a0100(num+""));
				rslist.add(this.frowset.getString("b0110"));
				rslist.add(this.frowset.getString("e0122"));
				rslist.add(this.frowset.getString("a0101"));
				rslist.add(this.frowset.getString("a0000"));
				rslist.add(this.frowset.getString("a00z0"));
				rslist.add(this.frowset.getString("a00z1"));
				rslist.add(this.frowset.getString("a00z2"));
				rslist.add(this.frowset.getString("a00z3"));
				
				String a01z0 = this.frowset.getString("a01z0");
				a01z0=a01z0!=null?a01z0:"1";
				
				String a01z0_1 = this.frowset.getString("a01z0_1");
				a01z0_1=a01z0_1!=null?a01z0_1:"1";
				
				String a0101_1 = this.frowset.getString("a0101_1");
				a0101_1=a0101_1!=null&&a0101_1.trim().length()>0?a0101_1:"";
				
				String a0101_2 = this.frowset.getString("a0101_2");
				a0101_2=a0101_2!=null&&a0101_2.trim().length()>0?a0101_2:"";
				if(a0101_1.length()<1){
					if(a0101_2.length()>1){
						biaoshi=1;
						rslist.add("1");
					}else{
						rslist.add("0");
					}
				}else if(a0101_2.length()<1){
					if(a0101_1.length()>1){
						biaoshi=1;
						rslist.add("2");
					}else{
						rslist.add("0");
					}
				}else{
					rslist.add("0");
				}
				int check=0;	
				for(int i=0;i<fieldlist.size();i++){
					FieldItem fielditem = (FieldItem)fieldlist.get(i);
					String item_1 = this.frowset.getString(fielditem.getItemid()+"_1");
					item_1=item_1!=null?item_1:"";
					String item_2 = this.frowset.getString(fielditem.getItemid()+"_2");
					item_2=item_2!=null?item_2:"";
					if("N".equalsIgnoreCase(fielditem.getItemtype())){
						if(item_1!=null&&item_1.trim().length()>0){
							float floatnum = Float.parseFloat(item_1);
							if(floatnum!=0&& "1".equals(a01z0_1)){
								rslist.add(item_1);
							}else{
								rslist.add(null);
								item_1="";
							}
						}else{
							rslist.add(null);
							item_1="";
						}
						if(item_2!=null&&item_2.trim().length()>0){
							float floatnum = Float.parseFloat(item_2);
							if(floatnum!=0&& "1".equals(a01z0)){
								rslist.add(item_2);
							}else{
								rslist.add(null);
								item_2="";
							}
						}else{
							rslist.add(null);
							item_2="";
						}
						if(!item_2.equalsIgnoreCase(item_1)){
							check=1;
						}
					}else{
						if("1".equals(a01z0_1)){
							rslist.add(item_1);
						}else{
							rslist.add(null);
							item_1="";
						}
						if("1".equals(a01z0)){
							rslist.add(item_2);
						}else{
							rslist.add(null);
							item_2="";
						}
						if(!item_2.equalsIgnoreCase(item_1)){
							check=1;
						}
					}
				}
				num++;
				if(check==1||biaoshi==1){
					list.add(rslist);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
     * 获取设置指标的属性
     * @param salaryid 
     */
	public ArrayList getField(String salaryid){
		String rightvalue = getvalue(salaryid);
		ArrayList list = new ArrayList();
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select itemid,itemdesc,itemlength,decwidth,codesetid,itemtype");
		sqlstr.append(" from salaryset where itemid in('");
		sqlstr.append(rightvalue.replaceAll(",","','"));
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
	/**
     * 获取对比设置的值
     * @param salaryid 
     */
	public String getvalue(String salaryid){
		SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
		return ctrl_par.getValue(SalaryCtrlParamBo.COMPARE_FIELD);
	}
	/**
     * 获取对比设置的值
     * @param datestr 
     */
	public String lastMonth(ContentDAO dao,String datestr){
		String[] arr = datestr.split("\\.");
		int year = Integer.parseInt(arr[0]);
		int month = Integer.parseInt(arr[1]);
		if(month<2){
			year=year-1;
			month=13;
		}
		String time="";
		String sql = "select max(a00z0) as a00z0 from salaryHistory where ";
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			sql+="to_char(a00z0,'YYYY.MM.DD')<'"+datestr+"'";
		else
			sql+="a00z0<'"+datestr+"'";
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				time=this.frowset.getDate("a00z0").toString();
			}
			if(time!=null&&time.trim().length()>1){
				time=time.substring(0,4)+"."+time.substring(5,7)+"."+time.substring(8,10);
			}else{
				time=year+"."+month+"."+"01";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time;
	}
	/**
     * 生成a0100
     * @param num 
     */
	public String a0100(String num){
		String a0100="";
		for(int i=0;i<8-num.trim().length();i++){
			a0100+="0";
		}
		
		return a0100+num;
	}
	
	/**
     * 检查是否有上一次记录或是上个业务日期的最后一次数据
     * @param dao 
     * @param bosdate 日期
     * @param count  次数
     * @return checknum 1、有上次数据，2、业务日期的最后一次数据 3、没有上次数据
     */
	public String checkNum(ContentDAO dao,String bosdate,String count ){
		String checknum = "1";
		int num = Integer.parseInt(count);
		Date lastdate = DateUtils.getSqlDate(lastMonth(dao,bosdate),"yyyy.MM.dd");
		
		StringBuffer sqlstr = new StringBuffer();
		if(num>1){
			checknum = "1";
		}else{
			sqlstr.append("select a0101");
			sqlstr.append(" from salaryHistory where");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				sqlstr.append(" to_char(a00z0,'YYYY.MM.DD')=");
			}
			else
			{
				sqlstr.append(" a00z0=");
			}
			sqlstr.append("'");
			sqlstr.append(lastdate);
			sqlstr.append("' and a0101=(select max(a0101) from salaryHistory where");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				sqlstr.append(" to_char(a00z0,'YYYY.MM.DD')='");
			}
			else
			{
				sqlstr.append(" a00z0='");
			}
			sqlstr.append(lastdate);
			sqlstr.append("')");
			checknum = "2";

			try {
				this.frowset = dao.search(sqlstr.toString());
				if(!this.frowset.next()){
					checknum = "3";
				}
			
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return checknum;
	}
	/**
	 * 取相同工资类别的上个业务日期和发放次数
	 * @param salaryid
	 * @param ywdate
	 * @return
	 */
	public String getBeforeYWDate(String salaryid,String ywdate,int number)
	{
		String date = "";
		RowSet rs=null;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{ 
			/**薪资类别*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			StringBuffer sql1=new StringBuffer("");
			int nn=0;
			while(true)
			{
				nn++;
				if(nn>=30)
				{
					date="";
					break;
				}
				/**相同业务日期的上一次*/
				if(number>1)
				{
					String d=ywdate;
					int n= number-1;
					date = d+"`"+n;
				//	return date;
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
				   
				    
				    rs = dao.search(sql.toString());
				    boolean flag=false;
				    while(rs.next())
				    {
				    	String d=rs.getString("a00z2");
				    	String n=rs.getString("a00z3");
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
					    rs = dao.search(sql.toString());
					    while(rs.next())
					    {
					    	String d=rs.getString("a00z2");
					    	String n=rs.getString("a00z3");
					    	date=d+"`"+n;
					    }
				    }
				}
				
				if(date.length()>0&&date.indexOf("`")!=-1)
				{
					String[] temps=date.split("`"); 
					sql1.setLength(0);
					sql1.append(" select count(A0100) ");
		    		sql1.append(" from salaryhistory T where T.salaryid="+salaryid+" and "+Sql_switcher.year("T.a00z2")+"=");
		    		sql1.append(temps[0].substring(0,4)+" and "+Sql_switcher.month("T.a00z2")+"="+temps[0].substring(5,7));
		    		sql1.append(" and T.a00z3="+temps[1]);
		    		sql1.append(" and (T.AppUser Like '%;"+this.userView.getUserName()+";%' or UPPER(T.curr_user)='"+this.userView.getUserName().toUpperCase()+"' or  (T.AppUser is null  "+gzbo.getPrivWhlStr("")+") )");//找上次我操作过的数据")
		    		rs=dao.search(sql1.toString());
		    		int count=0;
		    		if(rs.next())
		    			count=rs.getInt(1);
		    		if(count>0)
		    		{
		    			break;
		    		}
		    		else
		    		{
		    			ywdate=temps[0];
		    			number=Integer.parseInt(temps[1]);
		    		}
				}
				else
					break;
				
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
		return date;
	}

	/**
	 * 将变动数据导入临时表中
	 * @param temp_tablename
	 * @param beforedate
	 * @param number
	 * @param ywdate
	 * @param count
	 * @param fieldlist
	 * @param salaryid
	 */
	public void importDate(String temp_tablename,String beforedate,int number,String ywdate,int count,ArrayList fieldlist,String salaryid)
	{
		RowSet rs = null;
		try
		{
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			
			StringBuffer buf_1 = new StringBuffer();
			StringBuffer buf_2 = new StringBuffer();
			StringBuffer _buf_1 = new StringBuffer();
			StringBuffer _buf_2 = new StringBuffer();
			StringBuffer tempbuf_1 = new StringBuffer();
			StringBuffer tempbuf_2 = new StringBuffer();
			
			StringBuffer cloumn_1= new StringBuffer();
			StringBuffer cloumn_2 = new StringBuffer();
			StringBuffer _cloumn_1= new StringBuffer();
			StringBuffer _cloumn_2 = new StringBuffer();
			StringBuffer tempcloumn_1= new StringBuffer();
			StringBuffer tempcloumn_2 = new StringBuffer();
			
			StringBuffer c_1= new StringBuffer();
			StringBuffer c_2= new StringBuffer();
			StringBuffer tempc_1= new StringBuffer();
			StringBuffer tempc_2= new StringBuffer();
			
			StringBuffer w_1= new StringBuffer();
			StringBuffer chang_flag_1 = new StringBuffer();
			StringBuffer chang_flag_2 = new StringBuffer();
			StringBuffer _chang_flag_1 = new StringBuffer();
			StringBuffer _chang_flag_2 = new StringBuffer();
			StringBuffer tempchang_flag_1 = new StringBuffer();
			StringBuffer tempchang_flag_2 = new StringBuffer();
			StringBuffer sqlUpdate=new StringBuffer(temp_tablename+".a0100=T.a0100");
			StringBuffer sqlUpdate2=new StringBuffer(temp_tablename+".NBASE=S.NBASE,"+temp_tablename+".B0110=S.B0110,"+temp_tablename+".E0122=S.E0122,"+temp_tablename+".A0000=S.A0000");
			
			StringBuffer _temp = new StringBuffer();
			StringBuffer groupby = new StringBuffer();
			
			for( int i=0;i<fieldlist.size();i++)
			{
				FieldItem item = (FieldItem)fieldlist.get(i);
				_buf_1.append(","+item.getItemid()+" as "+item.getItemid()+"_1");
				_buf_2.append(","+item.getItemid()+" as "+item.getItemid()+"_2");
				_cloumn_1.append(","+item.getItemid()+"_1");
				_cloumn_2.append(","+item.getItemid()+"_2");
				_chang_flag_1.append(",'0' as "+item.getItemid()+"_1");
		    	_chang_flag_2.append(",'0' as "+item.getItemid()+"_2");
				if("N".equalsIgnoreCase(item.getItemtype())){//一个人发多次工资，数值型的求和后对比   zhaoxg 2013-11-28
					buf_1.append(",sum(T."+item.getItemid()+") as "+item.getItemid()+"_1");
					buf_2.append(",sum(S."+item.getItemid()+") as "+item.getItemid()+"_2");
					cloumn_1.append(","+item.getItemid()+"_1");
					cloumn_2.append(","+item.getItemid()+"_2");
					c_1.append(",TT."+item.getItemid()+"_1");
					c_2.append(",SS."+item.getItemid()+"_2");
					chang_flag_1.append(",'0' as "+item.getItemid()+"_1");
			    	chang_flag_2.append(",'0' as "+item.getItemid()+"_2");
				}else{
					tempbuf_1.append(",T."+item.getItemid()+" as "+item.getItemid()+"_1");
					tempbuf_2.append(",S."+item.getItemid()+" as "+item.getItemid()+"_2");
					tempcloumn_1.append(","+item.getItemid()+"_1");
					
					sqlUpdate.append(","+temp_tablename+"."+item.getItemid()+"_1=T."+item.getItemid());
					sqlUpdate2.append(","+temp_tablename+"."+item.getItemid()+"_2=S."+item.getItemid());
					
					tempcloumn_2.append(","+item.getItemid()+"_2");
					tempc_1.append(",TT."+item.getItemid()+"_1");
					tempc_2.append(",SS."+item.getItemid()+"_2");
					tempchang_flag_1.append(",'0' as "+item.getItemid()+"_1");
			    	tempchang_flag_2.append(",'0' as "+item.getItemid()+"_2");
					_temp.append(","+item.getItemid());//非求和的要加入group by字句中
				}

				if("N".equalsIgnoreCase(item.getItemtype())){
					w_1.append(" or "+Sql_switcher.isnull("TT."+item.getItemid()+"_1", "0")+" <> "+Sql_switcher.isnull("SS."+item.getItemid()+"_2", "0"));
				}



			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(beforedate!=null&&!"".equals(beforedate))
			{
				/**导入数据变动的人员*/
				String tableName="salaryhistory";
				StringBuffer psql = new StringBuffer();
				psql.append("select salaryid from salaryhistory where salaryid="+salaryid+" and "+Sql_switcher.year("a00z2")+"=");
				psql.append(beforedate.substring(0,4)+" and "+Sql_switcher.month("a00z2")+"="+beforedate.substring(5,7));
				psql.append(" and a00z3="+number);
				psql.append(" and (AppUser Like '%;"+this.userView.getUserName()+";%' or UPPER(curr_user)='"+this.userView.getUserName().toUpperCase()+"'  or  (AppUser is null  "+gzbo.getPrivWhlStr("")+") )");
				rs = dao.search(psql.toString());
				if(rs.next())
				{
					
				}else{//如果历史表中没有数据，找归档表
					tableName="salaryarchive";
					HistoryDataBo bo = new HistoryDataBo(this.getFrameconn());
					bo.syncSalaryarchiveStrut();//同步表结构
				}
				/**上个业务日期的数据*/
	    		StringBuffer sql1= new StringBuffer();
	    	//	sql1.append(" select A0100,A0101,A0000"+buf_1.toString()); 
	    		sql1.append(" select A0100,A0101,A0000,NBASE"+buf_1.toString()); //20141121 DENGCAN
	    		sql1.append(" from "+tableName+" T where T.salaryid="+salaryid+" and "+Sql_switcher.year("T.a00z2")+"=");
	    		sql1.append(beforedate.substring(0,4)+" and "+Sql_switcher.month("T.a00z2")+"="+beforedate.substring(5,7));
	    		sql1.append(" and T.a00z3="+number);
	    		sql1.append(" and (T.AppUser Like '%;"+this.userView.getUserName()+";%' or UPPER(T.curr_user)='"+this.userView.getUserName().toUpperCase()+"'  or  (T.AppUser is null  "+gzbo.getPrivWhlStr("")+") )");//找上次我操作过的数据")
	    	//	groupby.append(" group by A0100,A0101,A0000");
	    		groupby.append(" group by A0100,A0101,A0000,NBASE");  //20141121 DENGCAN
				/**上个业务日期的非数值数据*/
	    		StringBuffer sql3= new StringBuffer();
	    	//	sql3.append(" select A0100"+_temp.toString());
	    		sql3.append(" select A0100,NBASE"+_temp.toString()); //20141121 DENGCAN
	    		sql3.append(" from "+tableName+" a where salaryid="+salaryid+" and "+Sql_switcher.year("a00z2")+"=");
	    		sql3.append(beforedate.substring(0,4)+" and "+Sql_switcher.month("a00z2")+"="+beforedate.substring(5,7));
	    		sql3.append(" and a00z3="+number);
	    		sql3.append(" and (AppUser Like '%;"+this.userView.getUserName()+";%' or UPPER(curr_user)='"+this.userView.getUserName().toUpperCase()+"'  or  (AppUser is null  "+gzbo.getPrivWhlStr("")+") )");
	    		sql3.append(" and a00z1=(select max(a00z1) from "+tableName+" b where salaryid="+salaryid+" and "+Sql_switcher.year("a00z2")+"=");
	    		sql3.append(beforedate.substring(0,4)+" and "+Sql_switcher.month("a00z2")+"="+beforedate.substring(5,7));
	    		sql3.append(" and a00z3="+number);
	    		sql3.append(" and (AppUser Like '%;"+this.userView.getUserName()+";%' or UPPER(curr_user)='"+this.userView.getUserName().toUpperCase()+"'  or  (AppUser is null  "+gzbo.getPrivWhlStr("")+")  )");
	    	//	sql3.append(" and a.a0100=b.a0100)");
	    		sql3.append(" and a.a0100=b.a0100 AND a.nbase=b.nbase)");
	    		/**当前业务日期的数据 一定在历史表中，而不是在归档表中*/
	    		StringBuffer sql2= new StringBuffer();
	    //		sql2.append(" select A0100,A0101,A0000"+buf_2.toString());
	    		sql2.append(" select A0100,A0101,A0000,NBASE"+buf_2.toString()); //20141121 DENGCAN
	    		sql2.append(" from salaryhistory S where S.salaryid="+salaryid+" and "+Sql_switcher.year("S.a00z2")+"=");
	    		sql2.append(ywdate.substring(0,4)+" and "+Sql_switcher.month("S.a00z2")+"="+ywdate.substring(5,7));
	    		sql2.append(" and S.a00z3="+count);
	    		sql2.append(" and (S.AppUser Like '%;"+this.userView.getUserName()+";%' or UPPER(S.curr_user)='"+this.userView.getUserName().toUpperCase()+"'  or  (S.AppUser is null  "+gzbo.getPrivWhlStr("")+")  )");
	    		//sql2.append(" group by A0100,A0101,A0000");
	    		/**当前业务日期的数据 一定在历史表中，而不是在归档表中*/
	    		StringBuffer sql4= new StringBuffer();
	    		sql4.append(" select NBASE,B0110,E0122,A0000,a0100"+_temp.toString());
	    		sql4.append(" from salaryhistory a where salaryid="+salaryid+" and "+Sql_switcher.year("a00z2")+"=");
	    		sql4.append(ywdate.substring(0,4)+" and "+Sql_switcher.month("a00z2")+"="+ywdate.substring(5,7));
	    		sql4.append(" and a00z3="+count);
	    		sql4.append(" and (AppUser Like '%;"+this.userView.getUserName()+";%' or UPPER(curr_user)='"+this.userView.getUserName().toUpperCase()+"'   or  (AppUser is null  "+gzbo.getPrivWhlStr("")+") )");
	    		sql4.append(" and a00z1=(select max(a00z1) from "+tableName+" b where salaryid="+salaryid+" and "+Sql_switcher.year("a00z2")+"=");
	    		sql4.append(ywdate.substring(0,4)+" and "+Sql_switcher.month("a00z2")+"="+ywdate.substring(5,7));
	    		sql4.append(" and a00z3="+count);
	    		sql4.append(" and (AppUser Like '%;"+this.userView.getUserName()+";%' or UPPER(curr_user)='"+this.userView.getUserName().toUpperCase()+"'  or  (AppUser is null  "+gzbo.getPrivWhlStr("")+") )");
	    	//	sql4.append(" and a.a0100=b.a0100)");
	    		sql4.append(" and a.a0100=b.a0100 and a.nbase=b.nbase)");  //20141121 DENGCAN
	    		
	    		StringBuffer sql5= new StringBuffer();
	    		sql5.append(" select NBASE,A0100,B0110,E0122,A00Z0,A0101,A0000,A00Z1,A00Z2,A00Z3"+_buf_1.toString());
	    		sql5.append(" from "+tableName+" a where salaryid="+salaryid+" and "+Sql_switcher.year("a00z2")+"=");
	    		sql5.append(beforedate.substring(0,4)+" and "+Sql_switcher.month("a00z2")+"="+beforedate.substring(5,7));
	    		sql5.append(" and a00z3="+number);
	    		sql5.append(" and (AppUser Like '%;"+this.userView.getUserName()+";%' or UPPER(curr_user)='"+this.userView.getUserName().toUpperCase()+"'  or  (AppUser is null  "+gzbo.getPrivWhlStr("")+") )");//找上次我操作过的数据")
	    		sql5.append(" and a00z1=(select max(a00z1) from "+tableName+" b where salaryid="+salaryid+" and "+Sql_switcher.year("a00z2")+"=");
	    		sql5.append(beforedate.substring(0,4)+" and "+Sql_switcher.month("a00z2")+"="+beforedate.substring(5,7));
	    		sql5.append(" and a00z3="+number);
	    		sql5.append(" and (AppUser Like '%;"+this.userView.getUserName()+";%' or UPPER(curr_user)='"+this.userView.getUserName().toUpperCase()+"'  or  (AppUser is null  "+gzbo.getPrivWhlStr("")+") )");
	    	//	sql5.append(" and a.a0100=b.a0100)");
	    		sql5.append(" and a.a0100=b.a0100 and a.nbase=b.nbase)");  //20141121 DENGCAN
	    		/**当前业务日期的数据 一定在历史表中，而不是在归档表中*/
	    		StringBuffer sql6= new StringBuffer();
	    		sql6.append(" select NBASE,A0100,B0110,E0122,A00Z0,A0101,A0000,A00Z1,A00Z2,A00Z3"+_buf_2.toString());
	    		sql6.append(" from salaryhistory a where salaryid="+salaryid+" and "+Sql_switcher.year("a00z2")+"=");
	    		sql6.append(ywdate.substring(0,4)+" and "+Sql_switcher.month("a00z2")+"="+ywdate.substring(5,7));
	    		sql6.append(" and a00z3="+count);
	    		sql6.append(" and (AppUser Like '%;"+this.userView.getUserName()+";%' or UPPER(curr_user)='"+this.userView.getUserName().toUpperCase()+"'  or  (AppUser is null  "+gzbo.getPrivWhlStr("")+") )");
	    		sql6.append(" and a00z1=(select max(a00z1) from "+tableName+" b where salaryid="+salaryid+" and "+Sql_switcher.year("a00z2")+"=");
	    		sql6.append(ywdate.substring(0,4)+" and "+Sql_switcher.month("a00z2")+"="+ywdate.substring(5,7));
	    		sql6.append(" and a00z3="+count);
	    		sql6.append(" and (AppUser Like '%;"+this.userView.getUserName()+";%' or UPPER(curr_user)='"+this.userView.getUserName().toUpperCase()+"'  or  (AppUser is null  "+gzbo.getPrivWhlStr("")+") )");
	    	//	sql6.append(" and a.a0100=b.a0100)");
	    		sql6.append(" and a.a0100=b.a0100 and a.nbase=b.nbase)");  //20141121 DENGCAN
	    		/**导入数据*/
	    		StringBuffer buf = new StringBuffer();
	    		buf.append(" insert into "+temp_tablename+"(");
	//    		buf.append("A0100,A0101,A0000,changeflag"+cloumn_1.toString()+cloumn_2.toString()+")");
	//    		buf.append("select TT.A0100,TT.A0101,TT.A0000,0 as changeflag"+c_1.toString()+c_2.toString());
	    		buf.append("A0100,A0101,A0000,NBASE,changeflag"+cloumn_1.toString()+cloumn_2.toString()+")");  //20141121 DENGCAN
	    		buf.append("select TT.A0100,TT.A0101,TT.A0000,TT.NBASE,0 as changeflag"+c_1.toString()+c_2.toString());  //20141121 DENGCAN
	    		
	    		buf.append(" from ("+sql1.toString()+groupby+") TT,("+sql2.toString()+groupby+") SS where ");
	//    		buf.append(" TT.a0100=SS.a0100  ");
	    		buf.append(" TT.a0100=SS.a0100 AND UPPER(TT.nbase)=UPPER(SS.nbase) ");  //20141121 DENGCAN
	    		
	    		buf.append(" and ("+w_1.toString().trim().substring(2)+")");
	    		dao.update(buf.toString());
	    		
		        buf.setLength(0);
		        if(Sql_switcher.searchDbServer()!=2)  //不为oracle
		        {
		        	 buf.append("update "+temp_tablename+" set "+sqlUpdate.toString()); 
				 //  buf.append(" from "+temp_tablename+" left join ("+sql3+") T on T.a0100="+temp_tablename+".a0100"); 
		        	 buf.append(" from "+temp_tablename+" left join ("+sql3+") T on T.a0100="+temp_tablename+".a0100 and UPPER(T.nbase)=UPPER("+temp_tablename+".nbase) ");   //20141121 DENGCAN 
		        }
		        else
		        {
			        buf.append("update "+temp_tablename+" set "); 
			        buf.append("(a0100"+tempcloumn_1.toString()+")"); 
			        buf.append(" =(select T.a0100");//,s.A00Z1
			        buf.append(tempbuf_1.toString());
			        buf.append(" from ("+sql3+") T  where ");
			     //   buf.append("T.a0100="+temp_tablename+".a0100)");
			        buf.append("T.a0100="+temp_tablename+".a0100  and UPPER(T.nbase)=UPPER("+temp_tablename+".nbase) )"); //20141121 DENGCAN
			        
			      
		        }
		        dao.update(buf.toString()); 
	    		
		        buf.setLength(0);
		        if(Sql_switcher.searchDbServer()!=2)  //不为oracle
		        {
		        	 buf.append("update "+temp_tablename+" set "+sqlUpdate2.toString()); 
				//   buf.append(" from "+temp_tablename+" left join ("+sql4+") S on S.a0100="+temp_tablename+".a0100");
		        	 buf.append(" from "+temp_tablename+" left join ("+sql4+") S on S.a0100="+temp_tablename+".a0100  and UPPER(S.nbase)=UPPER("+temp_tablename+".nbase) ");   //20141121 DENGCAN 
		        }
		        else
		        {
			        buf.append("update "+temp_tablename+" set ");
			        buf.append("(NBASE,B0110,E0122,A0000"+tempcloumn_2.toString()+")");
			        buf.append(" =(select S.NBASE,S.B0110,S.E0122,S.A0000 ");//,s.A00Z1
			        buf.append(tempbuf_2.toString());
			        buf.append(" from ("+sql4+") S  where ");
			  //    buf.append("S.a0100="+temp_tablename+".a0100)");
			        buf.append("S.a0100="+temp_tablename+".a0100 and UPPER(S.nbase)=UPPER("+temp_tablename+".nbase) )");
		        }
		        dao.update(buf.toString()); 
	    		/**数据变动人员导入完毕*/
	    		/**导入新增*/
	    		buf.setLength(0);
	    		String pre=this.getSalaryBd(salaryid);
	    		String [] arr=pre.split(",");
	    		for(int i=0;i<arr.length;i++)
	    		{
	    			if(arr[i]==null|| "".equals(arr[i]))
	    				continue;
	         		buf.append(" insert into "+temp_tablename+"(");
	        		buf.append("NBASE,A0100,B0110,E0122,A00Z0,A0101,A0000,A00Z1,A00Z2,A00Z3,changeflag"+_cloumn_1.toString()+_cloumn_2.toString()+")");
	    	        buf.append("select NBASE,A0100,B0110,E0122,A00Z0,A0101,A0000,A00Z1,A00Z2,A00Z3,1 as changeflag");
	    	        buf.append(_chang_flag_1+_cloumn_2.toString()+" from ("+sql6.toString()+" and UPPER(nbase)='"+arr[i].toUpperCase()+"'");
	    	        buf.append(" and a0100 not in ( select a0100 from("+sql5.toString()+" and UPPER(nbase)='"+arr[i].toUpperCase()+"') temp1)) S");
	    	        dao.update(buf.toString());
	    	        buf.setLength(0);
	    		}
	    		/**导入减少人员*/
	    		for(int i=0;i<arr.length;i++)
	    		{
	    			if(arr[i]==null|| "".equals(arr[i]))
	    				continue;
	         		buf.append(" insert into "+temp_tablename+"(");
	        		buf.append("NBASE,A0100,B0110,E0122,A00Z0,A0101,A0000,A00Z1,A00Z2,A00Z3,changeflag"+_cloumn_2.toString()+_cloumn_1.toString()+")");
	    	        buf.append("select NBASE,A0100,B0110,E0122,A00Z0,A0101,A0000,A00Z1,A00Z2,A00Z3,2 as changeflag");
	    	        buf.append(_chang_flag_2+_cloumn_1.toString()+" from ("+sql5.toString()+" and UPPER(nbase)='"+arr[i].toUpperCase()+"' ");
	    	        buf.append(" and a0100 not in (select a0100 from ("+sql6.toString()+" and UPPER(nbase)='"+arr[i].toUpperCase()+"') temp1)) S");
	    	        dao.update(buf.toString());
	    	        buf.setLength(0);
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
	 * 当该工资类别没有上个业务日期时，全部为新增人员
	 * @param temp_tablename
	 * @param ywdate
	 * @param count
	 * @param fieldlist
	 * @param salaryid
	 */
	public void importAddPerson(String temp_tablename,String ywdate,int count,ArrayList fieldlist,String salaryid)
	{
		RowSet rs = null;
		try
		{
			
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			
    		StringBuffer buf = new StringBuffer();
    		ContentDAO dao = new ContentDAO(this.getFrameconn());	
		    StringBuffer cloumn_2 = new StringBuffer();
		    StringBuffer cloumn_1 = new StringBuffer();
			StringBuffer chang_flag_1 = new StringBuffer();
			StringBuffer bb=new StringBuffer();
    		for( int i=0;i<fieldlist.size();i++)
			{
				FieldItem item = (FieldItem)fieldlist.get(i);
				cloumn_1.append(","+item.getItemid()+"_1");
				cloumn_2.append(","+item.getItemid()+"_2");
			    bb.append(","+item.getItemid()+" as "+item.getItemid()+"_2");
				chang_flag_1.append(",'0' as "+item.getItemid()+"_1");
			}
    		String tableName="salaryhistory";
			StringBuffer psql = new StringBuffer();
			psql.append("select salaryid from salaryhistory where salaryid="+salaryid+" and "+Sql_switcher.year("a00z2")+"=");
			psql.append(ywdate.substring(0,4)+" and "+Sql_switcher.month("a00z2")+"="+ywdate.substring(5,7));
			psql.append(" and a00z3="+count);
			psql.append(" and (AppUser Like '%;"+this.userView.getUserName()+";%' or UPPER(curr_user)='"+this.userView.getUserName().toUpperCase()+"'  or  (AppUser is null  "+gzbo.getPrivWhlStr("")+") )");
			rs = dao.search(psql.toString());
			if(rs.next())
			{
				
			}else{//如果历史表中没有数据，找归档表
				tableName="salaryarchive";
				HistoryDataBo bo = new HistoryDataBo(this.getFrameconn());
				bo.syncSalaryarchiveStrut();//同步表结构
			}
         	buf.append(" insert into "+temp_tablename+"(");
        	buf.append("NBASE,A0100,B0110,E0122,A00Z0,A0101,A0000,A00Z1,A00Z2,A00Z3,changeflag"+cloumn_1.toString()+cloumn_2.toString()+")");
    	    buf.append("select NBASE,A0100,B0110,E0122,A00Z0,A0101,A0000,A00Z1,A00Z2,A00Z3,1 as changeflag");
    	    buf.append(chang_flag_1+bb.toString()+" from "+tableName+" where salaryid="+salaryid);
    	    buf.append(" and "+Sql_switcher.year("a00z2")+"=");
    	    buf.append(ywdate.substring(0,4)+" and "+Sql_switcher.month("a00z2")+"="+ywdate.substring(5,7));
    	    buf.append(" and a00z3="+count);
    	    buf.append(" and (AppUser Like '%;"+this.userView.getUserName()+";%' or UPPER(curr_user)='"+this.userView.getUserName().toUpperCase()+"'  or  (AppUser is null  "+gzbo.getPrivWhlStr("")+") )");//找上次我操作过的数据
    	    dao.update(buf.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
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
	
}
