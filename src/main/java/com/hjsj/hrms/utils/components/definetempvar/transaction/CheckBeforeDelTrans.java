package com.hjsj.hrms.utils.components.definetempvar.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 项目名称 ：ehr7.x
 * 类名称：CheckBeforeDelTrans
 * 类描述：删除临时变量前校验，被其他临时变量或计算公式使用的则不能删除
 * 创建人： lis
 * 创建时间：2015-10-29
 */
public class CheckBeforeDelTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		String type = (String) this.getFormHM().get("type");// 1:薪资类别,3:人事异动模块, 5：数据采集
		ContentDAO dao = new ContentDAO(this.frameconn);
		String nids = (String) this.getFormHM().get("nids");//要删除的临时变量id串，用“,”分割
		nids = nids != null && nids.trim().length() > 0 ? nids : "";
		if (nids.length() > 0) {
			String[] arr = nids.split(",");//临时变量id数组
			if (arr.length > 0) {
				for (int i = 0; i < arr.length; i++) {// 挨个取出每一个要删除的临时变量，并进行查找
					try {
						if(StringUtils.isBlank(arr[i]))
							continue;
						
						// 首先查出临时变量名字
						String tempVariableCName = "";//临时变量英文名称
						String tempVariableChz = "";//临时变量中文名称
						String tempCstate = "";//临时变量是共享，则是null，否则是当前薪资类别id
						String templetId = "";//人事异动模版id，gaohy
						String isShare = "0";//是否共享 0：不共享 1：共享
						
						StringBuffer sbquery = new StringBuffer("");
						sbquery.append("select cname,chz,cstate,templetID from midvariable where nid=?");
						this.frowset = dao.search(sbquery.toString(),Arrays.asList(arr[i]));
						if (this.frowset.next()) {
							tempVariableCName = frowset.getString(1);
							tempVariableChz = frowset.getString(2);
							tempCstate = this.frowset.getString("cstate") == null ? "" : this.frowset.getString("cstate");//Sql_switcher.readMemo(this.frowset, "cstate");
							templetId = this.frowset.getString("templetID")==null?"":this.frowset.getString("templetID");
							if("1".equals(type) || "5".equals(type)){//如果是从薪资类别模块进入,人事异动也是为null时是共享，gaohy,2016-1-7
								if(StringUtils.isBlank(tempCstate)){//从薪资类别进入的时候，cstate是null才是共享
									isShare = "1";
								}
							}else if("3".equals(type)){
								if("1".equals(tempCstate)){//从人事异动模板进入的时候，cstate是1才是共享 lis
									isShare = "1";
								}
							}
						}
						StringBuffer sb = new StringBuffer("");
						if ("1".equals(type)) {
							// 先检查临时变量表达式是否引用当前临时变量
							boolean b = false;
							sb.append("select cvalue from midvariable where nflag=0 and templetId = 0 ");
							if(!"1".equals(isShare)){//如果不是共享
								sb.append(" and cstate='"+tempCstate+"'");
							}
							this.frowset = dao.search(sb.toString());
							String cvalue = "";//公式
							while (this.frowset.next()) {
								cvalue = Sql_switcher.readMemo(this.frowset,
										"cvalue") == null ? "" : Sql_switcher
										.readMemo(this.frowset, "cvalue");
								
								//判断公式中是否用到了要改名的临时变量
								b = PubFunc.IsHasVariable(cvalue,tempVariableCName); // 先查找英文名
								if (!b) {
									b = PubFunc.IsHasVariable(cvalue,
											tempVariableChz); // 再查找中文名
								} 
								if(b) {
									// 表示该表达式引用了英文的临时变量
									hm.put("base", ResourceFactory.getProperty("gz_new.gz_hasUseByOtherVar"));//该临时变量已经被其他临时变量引用，不允许删除
									return;
								}
							}
							// 接着检查计算公式中是否引用了当前临时变量
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
									hm.put("base", ResourceFactory.getProperty("gz_new.gz_hasUseByOtherFomula"));//该临时变量已经被计算公式表达式引用，不允许删除
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
									hm.put("base", ResourceFactory.getProperty("gz_new.gz_hasUseByOtherFomulaCond"));//该临时变量已经被计算公式中的计算条件表达式引用，不允许删除
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
									hm.put("base", ResourceFactory.getProperty("gz_new.gz_hasUseByOtherSpFomula"));//该临时变量已经被审核公式引用，不允许删除
									return;
								}
							}
						}else if("3".equals(type)) {
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
			                if(!b){//bug 47853
		                    	b=checkTemplateSetIsHaveTempVar(templetId, tempVariableCName);
		                    }
			                if(b) {
                                // 表示该校验公式引用了临时变量
                                //hm.put("base", "检索条件中引用了临时变量("+tempVariableChz+")，不能删除！");
                                hm.put("base", "该临时变量已经被引用，不允许删除!");//改的提示信息，上面的为原来的   zhaoxg 2013-10-17
                                return;
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
	}
	
	private Boolean checkTemplateSetIsHaveTempVar(String tabid,String cname){
		Boolean isHave=false;
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try{
			String sql="select Hz,field_name from template_set where flag='V' and field_name=?";
			ArrayList list=new ArrayList();
			list.add(cname);
			rowSet=dao.search(sql,list);
			if(rowSet.next()){
				isHave=true;
			}
		
		}catch(Exception ex){
			ex.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return isHave;
	}
}
