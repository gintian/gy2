package com.hjsj.hrms.utils.components.definetempvar.transaction;

import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.definetempvar.businessobject.DefineTempVarBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 项目名称 ：ehr7.x
 * 类名称：SaveTempVarTrans
 * 类描述：保存临时变量
 * 创建人： lis
 * 创建时间：2015-10-31
 */
public class SaveTempVarTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			DefineTempVarBo bo = new DefineTempVarBo(this.frameconn,this.userView);
			String hasUse = ResourceFactory.getProperty("gz_new.gz_tempVarHasUse");//变量名已被占用
			HashMap hm = this.getFormHM();
			ContentDAO dao = new ContentDAO(this.frameconn);
			hm.put("base","");
			String tempvarname = (String)hm.get("tempvarname");//临时变量名称
			tempvarname=tempvarname!=null&&tempvarname.trim().length()>0?tempvarname:"";
			
			String ntype = (String)hm.get("ntype");//临时变量数据类型
			ntype=ntype!=null&&ntype.trim().length()>0?ntype:"";
			
			String fidlen = (String)hm.get("fidlen");//临时变量数据长度
			fidlen=fidlen!=null&&fidlen.trim().length()>0?fidlen:"10";
			
			String fiddec = (String)hm.get("fiddec");//临时变量数据小说点位数
			fiddec=fiddec!=null&&fiddec.trim().length()>0?fiddec:"0";
			
			String codesetid = (String)hm.get("codesetid");//临时变量数据是代码类时的代码id
			codesetid=codesetid!=null&&codesetid.trim().length()>0&& "4".equals(ntype)?codesetid:"";
			
			String type = (String)hm.get("type");// 1:薪资类别,3:人事异动模块, 5：数据采集
			type=type!=null&&type.trim().length()>0?type:"";
			
			String cstate = (String)hm.get("cstate");//人事模版id，或薪资类别id
			cstate=cstate!=null&&cstate.trim().length()>0?cstate:"";
			if(!"3".equals(type))
				cstate=PubFunc.decrypt(SafeCode.decode(cstate));
			
			String nflag = (String)hm.get("nflag");//0,工资发放|保险核算,2,报表，1,工资变动或者日常管理
			nflag=nflag!=null&&nflag.trim().length()>0?nflag:"0";
			
			String id = (String)hm.get("nid");
			id=id!=null&&id.trim().length()>0?id:"";
			ArrayList list = new ArrayList();
			ArrayList storeList = new ArrayList();
			if(id.length()>0){//如果是修改
				//先查询。如果没有重名，才允许修改
				StringBuffer sbquery = new StringBuffer("");
				sbquery.append(" select * from midvariable where (chz=? or cname=?) ");
				list.add(tempvarname);
				list.add(tempvarname);
				if("1".equals(type)){//如果是从薪资类别模板进入的
					sbquery.append(" and nflag=0 and templetId = 0 and (cstate =? or cstate is null)");
					list.add(cstate);
				}else if("3".equals(type)){
					sbquery.append(" and nflag=0 and  templetid<>0 and(templetId = ? or cstate = '1')");
					list.add(cstate);
				}
				sbquery.append(" and nid<>?");
				
				list.add(id);
				try {
					this.frowset = dao.search(sbquery.toString(),list);
					if(this.frowset.next()){
						hm.put("base",hasUse);//xieguiquan 2012-5-25
						return;
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				//修改之前先把原来的中文名字,英文名字查出来
				sbquery.setLength(0);
				String tempCHZ = "";
				String tempCName = "";
				String isShare = "0";//是否共享 0：不共享 1：共享
				String tempcstate ="";
				String templetId ="";
				String isok = "1"; 
				try{
					sbquery.append("select cstate,chz,cname,templetID from midvariable where nid=?");
					this.frowset = dao.search(sbquery.toString(),Arrays.asList(id));
					if(frowset.next()){
						tempCHZ = this.frowset.getString("chz")==null?"":this.frowset.getString("chz");
						tempCName = this.frowset.getString("cname")==null?"":this.frowset.getString("cname");
						tempcstate = this.frowset.getString("cstate")==null?"":this.frowset.getString("cstate");
						Integer dd = new Integer(this.frowset.getInt("templetID"));
						if(dd==null){
						    templetId="";
						}else{
						    templetId=dd.toString();
						}
						if("1".equals(type)|| "3".equals(type)){//如果是从薪资类别模块进入,或者人事异动，gaohy,2016-1-7
							if("".equals(tempcstate)){//从薪资类别进入的时候，cstate是null才是共享
								isShare = "1";
							}
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				if(!tempCHZ.equals(tempvarname)){
					//不重名，还需要检查引用该临时变量的地方（如条件、公式、临时变量表达式）
					if("1".equals(type)){//如果是从薪资类别模板进入，则需要检查midvariable表（临时变量表达式）、salaryformula表（计算公式）
												//hrpchkformula表（审核公式）,并把包含该变量的名字存在StringBuffer里面
						boolean hasVariable = false;
						//首先检查midvariable表（临时变量表达式）
						StringBuffer sb_query = new StringBuffer("");
						list.clear();
						sb_query.append("select cvalue from midvariable where nflag=0 and templetId = 0 ");
						if(!"1".equals(isShare)){//如果不是共享
							sb_query.append(" and cstate=? ");
							list.add(tempcstate);
						}
						try{
							this.frowset = dao.search(sb_query.toString(),list);
							while(this.frowset.next()){
								String strCvalue = Sql_switcher.readMemo(this.frowset, "cvalue")==null?"":Sql_switcher.readMemo(this.frowset, "cvalue");
								hasVariable = PubFunc.IsHasVariable(strCvalue,tempCHZ);
								String nochangeName = ResourceFactory.getProperty("gz_new.gz_tempVarName1");
								if(hasVariable) {
									isok = "0";
									hm.put("base",nochangeName);
									return;
								}
								hasVariable = PubFunc.IsHasVariable(strCvalue,tempCName);//查找英文名
								if(hasVariable) {
									isok = "0";
									hm.put("base",nochangeName);
									return;
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						//然后检查salaryformula表（计算公式）
						sb_query.setLength(0);
						sb_query.append("select rexpr,cond from salaryformula where salaryid<>-1 and salaryid<>-2");
						list.clear();
						if(!"1".equals(isShare)){//如果不是共享
							sb_query.append(" and salaryid=?");
							list.add(tempcstate);
						}
						try{
							this.frowset = dao.search(sb_query.toString(),list);
							while(this.frowset.next()){
								String rexpr = Sql_switcher.readMemo(this.frowset, "rexpr")==null?"":Sql_switcher.readMemo(this.frowset, "rexpr");
								hasVariable = PubFunc.IsHasVariable(rexpr,tempCHZ);
								//计算公式中引用了该临时变量，不能更名
								String nochangeName = ResourceFactory.getProperty("gz_new.gz_tempVarName2");
								if(hasVariable) {
									isok = "0";
									hm.put("base",nochangeName);
									return;
								}
								hasVariable = PubFunc.IsHasVariable(rexpr,tempCName);//查找英文名
								if(hasVariable) {
									isok = "0";
									hm.put("base",nochangeName);
									return;
								}
								
								String cond = Sql_switcher.readMemo(this.frowset, "cond")==null?"":Sql_switcher.readMemo(this.frowset, "cond");
								hasVariable = PubFunc.IsHasVariable(cond,tempCHZ);
								//计算公式条件中引用了该临时变量，不能更名
								String nochangeName2 = ResourceFactory.getProperty("gz_new.gz_tempVarName3");
								if(hasVariable) {
									isok = "0";
									hm.put("base",nochangeName2);
									return;
								}
								hasVariable = PubFunc.IsHasVariable(cond,tempCName);//查找英文名
								if(hasVariable) {
									isok = "0";
									hm.put("base",nochangeName2);
									return;
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						
						//最后检查hrpchkformula表（审核公式）
						sb_query.setLength(0);
						sb_query.append("select formula from hrpchkformula");
						if(!"1".equals(isShare)){//如果不是共享
							sb_query.append(" where tabid=" + tempcstate);
						}
						try{
							this.frowset = dao.search(sb_query.toString());
							while(this.frowset.next()){
								String formula = Sql_switcher.readMemo(this.frowset, "formula")==null?"":Sql_switcher.readMemo(this.frowset, "formula");
								hasVariable = PubFunc.IsHasVariable(formula,tempCHZ);
								//审核公式条件中引用了该临时变量，不能更名
								String nochangeName = ResourceFactory.getProperty("gz_new.gz_tempVarName4");
								if(hasVariable) {
									isok = "0";
									hm.put("base",nochangeName);
									return;
								}
								hasVariable = PubFunc.IsHasVariable(formula,tempCName);//查找英文名
								if(hasVariable) {
									isok = "0";
									hm.put("base",nochangeName);
									return;
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}else if("3".equals(type)){//如果是从人事异动业务模板进入的，则要检查三个表。template_set，template_table，gzadj_formula
						boolean hasVariable = false;
						//检查midvariable表（临时变量表达式）
						StringBuffer sb_query = new StringBuffer("");
						sb_query.append("select cvalue from midvariable where nflag=0 and templetId = "+templetId+" ");
						if(!"1".equals(isShare)){//如果不是共享
							sb_query.append(" and (cstate='"+tempcstate+"' or cstate is null)");
						}
						try{
							this.frowset = dao.search(sb_query.toString());
							while(this.frowset.next()){
								String strCvalue = Sql_switcher.readMemo(this.frowset, "cvalue")==null?"":Sql_switcher.readMemo(this.frowset, "cvalue");
								hasVariable = PubFunc.IsHasVariable(strCvalue,tempCHZ);//查找中文名
								if(hasVariable) {
									isok = "0";
									hm.put("base","临时变量表达式中引用了该临时变量，不能更名！");
									return;
								}
								hasVariable = PubFunc.IsHasVariable(strCvalue,tempCName);//查找英文名
								if(hasVariable) {
									isok = "0";
									hm.put("base","临时变量表达式中引用了该临时变量，不能更名！");
									return;
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						//然后检查template_table表
						sb_query.setLength(0);
						sb_query.append("select factor from template_table");
						if(!"1".equals(isShare)){//如果不是共享
							sb_query.append(" where tabid="+templetId+"");
						}
						try{
							this.frowset = dao.search(sb_query.toString());
							while(this.frowset.next()){
								String strFactor = Sql_switcher.readMemo(this.frowset, "factor")==null?"":Sql_switcher.readMemo(this.frowset, "factor");
								hasVariable = PubFunc.IsHasVariable(strFactor,tempCHZ);
								if(hasVariable) {
									isok = "0";
									hm.put("base","检索条件中引用了该临时变量，不能更名！");
									return;
								}
								hasVariable = PubFunc.IsHasVariable(strFactor,tempCName);//查找英文名
								if(hasVariable) {
									isok = "0";
									hm.put("base","检索条件中引用了该临时变量，不能更名！");
									return;
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						//最后检查gzadj_formula表
						sb_query.setLength(0);
						try{
							sb_query.append("select formula from gzadj_formula");
							if(!"1".equals(isShare)){//如果不是共享
								sb_query.append(" where tabid="+templetId+"");
							}
							this.frowset = dao.search(sb_query.toString());
							while(this.frowset.next()){
								String formula = Sql_switcher.readMemo(this.frowset, "formula")==null?"":Sql_switcher.readMemo(this.frowset, "formula");
								hasVariable = PubFunc.IsHasVariable(formula,tempCHZ);
								if(hasVariable) {
									isok = "0";
									hm.put("base","计算公式中引用了该临时变量，不能更名！");
									return;
								}
								hasVariable = PubFunc.IsHasVariable(formula,tempCName);//查找英文名
								if(hasVariable) {
									isok = "0";
									hm.put("base","计算公式中引用了该临时变量，不能更名！");
									return;
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						
						sb_query.setLength(0);
						try{
							sb_query.append("select cfactor from gzadj_formula");
							if(!"1".equals(isShare)){//如果不是共享
								sb_query.append(" where tabid="+templetId+"");
							}
							this.frowset = dao.search(sb_query.toString());
							while(this.frowset.next()){
								String cfactor = Sql_switcher.readMemo(this.frowset, "cfactor")==null?"":Sql_switcher.readMemo(this.frowset, "cfactor");
								hasVariable = PubFunc.IsHasVariable(cfactor,tempCHZ);
								if(hasVariable) {
									isok = "0";
									hm.put("base","计算公式条件中引用了该临时变量，不能更名！");
									return;
								}
								hasVariable = PubFunc.IsHasVariable(cfactor,tempCName);//查找英文名
								if(hasVariable) {
									isok = "0";
									hm.put("base","计算公式条件中引用了该临时变量，不能更名！");
									return;
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						
					}
				}
				if("1".equals(isok)){ //如果不重名，且没有被引用，则可以更名
					//开始修改变量名称
					RecordVo vo=new RecordVo("midvariable");
					vo.setInt("nid", Integer.parseInt(id));
					try{
						vo = dao.findByPrimaryKey(vo);
					}catch(Exception e){
						e.printStackTrace();
					}
					StringBuffer sqlstr = new StringBuffer();
					list.clear();
					sqlstr.append("update midvariable set chz=?,ntype=?,fldlen=?,flddec=?,codesetid=? where nid=? ");
		
					list.add(tempvarname);
					list.add(ntype);
					list.add(fidlen);
					list.add(fiddec);
					list.add(codesetid);
					list.add(id);
					
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("ntype", ntype);//临时变量数据类型
					bean.set("chz", tempvarname);//临时变量汉化名称
					bean.set("fldlen", fidlen);//临时变量长度
					bean.set("flddec", fiddec);//临时变量小数点位数
					bean.set("codesetid", codesetid);//临时变量代码类id
					storeList.add(bean);
					
					if("1".equals(type)){
						String edit = ResourceFactory.getProperty("label.edit");//修改
						String temvar = ResourceFactory.getProperty("label.gz.variable");//临时变量
						
						String _tempvarname = vo.getString("chz")==null?"":vo.getString("chz");
						String _ntype = vo.getString("ntype")==null?"":vo.getString("ntype");
						String _fidlen = vo.getString("fldlen")==null?"":vo.getString("fldlen");
						String _fiddec = vo.getString("flddec")==null?"":vo.getString("flddec");
						String _codesetid = vo.getString("codesetid")==null?"":vo.getString("codesetid");
						if(!_tempvarname.equals(tempvarname)||!_ntype.equals(ntype)||!_fidlen.equals(fidlen)||!_fiddec.equals(fiddec)||!_codesetid.equals(codesetid)){
							StringBuffer context = new StringBuffer();
							String name = bo.getSalaryName(cstate);
							//修改一般属性
							context.append(edit+"："+name+"（"+cstate+"）"+edit+temvar+"（"+tempvarname+"）"+ResourceFactory.getProperty("gz_new.gz_commonProperty")+"<br>");
							context.append("<table>");
							context.append("<tr>");
							context.append("<td>"+ResourceFactory.getProperty("gz_new.gz_propertyName")+"</td>");//属性名
							context.append("<td>"+ResourceFactory.getProperty("gz.gz_acounting.change.before")+"</td>");//变化、前
							context.append("<td>"+ResourceFactory.getProperty("gz.gz_acounting.change.affter")+"</td>");//变化后
							context.append("</tr>");
							if(!_tempvarname.equals(tempvarname)){
								context.append("<tr>");
								context.append("<td>"+ResourceFactory.getProperty("lable.portal.main.name")+"</td>");
								context.append("<td>"+_tempvarname+"</td>");
								context.append("<td>"+tempvarname+"</td>");
								context.append("</tr>");
							}
							if(!_ntype.equals(ntype)){
								HashMap map = this.codeMap();
								context.append("<tr>");
								context.append("<td>"+ResourceFactory.getProperty("gz_new.gz_tempVarType")+"</td>");
								context.append("<td>"+map.get(_ntype)+"</td>");
								context.append("<td>"+map.get(ntype)+"</td>");
								context.append("</tr>");
							}
							if(!_fidlen.equals(fidlen)){
								context.append("<tr>");
								context.append("<td>"+ResourceFactory.getProperty("system.item.length")+"</td>");
								context.append("<td>"+_fidlen+"</td>");
								context.append("<td>"+fidlen+"</td>");
								context.append("</tr>");
							}
							if(!_fiddec.equals(fiddec)){
								context.append("<tr>");
								context.append("<td>"+ResourceFactory.getProperty("gz.tempvar.median")+"</td>");
								context.append("<td>"+_fiddec+"</td>");
								context.append("<td>"+fiddec+"</td>");
								context.append("</tr>");
							}
							if(!_codesetid.equals(codesetid)){
								context.append("<tr>");
								context.append("<td>"+ResourceFactory.getProperty("codemaintence.codeitem.id")+"</td>");
								context.append("<td>"+_codesetid+"</td>");
								context.append("<td>"+codesetid+"</td>");
								context.append("</tr>");
							}

							context.append("</table>");
							hm.put("@eventlog", context.toString());
						}else{
							hm.put("@eventlog", edit+"："+edit+temvar+"（"+tempvarname+"）"+ResourceFactory.getProperty("gz_new.gz_propertyNochange"));
						}
					}
					
					try {
						dao.update(sqlstr.toString(),list);
						if("3".equals(type)){//如果是从人事异动业务模板进入的
							//修改template_set表
							StringBuffer sbupdate = new StringBuffer("");
							sbupdate.append("update template_set set field_hz='"+tempvarname+"' where field_name='"+id+"'");
							dao.update(sbupdate.toString());
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				hm.put("nid", id);//用于刷新页面后定位行  zhaoxg add 2014-11-19
				hm.put("ntype", ntype);
			}else{//新增
				DefineTempVarBo tempvarbo = new DefineTempVarBo();
				int nid = tempvarbo.getid(this.frameconn);
				//查询是否有重名的临时变量
				String sqlchz = " select * from midvariable where (chz='"+tempvarname+"' or cname='"+tempvarname+"') ";
				if("1".equals(type)){//如果是从薪资类别模板进入的
					if("0".equals(nflag)){
						sqlchz+= " and nflag=0 and templetId = 0 and (cstate ='"+cstate+"'or cstate is null)";
					}
				}else if("3".equals(type)){//如果是从人事异动模板进入的
					sqlchz+=" and nflag=0 and templetId <> 0 and (templetId ="+cstate+"or cstate = '1')";
				}
				try {
					this.frowset = dao.search(sqlchz);
					if(this.frowset.next()){
						System.out.println(this.frowset.getString("chz"));
						hm.put("base",hasUse);//xieguiquan 2012-5-25
						return;
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				StringBuffer sqlstr = new StringBuffer();
				//插入临时表之前先判断CName的值
				StringBuffer sqlquery = new StringBuffer("");
				String cname_new = "yk"+nid;
				sqlquery.append(" select count(*) from midvariable where cname='"+cname_new+"' ");
				try {
					this.frowset = dao.search(sqlquery.toString());
					int num = 0;
					if(frowset.next()){
						num = frowset.getInt(1);
					}
					if(num > 0){
						cname_new = "ykC"+nid;
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				
				LazyDynaBean bean = new LazyDynaBean();
				ArrayList dataList = new ArrayList();
				if("1".equals(type)){//薪资
						sqlstr.append("insert into midvariable(nid,cname,chz,ntype,cstate,fldlen,flddec,templetid,codesetid,nflag,sorting) values(?,?,?,?,?,?,?,?,?,?,?)");
						dataList.add(nid);
						dataList.add(cname_new);
						dataList.add(tempvarname);
						dataList.add(ntype);
						dataList.add(cstate);
						dataList.add(fidlen);
						dataList.add(fiddec);
						dataList.add(0);
						dataList.add(codesetid);
						dataList.add(nflag);
						int sorting = getSorting(dao,cstate,type);
						dataList.add(sorting);
						
						bean.set("nid", nid+"");//临时变量id
						bean.set("ntype", ntype);//临时变量数据类型
						bean.set("chz", tempvarname);//临时变量汉化名称
						bean.set("fldlen", fidlen);//临时变量长度
						bean.set("flddec", fiddec);//临时变量小数点位数
						bean.set("codesetid", codesetid);//临时变量代码类id
						bean.set("cstate", SafeCode.encode(PubFunc.encrypt(cstate)));//临时变量是否是共享，如果不是则是当前薪资类别id，否则为空
						bean.set("sorting", sorting);//临时变量排序号
						storeList.add(bean);
						StringBuffer context = new StringBuffer();
						String name = bo.getSalaryName(cstate);
						context.append(ResourceFactory.getProperty("button.new.add")+"："+name+"（"+cstate+"）"+ResourceFactory.getProperty("gz_new.gz_newTempVar")+"（"+tempvarname+"）<br>");

						hm.put("@eventlog", context.toString());
						dao.update(sqlstr.toString(),dataList);
				}else if("3".equals(type)){//是人事异动
					sqlstr.append("insert into midvariable(nid,cname,chz,ntype,fldlen,flddec,templetid,codesetid,nflag,sorting) values(?,?,?,?,?,?,?,?,?,?)");
					dataList.add(nid);
					dataList.add(cname_new);
					dataList.add(tempvarname);
					dataList.add(ntype);
					dataList.add(fidlen);
					dataList.add(fiddec);
					dataList.add(cstate);
					dataList.add(codesetid);
					dataList.add(nflag);
					int sorting = getSorting(dao,cstate,type);
					dataList.add(sorting);
					
					bean.set("nid", nid);//临时变量id
					bean.set("ntype", ntype);//临时变量数据类型
					bean.set("chz", tempvarname);//临时变量汉化名称
					bean.set("fldlen", fidlen);//临时变量长度
					bean.set("flddec", fiddec);//临时变量小数点位数
					bean.set("codesetid", codesetid);//临时变量代码类id
					bean.set("cstate", null);//临时变量是否是共享，如果不是则是当前薪资类别id，否则为空
					bean.set("sorting", sorting);//临时变量排序号
					storeList.add(bean);
					StringBuffer context = new StringBuffer();
					String name = bo.getTempName(cstate);
					context.append(ResourceFactory.getProperty("button.new.add")+"："+name+"（"+cstate+"）"+ResourceFactory.getProperty("gz_new.gz_newTempVar")+"（"+tempvarname+"）<br>");

					hm.put("@eventlog", context.toString());
					dao.update(sqlstr.toString(),dataList);
					
				}
			
			}
			
			if("3".equals(type)) //人事异动
			{
				TemplateStaticDataBo.getAllVariableHm(Integer.parseInt(cstate), this.getFrameconn(),true); //将最新临时变量信息写入缓存
			}
			hm.put("storeList", storeList);
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(this.frowset);
		}
		
		
	}
	
	/** 得到最大序号 **/
	private int getSorting(ContentDAO dao,String cstate,String type){
		int sorting = 0;
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select max(sorting) as sort from midvariable where ");
		if("1".equals(type)){
			sqlstr.append("cstate=?");
		}else if("3".equals(type)){
			sqlstr.append("templetid=?");
		}
		
		try {
			this.frowset = dao.search(sqlstr.toString(),Arrays.asList(cstate));
			while(this.frowset.next()){
				sorting=this.frowset.getInt("sort");
				sorting+=1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		finally{
			PubFunc.closeResource(this.frowset);
		}
		return sorting;
	}
	
	/** 数据类型 **/
	private HashMap codeMap(){
		HashMap map = new HashMap();
		try {
			map.put("1", ResourceFactory.getProperty("system.item.ntype"));//数值型
			map.put("2", ResourceFactory.getProperty("system.item.ctype"));//字符型
			map.put("3", ResourceFactory.getProperty("system.item.dtype"));//日期型
			map.put("4", ResourceFactory.getProperty("system.item.cdtype"));//代码型
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
