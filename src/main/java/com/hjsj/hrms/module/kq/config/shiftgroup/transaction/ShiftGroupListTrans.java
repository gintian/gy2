package com.hjsj.hrms.module.kq.config.shiftgroup.transaction;

import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.ShiftGroupService;
import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.impl.ShiftGroupServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 班组列表交易类
 * <p>Title: ShiftGroupListTrans </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2018-10-25 下午02:21:54</p>
 * @author linbz
 * @version 1.0
 */
public class ShiftGroupListTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
		RowSet rs = null;
        try 
        {
        	String subModuleId = (String) this.getFormHM().get("subModuleId");// 为空：初次进入页面 ；不为空：快速查询
    		//输入查询
    		if("kqshiftgroup_01".equals(subModuleId)) {
    			TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get(subModuleId);
    			String type = (String)this.getFormHM().get("type");
    			if("1".equals(type)) {// 1:输入查询
    				StringBuilder querySql = new StringBuilder();
    				ArrayList<String> valuesList = new ArrayList<String>();
    				valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");// 输入的内容
    				// 快速查询
    				if (valuesList != null && valuesList.size() > 0) {
    					querySql.append(" and (");
    				}
    				for (int i = 0; valuesList != null && i < valuesList.size(); i++) {
    					String queryVal = valuesList.get(i);
    					queryVal = SafeCode.decode(queryVal);// 解码
    					if (i != 0) {
    						querySql.append("or ");
    					}
    					querySql.append("(name like '%" + queryVal+"%')");
    				}
    				if (valuesList != null && valuesList.size() > 0) {
    					querySql.append(" ) ");
    				}
    				catche.setQuerySql(querySql.toString());
    			}else if("2".equals(type)){//方案查询
    				StringBuilder querySql = new StringBuilder();
    				HashMap queryFields = catche.getQueryFields();
    				String exp = (String) this.getFormHM().get("exp");
    				exp = SafeCode.decode(exp);
    				exp=PubFunc.keyWord_reback(exp);
    				String cond = (String) this.getFormHM().get("cond");
    				cond = SafeCode.decode(cond);
    				cond = cond.replaceAll("＜", "<");
    				cond = cond.replaceAll("＞", ">");
    				if(cond.length()<1 || exp.length()<1){
    					catche.setQuerySql(querySql.toString());
    					return;
    				}
    				querySql.append(" and ");
    				FactorList parser = new FactorList(exp,cond,userView.getUserName(),queryFields);
    				querySql.append(parser.getSingleTableSqlExpression("myGridData"));
    				catche.setQuerySql(querySql.toString());
    			}
    			return;
    		}
    		// =0显示全部 =1显示有效期内班组
    		String validityflag = (String) this.getFormHM().get("validityflag");
        	ShiftGroupService shiftGroupService = new ShiftGroupServiceImpl(this.userView,this.frameconn);
            // 获取表格列表
            String config = shiftGroupService.getShiftGroupTableConfig(validityflag);
            this.getFormHM().put("tableConfig", config.toString());
            // 功能授权
            JSONObject priv = new JSONObject();
    		priv.put("editorpriv", this.userView.hasTheFunction("272020202")?"1":"0");// 编辑班组
    		priv.put("delpriv", this.userView.hasTheFunction("272020203")?"1":"0");// 删除班组
    		priv.put("shiftpriv", this.userView.hasTheFunction("272020204")?"1":"0");// 排班管理
    		priv.put("changepriv", this.userView.hasTheFunction("27202020401")?"1":"0");// 班组人员维护
    		this.getFormHM().put("privs", priv);
    		// 班组用户信息
    		HashMap userInfoMap = shiftGroupService.getShiftGroupInfo();
    		this.getFormHM().put("userInfo", userInfoMap);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
			PubFunc.closeResource(rs);
		}
    }


}
