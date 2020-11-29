package com.hjsj.hrms.module.projectmanage.workhours.manhourssum.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * <p>Title: ManHoursSumQueryPlanTrans </p>
 * <p>Description: 查询组件方法</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-12-28 下午1:33:39</p>
 * @author liuyang
 * @version 1.0
 */
public class ManHoursSumQueryPlanTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            //如果有type参数说明是查询组件进入的
            String type = (String)this.getFormHM().get("type");
            
            if(!"1".equals(type) && !"2".equals(type))
                return;
            
            String subModuleId = (String)this.getFormHM().get("subModuleId");
            TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
            StringBuffer condsql = new StringBuffer();
            
            //快速查询
            if("1".equals(type)){
                 List values = (ArrayList) this.getFormHM().get("inputValues");
                 if(values==null || values.isEmpty()){
                     //刷新userView中的sql参数
                     tableCache.setQuerySql(condsql.toString());
                     userView.getHm().put(subModuleId, tableCache);
                     return;
                 }
                 
                 condsql.append(" and (1=2 ");
                 if("manhoursSum_id_001".equals(subModuleId)){
                     for(int i=0;i<values.size();i++){
                         String value = SafeCode.decode(values.get(i).toString());
                         //姓名
                         condsql.append(" OR myGridData.A0101 LIKE '%").append(value).append("%'");                        
                         //任务描述
                         condsql.append(" OR myGridData.P1505 LIKE '%").append(value).append("%'");
                        
                      }
                 }else {
                     for(int i=0;i<values.size();i++){
                         String value = SafeCode.decode(values.get(i).toString());
                         //姓名
                         condsql.append(" OR myGridData.A0101 LIKE '%").append(value).append("%'");                        
                         //电话
                         condsql.append(" OR myGridData.P1307 LIKE '%").append(value).append("%'");
                         //邮箱
                         condsql.append(" OR myGridData.P1309 LIKE '%").append(value).append("%'");
                      }
                }
                 condsql.append(" )");
            }else if("2".equals(type)){//方案查询
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
                 FactorList parser = new FactorList(exp ,cond, userView.getUserName());
                 condsql.append(parser.getSingleTableSqlExpression("myGridData"));
            }
            tableCache.setQuerySql(condsql.toString());
            userView.getHm().put(subModuleId, tableCache);
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
