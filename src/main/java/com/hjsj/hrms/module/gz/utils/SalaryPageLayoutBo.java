package com.hjsj.hrms.module.gz.utils;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryreport.businessobject.SalaryReportBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SalaryPageLayoutBo {

	private UserView userview;
	private Connection conn=null;
	public SalaryPageLayoutBo(Connection conn,UserView userview){
		this.conn = conn;
		this.userview=userview;
	}
	/**
	 * 获取发放功能按钮
	 * @param gz_module  薪资和保险区分标识  1：保险  否则是薪资
	 * @param isRedo 是否薪资重发数据
	 * @return
	 */
	public ArrayList getSalaryAccountingButtonList(SalaryTemplateBo gzbo,String gz_module,String accountingdate,String accountingcount,String salaryid,HashMap buttonMap,String isRedo){
		ArrayList buttonList = new ArrayList();
		VersionControl ver = new VersionControl();
		try{
			ButtonInfo splitButton = new ButtonInfo(ButtonInfo.BUTTON_SPLIT);//占位符
			//---------------按钮参数--------------------
			String  royalty_valid=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
			if(royalty_valid==null)
				royalty_valid="0";
			String isImportPiece="0";
			String  Piecevalid=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"valid");
			if(Piecevalid!=null && !"".equals(Piecevalid))
				isImportPiece=Piecevalid;
//			String comflag= gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.COMPARE_FIELD);
//		    String priv_mode="0";//是否出现数据比对菜单
//		    if(comflag!=null&&!comflag.equals(""))
//		    	priv_mode = "1";
		    String isVisibleItem = "1";//gzbo.isVisibleItem();
		    String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
		    String isShowManagerFunction="0"; 
		    LazyDynaBean bean = new LazyDynaBean();
			if(accountingdate!=null&&accountingdate.trim().length()>0&&accountingcount!=null&&accountingcount.trim().length()>0)
			{
				if(manager==null||manager.length()==0){
					bean = this.getGzLog(accountingdate,accountingcount,this.userview.getUserName(),salaryid);
				}else{
					bean = this.getGzLog(accountingdate,accountingcount,manager,salaryid);
				}			
			}
			if(manager==null||manager.length()==0||this.userview.getUserName().equals(manager))
			{
				isShowManagerFunction="1";//是管理员 
			}
			//------------------------------------------
			ArrayList list = new ArrayList();
			LazyDynaBean oneBean = new LazyDynaBean();
			String viewtype = (String)buttonMap.get("viewtype"); // 页面区分 0:薪资发放  1:审批  2:上报
			//------------------------导出---------------------------------------------
			if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020303"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020303"))){
				
				oneBean.set("text", ResourceFactory.getProperty("menu.gz.export"));
				oneBean.set("handler", "GzGlobal.exportData");
				list.add(oneBean);
				//menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.report"),"","GzGlobal.gzReport","",new ArrayList()));//薪资报表
			}
			
			//------------------------Excel导入------------------------------------------
			ArrayList menuList = new ArrayList();	
			if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3240231"))
					||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020306"))|| "2".equals(viewtype)){
				oneBean = new LazyDynaBean();
				oneBean.set("text", ResourceFactory.getProperty("gz_new.gz_accounting.inputExcel"));
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020302"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020302"))){
					menuList.add(getMenuBean(ResourceFactory.getProperty("gz_new.gz_accounting.formProject"),"","GzGlobal.importTable","",new ArrayList()));//按方案
				}
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020304"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020304")||("2".equals(viewtype)))
						||(!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020305"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020305"))||("2".equals(viewtype)&&this.userview.hasTheFunction("031405"))){
					menuList.add(getMenuBean(ResourceFactory.getProperty("gz_new.gz_accounting.formTemplate"),"","GzGlobal.importAsTemplate","",new ArrayList()));//按模板
				}

				oneBean.set("menu", menuList);
				list.add(oneBean);

			}
			//----------------------------薪资表 end --------------------------------------
			//---------------------------------编辑--------------------------------------------
			oneBean = new LazyDynaBean();
			menuList = new ArrayList();
			if(!"2".equals(viewtype)&&!"1".equals(royalty_valid)){//数据上报不要数据变化处理菜单
				oneBean.set("text", ResourceFactory.getProperty("menu.gz.dataChangeProcessing"));//数据变化处理
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3240201"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3250201"))){
						menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.comparisonWithFile"),"changecompare","GzGlobal.checkChangeCompare_account","",new ArrayList()));//变动比对(与工资档案比对)
				 
				}
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3240217"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3250218"))){
						menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.comparisonWithData"),"","GzGlobal.changesmore","",new ArrayList()));//数据比对(与上期数据比对)
				}
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3240212"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3250212"))){
						menuList.add(getMenuBean(ResourceFactory.getProperty("label.gz.handimport"),"handImport","GzGlobal.handImportMen","",new ArrayList()));//手工引入人员
				}
				if(menuList.size()!=0){
					oneBean.set("menu", menuList);
					list.add(oneBean);
				}
			}
			//---------------------------编辑 end -----------------------------------------
			//------------------------批量处理----------------------------------------
			if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3240204"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3250204"))){
				oneBean = new LazyDynaBean();
				menuList = new ArrayList();
				oneBean.set("text", ResourceFactory.getProperty("menu.gz.batch"));
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020401"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020401"))){
					if(!"1".equals(royalty_valid)){
						menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.batch.import"),"","GzGlobal.batchImport","",new ArrayList()));//批量导入
					}
				}
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020402"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020402"))){
					menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.batch.update"),"","GzGlobal.batchUpdate","",new ArrayList()));//批量修改
				}
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3240216"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3250216"))){
					menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.sortman"),"","GzGlobal.syncgzemp","",new ArrayList()));//同步人员顺序
				}
//暂时隐藏引入计件薪资的功能 zhanghua 2017-8-16
//				if((!gz_module.equals("1")&&viewtype.equals("0")&&this.userview.hasTheFunction("3242112")&&!isImportPiece.equals("0"))){
//					menuList.add(getMenuBean(ResourceFactory.getProperty("gz_new.gz_accounting.importPiece"),"","GzGlobal.importPiece","",new ArrayList()));//引入计件薪资
//				}
				oneBean.set("menu", menuList);
				list.add(oneBean);
			}
			
			//---------------------------批量处理 end-----------------------------------
			//---------------------------薪资发放---------------------------------------
			//2016-6-21 zhanghua 薪资发放项内所有子项转至主目录
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020502"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020502"))){
					if(!"1".equals(gz_module)){
						oneBean = new LazyDynaBean();
						oneBean.set("text", ResourceFactory.getProperty("menu.gz.updisk"));//银行报盘
						oneBean.set("handler", "GzGlobal.updisk");
						list.add(oneBean);
					}
				}
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020503"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020503"))||("2".equals(viewtype)&&this.userview.hasTheFunction("031403"))){
					oneBean = new LazyDynaBean();
					oneBean.set("text", ResourceFactory.getProperty("menu.gz.report"));//薪资报表
					oneBean.set("handler", "GzGlobal.gzReport");
					list.add(oneBean);
				}
				if(("0".equals(viewtype)&&this.userview.hasTheFunction("32404"))){
					if(!"1".equals(gz_module)){
						oneBean = new LazyDynaBean();
						oneBean.set("text", ResourceFactory.getProperty("menu.gz.tax"));//所得税管理
						oneBean.set("handler", "GzGlobal.searchTax");
						list.add(oneBean);
					}
				}
			//---------------------------薪资发放  end----------------------------------
			//----------------------------设置-----------------------------------------
			if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3240206"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3250206"))){
				oneBean = new LazyDynaBean();
				oneBean.set("text", ResourceFactory.getProperty("menu.gz.options"));
				menuList = new ArrayList();
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020607"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020607"))){
					if("1".equals(isShowManagerFunction)){
						ArrayList _list = new ArrayList();
						String appdate=ConstantParamter.getAppdate(this.userview.getUserName()).trim();
						String[] ym=StringUtils.split(appdate,".");					
						_list.add(getDateMenuBean("GzGlobal.setAppDate(picker,date)","/images/waiting.gif","datepicker","new Date('"+ym[0]+"','"+(Integer.parseInt(ym[1])-1)+"','"+ym[2]+"')"));
						menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.appdate"),"","treeMenu","/images/waiting.gif",_list));//设置业务日期
					}
				}
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020601"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020601"))){
					if("1".equals(isShowManagerFunction)&& "0".equals(isRedo)){
						menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.reappdate"),"","GzGlobal.reSetGzDate","/images/waiting.gif",new ArrayList()));//重置业务日期
					}
				}
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020602"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020602"))){
					if("1".equals(isShowManagerFunction)&& "0".equals(isRedo)){
						if("1".equals(gz_module))
							menuList.add(getMenuBean("重发","","GzGlobal.reDoGz","",new ArrayList()));//保险重发  ResourceFactory.getProperty("label.gz.insRedo")
						else
							menuList.add(getMenuBean("重发","","GzGlobal.reDoGz","",new ArrayList()));//薪资重发  ResourceFactory.getProperty("label.gz.gzRedo")
							
					}
				}
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020603"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020603"))){
					menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.formula"),"","GzGlobal.defineFormula","",new ArrayList()));//计算公式
				}
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020604"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020604"))){
		//			menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.template"),"","","",new ArrayList()));//邮件模板
				}
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020605"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020605"))){
					menuList.add(getMenuBean(ResourceFactory.getProperty("infor.menu.definition.shformula"),"","GzGlobal.defineSpFormula","",new ArrayList()));//审核公式
				}
				oneBean.set("menu", menuList);
				list.add(oneBean);
			}
			//----------------------------设置 end------------------------------------
			if(list.size()!=0){
				buttonList.add( this.getMenuStr(ResourceFactory.getProperty("gz_new.gz_accounting.FunctionNavigation"), list));//功能导航
				buttonList.add(splitButton);
			}
			buttonList.addAll(getAccountingButton(gzbo, buttonMap));//追加功能按钮
			ButtonInfo button = new ButtonInfo((String)buttonMap.get("lookStr"),ButtonInfo.TYPE_QUERYBOX,"GZ00000001");
			button.setType(ButtonInfo.TYPE_QUERYBOX);
			buttonList.add(button);
			if(accountingdate!=null&&accountingdate.trim().length()>0&&accountingcount!=null&&accountingcount.trim().length()>0){
				buttonList.add("->");
				buttonList.add("<b>"+ResourceFactory.getProperty("label.login.appdate")+" : "+accountingdate.substring(0, 7)+"</b>");
				buttonList.add("<b>"+ResourceFactory.getProperty("hmuster.label.counts")+" : "+accountingcount+"</b>");
				String style= "1".equals(isRedo)?"width:70px;":"width:40px;";
				String flagName=bean.get("sp_flagname")==null?"":(String) bean.get("sp_flagname");
				String lable="<b><div style='text-align:center;"+style+"' id='sp_flagname'>"+flagName;
				if("1".equals(isRedo))
					lable+="&nbsp(重发)";
				lable+="</div></b>";
				buttonList.add(lable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buttonList;
	}
	/**
	 * 获取薪资审批明细界面按钮
	 * @param accountingdate
	 * @param accountingcount
	 * @return
	 */
	public ArrayList getSalarySpButtons(String accountingdate,String accountingcount,HashMap buttonMap,String gz_module,SalaryTemplateBo gzbo){
		ArrayList buttonList = new ArrayList();
		try{
			VersionControl ver = new VersionControl();
			if((!"1".equals(gz_module)&&(this.userview.hasTheFunction("3240306")||this.userview.hasTheFunction("3270306")))||("1".equals(gz_module)&&(this.userview.hasTheFunction("3271306")||this.userview.hasTheFunction("3250306")))){
				buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.delete"),ButtonInfo.FNTYPE_DELETE,"GZ00000437"));
			}
			if((!"1".equals(gz_module)&&(this.userview.hasTheFunction("3240304")||this.userview.hasTheFunction("3270304")))||("1".equals(gz_module)&&(this.userview.hasTheFunction("3271304")||this.userview.hasTheFunction("3250304")))){
				buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.save"),ButtonInfo.FNTYPE_SAVE,"GZ00000436"));
			}
			if((!"1".equals(gz_module)&&(this.userview.hasTheFunction("3240303")||this.userview.hasTheFunction("3270303")))||("1".equals(gz_module)&&(this.userview.hasTheFunction("3271303")||this.userview.hasTheFunction("3250303")))){
				buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.reject"),"spCollectScope.reject"));//驳回
			}
			if((!"1".equals(gz_module)&&(this.userview.hasTheFunction("3270308")||this.userview.hasTheFunction("3240308")))||("1".equals(gz_module)&&(this.userview.hasTheFunction("3271309")||this.userview.hasTheFunction("3250308")))){
				buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.computer"),"spCollectScope.compute"));//计算
			}
			String allowEditSubdata=gzbo.getLprogramAttri("allow_edit_subdata",SalaryLProgramBo.CONFIRM_TYPE); //允许提交后更改数据 且 具有提交权限
			if(allowEditSubdata==null||allowEditSubdata.trim().length()==0)
			    allowEditSubdata="0";	
			if("1".equals(allowEditSubdata))
			{
				if((!"1".equals(gz_module)&&(this.userview.hasTheFunction("3240305")||this.userview.hasTheFunction("3270305")))||("1".equals(gz_module)&&(this.userview.hasTheFunction("3271305")||this.userview.hasTheFunction("3250305")))){
					buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.submit"),"spCollectScope.gbsubmit"));//个别提交
				}
			}
			ButtonInfo button = new ButtonInfo((String)buttonMap.get("lookStr"),ButtonInfo.TYPE_QUERYBOX,"GZ00000434");
			button.setType(ButtonInfo.TYPE_QUERYBOX);
			buttonList.add(button);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buttonList;
	}
	/**
	 * 获取薪资发放界面功能按钮
	 * @param gzbo
	 * @return
	 */
	public ArrayList getAccountingButton(SalaryTemplateBo gzbo,HashMap buttonMap){
		ArrayList buttonList = new ArrayList();
//		VersionControl ver = new VersionControl();
		try{
			ButtonInfo splitButton = new ButtonInfo(ButtonInfo.BUTTON_SPLIT);//占位符
			//---------------按钮参数--------------------
		    String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
		    String isShowManagerFunction="0";
			if(manager==null||manager.length()==0||this.userview.getUserName().equals(manager))
			{
				isShowManagerFunction="1";//是管理员
			}
			String salaryIsSubed="true";  //薪资是否为已提交状态
			String isEditData = "true";
//			if(gzbo.isSalaryPayed2())
				salaryIsSubed="false";
			/** 如果为操作用户同时薪资已确认的数据不让修改 */
			if(manager.length()>0&&!manager.equalsIgnoreCase(this.userview.getUserName())&& "true".equals(salaryIsSubed))
			{
				isEditData =  "false";
			}
			
			boolean canCreateNewSalary = true;
			List elementList = gzbo.getCtrlparam().getValue(com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo.FILLING_AGENCY,com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo.FILLING_AGENCYS);
			if(elementList.size() > 0) {
				canCreateNewSalary = false;//设置了指定的填报人，接下来看有没有启用
				Element element = null;
				for(int i = 0; i < elementList.size(); i++) {
					element=(Element)elementList.get(i);
					String username =  StringUtils.isBlank(element.getAttributeValue("username"))?"":element.getAttributeValue("username");
					String enable =  StringUtils.isBlank(element.getAttributeValue("enable"))?"":element.getAttributeValue("enable");
					if(username.equalsIgnoreCase(userview.getUserName()) && "1".equals(enable)) {
						canCreateNewSalary = true;
						break;
					}
				}
			}
			
			String gz_module = (String)buttonMap.get("gz_module");
			String viewtype = (String)buttonMap.get("viewtype");// 页面区分 0:薪资发放  1:审批  2:上报
			//------------------------------------------
			//如果应用机构指定了填报人，只有已经启用的填报人才能新建账套，这是非共享
			if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020301"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020301"))){
				//如果是非共享账套的管理员，非共享账套manage的长度为0，共享账套管理员不受限制，下发给填报人的时候，填报人受限制能不能填报数据
				if(((manager==null||manager.length()==0) && canCreateNewSalary) || this.userview.getUserName().equals(manager)){
					buttonList.add(new ButtonInfo(ResourceFactory.getProperty("menu.gz.new"),"GzGlobal.newSalaryTable"));
				}
			}
			if("true".equals(isEditData)){
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3240207"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3250207"))){
					//buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.delete"),ButtonInfo.FNTYPE_DELETE,"GZ00000026"));
					buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.delete"),"GzGlobal.deleteData"));
				}
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3240202"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3250202"))|| "2".equals(viewtype)){
					ButtonInfo savebutton = new ButtonInfo(ResourceFactory.getProperty("button.save"),ButtonInfo.FNTYPE_SAVE,"GZ00000025");
					savebutton.setId("salaryaccountingsave");
					buttonList.add(savebutton);
				}

				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020403"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020403"))||("2".equals(viewtype)&&this.userview.hasTheFunction("031402"))){
					buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.computer"),"GzGlobal.compute"));//计算
				}
				if(buttonList.size()>0)
					buttonList.add(splitButton);
			}
			//只有共享管理员才能下发并且设置了应用机构才行
			if(elementList.size() > 0 && (StringUtils.isNotBlank(manager)&&this.userview.getUserName().equals(manager)) && ((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3240226"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3250226")))){
				buttonList.add(new ButtonInfo(ResourceFactory.getProperty("reportManager.distribute"),"GzGlobal.distribute"));//下发
			}
			int tempnum=buttonList.size();
			if(("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3250210"))||(!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3240213"))||("2".equals(viewtype)&&this.userview.hasTheFunction("031406"))){
				buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.audit"),"GzGlobal.verify"));//审核
			}
			if("1".equals(isShowManagerFunction)){
				String appflag = (String) buttonMap.get("appflag");
				if("true".equalsIgnoreCase(appflag)&&((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3240208"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3250208")))){
					String appealName = ResourceFactory.getProperty("button.appeal");
					String spActorName = (String) buttonMap.get("spActorName");
					if(spActorName!=null&&spActorName.length()>0)//如果此人只有一个领导那么报批按钮上面显示 报[xxx]审批 否则单纯显示报批
						appealName = "报["+spActorName+"]审批";
					ButtonInfo btn = new ButtonInfo(appealName,"GzGlobal.appeal");
					btn.setId("gzappeal");
					buttonList.add(btn);//报批
				}
				String bedit = (String) buttonMap.get("bedit");
				if("true".equals(bedit)&&((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020504"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020504")))){
					buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.submit"),"GzGlobal.submit"));//提交
				}
			}
			if(manager!=null&&manager.length()>0&& "1".equals(isShowManagerFunction)&&!"2".equals(viewtype))//数据上报不要驳回，驳回只能在薪资发放里面
		 	{
				buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.reject"),"GzGlobal.gzReject"));//驳回
		 	}
			if("0".equals(isShowManagerFunction)){//非管理员用户即可使用报审功能 zhanghua 2017-8-15 30599
			//if(viewtype.equals("2")&&isShowManagerFunction.equals("0")){//数据上报直接进来 仅有数据上报页面有报审按钮
				if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3240225"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3250225"))||("2".equals(viewtype)&&this.userview.hasTheFunction("031401"))){
					buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.report"),"GzGlobal.gzAproval"));//报审
				}
			}
			if(tempnum!=buttonList.size())
				buttonList.add(splitButton);
			if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3240224"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("3250224"))){
				buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.reimport"),"GzGlobal.reimport"));//重新导入
			}
			if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020505"))
					//||(gz_module.equals("1")&&viewtype.equals("0")&&this.userview.hasTheFunction("325020505"))
					){
				buttonList.add(new ButtonInfo(ResourceFactory.getProperty("menu.gz.sendmessage"),"GzGlobal.sendMsg"));//发送通知
			}
			String returnflag = (String)buttonMap.get("returnflag");


			if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("324020503"))||("1".equals(gz_module)&& "0".equals(viewtype)&&this.userview.hasTheFunction("325020503"))||("2".equals(viewtype)&&this.userview.hasTheFunction("031403"))){

				SalaryReportBo salaryReportBo=new SalaryReportBo(this.conn,String.valueOf(gzbo.getSalaryid()),this.userview);

				ArrayList<LazyDynaBean> reportList=salaryReportBo.listCommonReport(gz_module,"0");
				StringBuffer bottoStr=new StringBuffer("<jsfn>{xtype:'button',id:'common_Report_button',");
				if(reportList.size()==1) {
					bottoStr.append("text:'"+reportList.get(0).get("text")+"',hidden:false,");
				}else {
					String hidden="false";
					if (reportList.size() == 0) {
						hidden = "true";
					}
					bottoStr.append("html:'常用报表<img style=\"width:10px;\" src=\"/ext/ext6/resources/images/button/arrow.gif\"/>',hidden:" + hidden + ",");
				}
				bottoStr.append("handler:accounting.openCommon_reportCombo");
				bottoStr.append("}</jsfn>");
				buttonList.add(bottoStr.toString());
			}


			if(!"noreturn".equalsIgnoreCase(returnflag)){
				buttonList.add(new ButtonInfo(ResourceFactory.getProperty("button.return"),"GzGlobal.returnBack"));//返回
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buttonList;
	}
	/**
	 * 递归生成功能导航菜单的json串
	 * @param name 菜单名
	 * @param list 菜单内容
	 * @return
	 */
	public String getMenuStr(String name,ArrayList list){
		StringBuffer str = new StringBuffer();
		try{
			if(name.length()>0){
				str.append("<jsfn>{xtype:'button',text:'"+name+"'");
			}
			str.append(",menu:{items:[");
			for(int i=0;i<list.size();i++){
				LazyDynaBean bean = (LazyDynaBean) list.get(i);
				if(i!=0)
					str.append(",");
				str.append("{");
				if(bean.get("xtype")!=null&&bean.get("xtype").toString().length()>0)
					str.append("xtype:'"+bean.get("xtype")+"'");
				if(bean.get("text")!=null&&bean.get("text").toString().length()>0)
					str.append("text:'"+bean.get("text")+"'");
				if(bean.get("id")!=null&&bean.get("id").toString().length()>0)
					str.append(",id:'"+bean.get("id")+"'");
				if(bean.get("handler")!=null&&bean.get("handler").toString().length()>0){
					if(bean.get("xtype")!=null&& "datepicker".equalsIgnoreCase(bean.get("xtype").toString())){//时间控件单独处理一下 方法GzGlobal.aaa(picker, date)这样写
						str.append(",todayTip:''");//消除今天 按钮提示文字
						str.append(",handler:function(picker, date){"+bean.get("handler")+";}");
					}else{
						str.append(",handler:function(){"+bean.get("handler")+"();}");
					}				
				}
				if(bean.get("icon")!=null&&bean.get("icon").toString().length()>0)
					str.append(",icon:'"+bean.get("icon")+"'");
				if(bean.get("value")!=null&&bean.get("value").toString().length()>0)
					str.append(",value:"+bean.get("value")+"");
				ArrayList menulist = (ArrayList)bean.get("menu");
				if(menulist!=null&&menulist.size()>0){
					str.append(this.getMenuStr("", (ArrayList)bean.get("menu")));
				}
				str.append("}");
			}
			str.append("]}");
			if(name.length()>0){				
				str.append("}</jsfn>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str.toString();
	}
	/**
	 * 生成菜单的bean
	 * @param text 名称
	 * @param id 主键
	 * @param handler 触发事件
	 * @param icon 图标
	 * @return
	 */
	public LazyDynaBean getMenuBean(String text,String id,String handler,String icon,ArrayList list){
		LazyDynaBean bean = new LazyDynaBean();
		try{
			if(text!=null&&text.length()>0)
				bean.set("text", text);
			if(id!=null&&id.length()>0)
				bean.set("id", id);
			if(icon!=null&&icon.length()>0)
				bean.set("icon", icon);
			if(handler!=null&&handler.length()>0){
				if(list!=null&&list.size()>0){
					bean.set("menu", list);
				}else{
					bean.set("handler", handler);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * 生成菜单的bean
	 * @param text 名称
	 * @param handler 触发事件
	 * @param icon 图标
	 * @return
	 */
	public LazyDynaBean getDateMenuBean(String handler,String icon,String xtype,String value){
		LazyDynaBean bean = new LazyDynaBean();
		try{
			if(xtype!=null&&xtype.length()>0)
				bean.set("xtype", xtype);
			if(value!=null&&value.length()>0)
				bean.set("value", value);
			if(handler!=null&&handler.length()>0)
				bean.set("handler", handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}
	//
	public LazyDynaBean getGzLog(String ff_bosdate,String ff_count,String username,String salaryid)
	{
		LazyDynaBean bean = new LazyDynaBean();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ff_bosdate = ff_bosdate.replaceAll("\\.", "-");
			String[] temps=ff_bosdate.split("-");
			String sql="select * from gz_extend_log where salaryid="+salaryid+" and lower(username)='"+username.toLowerCase()+"' and "+Sql_switcher.year("a00z2")+"="+temps[0]+" and "+Sql_switcher.month("a00z2")+"="+temps[1]+" and a00z3="+ff_count;
			RowSet rs=dao.search(sql);
			if(rs.next())
			{
				if(rs.getInt("isredo")==1)
					bean.set("isredo", 1);
				bean.set("sp_flag", rs.getString("sp_flag"));
				bean.set("sp_flagname", AdminCode.getCodeName("23", rs.getString("sp_flag")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	}

}
