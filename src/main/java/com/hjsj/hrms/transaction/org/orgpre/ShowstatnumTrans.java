package com.hjsj.hrms.transaction.org.orgpre;

import com.hjsj.hrms.businessobject.parse.parsebusiness.Factor;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
/**
 * 显示统计个数的公式的人
 * @author Luckstar
 *
 */
public class ShowstatnumTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String parttime=(String)hm.get("parttime");
		parttime=parttime==null?"":parttime;
		String fielditemid = (String)this.getFormHM().get("fielditemid");
		String codeitemid = (String)this.getFormHM().get("b0110");
		
		if(!codeitemid.equals(new CheckPrivSafeBo(frameconn, userView).checkOrg(codeitemid, "4")))
			throw GeneralExceptionHandler.Handle(new Exception("没有权限!"));
		
		PosparameXML pos = new PosparameXML(this.frameconn);
		String view_scan = pos.getNodeAttributeValue("/params/view_scan", "orgpre");
		view_scan=view_scan!=null&&view_scan.trim().length()>1?view_scan:"Usr,";
		view_scan=view_scan.toLowerCase();
		if("1".equals(parttime)){
			String rsql="";
			String wherestr="";
			StringBuffer rcolumns=new StringBuffer();
			String orderby="order by a0000";
			try{
				String codesetid="@K";
				/*String sql="select codesetid from organization where codeitemid='"+codeitemid+"'";
				ContentDAO dao = new ContentDAO(this.frameconn);
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					codesetid=this.frowset.getString("codesetid");
				}*/
				rcolumns.append(",dbpre,a0100,b0110,e0122,e01a1,a0101");
				//String[] dbArr = view_scan.split(",");
				StringBuffer sqlstr = new StringBuffer();
				ArrayList dbArr = this.userView.getPrivDbList();
				for(int i=0;i<dbArr.size();i++){
					String dbpre = (String)dbArr.get(i);
					if(view_scan.indexOf(dbpre.toLowerCase())==-1)
						continue;
					if(dbpre.length()==3){
						if(sqlstr!=null&&sqlstr.length()>1){
							sqlstr.append(" union ");
						}
						sqlstr.append(" select distinct "+dbpre+"A01.a0100,'");
						sqlstr.append(dbpre);
						sqlstr.append("' dbpre,");
						sqlstr.append(dbpre);
						sqlstr.append("A01");
						sqlstr.append(".A0000 a0000,"+dbpre+"A01.B0110,"+dbpre+"A01.E0122,"+dbpre+"A01.E01A1,"+dbpre+"A01.A0101");
						{
							sqlstr.append(" FROM ");
							sqlstr.append(dbpre);
							sqlstr.append("A01");
						}
						//如果设置岗位占编
						Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
						//兼职子集
						String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
						/**任免标识字段*/
						String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
						sqlstr.append(" left join ");
						sqlstr.append(dbpre);
						sqlstr.append(setid);
						sqlstr.append(" on "+dbpre+"A01.a0100="+dbpre+setid+".a0100");
						if("@K".equalsIgnoreCase(codesetid)){
		    				//兼任兼职
		    				String pos_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
							sqlstr.append(" where "+pos_field+"='"+codeitemid+"'");
						}else if("UM".equalsIgnoreCase(codesetid)){
							//兼职部门
		    				String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
							sqlstr.append(" where "+dept_field+"='"+codeitemid+"'");
						}else{
							/**兼职单位字段*/
		    				String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
							sqlstr.append(" where "+unit_field+"='"+codeitemid+"'");
						}
						sqlstr.append(" and "+dbpre+setid+"."+appoint_field+"='0'");
					}
				}
				rsql = sqlstr.toString();
			}catch(Exception e){
				rsql="";
				orderby="";
				e.printStackTrace();
			}finally{
				this.getFormHM().put("sql", rsql);
				this.getFormHM().put("wherestr", wherestr);
				this.getFormHM().put("columns", rcolumns.toString());
				this.getFormHM().put("orderby", orderby);
				ArrayList statitemlist=new ArrayList();	
				FieldItem fielditem=new FieldItem();
				String fieldname="";
				fielditem=DataDictionary.getFieldItem("b0110");
				fieldname=fielditem.getItemid();
				if("0"!=userView.analyseFieldPriv(fieldname))
					statitemlist.add(fielditem);
				fielditem=DataDictionary.getFieldItem("e0122");
				fieldname=fielditem.getItemid();
				if("0"!=userView.analyseFieldPriv(fieldname))
					statitemlist.add(fielditem);
				fielditem=DataDictionary.getFieldItem("e01a1");
				fieldname=fielditem.getItemid();
				if("0"!=userView.analyseFieldPriv(fieldname))
					statitemlist.add(fielditem);
				fielditem=DataDictionary.getFieldItem("a0101");
				fieldname=fielditem.getItemid();
				if("0"!=userView.analyseFieldPriv(fieldname))
					statitemlist.add(fielditem);
				this.getFormHM().put("fieldlist", statitemlist);
			}
		}else{
			String exprArr[] = exprDecom(this.getExpre(fielditemid));
			if(exprArr!=null&&exprArr.length>3){
				String[] arr = exprArr[3].split("\\|");
				String lower = "1";
				if(exprArr.length==5){
					lower = exprArr[4];
				}
				String sexpr = "";
				String sfactor="";
				if(arr!=null&&arr.length==2){
					sexpr = arr[0];
					sfactor=arr[1];
				}
				String rsql="";
				String wherestr="";
				StringBuffer rcolumns=new StringBuffer();
				String orderby="order by a0000";
				try{
					String sql="select codesetid from organization where codeitemid='"+codeitemid+"'";
					ContentDAO dao = new ContentDAO(this.frameconn);
					this.frowset = dao.search(sql);
					String codesetid="";
					if(this.frowset.next()){
						codesetid=this.frowset.getString("codesetid");
					}
					StringBuffer columns = new StringBuffer();
					this.getFormHM().put("fieldlist", this.getStatItemList("",columns,rcolumns));
					rcolumns.append(",dbpre,a0100,b0110,e0122,e01a1,a0101");
					//String[] dbArr = view_scan.split(",");
					StringBuffer sqlstr = new StringBuffer();
					ArrayList dbArr = this.userView.getPrivDbList();
					for(int i=0;i<dbArr.size();i++){
						String dbpre = (String)dbArr.get(i);
						if(view_scan.indexOf(dbpre.toLowerCase())==-1)
							continue;
						if(dbpre.length()==3){
							String whl="";
							if(sfactor!=null&&sfactor.trim().length()>0&&sexpr!=null&&sexpr.trim().length()>0){
								if(!userView.isSuper_admin()){
									whl=userView.getPrivSQLExpression(sfactor+"|"+sexpr,dbpre,false,false,true,new ArrayList()) ;
								}else{
									FactorList factorslist=new FactorList(sfactor,sexpr,dbpre,false,false,true,1,userView.getUserId());
									factorslist.setSuper_admin(userView.isSuper_admin());
									whl=factorslist.getSqlExpression();
								}
							}
							if(sqlstr!=null&&sqlstr.length()>1){
								sqlstr.append(" union ");
							}
							sqlstr.append(" select "+dbpre+"A01.a0000 a0000,'");
							sqlstr.append(dbpre);
							sqlstr.append("' dbpre,");
							sqlstr.append(dbpre);
							sqlstr.append("A01");
							sqlstr.append(".A0100,"+dbpre+"A01.B0110,"+dbpre+"A01.E0122,"+dbpre+"A01.E01A1,"+dbpre+"A01.A0101");
							
							//显示字段不加入查询字段
//							if(columns.length()>0){
//								sqlstr.append(columns.replaceAll("###", dbpre)+" ");
//							}
							
							
							if(whl!=null&&whl.trim().length()>1){
								sqlstr.append(whl);
							}else{
								sqlstr.append(" FROM ");
								sqlstr.append(dbpre);
								sqlstr.append("A01");
							}
							if("@K".equalsIgnoreCase(codesetid)){
								if(whl!=null&&whl.trim().length()>1){
									sqlstr.append(" and e01a1='"+codeitemid+"'");
								}else{
									sqlstr.append(" where e01a1='"+codeitemid+"'");
								}
							}else if("UM".equalsIgnoreCase(codesetid)){
								if("1".equals(lower)){
									if(whl!=null&&whl.trim().length()>1){
										sqlstr.append(" and e0122 like '"+codeitemid+"%'");
									}else{
										sqlstr.append(" where e0122 like '"+codeitemid+"%'");
									}
								}else{
									if(whl!=null&&whl.trim().length()>1){
										sqlstr.append(" and e0122='"+codeitemid+"'");
									}else{
										sqlstr.append(" where e0122='"+codeitemid+"'");
									}
								}
							}else{
								if("1".equals(lower)){
									if(whl!=null&&whl.trim().length()>1){
										sqlstr.append(" and b0110 like '"+codeitemid+"%'");
									}else{
										sqlstr.append(" where b0110 like '"+codeitemid+"%'");
									}
								}else{
									if(whl!=null&&whl.trim().length()>1){
										sqlstr.append(" and b0110='"+codeitemid+"'");
									}else{
										sqlstr.append(" where b0110='"+codeitemid+"'");
									}
								}
							}
						}
					}
					if(sqlstr.length()==0){
						sqlstr.append(" select UsrA01.a0000 a0000,'");
						sqlstr.append("Usr");
						sqlstr.append("' dbpre,");
						sqlstr.append("Usr");
						sqlstr.append("A01");
						sqlstr.append(".A0100,UsrA01.B0110,UsrA01.E0122,UsrA01.E01A1,UsrA01.A0101");
						if(columns.length()>0){
							sqlstr.append(columns.toString().replaceAll("###", "Usr")+" ");
						}
						{
							sqlstr.append(" FROM ");
							sqlstr.append("Usr");
							sqlstr.append("A01 where 1=2");
						}
					}
						
					rsql = sqlstr.toString();
				}catch(Exception e){
					rsql="";
					orderby="";
					e.printStackTrace();
				}finally{
					this.getFormHM().put("sql", rsql);
					this.getFormHM().put("wherestr", wherestr);
					this.getFormHM().put("columns", rcolumns.toString());
					this.getFormHM().put("orderby", orderby);
				}
			}else{
				throw GeneralExceptionHandler.Handle(new Exception("不能查看当前记录!"));
			}
		}
	}

	private String getExpre(String fielditemid){
		String Expression = "";
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select Expression from fielditem where itemid='");
		sqlstr.append(fielditemid.toUpperCase());
		sqlstr.append("'");
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		try {
			rs= dao.search(sqlstr.toString());
			if(rs.next()){
				Expression = rs.getString("Expression");
				Expression=Expression!=null?Expression:"";
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return Expression;
	}
	
	private String[] exprDecom(String expr){
		expr=expr!=null?expr:"";
		String[] exprArr = expr.split("::");
		return exprArr;
	}
	
	private ArrayList getStatItemList(String factors,StringBuffer columns,StringBuffer rcolumns)
	{
		ArrayList statitemlist=new ArrayList();	
		FieldItem fielditem=new FieldItem();
		String fieldname="";

		{
			fielditem=DataDictionary.getFieldItem("b0110");
			fieldname=fielditem.getItemid();
			if("0"!=userView.analyseFieldPriv(fieldname))
				statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("e0122");
			fieldname=fielditem.getItemid();
			if("0"!=userView.analyseFieldPriv(fieldname))
				statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("e01a1");
			fieldname=fielditem.getItemid();
			if("0"!=userView.analyseFieldPriv(fieldname))
				statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("a0101");
			fieldname=fielditem.getItemid();
			if("0"!=userView.analyseFieldPriv(fieldname))
				statitemlist.add(fielditem);	
		}
			
		HashSet fieldItemSet=getStatFieldItem(factors,this.userView);
		Iterator it = fieldItemSet.iterator();		
		while(it.hasNext())
		{
			   String item=(String)it.next();
				   if("b0110".equalsIgnoreCase(item))
					   continue;

				   if("e0122".equalsIgnoreCase(item)|| "e01a1".equalsIgnoreCase(item))
					   continue;
			   fielditem=DataDictionary.getFieldItem(item);
			   fieldname=fielditem.getItemid();
			   columns.append(",###"+fielditem.getFieldsetid()+"."+fieldname);
			   rcolumns.append(","+fieldname);
			   if("0"!=userView.analyseFieldPriv(fieldname))
				   statitemlist.add(fielditem);
		}		
		return statitemlist;
	}
	private HashSet getStatFieldItem(String factors,UserView userView)
	{
		HashSet fieldItemSet = new HashSet();
		if(factors==null||factors.length()<=0)
			return fieldItemSet;		
		if(factors!=null&&factors.length()>0)
		{
			String factorArr[]=factors.split("`");
			String factorstr=""; 
			for(int i=0;i<factorArr.length;i++)
			{
				factorstr=factorArr[i];
				factorstr=factorstr.toUpperCase();
				Factor factor = new Factor(userView.getDbname(), factorstr);
				String item=factor.getItem();
				if(item!=null&&item.length()>0)
				{
					fieldItemSet.add(item);
				}						
			}
		}	
		return fieldItemSet;
	}
}
