package com.hjsj.hrms.module.gz.zxdeclare.transaction;

import com.hjsj.hrms.module.gz.zxdeclare.businessobject.IDeclareService;
import com.hjsj.hrms.module.gz.zxdeclare.businessobject.impl.DeclareServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * 主页及申报页数据获取
 * @Titile: ZXDeclareDataTrans
 * @Description:
 * @Company:hjsj
 * @Create time: 2018年12月7日下午2:58:58
 * @author: wangbs
 * @version 1.0
 *
 */
public class ZXDeclareDataTrans extends IBusiness{
	@Override
	public void execute() throws GeneralException {
		String type = (String) this.formHM.get("type");
		String return_code = "success";
		String return_msg = "";//报错时的返回信息
		Map<String, Object> return_data = new HashMap<String, Object>(); //专项的信息map
		Map<String,Object> returnStr = new HashMap<String,Object>(); //返回到前台的map
		IDeclareService ZXDeclare = new DeclareServiceImpl(this.frameconn, this.userView);
		List<String> zxIdList = new ArrayList<String>();//专项代码集合
		zxIdList.add(IDeclareService.C_DECLARE_TYPE_CHILDEDU);
		zxIdList.add(IDeclareService.C_DECLARE_TYPE_CONTINU_EDU);
		zxIdList.add(IDeclareService.C_DECLARE_TYPE_HOUSING_RENT);
		zxIdList.add(IDeclareService.C_DECLARE_TYPE_INTEREST_EXPENSE);
		zxIdList.add(IDeclareService.C_DECLARE_TYPE_ILLNESS_MEDICALCARE);
		zxIdList.add(IDeclareService.C_DECLARE_TYPE_SUPPORT_ELDERLY);
		try {
			if("main".equals(type)) {
				Double total_money = 0.00; //六项已享受总额
				List<Map<String,Object>> items = new ArrayList<Map<String,Object>>(); //所有专项的信息list
				Map<String,Object> oneZxMap = new HashMap<String,Object>(); //一个专项的信息map
				List<Map<String,Object>> oneZxAllInfoList = new ArrayList<Map<String,Object>>(); //接收接口返回的一个专项的所有信息
				Map<String,Object> allZxData = ZXDeclare.listZXDeclare(this.userView);
				for(int i=0;i<zxIdList.size();i++) {
					oneZxMap = new HashMap<String, Object>();
					Double oneZxMoney = 0.00;//一个专项已享受金额
					if(allZxData.containsKey("zx_"+zxIdList.get(i))){//是否包含该专项的信息
						String declareCode = zxIdList.get(i);//专项代码
						oneZxAllInfoList = (List<Map<String, Object>>) allZxData.get("zx_"+zxIdList.get(i));
						List<Map<String,Object>> schoolContinueEduList = new ArrayList<Map<String,Object>>();//学历继续教育
						List<Map<String,Object>> jobContinueEduList = new ArrayList<Map<String,Object>>();//职业继续教育

						if(IDeclareService.C_DECLARE_TYPE_CONTINU_EDU.equals(declareCode)) {
							for(int j=0;j<oneZxAllInfoList.size();j++) {
								Map<String,Object> ContinueEduMap = oneZxAllInfoList.get(j);
								String cuntin_edu_type = (String)ContinueEduMap.get("cuntin_edu_type");
								if(IDeclareService.C_DECLARE_TYPE_CONTINU_EDU_EDU.equals(cuntin_edu_type)) {
									schoolContinueEduList.add(ContinueEduMap);
								}else {
									jobContinueEduList.add(ContinueEduMap);
								}
							}
						}
						if(IDeclareService.C_DECLARE_TYPE_CONTINU_EDU.equals(declareCode)) {
							if(schoolContinueEduList.size()==0) {
								this.addContinueZxInfo(oneZxMap,items,declareCode,oneZxMoney,IDeclareService.C_DECLARE_TYPE_CONTINU_EDU_EDU);
							}else {
                                oneZxMoney = this.assembleData(oneZxMap,items,schoolContinueEduList,ZXDeclare,oneZxMoney,declareCode);
                                total_money = total_money + oneZxMoney;
							}
							oneZxMoney = 0.00;
							oneZxMap = new HashMap<String, Object>();
							if(jobContinueEduList.size()==0) {
								this.addContinueZxInfo(oneZxMap,items,declareCode,oneZxMoney,IDeclareService.C_DECLARE_TYPE_CONTINU_EDU_PROFESSION);
							}else {
                                oneZxMoney = this.assembleData(oneZxMap,items,jobContinueEduList,ZXDeclare,oneZxMoney,declareCode);
                                total_money = total_money + oneZxMoney;
							}
						}else {
                            oneZxMoney = this.assembleData(oneZxMap,items,oneZxAllInfoList,ZXDeclare,oneZxMoney,declareCode);
                            total_money = total_money + oneZxMoney;
						}
					}else{
						if(IDeclareService.C_DECLARE_TYPE_CONTINU_EDU.equals((String)zxIdList.get(i))) {//继续教育特殊处理
							for(int k=0;k<2;k++) {
								oneZxMap = new HashMap<String, Object>();
								if(k==0) {
									this.addContinueZxInfo(oneZxMap,items,(String)zxIdList.get(i),oneZxMoney,IDeclareService.C_DECLARE_TYPE_CONTINU_EDU_EDU);
								} else {
									this.addContinueZxInfo(oneZxMap,items,(String)zxIdList.get(i),oneZxMoney,IDeclareService.C_DECLARE_TYPE_CONTINU_EDU_PROFESSION);
								}
							}
						}else {
							this.addEachZxInfo(oneZxMap,items,(String)zxIdList.get(i),oneZxMoney);
						}
					}
				}
				return_data.put("items", items);
				return_data.put("total_money", total_money);
			}else if("search".equals(type)) {
				String id = (String) this.formHM.get("id");
				return_data = ZXDeclare.getDeclareInfor(id);
			}else if("save".equals(type)) {//保存
				MorphDynaBean morphDynaParam = (MorphDynaBean) this.getFormHM().get("data");
				HashMap param = PubFunc.DynaBean2Map(morphDynaParam);
				return_code = ZXDeclare.saveZXDeclare(param,this.userView);
			}else if("submit".equals(type)) {//提交
				MorphDynaBean morphDynaParam = (MorphDynaBean) this.getFormHM().get("data");
				HashMap param = PubFunc.DynaBean2Map(morphDynaParam);
				return_code = ZXDeclare.submitZXDeclare(param,this.userView);
			}else if("retract".equals(type)) {//撤回
				String declares= (String)this.formHM.get("id");
				return_code = ZXDeclare.rejectDeclares(declares,null);
			}else if("change".equals(type)) {//变更
				String declares= (String)this.formHM.get("id");
				return_code = ZXDeclare.changeZXDeclare(declares,this.userView);
			}else if("revoke".equals(type)) {//撤销
				String declares= (String)this.formHM.get("id");
				return_code = ZXDeclare.revokeZXDeclare(declares,this.userView);
			}
			returnStr.put("return_code", return_code);
			returnStr.put("return_msg", return_msg);
			returnStr.put("return_data", return_data);
			this.formHM.put("returnStr",returnStr);
		} catch (GeneralException e) {
			return_code = "fail";
			return_msg = e.getErrorDescription();

			returnStr.put("return_code", return_code);
			returnStr.put("return_msg", return_msg);
			this.formHM.put("returnStr",returnStr);
			return;
		}
	}

	/**
	 * 截取目标日期年份
	 * @param targetDate
	 * @return
	 */
	private String subStringYear(String targetDate){
		String targetYear = targetDate.substring(0,4);
		return targetYear;
	}
	private String subStringMonth(String targetDate){
		String targetMonth = targetDate.substring(5,7);
		return targetMonth;
	}
	/**
	 * 拼装数据
	 * @param oneZxMap
	 * @param items
	 * @param oneZxAllInfoList
	 * @param ZXDeclare
	 * @param oneZxMoney
	 * @param declareCode
	 * @return
	 */
	private Double assembleData(Map<String,Object> oneZxMap, List<Map<String,Object>> items, List oneZxAllInfoList, IDeclareService ZXDeclare, Double oneZxMoney, String declareCode) throws GeneralException {
		String id = "";//加密申报id
		String approve_state = ""; //审核状态
		String declare_type = "";  //专项代码
		String deduct_type = IDeclareService.C_DECLARE_TYPE_MONTH;//默认按月抵扣
		String tempContinueEduType = "";//继续教育类型
		Map<String,Object> oneZxInfoMap = new HashMap<String,Object>(); //接收接口返回的一个专项的所有信息
		for(int j=0;j<oneZxAllInfoList.size();j++) {
			oneZxInfoMap = (Map<String, Object>) oneZxAllInfoList.get(j);
			String tempId = (String) oneZxInfoMap.get("id");
			if(IDeclareService.C_DECLARE_TYPE_CONTINU_EDU.equals(declareCode)) {
				tempContinueEduType = (String) oneZxInfoMap.get("cuntin_edu_type");
				if(IDeclareService.C_DECLARE_TYPE_CONTINU_EDU_PROFESSION.equals(tempContinueEduType)) {
					deduct_type = IDeclareService.C_DECLARE_TYPE_YEAR;//按年抵扣
				}
			}
			String tempApprove_state = (String) oneZxInfoMap.get("approve_state");
			String tempDeclare_type = (String) oneZxInfoMap.get("declare_type");
			/*获取当年最早计算日期*/
			String minPayDate = ZXDeclare.getMinPayDate(this.userView);
			String tempStart_date = (String) oneZxInfoMap.get("start_date");//主表抵扣起始日期
			String tempEnd_date = (String) oneZxInfoMap.get("end_date");//主表抵扣结束日期
			try{
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date payDate = dateFormat.parse(minPayDate);
				Date startDate = dateFormat.parse(tempStart_date);
				boolean isAfter =  payDate.after(startDate);
				if(isAfter){
					tempStart_date = minPayDate;
				}
			}catch (Exception e){
				e.printStackTrace();
				//获取当年最早计税日期出错
				throw new GeneralException(ResourceFactory.getProperty("gz.zxdeclare.error.getMinPayDate"));
			}

			if((StringUtils.isNotBlank(tempEnd_date) && "9999-12-31".equalsIgnoreCase(tempEnd_date)) || tempEnd_date.trim().length()==0){
				tempEnd_date = tempStart_date.substring(0, 4)+"-12-31";
			}

			BigDecimal tempDeduct_money = (BigDecimal) oneZxInfoMap.get("deduct_money");
			if(IDeclareService.C_DECLARE_TYPE_CHILDEDU.equals(declareCode)){//子女教育 计算已享受金额单独处理
				ArrayList sub_items = (ArrayList) oneZxInfoMap.get("sub_items");
				if(IDeclareService.C_APPROVE_STATE_ADOPT.equals(tempApprove_state)) {
				    for(int m = 0 ; m < sub_items.size() ; m++){
	                    HashMap subItem = (HashMap) sub_items.get(m);
						tempDeduct_money = (BigDecimal) subItem.get("deduct_money");

	                    String tempSubStart_date = (String) subItem.get("start_date");//教育起始日期
						String tempSubStartDateYear = subStringYear(tempSubStart_date);//教育起始年份
	                    String tempSubStartDateMonth = subStringMonth(tempSubStart_date);//教育起始月份

	                    Date nowDate = new Date();
	                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	                    String nowformatDate = format.format(nowDate);
	                    String nowYear = subStringYear(nowformatDate);
	                    String nowMonth = subStringMonth(nowformatDate);

	                    tempEnd_date = (String) subItem.get("end_date");//教育结束时间
	                    if((StringUtils.isNotBlank(tempEnd_date) && "9999-12-31".equalsIgnoreCase(tempEnd_date)) || StringUtils.isBlank(tempEnd_date)){
	                        tempEnd_date = nowYear+"-12-31";
	                    }

	                    //抵扣年与当前年比较
	                    int mainYearDifference = Integer.parseInt(tempStart_date.substring(0,4))-Integer.parseInt(nowYear);
	                    if(mainYearDifference == 0){//当前年是抵扣年
	                        //抵扣起始日期与当前日期比较
	                        int yearDifference = Integer.parseInt(tempSubStartDateYear)-Integer.parseInt(nowYear);
	                        if(yearDifference > 0){//该条子集记录的教育起始日期在当前年之后
	                            oneZxMoney += 0;
	                        }else if(yearDifference==0){
	                            int monthDifference = Integer.parseInt(tempSubStartDateMonth)-Integer.parseInt(nowMonth);
	                            if(monthDifference > 0){
	                                oneZxMoney += 0;
	                            }else if(monthDifference == 0){//该子集记录是生效的第一个月
	                                oneZxMoney = oneZxMoney + tempDeduct_money.doubleValue();
	                            }else if(monthDifference < 0){//该子集记录已生效一个月以上
	                                tempSubStart_date = tempSubStartDateYear+"-"+tempSubStartDateMonth+"-01";//子集记录新的教育起始日期，月初
	                                if(Integer.parseInt(tempStart_date.substring(5,7))>Integer.parseInt(tempSubStartDateMonth)){
										tempSubStart_date = tempStart_date;
									}
									oneZxMoney += this.getOneZxMoney(tempSubStart_date,tempEnd_date,tempApprove_state, tempDeduct_money, ZXDeclare, deduct_type);
	                            }
	                        }else if(yearDifference < 0){
	                            oneZxMoney += this.getOneZxMoney(tempStart_date,tempEnd_date,tempApprove_state, tempDeduct_money, ZXDeclare, deduct_type);
	                        }
	                    }else{//与抵扣年份不匹配
	                        oneZxMoney += 0;
	                    }
	                }
				}else {
                    oneZxMoney += 0;
				}
			}else{
				oneZxMoney += this.getOneZxMoney(tempStart_date,tempEnd_date,tempApprove_state, tempDeduct_money, ZXDeclare, deduct_type);
			}
			if(IDeclareService.C_APPROVE_STATE_FILED.equals(tempApprove_state)) {
				continue;
			}
			id = tempId;
			approve_state = tempApprove_state;
			declare_type = tempDeclare_type;
		}
		if(StringUtils.isNotBlank(id)) {
			oneZxMap.put("id", id);
			oneZxMap.put("declare_type", declare_type);
			oneZxMap.put("approve_state", approve_state);
			oneZxMap.put("money", oneZxMoney);
			if(IDeclareService.C_DECLARE_TYPE_CONTINU_EDU.equals(declareCode)) {
				oneZxMap.put("cuntin_edu_type", tempContinueEduType);
			}
			items.add(oneZxMap);
		}else {
		    if(StringUtils.isNotBlank(tempContinueEduType)) {
                this.addContinueZxInfo(oneZxMap, items, IDeclareService.C_DECLARE_TYPE_CONTINU_EDU, oneZxMoney, tempContinueEduType);
            }else {
                this.addEachZxInfo(oneZxMap,items,declareCode,oneZxMoney);
            }
		}
        return oneZxMoney;
	}


	/**
	 * 计算一个专项的已享受金额
	 * @param start_date 起始日期
	 * @param end_date 结束
	 * @param approve_state 审核状态
	 * @param deduct_money 扣除金额
	 * @param ZXDeclare
	 * @return
	 */
	private double getOneZxMoney(String start_date, String end_date, String approve_state, BigDecimal deduct_money, IDeclareService ZXDeclare, String deduct_type){
		double zxMoney =0; //专项已享受金额
		int startYearCount = 0;
		int startMonthCount = 0;
		int endYearCount = 0;
		int endMonthCount = 0;
		if(IDeclareService.C_APPROVE_STATE_ADOPT.equals(approve_state)) { //已通过
			String startYear = start_date.substring(0, 4);
			String startMonth = start_date.substring(5, 7);
			String endYear = end_date.substring(0,4);
			String endMonth = end_date.substring(5,7);
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String nowDate = format.format(date);
			String nowYear = nowDate.substring(0, 4);
			String nowMonth = nowDate.substring(5, 7);
			int year = Integer.parseInt(startYear)-Integer.parseInt(nowYear);//抵扣起始日期与当前日期比较

			if(IDeclareService.C_DECLARE_TYPE_MONTH.equals(deduct_type)){
				if(year > 0){
					return 0;
				}
				if(year == 0){
					int month = Integer.parseInt(startMonth)-Integer.parseInt(nowMonth);
					if(month > 0){
						return 0;
					}
					startMonthCount = Integer.parseInt(startMonth);
					startYearCount =  Integer.parseInt(startYear);
				}else{//起始日期小于当前年
					startYearCount =  Integer.parseInt(nowYear);
					startMonthCount = 1;
				}
				year = Integer.parseInt(endYear)-Integer.parseInt(nowYear);
				if(year < 0){
					return 0;
				}
				if(year == 0){
					int month = Integer.parseInt(endMonth) - Integer.parseInt(nowMonth);
					if(month > 0){
						endMonthCount = Integer.parseInt(nowMonth);
					}else{
						endMonthCount = Integer.parseInt(endMonth);
					}
					endYearCount = Integer.parseInt(endYear);
				}else{
					endYearCount = Integer.parseInt(nowYear);
					endMonthCount = Integer.parseInt(nowMonth);
				}
				if(startYearCount != endYearCount){
					return 0;
				}

				zxMoney = ((endMonthCount - startMonthCount+1) * deduct_money.doubleValue());
			}else{//职业资格继续教育
				if(year == 0){
					int month = Integer.parseInt(startMonth)-Integer.parseInt(nowMonth);
					if(month > 0){//在抵扣起始年  不到抵扣月
						return 0;
					}
					zxMoney = deduct_money.doubleValue();
				}else{//起始抵扣日期不在当前年
					return 0;
				}
			}
		}
		return zxMoney;
	}

	/**
	 * 某专项没有信息时赋予默认值
	 * @param oneZxMap 一个专项的信息map
	 * @param items 所有专项的信息list
	 * @param declare_type 专项代码
	 * @param oneZxMoney 专项已享受额
	 */
	private void addEachZxInfo(Map<String,Object> oneZxMap, List<Map<String,Object>> items, String declare_type,Double oneZxMoney) {
		oneZxMap.put("id", -1);
		oneZxMap.put("declare_type", declare_type);
		oneZxMap.put("approve_state", "");
		oneZxMap.put("money", oneZxMoney);
		items.add(oneZxMap);
	}
	/**
	 * 继续教育没有信息时赋予默认值
	 * @param oneZxMap 一个专项的信息map
	 * @param items 所有专项的信息list
	 * @param declare_type 专项代码
	 * @param oneZxMoney 专项已享受额
	 * @param cuntin_edu_type
	 */
	private void addContinueZxInfo(Map<String,Object> oneZxMap, List<Map<String,Object>> items, String declare_type,Double oneZxMoney,String cuntin_edu_type) {
		oneZxMap.put("id", -1);
		oneZxMap.put("cuntin_edu_type", cuntin_edu_type);
		oneZxMap.put("declare_type", declare_type);
		oneZxMap.put("approve_state", "");
		oneZxMap.put("money", oneZxMoney);
		items.add(oneZxMap);
	}
}
