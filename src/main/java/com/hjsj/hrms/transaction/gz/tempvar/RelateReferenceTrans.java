package com.hjsj.hrms.transaction.gz.tempvar;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class RelateReferenceTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		String flag = (String) this.getFormHM().get("type");// 3:人事异动模块1:薪资类别 5：数据采集 2:暂定和1是一个意思，zhaoxg add 2015-2-6
		ContentDAO dao = new ContentDAO(this.frameconn);
		String nid = (String) hm.get("nid");
		String cstate = (String) hm.get("cstate");//该临时变量所在的薪资类别
		String isok = "1";// you 0
		StringBuffer error = new StringBuffer("");
		StringBuffer errors = new StringBuffer("");
		// 首先查出临时变量名字
		String tempVariableCName = "";
		String tempVariableChz = "";
		String tempCstate = "";
		String isShare = "0";// 是否共享 0：不共享 1：共享
		int nflag=0;
		int templetId = 0;
		StringBuffer sbquery = new StringBuffer("");
		sbquery.append("select cname,chz,cstate,templetID,nflag from midvariable where nid= "
				+ nid);
		try {
			this.frowset = dao.search(sbquery.toString());
			if (this.frowset.next()) {
				tempVariableCName = frowset.getString(1);
				tempVariableChz = frowset.getString(2);
				tempCstate = this.frowset.getString("cstate") == null ? ""
						: this.frowset.getString("cstate");
				templetId = this.frowset.getInt("templetID");
				nflag=this.frowset.getInt("nflag");
				if ("1".equals(flag)||"2".equals(flag)) {// 以果是从薪资类别模块进入
					if ("".equals(tempCstate)) {// 从薪资类别进入的时候，cstate是null才是共享
						isShare = "1";
					}
				} else if ("3".equals(flag)) {// 以果是从人事异动模板进入
					if ("1".equals(tempCstate)) {// 从人事异动模板进入的时候，cstate是1才是共享
						isShare = "1";
					}
				} else if("5".equals(flag)){
					if ("".equals(tempCstate)) {// 从数据采集进入的时候，cstate是null才是共享
						isShare = "1";
					}
				}
			}
			StringBuffer sb = new StringBuffer("");
			if ("1".equals(flag)||"2".equals(flag)) {
				// 先检查临时变量表达式是否引用
				boolean b = false;
				if(nflag==4){
				    sb.append("select cstate,chz,cvalue from midvariable where nflag=4 and templetId = 0 "); //薪资类别和薪资总额进入时 查询是不同的 区别就是Nfalg 0：薪资类别 4：薪资总额 
				}else{
				    sb.append("select cstate,chz,cvalue from midvariable where nflag=0 and templetId = 0 ");
				}
				if(!"1".equals(isShare)){//如果果不是共享
					sb.append(" and cstate='"+tempCstate+"'");
				}
				this.frowset = dao.search(sb.toString());
				String cvalue = "";
				String temp = ""; //存放查出来的cstate
				while (this.frowset.next()) {
					cvalue = Sql_switcher.readMemo(this.frowset, "cvalue") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "cvalue");
					temp = this.frowset.getString("cstate") == null ? ""
							: this.frowset.getString("cstate");
					b = PubFunc.IsHasVariable(cvalue, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(cvalue, tempVariableChz); // 再查找中文名
					}
					if (b) {
						// 表示该表达式引用了该临时变量
						if ("".equals(tempCstate)) {// 从薪资类别进入的时候，cstate是null才是共享
							if("".equals(temp) || temp==null){
								temp = cstate;
							}
						} else 
							temp = cstate;
						//根据薪资类别号查出薪资类别名
						sb.setLength(0);
						sb.append("select cname from salarytemplate where salaryid="+temp+" ");
						this.frecset = dao.search(sb.toString());
						String csalaryname = "";
						while (this.frecset.next()) {
							csalaryname = this.frecset.getString("cname") == null ? ""
									: this.frecset.getString("cname");
						}
						errors.append("  "+csalaryname+"("+ temp + "): ");
						errors.append(frowset.getString("chz"));
						errors.append("\r\n");
					}
				}
				if(errors != null && errors.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下临时变量引用");
					error.append("\r\n");
					error.append(errors);
				}
				// 接着检查计算公式中是否引用
				sb.setLength(0);
				if(nflag==4){
				    sb.append("select salaryid,hzname,rexpr from salaryformula where salaryid=-1");
				}else{
				    sb.append("select salaryid,hzname,rexpr from salaryformula where salaryid<>-2 and salaryid<>-1");
				}
				if(!"1".equals(isShare)){//以果不是共享
					sb.append(" and salaryid="+tempCstate+"");
				}
				this.frowset = dao.search(sb.toString());
				String rexpr = "";
				errors.setLength(0);
				while (this.frowset.next()) {
					rexpr = Sql_switcher.readMemo(this.frowset, "rexpr") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "rexpr");
					Integer dd =new Integer(this.frowset.getInt("salaryid"));
					if(dd==null){
					    temp=""; 
					}else{
					    temp=dd.toString();
					}
					//temp = Sql_switcher.readMemo(this.frowset, "salaryid") == null ? ""
					//		: Sql_switcher.readMemo(this.frowset, "salaryid");
					b = PubFunc.IsHasVariable(rexpr, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(rexpr, tempVariableChz); // 再查找中文名
					}
					if (b) {
						// 表示该计算公式引用了英文的临时变量
						if (!"".equals(tempCstate)) {// 从薪资类别进入的时候，cstate是null才是共享
							temp = cstate;
						} 
						//根据薪资类别号查出薪资类别名
						sb.setLength(0);
						sb.append("select cname from salarytemplate where salaryid="+temp+" ");
						this.frecset = dao.search(sb.toString());
						String csalaryname = "";
						while (this.frecset.next()) {
							csalaryname = this.frecset.getString("cname") == null ? ""
									: this.frecset.getString("cname");
						}
						errors.append("  "+csalaryname+"("+ temp + "): ");
						errors.append(frowset.getString("hzname"));
						errors.append("\r\n");
					}
				}
				if(errors != null && errors.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下计算公式引用");
					error.append("\r\n");
					error.append(errors);
				}
				// 接着检查计算公式表达式中是否引用
				sb.setLength(0);
				if(nflag==4){
                    sb.append("select salaryid,hzname,cond from salaryformula where salaryid=-1");//薪资总额中的临时变量目前是 无法共享的 xcs 2013-10-30
                }else{
                    sb.append("select salaryid,hzname,cond from salaryformula where salaryid<>-2 and salaryid<>-1");
                    if(!"1".equals(isShare)){//以果不是共享
                        sb.append(" and salaryid="+tempCstate+"");
                    }
                }
				
				this.frowset = dao.search(sb.toString());
				errors.setLength(0);
				while (this.frowset.next()) {
					String cond = Sql_switcher.readMemo(this.frowset, "cond") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "cond");
					Integer dd =new Integer(this.frowset.getInt("salaryid"));
                    if(dd==null){
                        temp=""; 
                    }else{
                        temp=dd.toString();
                    }
					/*temp = Sql_switcher.readMemo(this.frowset, "salaryid") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "salaryid");*/
					b = PubFunc.IsHasVariable(cond, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(cond, tempVariableChz); // 再查找中文名
					}
					if (b) {
						// 表示该计算条件引用了英文的临时变量
						if (!"".equals(tempCstate)) {// 从薪资类别进入的时候，cstate是null才是共享
							temp = cstate;
						} 
						//根据薪资类别号查出薪资类别名
						sb.setLength(0);
						sb.append("select cname from salarytemplate where salaryid="+temp+" ");
						this.frecset = dao.search(sb.toString());
						String csalaryname = "";
						while (this.frecset.next()) {
							csalaryname = this.frecset.getString("cname") == null ? ""
									: this.frecset.getString("cname");
						}
						errors.append("  "+csalaryname+"("+ temp + "): ");
						errors.append(frowset.getString("hzname"));
						errors.append("\r\n");
					}
				}
				if(errors != null && errors.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下计算公式条件表达式引用");
					error.append("\r\n");
					error.append(errors);
				}
				// 最后判断审核公式中是否引用
				sb.setLength(0);
				sb.append("select chkid,name,formula,tabid from hrpchkformula");
				if(!"1".equals(isShare)){//以果不是共享
					sb.append(" where chkid="+tempCstate+"");
				}
				errors.setLength(0);
				this.frowset = dao.search(sb.toString());
				while (this.frowset.next()) {
					String formula = Sql_switcher.readMemo(this.frowset,
							"formula") == null ? "" : Sql_switcher.readMemo(
							this.frowset, "formula");
					Integer dd =new Integer(this.frowset.getInt("chkid"));
                    if(dd==null){
                        temp=""; 
                    }else{
                        temp=dd.toString();
                    }
                    /*
					temp = Sql_switcher.readMemo(this.frowset, "chkid") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "chkid");*/
					b = PubFunc.IsHasVariable(formula, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(formula, tempVariableChz); // 再查找中文名
					}
					if (b) {
						// 表示该审核公式引用了英文的临时变量
						if (!"".equals(tempCstate)) {// 从薪资类别进入的时候，cstate是null才是共享
							temp = cstate;
						} 
						//根据薪资类别号查出薪资类别名
						sb.setLength(0);
						sb.append("select cname from salarytemplate where salaryid="+frowset.getString("tabid")+" ");//zhaoxg 2013-10-12 原来写错了吧？反正改了哈
						this.frecset = dao.search(sb.toString());
						String csalaryname = "";
						while (this.frecset.next()) {
							csalaryname = frecset.getString("cname") == null ? ""
									: frecset.getString("cname");
						}
						errors.append("  "+csalaryname+"("+ frowset.getString("tabid") + "): ");
						errors.append(frowset.getString("name"));
						errors.append("\r\n");
					}
				}
				if(errors != null && errors.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下审核公式引用");
					error.append("\r\n");
					error.append(errors);
				}
			} else if ("3".equals(flag)) {
				// 先检查临时变量表达式是否引用
				boolean b = false;
				/**
				sb.append("select chz,cvalue from midvariable where nflag=0 and templetId = "+templetId+" ");
				if(!isShare.equals("1")){//此 if语句下 代表 该临时变量是不共享的 
					sb.append("  and cstate='"+tempCstate+"' or cstate is null "); 
				}
				**/
				if(!"1".equals(isShare)){//如果该临时变量不是共享的 那么只需查询 该临时变量归属的模版所拥有的临时变量是否引用该临时变量
				    sb.append("select chz,cvalue,templetId from midvariable where nflag=0 and templetId = "+templetId+" ");
				}else{//查询全部人事异动的所有临时变量
				    sb.append("select chz,cvalue,templetId from midvariable where nflag=0 and templetId <> 0 ");
				}
				this.frowset = dao.search(sb.toString());
				String cvalue = "";
				errors.setLength(0);
				while (this.frowset.next()) {
					cvalue = Sql_switcher.readMemo(this.frowset, "cvalue") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "cvalue");
					int tabid =this.frowset.getInt("templetId");
					b = PubFunc.IsHasVariable(cvalue, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(cvalue, tempVariableChz); // 再查找中文名
					}
					if (b) {
						// 表示该表达式引用了英文的临时变量
						//errors.append("  模板号("+ cstate + "): ");
					    errors.append("  模板号("+ tabid + "): ");
						errors.append(frowset.getString("chz")); //之前显示的是表达式的值，现在改为临时变量的值，更为合理一些 20151010 liuzy
						errors.append("\r\n");
					}
				}
				if(errors != null && errors.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下临时变量的表达式引用");
					error.append("\r\n");
					error.append(errors);
				}
				// 接着检查计算公式表达式中是否引用该临时变量
				sb.setLength(0);
				if(!"1".equals(isShare)){//如果不是共享 查询该模版下的 计算公式表达式
				    sb.append("select chz,formula,tabid from gzadj_formula where tabid="+templetId+"");
				}else{
				    sb.append("select chz,formula,tabid from gzadj_formula");
				}
				errors.setLength(0);
				this.frowset = dao.search(sb.toString());
				while (this.frowset.next()) {
					String formula = Sql_switcher.readMemo(this.frowset,
							"formula") == null ? "" : Sql_switcher.readMemo(
							this.frowset, "formula");
					int tabid = this.frowset.getInt("tabid");
					b = PubFunc.IsHasVariable(formula, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(formula, tempVariableChz); // 再查找中文名
					}
					if (b) {
						// 表示该计算公式引用了英文的临时变量
						errors.append("  模板号("+ tabid + "): ");
						errors.append(frowset.getString("chz"));
						errors.append("\r\n");
					}
				}
				if(errors != null && errors.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下计算公式表达式引用");
					error.append("\r\n");
					error.append(errors);
				}
				// 接着检查计算公式计算条件中是否引用
				sb.setLength(0);
				/**sb.append("select chz,cfactor from gzadj_formula ");
				if(!isShare.equals("1")){//以果不是共享
					sb.append(" where tabid="+templetId+"");
				}**/
				if(!"1".equals(isShare)){//如果不是共享 查询该模版下的 计算公式计算条件
                    sb.append("select chz,cfactor,tabid from gzadj_formula where tabid="+templetId+"");
                }else{
                    sb.append("select chz,cfactor,tabid from gzadj_formula");
                }
				errors.setLength(0);
				this.frowset = dao.search(sb.toString());
				while (this.frowset.next()) {
					String cfactor = Sql_switcher.readMemo(this.frowset,
							"cfactor") == null ? "" : Sql_switcher.readMemo(
							this.frowset, "cfactor");
					int tabid = this.frowset.getInt("tabid");
					b = PubFunc.IsHasVariable(cfactor, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(cfactor, tempVariableChz); // 再查找中文名
					}
					if (b) {
						// 表示该计算条件引用了英文的临时变量
						errors.append("  模板号("+ tabid + "): ");
						errors.append(frowset.getString("chz"));
						errors.append("\r\n");
					}
				}
				if(errors != null && errors.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下计算公式中的计算条件表达式引用");
					error.append("\r\n");
					error.append(errors);
				}
				// 接着检查检索条件中是否引用
                sb.setLength(0);
                errors.setLength(0);
                if(!"1".equals(isShare)){//如果不是共享
                    sb.append("select name,factor,tabid from template_table where tabid= "+templetId+"");
                }else{
                    sb.append("select name,factor,tabid from template_table");
                }
                this.frowset = dao.search(sb.toString());
                while (this.frowset.next()) {
                    String factor = Sql_switcher.readMemo(
                            this.frowset, "factor") == null ? ""
                            : Sql_switcher.readMemo(this.frowset,
                                    "factor");
                    int tabid = this.frowset.getInt("tabid");
                    b = PubFunc.IsHasVariable(factor,tempVariableCName); // 先查找英文名
                    if (!b) {
                        b = PubFunc.IsHasVariable(factor,tempVariableChz); // 再查找中文名
                    } 
                    if (b) {
                        // 表示该计算条件引用了英文的临时变量
                        errors.append("  模板号("+ tabid + "): ");
                        errors.append(frowset.getString("name"));
                        errors.append("\r\n");
                    }
                }
                if(errors != null && errors.length()>0){
                    error.append("临时变量("+tempVariableChz+" )被以下检索公式表达式引用");
                    error.append("\r\n");
                    error.append(errors);
                }
				// 最后判断校验公式中是否引用
				sb.setLength(0);
				if(!"1".equals(isShare)){//以果不是共享
					sb.append("select name,formula,tabid from hrpChkformula where flag=0 and tabid="+templetId+"");
				}else{
				    sb.append("select name,formula,tabid from hrpChkformula where flag=0");
				}
				errors.setLength(0);
				this.frowset = dao.search(sb.toString());
				while (this.frowset.next()) {
					String formula = Sql_switcher.readMemo(this.frowset,
							"formula") == null ? "" : Sql_switcher.readMemo(
							this.frowset, "formula");
					int tabid = this.frowset.getInt("tabid");
					b = PubFunc.IsHasVariable(formula, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(formula, tempVariableChz); // 再查找中文名
					}
					if (b) {
						// 表示该审核公式引用了英文的临时变量
						errors.append("  模板号("+ tabid + "): ");
						errors.append(frowset.getString("name"));
						errors.append("\r\n");
					}
				}
				if(errors != null && errors.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下校验公式表达式引用");
					error.append("\r\n");
					error.append(errors);
				}
			}else if("5".equals(flag)){
				// 先检查临时变量表达式是否引用
				boolean b = false;
				sb.append("select cstate,chz,cvalue from midvariable where nflag=5 and templetId = 0 ");
				if(!"1".equals(isShare)){//以果不是共享
					sb.append(" and cstate='"+tempCstate+"'");
				}
				this.frowset = dao.search(sb.toString());
				String cvalue = "";
				String temp = ""; //存放查出来的cstate
				while (this.frowset.next()) {
					cvalue = Sql_switcher.readMemo(this.frowset, "cvalue") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "cvalue");
					temp = this.frowset.getString("cstate") == null ? ""
							: this.frowset.getString("cstate");
					b = PubFunc.IsHasVariable(cvalue, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(cvalue, tempVariableChz); // 再查找中文名
					}
					if (b) {
						// 表示该表达式引用了英文的临时变量
						errors.append("  模板号("+ cstate + "): ");
						errors.append(frowset.getString("cvalue"));
						errors.append("\r\n");
					}
				}
				if(errors != null && errors.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下临时变量引用");
					error.append("\r\n");
					error.append(errors);
				}
				// 接着检查计算公式中是否引用
				sb.setLength(0);
				sb.append("select salaryid,hzname,rexpr from salaryformula where salaryid=-2 ");
				if(!"1".equals(isShare)){//以果不是共享
					sb.append("and cstate='"+tempCstate+"'");
				}
				errors.setLength(0);
				this.frowset = dao.search(sb.toString());
				String rexpr = "";
				errors.setLength(0);
				while (this.frowset.next()) {
					rexpr = Sql_switcher.readMemo(this.frowset, "rexpr") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "rexpr");
					Integer dd =new Integer(this.frowset.getInt("salaryid"));
                    if(dd==null){
                        temp=""; 
                    }else{
                        temp=dd.toString();
                    }
                    /*
					temp = Sql_switcher.readMemo(this.frowset, "salaryid") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "salaryid");*/
					b = PubFunc.IsHasVariable(rexpr, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(rexpr, tempVariableChz); // 再查找中文名
					}
					if (b) {
						// 表示该表达式引用了英文的临时变量
						errors.append("  模板号("+ cstate + "): ");
						errors.append(frowset.getString("hzname"));
						errors.append("\r\n");
					}
				}
				if(errors != null && errors.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下计算公式引用");
					error.append("\r\n");
					error.append(errors);
				}
				// 接着检查计算公式表达式中是否引用
				sb.setLength(0);
				sb.append("select salaryid,hzname,cond from salaryformula where salaryid=-2 ");
				if(!"1".equals(isShare)){//以果不是共享
					sb.append(" and cstate='"+tempCstate+"' ");
				}
				this.frowset = dao.search(sb.toString());
				errors.setLength(0);
				while (this.frowset.next()) {
					String cond = Sql_switcher.readMemo(this.frowset, "cond") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "cond");
					Integer dd =new Integer(this.frowset.getInt("salaryid"));
                    if(dd==null){
                        temp=""; 
                    }else{
                        temp=dd.toString();
                    }
                    /*
					temp = Sql_switcher.readMemo(this.frowset, "salaryid") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "salaryid");*/
					b = PubFunc.IsHasVariable(cond, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(cond, tempVariableChz); // 再查找中文名
					}
					if (b) {
						
						// 表示该表达式引用了英文的临时变量
						errors.append("  模板号("+ cstate + "): ");
						errors.append(frowset.getString("hzname"));
						errors.append("\r\n");
					}
				}
				if(errors != null && errors.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下计算公式条件表达式引用");
					error.append("\r\n");
					error.append(errors);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		if (error != null && error.length()>0) {
			// 将查出来的包含该临时变量的表达式写进txt中
			isok="0";
			String _filename = this.userView.getUserName()+"_"+PubFunc.getStrg()+".txt";
			String filename = PubFunc.getTxtFile(error.toString(),_filename);
			/* 安全问题 薪资总额 设置 临时变量 相关引用 xiaoyun 2014-9-13 start */
			filename = SafeCode.encode(PubFunc.encrypt(filename));
			/* 安全问题 薪资总额 设置 临时变量 相关引用 xiaoyun 2014-9-13 end */
			this.formHM.put("filename", filename);
		}
		hm.put("isok", isok);

	}
}
