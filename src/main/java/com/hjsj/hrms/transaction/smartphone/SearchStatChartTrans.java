package com.hjsj.hrms.transaction.smartphone;

import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.List;
/**
 * 显示统计分析
 * @author Administrator
 *
 */
public class SearchStatChartTrans  extends IBusiness {

	public void execute() throws GeneralException {
		String statid=(String)this.getFormHM().get("statid");
		if(statid==null||statid.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("得到参数错误！"));
		String userbase=(String)this.getFormHM().get("nbase");
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
					   list.add(rec);
				       statTotal += statvalues[i];
				   }
			       
		   }
		   this.getFormHM().put("snamedisplay",SNameDisplay);
		   this.getFormHM().put("legendlist",list);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
