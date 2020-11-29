package com.hjsj.hrms.transaction.performance.data_collect;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.data_collect.DataCollectBo;
import com.hjsj.hrms.businessobject.performance.data_collect.Data_collectBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
/**
 * 
* 
* 类名称：SearchDataTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 21, 2013 11:57:11 AM   
* 修改人：zhaoxg   
* 修改时间：Aug 21, 2013 11:57:11 AM   
* 修改备注：   数据采集主界面
* @version    
*
 */
public class SearchDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
		HashMap hm =(HashMap) this.getFormHM().get("requestPamaHM");
		String fieldsetid =(String) hm.get("fieldsetid");
		String sql = "select * from constant where Constant = 'DATA_COLLECT_SCOPE'";
		DataCollectBo bo = new DataCollectBo(this.frameconn,"Params");
		DataCollectBo databo = new DataCollectBo(this.frameconn,this.userView);
		String cbase  = bo.getXmlValue1("cbase",fieldsetid);
		String state_id  = bo.getXmlValue1("state_id",fieldsetid);
		String set_id  = bo.getXmlValue1("set_id",fieldsetid);
		String flag  = bo.getXmlValue1("flag",fieldsetid);
		String value = bo.getValue(fieldsetid);
		boolean have = false;
		String sqll = "select useflag from fielditem where itemid = '"+state_id+"' and fieldsetid = '"+set_id+"'";
		RowSet rs1 = dao.search(sqll);
		if(rs1.next()){
			if("1".equals(rs1.getString("useflag"))){
				have = true;
			}
		}
		RowSet rs = dao.search(sql);
		if(have&&rs.next()&&fieldsetid!=null&&!"".equals(fieldsetid)&&fieldsetid.equals(set_id)){
			HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
			String code = "";
			String filtervalue = (String)this.getFormHM().get("filtervalue");//月标识
			String spType = (String)this.getFormHM().get("spType");// 审批状态
			if(spType==null){
				spType="0";
			}
			String dbname=(String)this.getFormHM().get("dbname");//数据库
		
			code=(String)reqhm.get("a_code");															
			code=code!=null&&code.trim().length()>0?code:"";
			
			String codeitemid = (String)this.getFormHM().get("codeitemid");
			codeitemid=codeitemid!=null&&codeitemid.trim().length()>0?codeitemid:"";//单位号
			if(code.trim().length()>=2){
				codeitemid = code.substring(2,code.length());
			}
			else
				codeitemid="";
			
			String yearnum = (String)this.getFormHM().get("yearnum");//年标识
			yearnum=yearnum!=null&&yearnum.trim().length()>0?yearnum:"";
			Calendar  calendar = Calendar.getInstance();
			if(yearnum.trim().length()!=4){				
				yearnum = calendar.get(Calendar.YEAR)+"";
			}
			if(filtervalue==null)
				filtervalue=(calendar.get(Calendar.MONTH)+1)+"";	
			String ym = "";
			if(!"0".equals(filtervalue)){
				ym = yearnum+"."+filtervalue+"."+01;
			}
			

			String pre = cbase.split(",")[0];
			if(dbname!=null&&!"".equals(dbname)){
				pre=dbname;
			}
			StringBuffer _sql1 = new StringBuffer("");
			String priStrSql = InfoUtils.getWhereINSql(this.userView, pre);
			_sql1.append("select "+pre+"a01.A0100 ");
			if (priStrSql.length() > 0)
				_sql1.append(priStrSql);
			else
				_sql1.append(" from "+pre+"a01");
			
			String SQL = "select * from "+pre+set_id+" where 1=2";
			RowSet temprs = dao.search(SQL);
			ResultSetMetaData data=temprs.getMetaData();
			StringBuffer buf = new StringBuffer();
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
				 buf.append(",");
				 buf.append(data.getColumnName(i).toLowerCase());
			 }
			 if(temprs!=null){
				 temprs.close();
			 }
			StringBuffer tempsql = new StringBuffer();
			StringBuffer _sql = new StringBuffer();
			 _sql.append("select  ('"+pre+"'"+Sql_switcher.concat()+"':'"+Sql_switcher.concat()+""+pre+set_id+".A0100"+""+Sql_switcher.concat()+"':'"+Sql_switcher.concat()+""+Sql_switcher.sqlToChar(pre+set_id+".I9999")+") as pre,(select B0110 from "+pre+"A01 where "+pre+"A01.A0100="+pre+set_id+".A0100) as B0110, (select E0122 from "+pre+"A01 where "+pre+"A01.A0100="+pre+set_id+".A0100) as E0122,(select A0101 from "+pre+"A01 where "+pre+"A01.A0100="+pre+set_id+".A0100) as A0101,"+state_id+" as zt,(select E01A1 from "+pre+"A01 where "+pre+"A01.A0100="+pre+set_id+".A0100) as E01A1 "+buf.toString()+" from "+pre+set_id+"");
			 _sql.append(" where 1=1 ");
			 if(spType!=null&&!"0".equals(spType))
				 _sql.append(" and "+state_id+"='"+spType+"' ");
			 _sql.append(" and "+Sql_switcher.year(fieldsetid+"z0")+"="+yearnum+" ");
			 if(filtervalue!=null&&!"0".equals(filtervalue))
				 _sql.append(" and "+Sql_switcher.month(fieldsetid+"z0")+"="+filtervalue+" ");
			 if(code.trim().length()>2&& "UM".equals(code.substring(0, 2))){
				 	tempsql.append("select * from (");
			 		tempsql.append(_sql);
			 		if(Sql_switcher.searchDbServer()== Constant.ORACEL){
			 			tempsql.append(") where E0122 like '"+codeitemid+"%"+"'");
			 		}else{
			 			tempsql.append(") as aa where aa.E0122 like '"+codeitemid+"%"+"'");
			 		}			 		
			 		_sql = new StringBuffer();
			 		_sql.append(tempsql);
			 }else if(code.trim().length()>2&& "UN".equals(code.substring(0, 2))){
				 	tempsql.append("select * from (");
			 		tempsql.append(_sql);
			 		if(Sql_switcher.searchDbServer()== Constant.ORACEL){
			 			tempsql.append(")  where B0110 like '"+codeitemid+"%"+"'");
			 		}else{
			 			tempsql.append(") as aa where aa.B0110 like '"+codeitemid+"%"+"'");
			 		}
			 		
			 		_sql = new StringBuffer();
			 		_sql.append(tempsql);
			 }
	    		if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
	    		{
	    			
	    		}
	    		else
	    		{
	    			_sql.append(" and a0100 in ( "+_sql1+")"); 
	    		}
			 _sql.append("order by a0100,"+fieldsetid+"z0,"+fieldsetid+"z1"); 
			 HashMap _hm = new HashMap();
			 _hm.put("ym", ym);
			 _hm.put("pre", pre);
			 _hm.put("fieldsetid", fieldsetid);
			 this.userView.setHm(_hm);
			ArrayList dblist = databo.getDbList(cbase);
			ArrayList filterList = databo.getFilterList();			
			ArrayList spTypeList = databo.getSpTypeList();
			ArrayList fieldlist=databo.getfieldlist(fieldsetid, state_id);
			this.getFormHM().put("dblist",dblist);
			this.getFormHM().put("filterList",filterList);
			this.getFormHM().put("spTypeList",spTypeList);
			this.getFormHM().put("isHave", "1");
			this.getFormHM().put("yearnum", yearnum);
			this.getFormHM().put("_sql", _sql.toString());
			this.getFormHM().put("fieldlist", fieldlist);
			this.getFormHM().put("fieldsetid", fieldsetid);
			this.getFormHM().put("tablename", pre+set_id);
			this.getFormHM().put("dbname", pre);
			this.getFormHM().put("filtervalue", filtervalue);
			this.getFormHM().put("zt", state_id);
			this.getFormHM().put("ym", ym);
		}else{

			String[] dbid =(String[]) this.getFormHM().get("dbid");
			String cexpr="";
			String personScope="-1";
			Data_collectBo dbo=new Data_collectBo();
			Connection conn = this.getFrameconn();
			ArrayList ValueList = dbo.getXmlValue(conn);
			HashMap VMap = new HashMap();
			for(int i=0;i<ValueList.size();i++){
				HashMap temMap = (HashMap) ValueList.get(i);
				if(fieldsetid.equals((String)temMap.get("set_id"))){
					VMap=temMap;
					break;
				}
			}
			if(!(VMap.get("dbid")==null||"".equals(VMap.get("dbid")))){
				dbid= ((String)VMap.get("dbid")).split(",");
			}else{
				dbid=new String[0];
			}
			cexpr=(String) VMap.get("cexpr");
			personScope=(String)VMap.get("flag");
			ArrayList auditList =dbo.getAudit(fieldsetid, conn);
			ArrayList dbList = dbo.getDbList(dbid, conn);
			this.getFormHM().put("cexpr",cexpr);
			this.getFormHM().put("auditList", auditList);
			this.getFormHM().put("dbList", dbList);
			this.getFormHM().put("dbid", dbid);
			this.getFormHM().put("personScope", personScope);
			this.getFormHM().put("fieldsetid", fieldsetid);
			this.getFormHM().put("isHave", "2");
			
		}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
