package com.hjsj.hrms.module.kq.config.shiftgroup.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.ShiftService;
import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.impl.ShiftServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 排班-班组人员维护交易类
 * <p>Title: ShiftGroupListTrans </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2018-10-25 下午02:21:54</p>
 * @author linbz
 * @version 1.0
 */
public class ShiftEmpTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
		RowSet rs = null;
        try 
        {
        	String subModuleId = (String) this.getFormHM().get("subModuleId");// 为空：初次进入页面 ；不为空：快速查询
    		//输入查询
    		if(StringUtils.isNotBlank(subModuleId)) {
    			TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get(subModuleId);
    			String type = (String)this.getFormHM().get("type");
    			if("1".equals(type)) {// 1:输入查询
    				StringBuilder querySql = new StringBuilder();
    				ArrayList<String> valuesList = new ArrayList<String>();
    				valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");// 输入的内容
    				Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
    				String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
    				FieldItem pinyinFi = DataDictionary.getFieldItem(pinyin_field);
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
    					querySql.append("(a0101 like '%"+ queryVal +"%' or g_no like '%"+ queryVal +"%'");
    					if(pinyinFi != null && !"0".equalsIgnoreCase(pinyinFi.getUseflag()))
    						querySql.append(" or " + pinyin_field + " like '%"+ queryVal +"%'");
    					
    					querySql.append(")");
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
    		
    		String jsonStr = (String)this.formHM.get("jsonStr");
    		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
        	// "type":init 初始化； change_emp: 班组人员调整 ；change_emp_data班组人员选择调入调出时间窗口获取需要的数据
        	String type = jsonObj.getString("type");
        	ShiftService shiftService = new ShiftServiceImpl(this.userView, this.frameconn);
        	if("init".equalsIgnoreCase(type)) {
        		// 获取表格列表
        		String config = shiftService.getShiftGroupEmpTableConfig(jsonObj);
        		this.getFormHM().put("tableConfig", config.split("`")[0]);
        		this.getFormHM().put("untableConfig", config.split("`")[1]);
        	}else if("change_emp".equalsIgnoreCase(type)) {
        		// 
        		String config = shiftService.changeShiftGroupEmp(jsonObj);
        		this.getFormHM().put("returnStr", config);
        	}else if("change_emp_data".equalsIgnoreCase(type)) {
        		// 
        		HashMap dataMap = shiftService.getGroupChangeEmpData(jsonObj);
        		this.getFormHM().put("weekList", (ArrayList)dataMap.get("weekList"));
                this.getFormHM().put("dateJson", (String)dataMap.get("dateJson"));
                this.getFormHM().put("year", (String)dataMap.get("year"));
                this.getFormHM().put("month", (String)dataMap.get("month"));
                this.getFormHM().put("weekIndex", (String)dataMap.get("weekIndex"));
        	}
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
			PubFunc.closeResource(rs);
		}
    }


}
