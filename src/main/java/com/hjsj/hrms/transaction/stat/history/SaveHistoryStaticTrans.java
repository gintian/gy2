package com.hjsj.hrms.transaction.stat.history;

import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.StatCondAnalyse;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/*子集为按月变化，用户填写规则为
2009-01-01 1
2009-02-01 1
2009-03-01 1
。。。。
按年变化子集
2009-01-01 1
按季变化
2009-01-01 1
2009-04-01 2
2009-07-01 3
2009-10-01 4
半年变化
2009-01-01 1
2009-07-01 2*/
public class SaveHistoryStaticTrans  extends IBusiness {
	public void execute() throws GeneralException {
		String statid=(String)this.getFormHM().get("statid");
		ArrayList list=(ArrayList)this.getFormHM().get("list");
		String archive_type=(String)this.getFormHM().get("archive_type");
		String year=(String)this.getFormHM().get("year");
		String month=(String)this.getFormHM().get("month");
		String userid=(String)this.getFormHM().get("userid");
		if(archive_type==null||archive_type.length()<=0)
		{
			this.getFormHM().put("flag", "0");
			return;
		}else if(year==null||year.length()<=0)
		{
			this.getFormHM().put("flag", "0");
			return;
		}else if(month==null||month.length()<=0)
		{
			this.getFormHM().put("flag", "0");
			return;
		}else if(list.isEmpty())
		{
			this.getFormHM().put("flag", "0");
			return;
		}else if(userid==null||userid.length()<=0)
		{
			this.getFormHM().put("flag", "0");
			return;
		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String setName="";
		String nbase="";
		String condid="";
		String infokind="1";
		String sql="select * from SName where id="+statid;
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				setName=this.frowset.getString("archive_set");
				nbase = this.frowset.getString("nbase");
				condid = this.frowset.getString("condid");
				infokind = this.frowset.getString("infokind");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(setName==null||setName.length()<=0)
		{
			this.getFormHM().put("flag", "0");
			return;
		}	
		
		String z0="";//年月标示
		String z1="1";//次数
		if("1".equals(archive_type))//月
		{
			z0=year+"-"+month+"-01";
		}else if("2".equals(archive_type))//季
		{
			if("1".equals(month))
			{
				z0=year+"-01-01";
				
			}else if("2".equals(month))
			{
				z0=year+"-04-01";
			}else if("3".equals(month))
			{
				z0=year+"-07-01";
			}else
			{
				z0=year+"-10-01";
			}
			z1=month;
		}else if("3".equals(archive_type))//半年
		{
			if("1".equals(month))
			{
				z0=year+"-01-01";
			}else
			{
				z0=year+"-07-01";
			}
			z1=month;
		}else if("4".equals(archive_type))//年
		{
			z0=year+"-01-01";
		}
		ArrayList newlist = new ArrayList();
		StringBuffer sb =new StringBuffer();
		if(nbase==null||nbase.length()<2){
			ArrayList dblist = this.userView.getPrivDbList();
			for(int n=0;n<dblist.size();n++){
				sb.append(","+((String)dblist.get(n)));
			}
			nbase = sb.substring(1);
		}
		/*for(int i=0;i<list.size();i++)
		{
			LazyDynaBean rec=(LazyDynaBean)list.get(i);
			if(rec==null)
				continue;
			String legend=(String)rec.get("name");
			getNewList(statid,legend,dao,newlist,userid,nbase.toUpperCase(),condid,infokind);
		}*/
		userid=userid.substring(2);
		StringBuffer buf=new StringBuffer();
		buf.append("select i9999 from "+setName);
		buf.append(" where b0110='"+userid+"'");
		buf.append(" and "+setName+"z0="+Sql_switcher.dateValue(z0));
		buf.append(" and "+setName+"z1="+z1);
		try {
			RecordVo vo = new RecordVo(setName);
			vo.setString("b0110", userid);
			vo.setDate(setName.toLowerCase()+"z0", DateUtils.getDate(z0,"yyyy-MM-dd"));
			vo.setString(setName.toLowerCase()+"z1",z1);
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean rec=(LazyDynaBean)list.get(i);
				if(rec==null)
					continue;
				String legend=(String)rec.get("name");
				//String archive_field=(String)rec.get("name");
				String value=(String)rec.get("value");
				String archive_field=getArchive_field(statid,legend,dao);
				vo.setString(archive_field, value);
				
			}
			this.frowset=dao.search(buf.toString());
			if(this.frowset.next())
			{
				int i9999=this.frowset.getInt("i9999");
				vo.setInt("i9999", i9999);
				vo.setDate("modtime", new Date());				
				vo.setString("modusername", this.userView.getUserName());
				dao.updateValueObject(vo);
			}else{
				StructureExecSqlString structureExecSqlString=new StructureExecSqlString();
				String i9999=structureExecSqlString.getUserI9999(setName,userid,"B0110",this.getFrameconn());
				vo.setInt("i9999", Integer.parseInt(i9999));
				vo.setDate("createtime", new Date());
				vo.setDate("modtime", new Date());
				vo.setString("createusername", this.userView.getUserName());
				vo.setString("modusername", this.userView.getUserName());
				dao.addValueObject(vo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.getFormHM().put("flag", "0");
		}
		
	}
    private String getArchive_field(String id,String legend,ContentDAO dao)
    {
    	String archive_field="";
    	String sql="select archive_field from slegend  where id="+id+" and legend='"+legend+"'";
    	try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
				archive_field=this.frowset.getString("archive_field");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return archive_field;
    }
    
    private void getNewList(String id,String legend,ContentDAO dao,ArrayList newlist,String userid,String nbase,String condid,String infokind) throws GeneralException{
    	String sql="select archive_field,LExpr,Factor from slegend  where id="+id+" and legend='"+legend+"'";
    	String archive_field="";
    	String LExpr="";
    	String Factor="";
    	LazyDynaBean rec=new LazyDynaBean();
    	try {
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				archive_field=this.frowset.getString("archive_field");
				LExpr=this.frowset.getString("LExpr");
				Factor=this.frowset.getString("Factor");
				if(condid!=null&&condid.length()>0){
					GeneralQueryStat generalstat=new GeneralQueryStat();
					generalstat.combineCond(condid, LExpr, Factor, this.frameconn);
					LExpr = generalstat.getLexpr();
					Factor = generalstat.getLfactor();
				}
				StatCondAnalyse cond = new StatCondAnalyse();
				String strQuery =cond.getCondQueryString(
						LExpr,
						Factor,
						"Usr",
						false,
						userView.getUserName(),
						userid,userView,infokind,true);
				//System.out.println(strQuery);
				if("1".equals(infokind)){
					StringBuffer sb = new StringBuffer();
					String tmpsql = ("select distinct Usra01.a0100 as a0100" + strQuery).toUpperCase();
					if(nbase.indexOf(",")==-1){
						sb.append(tmpsql.replaceAll("USR", nbase));
					}else{
						String[] tmpdbpres=nbase.split(",");
						for(int n=tmpdbpres.length-1;n>=0;n--){
							String tmpdbpre=tmpdbpres[n];
							if(tmpdbpre.length()==3){
								if(sb.length()>0){
									sb.append(" union all "+tmpsql.replaceAll("USR", tmpdbpre));
								}else{
									sb.append(tmpsql.replaceAll("USR", tmpdbpre));
								}
							}
						}
					}
					strQuery = "select count(a0100) as lexprData from (" + sb.toString()+") tt";
		         	//strQuery = "select count(distinct " + userbase + "a01.a0100) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
				}else if("2".equals(infokind))
		    		strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
		    	else if("3".equals(infokind))
		    		strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
				//System.out.println(strQuery);
				List rsset = ExecuteSQL.executeMyQuery(strQuery);
				rec.set("value", (String)((LazyDynaBean)rsset.get(0)).get("lexprdata"));
				rec.set("name", archive_field);
				newlist.add(rec);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
