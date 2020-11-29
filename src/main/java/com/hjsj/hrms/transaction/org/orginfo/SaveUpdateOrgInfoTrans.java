package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveUpdateOrgInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		String codeitemid=(String)this.getFormHM().get("codeitemid");
		String codeitemdesc=(String)this.getFormHM().get("codeitemdesc");
		String codesetid=(String)this.getFormHM().get("codesetid");
		String corcode=(String)this.getFormHM().get("corcode");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = (String)this.getFormHM().get("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		corcode=corcode==null?"":corcode;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String corcode_unique=com.hrms.struts.constant.SystemConfig.getPropertyValue("corcode_unique");
		if("1".equals(corcode_unique)&&corcode.length()>0){
			try {
				this.frowset=dao.search("select codeitemid,codeitemdesc from (select codeitemid,codeitemdesc from organization where corcode='"+corcode+"' and codeitemid<>'"+codeitemid+"'  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  union all select codeitemid,codeitemdesc from vorganization where corcode='"+corcode+"' and codeitemid<>'"+codeitemid+"'  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ) tt");
				if(this.frowset.next()){
						if("@K".equals(codesetid))
							throw GeneralExceptionHandler.Handle(new GeneralException("","岗位代码值\""+corcode+"\"在系统中已存在["+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")]，必需唯一!","",""));
						if("UM".equals(codesetid))
							throw GeneralExceptionHandler.Handle(new GeneralException("","部门代码值\""+corcode+"\"在系统中已存在["+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")]，必需唯一!","",""));
						if("UN".equals(codesetid))
							throw GeneralExceptionHandler.Handle(new GeneralException("","单位代码值\""+corcode+"\"在系统中已存在["+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")]，必需唯一!","",""));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}
		
		String isorg = (String)this.getFormHM().get("isorg");
		String start_date = (String)this.getFormHM().get("start_date");
		String end_date = (String)this.getFormHM().get("end_date");// xuj 2009-10-30 在organazition（vorganization虚拟机构）表中增加“有效日期起”、“有效日期止”两个字段
		StringBuffer sqlstr=new StringBuffer();
		sqlstr.delete(0,sqlstr.length());
		try{
		  if("org".equalsIgnoreCase(isorg))
			  sqlstr.append("update organization set codeitemdesc=?");
		  else if("vorg".equalsIgnoreCase(isorg))
			  sqlstr.append("update vorganization set codeitemdesc=?");		 
		  sqlstr.append(" ,corcode=?,start_date="+Sql_switcher.dateValue(start_date)+",end_date="+Sql_switcher.dateValue(end_date)+" where codeitemid='");
		  sqlstr.append(codeitemid);
		  sqlstr.append("'");	
		  ArrayList list=new ArrayList();
		  RecordVo vo = new RecordVo("organization");
		  Map lenmap = vo.getAttrLens();
		  int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
		  list.add(PubFunc.splitString(codeitemdesc,codeitemdesclen));
		  list.add(corcode);
		  AdminCode.updateCodeItemDesc(codesetid,codeitemid,PubFunc.splitString(codeitemdesc,codeitemdesclen));
		  dao.update(sqlstr.toString(),list);

		  try{
				String sql = null;
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar calendar = Calendar.getInstance();
				String date = sdf.format(calendar.getTime());
				if("@K".equalsIgnoreCase(codesetid)){
					
					RecordVo pos_code_field_constant_vo=ConstantParamter.getRealConstantVo("POS_CODE_FIELD",this.getFrameconn());
					if(pos_code_field_constant_vo!=null)
					{
					  String  pos_code_field=pos_code_field_constant_vo.getString("str_value");
					  FieldItem pos_code_fieldItem = DataDictionary.getFieldItem(pos_code_field);
					  if(pos_code_field!=null&&pos_code_field.length()>1 && pos_code_fieldItem != null && "1".equals(pos_code_fieldItem.getUseflag())){
						  sql = "select e01a1 from K01 where e01a1='"+codeitemid.toUpperCase()+"'";
						  this.frecset = dao.search(sql);
						  if(this.frecset.next()){
							  sql = "update K01 set e0122='"+getTargetUMCodeitemid(isorg,codeitemid)+"',"+pos_code_field+"='"+corcode+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1='"+codeitemid.toUpperCase()+"'";
						  }else{
							  sql = "insert into K01(e0122,e01a1,"+pos_code_field+",createusername,modusername,createtime,modtime) values ('"+getTargetUMCodeitemid(isorg,codeitemid)+"','"+(codeitemid).toUpperCase()+"','"+corcode+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
						  }
						  dao.update(sql);
					  }else{
						  sql = "select e01a1 from K01 where e01a1='"+codeitemid.toUpperCase()+"'";
						  this.frecset = dao.search(sql);
						  if(this.frecset.next()){
							  sql = "update K01 set e0122='"+getTargetUMCodeitemid(isorg,codeitemid)+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1='"+codeitemid.toUpperCase()+"'";
						  }else{
							  sql = "insert into K01(e0122,e01a1,createusername,modusername,createtime,modtime) values ('"+getTargetUMCodeitemid(isorg,codeitemid)+"','"+(codeitemid).toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
						  }
						  dao.update(sql);
					  }
					}else{
						sql = "select e01a1 from K01 where e01a1='"+codeitemid.toUpperCase()+"'";
						  this.frecset = dao.search(sql);
						  if(this.frecset.next()){
							  sql = "update K01 set e0122='"+getTargetUMCodeitemid(isorg,codeitemid)+"',createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1='"+codeitemid.toUpperCase()+"'";
						  }else{
							  sql = "insert into K01(e0122,e01a1,createusername,modusername,createtime,modtime) values ('"+getTargetUMCodeitemid(isorg,codeitemid)+"','"+(codeitemid).toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
						  }
						  dao.update(sql);
					}
				}else{
					RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.getFrameconn());
					if(unit_code_field_constant_vo!=null)
					{
					  String  unit_code_field=unit_code_field_constant_vo.getString("str_value");
					  FieldItem unit_code_fieldItem = DataDictionary.getFieldItem(unit_code_field);
					  if(unit_code_field!=null&&unit_code_field.length()>1 && unit_code_fieldItem != null && "1".equals(unit_code_fieldItem.getUseflag())){
						  sql = "select b0110 from B01 where b0110='"+(codeitemid).toUpperCase()+"'";
						  this.frecset = dao.search(sql);
						  if(this.frecset.next()){
							  sql = "update B01 set "+unit_code_field+"='"+corcode+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+"  where b0110='"+(codeitemid).toUpperCase()+"'";
						  }else{
							  sql = "insert into B01(b0110,"+unit_code_field+",createusername,modusername,createtime,modtime) values ('"+(codeitemid).toUpperCase()+"','"+corcode+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
						  }
						  dao.update(sql);
					  }else{
						  sql = "select b0110 from B01 where b0110='"+(codeitemid).toUpperCase()+"'";
						  this.frecset = dao.search(sql);
						  if(this.frecset.next()){
							  sql = "update B01 set createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+"  where b0110='"+(codeitemid).toUpperCase()+"'";
						  }else{
							  sql = "insert into B01(b0110,createusername,modusername,createtime,modtime) values ('"+(codeitemid).toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
						  }
						  dao.update(sql);
					  }
					}else{
						sql = "select b0110 from B01 where b0110='"+(codeitemid).toUpperCase()+"'";
						  this.frecset = dao.search(sql);
						  if(this.frecset.next()){
							  sql = "update B01 set createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+"  where b0110='"+(codeitemid).toUpperCase()+"'";
						  }else{
							  sql = "insert into B01(b0110,createusername,modusername,createtime,modtime) values ('"+(codeitemid).toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
						  }
						  dao.update(sql);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		  this.getFormHM().put("isrefresh","update");
		  this.getFormHM().put("codeitemdesc",PubFunc.splitString(codeitemdesc,codeitemdesclen));
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	private String getTargetUMCodeitemid(String isorg,String code)
	{
		String pre="@K";
		String uncodeitemid="";
     	StringBuffer strsql=new StringBuffer();
     	ResultSet rs = null;
     	String table="organization";
		try{
			
			   if("vorg".equalsIgnoreCase(isorg))
				   table= "vorganization";	
				ContentDAO db=new ContentDAO(this.getFrameconn());
				strsql.delete(0,strsql.length());
				strsql.append("select codesetid from "+table+" where codeitemid='"+code+"'");
				rs = db.search(strsql.toString());
				while(rs.next()){
					pre=rs.getString("codesetid");
					uncodeitemid = code;
				}
				if("@K".equalsIgnoreCase(pre))
				{
					strsql.delete(0,strsql.length());
					strsql.append("select * from "+table);
					strsql.append(" where codeitemid='");
					strsql.append(code);
					strsql.append("'");					
					this.frowset =db.search(strsql.toString());	//执行当前查询的sql语句	
					if(this.frowset.next())
					{
						pre=this.frowset.getString("codesetid");
						code=this.frowset.getString("parentid");
						if("@K".equalsIgnoreCase(pre))
							uncodeitemid=code;
					}			
				}				
			 
			}catch (SQLException sqle){
				sqle.printStackTrace();
			}		
		return uncodeitemid;
	}
}
