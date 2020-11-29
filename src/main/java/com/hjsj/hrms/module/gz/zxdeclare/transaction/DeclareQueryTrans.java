/**
 * FileName: DeclareQueryTrans
 * Author:   hssoft
 * Date:     2018/12/8 15:57
 * Description: 专项申报查询方案交易类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.hjsj.hrms.module.gz.zxdeclare.transaction;

import com.hjsj.hrms.module.gz.zxdeclare.businessobject.IDeclareService;
import com.hjsj.hrms.module.gz.zxdeclare.businessobject.impl.DeclareServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 〈类功能描述〉<br>
 * 〈专项申报查询方案交易类 〉
 *
 * @author hssoft
 * @create 2018/12/8
 * @since 1.0.0
 */
public class DeclareQueryTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        //如果有type参数说明是查询组件进入的
        String type = (String)this.getFormHM().get("type");
        if(StringUtils.isNotBlank(type)){//类型不为空
            String subModuleId = (String)this.getFormHM().get("subModuleId");
            TableDataConfigCache cache = (TableDataConfigCache)userView.getHm().get(subModuleId);
            IDeclareService declareService = new DeclareServiceImpl(this.frameconn, this.userView);
            if("1".equals(type)){//输入内容快速查询
                ArrayList<String> valuesList = new ArrayList<String>();
                // 输入的内容
                valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
                //查询控件的查询条件
                String sqlCondition = (String) this.getFormHM().get("sqlCondition");
                if(valuesList != null){
                    StringBuffer querySql = new StringBuffer();
                    sqlCondition = declareService.getSqlCondition(valuesList);
                    querySql.append(sqlCondition);
                    cache.setQuerySql(querySql.toString());
                }
            }else if("2".equals(type)){
                ArrayList<MorphDynaBean> items = (ArrayList<MorphDynaBean>)this.getFormHM().get("items");

                StringBuffer condsql = new StringBuffer("");
                HashMap queryFields = cache.getQueryFields();
                String exp = (String) this.getFormHM().get("exp");
                exp = SafeCode.decode(exp);
                exp = PubFunc.keyWord_reback(exp);
                String cond = (String) this.getFormHM().get("cond");
                cond = SafeCode.decode(cond);
                cond = PubFunc.keyWord_reback(cond);
                //调用解析公共类时传入查询字段集合queryFields，解析时就不会将非数据字典字段过滤掉了
                FactorList parser = new FactorList(exp ,cond, userView.getUserName(),queryFields);
                String sqlExp = parser.getSingleTableSqlExpression("myGridData");
                if(StringUtils.isNotBlank(sqlExp)){
                    condsql.append(" and ").append(sqlExp);
                }
                cache.setQuerySql(condsql.toString());
            }
            userView.getHm().put(subModuleId, cache);
            return;
        }
    }
}
