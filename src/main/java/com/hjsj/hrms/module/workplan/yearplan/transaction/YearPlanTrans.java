package com.hjsj.hrms.module.workplan.yearplan.transaction;

import com.hjsj.hrms.module.workplan.yearplan.businessobject.YearPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.ibm.icu.text.SimpleDateFormat;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Date;
/**
 * 计划制订 
 * changxy	
 * */
public class YearPlanTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		
		YearPlanBo bo=new YearPlanBo(this.userView,this.frameconn);
		try {
			String operaflag=(String)this.getFormHM().get("operaflag");
			if(operaflag!=null&&!"".equals(operaflag)){
				if("edit".equalsIgnoreCase(operaflag)){//编辑或任务追踪查看计划
					String planid=(String)this.getFormHM().get("planid");
					ArrayList list=bo.searchYearPlan(planid);
					this.getFormHM().put("list", list);
				}else if("decrpt".equals(operaflag)){//选择审批人时需 是牵头单位下的人员
					ArrayList list=(ArrayList)this.getFormHM().get("orglist");
					String decrylist="";
					for (int i = 0; i < list.size(); i++) {
						if(list.get(i)!=null&&!"".equals(list.get(i))){
							decrylist+=PubFunc.decrypt((String)list.get(i))+",";
						}
					}
					this.getFormHM().put("decrylist",decrylist.substring(0, decrylist.length()-1));
				}else if("responsible".equals(operaflag)){//选择责任人时 自动带入责任单位责任组责任室
					String orgid=(String)this.getFormHM().get("orgid");//责任人所在部门
					ArrayList orglist=(ArrayList)this.getFormHM().get("leadUion");
					orgid=PubFunc.decrypt(orgid);
					String org=bo.getOrgName(orglist, orgid);
					this.getFormHM().put("orgIdAndName", org);
					
				}else if("move".equals(operaflag)){//拖拽移动操作
					String orP_id=(String)this.getFormHM().get("orP_id");
					String orP_41=(String)this.getFormHM().get("orP_41");
					String toP_id=(String)this.getFormHM().get("toP_id");
					String toP_41=(String)this.getFormHM().get("toP_41");
					String dropPosition=(String)this.getFormHM().get("dropPosition");
					LazyDynaBean bean=new LazyDynaBean();
					bean.set("orP_id", orP_id);
					bean.set("orP_41", orP_41);
					bean.set("toP_id", toP_id);
					bean.set("toP_41", toP_41);
					bean.set("dropPosition", dropPosition);
					bean.set("year",(String)this.getFormHM().get("year"));
					ArrayList<LazyDynaBean> data=bo.orderYearPlan(bean);
					this.getFormHM().put("datalist", data);
				}else if("getorgid".equals(operaflag)){//任务指派牵头单位关联操作人
					String b0110 = this.userView.getUnitIdByBusi("5");//牵头单位在操作人员管理范围（绩效业务管理范围）
					StringBuffer sbf=new StringBuffer();
					sbf.append("");
					if(b0110.split("`")[0].length() > 2){//组织机构去除UN、UM后不为空：取本级，本级，下级。为空：最高权限
						String[] b0110Array = b0110.split("`");
						for (int i = 0; i < b0110Array.length; i++) {
							sbf.append(b0110Array[i].substring(2));
							if(i<b0110Array.length-1)
								sbf.append(",");
						}
					}
					this.getFormHM().put("recommendOrgid", sbf.toString());
				}else{
					boolean flag=false;
					ArrayList arry=(ArrayList)this.getFormHM().get("list");
					if("delete".equalsIgnoreCase(operaflag)){//删除计划
						flag=bo.delPlan(arry);
					}
					if("release".equalsIgnoreCase(operaflag)){//发布计划
						//haosl add 20170309 计划是否已经指派过,有的话直接将任务状态置为 “执行中”。。。
						flag=bo.updatePlanType(arry, "release");
						boolean isAssign =(Boolean)this.getFormHM().get("isAssign");
						if(isAssign && flag){
							if(this.getFormHM().get("assignIds")!=null){
								ArrayList list = (ArrayList)this.getFormHM().get("assignIds");
								flag = bo.updatePlanType(list, "assign");
							}
						}
					}
					if("stop".equalsIgnoreCase(operaflag)){//暂停计划
						flag=bo.updatePlanType(arry, "stop");
					}
					if("done".equalsIgnoreCase(operaflag)){//点击完成计划
						flag=bo.updatePlanType(arry, "done");
					}
					if("hasPlanAssigned".equalsIgnoreCase(operaflag)){//判断是否进行过任务指派
						ArrayList list = bo.hasPlanAssigned(arry);
						this.getFormHM().put("ids", list);
					}
					this.getFormHM().put("flag", flag);
				}
			}else{//页面初始化
				String year=(String)this.getFormHM().get("year");
				if(year==null||"".equals(year)){
					SimpleDateFormat sfm=new SimpleDateFormat("yyyy");
					year=sfm.format(new Date());
				}
				ArrayList<ColumnsInfo> comsInfo=new ArrayList<ColumnsInfo>();
				ArrayList buttonList=new ArrayList();
				comsInfo=bo.getColumnList();
				buttonList=getbuttonList();
				TableConfigBuilder builder=new TableConfigBuilder("yearplan_00001",comsInfo,"yearplan",userView,this.getFrameconn());
				
				if (this.userView.isSuper_admin() || this.userView.hasTheFunction("0KR01000102")){
					builder.setScheme(true);
					builder.setSetScheme(true);
					builder.setShowPublicPlan(false);
					builder.setShowPublicPlan(this.userView.hasTheFunction("0KR0100010201"));
				}
				builder.setConstantName("workplan/yearplan");  //设置导出excel
				builder.setTableTools(buttonList);
				builder.setAutoRender(true);
				builder.setEditable(false);//不可编辑
				builder.setTitle("年计划");
				builder.setDataSql(bo.getsql());
				builder.setOrderBy("order by p1741");
				String config=builder.createExtTableConfig();
				this.getFormHM().put("yearlist",bo.getYear()); 
				this.getFormHM().put("year", year);
				this.getFormHM().put("tableconfig", config);
				
				TableDataConfigCache tablecatch=(TableDataConfigCache)this.userView.getHm().get("yearplan_00001");
				SimpleDateFormat sft=new SimpleDateFormat("yyyy");
				int currentYear=Integer.parseInt(sft.format(new Date()));
				if(year!=null){
					tablecatch.setQuerySql(" and p1701='"+year+"' ");
					this.userView.getHm().put("year", year);
				}
				else{
					tablecatch.setQuerySql(" and p1701='"+currentYear+"' ");
					this.userView.getHm().put("year", currentYear);
				}
					
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	
	}

	
	public ArrayList getbuttonList(){
		ArrayList list=new ArrayList();
		YearPlanBo bo = new YearPlanBo(this.userView, this.getFrameconn());
		ArrayList exportBtn = new ArrayList();
		if(this.userView.hasTheFunction("0KR0100010101"))
			exportBtn.add(bo.getMenuBean("导出Excel", "yearPlan_me.exportExcel()", "/images/export.gif",new ArrayList()));
		if(this.userView.hasTheFunction("0KR01000101")){
			String menuStr = bo.getMenuStr("输出", "", exportBtn);
			list.add(menuStr);
		}
		if(this.userView.hasTheFunction("0KR01000103"))
			list.add(new ButtonInfo("新建","YearplanGlobal.createYearPlan"));
		if(this.userView.hasTheFunction("0KR01000105"))
			list.add(new ButtonInfo("删除","YearplanGlobal.delYearPlan"));
		if(this.userView.hasTheFunction("0KR01000106"))
			list.add(new ButtonInfo("发布","YearplanGlobal.releaseYearPlan"));
		if(this.userView.hasTheFunction("0KR01000107"))
			list.add(new ButtonInfo("暂停","YearplanGlobal.stopYearPlan"));
		if(this.userView.hasTheFunction("0KR01000108"))
			list.add(new ButtonInfo("完成","YearplanGlobal.doneYearPlan"));
		if(this.userView.hasTheFunction("0KR01000109"))
			list.add(new ButtonInfo("任务指派","YearplanGlobal.taskAssignment"));
		
		ButtonInfo searchBox = new ButtonInfo();
		searchBox.setFunctionId("WP00002003");//查询所走的交易号
		searchBox.setText("请输入任务名称...");//blank text
		searchBox.setType(ButtonInfo.TYPE_QUERYBOX);//类型 查询框
		searchBox.setShowPlanBox(false);//不显示查询方案
		list.add(searchBox);
		return list;
	} 
}
