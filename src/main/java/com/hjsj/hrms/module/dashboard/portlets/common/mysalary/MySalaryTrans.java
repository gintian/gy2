package com.hjsj.hrms.module.dashboard.portlets.common.mysalary;

import com.hjsj.hrms.module.gz.mysalary.businessobject.MySalaryService;
import com.hjsj.hrms.module.gz.mysalary.businessobject.impl.MySalaryServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MySalaryTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				String queryDate = (String)this.getFormHM().get("queryDate");

				String schemeId = (String)this.getFormHM().get("schemeId");//方案id
		        Map returnData = new HashMap();
		        String returnCode = "success";
		        String returnMsg ="";
		        String state = "self";//查看员工薪酬
		        MySalaryService mySalaryService = new MySalaryServiceImpl(this.getFrameconn());
		        try {
		            if(StringUtils.isEmpty(userView.getA0100())){//如果a0100没有值 则代表是业务用户并且没有关联自助用户  不允许查看我的薪酬
		                returnData.put("isSelfServiceUser","0");
		                return;
		            }else{
		                returnData.put("isSelfServiceUser","1");
		            }
		            ArrayList  mySalarySchemeList = (ArrayList) mySalaryService.listMySalaryScheme(userView);
		            if(mySalarySchemeList.size() == 0) {
		            	returnData.put("mysalarySchemeNotDate", "1");
		            	return;
		            }
		            ArrayList schemes = new ArrayList();
		            for(int i = 0; i <mySalarySchemeList.size(); i++) {
		            	HashMap map = new HashMap();
		            	map.put("id", ((HashMap)mySalarySchemeList.get(i)).get("id"));
		            	map.put("name", ((HashMap)mySalarySchemeList.get(i)).get("name"));
		            	schemes.add(map); 
		            }
		            if(StringUtils.isBlank(schemeId)) {
		            	schemeId = (String) ((HashMap)mySalarySchemeList.get(0)).get("id");
		            }
		            
		            HashMap mySalaryScheme = new HashMap();
	            	mySalaryScheme = mySalaryService.getMySalaryScheme(schemeId,userView);
	            	String dateField = (String)((HashMap)mySalaryScheme.get("salary_fields")).get("salary_date");
	            	String viewTable = (String)((HashMap)mySalaryScheme.get("salary_fields")).get("salary_table");
		            String name = (String)((HashMap)mySalaryScheme.get("salary_fields")).get("name");
	            	HashMap mySalaryDateHM = mySalaryService.getMySalarySchemeMaxAndMinDate(viewTable,dateField,this.userView.getDbname(),this.userView.getA0100());
		            if(StringUtils.isBlank((String)mySalaryDateHM.get("maxDate"))) {
		            	returnData.put("mysalarySchemeNotDate", "1");
		            	return;
		            }
		            if(StringUtils.isBlank(queryDate)) {
		            	queryDate = (String)mySalaryDateHM.get("maxDate"); 
		            }
		            HashMap salaryInfo = mySalaryService.getMySalaryInfo(this.userView, schemeId, queryDate, queryDate);
		            returnData.put("schemes", schemes);
		            returnData.put("schemeId", schemeId);
		            returnData.put("name",name);
		            returnData.put("queryDate",queryDate);
		            returnData.put("schemeDate", mySalaryDateHM);
		            returnData.put("schemeData",salaryInfo);
		        }catch (GeneralException e){
		            e.printStackTrace();
		            returnCode = "fail";
		            returnMsg = e.getErrorDescription();
		            if (StringUtils.isEmpty(schemeId)) {//初始化时获取第一个薪酬方案有异常时  将此人所属的薪酬方案返回 避免前台页面因为第一个薪酬方案异常而空白
		                try{
		                	ArrayList  mySalarySchemeList = (ArrayList) mySalaryService.listMySalaryScheme(userView);
		                    returnData.put("schemes",mySalarySchemeList);
		                }catch (GeneralException ex){
		                    returnMsg = "getSchemesError";
		                }
		            }
		        }finally {
		        	this.getFormHM().put("return_data",returnData);
		        	this.getFormHM().put("return_msg",returnMsg);
		        	this.getFormHM().put("return_code",returnCode);
		        }
	}

}
