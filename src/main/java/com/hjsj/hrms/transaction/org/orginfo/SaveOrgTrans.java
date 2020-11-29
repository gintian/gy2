/*
 * Created on 2005-12-13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.businessobject.info.OrgInfoUtils;
import com.hjsj.hrms.businessobject.org.AddOrgInfo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveOrgTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String grade=(String)this.getFormHM().get("grade");
		String first=(String)this.getFormHM().get("first");
		String code=(String)this.getFormHM().get("code");
		
		String codeitemid=(String)this.getFormHM().get("codeitemid");
		String codeitemdesc=(String)this.getFormHM().get("codeitemdesc");
		RecordVo vo = new RecordVo("organization");
		  Map lenmap = vo.getAttrLens();
		  int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
		String codesetid=(String)this.getFormHM().get("codesetid");
		if("".equalsIgnoreCase(code)&&"UM".equalsIgnoreCase(codesetid)){
			throw GeneralExceptionHandler.Handle(new GeneralException("","根节点下不能新增部门，操作失败！","",""));
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = (String)this.getFormHM().get("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate.replace(".", "-"):sdf.format(new java.util.Date());
		String corcode=(String)this.getFormHM().get("corcode");
		corcode=corcode==null?"":corcode;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String corcode_unique=com.hrms.struts.constant.SystemConfig.getPropertyValue("corcode_unique");
		if("1".equals(corcode_unique)&&corcode.length()>0){
			try {
				this.frowset=dao.search("select codeitemid,codeitemdesc from (select codeitemid,codeitemdesc from organization where corcode='"+corcode+"' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  union all select codeitemid,codeitemdesc from vorganization where corcode='"+corcode+"' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ) tt");
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
		String vflag = (String)this.getFormHM().get("vflag");//vflag=0为不能显示虚拟机构，1为可以显示
		String vorganization = (String)this.getFormHM().get("vorganization");//0为选择增加正式机构，1为虚拟机构
		String start_date1 = (String)this.getFormHM().get("start_date");
		String end_date1 = (String)this.getFormHM().get("end_date");// xuj 2009-10-30 在organazition（vorganization虚拟机构）表中增加“有效日期起”、“有效日期止”两个字段
		Date start_date=null;
		Date end_date=null;
		start_date1 = start_date1!=null&&start_date1.length()>9?start_date1:sdf.format(new java.util.Date());
		end_date1 = end_date1!=null&&end_date1.length()>9?end_date1:"9999-12-31";
		try {
			start_date = new Date(sdf.parse(start_date1.replace(".", "-")).getTime());
			end_date = new Date(sdf.parse(end_date1.replace(".", "-")).getTime());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		String pid ="";
		if(code!=null && code.trim().length()>0)
			   pid = code.toUpperCase();
			else
			   pid = (code + codeitemid).toUpperCase();
		StringBuffer sqlstr=new StringBuffer();
		boolean doInitLayer=false;
		AddOrgInfo ao=new AddOrgInfo(this.frameconn);
		if("0".equalsIgnoreCase(vflag)){
			sqlstr.append("select codeitemdesc,codeitemid from organization where  codeitemid like '");
			sqlstr.append((code + codeitemid).toUpperCase());
			sqlstr.append("%' "); 
			sqlstr.append(" union all ");
			sqlstr.append("select codeitemdesc,codeitemid from vorganization where  codeitemid like '");
			sqlstr.append((code + codeitemid).toUpperCase());
			sqlstr.append("%' order by codeitemid");
			
			//System.out.println(sqlstr.toString());
			try{			
				this.frowset=dao.search(sqlstr.toString());
				StringBuffer sb = new StringBuffer();
				while(this.frowset.next()){
					sb.append("、"+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")");
				}
				if(sb.length()>0)
				{	
					String tmp = sb.substring(1);
					this.getFormHM().put("labelmessage",ResourceFactory.getProperty("label.org.adderrors1")+tmp);
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.adderrors1")+tmp,"",""));
			    }
				else
				{
					sqlstr.delete(0,sqlstr.length());
					sqlstr.append("insert into organization(codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,corcode,start_date,end_date,layer)values(?,?,?,?,?,?,?,?,?,?,?,?)");
					ArrayList sqlvalue=new ArrayList();
					sqlvalue.add(codesetid);
					sqlvalue.add((code +codeitemid).toUpperCase());
					sqlvalue.add(PubFunc.splitString(codeitemdesc,codeitemdesclen));
					cat.debug("-------code------------>" + code);
					String parentid="";
					if(code!=null && code.trim().length()>0){
						parentid=code.toUpperCase();
					   sqlvalue.add(code.toUpperCase());
					}else{
						parentid=(code + codeitemid).toUpperCase();
					   sqlvalue.add((code + codeitemid).toUpperCase());
					}
					sqlvalue.add((code + codeitemid).toUpperCase());
					sqlvalue.add(null);
					sqlvalue.add(grade);
					sqlvalue.add(getMaxA0000(code));
					sqlvalue.add(corcode);
					sqlvalue.add(start_date);
					sqlvalue.add(end_date);
					int layer=ao.getLayer(parentid, (code +codeitemid).toUpperCase(), codesetid);
					if(0==layer){
						doInitLayer=true;
					}
					sqlvalue.add(new Integer(layer));
					dao.insert(sqlstr.toString(),sqlvalue);
					CodeItem item=new CodeItem();
					item.setCodeid(codesetid);
					item.setCodeitem((code +codeitemid).toUpperCase());
					item.setCodename(PubFunc.splitString(codeitemdesc,codeitemdesclen));
					if(code!=null && code.trim().length()>0)
						item.setPcodeitem(code.toUpperCase());
				    else
				    	item.setPcodeitem((code +codeitemid).toUpperCase());
					item.setCcodeitem((code +codeitemid).toUpperCase());
					item.setCodelevel(grade);
					//System.out.println(item);					
					AdminCode.addCodeItem(item);
					//System.out.println(AdminCode.getCodeName(codesetid,(code +codeitemid).toUpperCase()));
	            	
					//System.out.println((code + codeitemid).toUpperCase());
					//System.out.println(PubFunc.splitString(codeitemdesc,50));
					AdminCode.updateCodeItemDesc(codesetid,(code + codeitemid).toUpperCase(),PubFunc.splitString(codeitemdesc,codeitemdesclen));
					if("1".equals(first))
					{
						sqlstr.delete(0,sqlstr.length());
						sqlstr.append("update organization set childid='");
						sqlstr.append(code + codeitemid);
						sqlstr.append("' where codeitemid='");
						sqlstr.append(code);
						sqlstr.append("'");	
						dao.update(sqlstr.toString());
					}
				}
			}catch(Exception e){
			   e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e);
			}finally
			{
				try{
				   //this.getFrameconn().commit();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		else if("1".equalsIgnoreCase(vflag)){
			if("0".equalsIgnoreCase(vorganization)){
				sqlstr.append("select codeitemdesc,codeitemid from organization where  codeitemid like '");
				sqlstr.append((code + codeitemid).toUpperCase());
				sqlstr.append("%' ");
				sqlstr.append(" union all ");
				sqlstr.append("select codeitemdesc,codeitemid from vorganization where  codeitemid like '");
				sqlstr.append((code + codeitemid).toUpperCase());
				sqlstr.append("%' order by codeitemid");
				
				//System.out.println(sqlstr.toString());
				try{			
					this.frowset=dao.search(sqlstr.toString());
					StringBuffer sb = new StringBuffer();
					while(this.frowset.next()){
						sb.append("、"+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")");
					}
					if(sb.length()>0)
					{	
						String tmp = sb.substring(1);
						this.getFormHM().put("labelmessage",ResourceFactory.getProperty("label.org.adderrors1")+tmp);
						throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.adderrors1")+tmp,"",""));
				    }
					else
					{
						sqlstr.delete(0,sqlstr.length());
						//新增时添加levelA0000 同级排序 字段  wangb  20170807
						sqlstr.append("insert into organization(codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,corcode,start_date,end_date,layer,levelA0000)values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
						ArrayList sqlvalue=new ArrayList();
						sqlvalue.add(codesetid);
						sqlvalue.add((code +codeitemid).toUpperCase());
						sqlvalue.add(PubFunc.splitString(codeitemdesc,codeitemdesclen));
						cat.debug("-------code------------>" + code);
						String parentid="";
						if(code!=null && code.trim().length()>0){
							parentid=code.toUpperCase();
						   sqlvalue.add(code.toUpperCase());
						}else{
							parentid=(code + codeitemid).toUpperCase();
						   sqlvalue.add((code + codeitemid).toUpperCase());
						}
						sqlvalue.add((code + codeitemid).toUpperCase());
						sqlvalue.add(null);
						sqlvalue.add(grade);
						sqlvalue.add(getMaxA0000(code));
						sqlvalue.add(corcode);
						sqlvalue.add(start_date);
						sqlvalue.add(end_date);
						int layer=ao.getLayer(parentid, (code +codeitemid).toUpperCase(), codesetid);
						if(0==layer){
							doInitLayer=true;
						}
						sqlvalue.add(new Integer(layer));
						//levelA0000 新增机构的最大levelA0000 值
						sqlvalue.add(getMaxLevelA0000(code));
						dao.insert(sqlstr.toString(),sqlvalue);
						CodeItem item=new CodeItem();
						item.setCodeid(codesetid);
						item.setCodeitem((code +codeitemid).toUpperCase());
						item.setCodename(PubFunc.splitString(codeitemdesc,codeitemdesclen));
						if(code!=null && code.trim().length()>0)
							item.setPcodeitem(code.toUpperCase());
					    else
					    	item.setPcodeitem((code +codeitemid).toUpperCase());
						item.setCcodeitem((code +codeitemid).toUpperCase());
						item.setCodelevel(grade);
						//System.out.println(item);
					
						AdminCode.addCodeItem(item);
						//System.out.println(AdminCode.getCodeName(codesetid,(code +codeitemid).toUpperCase()));
		            	
						//System.out.println((code + codeitemid).toUpperCase());
						//System.out.println(PubFunc.splitString(codeitemdesc,50));
						AdminCode.updateCodeItemDesc(codesetid,(code + codeitemid).toUpperCase(),PubFunc.splitString(codeitemdesc,codeitemdesclen));
						if("1".equals(first))
						{
							sqlstr.delete(0,sqlstr.length());
							sqlstr.append("update organization set childid='");
							sqlstr.append(code + codeitemid);
							sqlstr.append("' where codeitemid='");
							sqlstr.append(code);
							sqlstr.append("'");	
							dao.update(sqlstr.toString());
						}
					}
				}catch(Exception e){
				   e.printStackTrace();
				   throw GeneralExceptionHandler.Handle(e);
				}finally
				{
					try{
					   //this.getFrameconn().commit();
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}else if("1".equalsIgnoreCase(vorganization)){
				sqlstr.append("select codeitemdesc,codeitemid from organization where  codeitemid like '");
				sqlstr.append((code + codeitemid).toUpperCase());
				sqlstr.append("%' ");
				sqlstr.append(" union all ");
				sqlstr.append("select codeitemdesc,codeitemid from vorganization where  codeitemid like '");
				sqlstr.append((code + codeitemid).toUpperCase());
				sqlstr.append("%' order by codeitemid");
				//System.out.println(sqlstr.toString());
				try{			
					this.frowset=dao.search(sqlstr.toString());
					StringBuffer sb = new StringBuffer();
					while(this.frowset.next()){
						sb.append("、"+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")");
					}
					if(sb.length()>0)
					{	
						String tmp = sb.substring(1);
						this.getFormHM().put("labelmessage",ResourceFactory.getProperty("label.org.adderrors1")+tmp);
						throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.adderrors1")+tmp,"",""));
				    }
					else
					{
						sqlstr.delete(0,sqlstr.length());
						sqlstr.append("insert into vorganization(codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,corcode,start_date,end_date,layer)values(?,?,?,?,?,?,?,?,?,?,?,?)");
						ArrayList sqlvalue=new ArrayList();
						sqlvalue.add(codesetid);
						sqlvalue.add((code +codeitemid).toUpperCase());
						sqlvalue.add(PubFunc.splitString(codeitemdesc,codeitemdesclen));
						cat.debug("-------code------------>" + code);
						String parentid="";
						if(code!=null && code.trim().length()>0){
							parentid=code.toUpperCase();
						   sqlvalue.add(code.toUpperCase());
						}else{
							parentid=(code + codeitemid).toUpperCase();
						   sqlvalue.add((code + codeitemid).toUpperCase());
						}
						sqlvalue.add((code + codeitemid).toUpperCase());
						sqlvalue.add(null);
						sqlvalue.add(grade);
						sqlvalue.add(getMaxA0000(code));
						sqlvalue.add(corcode);
						sqlvalue.add(start_date);
						sqlvalue.add(end_date);
						
						int layer=ao.getLayer(parentid, (code +codeitemid).toUpperCase(), codesetid);
						if(0==layer){
							doInitLayer=true;
						}
						sqlvalue.add(new Integer(layer));
						dao.insert(sqlstr.toString(),sqlvalue);
						CodeItem item=new CodeItem();
						item.setCodeid(codesetid);
						item.setCodeitem((code +codeitemid).toUpperCase());
						item.setCodename(PubFunc.splitString(codeitemdesc,codeitemdesclen));
						if(code!=null && code.trim().length()>0)
							item.setPcodeitem(code.toUpperCase());
					    else
					    	item.setPcodeitem((code +codeitemid).toUpperCase());
						item.setCcodeitem((code +codeitemid).toUpperCase());
						item.setCodelevel(grade);
						//System.out.println(item);
					
						AdminCode.addCodeItem(item);
						//System.out.println(AdminCode.getCodeName(codesetid,(code +codeitemid).toUpperCase()));
		            	
						//System.out.println((code + codeitemid).toUpperCase());
						//System.out.println(PubFunc.splitString(codeitemdesc,50));
						AdminCode.updateCodeItemDesc(codesetid,(code + codeitemid).toUpperCase(),PubFunc.splitString(codeitemdesc,codeitemdesclen));
						if("1".equals(first))
						{	
							sqlstr.delete(0,sqlstr.length());
							sqlstr.append("update vorganization set childid='");
							sqlstr.append(code + codeitemid);
							sqlstr.append("' where codeitemid='");
							sqlstr.append(code);
							sqlstr.append("'");
							dao.update(sqlstr.toString());
							sqlstr.delete(0,sqlstr.length());
							sqlstr.append("update organization set childid='");
							sqlstr.append(code + codeitemid);
							sqlstr.append("' where codeitemid='");
							sqlstr.append(code);
							sqlstr.append("'");	
							dao.update(sqlstr.toString());
						}
					}
				}catch(Exception e){
				   e.printStackTrace();
				   throw GeneralExceptionHandler.Handle(e);
				}finally
				{
					try{
					   //this.getFrameconn().commit();
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		if(doInitLayer){//重置层级
			try {
				ao.executeInitLayer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String issuperuser ="";
		String manageprive ="";
		if(userView.isSuper_admin()){
			issuperuser ="1";
			manageprive=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
		}
		else if(userView.getStatus()==4||userView.getStatus()==0){
			issuperuser="0";
			manageprive=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
		}
		else{
			issuperuser="0";
			manageprive=userView.getManagePrivCode()+"no";
		}
		try{
			String sql = null;
			Calendar calendar = Calendar.getInstance();
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(calendar.getTime());
			boolean isInsert = false;
			String fieldsetId = "";
			if("@K".equalsIgnoreCase(codesetid)){
				
				RecordVo pos_code_field_constant_vo=ConstantParamter.getRealConstantVo("POS_CODE_FIELD",this.getFrameconn());
				if(pos_code_field_constant_vo!=null)
				{
				  String  pos_code_field=pos_code_field_constant_vo.getString("str_value");
				  //可能存在建好的指标被取消构库了
				  FieldItem pos_code_fieldItem = DataDictionary.getFieldItem(pos_code_field);
				  if(pos_code_field!=null&&pos_code_field.length()>1 && pos_code_fieldItem != null && "1".equals(pos_code_fieldItem.getUseflag())){
					  sql = "select e01a1 from K01 where e01a1='"+(code + codeitemid).toUpperCase()+"'";
					  this.frecset = dao.search(sql);
					  if(this.frecset.next()){
						  sql = "update K01 set e0122='"+pid+"',"+pos_code_field+"='"+corcode+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1='"+(code + codeitemid).toUpperCase()+"'";
					  }else{
						  sql = "insert into K01(e0122,e01a1,"+pos_code_field+",createusername,modusername,createtime,modtime) values ('"+pid+"','"+(code + codeitemid).toUpperCase()+"','"+corcode+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
						  isInsert = true;
						  fieldsetId = "k01";
					  }
					  dao.update(sql);
				  }else{
					  sql = "select e01a1 from K01 where e01a1='"+(code + codeitemid).toUpperCase()+"'";
					  this.frecset = dao.search(sql);
					  if(this.frecset.next()){
						  sql = "update K01 set e0122='"+pid+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1='"+(code + codeitemid).toUpperCase()+"'";
					  }else{
						  sql = "insert into K01(e0122,e01a1,createusername,modusername,createtime,modtime) values ('"+pid+"','"+(code + codeitemid).toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
						  isInsert = true;
						  fieldsetId = "k01";
					  }
					  dao.update(sql);
				  }
				}else{
					sql = "select e01a1 from K01 where e01a1='"+(code + codeitemid).toUpperCase()+"'";
					  this.frecset = dao.search(sql);
					  if(this.frecset.next()){
						  sql = "update K01 set e0122='"+pid+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1='"+(code + codeitemid).toUpperCase()+"'";
					  }else{
						  sql = "insert into K01(e0122,e01a1,createusername,modusername,createtime,modtime) values ('"+pid+"','"+(code + codeitemid).toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
						  isInsert = true;
						  fieldsetId = "k01";
					  }
					  dao.update(sql);
				}
			}else{
				RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.getFrameconn());
				if(unit_code_field_constant_vo!=null)
				{
				  String  unit_code_field=unit_code_field_constant_vo.getString("str_value");
				  FieldItem unit_code_fieldItem = DataDictionary.getFieldItem(unit_code_field);
				  //可能存在建好的指标被取消构库了
				  if(unit_code_field!=null&&unit_code_field.length()>1 && unit_code_fieldItem != null && "1".equals(unit_code_fieldItem.getUseflag())){
					  sql = "select b0110 from B01 where b0110='"+(code + codeitemid).toUpperCase()+"'";
					  this.frecset = dao.search(sql);
					  if(this.frecset.next()){
						  sql = "update B01 set "+unit_code_field+"='"+corcode+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+"  where b0110='"+(code + codeitemid).toUpperCase()+"'";
					  }else{
						  sql = "insert into B01(b0110,"+unit_code_field+",createusername,modusername,createtime,modtime) values ('"+(code + codeitemid).toUpperCase()+"','"+corcode+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
						  isInsert = true;
						  fieldsetId = "b01";
					  }
					  dao.update(sql);
				  }else{
					  sql = "select b0110 from B01 where b0110='"+(code + codeitemid).toUpperCase()+"'";
					  this.frecset = dao.search(sql);
					  if(this.frecset.next()){
						  sql = "update B01 set createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+"  where b0110='"+(code + codeitemid).toUpperCase()+"'";
					  }else{
						  sql = "insert into B01(b0110,createusername,modusername,createtime,modtime) values ('"+(code + codeitemid).toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
						  isInsert = true;
						  fieldsetId = "b01";
					  }
					  dao.update(sql);
				  }
				}else{
					 sql = "select b0110 from B01 where b0110='"+(code + codeitemid).toUpperCase()+"'";
					  this.frecset = dao.search(sql);
					  if(this.frecset.next()){
						  sql = "update B01 set createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+"  where b0110='"+(code + codeitemid).toUpperCase()+"'";
					  }else{
						  sql = "insert into B01(b0110,createusername,modusername,createtime,modtime) values ('"+(code + codeitemid).toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
						  isInsert = true;
						  fieldsetId = "b01";
					  }
					  dao.update(sql);
				}
			}
			
			if(isInsert) {
				OrgInfoUtils orgInfoUtils=new OrgInfoUtils(this.getFrameconn());	
				orgInfoUtils.updateSequenceableValue(code + codeitemid, fieldsetId, "0");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		this.getFormHM().put("isrefresh","save");
		this.getFormHM().put("issuperuser",issuperuser);
		this.getFormHM().put("manageprive",manageprive);

	}
	private String getMaxA0000(String descode) throws GeneralException
    {
        String a0000="1";
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try{
           //获取子节点中最大的a000时，同时查询组织机构表（organization）和虚拟机构表（vorganization）  chenxg  update 2015-01-15
           this.frowset=dao.search("SELECT MAX(A0000) as a0000 FROM (select A0000 from organization where codeitemid like '"+descode+"%' union select A0000 from vorganization where codeitemid like '"+descode+"%') u");
           if(this.frowset.next())
           {
               a0000=String.valueOf(this.frowset.getInt("a0000") + 1);
               dao.update("update organization set a0000=a0000 + 1 where a0000>" + this.frowset.getInt("a0000"));
               //更新虚拟机构表（vorganization）中的a0000  chenxg  add 2015-01-15
               dao.update("update vorganization set a0000=a0000 + 1 where a0000>" + this.frowset.getInt("a0000"));
               
           }
           else {
               dao.update("update organization set a0000=a0000 + 1 where a0000>0"); 
               //更新虚拟机构表（vorganization）中的a0000  chenxg  add 2015-01-15
               dao.update("update vorganization set a0000=a0000 + 1 where a0000>0");
           }
        }catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return a0000;
    }
	/**
	 * 同级机构排序获取levelA0000最大值   wangb    20170708
	 * @param descode
	 * @return
	 * @throws GeneralException
	 */
	private String getMaxLevelA0000(String descode)throws GeneralException
	{
		String levelA0000 = "1";
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list = new ArrayList();
		try {
			list.add(descode);
			if(descode == null || "".equalsIgnoreCase(descode) || descode.trim().length()==0)//不存在 当前机构为顶级机构  bug 35006 wangb  20180301
				this.frowset=dao.search("select MAX(LEVELA0000) as levelA0000 from organization where codeitemid=parentid");
			else//上级机构存在
				this.frowset=dao.search("SELECT MAX(LEVELA0000) as levelA0000 FROM organization where PARENTID=?",list);
			if(this.frowset.next())
				levelA0000 = String.valueOf(this.frowset.getInt("levelA0000") + 1);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return levelA0000;
	}
	
	
}
