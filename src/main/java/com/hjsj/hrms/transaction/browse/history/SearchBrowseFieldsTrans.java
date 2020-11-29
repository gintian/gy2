/**
 * 
 */
package com.hjsj.hrms.transaction.browse.history;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Owner
 * 
 */
public class SearchBrowseFieldsTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String userbase = (String) this.getFormHM().get("userbase");
		ArrayList dbaselist = userView.getPrivDbList();
		if (dbaselist == null || dbaselist.size() <= 0) {
			this.getFormHM().put("strsql", "");
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					"没有授权人员库！", "", ""));
		}
		ArrayList browsefields = (ArrayList)this.getFormHM().get("browsefields");
		if(browsefields.size()==0){
			this.getFormHM().put("strsql", "");
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					"没有授权指标信息！", "", ""));
		}
		String dbstring=dbaselist.toString().toUpperCase();
		if(dbstring.indexOf(userbase.toUpperCase())==-1){
			userbase=(String)dbaselist.get(0);
			
		}
		String strwhere = "";
		StringBuffer wheresql = new StringBuffer();
		String code = "";
		code = (String) this.getFormHM().get("code");
		String coe = userView.getManagePrivCodeValue();                  
		if(code!=null && code.length()>0){
			if(coe!=null && coe.length()>0){
				if(code.indexOf(coe)>=0){
					
				}else if(coe.indexOf(code)>=0){
					code=coe;
				}else{
					code="1|";
				}					
			}
							
		}
		String kind = "";
		kind = (String) this.getFormHM().get("kind");
		kind = kind == null || kind.length() == 0 ? "2" : kind;
		String orglike = (String) this.getFormHM().get("orglike");
		String backdate = (String) this.getFormHM().get("backdate");
		String ifbackup = (String) this.getFormHM().get("ifbackup");
		if ("0".equals(ifbackup)) {
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					"您还没有快照的人员历史信息！", "", ""));
		}

		StringBuffer orderby = new StringBuffer();
		orderby.append(" order by a0000");
		if (code.length() == 0) {
			if (!userView.isSuper_admin()) {
				String expr = "1";
				String factor = "";
				if ("UN".equals(userView.getManagePrivCode())) {
					factor = "B0110=";
					kind = "2";
					if (userView.getManagePrivCodeValue() != null
							&& userView.getManagePrivCodeValue().length() > 0) {
						factor += userView.getManagePrivCodeValue();
						factor += "%`";
					} else {
						factor += "%`B0110=`";
						expr = "1+2";
					}
				} else if ("UM".equals(userView.getManagePrivCode())) {
					factor = "E0122=";
					kind = "1";
					if (userView.getManagePrivCodeValue() != null
							&& userView.getManagePrivCodeValue().length() > 0) {
						factor += userView.getManagePrivCodeValue();
						factor += "%`";
					} else {
						factor += "%`E0122=`";
						expr = "1+2";
					}
				} else if ("@K".equals(userView.getManagePrivCode())) {
					factor = "E01A1=";
					kind = "0";
					if (userView.getManagePrivCodeValue() != null
							&& userView.getManagePrivCodeValue().length() > 0) {
						factor += userView.getManagePrivCodeValue();
						factor += "%`";
					} else {
						factor += "%`E01A1=`";
						expr = "1+2";
					}
				} else {
					expr = "1+2";
					factor = "B0110=";
					kind = "2";
					if (userView.getManagePrivCodeValue() != null
							&& userView.getManagePrivCodeValue().length() > 0)
						factor += userView.getManagePrivCodeValue();
					factor += "%`B0110=`";
				}
				ArrayList fieldlist = new ArrayList();
				try {

					/** 表过式分析 */
					/** 非超级用户且对人员库进行查询 */
					StringBuffer sql = new StringBuffer();
					sql.append("from hr_emp_hisdata heh,hr_hisdata_list hhl");
					sql.append(" where heh.id=hhl.id");
					wheresql.append(" and UPPER(nbase)='" + userbase.toUpperCase() + "'");
					sql.append(" and hhl.create_date=" + Sql_switcher.dateValue(backdate));
					sql.append(getPrivWhr(userbase, expr + "|" + factor, backdate));
					
					strwhere = sql.toString();
					//System.out.println(strwhere);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				wheresql
						.append(" from hr_emp_hisdata heh,hr_hisdata_list hhl where heh.id=hhl.id ");
				wheresql.append(" and UPPER(nbase)='" + userbase.toUpperCase() + "'");
				wheresql.append(" and hhl.create_date="
						+ Sql_switcher.dateValue(backdate));
				strwhere = wheresql.toString();
				///System.out.println(strwhere);
			}
		} else {
			wheresql.append(" from hr_emp_hisdata heh,hr_hisdata_list hhl where heh.id=hhl.id ");
			if(userView.getManagePrivCode()!=null && userView.getManagePrivCode().length()>0){
				if ("UN".equals(userView.getManagePrivCode())) {
					if("1".equals(kind)){
						if ("1".equals(orglike)) {
							wheresql.append(" and e0122 like '");
							//	String priv = this.userView.getHighPrivExpression();
							wheresql.append(code);
							wheresql.append("%'");
						} else {
							wheresql.append(" and e0122 = '");
							wheresql.append(code);
							wheresql.append("'");
						}
					}else if("0".equals(kind)){
						if ("1".equals(orglike)) {
							wheresql.append(" and e01a1 like '");
							wheresql.append(code);
							wheresql.append("%'");
						} else {
							wheresql.append(" and e01a1 = '");
							wheresql.append(code);
							wheresql.append("'");
						}
					}else{						
						if ("1".equals(orglike)) {
							wheresql.append(" and b0110 like '");
							wheresql.append(code);
							wheresql.append("%'");
						} else {
							wheresql.append(" and b0110 = '");
							wheresql.append(code);
							wheresql.append("'");
						}
					}
				} else if ("UM".equals(userView.getManagePrivCode())) {
					if("0".equals(kind)){
						if ("1".equals(orglike)) {
							wheresql.append(" and e01a1 like '");
							wheresql.append(code);
							wheresql.append("%'");
						} else {
							wheresql.append(" and e01a1 = '");
							wheresql.append(code);
							wheresql.append("'");
						}
					}else{						
						if ("1".equals(orglike)) {
							wheresql.append(" and e0122 like '");
							//	String priv = this.userView.getHighPrivExpression();
							wheresql.append(code);
							wheresql.append("%'");
						} else {
							wheresql.append(" and e0122 = '");
							wheresql.append(code);
							wheresql.append("'");
						}
					}
					
				} else if ("@K".equals(userView.getManagePrivCode())) {
					if ("1".equals(orglike)) {
						wheresql.append(" and e01a1 like '");
						wheresql.append(code);
						wheresql.append("%'");
					} else {
						wheresql.append(" and e01a1 = '");
						wheresql.append(code);
						wheresql.append("'");
					}
				}
			}else{
				
				if ("2".equals(kind)) {
					if ("1".equals(orglike)) {
						wheresql.append(" and b0110 like '");
						wheresql.append(code);
						wheresql.append("%'");
					} else {
						wheresql.append(" and b0110 = '");
						wheresql.append(code);
						wheresql.append("'");
					}
				} else if ("1".equals(kind)) {
					if ("1".equals(orglike)) {
						wheresql.append(" and e0122 like '");
						//	String priv = this.userView.getHighPrivExpression();
						wheresql.append(code);
						wheresql.append("%'");
					} else {
						wheresql.append(" and e0122 = '");
						wheresql.append(code);
						wheresql.append("'");
					}
					
				} else if ("0".equals(kind)) {
					if ("1".equals(orglike)) {
						wheresql.append(" and e01a1 like '");
						wheresql.append(code);
						wheresql.append("%'");
					} else {
						wheresql.append(" and e01a1 = '");
						wheresql.append(code);
						wheresql.append("'");
					}
				}
			}

			wheresql.append(" and UPPER(nbase)='" + userbase.toUpperCase() + "'");
			wheresql.append(" and hhl.create_date=" + Sql_switcher.dateValue(backdate));
			wheresql.append(getPrivWhr(userbase, "", backdate));
			strwhere = wheresql.toString();
		}

		String query = (String) hm.get("query");
		this.getFormHM().put("query", "0");
		if (query != null && "1".equals(query)) {
			StringBuffer querysql = new StringBuffer();
			ArrayList querylist = (ArrayList) this.getFormHM().get("queryfieldlist");
			rebackKeyword(querylist);
			String querylike = (String) this.getFormHM().get("querylike"); // 模糊查询
			this.getFormHM().put("querylike", "");
			String select_name = (String) this.getFormHM().get("select_name"); //姓名
			if(select_name!=null&&select_name.trim().length()>0){
				select_name = PubFunc.hireKeyWord_filter_reback(select_name);
				if("1".equals(querylike) || select_name.endsWith("*")){
					if(select_name.endsWith("*"))
						select_name = select_name.substring(0, select_name.length() - 1);
					
					querysql.append(" and a0101 like '%"+select_name+"%'");
				}else{
					querysql.append(" and a0101 = '"+select_name+"'");
				}
			}
			int size = querylist.size();
			for (int i = 0; i < size; i++) {
				FieldItem item = (FieldItem) querylist.get(i);
				String itemtype = item.getItemtype();
				String itemid = item.getItemid();
				/** 如果值未填的话，default是否为不查 */
				if ((item.getValue() == null || "".equals(item.getValue()))&& (!"D".equals(item.getItemtype())))
					continue;
				if (((item.getValue() == null || "".equals(item.getValue()))
				        && (item.getViewvalue() == null || "".equals(item.getViewvalue())))
						&& ("D".equals(item.getItemtype())))
					continue;
				String value = item.getValue();
				String viewvalue = item.getViewvalue();
				if ("A".equals(itemtype)) {
					if (item.getCodesetid() == null || "0".equals(item.getCodesetid())) {
						if ("1".equals(querylike)) {
							querysql.append(" and " + itemid + " like '%" + value + "%'");
						} else {
							querysql.append(" and " + itemid + "='" + value + "'");
						}
					} else {
					    String codesetid = item.getCodesetid();
						if(value!=null&&value.length()>0){
						    querysql.append(" and " + itemid + " in (");
						    querysql.append(getCodesetSql(codesetid, value, querylike));
						    querysql.append(")");
						}
					}

				} else if ("D".equals(itemtype)) {
					String s_str_date = value;
					String e_str_date = viewvalue;
					s_str_date = s_str_date.replaceAll("\\.", "-");
					e_str_date = e_str_date.replaceAll("\\.", "-");
					Date s_date = null;
					Date e_date = null;
					SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
					try {
						if (s_str_date.length() > 0)
							s_date = simple.parse(s_str_date);
						if (e_str_date.length() > 0)
							e_date = simple.parse(e_str_date);
					} catch (ParseException pex) {
						throw new GeneralException("输入的日期格式错误或范围不完整，请重新输入！");
					}
					if(s_str_date.length() > 0&&e_str_date.length() > 0){
						querysql.append(" and "+itemid+" between "+Sql_switcher.dateValue(s_str_date)+" and "+Sql_switcher.dateValue(e_str_date));
					}else{
						if(s_str_date.length() > 0){
							querysql.append(" and "+itemid+">"+Sql_switcher.dateValue(s_str_date));
						}
						if(e_str_date.length() > 0){
							querysql.append(" and "+itemid+"<"+Sql_switcher.dateValue(e_str_date));
						}
					}
				} else if ("N".equals(itemtype)) {
					querysql.append(" and " + itemid + "=" + value + "");
				}

			}

			//for (int i = 0; i < querylist.size(); i++) {
			//	FieldItem item = (FieldItem) querylist.get(i);
			//	item.setValue("");
			//	item.setViewvalue("");
			//}

			this.getFormHM().put("queryfieldlist", querylist);
			this.getFormHM().put("isShowCondition", "none");
			strwhere +=querysql.toString();
		}
		//添加高级查询liwc
		//String strQuery = (String) this.getFormHM().get("strQuery");
		String strQuery = (String)this.getUserView().getHm().get("staff_sql");
		strwhere +=strQuery==null?"":strQuery;
		//去除高级查询条件
		this.getUserView().getHm().remove("staff_sql");
		
		this.getFormHM().put("cond_str", strwhere);
		this.getFormHM().put("order_by", orderby.toString());

	}
	
	/**
	 * 管理范围权限sql
	 * @Title: getPrivWhr   
	 * @Description: 管理范围权限sql   
	 * @param userbase 人员库前缀
	 * @param expression 条件表达式
	 * @param backdate 历史时点
	 * @return
	 */
	private String getPrivWhr(String userbase, String expression, String backdate) {
	    StringBuffer privWhr = new StringBuffer();
	    String strwhere = "";
	    
	    //zxj 20151115  高级授权处理，由于设计缺陷，可能出现高级权限无法适用于历史时点数据的情况，报错即可，暂无法处理。
        ArrayList fieldlist = new ArrayList();
        try {
            strwhere = userView.getPrivSQLExpression(expression, userbase, false, fieldlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        privWhr.append(" and a0100 in(select a0100 " + strwhere.replaceAll(userbase + "A01","hr_emp_hisdata"));
        privWhr.append(" and (nbase='" + userbase + "'");
        privWhr.append(" or nbase='" + userbase.toUpperCase() + "')");
        privWhr.append(" and id in (select id from hr_hisdata_list");
        privWhr.append(" where create_date=" + Sql_switcher.dateValue(backdate));
        privWhr.append(" ))");
        
        return privWhr.toString();
	}
	private void rebackKeyword(ArrayList list){
		for(int i=0;i<list.size();i++)
        {
            FieldItem item=(FieldItem)list.get(i);
            String value = item.getValue();
            String viewvalue = item.getViewvalue();
            value = PubFunc.hireKeyWord_filter_reback(value);
            viewvalue = PubFunc.hireKeyWord_filter_reback(viewvalue);
            item.setValue(value);
            item.setViewvalue(viewvalue);
        }
	}

    /**
     * 代码类指标查询生成sql条件
     * 
     * @param codesetid
     *            代码类
     * @param codeitemid
     *            代码项的值
     * @param like
     *            是否模糊查询 =1：是；=0：否
     * @return
     */
    private String getCodesetSql(String codesetid, String codeitemid, String like) {
        StringBuffer sql = new StringBuffer();
        try {
            if (StringUtils.isEmpty(codesetid) || StringUtils.isEmpty(codeitemid)) { return "1=2"; }

            sql.append("select codeitemid from");
            if (",UN,UM,@K".contains("," + codesetid.toUpperCase() + ",")) {
                sql.append(" organization");
            } else {
                sql.append(" codeitem");
            }

            sql.append(" where codesetid='" + codesetid + "'");
            sql.append(" and (1=2");
            String[] values = codeitemid.split("[|]");
            if (codeitemid.indexOf("`") != -1) {
                values = codeitemid.split("`");
            }

            for (int m = 0; m < values.length; m++) {
                String tempValue = values[m];
                if(StringUtils.isEmpty(tempValue)) {
                    continue;
                }
                
                if (values[m].endsWith("*") || values[m].endsWith("?") || values[m].endsWith("？")
                        || "1".equalsIgnoreCase(like)) {
                    if (values[m].endsWith("*") || values[m].endsWith("?") || values[m].endsWith("？")) {
                        tempValue = tempValue.substring(0, tempValue.length() - 1);
                    }

                    sql.append(" or codeitemid like '" + tempValue + "%'");
                } else {
                    sql.append(" or codeitemid = '" + values[m] + "'");
                }

                sql.append(" or codeitemdesc like '%" + tempValue + "%'");
            }

            sql.append(")");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql.toString();
    }
}
