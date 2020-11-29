package com.hjsj.hrms.module.talentmarkets.competition.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * @Description:岗位编制querybox查询交易类
 * @Author manjg
 * @Date 2019/7/26 10:17
 * @Version V1.0
 **/
public class JobPreparationQueryPlanTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        //如果有type参数说明是查询组件进入的
        String type = (String)this.getFormHM().get("type");
        String subModuleId = (String)this.getFormHM().get("subModuleId");
        TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
        StringBuffer condsql = new StringBuffer();

        if(!StringUtils.isEmpty(type)){
            //快速查询
            if("1".equals(type)){
                List values = (ArrayList) this.getFormHM().get("inputValues");
                if((values==null) || (values.isEmpty())){
                    //刷新userView中的sql参数
                    tableCache.setQuerySql(condsql.toString());
                    userView.getHm().put(subModuleId, tableCache);
                    return;
                }
                for(int i=0;i<values.size();i++){
                    /*if(i == 0){
                        condsql.append(" and (");
                    }else{
                        condsql.append(" or (");
                    }*/
                    condsql.append(" and (");
                    String value = SafeCode.decode(values.get(i).toString());
                    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
                    String pinyinField = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
                    if((pinyinField==null) || (pinyinField.length()==0)){
                        pinyinField="c0103";
                    }
                    condsql.append(" lower((select organization.codeitemdesc from organization where organization.codeitemid = myGridData.E01A1 and codesetid = '@K')) like '%");
                    condsql.append(value.toLowerCase()).append("%')");
                }
                tableCache.setQuerySql(condsql.toString());
            }else if("2".equals(type)){
                //方案查询
                condsql.append(" and ");
                String exp = SafeCode.decode(this.getFormHM().get("exp").toString());
                exp = PubFunc.keyWord_reback(exp);
                String cond = PubFunc.keyWord_reback(SafeCode.decode(this.getFormHM().get("cond").toString()));
                if(cond.length()<1 || exp.length()<1){
                    //刷新userView中的sql参数
                    tableCache.setQuerySql("");
                    userView.getHm().put(subModuleId, tableCache);
                    return;
                }
                FactorList parser = new FactorList(exp ,cond, userView.getUserName(),tableCache.getQueryFields());
                condsql.append(parser.getSingleTableSqlExpression("myGridData"));
                tableCache.setQuerySql(condsql.toString());
            }
            userView.getHm().put(subModuleId, tableCache);
            return;
        }
    }
}
