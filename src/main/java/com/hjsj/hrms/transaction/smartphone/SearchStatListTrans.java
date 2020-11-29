package com.hjsj.hrms.transaction.smartphone;

import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.StatCondAnalyse;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchStatListTrans  extends IBusiness {

	public void execute() throws GeneralException {
		StringBuffer strsql=new StringBuffer();
		String statid=(String)this.getFormHM().get("statid");		
		String showstyle=(String)this.getFormHM().get("showstyle");	
		String resourcepriv = (String)this.getFormHM().get("resourcepriv");
		String colums="";
		String order="";
		String allcount="0";
		String wherenull="";
		if(com.hrms.hjsj.utils.Sql_switcher.searchDbServer()!=2){
			wherenull=" and categories<>''";
		}
		if((statid==null||statid.length()<=0)&&(showstyle==null||showstyle.length()<=0))
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());//判断是否分类
			try {
				String sql = "select count(*) a from sname where (categories is not null"+wherenull+") and infokind=1 and type=1";
				if(!this.userView.isSuper_admin())
					sql+=" and id in("+resourcepriv+")";
				this.frowset=dao.search(sql);
				if(this.frowset.next())
				{
					int count=this.frowset.getInt("a");
					if(count>0)
						showstyle="0";
					else
						showstyle="1";
				}else
					showstyle="1";
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if("0".equals(showstyle))
		{
			colums="codeitemid,codeitemdesc";
			strsql.append("select codeitemid,codeitemdesc from (");
			strsql.append("select categories as codeitemdesc,-1 as codeitemid,min(snorder) snorder from sname where (categories is not null"+ wherenull+") and infokind=1 and type=1");
			if(!this.userView.isSuper_admin())
				strsql.append(" and id in("+resourcepriv+")");
			strsql.append("group by categories");
			strsql.append(" union select name as codeitemdesc,id as codeitemid,snorder from sname  where (categories is null or categories='') and infokind=1 and type=1");
			if(!this.userView.isSuper_admin())
				strsql.append(" and id in("+resourcepriv+")");
			strsql.append(")aa");
			order="order by snorder";
			this.getFormHM().put("returnvalue", "categories");//返回到分类
		}
		else if("1".equals(showstyle))
		{
			String categories=(String)this.getFormHM().get("categories");
			colums="id,name";
			strsql.append("select "+colums+" from sname where infokind=1 and type=1");
			if(categories!=null&&categories.length()>0)
			{
				categories=SafeCode.decode(categories); 
				strsql.append(" and categories='"+categories+"'");
			}
			if(!this.userView.isSuper_admin())
				strsql.append(" and id in("+resourcepriv+")");
			order="order by snorder";
			this.getFormHM().put("categories", "");
		}else if("2".equals(showstyle))
		{
			String userbase=(String)this.getFormHM().get("nbase");
			colums="legend,norder";
			strsql.append("select legend,norder from slegend where id='"+statid+"'");
			order="order by norder";
			if(userbase==null||userbase.length()<=0)
			{
				ArrayList Dblist=userView.getPrivDbList();				
				if(Dblist!=null && Dblist.size()>0){
					userbase=Dblist.get(0).toString();
				}
			}
			this.getFormHM().put("nbase", userbase);
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
	    	String stat_id=sysbo.getValue(Sys_Oth_Parameter.STAT_ID);
	    	String commlexr="";
	        String commfacor="";
	    	if(stat_id!=null&&stat_id.length()>0&&!"#".equals(stat_id))
	    	{
	    		String[] stat_ids=new String[1];
	    		stat_ids[0]=stat_id;
	    		String[] curr_id=stat_ids;	   
	    		//加上常用查询进行的统计
	    	   
	    		GeneralQueryStat generalstat=new GeneralQueryStat();
	    		generalstat.getGeneralQueryLexrfacor(curr_id,userbase,"",this.getFrameconn());
	    	    
	    	    if(curr_id!=null)
	    	    {
	    	    	commlexr=generalstat.getLexpr();
	    	    	commfacor=generalstat.getLfactor();
	    	    }
	    	}
	    	boolean isresult=true;
	    	StringBuffer sql =new StringBuffer();
			sql.append("select flag from SName where id=");
			sql.append(statid);
			List rs =ExecuteSQL.executeMyQuery(sql.toString());
			if (!rs.isEmpty()) {
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				String flag = rec.get("flag")!=null?rec.get("flag").toString():"";
				if(flag!=null&& "1".equals(flag))
					isresult=false; //false时才查询，查询结果表
			}
			StatDataEncapsulation simplestat=new StatDataEncapsulation();
			int[] statvalues;
			try {
				statvalues = simplestat.getLexprData(userbase, Integer.parseInt(statid), "", userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,commlexr,commfacor,"","");
				String SNameDisplay = simplestat.getSNameDisplay();
				ArrayList list=new ArrayList();
				LazyDynaBean nordercountbean=new LazyDynaBean();
				if (statvalues != null && statvalues.length > 0) 
				{
					   String[] fieldDisplay = simplestat.getDisplay();
					   String[] norderDisplay = simplestat.getNorder_display();
					   int statTotal = 0;
					   for (int i = 0; i < statvalues.length; i++) 
					   {
						   LazyDynaBean rec=new LazyDynaBean();
						   String str=fieldDisplay[i];
						   rec.set("legend",str);
						   rec.set("count",String.valueOf(statvalues[i]));
						   rec.set("norder", norderDisplay[i]);
						   nordercountbean.set(norderDisplay[i], String.valueOf(statvalues[i]));
						   list.add(rec);
					       statTotal += statvalues[i];
					   }
				       
			   }
			   this.getFormHM().put("snamedisplay",SNameDisplay);
			   this.getFormHM().put("nordercountbean", nordercountbean);
			   this.getFormHM().put("legendlist",list);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
		}else if("3".equals(showstyle))
		{
			String userbase=(String)this.getFormHM().get("nbase");
			String norder=(String)this.getFormHM().get("norder");			
			StringBuffer sql =new StringBuffer();
			sql.append("select flag from SName where id=");
			sql.append(statid);
			boolean isresult=true;
			List rs =ExecuteSQL.executeMyQuery(sql.toString());
			if (!rs.isEmpty()) {
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				String flag = rec.get("flag")!=null?rec.get("flag").toString():"";
				if(flag!=null&& "1".equals(flag))
					isresult=false; //false时才查询，查询结果表
			}
			sql.setLength(0);
			sql.append("select lexpr,factor from slegend where id='"+statid+"' and norder='"+norder+"'");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String strlexpr="";
			String strfactor="";
			try {
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next())
				{
					strlexpr=this.frowset.getString("lexpr");
					strfactor=this.frowset.getString("factor")+ "`";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
	    	String stat_id=sysbo.getValue(Sys_Oth_Parameter.STAT_ID);
	    	StatCondAnalyse cond = new StatCondAnalyse();
	    	if(stat_id!=null&&stat_id.length()>0&&!"#".equals(stat_id))
	    	{
	    		String[] stat_ids=new String[1];
	    		stat_ids[0]=stat_id;
	    		String []curr_id=stat_ids;
	    		GeneralQueryStat generalstat=new GeneralQueryStat();
				generalstat.getGeneralQueryLexrfacor(curr_id,userbase,"",this.getFrameconn());
				String[] style=new StatDataEncapsulation().getCombinLexprFactor(strlexpr,strfactor,generalstat.getLexpr(),generalstat.getLfactor());
				if(style!=null && style.length==2)
			    {
			    	strlexpr=style[0];
			    	strfactor=style[1];
			    }
	    	}
			String wheresql=cond.getCondQueryString(strlexpr,strfactor,userbase,false,userView.getUserName(),"",userView,"1",isresult,false);		
			colums="a0100,a0101,e0122,b0110,e0122";
			order="order by a0000";
			strsql.append("select distinct a0000,");
	        strsql.append(userbase);
	        strsql.append("a01.a0100 ,");
	        strsql.append(userbase);
	        strsql.append("a01.a0101 ,");
	        strsql.append(userbase);        
	        strsql.append("a01.b0110 b0110,");
            strsql.append(userbase);
            strsql.append("a01.e0122 e0122,");
            strsql.append(userbase);
            strsql.append("a01.e01a1 e01a1 ");
			strsql.append(wheresql);
			sql.setLength(0);
			sql.append("select count(*) aa "+wheresql);
			try {
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next())
				{
					allcount=this.frowset.getInt("aa")+"";
					
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.getFormHM().put("allcount", allcount);
		this.getFormHM().put("sql", strsql.toString());
		this.getFormHM().put("columns",colums);
		this.getFormHM().put("strwhere",""); 
		this.getFormHM().put("order",order); 
		this.getFormHM().put("showstyle", showstyle);
	}

}
