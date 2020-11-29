package com.hjsj.hrms.transaction.smartphone;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class GetGeneralQueryTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String a_code = (String)this.getFormHM().get("a_code");
		StringBuffer strsql=new StringBuffer();
		ArrayList list =new ArrayList();
		String showstyle = "1";
		HashMap reqMap = (HashMap)this.getFormHM().get("requestPamaHM");
		String dbpre = (String)reqMap.get("dbpre");
		dbpre=dbpre==null?"":dbpre;
		try
        {
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if(dbpre.length()>0){
				showstyle="2";
				String sql = this.getPersonSQL(a_code, dbpre, dao);
				this.frowset = dao.search(sql);
				while(this.frowset.next()){
	        		 DynaBean vo=new LazyDynaBean();
		                vo.set("nbase",this.frowset.getString("nbase"));
		                vo.set("a0100",this.getFrowset().getString("a0100"));
		                vo.set("a0101", this.frowset.getString("a0101"));
		                list.add(vo);
	        	}
			}else{
				if(a_code.length()>0){
					ArrayList dblist=userView.getPrivDbList();
					if(dblist.size()>1){
						strsql.append("select pre,dbname from dbname where pre in (");
				        for(int i=0;i<dblist.size();i++)
				        {
				            if(i!=0)
				            	strsql.append(",");
				            strsql.append("'");
				            strsql.append((String)dblist.get(i));
				            strsql.append("'");
				        }
				        strsql.append(")");
				        strsql.append(" order by dbid");
				        this.frowset = dao.search(strsql.toString());
			        	while(this.frowset.next()){
			        		String pre = this.frowset.getString("pre");
			        		 DynaBean vo=new LazyDynaBean();
				                vo.set("id",pre);
				                vo.set("name",this.getFrowset().getString("dbname"));
				                vo.set("count", ""+this.getCount(a_code, pre, dao));
				                list.add(vo);
			        	}
			        	DynaBean vo=new LazyDynaBean();
		                vo.set("id","all");
		                vo.set("name","全部人员库");
		                vo.set("count", ""+this.getCount(a_code, "all", dao));
		                list.add(vo);
					}else{
						String pre = (String)dblist.get(0);
						showstyle="2";
						String sql = this.getPersonSQL(a_code, pre, dao);
						this.frowset = dao.search(sql);
						while(this.frowset.next()){
			        		 DynaBean vo=new LazyDynaBean();
				                vo.set("nbase",this.frowset.getString("nbase"));
				                vo.set("a0100",this.getFrowset().getString("a0100"));
				                vo.set("a0101", this.frowset.getString("a0101"));
				                list.add(vo);
			        	}
					}
				}else{
					 /**应用库过滤前缀符号*/
		        	ArrayList dblist=userView.getPrivDbList();
		            if(dblist.size()==0){
		            	throw new GeneralException(ResourceFactory
		    					.getProperty("errors.static.notdbname"));
		            }
		            strsql.append("select id,name,type from lexpr where type='");
		            strsql.append("1");
		            strsql.append("' order by norder");
		            
		            /**常用查询条件列表*/
		            this.frowset=dao.search(strsql.toString());
		            while(this.frowset.next())
		            {
		                if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("id"))))
		                	continue;
		                DynaBean vo=new LazyDynaBean();
		                String id = ""+this.frowset.getInt("id");
		                vo.set("id",id);
		                vo.set("name",this.getFrowset().getString("name"));
		                vo.set("type",this.frowset.getString("type"));
		                vo.set("count", ""+this.getCount(id, "all", dao));
		                list.add(vo);
		            }
				}
			}
        }
        catch(SQLException sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("condlist",list);
            this.getFormHM().put("showstyle", showstyle);
            this.getFormHM().put("allcount", ""+list.size());
        }
	}
	
	private int getCount(String id,String dbpre,ContentDAO dao) throws SQLException, GeneralException{
		int count=0;
		String sql = "select lexpr,factor from lexpr where type='1' and id="+id;
		this.frecset = dao.search(sql);
		if(this.frecset.next()){
			//String lexpr = this.frecset.getString("lexpr");
			//String factor = this.frecset.getString("factor");
			String lexpr = Sql_switcher.readMemo(this.frecset, "lexpr");
			String factor = Sql_switcher.readMemo(this.frecset, "factor");
			factor=factor.replaceAll("\\$THISMONTH\\[\\]","当月");
			if("all".equals(dbpre)){
				sql = userView.getPrivSQLExpression(lexpr+"|"+factor,"###",false,false,true,new ArrayList());
				StringBuffer sqlstr = new StringBuffer("select count(a0100) count from (");
				ArrayList dblist=userView.getPrivDbList();
				int dbsize = dblist.size();
				for(int i=0;i<dbsize;i++){
					String pre=(String)dblist.get(i);
					sqlstr.append("select "+pre+"A01.a0100 as a0100 "+sql.replaceAll("###", pre)+" union all ");
				}
				sql=sqlstr.substring(0,sqlstr.length()-11)+") ttt";
				this.frecset = dao.search(sql);
			}else{
				sql = "select count(###A01.a0100) count "+userView.getPrivSQLExpression(lexpr+"|"+factor,"###",false,false,true,new ArrayList());
				this.frecset = dao.search(sql.replaceAll("###", dbpre));
			}
			if(this.frecset.next()){
				count = this.frecset.getInt("count");
			}
		}
		return count;
	}
	private String getPersonSQL(String id,String dbpre,ContentDAO dao) throws SQLException, GeneralException{
		String sql = "select name,lexpr,factor from lexpr where type='1' and id="+id;
		this.frecset = dao.search(sql);
		StringBuffer html = new StringBuffer();
		if(this.frecset.next()){
			//String lexpr = this.frecset.getString("lexpr");
			//String factor = this.frecset.getString("factor");
			String lexpr = Sql_switcher.readMemo(this.frecset, "lexpr");
			String factor = Sql_switcher.readMemo(this.frecset, "factor");
			factor=factor.replaceAll("\\$THISMONTH\\[\\]","当月");
			html.append(this.frecset.getString("name"));
			if("all".equals(dbpre)){
				sql = userView.getPrivSQLExpression(lexpr+"|"+factor,"###",false,false,true,new ArrayList());
				StringBuffer sqlstr = new StringBuffer("select nbase,a0100,a0101 from (");
				ArrayList dblist=userView.getPrivDbList();
				int dbsize = dblist.size();
				for(int i=0;i<dbsize;i++){
					String pre=(String)dblist.get(i);
					sqlstr.append("select '"+pre+"' as nbase,"+pre+"A01.a0100 as a0100,"+pre+"A01.a0101 as a0101,"+pre+"A01.a0000 as a0000 "+sql.replaceAll("###", pre)+" union all ");
				}
				sql=sqlstr.substring(0,sqlstr.length()-11)+") ttt";// order by a0000";
			}else{
				sql = "select '"+dbpre+"' as nbase,"+dbpre+"A01.a0100 as a0100,"+dbpre+"A01.a0101 as a0101 "+userView.getPrivSQLExpression(lexpr+"|"+factor,dbpre,false,false,true,new ArrayList())+" order by a0000";
			}
		}
		if(!"all".equals(dbpre)){
			this.frecset = dao.search("select dbname from dbname where upper(pre)='"+dbpre.toUpperCase()+"'");
			if(this.frecset.next())
				html.append("("+this.frecset.getString("dbname")+")");
		}else{
			html.append("(全部人员库)");
		}
		this.getFormHM().put("html", html.toString());
		return sql;
	}
}
