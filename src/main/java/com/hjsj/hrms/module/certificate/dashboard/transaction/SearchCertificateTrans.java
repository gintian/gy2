package com.hjsj.hrms.module.certificate.dashboard.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 快速查询交易类
 * @Title:        SearchCertificateTrans.java
 * @Description:  证书管理个人门户-借阅证书调用的交易类
 * @Company:      hjsj     
 * @Create time:  2018-7-25 上午11:02:23
 * @author        linbz
 * @version       1.0
 */
public class SearchCertificateTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            ArrayList<String> valuesList = new ArrayList<String>();
            String subModuleId = (String) this.getFormHM().get("subModuleId");
            TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get(subModuleId);
            if (catche != null) {
                // 拼接sql
                StringBuffer sql = new StringBuffer();
                // 查询类型，1为输入查询，2为方案查询
                String type = (String) this.getFormHM().get("type");
                if ("1".equals(type)) {
                    // 输入的内容
                    valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
                    
                    if (valuesList.size() > 0) {
                        String where = getWhereSql(valuesList);
                        if (!StringUtils.isEmpty(where))
                            sql.append(" and (" + where + ")");
                    }
                    
                } else {
                    sql.append(" and ");
                    String exp = SafeCode.decode(this.getFormHM().get("exp").toString());
                    exp = PubFunc.keyWord_reback(exp);
                    String cond = PubFunc.keyWord_reback(SafeCode.decode(this.getFormHM().get("cond").toString()));
                    if (cond.length() < 1 || exp.length() < 1) {
                        // 查询方案点击全部，刷新保存的快速查询sql
                        if (catche.getCustomParamHM() != null)
                            catche.getCustomParamHM().put("fastQuerySql", "");
                        // 刷新userView中的sql参数
                        catche.setQuerySql("");
                        this.userView.getHm().put(subModuleId, catche);
                        return;
                    }
                    
                    FactorList parser = new FactorList(exp, cond, userView.getUserName());
                    sql.append(parser.getSingleTableSqlExpression("myGridData"));
                }

                catche.setQuerySql(sql.toString());
                //保存快速查询条件备用
                 if(catche.getCustomParamHM()==null)
                     catche.setCustomParamHM(new HashMap<String, String>());
                 
                 catche.getCustomParamHM().put("fastQuerySql", sql.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 拼接 查询条件
     * 
     * @param valuelist
     *            输入框中的输入的值
     * @return
     */
    private String getWhereSql(ArrayList<String> valuelist) {
    	
        if (valuelist == null || valuelist.size() < 1)
            return "";
        CertificateConfigBo certificateConfigBo = new CertificateConfigBo(this.frameconn, this.userView);
        // 证书名称
		String certName = certificateConfigBo.getCertName();
        StringBuffer where = new StringBuffer("");
        for (int i = 0; i < valuelist.size(); i++) {
            String value = valuelist.get(i);
            if (StringUtils.isEmpty(value))
                continue;

            value = SafeCode.decode(value);
            if (StringUtils.isEmpty(value))
                continue;

            if (StringUtils.isNotBlank(where.toString()))
                where.append(" or");

            where.append(" A0101 like '%" + value + "%'");
            where.append(" or " + certName + " like '%" + value + "%'");
        }
        return where.toString();
    }

}