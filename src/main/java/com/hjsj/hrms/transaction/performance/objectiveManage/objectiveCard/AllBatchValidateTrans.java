package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectiveEvaluateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * 全部提交目标卡验证
 * @date 2013-03-11
 * @author 田野
 *
 */
public class AllBatchValidateTrans extends IBusiness {

	public void execute() throws GeneralException {
	try {
		String year="-1";
		String status="-2";
		String quarter="-1";
		String month="-1";
		String planid="-1";
		String isSort="1";
		String isOrder="0";
		Map infoMap = new HashMap();
		StringBuffer str = new StringBuffer();
		StringBuffer records  = new StringBuffer();
		planid=(String)this.getFormHM().get("plan_id")==null?planid:(String)this.getFormHM().get("plan_id");
		isSort=(String)this.getFormHM().get("sort")==null?isSort:(String)this.getFormHM().get("sort");
		isOrder=(String)this.getFormHM().get("order")==null?isOrder:(String)this.getFormHM().get("order");
		ObjectiveEvaluateBo bo = new ObjectiveEvaluateBo(this.getFrameconn(),this.getUserView());
		//获得当前用户下参与打分的所有计划相关信息
		HashMap map;
	
			map = bo.getYearAndPersonList3(year, quarter, month, status, this.userView,planid,isSort,isOrder);
		
		ArrayList personList = (ArrayList)map.get("3");
		 for(int i=0;i<personList.size();i++)
         {
          	LazyDynaBean abean=(LazyDynaBean)personList.get(i);
         	String scoreStatus =(String)abean.get("scorestatus");
         	String name =(String)abean.get("name");
         	String tmpstatus=(String)abean.get("status");//用来判断是否可以评估记录，前台是靠它显示“查看”还是‘评估’
         	String personName = (String)abean.get("a0101");
         	String record = (String)abean.get("record");
         	String sp_flag = (String)abean.get("sp_flag");
         	String clientName = SystemConfig.getPropertyValue("clientName");
         	if(clientName!=null&& "gwyjy".equalsIgnoreCase(clientName)){
         		//判断是否存在可打分中没有打分的计划，
             	if(scoreStatus!=null&& "0".equals(scoreStatus)&& "1".equals(tmpstatus)&& "03".equals(sp_flag)){
             		if(!infoMap.containsKey(name)){
             			infoMap.put(name, personName);
             		}else{
             			infoMap.put(name, (String)infoMap.get(name)+" ,"+personName);
             		}
             	}else{
             		if(scoreStatus!=null&& "1".equals(tmpstatus)){//筛选出需要评估的数据进行数据库操作
             			if("".equals(records.toString())){
                     		records.append(record) ;
                     	}else{
                     		records.append("/"+record);
                     	}
             		}
             	}
         		
         	}else{
         		//判断是否存在可打分中没有打分的计划，
             	if(scoreStatus!=null&& "0".equals(scoreStatus)&& "1".equals(tmpstatus)){
             		if(!infoMap.containsKey(name)){
             			infoMap.put(name, personName);
             		}else{
             			infoMap.put(name, (String)infoMap.get(name)+" ,"+personName);
             		}
             	}else{
             		if(scoreStatus!=null&& "1".equals(tmpstatus)){//筛选出需要评估的数据进行数据库操作
             			if("".equals(records.toString())){
                     		records.append(record) ;
                     	}else{
                     		records.append("/"+record);
                     	}
             		}
             	}
         	}
         	
         }
		 //判断没有打分的记录是否不为空，进行组装提示信息
		 if(!infoMap.isEmpty()){
			 Iterator it = infoMap.entrySet().iterator();
			 while (it.hasNext()) {
				 Map.Entry pairs = (Map.Entry) it.next();
				 str.append("\r\n<"+ pairs.getKey()+">中未对:"+pairs.getValue()+"进行打分!");
			 }
			 str.append("\r\n不能进行提交！");
		 }
		this.getFormHM().put("records",records.toString());
		this.getFormHM().put("info",SafeCode.encode(str.toString()));
	} catch (GeneralException e) {
		e.printStackTrace();
	}
	}
	
}
