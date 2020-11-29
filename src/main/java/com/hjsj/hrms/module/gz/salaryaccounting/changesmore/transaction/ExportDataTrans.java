package com.hjsj.hrms.module.gz.salaryaccounting.changesmore.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.changesmore.businessobject.ChangesmoreBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryPropertyBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：ExportDataTrans
 * 类描述：上期数据比对 excel导出
 * 创建人：zhanghua
 * 创建时间：2016-11-09
 * @version
 */

public class ExportDataTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try {
			String salaryid = (String) this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			
			ChangesmoreBo bo = new ChangesmoreBo(this.getFrameconn(), Integer.parseInt(salaryid), userView);
			
			String imodule = (String) this.getFormHM().get("imodule");
			imodule = PubFunc.decrypt(SafeCode.decode(imodule));
			/**type=1 薪资发放中的数据比对  =2薪资审批中的数据比对**/
			String type = (String) this.getFormHM().get("type");
			/**业务日期**/
			String appdate = (String) this.getFormHM().get("appdate");
			appdate = PubFunc.decrypt(SafeCode.decode(appdate));
			/**次数**/
			String count = (String) this.getFormHM().get("count");
			count = PubFunc.decrypt(SafeCode.decode(count));
			
			String changeflags = (String) this.getFormHM().get("changeflags");
			
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			boolean ispriv=false;
			if(gzbo.getManager()!=null&&gzbo.getManager().length()>0&&!this.userView.getUserName().equalsIgnoreCase(gzbo.getManager()))//共享非管理员
				ispriv=true;
			
			ArrayList<LazyDynaBean> salaryItems = bo.getSalaryTemplateBo().getSalaryItemList("", salaryid, 1);
			ArrayList<LazyDynaBean> fieldItems=new ArrayList<LazyDynaBean>();
			for(LazyDynaBean bean:salaryItems)//获取待比对指标。 仅取非系统项数字型指标
				if(!"3".equals(bean.get("initflag").toString())&& "N".equals(bean.get("itemtype").toString()))
					fieldItems.add(bean);
			
			//拼接组织机构查询条件
			String selectID = (String) this.getFormHM().get("selectID");
			String deptSql="";
			SalaryPropertyBo PropertyBo=new SalaryPropertyBo(this.getFrameconn(),salaryid,0,this.getUserView());
			String deptid=PropertyBo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
			String orgid=PropertyBo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");//归属单位
			if(selectID!=null&&selectID.trim().length()>0){
				deptSql=bo.getDeptSqlWhere(selectID, deptid,orgid);
			}
			String tableName= "1".equals(type)?gzbo.getGz_tablename():"SALARYHISTORY";
			ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
			ArrayList dataListNew=new ArrayList();
			if("0".equals(changeflags.trim())){//主页面导出
				//取得发放日期 次数和权限条件
				ArrayList<String> arrStr=bo.getSqlWhere(tableName,appdate, count, type,ispriv);
				String strWhere=arrStr.get(0);//本次发放日期
				String strWhereNow=arrStr.get(1);//上次发放日期
				String tableNameOld=bo.isArchive(strWhere);
				if("salaryarchive".equalsIgnoreCase(tableNameOld))
					strWhere=strWhere.replaceAll("salaryhistory", "salaryarchive");
				ArrayList dataList=bo.getChangeMainDataList(tableName,tableNameOld, type, fieldItems,deptSql,strWhere,strWhereNow);
				String [] fieldName={"itemdesc","lastdata","nowdata","peoplenum","margin"};
				
				for(Object obj:dataList){//转换格式
					LazyDynaBean bean=(LazyDynaBean)obj;
					LazyDynaBean newbean=new LazyDynaBean();
					for(String str:fieldName){
						LazyDynaBean contbean=new LazyDynaBean();
						contbean.set("content", bean.get(str).toString().replace(",",""));//存在千分位逗号，删除掉
						newbean.set(str, contbean );
					}
					dataListNew.add(newbean);
				}
				
				columnsInfo = bo.getColumnListForExc();


			}
			else{//明细页面导出
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
				String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
				if(StringUtils.isBlank(display_e0122)|| "00".equals(display_e0122))
					display_e0122="0";		
				String fieldItem = (String) this.getFormHM().get("fieldItem");
				if(fieldItem.trim().length()<=0)
					return;
				String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//是否定义唯一性指标 0：没定义
				String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标值
				if(!"0".equals(uniquenessvalid))
					onlyname=bo.getOnlyName(onlyname);
				else
					onlyname="";
				if("a0101".equalsIgnoreCase(onlyname))
					onlyname="";
				//获取数据
				ArrayList dataList=bo.getChangeDetailsDataList(tableName, appdate, count, type, fieldItem, deptSql, deptid, orgid,ispriv,onlyname);
				/** 获取列头 */
				columnsInfo = bo.getExcelColumnListDetail(deptid,orgid,onlyname);
				
				for(Object obj:dataList){//转换格式
					LazyDynaBean bean=(LazyDynaBean)obj;
					LazyDynaBean newbean=new LazyDynaBean();
					for(ColumnsInfo col:columnsInfo){
						LazyDynaBean contbean=new LazyDynaBean();
						String codesetid=col.getCodesetId();
						String content="";
						String value=bean.get(col.getColumnId()).toString();
						content=value;
//						
//						//将组织机构代码 转换成名称
//						if(!StringUtils.isBlank(codesetid)){
//						    if("un".equalsIgnoreCase(codesetid)){
//						        content = AdminCode.getCodeName("UN", value);
//						        if(StringUtils.isBlank(content))
//						            content = AdminCode.getCodeName("UM", value);
//						    } 
//						    else if("um".equalsIgnoreCase(codesetid))//此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值  
//							{							
//								if("e0122".equalsIgnoreCase(col.getColumnId()))
//								{
//									if(Integer.parseInt(display_e0122)==0)
//										content=AdminCode.getCodeName("UM",value);
//									else
//									{
//										CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
//						    	    	if(item!=null)
//						    	    	{
//						    	    		content=item.getCodename();
//						        		}
//						    	    	else
//						    	    	{
//						    	    		content=AdminCode.getCodeName("UM",value);
//						    	    	}
//									}								
//								}else
//									content = (AdminCode.getCodeName("UM",value)==null || (AdminCode.getCodeName("UM",value)!=null && AdminCode.getCodeName("UM",value).trim().length()==0))?AdminCode.getCodeName("UN",value): AdminCode.getCodeName("UM",value);	
//							}
//						}
						contbean.set("content", content);
						newbean.set(col.getColumnId(), contbean );
					}
					dataListNew.add(newbean);
				}
			}
			String fileName = "gz_"+this.userView.getUserName()+".xls";
			ExportExcelUtil excelUtil = new ExportExcelUtil(this.getFrameconn(),this.userView);
			/**excel数据头部列从第几行开始**/
			int headStartRowNum = 0;
			// 导出excel
			excelUtil.setConvertToZero(false);
			excelUtil.exportExcel(fileName,"对比表",null, excelUtil.getHeadListByColum(columnsInfo),dataListNew, null,headStartRowNum);
			this.getFormHM().put("fileName",SafeCode.encode(PubFunc.encrypt(fileName)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
