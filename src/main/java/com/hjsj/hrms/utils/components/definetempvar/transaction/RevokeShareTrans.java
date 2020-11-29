package com.hjsj.hrms.utils.components.definetempvar.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Arrays;
/**
 * 项目名称 ：ehr
 * 类名称：RevokeShareTrans
 * 类描述：取消共享
 * 创建人： lis
 * 创建时间：2015-10-30
 */
public class RevokeShareTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String type=(String)this.getFormHM().get("type");//1：薪资发放，3:人事异动模块 ，5:数据采集
			String nid = (String) this.getFormHM().get("nid");//当前临时变量id
			String cstate = (String) this.getFormHM().get("salaryid");//薪资类别id
			if(!"3".equals(type))//人事异动不需要加密解密,gaohy,2016-1-6
				cstate = PubFunc.decrypt(SafeCode.decode(cstate));
			StringBuffer sb = new StringBuffer("");
			ContentDAO dao = new ContentDAO(this.frameconn);
			// 首先查出临时变量名字
			String tempVariableCName = "";
			String tempVariableChz = "";
			String templetId = "";
			StringBuffer sbquery = new StringBuffer("");
			sbquery.append("select cname,chz,templetID from midvariable where nid=?");
			this.frowset = dao.search(sbquery.toString(),Arrays.asList(nid));
			if (this.frowset.next()) {
				tempVariableCName = frowset.getString(1);
				tempVariableChz = frowset.getString(2);
				templetId = this.frowset.getString("templetID")==null?"":this.frowset.getString("templetID");
			}
			if ("1".equals(type)) {
				// 先检查临时变量表达式是否引用
				boolean b = false;
				sb.setLength(0);
				sb.append("select cvalue from midvariable where cstate<>? and nid<>? ");
				this.frowset = dao.search(sb.toString(),Arrays.asList(cstate,nid));
				String cvalue = "";
				while (this.frowset.next()) {
					cvalue = Sql_switcher.readMemo(this.frowset,
							"cvalue") == null ? "" : Sql_switcher.readMemo(
							this.frowset, "cvalue");
					b = PubFunc.IsHasVariable(cvalue, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(cvalue,tempVariableChz); // 再查找中文名
					}
					if(b) {
						// 表示该表达式引用了英文的临时变量
						this.formHM.put("base", ResourceFactory.getProperty("gz_new.gz_shareVarUseByVar"));//共享临时变量已被其他临时变量引用，不允许设为私有！
						return;
					}
				}
				// 接着检查计算公式中是否引用
				sb.setLength(0);
				sb.append("select rexpr from salaryformula where salaryid<>? and salaryid<>-2 and salaryid<>-1");
				this.frowset = dao.search(sb.toString(),Arrays.asList(cstate));
				String rexpr =  "";
				while (this.frowset.next()) {
					rexpr = Sql_switcher.readMemo(this.frowset, "rexpr") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "rexpr");
					b = PubFunc.IsHasVariable(rexpr, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(rexpr,tempVariableChz); // 再查找中文名
					} 
					if(b) {
						// 表示该计算公式引用了英文的临时变量
						this.formHM.put("base", ResourceFactory.getProperty("gz_new.gz_shareVarUseByFomula"));//共享临时变量已被其他薪资/保险类别中的计算公式表达式引用，不允许设为私有
						return;
					}
				}
				// 接着检查计算公式表达式中是否引用
				sb.setLength(0);
				sb.append("select cond from salaryformula where salaryid<>'"+cstate+"' and salaryid<>-2 and salaryid<>-1 ");
				this.frowset = dao.search(sb.toString());
				while (this.frowset.next()) {
					String cond = Sql_switcher.readMemo(this.frowset, "cond") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "cond");
					b = PubFunc.IsHasVariable(cond, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(cond, tempVariableChz); // 再查找中文名
					} 
					if(b) {
						// 表示该计算条件引用了英文的临时变量
						this.formHM.put("base", ResourceFactory.getProperty("gz_new.gz_shareVarUseByFomulaCond"));//共享临时变量已被其他薪资/保险类别中的计算公式的计算条件表达式引用，不允许设为私有
						return;
					}
				}
				// 最后判断审核公式中是否引用
				sb.setLength(0);
				sb.append("select formula from hrpchkformula where chkid<>'"+cstate+"' ");
				this.frowset = dao.search(sb.toString());
				while (this.frowset.next()) {
					String formula = Sql_switcher.readMemo(this.frowset,
							"formula") == null ? "" : Sql_switcher.readMemo(
							this.frowset, "formula");
					b = PubFunc.IsHasVariable(formula,
							tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(formula,
								tempVariableChz); // 再查找中文名
					} 
					if(b) {
						// 表示该审核公式引用了英文的临时变量
						this.formHM.put("base", ResourceFactory.getProperty("gz_new.gz_shareVarUseBySpFomula"));//共享临时变量已被其他薪资/保险类别中的审核公式引用，不允许设为私有
						return;
					}
				}
			}else if("3".equals(type)){
				// 先检查临时变量表达式是否引用
				boolean b = false;
				sb.append("select cvalue from midvariable where  nid<>'"+nid+"' and nflag=0 and templetId <> 0  ");//查询人事异动下不是该临时变量表达式中是否引用了该临时变量
				this.frowset = dao.search(sb.toString());
				String cvalue = "";
				while (this.frowset.next()) {
					cvalue = Sql_switcher.readMemo(this.frowset,
							"cvalue") == null ? "" : Sql_switcher.readMemo(
							this.frowset, "cvalue");
					b = PubFunc
							.IsHasVariable(cvalue, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(cvalue,
								tempVariableChz); // 再查找中文名
					} 
					if(b) {
						// 表示该表达式引用了英文的临时变量
						//this.formHM.put("base", "其他薪资类别的临时变量表达式中引用了该临时变量，不能将其设置为私有！");
						this.formHM.put("base", "共享临时变量已被其他薪资/保险类别引用，不允许设为私有！");//zhaoxg add 2013-10-21 测试要求这么改的，所以屏蔽原来的细节判断
						return;
					}
				}
				// 接着检查检索条件表达式中是否引用
				sb.setLength(0);
				sb.append("select tabid, name,factor from template_table where tabid<>'"+templetId+"' ");
				this.frowset = dao.search(sb.toString());
				while (this.frowset.next()) {
					String factor = Sql_switcher.readMemo(this.frowset, "factor") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "factor");
					b = PubFunc.IsHasVariable(factor, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(factor,
								tempVariableChz); // 再查找中文名
					} 
					if(b) {
						// 查检索条件表达式中是否引用
						//this.formHM.put("base", "其他薪资类别的检索条件中引用了该临时变量，不能将其设置为私有！");
						this.formHM.put("base", "共享临时变量已被其他薪资/保险类别引用，不允许设为私有！");//zhaoxg add 2013-10-21 测试要求这么改的，所以屏蔽原来的细节判断
						return;
					}
				}
				// 接着检查计算公式表达式中是否引用
				sb.setLength(0);
				sb.append("select tabid,chz,formula from gzadj_formula where tabid<>'"+templetId+"' ");
				this.frowset = dao.search(sb.toString());
				while (this.frowset.next()) {
					String formula = Sql_switcher.readMemo(this.frowset, "formula") == null ? ""
							: Sql_switcher.readMemo(this.frowset, "formula");
					b = PubFunc.IsHasVariable(formula, tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc
								.IsHasVariable(formula, tempVariableChz); // 再查找中文名
					} 
					if(b) {
						// 表示该计算条件引用了英文的临时变量
						//this.formHM.put("base", "其他薪资类别的计算公式中引用了该临时变量，不能将其设置为私有！");
						this.formHM.put("base", "共享临时变量已被其他薪资/保险类别引用，不允许设为私有！");//zhaoxg add 2013-10-21 测试要求这么改的，所以屏蔽原来的细节判断
						return;
					}
				}
				// 最后判断校验公式中是否引用
				sb.setLength(0);
				sb.append("select name,formula,tabid from hrpChkformula where flag=0 and  tabid<>'"+templetId+"' ");
				this.frowset = dao.search(sb.toString());
				while (this.frowset.next()) {
					String formula = Sql_switcher.readMemo(this.frowset,
							"formula") == null ? "" : Sql_switcher.readMemo(
							this.frowset, "formula");
					b = PubFunc.IsHasVariable(formula,
							tempVariableCName); // 先查找英文名
					if (!b) {
						b = PubFunc.IsHasVariable(formula,
								tempVariableChz); // 再查找中文名
					} 
					if(b) {
						// 表示该审核公式引用了英文的临时变量
						//this.formHM.put("base", "其他薪资类别的计算公式条件中引用了该临时变量，不能将其设置为私有！");
						this.formHM.put("base", "共享临时变量已被其他薪资/保险类别引用，不允许设为私有！");//zhaoxg add 2013-10-21 测试要求这么改的，所以屏蔽原来的细节判断
						return;
					}
				}
			}
			this.formHM.put("base", "ok");
			if("3".equals(type))//人事异动 lis
				dao.update("update midvariable set cState=null where nId="+nid);
			else
				dao.update("update midvariable set cState='"+cstate+"' where nId="+nid);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
