package com.hjsj.hrms.transaction.gz.tempvar;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SaveTempVarTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String sucess = "ok";
		String tempvarname = (String)hm.get("tempvarname");
		tempvarname=tempvarname!=null&&tempvarname.trim().length()>0?tempvarname:"";
		
		String ntype = (String)hm.get("ntype");
		ntype=ntype!=null&&ntype.trim().length()>0?ntype:"";
		
		String fidlen = (String)hm.get("fidlen");
		fidlen=fidlen!=null&&fidlen.trim().length()>0?fidlen:"10";
		
		String fiddec = (String)hm.get("fiddec");
		fiddec=fiddec!=null&&fiddec.trim().length()>0?fiddec:"0";
		
		String codesetid = (String)hm.get("codesetid");
		codesetid=codesetid!=null&&codesetid.trim().length()>0&& "4".equals(ntype)?codesetid:"";
		
		String type = (String)hm.get("type");
		type=type!=null&&type.trim().length()>0?type:"";
		
		String cstate = (String)hm.get("cstate");
		cstate=cstate!=null&&cstate.trim().length()>0?cstate:"";
		
		String flag = (String)hm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		
		String nflag = (String)hm.get("nflag");
		nflag=nflag!=null&&nflag.trim().length()>0?nflag:"0";
		
		String id = (String)hm.get("nid");
		id=id!=null&&id.trim().length()>0?id:"";
		
		String showflag = (String) hm.get("showflag");
		showflag=showflag!=null&&showflag.trim().length()>0?showflag:"0";
		
		this.getFormHM().put("showflag", showflag);
		
		if("1".equals(flag)){
			sucess="continue";
		}
		
		if(id.length()>0){//如果是修改
			//先查询。如果没有重名，才允许修改
			StringBuffer sbquery = new StringBuffer("");
			sbquery.append(" select * from midvariable where (chz='"+tempvarname+"' or cname='"+tempvarname+"') ");
			if("3".equals(type)){//如果是从人事异动模板进入的
				sbquery.append(" and nflag=0 and templetId <> 0 and (templetId ="+cstate+"or cstate = '1')");
			} else if("5".equals(type)){//如果是从数据采集进入的 xcs modify @2013-8-1
				sbquery.append(" and nflag=5 and templetId = 0 and (cstate ='"+cstate+"'or cstate is null)");
			} else if("1".equals(type)||"2".equals(type)){//如果是从薪资类别模板进入的
				sbquery.append(" and nflag=0 and templetId = 0 and (cstate ='"+cstate+"'or cstate is null)");
			}
			sbquery.append(" and nid<>"+id);
			try {
				this.frowset = dao.search(sbquery.toString());
				if(this.frowset.next()){
					hm.put("base","变量名已被占用！");//xieguiquan 2012-5-25
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
				sbquery.append("select cstate,chz,cname,templetID from midvariable where nid='"+id+"'");
				this.frowset = dao.search(sbquery.toString());
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
					/*
					templetId = this.frowset.getString("templetID")==null?"":this.frowset.getString("templetID");
					*/
					if("1".equals(type)){//如果是从薪资类别模块进入
						if("".equals(tempcstate)){//从薪资类别进入的时候，cstate是null才是共享
							isShare = "1";
						}
					}else if("3".equals(type)){//如果是从人事异动模板进入
						if("1".equals(tempcstate)){//从人事异动模板进入的时候，cstate是1才是共享
							isShare = "1";
						}
					}else if("5".equals(type)){//如果是从数据采集进入的 xcs modify @2013-8-1
						if("".equals(tempcstate)){//从数据采集进入的时候，cstate是null才是共享
							isShare = "1";
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			if(!tempCHZ.equals(tempvarname)){
				//不重名，还需要检查引用该临时变量的地方（如条件、公式、临时变量表达式）
				if("3".equals(type)){//如果是从人事异动业务模板进入的，则要检查三个表。template_set，template_table，gzadj_formula
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
					
				} else if("1".equals(type)){//如果是从薪资类别模板进入，则需要检查midvariable表（临时变量表达式）、salaryformula表（计算公式）
											//hrpchkformula表（审核公式）,并把包含该变量的名字存在StringBuffer里面
					boolean hasVariable = false;
					//首先检查midvariable表（临时变量表达式）
					StringBuffer sb_query = new StringBuffer("");
					sb_query.append("select cvalue from midvariable where nflag=0 and templetId = 0 ");
					if(!"1".equals(isShare)){//如果不是共享
						sb_query.append(" and cstate='"+tempcstate+"' ");
					}
					try{
						this.frowset = dao.search(sb_query.toString());
						while(this.frowset.next()){
							String strCvalue = Sql_switcher.readMemo(this.frowset, "cvalue")==null?"":Sql_switcher.readMemo(this.frowset, "cvalue");
							hasVariable = PubFunc.IsHasVariable(strCvalue,tempCHZ);
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
					//然后检查salaryformula表（计算公式）
					sb_query.setLength(0);
					sb_query.append("select rexpr from salaryformula where salaryid<>-1 and salaryid<>-2");
					if(!"1".equals(isShare)){//如果不是共享
						sb_query.append(" and salaryid="+tempcstate+"");
					}
					try{
						this.frowset = dao.search(sb_query.toString());
						while(this.frowset.next()){
							String rexpr = Sql_switcher.readMemo(this.frowset, "rexpr")==null?"":Sql_switcher.readMemo(this.frowset, "rexpr");
							hasVariable = PubFunc.IsHasVariable(rexpr,tempCHZ);
							if(hasVariable) {
								isok = "0";
								hm.put("base","计算公式中引用了该临时变量，不能更名！");
								return;
							}
							hasVariable = PubFunc.IsHasVariable(rexpr,tempCName);//查找英文名
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
					sb_query.append("select cond from salaryformula where salaryid<>-1 and salaryid<>-2");
					if(!"1".equals(isShare)){//如果不是共享
						sb_query.append(" and salaryid="+tempcstate+"");
					}
					try{
						this.frowset = dao.search(sb_query.toString());
						while(this.frowset.next()){
							String cond = Sql_switcher.readMemo(this.frowset, "cond")==null?"":Sql_switcher.readMemo(this.frowset, "cond");
							hasVariable = PubFunc.IsHasVariable(cond,tempCHZ);
							if(hasVariable) {
								isok = "0";
								hm.put("base","计算公式条件中引用了该临时变量，不能更名！");
								return;
							}
							hasVariable = PubFunc.IsHasVariable(cond,tempCName);//查找英文名
							if(hasVariable) {
								isok = "0";
								hm.put("base","计算公式条件中引用了该临时变量，不能更名！");
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
						sb_query.append(" where tabid="+tempcstate+"");
					}
					try{
						this.frowset = dao.search(sb_query.toString());
						while(this.frowset.next()){
							String formula = Sql_switcher.readMemo(this.frowset, "formula")==null?"":Sql_switcher.readMemo(this.frowset, "formula");
							hasVariable = PubFunc.IsHasVariable(formula,tempCHZ);
							if(hasVariable) {
								isok = "0";
								hm.put("base","审核公式条件中引用了该临时变量，不能更名！");
								return;
							}
							hasVariable = PubFunc.IsHasVariable(formula,tempCName);//查找英文名
							if(hasVariable) {
								isok = "0";
								hm.put("base","审核公式条件中引用了该临时变量，不能更名！");
								return;
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				} else if("5".equals(type)){
					//数据采集判断开始     xcs modify @ 2013-8-1
					//如果是从数据采集进入，则需要检查midvariable表（临时变量表达式）、salaryformula表（计算公式）,并把包含该变量的名字存在StringBuffer里面
					boolean hasVariable = false;
					//首先检查midvariable表（临时变量表达式）
					StringBuffer sb_query = new StringBuffer("");
					sb_query.append("select cvalue from midvariable where nflag=5 and templetId = 0 ");
					if(!"1".equals(isShare)){//如果不是共享
						sb_query.append(" and cstate='"+tempcstate+"' ");
					}
					try{
						this.frowset = dao.search(sb_query.toString());
						while(this.frowset.next()){
							String strCvalue = Sql_switcher.readMemo(this.frowset, "cvalue")==null?"":Sql_switcher.readMemo(this.frowset, "cvalue");
							hasVariable = PubFunc.IsHasVariable(strCvalue,tempCHZ);
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
					//然后检查salaryformula表（计算公式）
					sb_query.setLength(0);
					sb_query.append("select rexpr from salaryformula where  salaryid=-2");
					if(!"1".equals(isShare)){//如果不是共享
						sb_query.append(" and cstate='"+tempcstate+"'");
					}
					try{
						this.frowset = dao.search(sb_query.toString());
						while(this.frowset.next()){
							String rexpr = Sql_switcher.readMemo(this.frowset, "rexpr")==null?"":Sql_switcher.readMemo(this.frowset, "rexpr");
							hasVariable = PubFunc.IsHasVariable(rexpr,tempCHZ);
							if(hasVariable) {
								isok = "0";
								hm.put("base","计算公式中引用了该临时变量，不能更名！");
								return;
							}
							hasVariable = PubFunc.IsHasVariable(rexpr,tempCName);//查找英文名
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
					sb_query.append("select cond from salaryformula  where  salaryid=-2");
					if(!"1".equals(isShare)){//如果不是共享
						sb_query.append("  and cstate='"+tempcstate+"'");
					}
					try{
						this.frowset = dao.search(sb_query.toString());
						while(this.frowset.next()){
							String cond = Sql_switcher.readMemo(this.frowset, "cond")==null?"":Sql_switcher.readMemo(this.frowset, "cond");
							hasVariable = PubFunc.IsHasVariable(cond,tempCHZ);
							if(hasVariable) {
								isok = "0";
								hm.put("base","计算公式条件中引用了该临时变量，不能更名！");
								return;
							}
							hasVariable = PubFunc.IsHasVariable(cond,tempCName);//查找英文名
							if(hasVariable) {
								isok = "0";
								hm.put("base","计算公式条件中引用了该临时变量，不能更名！");
								return;
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}//数据采集判断结束
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
				sqlstr.append("update midvariable set chz='");
				sqlstr.append(tempvarname+"',ntype=");
				sqlstr.append(ntype+",fldlen=");
				sqlstr.append(fidlen+",flddec=");
				sqlstr.append(fiddec+",codesetid='");
				sqlstr.append(codesetid+"' where nid=");
				sqlstr.append(id+"");
				
				if("1".equals(type)){
					String _tempvarname = vo.getString("chz")==null?"":vo.getString("chz");
					String _ntype = vo.getString("ntype")==null?"":vo.getString("ntype");
					String _fidlen = vo.getString("fldlen")==null?"":vo.getString("fldlen");
					String _fiddec = vo.getString("flddec")==null?"":vo.getString("flddec");
					String _codesetid = vo.getString("codesetid")==null?"":vo.getString("codesetid");
					if(!_tempvarname.equals(tempvarname)||!_ntype.equals(ntype)||!_fidlen.equals(fidlen)||!_fiddec.equals(fiddec)||!_codesetid.equals(codesetid)){
						StringBuffer context = new StringBuffer();
						SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
						String name = bo.getSalaryName(cstate);
						context.append("修改："+name+"（"+cstate+"）修改临时变量（"+tempvarname+"）一般属性<br>");
						context.append("<table>");
						context.append("<tr>");
						context.append("<td>属性名</td>");
						context.append("<td>变化前</td>");
						context.append("<td>变化后</td>");
						context.append("</tr>");
						if(!_tempvarname.equals(tempvarname)){
							context.append("<tr>");
							context.append("<td>名称</td>");
							context.append("<td>"+_tempvarname+"</td>");
							context.append("<td>"+tempvarname+"</td>");
							context.append("</tr>");
						}
						if(!_ntype.equals(ntype)){
							HashMap map = this.codeMap();
							context.append("<tr>");
							context.append("<td>类型</td>");
							context.append("<td>"+map.get(_ntype)+"</td>");
							context.append("<td>"+map.get(ntype)+"</td>");
							context.append("</tr>");
						}
						if(!_fidlen.equals(fidlen)){
							context.append("<tr>");
							context.append("<td>长度</td>");
							context.append("<td>"+_fidlen+"</td>");
							context.append("<td>"+fidlen+"</td>");
							context.append("</tr>");
						}
						if(!_fiddec.equals(fiddec)){
							context.append("<tr>");
							context.append("<td>位数</td>");
							context.append("<td>"+_fiddec+"</td>");
							context.append("<td>"+fiddec+"</td>");
							context.append("</tr>");
						}
						if(!_codesetid.equals(codesetid)){
							context.append("<tr>");
							context.append("<td>代码</td>");
							context.append("<td>"+_codesetid+"</td>");
							context.append("<td>"+codesetid+"</td>");
							context.append("</tr>");
						}

						context.append("</table>");
						this.getFormHM().put("@eventlog", context.toString());
					}else{
						this.getFormHM().put("@eventlog", "修改：修改临时变量（"+tempvarname+"）属性没变");
					}
				}
				
				try {
					dao.update(sqlstr.toString());
					if("3".equals(type)){//如果是从人事异动业务模板进入的
						//修改template_set表
						StringBuffer sbupdate = new StringBuffer("");
						sbupdate.append("update template_set set field_hz='"+tempvarname+"' where field_name='"+id+"'");
						dao.update(sbupdate.toString());
					}
				} catch (SQLException e) {
					sucess = "update";
					e.printStackTrace();
				}
			}
			hm.put("nid", id);//用于刷新页面后定位行  zhaoxg add 2014-11-19
			hm.put("ntype", ntype);
		}else{
			TempvarBo tempvarbo = new TempvarBo();
			int nid = tempvarbo.getid(this.frameconn);
			//查询是否有重名的临时变量
			String sqlchz = " select * from midvariable where (chz='"+tempvarname+"' or cname='"+tempvarname+"') ";
			if("3".equals(type)){//如果是从人事异动模板进入的
				sqlchz+=" and nflag=0 and templetId <> 0 and (templetId ="+cstate+"or cstate = '1')";
			} else if("1".equals(type)||"2".equals(type)){//如果是从薪资类别模板进入的
				if("0".equals(nflag)){
					sqlchz+= " and nflag=0 and templetId = 0 and (cstate ='"+cstate+"'or cstate is null)";
				}else if("4".equals(nflag)){
					sqlchz+= " and nflag=4 and templetId = 0 and (cstate ='"+cstate+"'or cstate is null)";
				}
				
			} else if("5".equals(type)){//如果从数据采集进入 xcs modify @ 2013-8-1
				sqlchz+= " and nflag=5 and templetId = 0 and (cstate ='"+cstate+"'or cstate is null)";
			}
			try {
				this.frowset = dao.search(sqlchz);
				if(this.frowset.next()){
					hm.put("base","变量名已被占用！");//xieguiquan 2012-5-25
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
			
			if(!"3".equals(type)){
				if("5".equals(type)){//xcs modify @ 2013-8-1
					sqlstr.append("insert into midvariable(nid,cname,chz,ntype,cstate,fldlen,flddec,templetid,codesetid,nflag,sorting) values(");
					sqlstr.append(nid+",'"+cname_new+"','"+tempvarname+"','"+ntype+"','"+cstate+"',");
					sqlstr.append(fidlen+","+fiddec+",0,'"+codesetid+"',"+nflag+",");
					sqlstr.append(getSorting(dao,cstate,type)+")");
				}else{
					sqlstr.append("insert into midvariable(nid,cname,chz,ntype,cstate,fldlen,flddec,templetid,codesetid,nflag,sorting) values(");
					sqlstr.append(nid+",'"+cname_new+"','"+tempvarname+"','"+ntype+"',"+cstate+",");
					sqlstr.append(fidlen+","+fiddec+",0,'"+codesetid+"',"+nflag+",");
					sqlstr.append(getSorting(dao,cstate,type)+")");
					if("1".equals(type)){
						StringBuffer context = new StringBuffer();
						SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
						String name = bo.getSalaryName(cstate);
						context.append("新增："+name+"（"+cstate+"）新增临时变量（"+tempvarname+"）<br>");

						this.getFormHM().put("@eventlog", context.toString());
					}
				}
			}else{
				sqlstr.append("insert into midvariable(nid,cname,chz,ntype,fldlen,flddec,templetid,codesetid,nflag,sorting) values(");
				sqlstr.append(nid+",'"+cname_new+"','"+tempvarname+"','"+ntype+"',");
				sqlstr.append(fidlen+","+fiddec+","+cstate+",'"+codesetid+"',"+nflag+",");
				sqlstr.append(getSorting(dao,cstate,type)+")");

			}
		
			try {
				dao.update(sqlstr.toString());
			} catch (SQLException e) {
				sucess = "insert";
				e.printStackTrace();
			}
			hm.put("nid", nid+"");//用于刷新页面后定位行  zhaoxg add 2014-11-19
			hm.put("ntype", ntype);
		}
		
		hm.put("base",sucess);
	}
	private int getSorting(ContentDAO dao,String cstate,String type){
		int sorting = 0;
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select max(sorting) as sort from midvariable where ");
		if(!"3".equalsIgnoreCase(type)){
			sqlstr.append("cstate='");
		}else{
			sqlstr.append("templetid=");
		}
		if(!"3".equalsIgnoreCase(type)){
			sqlstr.append(cstate+"'");
		}else{
			sqlstr.append(cstate);
		}
		try {
			this.frowset = dao.search(sqlstr.toString());
			while(this.frowset.next()){
				sorting=this.frowset.getInt("sort");
				sorting+=1;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sorting;
	}
	private HashMap codeMap(){
		HashMap map = new HashMap();
		try {
			map.put("1", "数值型");
			map.put("2", "字符型");
			map.put("3", "日期型");
			map.put("4", "代码型");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
}
