package com.hjsj.hrms.businessobject.kq.query;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.SqlDifference;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.math.NumberUtils;

import java.util.ArrayList;
import java.util.Iterator;

public class CodingAnalytical {

	public ArrayList getFields(ArrayList item,String table){
		CommonData tempField = new CommonData();
		ArrayList initField = new ArrayList();
		for(Iterator it = item.iterator();it.hasNext();){
			FieldItem fitem = (FieldItem)it.next();
			if (!fitem.isVisible()) 
			{
				continue;
			}
			String setid = fitem.getFieldsetid();
			setid = (setid == null || "".equals(setid))? table:setid;
			tempField = new CommonData(fitem.getItemid() +":" + fitem.getItemtype() + ":" + fitem.getCodesetid() + ":"  + setid,fitem.getItemdesc());
			initField.add(tempField);
		}
		return initField;
	}
	
	public String analytical(String search) {

		StringBuffer wherestr = new StringBuffer();

		if (search != null && search.trim().length() > 0) {
			search = SafeCode.decode(search);
			String searcharr[] = search.split("::");
			if (searcharr.length == 3) {
				wherestr.append(" (");
				String sexpr = searcharr[0];
				String sfactor = searcharr[1];//查询条件串
				sexpr = PubFunc.keyWord_reback(sexpr);
				sfactor = PubFunc.keyWord_reback(sfactor);
				boolean blike = false;
				blike = searcharr[2] != null && "1".equals(searcharr[2]) ? true
						: false;
				String strSFACTOR = sfactor;
				sfactor = "";
				String strItem[] = strSFACTOR.split("`");
				String xpr = "";
				for (int i = 0; i < strItem.length; i++) {
					String item[] = strItem[i].split(":");
					String itemid = item[0];
					String itemType = item[1];
					String setid = item[2];
					String code = item[3];
					String itemVale = "";
					
					if(item.length > 4){
						itemVale = item[4].trim();
					}
					String itemValu[] = itemVale.split(" ");
					itemVale = itemValu[0];
					itemVale = itemVale.replaceAll("'", "''");
					if("UN".equals(setid) || "UM".equals(setid) || "@K".equals(setid)){
						if(itemVale.indexOf("$") == 0){
							itemVale = itemVale.substring(1);
							if(itemVale.indexOf("?") != -1){
								itemVale = itemVale.replaceAll("\\?", "_");
							}
							
							if(itemVale.indexOf("*") != -1){
								itemVale = itemVale.replaceAll("\\*", "%");
							}
							
							if ("<>".equalsIgnoreCase(code)	&& !"".equals(itemVale))// zwl 查询 不等于空时候
                            {
                                wherestr.append(xpr + itemid + " NOT IN(SELECT codeitemid FROM organization where codeitemdesc like '" + itemVale + "' AND codesetid='" + setid + "')");
                            } else
							{
								if (blike) //模糊查询
                                {
                                    wherestr.append(xpr + itemid + " IN(SELECT codeitemid FROM organization where codeitemdesc like '%" + itemVale + "%' AND codesetid='" + setid + "')");
                                } else {
                                    wherestr.append(xpr + itemid + " IN(SELECT codeitemid FROM organization where codeitemdesc = '" + itemVale + "' AND codesetid='" + setid + "')");
                                }
							}
						} else {
						    //zxj 20180521 jazz 37273 模糊查询下 等于空、不等于空处理优化
							if ("<>".equalsIgnoreCase(code)) {
							    if (!"".equals(itemVale))// zwl 查询 不等于空时候
                                {
                                    wherestr.append(xpr + itemid + Sql_switcher.isnull(itemid, "'#'") + " not like '" + itemVale + "%'");
                                } else {
                                    wherestr.append(xpr + itemid + " is not null and " + itemid + "<>''");
                                }
							} else {
							    if (!"".equals(itemVale)) {
                                    wherestr.append(xpr + itemid + " like '" + itemVale + "%'");
                                } else {
							        if (!blike) {
							            wherestr.append(xpr + "(" + itemid + " is null or " + itemid + "='')");
                                    } else {
                                        wherestr.append(xpr + "1=1");
                                    }
							    }
							}
						}
					}else if("A".equals(itemType)){
						if(itemVale.indexOf("$") == 0) {
                            itemVale = itemVale.substring(1);
                        }
						
						if (blike) {
							if("".equals(itemVale)){
							    //zxj 20180521 jazz 37273 模糊查询下 不等于空相当与不等于所有，等于空相当与等于所有
								if ("<>".equalsIgnoreCase(code))// zwl 查询 不等于空时候
                                {
                                    wherestr.append(xpr + "1=2");
                                } else {
                                    wherestr.append(xpr + "1=1");
                                }
							}else{
								if ("<>".equalsIgnoreCase(code))// zwl 查询 不等于空时候
                                {
                                    wherestr.append(xpr + Sql_switcher.isnull(itemid, "'#'") + " not like '%" + itemVale + "%'");
                                } else {
                                    wherestr.append(xpr + itemid + " like '%" + itemVale + "%'");
                                }
							}
						}else{
							if("".equals(itemVale)){
								if ("<>".equalsIgnoreCase(code))// zwl 查询 不等于空时候
                                {
                                    wherestr.append(xpr + SqlDifference.isNotNull(itemid));
                                } else {
                                    wherestr.append(xpr + SqlDifference.isNull(itemid));
                                }
							} else if(itemVale.indexOf("%") != -1 || itemVale.indexOf("?") != -1 || itemVale.indexOf("_") != -1 || itemVale.indexOf("*") != -1){
								if(itemVale.indexOf("?") != -1){
									itemVale = itemVale.replaceAll("\\?", "_");
								}
								
								if(itemVale.indexOf("*") != -1){
									itemVale = itemVale.replaceAll("\\*", "%");
								}
								
								if ("<>".equalsIgnoreCase(code))// zwl 查询 不等于空时候
                                {
                                    wherestr.append(xpr + Sql_switcher.isnull(itemid, "'#'") + " NOT LIKE '" + itemVale.trim() + "'");
                                } else {
                                    wherestr.append(xpr + itemid + " LIKE '" + itemVale.trim() + "'");
                                }
							} else {
								if ("<>".equalsIgnoreCase(code))// zwl 查询 不等于空时候
                                {
                                    wherestr.append(xpr + Sql_switcher.isnull(itemid, "'#'") + code + "'" + itemVale.trim() + "'");
                                } else {
                                    wherestr.append(xpr + itemid + code + "'" + itemVale.trim() + "'");
                                }
							}
						}
					} else if("N".equals(itemType)){
						itemVale = itemVale.trim() == null || itemVale.trim().length() < 1 ? "0" : itemVale.trim();
						
						//zxj changed 2013.07.11 增加处理不是数字的情况
						if (NumberUtils.isNumber(itemVale)) {
                            wherestr.append(xpr + Sql_switcher.isnull(itemid,"0") + code + itemVale);
                        } else {
						    //如果查等于某值，因为不是数字，那么就认为没有等于这个值的； 
						    if ("=".equals(code)) {
                                wherestr.append(xpr + "1=2");
                            } else //如果查不等于的，那么数字肯定都不等于这个错误的值
                            {
                                wherestr.append(xpr + "1=1");
                            }
                        }
					}else if("D".equals(itemType)){
						wherestr.append(xpr	+ this.getDataValue(itemid, code,itemVale.trim()));
					}
					int temp = sexpr.indexOf((i + 1) + "") + String.valueOf(i + 1).length();// 下一个的位数
					if (sexpr.substring(sexpr.indexOf((i + 1) + "")) != null
							&& sexpr.substring(temp).length() > 0) {
						xpr = sexpr.substring(temp, temp + 1);
						if ("+".equals(xpr)) {
                            xpr = " OR ";
                        } else if ("*".equals(xpr)) {
                            xpr = " AND ";
                        }
					} else {
                        xpr = "";
                    }
				}
				wherestr.append(")");
			}
		}
		
		if (wherestr.toString().trim().length()<3) {
            return " 1=2";
        }
		
		return wherestr.toString();
	}

	public String getDataValue(String fielditemid, String operate, String value) {

		StringBuffer a_value = new StringBuffer();
		if (value != null && value.length() > 0) {
			String[] tempvalue = value.replaceAll("\\.", "-").split("-");
			String year = "";
			String month = "";
			String day = "";
			for(int i = 0;i<tempvalue.length;i++){
				if(i==0){
					if(tempvalue[i].indexOf("?") != -1){
					} else if(tempvalue[i].length() == 4){
						year = tempvalue[i];
					}else{
						return "";
					}
				}else if(i == 1){
					if(tempvalue[i].indexOf("?") != -1){
						
					}else if (tempvalue[i].length() <= 2){
						if (tempvalue[i].length() == 1) {
							month = "0" + tempvalue[i];
						}else {
							month = tempvalue[i];
						}
					}else {
						return "";
					}
				} else if(i == 2){
					if(tempvalue[i].indexOf("?") != -1){
						
					}else if (tempvalue[i].length() <= 2){
						if (tempvalue[i].length() == 1) {
							day = "0" + tempvalue[i];
						}else if(tempvalue[i].length() == 2){
							day = tempvalue[i];
						}
					} else {
						return "";
					}
				}
			}
			/* 防止出现 都是 ? 好的情况 */
			if("".equals(year) && "".equals(month) && "".equals(day)){
				return "";
			}
			try {
				a_value.append("(");
				if ("=".equals(operate)) {
					if(!"".equals(year)){
						a_value.append(Sql_switcher.year(fielditemid) + operate
							+ year + " AND ");
					}
					if(!"".equals(month)){
						a_value.append(Sql_switcher.month(fielditemid) + operate
							+ month + " AND ");
					}
					if(!"".equals(day)){
						a_value.append(Sql_switcher.day(fielditemid) + operate
							+ day);
					} else {
						a_value.setLength(a_value.length() - 4);
					}
				} else if(operate.indexOf("=") != -1){
					a_value.append("(");
					if(!"".equals(year)){
						a_value.append(Sql_switcher.year(fielditemid) + "="
							+ year + " AND ");
					}
					if(!"".equals(month)){
						a_value.append(Sql_switcher.month(fielditemid) + "="
							+ month + " AND ");
					}
					if(!"".equals(day)){
						a_value.append(Sql_switcher.day(fielditemid) + "="
							+ day);
					} else {
						a_value.setLength(a_value.length() - 4);
					}
					a_value.append(") OR (");
					operate = operate.replaceAll("=", "");
					if(!"".equals(year)){
						a_value.append("(" + Sql_switcher.year(fielditemid) + operate + year + ") OR ");
					}
					if(!"".equals(month)){
						a_value.append("(");
						if(!"".equals(year)){
							a_value.append("(" + Sql_switcher.year(fielditemid) + "=" + year + ") AND ");
						}
						a_value.append(Sql_switcher.month(fielditemid) + operate + month);
						a_value.append(") OR ");
					}
					if(!"".equals(day)){
						a_value.append("(");
						if(!"".equals(year)){
							a_value.append("(" + Sql_switcher.year(fielditemid) + "=" + year + ") AND ");
						}
						if(!"".equals(month)){
							a_value.append("(" + Sql_switcher.month(fielditemid) + "=" + month + ") AND ");
						}
						a_value.append(Sql_switcher.day(fielditemid) + operate + day);
						a_value.append(")");
					} else {
						a_value.setLength(a_value.length() - 4);
					}
					a_value.append(")");
				}else{
					if(!"".equals(year)){
						a_value.append("(" + Sql_switcher.year(fielditemid) + operate + year + ") OR ");
					}
					if(!"".equals(month)){
						a_value.append("(");
						if(!"".equals(year)){
							a_value.append("(" + Sql_switcher.year(fielditemid) + "=" + year + ") AND ");
						}
						a_value.append(Sql_switcher.month(fielditemid) + operate + month);
						a_value.append(") OR ");
					}
					if(!"".equals(day)){
						a_value.append("(");
						if(!"".equals(year)){
							a_value.append("(" + Sql_switcher.year(fielditemid) + "=" + year + ") AND ");
						}
						if(!"".equals(month)){
							a_value.append("(" + Sql_switcher.month(fielditemid) + "=" + month + ") AND ");
						}
						a_value.append(Sql_switcher.day(fielditemid) + operate + day);
						a_value.append(")");
					} else {
						a_value.setLength(a_value.length() - 4);
					}
				}
				a_value.append(")");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			a_value.append("(" + fielditemid + operate + "NULL");
			a_value.append(" or " + fielditemid + operate + "'')");
		}
		return a_value.toString();
	}
}
