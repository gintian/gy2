package com.hjsj.hrms.module.kq.holiday.transaction;

import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.module.kq.holiday.businessobject.HolidayBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowOverTimeLeaveDetailedTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {

        try {
        	// 34146 linbz 增加primarykey_e为usr|00000009| 库和编号的隐藏列参数，防止栏目设置不显示库标识取不到
        	String primaryKey = (String) this.getFormHM().get("primaryKey");
            primaryKey = PubFunc.decrypt(primaryKey);
            if(primaryKey.indexOf("|") == -1)
            	return;
            String nbase = primaryKey.split("\\|")[0];
            String a0100 = primaryKey.split("\\|")[1];
            
            HolidayBo bo = new HolidayBo(this.frameconn, this.userView);
            HashMap leavetimeUnitMap = bo.getLeavetimeUnit();
        	// 获取调休时长单位长度
            int decimalwidth = (Integer)leavetimeUnitMap.get("decimalwidth");
            String itemUnit = (String)leavetimeUnitMap.get("item_unit");
            String standardUnit = KqParam.getInstance().getSTANDARD_HOURS();
            String tranUnit = standardUnit;
            if (itemUnit == null || itemUnit.length() <= 0)
                itemUnit = KqConstant.Unit.HOUR;
            
            if (itemUnit.equals(KqConstant.Unit.HOUR))
                tranUnit = "60.0";
            else if (itemUnit.equals(KqConstant.Unit.MINUTE))
                tranUnit = "1.0";
            else if (itemUnit.equals(KqConstant.Unit.DAY))
                tranUnit = "(60.0*" + standardUnit + ")";
            FieldItem fieldItem = DataDictionary.getFieldItem("Q3305", "Q33");
			int Q3305Len = fieldItem.getItemlength();
			fieldItem = DataDictionary.getFieldItem("Q3307", "Q33");
			int Q3307Len = fieldItem.getItemlength();
			fieldItem = DataDictionary.getFieldItem("Q3309", "Q33");
			int Q3309Len = fieldItem.getItemlength();
            StringBuffer sql = new StringBuffer();
            sql.append("select Q3303");//Q3305,Q3307,Q3309
            sql.append(",CAST(ROUND(Q3305/" + tranUnit + "," + decimalwidth + ") AS NUMERIC("+ Q3305Len +","+ decimalwidth +" )) AS Q3305F");
            sql.append(",CAST(ROUND(Q3307/" + tranUnit + "," + decimalwidth + ") AS NUMERIC("+ Q3307Len +","+ decimalwidth +" )) AS Q3307F");
            sql.append(",CAST(ROUND(Q3309/" + tranUnit + "," + decimalwidth + ") AS NUMERIC("+ Q3309Len +","+ decimalwidth +" )) AS Q3309F");
            sql.append(" from q33");
            sql.append(" where nbase='" + nbase + "'");
            sql.append(" and a0100='" + a0100 + "'");
            // 34234 linbz 前台传回的调休有效期间作为查询的起始时间
            String leaveActiveTime = (String) this.getFormHM().get("leaveActiveTime");
            if(StringUtils.isNotEmpty(leaveActiveTime) && leaveActiveTime.indexOf("~")!=-1) {
            	String start = leaveActiveTime.split("~")[0];
            	String end = leaveActiveTime.split("~")[1];
            	sql.append(" and q3303 >= '" + start.replace("-", ".") + "'");
                sql.append(" and q3303 <= '" + end.replace("-", ".") + "' ");
            }
            // 获取列头集合
            ArrayList<ColumnsInfo> columnList =  HolidayBo.getOverTimeColumnList(decimalwidth);
            /** 加载表格 */
            TableConfigBuilder builder = new TableConfigBuilder("holidayOverTimeLeaveDetailed_0001", columnList,
                    "holidayOverTimeLeaveDetailed_0001", this.userView, this.frameconn);
            builder.setDataSql(sql.toString());
            builder.setOrderBy("order by q3303");
            builder.setAutoRender(false);
            builder.setPageSize(20);
            builder.setColumnFilter(true);
            String config = builder.createExtTableConfig();
            this.getFormHM().put("tableConfig", config.toString());
            if (itemUnit.equals(KqConstant.Unit.HOUR))
            	this.getFormHM().put("hourlyBasis", "小时");
            else if (itemUnit.equals(KqConstant.Unit.MINUTE))
            	this.getFormHM().put("hourlyBasis", "分钟");
            else if (itemUnit.equals(KqConstant.Unit.DAY))
            	this.getFormHM().put("hourlyBasis", "天");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
