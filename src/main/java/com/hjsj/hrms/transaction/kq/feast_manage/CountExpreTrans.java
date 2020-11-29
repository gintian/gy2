package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.feast_manage.FeastComputer;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 计算年假
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 31, 2006:4:53:14 PM</p>
 * @author sx
 * @version 1.0
 *
 */
public class CountExpreTrans extends IBusiness {

	// 是否勾选了上年结余
	private String balance = "";
	private String updateStart;
	private String updateEnd;
	public void execute() throws GeneralException
	{
		String feast_start=(String)this.getFormHM().get("feast_start");
		String feast_end=(String)this.getFormHM().get("feast_end");		
		String dbpre=(String)this.getFormHM().get("dbpre");
		String theYear=(String)this.getFormHM().get("kq_year");
		String hols_status=(String)this.getFormHM().get("hols_status");
		String count_fields=(String)this.getFormHM().get("count_fields");
		String strsql=(String)this.getFormHM().get("strsql");
		
		// 是否计算上年年假结余 1为计算，0为不计算
		HashMap map = (HashMap) this.getFormHM().get("requestPamaHM");
		balance = (String) map.get("balance");
		if (!"1".equals(balance)) {
			balance = "0";
		}
		if ("1".equals(balance))
		{
			 //验证可休天数和结余长度
			 checklength();
		}
		// 上年结余截止日期
		String balanceEnd = (String) map.get("balanceEnd");
		//上年结余字段名称
		String balanceName = this.getBalance();
		
		if(count_fields==null||count_fields.length()<=0)
			count_fields="q1703";
		String clear_zone=(String)this.getFormHM().get("clear_zone");		
		String[] exp_fields=count_fields.split("`");
		ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
				Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		
		FeastComputer feastComputer=new FeastComputer(this.getFrameconn(),this.userView);
		 String exc_p="";
		ArrayList dblist=new ArrayList();
		if(dbpre==null||dbpre.length()<=0)
			dblist=userView.getPrivDbList();
		else if("all".equalsIgnoreCase(dbpre))
			dblist=userView.getPrivDbList();
		else 
			dblist.add(dbpre);
		for(int i=0;i<dblist.size();i++)
		 {
			   String userbase= dblist.get(i).toString();
               String whereIN=RegisterInitInfoData.getWhereINSql(userView,userbase);			    
			   String whereB0110=RegisterInitInfoData.selcet_OrgId(userbase,"b0110",whereIN);
			   ArrayList orgidb0110List=OrgRegister.getQrgE0122List(this.getFrameconn(),whereB0110,"b0110");
			   for(int t=0;t<orgidb0110List.size();t++)
			   {
						 String b0110_one=orgidb0110List.get(t).toString();	
						// String nbase=RegisterInitInfoData.getOneB0110Dase(this.getFormHM(),this.userView,userbase,b0110_one,this.getFrameconn());
						 /********按照该单位的人员库的操作*********/
						 if(userbase!=null&&userbase.length()>0)
						 {
							 for(int s=0;s<exp_fields.length;s++)
							 {
								 String exp_field=exp_fields[s];
								 if(exp_field==null||exp_field.length()<=0)
									 continue;
                                 exc_p=feastComputer.getFeastComputer(b0110_one,exp_field,hols_status,this.getFrameconn());
                                 
								 if(exc_p==null||exc_p.length()<=0)
									 continue;
								 ArrayList userList = selectFeastUser(userbase,theYear,whereIN,hols_status);								 
								 insertFeastUser(userbase,theYear,feast_start,feast_end,whereIN,hols_status,exp_fields);
								 if ("1".equals(balance)) {
									 // 将上年结余天数更新到今年的记录中
									 updateBalance(theYear, hols_status,userList);
								 } 
								 deleteNoFeastUser(userbase,b0110_one,theYear,hols_status);
								
								 StringBuffer whl=new StringBuffer();
								 whl.append("select a0100 from q17");
								 whl.append(" where b0110='"+b0110_one+"'");
								 whl.append(" and nbase='"+userbase+"'");
								 whl.append(" and q1701='"+theYear+"'");
								 whl.append(" and q1709='"+hols_status+"'");
								 whl.append(" and a0100 in(select a0100 "+whereIN+")");
								 countExc_p(alUsedFields,userbase,exp_field,exc_p,whl.toString(),theYear,hols_status);
								 updateData(userbase,b0110_one,theYear,whereIN,hols_status);
							 }
							 // 把上年结余为null的全部重新计算出来
							 if ("1".equals(balance)) {
								// 更新结余截止时间
								 updateBalanceEnd(theYear, hols_status, userbase, b0110_one, whereIN, balanceEnd);
								 // szk将上年结余天数更新到今年的记录中并计算结余中已申请的天数暂时存到q17z6
								 updateNullBalance(theYear, hols_status, userbase, b0110_one, whereIN);
								
								 // 更新结余剩余天数
								 updateNullBalanceReamain(theYear, hols_status, userbase, b0110_one, whereIN);
								 // 更新可用天数
								 updateData(userbase,b0110_one,theYear,whereIN,hols_status);
							 }
							 
						 }else
						 {
								 /********防止改变靠勤人员库参数***********/
							 deleteData(userbase,b0110_one,whereIN,theYear,hols_status);
						 }
						 
		      }	
		 }
	     if(clear_zone!=null&& "1".equals(clear_zone))
	     {
	    	 String dSql="delete from q17 where q1701='"+theYear+"' and q1709='"+hols_status+"' and "+Sql_switcher.isnull("q1703", "0")+"=0 ";
	    	 if ("1".equals(balance) ) {
	    		 dSql += " and " +Sql_switcher.isnull(getBalance(), "0")+"=0 "; 
	    	 }
			 updateStart="update q17 set q17.q17z1='"+feast_start+"'";
			 updateEnd="update q17 set q17.q17z3='"+feast_end+"'";
			 ContentDAO dao=new ContentDAO(this.getFrameconn());
			 try {
				dao.update(dSql);
				String[] str=strsql.split("UNION");
				for (int j = 0; j < str.length; j++) {
					int count=str[j].indexOf("from");
					String updateStarts = updateStart+str[j].substring(count+15,str[j].length()-29)+" and Q17.q17z1 is null";
					String updateEnds = updateEnd+str[j].substring(count+15,str[j].length()-29)+" and Q17.q17z3 is null";
					dao.update(updateStarts);
					dao.update(updateEnds);
				}
			 } catch (Exception e) {
			
				e.printStackTrace();
			 }
	     }		
		this.getFormHM().put("hols_status",hols_status);
	}
	
	/**
	 * 验证年假的长度是否》=结余
	 * @return
	 * @author szk
	 * 2014-5-29下午05:21:51
	 * @throws GeneralException 
	 */
	private void checklength() throws GeneralException
	{
		FieldItem q17z4 = DataDictionary.getFieldItem(this.getBalance());
		FieldItem q1707 = DataDictionary.getFieldItem("q1707");
		int q1707len = q1707.getItemlength();
		int q17z4len = q17z4.getItemlength();
		int q17z4min = q17z4.getDecimalwidth();
		int q1707min = q1707.getDecimalwidth();
		if (q1707min > q17z4min)
		{
			throw new GeneralException("可休天数的小数位数大于上年结余的小数位数，计算时可能出现错误，请修改业务字典之后再计算！");
		}
		if (q1707len > q17z4len)
		{
			throw new GeneralException("可休天数的长度大于上年结余的长度，计算时可能出现错误，请修改业务字典之后再计算！");
		}
	}

	/**
	 * 获得没有插入Q17的人员信息
	 * @param nbase
	 * @param theYear
	 * @param whereIN
	 * @param hols_status
	 * @return
	 */
	public ArrayList selectFeastUser (String nbase,String theYear,String whereIN,String hols_status) {
		StringBuffer sel = new StringBuffer();
		ArrayList userList = new ArrayList();
		sel.append(" select '");
		sel.append(nbase);
		sel.append("' nbase,a0100 from ");
		sel.append(nbase);
		sel.append("A01");
		sel.append(" WHERE NOT EXISTS(SELECT * FROM q17");
		sel.append(" where q17.a0100=");
		sel.append(nbase);
		sel.append("A01.a0100 and ");
		String q17_b0110=Sql_switcher.isnull("q17.b0110","'a'");
		String a01_b0110=Sql_switcher.isnull(nbase+"A01.b0110","'a'");
		sel.append(q17_b0110);
		sel.append("=");
		sel.append(a01_b0110);
		sel.append(" and q17.q1701='");
		sel.append(theYear);
		sel.append("' and q17.q1709='");
		sel.append(hols_status);
		sel.append("' and q17.nbase='");
		sel.append(nbase);
		sel.append("') AND a0100 in(select a0100 ");
		sel.append(whereIN);
		sel.append(")");
		
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sel.toString());
			while (frowset.next()) {
				ArrayList list = new ArrayList();
				list.add(frowset.getString("nbase"));
				list.add(frowset.getString("a0100"));
				list.add(frowset.getString("nbase"));
				list.add(frowset.getString("a0100"));
				userList.add(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userList;
	}
	
	/**
	 * 更新上年结余为null的记录
	 * @param theYear
	 * @param hols_status
	 * @param nbase
	 * @param b0110_one
	 * @param whereIN
	 */
	public void updateNullBalance(String theYear, String hols_status,String nbase,String b0110_one,String whereIN) {
		StringBuffer sql = new StringBuffer();
		int currYear = Integer.parseInt(theYear);
		String topYear = String.valueOf(currYear - 1);
		sql.append("update q17 set ");
		sql.append(this.getBalance());
		
		// 查看结余剩余字段
		String field = KqUtilsClass.getFieldByDesc("q17", "结余剩余");
		String balanceEnd = KqUtilsClass.getFieldByDesc("q17", "结余截止日期");
		String now = PubFunc.FormatDate(new Date(), "yyyy.MM.dd");
		/*if (balanceEnd.length() > 0 && field.length() > 0) {
			sql.append("=(select ");
			sql.append("  q1707-"+Sql_switcher.isnull(field, "0")+" q1707 from q17 m where ");
			sql.append("m.nbase='"+nbase+"' and m.a0100=q17.a0100 and m.q1701='");
			sql.append(topYear);
			sql.append("' and m.q1709='");
			sql.append(hols_status);
			sql.append("') where nbase='"+nbase+"' and q1701='");
		} else {
			sql.append("=(select case when ");
			sql.append(Sql_switcher.isnull("q1707", "0"));
			sql.append(" < ");
			sql.append(Sql_switcher.isnull("q1703", "0"));
			sql.append(" then ");
			sql.append(Sql_switcher.isnull("q1707", "0"));
			sql.append(" else ");
			sql.append(Sql_switcher.isnull("q1703", "0"));
			sql.append(" end q1707 from q17 m where ");
			sql.append("m.nbase='"+nbase+"' and m.a0100=q17.a0100 and m.q1701='");
			sql.append(topYear);
			sql.append("' and m.q1709='");
			sql.append(hols_status);
			sql.append("') where nbase='"+nbase+"' and q1701='");
		}*/
		sql.append("=(select ");
		sql.append("  q1707 from q17 m where ");
		sql.append("m.nbase='"+nbase+"' and m.a0100=q17.a0100 and m.q1701='");
		sql.append(topYear);
		sql.append("' and m.q1709='");
		sql.append(hols_status);
		sql.append("') , "+field+"="+this.getBalance()+"-"+field);
		
		sql.append(" where nbase='"+nbase+"' and q1701='");
		sql.append(theYear);
		sql.append("' and q1709='");
		sql.append(hols_status);
		sql.append("'");
		sql.append("and b0110='"+b0110_one+"'");
		try {
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新结余剩余
	 * @param theYear
	 * @param hols_status
	 * @param nbase
	 * @param b0110_one
	 * @param whereIN
	 */
	public void updateNullBalanceReamain(String theYear, String hols_status,String nbase,String b0110_one,String whereIN) {
		StringBuffer sql = new StringBuffer();
		int currYear = Integer.parseInt(theYear);
		sql.append("update q17 set ");
		// 查看结余剩余字段
		String field = KqUtilsClass.getFieldByDesc("q17", "结余剩余");
		sql.append(field);
		sql.append("=");
		switch (Sql_switcher.searchDbServer())
		{//szk20131104 剩余为null时，剩余=结余
		case Constant.MSSQL:{
			
			sql.append("isnull("+this.getBalance()+"-"+field+","+this.getBalance()+")");
			break;
		}
		case Constant.ORACEL:{
		
			sql.append("nvl("+this.getBalance()+"-"+field+","+this.getBalance()+")");
			break;
		}
		}
	
		
		//sql.append(" where "+Sql_switcher.sqlNull(field, -1f)+" = -1  and b0110='"+b0110_one+"'");
		sql.append(" where b0110='"+b0110_one+"'");
		sql.append(" and q1701='"+theYear+"'");
		sql.append(" and nbase='"+nbase+"'");
		sql.append(" and q1709='"+hols_status+"'");
		sql.append(" and a0100 in(select a0100 "+whereIN+")");
		
		try {
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 更新上年结余截止日期
	 * @param theYear
	 * @param hols_status
	 * @param nbase
	 * @param b0110_one
	 * @param whereIN
	 */
	public void updateBalanceEnd(String theYear, String hols_status,String nbase,String b0110_one,String whereIN, String balanceEnd) {
		StringBuffer sql = new StringBuffer();
		String field = KqUtilsClass.getBalanceEnd();
		if (balanceEnd.length() > 0 && field.length() > 0) {
	
			sql.append("update q17 set ");
			
			sql.append(field);
			sql.append(" =");
			sql.append(Sql_switcher.dateValue(balanceEnd));
			sql.append(" where b0110='"+b0110_one+"'");
			sql.append(" and nbase='"+nbase+"'");
			sql.append(" and q1701='"+theYear+"'");
			sql.append(" and q1709='"+hols_status+"'");
			sql.append(" and a0100 in(select a0100 "+whereIN+")");
			
			try {
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				dao.update(sql.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 将上年结余更新到本年的数据中
	 * @param nbase
	 * @param b0110
	 * @param theYear
	 */
	public void updateBalance(String theYear, String hols_status, ArrayList list) {
		StringBuffer sql = new StringBuffer();
		int currYear = Integer.parseInt(theYear);
		String topYear = String.valueOf(currYear - 1);
		sql.append("update q17 set ");
		sql.append(this.getBalance());
		sql.append("=(select case when ");
		sql.append(Sql_switcher.isnull("q1707", "0"));
		sql.append(" < ");
		sql.append(Sql_switcher.isnull("q1703", "0"));
		sql.append(" then ");
		sql.append(Sql_switcher.isnull("q1707", "0"));
		sql.append(" else ");
		sql.append(Sql_switcher.isnull("q1703", "0"));
		sql.append(" end q1707 from q17 where ");
		sql.append("nbase=? and a0100=? and q1701='");
		sql.append(topYear);
		sql.append("' and q1709='");
		sql.append(hols_status);
		sql.append("') where nbase=? and a0100=? and q1701='");
		sql.append(theYear);
		sql.append("' and q1709='");
		sql.append(hols_status);
		sql.append("'");
		
		try {
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.batchUpdate(sql.toString(), list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 计算
	 * @param alUsedFields  所用的fieldlist
	 * @param nbase  人员库前缀
	 * @param exc_p  公式
	 * @param whl  select 过滤语句
	 */
	public void countExc_p(ArrayList alUsedFields,String nbase,String exc_field,String exc_p,String whl,String theYear,String hols_status)
	{
		if(exc_p==null||exc_p.length()<=0)
		  return;
		int infoGroup = 0; // forPerson 人员
		GetCheckExpreTrans check = new GetCheckExpreTrans();
		int varType = check.getFieldType(exc_field);
		String varTypes = check.getFieldTypes(exc_field);
		YearMonthCount ycm = null;		
		FieldItem exc_item=DataDictionary.getFieldItem(exc_field);
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		YksjParser yp = new YksjParser(this.userView, alUsedFields,
				YksjParser.forSearch,varType, infoGroup, "Ht", nbase);	
		yp.setRenew_term(" q1701='"+theYear+"' and q1709='"+hols_status+"'");
		//yp.setRenew_term("q1709='"+hols_status+"'");
		yp.run(exc_p, ycm, exc_field,"q17", dao,
				whl,this.getFrameconn(),varTypes,12,exc_item.getDecimalwidth(),2,null);
	}
	/**
	 * 添加不存在的用户
	 * @param nbase
	 * @param b0110
	 * @param theYear
	 * @param feast_start
	 * @param feast_end
	 * @param whereIN
	 * @throws GeneralException
	 */
	public void insertFeastUser(String nbase,String theYear,String feast_start,String feast_end,String whereIN,String hols_status,String[] exp_fields)throws GeneralException
	{
		StringBuffer insert=new StringBuffer();
		synchronizationInit(nbase,theYear,whereIN,hols_status,feast_start,feast_end,exp_fields);
		
		
		String char_to_date_start=Sql_switcher.dateValue(feast_start);
		String char_to_date_end=Sql_switcher.dateValue(feast_end);
		insert.append("INSERT INTO q17 (nbase,A0100,Q1701,B0110,E0122,E01A1,A0101,");
		insert.append("Q1703,");//年假天数
		insert.append("Q17Z1,");//年假开始 
		insert.append("Q17Z3,");//年假结束
		insert.append("Q1705,");//已休天数
		insert.append("Q1707,");//可休天数
		// 上年结余
		if ("1".equals(balance)) {
			insert.append(this.getBalance());
			insert.append(",");
		}
		
		insert.append("Q1709) ");
		insert.append(" select '"+nbase+"',a0100,'"+theYear+"',");
		insert.append(" B0110,E0122,E01A1,A0101,0,");
		insert.append(""+char_to_date_start+",");
		insert.append(""+char_to_date_end+",");
		// 上年结余
		if ("1".equals(balance)) {
			insert.append("0,0,0,'"+hols_status+"' from "+nbase+"A01");
		} else {
			insert.append("0,0,'"+hols_status+"' from "+nbase+"A01");
		}
		insert.append(" WHERE NOT EXISTS(SELECT * FROM q17");
		insert.append(" where q17.a0100="+nbase+"A01.a0100");
		//insert.append(" and q17.b0110="+nbase+"A01.b0110");
		String q17_b0110=Sql_switcher.isnull("q17.b0110","'a'");
		String a01_b0110=Sql_switcher.isnull(nbase+"A01.b0110","'a'");
		insert.append(" and "+q17_b0110+"="+a01_b0110+"");
		insert.append(" and q17.q1701='"+theYear+"'");
		insert.append(" and q17.q1709='"+hols_status+"'");
		insert.append(" and q17.nbase='"+nbase+"')");		
		insert.append(" AND a0100 in(select a0100 "+whereIN+")");		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList insertList = new ArrayList();        
		try {			
			dao.insert(insert.toString(),insertList);
		} catch (Exception e) {			
			//throw GeneralExceptionHandler.Handle(e);
			this.getFormHM().put("error_message",ResourceFactory.getProperty("kq.error.insert.emp"));
	    	this.getFormHM().put("error_return","/kq/feast_manage/managerdata.do?b_search=link"); 
	    	this.getFormHM().put("error_flag","2"); 
	    	e.printStackTrace();
		}
		this.getFormHM().put("error_flag","0"); 
	}
	
	/**
	 * 获得上年结余的字段名称
	 * @return
	 */
	public String getBalance () {
		// 获得年假结余的列名
		String balance = "";
		
		ArrayList fieldList = DataDictionary
					.getFieldList("q17", Constant.USED_FIELD_SET);
		for (int i = 0; i < fieldList.size(); i++) {
			FieldItem item = (FieldItem) fieldList.get(i);
			if ("上年结余".equalsIgnoreCase(item.getItemdesc())) {
				balance = item.getItemid();
			}
		}
		
		return balance;
	}
	
	public void synchronizationInit(String nbase,String theYear,String whereIN,String hols_status,String feast_start,String feast_end,String[] exp_fields)throws GeneralException
	{
		 String char_to_date_start=Sql_switcher.dateValue(feast_start);
		 String char_to_date_end=Sql_switcher.dateValue(feast_end);
		 String destTab="q17";//目标表
		 String srcTab=nbase+"A01";//源表
		 String strJoin="Q17.A0100="+srcTab+".A0100";//关联串  xxx.field_name=yyyy.field_namex,....
		 String start="";
		 String end="";
		 String q17z1=null;
		 String q17z3=null;
		 //起始时间,结束时间...
		 for (int i = 0; i < exp_fields.length; i++) {
			if("q17z1".equalsIgnoreCase(exp_fields[i]))
				q17z1=exp_fields[i];
			else if ("q17z3".equalsIgnoreCase(exp_fields[i]))
				q17z3=exp_fields[i];
		}
		 if(!"q17z1".equalsIgnoreCase(q17z1)){
			 start="`Q17.Q17Z1="+char_to_date_start;
		 }
		 if(!"q17z3".equalsIgnoreCase(q17z3))
			 end="`Q17.Q17Z3="+char_to_date_end;
		 String strSet="Q17.B0110="+srcTab+".B0110`Q17.E0122="+srcTab+".E0122`Q17.E01A1="+srcTab+".E01A1`Q17.A0101="+srcTab+".A0101"+start+end;//更新串  xxx.field_name=yyyy.field_namex,....
         String strDWhere="q17.q1701='"+theYear+"' and q17.nbase='"+nbase+"' and q17.q1709='"+hols_status+"'";//更新目标的表过滤条件
		 String strSWhere=srcTab+".a0100 in (select a0100 "+whereIN+")";//源表的过滤条件  
		 String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
//		 String updateStart=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,"Q17.q17z1="+char_to_date_start,strDWhere,strSWhere+" and Q17.q17z1 is null");
		 updateStart="update "+destTab+" set q17.q17z1="+char_to_date_start;
//		 String updateEnd=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,"Q17.q17z3="+char_to_date_end,strDWhere,strSWhere+" and Q17.q17z3 is null");
		 updateEnd="update "+destTab+" set q17.q17z3="+char_to_date_end;
		 String othWhereSql=destTab+".a0100 in(select a0100 "+whereIN+")";
		 update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,othWhereSql);
		 ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {	
			dao.update(update);
		} catch (Exception e) {
			
			this.getFormHM().put("error_message",ResourceFactory.getProperty("kq.error.data.synchronization"));
	    	this.getFormHM().put("error_return","/kq/feast_manage/managerdata.do?b_search=link"); 
	    	this.getFormHM().put("error_flag","2");	
	    	e.printStackTrace();
		}
		this.getFormHM().put("error_flag","0");
	}
	/**
	 * 删除不存在的用户
	 * @param nbase
	 * @param b0110
	 * @param theYear
	 * @param feast_start
	 * @param feast_end
	 * @param whereIN
	 * @throws GeneralException
	 */
	public void deleteNoFeastUser(String nbase,String b0110,String theYear,String hols_status)throws GeneralException
	{
		
		StringBuffer delete=new StringBuffer();
		delete.append("DELETE FROM q17 WHERE NOT A0100 IN (SELECT A0100 FROM "+nbase+"A01 where b0110='"+b0110+"')");
		delete.append(" AND q17.q1701='"+theYear+"'");
		delete.append(" AND q17.nbase='"+nbase+"'");
		delete.append(" and q17.q1709='"+hols_status+"'");
        delete.append(" AND b0110='"+b0110+"'");      
        ContentDAO dao = new ContentDAO(this.getFrameconn());
		        
		try {			
			dao.update(delete.toString());
		} catch (Exception e) {
			e.printStackTrace();
			//throw GeneralExceptionHandler.Handle(e);
		}

	}
	public boolean deleteData(String userbase,String b0110,String whereIN,String year,String hols_status) {
		boolean isCollect = false;
		StringBuffer strsql = new StringBuffer();		
		strsql.append("delete from Q17");
		strsql.append(" where nbase=?");				
		strsql.append(" and b0110=?");
		strsql.append(" and q1701=?");
		strsql.append(" and q1709=?");
		strsql.append(" and a0100 in(select a0100 "+whereIN+")");
		ArrayList deletelist=new ArrayList();
		deletelist.add(userbase);	
		deletelist.add(b0110);
		deletelist.add(year);
		deletelist.add(hols_status);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.delete(strsql.toString(),deletelist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isCollect;
	}
	public void updateData(String nbase,String b0110,String theYear,String whereIN,String hols_status)
	{
		StringBuffer update=new StringBuffer();
		update.append("update q17 set");
		/*if ("1".equals(balance)) {
			update.append(" q1707=q1703+");
			// 查看剩余结余天数
			String field = KqUtilsClass.getFieldByDesc("q17", "结余剩余");
			
			update.append(Sql_switcher.isnull(this.getBalance(), "0"));
			
			update.append("-q1705");
		} else {
			update.append(" q1707=q1703-q1705");
		}*/
		update.append(" q1707=q1703-q1705");
		update.append(" where b0110='"+b0110+"'");
		update.append(" and q1701='"+theYear+"'");
		update.append(" and nbase='"+nbase+"'");
		update.append(" and q1709='"+hols_status+"'");
		update.append(" and a0100 in(select a0100 "+whereIN+")");		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.update(update.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
