package com.hjsj.hrms.transaction.smartphone;

import com.hjsj.hrms.businessobject.general.muster.hmuster.HmusterBo;
import com.hjsj.hrms.interfaces.general.HmusterXML;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetHRosterTrans extends IBusiness {

	private String tmpnbase="";
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String flag = (String)hm.get("flag");
		String moduleflag="3";
		String flaga = "1";
		ArrayList list = new ArrayList();
		String id=(String)hm.get("sortid");
		String showstyle = "1";
		String a_code = (String)this.getFormHM().get("a_code");
		String dbpre = (String)hm.get("dbpre");
		try{
			StringBuffer strsql=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if(a_code.length()>0){
				showstyle = "2";
			}else{
				ArrayList dblist=userView.getPrivDbList();
	            if(dblist.size()==0){
	            	throw new GeneralException(ResourceFactory
	    					.getProperty("errors.static.notdbname"));
	            }
				String temp=this.userView.getResourceString(5);
				if(temp.trim().length()==0)
					temp="-1";
				StringBuffer sqlstr = new StringBuffer();
				sqlstr.append("SELECT tabid,cname FROM muster_name where (sortid is null or sortid='0') and nmodule='");
				sqlstr.append(moduleflag);
				if(!"su".equalsIgnoreCase(userView.getUserName())){
					sqlstr.append("' and tabid in (");   
					sqlstr.append(temp); 
					sqlstr.append(") order by nmodule, tabid");
				}else{
					sqlstr.append("'"); 
				}
				List rs1 = new ArrayList();
				if("1".equals(flag)){
					rs1=ExecuteSQL.executeMyQuery(sqlstr.toString());
				}
				List rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(flag,moduleflag,id,flaga,temp,userView)); 
				if(!rs.isEmpty()||!rs1.isEmpty()){
					for(int i=0;i<rs.size();i++){
						DynaBean rec=(DynaBean)rs.get(i);
						if("1".equals(flag)){
							String sortid=rec.get("sortid")!=null?rec.get("sortid").toString():"";
							String sortname=rec.get("sortname")!=null?rec.get("sortname").toString():"";
							DynaBean vo=new LazyDynaBean();
							vo.set("sortid", sortid);
							vo.set("name",sortname);
							list.add(vo);
						}else{
							String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
							String cname=rec.get("cname")!=null?rec.get("cname").toString():"";
							if(this.hasFactor(tabid)){
								DynaBean vo=new LazyDynaBean();
								vo.set("tabid", tabid);
								vo.set("name",cname);
								//HmusterBo hmusterBo = this.initBo(tabid);
								vo.set("count",""+this.getCount(dao,tabid));
								list.add(vo);  
							}
						}
					}
					if("1".equals(flag)){
						for(int i=0;i<rs1.size();i++){
							DynaBean rec=(DynaBean)rs1.get(i);
							String tabid=rec.get("tabid")!=null?rec.get("tabid").toString():"";
							String cname=rec.get("cname")!=null?rec.get("cname").toString():"";
							if(this.hasFactor(tabid)){
								DynaBean vo=new LazyDynaBean();
								vo.set("tabid", tabid);
								vo.set("name",cname);
								//HmusterBo hmusterBo = this.initBo(tabid);
								vo.set("count",""+this.getCount(dao,tabid));
								list.add(vo);  
							}	 
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.getFormHM().put("condlist", list);
            this.getFormHM().put("showstyle", showstyle);
            this.getFormHM().put("allcount", ""+list.size());
		}
	}

	private String getLoadTreeQueryString(String flag,String moduleflag,String sortid,String flaga,String temp,UserView userview) throws SQLException {
		StringBuffer strsql=new StringBuffer();
		String nprint=flaga;
		String flags="";
		if ("1".equals(flaga)){ // 人员库
			flags = "A";
		} else if ("3".equals(flaga)){ // 职位库
			flags = "K";
		} else if ("2".equals(flaga)){ // 单位库
			flags = "B";
		} else
			flags = "A";

		if("81".equals(moduleflag))   //考勤
			flags="Q";
		if("5".equals(moduleflag))   //人事异动
			flags="A";
		if("1".equals(flag)){
			strsql.append("select sortid,sortname,nmodule from Muster_Sort where ");
			strsql.append("nmodule=");
			strsql.append(moduleflag);
			strsql.append(" and sortid in(select SortId from muster_name");   
			if(!"5".equals(moduleflag)){
				if(!(userview.isAdmin()&& "1".equals(userview.getGroupId()))){
					strsql.append(" where tabid in (");   
					strsql.append(temp); 
					strsql.append(")");
				}
			}
			strsql.append(")");
		}else{
			strsql.append("SELECT tabid,cname FROM muster_name where ");
			/*strsql.append("sortid=");
			strsql.append(sortid);
			strsql.append(" and ");  */
			strsql.append("nmodule=");
			strsql.append(moduleflag);
			strsql.append(" and tabid<>1000 and tabid<>1010 and tabid<>1020 and flaga='");   
			strsql.append(flags);
			strsql.append("'"); 
			
			if("Q".equals(flags)|| "5".equals(moduleflag))
				strsql.append(" and nprint='"+nprint+"'");
			if(!"5".equals(moduleflag)){
				if(!(userview.isAdmin()&& "1".equals(userview.getGroupId()))){
					strsql.append(" and tabid in (");   
					strsql.append(temp); 
					strsql.append(")");
				}
			}
			
			strsql.append(" order by nmodule, tabid");
			/*ContentDAO dao = new ContentDAO(this.frameconn);
			this.frecset=dao.search("select sortname from muster_sort where sortid="+sortid+" and nmodule=3");
			String html = "";
			if(this.frecset.next()){
				html=this.frecset.getString("sortname");
			}
			this.getFormHM().put("html", html);*/
		}
		return strsql.toString();
	}
	
	public boolean hasFactor(String tabid)
	{
		boolean has=false;
		Connection  con=null;
		try
		{
			con=(Connection)AdminDb.getConnection();
			HmusterXML hmxml = new HmusterXML(con,tabid);
			String factor = hmxml.getValue(HmusterXML.FACTOR);
			if(factor!=null&&factor.trim().length()>0)
			{
				has=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(con!=null)
			{
				try
				{
					if (con != null) {
						con.close();
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return has;
	}
	
	private int getCount(ContentDAO dao,String tabID) throws SQLException, GeneralException{
		int ret_count=0;
		ArrayList dblist = this.userView.getPrivDbList();
		HmusterBo hmusterBo=new HmusterBo(this.getFrameconn(),this.getUserView());
		HmusterXML hmxml = new HmusterXML(this.getFrameconn(),tabID);
		String flag=hmxml.getValue(HmusterXML.HISTORYMODE);
		String history="";
		if("1".equals(flag))
			history="3";
		else if("0".equals(flag))
			history="1";
		else
			history=flag;
		String year=hmxml.getValue(HmusterXML.YEAR);
		String month=hmxml.getValue(HmusterXML.MONTH);
		String count=hmxml.getValue(HmusterXML.TIMES);
		/**<!--子集历史记录条件,空表示所有子集记录 -->
		 * <SUBSETID>子集id</SUBSETID><SUBFACTOR>因子A0405<>,A0405<=21</SUBFACTOR><SUBEXPR>表达式1*2</SUBEXPR>
		 */
		String subset=hmxml.getValue(HmusterXML.SUBSETID);
		String subfactor=hmxml.getValue(HmusterXML.SUBFACTOR);
		String subexpr=hmxml.getValue(HmusterXML.SUBEXPR);
		/**>模糊查询True|False*/
		String FUZZYFLAG=hmxml.getValue(HmusterXML.FUZZYFLAG);
		String factor = hmxml.getValue(HmusterXML.FACTOR);
		String expr=hmxml.getValue(HmusterXML.EXPR);
		String NO_MANAGE_PRIV=hmxml.getValue(HmusterXML.NO_MANAGE_PRIV);
		String fromScopt="";
		String toScope="";
		String selectedPoint="";
		if("2".equals(flag))
		{
	    	fromScopt=subexpr+"::"+subfactor.replaceAll(",", "`")+"::"+("TRUE".equals(FUZZYFLAG)?"1":"0");	//history=2 1*2::a58z0=`a58z1=`::0
	    	toScope=subset;	//A58
	    	selectedPoint=subset;	//a58
		}
		/**每页行数(0: 自动计算, 1: 用户指定)*/
		String isAutoCount=hmxml.getValue(HmusterXML.ROWCOUNTMODE);
		/**用户指定行数*/
		String pageRows=hmxml.getValue(HmusterXML.ROWCOUNT);
		
		/**对历史记录查询True|False*/
		String HIS=hmxml.getValue(HmusterXML.HIS);
		/**查机构库时，仅查部门True|False*/
		String DEPTONLY=hmxml.getValue(HmusterXML.DEPTONLY);
		/**查机构库时，仅查单位True|False*/
		String UNITONLY=hmxml.getValue(HmusterXML.UNITONLY);
		/**0不汇总,1按人员/单位/职位汇总*/
		String countflag=hmxml.getValue(HmusterXML.NEEDSUM);
		if(!"2".equals(history))
			countflag="0";
		/**USR,RET(人员库，逗号分隔，空表示全部人员库)*/
		String nbase=hmxml.getValue(HmusterXML.NBASE);
		tmpnbase=nbase.toUpperCase();
		String queryScope="0";
		hmusterBo.getFactorSQL(nbase, FUZZYFLAG, HIS, DEPTONLY, UNITONLY, factor, expr, "1", userView,"",NO_MANAGE_PRIV,this.userView.getUserName()+"_muster_"+tabID);
		StringBuffer sqlstr = new StringBuffer();
		for(int i=0;i<dblist.size();i++){
			String dbpre = (String)dblist.get(i);
			if(tmpnbase.indexOf(dbpre.toUpperCase())!=-1){
				String sql ="";
				if("1".equals(countflag)){
					sql = hmusterBo.createCountSQL(history, this.userView.getUserName(), this.userView.getUserName()+"_muster_"+tabID, tabID,"1", dbpre, queryScope, flag, year, month, count,fromScopt, toScope, selectedPoint, "0", "");
				}else{
					sql = hmusterBo.createSQL2(history, this.userView.getUserName(), this.userView.getUserName()+"_muster_"+tabID, tabID,"1", dbpre, queryScope, flag, year, month, count,fromScopt, toScope, selectedPoint, "0", "");
				}
				sql=sql.substring(sql.toUpperCase().indexOf("SELECT"),sql.toUpperCase().indexOf("ORDER BY"));
				//System.out.println(sql);
				sqlstr.append(sql+" union all ");
			}
		}
		sqlstr.setLength(sqlstr.length()-11);
		this.frecset = dao.search(sqlstr.toString());
		while(this.frecset.next()){
			ret_count++;
		}
		return ret_count;
	}
	
	private HmusterBo initBo(String tabID){
		HmusterBo hmusterBo=new HmusterBo(this.getFrameconn(),this.getUserView());
		HmusterXML hmxml = new HmusterXML(this.getFrameconn(),tabID);
		String factor = hmxml.getValue(HmusterXML.FACTOR);
		String expr=hmxml.getValue(HmusterXML.EXPR);
		String NO_MANAGE_PRIV=hmxml.getValue(HmusterXML.NO_MANAGE_PRIV);
		String historyRecord="0";
	    /**取子集记录方式：0当前记录(默认值),1某次历史记录,2根据条件取历史记录*/
		String flag=hmxml.getValue(HmusterXML.HISTORYMODE);
		String year=hmxml.getValue(HmusterXML.YEAR);
		String month=hmxml.getValue(HmusterXML.MONTH);
		String count=hmxml.getValue(HmusterXML.TIMES);
		String subset=hmxml.getValue(HmusterXML.SUBSETID);
		String subfactor=hmxml.getValue(HmusterXML.SUBFACTOR);
		String subexpr=hmxml.getValue(HmusterXML.SUBEXPR);
		/**>模糊查询True|False*/
		/**每页行数(0: 自动计算, 1: 用户指定)*/
		String isAutoCount=hmxml.getValue(HmusterXML.ROWCOUNTMODE);
		/**用户指定行数*/
		String pageRows=hmxml.getValue(HmusterXML.ROWCOUNT);
		/**>模糊查询True|False*/
		String FUZZYFLAG=hmxml.getValue(HmusterXML.FUZZYFLAG);
		/**对历史记录查询True|False*/
		String HIS=hmxml.getValue(HmusterXML.HIS);
		/**查机构库时，仅查部门True|False*/
		String DEPTONLY=hmxml.getValue(HmusterXML.DEPTONLY);
		/**查机构库时，仅查单位True|False*/
		String UNITONLY=hmxml.getValue(HmusterXML.UNITONLY);
		/**0不汇总,1按人员/单位/职位汇总*/
		String needsum=hmxml.getValue(HmusterXML.NEEDSUM);
		/**USR,RET(人员库，逗号分隔，空表示全部人员库)*/
		String nbase=hmxml.getValue(HmusterXML.NBASE);
		tmpnbase=nbase.toUpperCase();
		String queryScope="0";
		hmusterBo.getFactorSQL(nbase, FUZZYFLAG, HIS, DEPTONLY, UNITONLY, factor, expr, "1", userView,"",NO_MANAGE_PRIV,this.userView.getUserName()+"_muster_"+tabID);
		//hmusterBo.getDbpreSQL(dbpre)
		
		return hmusterBo;
	}
}
