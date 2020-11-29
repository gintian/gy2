package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class SimpleQueryTrans extends IBusiness {

    /**
     * 查询单位信息，区分查部门＼查单位＼都查
     * @param qobj
     * @param strWhere
     * @return
     */
    private String getQueryObjWhere(String qobj) {
        String strfilter = null;
        if ("1".equals(qobj)) {
            strfilter = " B01.B0110 in (select codeitemid from organization where codesetid='UM')";
        } else
            //if(qobj.equals("2"))
            strfilter = " B01.B0110 in (select codeitemid from organization where codesetid='UN')";
        return strfilter;
    }

    public void execute() throws GeneralException {
        try {
            ArrayList factorlist = (ArrayList) this.getFormHM().get("factorlist");
            if (factorlist == null)
                return;

            //查询类型，简单查询或通用查询
            String qobj = (String) this.getFormHM().get("qobj");

            String group_id = (String) this.getFormHM().get("group_id");

            /**default 为人员库*/
            String type = (String) this.getFormHM().get("type");
            if (type == null || "".equals(type))
                type = "1";

            String dbpre = (String) this.getFormHM().get("dbpre");
            String history = (String) this.getFormHM().get("history");
            String like = (String) this.getFormHM().get("like");
            String result = (String) this.getFormHM().get("result");

            if (history == null || "".equals(history))
                history = "0";

            if (like == null || "".equals(like))
                like = "0";

            boolean blike = false;
            if ("1".equals(like))
                blike = true;

            boolean bresult = true;
            if (result == null || result.length() <= 0)
                result = "";

            if ("1".equals(result))
                bresult = false;

            cat.debug("history=" + history);
            StringBuffer sfactor = new StringBuffer();

            StringBuffer sexpr = new StringBuffer();
            /**合成通用的表达式*/
            for (int i = 0; i < factorlist.size(); i++) {
                Factor factor = (Factor) factorlist.get(i);
                if (i != 0) {
                    factor.setLog(PubFunc.keyWord_reback(factor.getLog()));
                    sexpr.append(factor.getLog());
                }
                sexpr.append(i + 1);
                sfactor.append(factor.getFieldname().toUpperCase());
                sfactor.append(PubFunc.keyWord_reback(factor.getOper()));
                String q_value = factor.getValue().trim();
                if (q_value != null && q_value.length() > 0)
                    q_value = PubFunc.getStr(q_value);

                if (("0".equalsIgnoreCase(factor.getCodeid())) && "1".equals(like)
                        && ("A".equals(factor.getFieldtype()) || "M".equals(factor.getFieldtype()))) {
                    if (!(q_value == null || "".equals(q_value)))
                        sfactor.append("*");
                }

                sfactor.append(factor.getValue());

                /**对字符型指标有模糊*/
                if ("1".equals(like) && ("A".equals(factor.getFieldtype()) || "M".equals(factor.getFieldtype()))) {
                    if (!(q_value == null || "".equals(q_value)))
                        sfactor.append("*");
                }
                sfactor.append("`");
            }

            /**查询对象不是人员时，库前缀为空*/
            if (!"1".equals(type))
                dbpre = "";

            /**非超级用户且对人员库进行查询*/
            ArrayList fieldlist = new ArrayList();
            boolean bhis = false;
            if ("1".equals(history))
                bhis = true;
 
            //获取人员库
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            ArrayList nbases = kqUtilsClass.getKqPreList();
            
            String strwhere = "";
            String columns = "";
            StringBuffer strsql = new StringBuffer();
            
            for (int i = 0; i < nbases.size(); i++)
    		{
                String nbase = (String)nbases.get(i);
            
                if ("1".equals(type)) {
                    if ((!userView.isSuper_admin())) {
                        if (userView.getKqManageValue() != null && !"".equals(userView.getKqManageValue()))
                            strwhere = userView.getKqPrivSQLExpression(sexpr.toString() + "|" + sfactor.toString(), nbase, fieldlist);
                        else
                            strwhere = userView.getPrivSQLExpression(sexpr.toString() + "|" + sfactor.toString(), nbase, bhis, blike,
                                    bresult, fieldlist);
                    } else {
                        FactorList factorslist = new FactorList(sexpr.toString(), sfactor.toString(), nbase, bhis, blike, bresult,
                                Integer.parseInt(type), userView.getUserId());
                        fieldlist = factorslist.getFieldList();
                        strwhere = factorslist.getSqlExpression();
                    }
        
                    //过滤暂停考勤人员
                    String kqTypeWhr = kqUtilsClass.getKqTypeWhere(KqConstant.KqType.STOP, true);
                    strwhere = strwhere + kqTypeWhr;
                }
        
                if ("2".equals(type) && (!"0".equals(qobj))) {
                    if (strwhere.length() > 0)
                        strwhere = strwhere + " and " + getQueryObjWhere(qobj);
                }
        
                fieldlist = privFieldList(fieldlist, type, history);
        
          
                /**1人员　2:单位 3:职位*/
                if ("1".equals(type)) {
                    strsql.append("select distinct a0000,");
                    strsql.append("'").append(nbase).append("' nbase,");
                    strsql.append(nbase);
                    strsql.append("a01.a0100, ");
                    strsql.append(nbase);
                    strsql.append("a01.b0110 b0110,");
                    strsql.append(nbase);
                    strsql.append("a01.e0122 e0122,");
                    strsql.append(nbase);
                    strsql.append("a01.e01a1 e01a1,a0101 ");
                    strsql.append(" ");
                    columns = "nbase,a0100,b0110,e0122,e01a1,a0101,";
                } else if ("2".equals(type)) {
                    strsql.append("select distinct b01.b0110 b0110 ");
                    strsql.append(" ");
                    columns = "b0110,";
                } else if ("3".equals(type)) {
                    strsql.append("select distinct k01.e01a1 e01a1");
                    strsql.append(" ");
                    columns = "e01a1,";
                }
        
                
                if (strwhere.length() > 0)
                    strwhere = strwhere + " and not exists(select 1 from kq_group_emp where kq_group_emp.A0100=" + nbase
                            + "A01.a0100 and nbase='" + nbase + "')";
        
                strsql.append(strwhere);
                strsql.append(" union ");
                strwhere="";
        	}
            
            if (strsql.length() > 0)
            	strsql.setLength(strsql.length() - 7);
            // 33966 linbz 增加人员库查询条件
            StringBuffer allsql = new StringBuffer("");
            if(StringUtils.isNotEmpty(dbpre) && !"all".equalsIgnoreCase(dbpre)) {
            	allsql.append("select * from (");
            	allsql.append(strsql.toString());
            	allsql.append(") z ");
            	allsql.append(" where z.nbase='").append(dbpre).append("' ");
            }else {
            	allsql.append(strsql.toString());
            }
            
            this.getFormHM().put("sqlstr_s", allsql.toString());
            this.getFormHM().put("columnstr_s", columns);
            this.getFormHM().put("wherestr_s", "");
            this.getFormHM().put("type", type);
            this.getFormHM().put("ordeby_s", " order by b0110,e0122,a0100");

            KqParameter kptr = new KqParameter();
            this.getFormHM().put("isPost", kptr.getKq_orgView_post());
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * @param fieldlist
     * @param flag 1:人员2：单位3：职位
     * @return
     */
    private ArrayList privFieldList(ArrayList fieldlist, String flag, String history) {
        ArrayList list = new ArrayList();
        /**权限分析*/
        for (int j = 0; j < fieldlist.size(); j++) {
            FieldItem fielditem = (FieldItem) fieldlist.get(j);
            if ("1".equals(history) && (!fielditem.isMainSet()))
                continue;
            String fieldname = fielditem.getItemid();
            if ("e01a1".equals(fieldname) && "3".equals(flag))
                continue;
            else if ("b0110".equals(fieldname) && "2".equals(flag))
                continue;
            else {
                if ("b0110,e0122,e01a1,a0101".indexOf(fieldname) != -1)
                    continue;
            }
            cat.debug("priv_field=" + fieldname);
            fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname.toUpperCase())));
            list.add(fielditem);
        }
        return list;
    }
}