package com.hjsj.hrms.module.gz.salaryaccounting.inout.transaction;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.inout.businessobject.SalaryInOutBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *Title:SetExpTypeTrans
 *Description:设置导出格式
 *Company:HJHJ
 *Create time:2015-7-3 
 *@author lis
 */
public class SetExpTypeTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			String imodule=(String)this.getFormHM().get("imodule");
			imodule=PubFunc.decrypt(SafeCode.decode(imodule));
			//typeflag表示是导出还是下载，导出时1或者没有 下载模板是2
			String typeflag = (String)this.getFormHM().get("typeflag");
			String spFlag = (String)this.getFormHM().get("flag");
			salaryid=SafeCode.decode(salaryid); //解码
			salaryid =PubFunc.decrypt(salaryid); //解密
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			safeBo.isSalarySetResource(salaryid.toString(),null);
			
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.valueOf(salaryid),this.userView);
			SalaryInOutBo inOutBo = new SalaryInOutBo(this.getFrameconn(), Integer.valueOf(salaryid), this.userView);
			ArrayList salaryItemList=gzbo.getSalaryItemList("",salaryid,1);//取得对应薪资类别的所有薪资项目
			
			ArrayList salaryItemList2=new ArrayList();
			String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  //是否显示停发标识 ， 1：有  	
			String flow_flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");//是否走审批1：是
			if("1".equalsIgnoreCase(flow_flag) && !"2".equals(typeflag)) {//只有导出并且走审批显示审批意见
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("itemid", "appprocess");
				abean.set("itemdesc", "审批意见");
				abean.set("itemtype", "M");
				abean.set("initflag", "3");//默认当作系统项，这样，不会应该底下判断是否有累计项或者导入项的权限导致错误
				salaryItemList.add(0,abean);
			}
			if("2".equals(typeflag)) {
				String hiddenStr = ",a0100,a01z0,a0000,a00z0,a00z1,a00z2,a00z3,nbase,sp_flag,sp_flag2,appprocess,";//默认隐藏的字段
				//找出唯一性指标，如果是唯一性指标则不让选择，必然下载的字段
				Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
				//系统设置唯一性指标代码
				String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
				String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
				if(uniquenessvalid==null|| "0".equals(uniquenessvalid))
					onlyname="";
				//找出所有的计算项
				HashMap calcuItemMap = inOutBo.getCalcuItemMap();//公式计算项
				//如果设置了，excel_template_limit
				salaryItemList = gzbo.getHeadList(salaryItemList, calcuItemMap);
				for(int i=0;i<salaryItemList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)salaryItemList.get(i);
					String itemid=(String)abean.get("itemid");
					LazyDynaBean salaryItemBean = new LazyDynaBean();
					if(hiddenStr.indexOf(itemid.toLowerCase()) != -1)//判断是否显示停发标识
						continue;
					
					if(itemid.equalsIgnoreCase(onlyname))
						continue;
					
					String flag=(String)abean.get("initflag");//0：输入项,1：累积项, 2：导入, 3：系统项 
					if (!this.userView.isSuper_admin()) {
						// 0：输入项,1：累积项, 2：导入, 3：系统项
						if (!"3".equals(flag)) {
							// 当前用户是否拥有该薪资项目的权限
							if ("0".equalsIgnoreCase(this.userView.analyseFieldPriv(itemid)))
								continue;
						}
					}
					salaryItemBean.set("itemid",itemid);
					salaryItemBean.set("itemdesc",(String)abean.get("itemdesc"));
					salaryItemList2.add(salaryItemBean);
				}
			}else {
				for(int i=0;i<salaryItemList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)salaryItemList.get(i);
					String itemid=(String)abean.get("itemid");
					LazyDynaBean salaryItemBean = new LazyDynaBean();
					if("a01z0".equalsIgnoreCase(itemid)&&(a01z0Flag==null|| "0".equals(a01z0Flag)))//判断是否显示停发标识
						continue;
					
					/*A0000：顺序字段，A0100：人员编号，NBASE：人员库标识,这三个不显示
					 * 如果是超户则全部显示，如果不是，系统项全部显示，不是系统项的则要看当前用户有没有该权限
					 */
					
					if(!"A0000".equals(itemid)&&!"A0100".equals(itemid)&&!"NBASE".equals(itemid))
					{
						String flag=(String)abean.get("initflag");//0：输入项,1：累积项, 2：导入, 3：系统项 
						if (!this.userView.isSuper_admin()) {
							// 0：输入项,1：累积项, 2：导入, 3：系统项
							if (!"3".equals(flag)) {
								// 当前用户是否拥有该薪资项目的权限
								if ("0".equalsIgnoreCase(this.userView.analyseFieldPriv(itemid)))
									continue;
							}
						}
						salaryItemBean.set("itemid",itemid);
						salaryItemBean.set("itemdesc",(String)abean.get("itemdesc"));
						salaryItemList2.add(salaryItemBean);
					}
				}
			}
			
			//----------------------------根据栏目设置进行排序--------------------
			String tName="";
			if("sp".equalsIgnoreCase(spFlag))
				tName="salaryspdetail_"+salaryid;
			else
				tName="salary_"+salaryid;
			
			//栏目设置已经存在，则从数据库中取
			int schemeId = gzbo.getSchemeId(tName);
			// 从数据库中得到可以显示的薪资项目代码
			if(schemeId > 0){
				Map<String,Integer> itemIdList = gzbo.getTableItems(schemeId,"1");
				salaryItemList2 = gzbo.getSchemedHeadItemList(salaryItemList2, itemIdList);
			}
			//---------------------------排序结束---------------------------
			
			this.getFormHM().put("data",salaryItemList2);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
