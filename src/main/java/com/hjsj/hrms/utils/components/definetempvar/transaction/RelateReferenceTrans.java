package com.hjsj.hrms.utils.components.definetempvar.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.definetempvar.businessobject.DefineTempVarBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 项目名称 ：ehr7.x
 * 类名称：RelateReferenceTrans
 * 类描述：当前临时变量的相关引用
 * 创建人： lis
 * 创建时间：2015-10-30
 */
public class RelateReferenceTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		DefineTempVarBo bo = new DefineTempVarBo(this.getFrameconn(), this.userView);
		String type = (String) this.getFormHM().get("type");// 3:人事异动模块 1:薪资类别 5：数据采集 2:暂定和1是一个意思，zhaoxg add 2015-2-6
		ContentDAO dao = new ContentDAO(this.frameconn);
		String nid = (String) hm.get("nid");//该临时变量的id
		String cstate = (String) hm.get("cstate");//该临时变量所在的薪资类别
		if(!"3".equals(type))
			cstate = PubFunc.decrypt(SafeCode.decode(cstate));
		String isok = "1";// you 0
		StringBuffer error = new StringBuffer("");
		StringBuffer errors1 = new StringBuffer("");//临时变量引用
		StringBuffer errors2 = new StringBuffer("");//计算公式表达式引用
		StringBuffer errors3 = new StringBuffer("");//计算公式计算条件表达式引用
		StringBuffer errors4 = new StringBuffer("");//审核公式条件表达式引用
		// 首先查出临时变量名字
		String tempVariableCName = "";
		String tempVariableChz = "";
		String tempCstate = "";
		String isShare = "0";// 是否共享 0：不共享 1：共享
		int nflag=0;
		int templetId = 0;
		StringBuffer sbquery = new StringBuffer("");
		sbquery.append("select cname,chz,cstate,templetID,nflag from midvariable where nid=? ");
		try {
			this.frowset = dao.search(sbquery.toString(),Arrays.asList(nid));
			if (this.frowset.next()) {
				tempVariableCName = frowset.getString(1);//查询当前临时变量
				tempVariableChz = frowset.getString(2);//临时变量中文名称
				tempCstate = this.frowset.getString("cstate") == null ? ""//是否共享，如果是共享则是null
						: this.frowset.getString("cstate");
				templetId = this.frowset.getInt("templetID");//模板号
				nflag=this.frowset.getInt("nflag");
				if ("1".equals(type)) { //从薪资类别模块进入
					if (StringUtils.isBlank(tempCstate)) {// 从薪资类别进入的时候，cstate是null才是共享
						isShare = "1";
					}
				}else if ("3".equals(type)) {// 以果是从人事异动模板进入
					if ("1".equals(tempCstate)) {// 从人事异动模板进入的时候，cstate是1才是共享
						isShare = "1";
					}
				} 
			}
			StringBuffer sb = new StringBuffer("");
			if ("1".equals(type)) { //从薪资类别模块进入
				// 先检查临时变量表达式是否引用
				boolean b = false;
				ArrayList list = new ArrayList(); 
				sb.append("select cstate,chz,cvalue from midvariable where nflag=? and templetId = 0 "); //薪资类别和薪资总额进入时 查询是不同的 区别就是Nfalg 0：薪资类别 4：薪资总额 
				list.add(nflag);
				if(!"1".equals(isShare)){//如果果不是共享
					sb.append(" and cstate=?");
					list.add(tempCstate);
				}
				this.frowset = dao.search(sb.toString(),list);
				String cvalue = "";
				String temp = ""; //存放查出来的cstate
				while (this.frowset.next()) {
					cvalue = Sql_switcher.readMemo(this.frowset, "cvalue") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "cvalue");
					temp = this.frowset.getString("cstate") == null ? ""
							: this.frowset.getString("cstate");
					//判断公式中是否用到了要改名的临时变量
					b = PubFunc.IsHasVariable(cvalue, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(cvalue, tempVariableChz); // 再查找中文名
					}
					if (b) {
						// 表示该表达式引用了该临时变量
						if (StringUtils.isBlank(tempCstate)) {// 从薪资类别进入的时候，cstate是null才是共享
							if(StringUtils.isBlank(temp)){
								temp = cstate;
							}
						} else 
							temp = cstate;
						//根据薪资类别号查出薪资类别名
						String csalaryname = bo.getSalaryName(temp);
						
						errors1.append("  "+csalaryname+"("+ temp + "): ");
						errors1.append(frowset.getString("chz"));
						errors1.append("\r\n");
					}
				}
				String var = ResourceFactory.getProperty("label.gz.variable");
				if(StringUtils.isNotBlank(errors1.toString())){
					error.append(var+"("+tempVariableChz+" )"+ResourceFactory.getProperty("gz_new.gz_useByVar"));
					error.append("\r\n");
					error.append(errors1);
				}
				// 接着检查计算公式中是否引用
				sb.setLength(0);
				if(nflag==0){
				    sb.append("select salaryid,hzname,rexpr,cond from salaryformula where salaryid<>-2 and salaryid<>-1");
				}
				ArrayList<String> list2 = new ArrayList<String>();
				if(!"1".equals(isShare)){//以果不是共享
					sb.append(" and salaryid=?");
					list2.add(tempCstate);
				}
				this.frowset = dao.search(sb.toString(),list2);
				String rexpr = "";//计算公式表达式
				while (this.frowset.next()) {
					
					rexpr = Sql_switcher.readMemo(this.frowset, "rexpr") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "rexpr");
					Integer dd =new Integer(this.frowset.getInt("salaryid"));
					if(dd==null){
					    temp=""; 
					}else{
					    temp=dd.toString();
					}
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
						String csalaryname = bo.getSalaryName(temp);
						
						errors2.append("  "+csalaryname+"("+ temp + "): ");
						errors2.append(frowset.getString("hzname"));
						errors2.append("\r\n");
					}
					
					// 接着检查计算公式计算条件表达式中是否引用
					String cond = Sql_switcher.readMemo(this.frowset, "cond") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "cond");
					
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
						errors3.append("  "+csalaryname+"("+ temp + "): ");
						errors3.append(frowset.getString("hzname"));
						errors3.append("\r\n");
					}
				}
				if(StringUtils.isNotBlank(errors2.toString())){
					//被以下计算公式表达式引用
					error.append(var+"("+tempVariableChz+" )"+ResourceFactory.getProperty("gz_new.gz_useByFomula"));
					error.append("\r\n");
					error.append(errors2);
				}
				
				if(StringUtils.isNotBlank(errors3.toString())){
					//被以下计算公式计算条件表达式引用
					error.append(var+"("+tempVariableChz+" )"+ResourceFactory.getProperty("gz_new.gz_useByOtherFomula"));
					error.append("\r\n");
					error.append(errors3);
				}
				
				// 最后判断审核公式中是否引用
				sb.setLength(0);
				sb.append("select chkid,name,formula,tabid from hrpchkformula");
				ArrayList<String> list3 = new ArrayList<String>();
				if(!"1".equals(isShare)){//如果不是共享
					sb.append(" where chkid=?");
					list3.add(tempCstate);
				}
				this.frowset = dao.search(sb.toString(),list3);
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
						errors4.append("  "+csalaryname+"("+ frowset.getString("tabid") + "): ");
						errors4.append(frowset.getString("name"));
						errors4.append("\r\n");
					}
				}
				if(StringUtils.isNotBlank(errors4.toString())){
					error.append(var + "("+tempVariableChz+" )"+ResourceFactory.getProperty("gz_new.gz_useByOtherSpFomula"));
					error.append("\r\n");
					error.append(errors4);
				}
			}else if ("3".equals(type)) {
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
				error.setLength(0);
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
						errors1.append("  模板号("+ tabid + "): ");
						errors1.append(frowset.getString("chz")); //之前显示的是表达式的值，现在改为临时变量的值，更为合理一些 20151010 liuzy
						errors1.append("\r\n");
					}
				}
				if(errors1 != null && errors1.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下临时变量的表达式引用");
					error.append("\r\n");
					error.append(errors1);
				}
				// 接着检查计算公式表达式中是否引用该临时变量
				sb.setLength(0);
				if(!"1".equals(isShare)){//如果不是共享 查询该模版下的 计算公式表达式
				    sb.append("select chz,formula,tabid from gzadj_formula where tabid="+templetId+"");
				}else{
				    sb.append("select chz,formula,tabid from gzadj_formula");
				}
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
						errors2.append("  模板号("+ tabid + "): ");
						errors2.append(frowset.getString("chz"));
						errors2.append("\r\n");
					}
				}
				if(errors2 != null && errors2.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下计算公式表达式引用");
					error.append("\r\n");
					error.append(errors2);
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
						errors3.append("  模板号("+ tabid + "): ");
						errors3.append(frowset.getString("chz"));
						errors3.append("\r\n");
					}
				}
				if(errors3 != null && errors3.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下计算公式中的计算条件表达式引用");
					error.append("\r\n");
					error.append(errors3);
				}
				// 接着检查检索条件中是否引用
                sb.setLength(0);
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
                    	errors4.append("  模板号("+ tabid + "): ");
                    	errors4.append(frowset.getString("name"));
                    	errors4.append("\r\n");
                    }
                }
                if(errors4 != null && errors4.length()>0){
                    error.append("临时变量("+tempVariableChz+" )被以下检索公式表达式引用");
                    error.append("\r\n");
                    error.append(errors4);
                }
				// 最后判断校验公式中是否引用
				sb.setLength(0);
				if(!"1".equals(isShare)){//以果不是共享
					sb.append("select name,formula,tabid from hrpChkformula where flag=0 and tabid="+templetId+"");
				}else{
				    sb.append("select name,formula,tabid from hrpChkformula where flag=0");
				}
				errors4.setLength(0);
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
						errors4.append("  模板号("+ tabid + "): ");
						errors4.append(frowset.getString("name"));
						errors4.append("\r\n");
					}
				}
				if(errors4 != null && errors4.length()>0){
					error.append("临时变量("+tempVariableChz+" )被以下校验公式表达式引用");
					error.append("\r\n");
					error.append(errors4);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(this.frecset);
		}
		if (error != null && error.length()>0) {
			isok="0";
			this.getFormHM().put("textValue", error.toString());
		}
		hm.put("isok", isok);

	}
}
