package com.hjsj.hrms.transaction.gz.tempvar;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class CheckBeforeDelTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		String flag = (String) this.getFormHM().get("type");// 3:人事异动模块1:薪资类别 5：数据采集
		String cstatestr = (String) this.getFormHM().get("cstate");
		cstatestr = cstatestr != null && cstatestr.trim().length() > 0 ? cstatestr : "";
		String[] cstate = cstatestr.split(",");
		ContentDAO dao = new ContentDAO(this.frameconn);
		String nid = (String) hm.get("nid");
		nid = nid != null && nid.trim().length() > 0 ? nid : "";
		if (nid.length() > 0) {
			String[] arr = nid.split(",");
			if (arr.length > 0) {
				for (int i = 0; i < arr.length; i++) {// 挨个取出每一个要删除的临时变量，并进行查找
					try {
						// 首先查出临时变量名字
						String tempVariableCName = "";
						String tempVariableChz = "";
						String tempCstate = "";
						String isShare = "0";//是否共享 0：不共享 1：共享
						String templetId = "";
						StringBuffer sbquery = new StringBuffer("");
						sbquery.append("select cname,chz,cstate,templetID from midvariable where nid= "+arr[i]);
						this.frowset = dao.search(sbquery.toString());
						if (this.frowset.next()) {
							tempVariableCName = frowset.getString(1);
							tempVariableChz = frowset.getString(2);
							tempCstate = this.frowset.getString("cstate") == null ? "" : this.frowset.getString("cstate");//Sql_switcher.readMemo(this.frowset, "cstate");
							templetId = this.frowset.getString("templetID")==null?"":this.frowset.getString("templetID");
							if("1".equals(flag)){//如果是从薪资类别模块进入
								if("".equals(tempCstate)){//从薪资类别进入的时候，cstate是null才是共享
									isShare = "1";
								}
							}else if("3".equals(flag)){//如果是从人事异动模板进入
								if("1".equals(tempCstate)){//从人事异动模板进入的时候，cstate是1才是共享
									isShare = "1";
								}
							}else if("5".equals(flag)){
								if("".equals(tempCstate)){//从数据采集进入的时候，cstate是null才是共享
									isShare = "1";
								}
							}
						}
						StringBuffer sb = new StringBuffer("");
						if ("1".equals(flag)) {
							// 先检查临时变量表达式是否引用
							boolean b = false;
							sb.append("select cvalue from midvariable where nflag=0 and templetId = 0 ");
							if(!"1".equals(isShare)){//如果不是共享
								sb.append(" and cstate='"+tempCstate+"'");
							}
							this.frowset = dao.search(sb.toString());
							String cvalue = "";
							while (this.frowset.next()) {
								cvalue = Sql_switcher.readMemo(this.frowset,
										"cvalue") == null ? "" : Sql_switcher
										.readMemo(this.frowset, "cvalue");
								b = PubFunc.IsHasVariable(cvalue,
										tempVariableCName); // 先查找英文名
								if (!b) {
									b = PubFunc.IsHasVariable(cvalue,
											tempVariableChz); // 再查找中文名
								} 
								if(b) {
									// 表示该表达式引用了英文的临时变量
									//hm.put("base", "临时变量表达式中引用了临时变量("+tempVariableChz+")，不能删除！");
									hm.put("base", "该临时变量已经被引用，不允许删除!");//改的提示信息，上面的为原来的   zhaoxg 2013-10-17
									return;
								}
							}
							// 接着检查计算公式中是否引用
							sb.setLength(0);
							sb.append("select rexpr from salaryformula where salaryid<>-2 and salaryid<>-1 ");
							if(!"1".equals(isShare)){//如果不是共享
								sb.append(" and salaryid='"+tempCstate+"'");
							}
							this.frowset = dao.search(sb.toString());
							String rexpr = "";
							while (this.frowset.next()) {
								rexpr = Sql_switcher.readMemo(
										this.frowset, "rexpr") == null ? ""
										: Sql_switcher.readMemo(this.frowset,
												"rexpr");
								b = PubFunc.IsHasVariable(rexpr,
										tempVariableCName); // 先查找英文名
								if (!b) {
									b = PubFunc.IsHasVariable(rexpr,
											tempVariableChz); // 再查找中文名
								} 
								if(b) {
									// 表示该计算公式引用了英文的临时变量
									//hm.put("base", "计算公式中引用了临时变量("+tempVariableChz+")，不能删除！");
									hm.put("base", "该临时变量已经被引用，不允许删除!");//改的提示信息，上面的为原来的   zhaoxg 2013-10-17
									return;
								}
							}
							// 接着检查计算公式表达式中是否引用
							sb.setLength(0);
							sb.append("select cond from salaryformula where salaryid<>-2 and salaryid<>-1 ");
							if(!"1".equals(isShare)){//如果不是共享
								sb.append(" and salaryid='"+tempCstate+"'");
							}
							this.frowset = dao.search(sb.toString());
							while (this.frowset.next()) {
								String cond = Sql_switcher.readMemo(
										this.frowset, "cond") == null ? ""
										: Sql_switcher.readMemo(this.frowset,
												"cond");
								b = PubFunc.IsHasVariable(cond,
										tempVariableCName); // 先查找英文名
								if (!b) {
									b = PubFunc.IsHasVariable(cond,
											tempVariableChz); // 再查找中文名
								} 
								if(b) {
									// 表示该计算条件引用了英文的临时变量
									//hm.put("base", "计算公式条件中引用了临时变量("+tempVariableChz+")，不能删除！");
									hm.put("base", "该临时变量已经被引用，不允许删除!");//改的提示信息，上面的为原来的   zhaoxg 2013-10-17
									return;
								}
							}
							// 最后判断审核公式中是否引用
							sb.setLength(0);
							sb.append("select formula from hrpchkformula");
							if(!"1".equals(isShare)){//如果不是共享
								sb.append(" where chkid='"+tempCstate+"'");
							}
							this.frowset = dao.search(sb.toString());
							while (this.frowset.next()) {
								String formula = Sql_switcher.readMemo(
										this.frowset, "formula") == null ? ""
										: Sql_switcher.readMemo(this.frowset,
												"formula");
								b = PubFunc.IsHasVariable(formula,
										tempVariableCName); // 先查找英文名
								if (!b) {
									b = PubFunc.IsHasVariable(formula,
											tempVariableChz); // 再查找中文名
								} 
								if(b) {
									// 表示该审核公式引用了英文的临时变量
									//hm.put("base", "审核公式中引用了临时变量("+tempVariableChz+")，不能删除！");
									hm.put("base", "该临时变量已经被引用，不允许删除!");//改的提示信息，上面的为原来的   zhaoxg 2013-10-17
									return;
								}
							}
						} else if ("3".equals(flag)) {
							// 先检查临时变量表达式是否引用
							boolean b = false;
							if(!"1".equals(isShare)){//如果不是共享
								sb.append("select cvalue from midvariable where nflag=0 and templetId = '"+templetId+"' ");
							}else{
							    sb.append("select cvalue from midvariable where nflag=0 and templetId <>0 ");
							}
							this.frowset = dao.search(sb.toString());
							String cvalue = "";
							while (this.frowset.next()) {
								cvalue = Sql_switcher.readMemo(this.frowset,
										"cvalue") == null ? "" : Sql_switcher
										.readMemo(this.frowset, "cvalue");
								b = PubFunc.IsHasVariable(cvalue,
										tempVariableCName); // 先查找英文名
								if (!b) {
									b = PubFunc.IsHasVariable(cvalue,
											tempVariableChz); // 再查找中文名
								} 
								if(b) {
									// 表示该临时表达式引用了临时变量
									//hm.put("base", "临时变量表达式中引用了临时变量("+tempVariableChz+")，不能删除！");
									hm.put("base", "该临时变量已经被引用，不允许删除!");//改的提示信息，上面的为原来的   zhaoxg 2013-10-17
									return;
								}
							}
							//接着检查计算公式表达式是否引用
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
			                    b = PubFunc.IsHasVariable(formula, tempVariableCName); // 先查找英文名
			                    if (!b) {
			                        b = PubFunc.IsHasVariable(formula, tempVariableChz); // 再查找中文名
			                    }
			                    if(b) {
                                    // 表示计算公式表达式引用了英文的临时变量
                                    //hm.put("base", "临时变量表达式中引用了临时变量("+tempVariableChz+")，不能删除！");
                                    hm.put("base", "该临时变量已经被引用，不允许删除!");//改的提示信息，上面的为原来的   zhaoxg 2013-10-17
                                    return;
                                }
			                }
							// 接着检查计算公式 计算条件中是否引用
			                sb.setLength(0);
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
			                    b = PubFunc.IsHasVariable(cfactor, tempVariableCName); // 先查找英文名
			                    if (!b) {
			                        b = PubFunc.IsHasVariable(cfactor, tempVariableChz); // 再查找中文名
			                    }
			                    if(b) {
                                    // 表示该计算条件表达式引用了英文的临时变量
                                    //hm.put("base", "检索条件中引用了临时变量("+tempVariableChz+")，不能删除！");
                                    hm.put("base", "该临时变量已经被引用，不允许删除!");//改的提示信息，上面的为原来的   zhaoxg 2013-10-17
                                    return;
                                }
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
			                    b = PubFunc.IsHasVariable(factor,tempVariableCName); // 先查找英文名
			                    if (!b) {
			                        b = PubFunc.IsHasVariable(factor,tempVariableChz); // 再查找中文名
			                    } 
			                    if(b) {
                                    // 表示该检索条件引用了临时变量
                                    //hm.put("base", "检索条件中引用了临时变量("+tempVariableChz+")，不能删除！");
                                    hm.put("base", "该临时变量已经被引用，不允许删除!");//改的提示信息，上面的为原来的   zhaoxg 2013-10-17
                                    return;
                                }
			                }
			             // 最后判断校验公式中是否引用
			                sb.setLength(0);
			                if(!"1".equals(isShare)){//以果不是共享
			                    sb.append("select name,formula,tabid from hrpChkformula where flag=0 and tabid="+templetId+"");
			                }else{
			                    sb.append("select name,formula,tabid from hrpChkformula where flag=0");
			                }
			                this.frowset = dao.search(sb.toString());
			                while (this.frowset.next()) {
			                    String formula = Sql_switcher.readMemo(this.frowset,
			                            "formula") == null ? "" : Sql_switcher.readMemo(
			                            this.frowset, "formula");
			                    b = PubFunc.IsHasVariable(formula, tempVariableCName); // 先查找英文名
			                    if (!b) {
			                        b = PubFunc.IsHasVariable(formula, tempVariableChz); // 再查找中文名
			                    }
			                    if(b) {
                                    // 表示该校验公式引用了临时变量
                                    //hm.put("base", "检索条件中引用了临时变量("+tempVariableChz+")，不能删除！");
                                    hm.put("base", "该临时变量已经被引用，不允许删除!");//改的提示信息，上面的为原来的   zhaoxg 2013-10-17
                                    return;
                                }
			                }
						}else if("5".equals(flag)){
							// 先检查临时变量表达式是否引用
							boolean b = false;
							sb.append("select cvalue from midvariable where nflag=5 and templetId = '0' ");
							if(!"1".equals(isShare)){//如果不是共享
								sb.append("  and cstate='"+tempCstate+"'");
							}
							this.frowset = dao.search(sb.toString());
							String cvalue = "";
							while (this.frowset.next()) {
								cvalue = Sql_switcher.readMemo(this.frowset,
										"cvalue") == null ? "" : Sql_switcher
										.readMemo(this.frowset, "cvalue");
								b = PubFunc.IsHasVariable(cvalue,
										tempVariableCName); // 先查找英文名
								if (!b) {
									b = PubFunc.IsHasVariable(cvalue,
											tempVariableChz); // 再查找中文名
								} 
								if(b) {
									// 表示该表达式引用了英文的临时变量
									//hm.put("base", "临时变量表达式中引用了临时变量("+tempVariableChz+")，不能删除！");
									hm.put("base", "该临时变量已经被引用，不允许删除!");//改的提示信息，上面的为原来的   zhaoxg 2013-10-17
									return;
								}
							}
							// 接着检查计算公式中是否引用
							sb.setLength(0);
							sb.append("select rexpr from salaryformula where salaryid=-2");
							if(!"1".equals(isShare)){//如果不是共享
								sb.append(" and cstate='"+tempCstate+"'");
							}
							this.frowset = dao.search(sb.toString());
							String rexpr = "";
							while (this.frowset.next()) {
								rexpr = Sql_switcher.readMemo(
										this.frowset, "rexpr") == null ? ""
										: Sql_switcher.readMemo(this.frowset,
												"rexpr");
								b = PubFunc.IsHasVariable(rexpr,
										tempVariableCName); // 先查找英文名
								if (!b) {
									b = PubFunc.IsHasVariable(rexpr,
											tempVariableChz); // 再查找中文名
								} 
								if(b) {
									// 表示该计算公式引用了英文的临时变量
									//hm.put("base", "计算公式中引用了临时变量("+tempVariableChz+")，不能删除！");
									hm.put("base", "该临时变量已经被引用，不允许删除!");//改的提示信息，上面的为原来的   zhaoxg 2013-10-17
									return;
								}
							}
							// 接着检查计算公式表达式中是否引用
							sb.setLength(0);
							sb.append("select cond from salaryformula where salaryid=-2 ");
							if(!"1".equals(isShare)){//如果不是共享
								sb.append(" and cstate='"+tempCstate+"'");
							}
							this.frowset = dao.search(sb.toString());
							while (this.frowset.next()) {
								String cond = Sql_switcher.readMemo(
										this.frowset, "cond") == null ? ""
										: Sql_switcher.readMemo(this.frowset,
												"cond");
								b = PubFunc.IsHasVariable(cond,
										tempVariableCName); // 先查找英文名
								if (!b) {
									b = PubFunc.IsHasVariable(cond,
											tempVariableChz); // 再查找中文名
								} 
								if(b) {
									// 表示该计算条件引用了英文的临时变量
									//hm.put("base", "计算公式条件中引用了临时变量("+tempVariableChz+")，不能删除！");
									hm.put("base", "该临时变量已经被引用，不允许删除!");//改的提示信息，上面的为原来的   zhaoxg 2013-10-17
									return;
								}
							}
							
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
				}
			}
		}
		hm.put("base", "ok");
		hm.put("nid", nid);
	}

}
