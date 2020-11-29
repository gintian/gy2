package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SimpleQueryTrans</p>
 * <p>Description:简单查询交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 21, 2005:10:34:40 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
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

    /* 
     */
    public void execute() throws GeneralException {
        ArrayList factorlist = (ArrayList) this.getFormHM().get("factorlist");

        String qobj = (String) this.getFormHM().get("qobj");
        /**查询类型，简单查询或通用查询*/

        if (factorlist == null)
            return;

        /**default 为人员库*/
        String type = (String) this.getFormHM().get("type");
        if (type == null || "".equals(type))
            type = "1";

        String dbpre = (String) this.getFormHM().get("dbpre");
        String like = (String) this.getFormHM().get("like");
        String result = (String) this.getFormHM().get("result");
       
        
        String history = (String) this.getFormHM().get("history");
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
            factor.setOper(PubFunc.keyWord_reback(factor.getOper()));
            factor.setLog(PubFunc.keyWord_reback(factor.getLog()));
            if (i != 0) {
                sexpr.append(factor.getLog());
            }
            sexpr.append(i + 1);
            sfactor.append(factor.getFieldname().toUpperCase());
            sfactor.append(factor.getOper());
            String q_value = factor.getValue().trim();
            if (("0".equalsIgnoreCase(factor.getCodeid())) && "1".equals(like)
                    && ("A".equals(factor.getFieldtype()) || "M".equals(factor.getFieldtype()))) {
                if (!(q_value == null || "".equals(q_value)))
                    sfactor.append("*");
            }
            
            if ("1".equals(like)) {
                String vv = q_value.replaceAll(" ", "%");
                vv = vv.replaceAll("　", "%");
                sfactor.append(vv);
            } else
                sfactor.append(factor.getValue());
            
            /**对字符型指标有模糊*/
            if ("1".equals(like)
                    && ("A".equals(factor.getFieldtype()) || "M".equals(factor.getFieldtype()))) {
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
        fieldlist = privFieldList(fieldlist, type, history);
        String filterfield = getFilterFields(fieldlist, history);
        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        /**查询条件*/
        ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
        String userOrgId = managePrivCode.getPrivOrgId();
        KqParameter para = new KqParameter(this.getFormHM(), this.userView, "UN" + userOrgId, this.getFrameconn());
        //szk获取人员库
        String nbase = para.getNbase();
        
        HashMap hashmap = para.getKqParamterMap();
        String g_no = ((String) hashmap.get("g_no")).toLowerCase();
        String cardno = ((String) hashmap.get("cardno")).toLowerCase();
        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        boolean bhis = false;
        if ("1".equals(history))
            bhis = true;
       
        String strwhere = "";
        String columns = "";
        StringBuffer strsql = new StringBuffer();
        String[] dbases = nbase.split(",");
        for (int i = 0; i < dbases.length; i++)
		{
       	 if ( dbases[i].equals(dbpre))
			{
       		 dbases= new String[1];
       		 dbases[0]=dbpre;
       		 break;
			}
		}
        for (int i = 0; i < dbases.length; i++)
		{
        if ((!userView.isSuper_admin()) && "1".equals(type)) {
            if (userView.getKqManageValue() != null && !"".equals(userView.getKqManageValue()))
                strwhere = userView.getKqPrivSQLExpression(sexpr.toString() + "|"
                        + sfactor.toString(), dbases[i], fieldlist);
            else
                strwhere = userView.getPrivSQLExpression(sexpr.toString() + "|"
                        + sfactor.toString(), dbases[i], bhis, blike, bresult, fieldlist);
        } else {
            FactorList factorslist = new FactorList(sexpr.toString(), sfactor.toString(), dbases[i],
                    bhis, blike, bresult, Integer.parseInt(type), userView.getUserId());
            fieldlist = factorslist.getFieldList();
            strwhere = factorslist.getSqlExpression();
        }

        if ("2".equals(type) && (!"0".equals(qobj))) {
            if (strwhere.length() > 0)
                strwhere = strwhere + " and " + getQueryObjWhere(qobj);
        }
        KqUtilsClass kqUtils = new KqUtilsClass(this.frameconn, this.userView);
        //不包括暂停考勤人员
        strwhere = strwhere + kqUtils.getKqTypeWhere(KqConstant.KqType.STOP, true);
        //不包括不考勤人员
        strwhere = strwhere + kqUtils.getKqTypeWhere(KqConstant.KqType.NO, true);
        
        
     
        /**1人员　2:单位 3:职位*/
        if ("1".equals(type)) {
            strsql.append("select distinct a0000,");
            strsql.append("'" + dbases[i] + "' nbase,");
            strsql.append( dbases[i]);
            strsql.append("a01.a0100, ");
            strsql.append( dbases[i]);
            strsql.append("a01.b0110 b0110,");
            strsql.append( dbases[i]);
            strsql.append("a01.e0122 e0122,");
            strsql.append( dbases[i]);
            strsql.append("a01.e01a1 e01a1,a0101," + g_no + " gno," + cardno + " cardno");
            strsql.append(" ");
            columns = "a0100,nbase,b0110,e0122,e01a1,a0101,gno,cardno,";
        } else if ("2".equals(type)) {
            strsql.append("select distinct b01.b0110 b0110 ");
            strsql.append(" ");
            columns = "b0110,";
        } else if ("3".equals(type)) {
            strsql.append("select distinct k01.e01a1 e01a1");
            strsql.append(" ");
            columns = "e01a1,";
        }
        /**存在不同字段*/
        //2013年3月4日14:09:55 edit by wangmj 人员信息是从主集取得，不能添加子集指标
        /*if(!(filterfield==null||filterfield.equals("")))
        {
        	columns=columns+filterfield+",";
        	strsql.append(",");
        	strsql.append(filterfield);
        } */
        strsql.append(strwhere);
        strsql.append(" union ");
        strwhere="";
		}
        if (strsql.length() > 0)
        	strsql.setLength(strsql.length() - 7);
        this.getFormHM().put("sql_str", strsql.toString());
        this.getFormHM().put("columns", columns);
        this.getFormHM().put("cond_str", "");
        this.getFormHM().put("type", type);
        this.getFormHM().put("cond_order", " order by b0110,e0122");

        this.getFormHM().put("viewPost", para.getKq_orgView_post());
    }

    /**
     * 取得查询条件中的指标和固定指标不同的项目串
     * @param list
     * @return
     */
    private String getFilterFields(ArrayList list, String history) {
        StringBuffer strfield = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            FieldItem item = (FieldItem) list.get(i);
            if ("1".equals(history) && (!item.isMainSet()))
                continue;
            strfield.append(item.getItemid().toLowerCase());
            strfield.append(",");
        }
        if (strfield.length() > 0)
            strfield.setLength(strfield.length() - 1);
        return strfield.toString();
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
            fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname
                    .toUpperCase())));
            list.add(fielditem);
        }
        return list;
    }
}
