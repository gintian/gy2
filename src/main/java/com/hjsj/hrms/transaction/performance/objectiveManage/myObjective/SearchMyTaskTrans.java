package com.hjsj.hrms.transaction.performance.objectiveManage.myObjective;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author duml
 * @version 1.0
 * 
 */
public class SearchMyTaskTrans extends IBusiness {

	public void execute() throws GeneralException {
		String a0100=this.userView.getA0100();
		String nbase=this.userView.getDbname();
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		StringBuffer sql=new StringBuffer();
		sql.append("select pdt.TO_P0400,pdt.TO_P0407,pdt.TO_a0101,pdt.A0100,pdt.A0101,");	
		sql.append(this.today("pdt.Create_date","2")+" as Create_date,");
		sql.append("pdt.nbase,CASE WHEN pdt.TASK_TYPE=1 THEN '主办任务'  WHEN pdt.TASK_TYPE=2 THEN '协办任务'   ELSE '' END AS TASK_TYPE,");
		sql.append(" pa.article_name,pa.ext,pa.Article_id from per_designate_task pdt  left join per_article pa on pdt.p0400=pa.task_id where pdt.TO_A0100='");
		sql.append(a0100);
		sql.append("' and pdt.TO_NBASE='");
		String opt=(String)hm.get("opt");
		hm.remove("opt");
		sql.append(nbase.toUpperCase());
		sql.append("'");
		String extendsql="";
		String record="1";
	
		String days="30";
		String startdate="";
		String enddate="";
		StringBuffer ext_sql = new StringBuffer();
		if(opt!=null&&opt.length()!=0&& "search".equalsIgnoreCase(opt)){
			record=(String)this.getFormHM().get("record");
			if(record!=null&&record.length()!=0&& "2".equalsIgnoreCase(record)){
				startdate=(String)this.getFormHM().get("startdate");
				enddate=(String)this.getFormHM().get("enddate");
				if(enddate!=null&&enddate.trim().length()>0){
					ext_sql.append(" and ( "+Sql_switcher.year(this.today(enddate,"1"))+">"+Sql_switcher.year("pdt.Create_date"));
					ext_sql.append(" or ( "+Sql_switcher.year(this.today(enddate,"1"))+"="+Sql_switcher.year("pdt.Create_date")+" and "+Sql_switcher.month(this.today(enddate,"1"))+">"+Sql_switcher.month("pdt.Create_date")+" ) ");
					ext_sql.append(" or ( "+Sql_switcher.year(this.today(enddate,"1"))+"="+Sql_switcher.year("pdt.Create_date")+" and "+Sql_switcher.month(this.today(enddate,"1"))+"="+Sql_switcher.month("pdt.Create_date")+" and "+Sql_switcher.day(this.today(enddate,"1"))+">="+Sql_switcher.day("pdt.Create_date")+" ) ) ");
				}
				if(startdate!=null&&startdate.trim().length()>0){
					ext_sql.append(" and ( "+Sql_switcher.year(this.today(startdate,"1"))+"<"+Sql_switcher.year("pdt.Create_date"));
					ext_sql.append(" or ( "+Sql_switcher.year(this.today(startdate,"1"))+"="+Sql_switcher.year("pdt.Create_date")+" and "+Sql_switcher.month(this.today(startdate,"1"))+"<"+Sql_switcher.month("pdt.Create_date")+" ) ");
					ext_sql.append(" or ( "+Sql_switcher.year(this.today(startdate,"1"))+"="+Sql_switcher.year("pdt.Create_date")+" and "+Sql_switcher.month(this.today(startdate,"1"))+"="+Sql_switcher.month("pdt.Create_date")+" and "+Sql_switcher.day(this.today(startdate,"1"))+"<="+Sql_switcher.day("pdt.Create_date")+" ) ) ");
				
				}
				sql.append(ext_sql.toString());
				days=(String)this.getFormHM().get("latest");
			}
			if(record!=null&&record.length()!=0&& "1".equalsIgnoreCase(record)){
				days=(String)this.getFormHM().get("latest");
				ext_sql.append(" and "+ Sql_switcher.diffDays(this.gettime(), "pdt.Create_date")+"<="+days);
				sql.append(ext_sql.toString());
			}
		}else{
			ext_sql.append(" and "+ Sql_switcher.diffDays(this.gettime(), "pdt.Create_date")+"<=30");
			sql.append(ext_sql.toString());
		}
		sql.append(" order by pdt.Create_date desc");
		ContentDAO dao=new ContentDAO(this.frameconn);
		ArrayList tasklist=new ArrayList();
		int i=0;
		try {
			this.frowset=dao.search(sql.toString());
			LazyDynaBean bean=null;
			ArrayList namelist=null;
			HashMap namemap=new HashMap();
			HashMap keymap=new HashMap();
			while(this.frowset.next()){
				bean=new LazyDynaBean();
				String p0400=this.frowset.getString("TO_P0400");
				String fa0100=this.frowset.getString("a0100");
				String artname=this.frowset.getString("article_name");
				artname=artname==null||artname.trim().length()==0?"-1":artname;
				String article_id=this.frowset.getString("Article_id")==null?"":this.frowset.getString("Article_id");
				i=i+1;
				bean.set("id", String.valueOf(i));
				String  content=this.frowset.getString("TO_P0407");
				bean.set("content", content);
				
				bean.set("articlename", artname);
				
				String xdpeople=this.frowset.getString("A0101");
				bean.set("xdpeople", xdpeople);
				String cratetime=this.frowset.getString("Create_date");
				bean.set("Create_date", cratetime);
				String task=this.frowset.getString("TASK_TYPE");
				bean.set("TASK_TYPE", task);
				bean.set("ext", this.frowset.getString("ext"));
				
				bean.set("article_id", article_id);
				if(namemap.get(p0400+"*"+content+"*"+fa0100)!=null){
					i=i-1;
					namelist=(ArrayList)namemap.get(p0400+"*"+content+"*"+fa0100);
					LazyDynaBean cd=new LazyDynaBean();
					cd.set("dataname",artname);
					cd.set("datavalue",article_id);
					namelist.add(cd);
					namemap.put(p0400+"*"+content+"*"+fa0100,namelist);
				}else{
					LazyDynaBean cd=new LazyDynaBean();
					cd.set("dataname",artname);
					cd.set("datavalue",article_id);
					namelist=new ArrayList();
					namelist.add(cd);
					namemap.put(p0400+"*"+content+"*"+fa0100,namelist);
					tasklist.add(bean);
					keymap.put(String.valueOf(tasklist.size()-1),p0400+"*"+content+"*"+fa0100);
				}
				
			}
			ArrayList newlist=new ArrayList();
			for(int k=0;k<tasklist.size();k++){
				String key=(String)keymap.get(String.valueOf(k));
				LazyDynaBean abean=(LazyDynaBean)tasklist.get(k);
				ArrayList nmlist=(ArrayList)namemap.get(key);
				abean.set("namelist", nmlist);
				newlist.add(abean);
			}
			this.getFormHM().put("tasklist", newlist);
			
			this.getFormHM().put("record",record);
			this.getFormHM().put("startdate", startdate);
			this.getFormHM().put("enddate", enddate);
			this.getFormHM().put("latest", days);
		} catch (SQLException e) {
		
			e.printStackTrace();
		}
	}
	private String today(String time,String flag){
		String day="";
		if("1".equalsIgnoreCase(flag)){
			if(Sql_switcher.searchDbServer()==Constant.MSSQL){
				day="convert(varchar(10),'"+time+"',120)";
			}
			if(Sql_switcher.searchDbServer()==Constant.ORACEL){
				 day="TO_DATE('"+time+"','yyyy-mm-dd') ";
	
			}
		}else{
			if(Sql_switcher.searchDbServer()==Constant.MSSQL){
				day="convert(varchar(30),"+time+",120)";
			}
			if(Sql_switcher.searchDbServer()==Constant.ORACEL){
				 day="TO_CHAR("+time+",'yyyy-mm-dd hh24:mi:ss') ";
	
			}
		}
		return day;
		
	}
	private String gettime(){
		String time="";
		if(Sql_switcher.searchDbServer()==Constant.MSSQL){
			time=" CONVERT(VARCHAR(10),GETDATE(),120) ";
		}
		if(Sql_switcher.searchDbServer()==Constant.ORACEL){
			time="TO_DATE(TO_CHAR(SYSDATE, 'YYYY-MM-DD'),'YYYY-MM-DD') ";

		}
		return time ;
		
		
	}
}
