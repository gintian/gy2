/*
 * Created on 2006-5-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.module.card.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * 快速查询
 */
public class SearchPersonListTrans extends IBusiness {

	
	@Override
    public void execute() throws GeneralException {
		try{
			String dbname=(String)this.getFormHM().get("dbname");
			String inforkind=(String)this.getFormHM().get("inforkind");
			String comSearch=(String)this.getFormHM().get("comSearch");//输入内容查询标记
			if(comSearch!=null&&"1".equals(comSearch)){
				String A0101=(String)this.getFormHM().get("A0101");
				getPersonList(inforkind,A0101.trim(),dbname);
			}
			this.getFormHM().put("flagType", true);
		}catch(Exception ex)
		{
			this.getFormHM().put("flagType", false);
			this.getFormHM().put("eMsg", ex.getMessage());
			ex.printStackTrace();
		}

	}
	public void getPersonList(String inforkind,String a0101,String userbase) throws Exception{
			String sql="";
			ContentDAO dao=new ContentDAO(this.frameconn);
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一标识 唯一标识关联人员
			String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);//拼音简码
			String backdate=DateUtils.format(new Date(), "yyyy-MM-dd");//单位 岗位 基准岗位 关联日期
			String codeValue=userView.getUnitPosWhereByPriv("codeitemid");
			ArrayList dbList=this.userView.getPrivDbList();
			if(dbList==null)
				dbList.add(this.userView.getDbname());
			if(!(a0101==null||a0101.length()<1)) {//空查询 不查
				if("1".equals(inforkind)){//人员
					StringBuffer sqlStr=new StringBuffer();
					for(int i=0;i<dbList.size();i++) {
						String dbname=dbList.get(i).toString();
						String privsql=this.userView.getPrivSQLExpression(dbname, true);
						if(StringUtils.isNotEmpty(privsql)) {
							sqlStr.append("select distinct '"+this.userView.getUserName()+"' as username,'"+dbname+"' as nbase,"
									+ dbname+"A01.A0100 as objid,"+inforkind+" as flag,"+this.userView.getStatus()+" as status ");
							sqlStr.append(privsql);
						}else {
							sqlStr.append("select distinct '"+this.userView.getUserName()+"' as username,'"+dbname+"' as nbase,"
									+dbname+"A01.A0100 as objid,"+inforkind+" as flag,"+this.userView.getStatus()+" as status from "+dbname+"A01 where ");
						}
						
						sqlStr.append(" and ( ");
						sqlStr.append(" A0101 like '%"+a0101+"%' ");
						if(StringUtils.isNotEmpty(onlyname)) {
							sqlStr.append(" or "+onlyname+" like '%"+a0101+"%'" );
						}
						if(StringUtils.isNotEmpty(pinyin_field)) {
							sqlStr.append(" or "+pinyin_field+" like '%"+a0101+"%'" );
						}
						sqlStr.append(" ) ");
						
						if(i<dbList.size()-1)
							sqlStr.append(" union all ");
						
					}
					sql=sqlStr.toString();
				}else if("2".equals(inforkind)){//单位
					 sql = " select codeitemid as objid from organization  where ( codesetid='UN' or codesetid='UM') and codeitemdesc like '%"+a0101+"%'" +
							" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and  "
					+ codeValue;
				}else if("4".equals(inforkind)){//岗位
					sql=" select codeitemid as objid from organization  where codesetid='@K' and codeitemdesc like '%"+a0101+"%'" +
					" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and  "+ codeValue;
				}else if("6".equals(inforkind)){//基准岗位
					String codesetid="";
					com.hrms.frame.dao.RecordVo constantuser_vo = ConstantParamter.getRealConstantVo("PS_C_CODE");
		            if (constantuser_vo != null)
		                 codesetid = constantuser_vo.getString("str_value");
		            sql=" select codeitemid as objid" +
		            	"  from H01 right join codeitem  on codeitem.codeitemid=H01.H0100  " +
		            	"where codesetid='"+codesetid+"' and "+Sql_switcher.dateValue(backdate)+
		            	"between start_date and end_date  and codeitem.codeitemdesc like '%"+a0101+"%'";
					
				}
				//插入临时表 t_card_result
					DbWizard db=new DbWizard(this.frameconn);
					Boolean dbflag=false;
					dbflag=db.isExistTable("t_card_result", false);
					if(!dbflag) {//业务用户临时表 无此表时 创建查询结果表   //自助与业务用户共用同一张临时表 
						Table table=new Table("t_card_result");
						//table.setName(this.userView.getUserName()+dbper+"Result");
						Field username=new Field("username","username");
						username.setDatatype(DataType.STRING);
						username.setLength(50);
						
						Field nbase=new Field("nbase","nbase");
						nbase.setDatatype(DataType.STRING);
						nbase.setLength(3);
						
						Field objid=new Field("objid","objid");
						objid.setDatatype(DataType.STRING);
						objid.setLength(50);
						
						Field flag=new Field("flag","flag");
						flag.setDatatype(DataType.INT);
						
						Field status=new Field("status","status");//自助用户业务用户区分 0 业务用户  4 自助用户
						status.setDatatype(DataType.INT);
						
						table.addField(username);
						table.addField(nbase);
						table.addField(objid);
						table.addField(flag);
						table.addField(status);
						db.createTable(table);
						
					}
					StringBuffer delSql=new StringBuffer();
					delSql.append("delete from t_card_result where username='"+this.userView.getUserName()+"'");
					delSql.append(" and status="+this.userView.getStatus());
					if("1".equals(inforkind)) {
						delSql.append(" and nbase in (");
						for(int i=0;i<dbList.size();i++) {
							delSql.append("'"+dbList.get(i).toString()+"'");
							if(i<dbList.size()-1)
								delSql.append(",");
						}
						delSql.append(" )");
					}
					delSql.append(" and flag="+Integer.parseInt(inforkind));
					
					StringBuffer instSql=new StringBuffer();
					instSql.append("insert into t_card_result(username,nbase,objid,flag,status)");
					if(!"1".equals(inforkind)) {
						instSql.append(" select '"+this.userView.getUserName()+"' as username,");
						if("2".equals(inforkind)) {
							instSql.append("'B' as nbase,");
						}else if("4".equals(inforkind)) {
							instSql.append("'K' as nbase,");
						}else if("6".equals(inforkind)) {
							instSql.append("'H' as nbase,");
						}
						instSql.append("A.objid as objid, "+Integer.parseInt(inforkind)+" as flag,"
								+ " "+this.userView.getStatus()+" as status");
						instSql.append(" from ("+sql+") A");
					}else {
						instSql.append(sql);
					}
					dao.update(delSql.toString());
					dao.update(instSql.toString());
			}
	}

}
