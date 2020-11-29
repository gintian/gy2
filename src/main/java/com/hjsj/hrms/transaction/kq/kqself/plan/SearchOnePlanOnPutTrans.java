package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 查看没有提交计划人员
 * @author Owner
 *
 */
public class SearchOnePlanOnPutTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");		
		String table = (String) hm.get("dtable");
		String plan_id=(String)hm.get("plan_id");
		String select_flag=(String)this.getFormHM().get("select_flag");
		String select_name=(String)this.getFormHM().get("select_name");
		this.getFormHM().put("select_flag",select_flag);
		this.getFormHM().put("select_name",select_name);
		ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);
		StringBuffer sql_str = new StringBuffer();
		StringBuffer cond_str = new StringBuffer();
		String columns = "";
		String a_code=(String)this.getFormHM().get("a_code");
		if(a_code==null||a_code.length()<=0)
	    {
	    	   a_code="UN";
	    }
		String kind="2";
		String code="";
		if(a_code!=null&&a_code.length()>0)
		{
			String codesetid=a_code.substring(0,2);
			if("UN".equalsIgnoreCase(codesetid))
			{
				kind="2";
			}else if("UM".equalsIgnoreCase(codesetid))
			{
				kind="1";
			}else if("@K".equalsIgnoreCase(codesetid))
			{
				kind="0";
			}
			if(a_code.length()>=3)
			{
				code=a_code.substring(2);
			}else
			{
				code=this.userView.getUserOrgId();
			}
		}
		ArrayList kq_dbase_list=setKqPerList(code,kind);
		String select_pre=(String)this.getFormHM().get("select_pre");
		if(select_pre==null||select_pre.length()<=0)
		{
			if(kq_dbase_list!=null&&kq_dbase_list.size()>0)
				select_pre="all";
		}
		ArrayList sql_db_list=new ArrayList();
		if(select_pre!=null&&select_pre.length()>0&&!"all".equals(select_pre))
		{
			sql_db_list.add(select_pre);
		}else
		{
			sql_db_list=kq_dbase_list;
		}
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
		String where_c=kqUtilsClass.getWhere_C(select_flag,"a0101",select_name);
		if("1".equals(kind))
		{
			cond_str.append(" e0122 like '"+code+"%'");
		}else if("0".equals(kind))
		{
			cond_str.append(" e01a1 like '"+code+"%'");	
		}else
		{
			cond_str.append(" b0110 like '"+code+"%'");	
		}		
		cond_str.append(where_c);
		StringBuffer sql=new StringBuffer();
		for(int i=0;i<sql_db_list.size();i++)
		{
			String nbase=sql_db_list.get(i).toString();
			sql.append("select "+i+" as i,'"+nbase+"' as nbase,a0100,a0101,b0110,e0122,e01a1,a0000 ");
			sql.append(" from ");
			sql.append(nbase+"A01 where ");
			sql.append(cond_str.toString());
			String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);			
			sql.append(" and a0100 in(select a0100 "+whereIN+") ");
			sql.append(" and NOT EXISTS (");
			sql.append("select * from q31 A where A.a0100="+nbase+"A01.a0100" );
			sql.append(" and "+cond_str.toString());
			sql.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");
			sql.append(" and  q2901='"+plan_id+"'");
			sql.append(" and a0100 in(select a0100 "+whereIN+")");
			sql.append(") ");
			sql.append(" and EXISTS(");
			sql.append("select 1 from q17 where "+nbase+"A01.a0100=q17.a0100 and  nbase='"+nbase+"' and q1701='"+getQ2903(plan_id)+"'");
			sql.append(" and q17.q1703>0 "); 
			sql.append(" and q17.a0100 in(select a0100 "+whereIN+") "); 
		    sql.append(")");
			sql.append(" union ");
		}
		sql.setLength(sql.length()-7);			
		//System.out.println(sql.toString());
		this.getFormHM().put("sql",sql.toString());
		this.getFormHM().put("com","nbase,a0100,a0101,b0110,e0122,e01a1,a0000");		
	}
	 private ArrayList setKqPerList(String code,String kind)throws GeneralException
	    {
	    	ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");
	    	KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
			kq_dbase_list=kqUtilsClass.setKqPerList(code,kind);					
			this.getFormHM().put("kq_dbase_list",kq_dbase_list);
			this.getFormHM().put("kq_list",kqUtilsClass.getKqNbaseList(kq_dbase_list));
			return kq_dbase_list;
	    }
	 public String getQ2903(String q2901)
	 {
		 String sql="select * from q29 where q2901='"+q2901+"'";
		 String q2903=PubFunc.getStringDate("yyyy");
		 try
		 {
			 ContentDAO dao=new ContentDAO(this.getFrameconn());
			 this.frowset=dao.search(sql);
			 if(this.frowset.next())
			 {
				 q2903=this.frowset.getString("q2903");
			 }
		 }catch(Exception e)
		 {
			e.printStackTrace(); 
		 }
		 return q2903;
	 }
}
