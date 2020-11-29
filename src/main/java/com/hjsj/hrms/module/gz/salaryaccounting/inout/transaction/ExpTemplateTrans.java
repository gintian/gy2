package com.hjsj.hrms.module.gz.salaryaccounting.inout.transaction;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.inout.businessobject.SalaryInOutBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.module.gz.utils.SalarySetBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
* @ClassName: ExpTemplateTrans 
* @Description: TODO(薪资导出模板) 
* @author lis 
* @date 2015-7-21 上午09:53:29
*/
public class ExpTemplateTrans extends IBusiness
{

	@Override
    public void execute() throws GeneralException
	{
		try
		{
			String salaryid = (String) this.getFormHM().get("salaryid");//薪资类别id
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid)); //解密
			ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();//可以导出的薪资项目
			
			//如果用户没有当前薪资类别的资源权限   20140915  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			String flag=(String)this.getFormHM().get("flag");//是否是薪资审批
			
			String type_format=(String)this.getFormHM().get("type_format");//xls格式 |xlsx格式
		    
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.valueOf(salaryid),this.userView);
			SalaryInOutBo inOutBo = new SalaryInOutBo(this.getFrameconn(), Integer.valueOf(salaryid), this.userView);
			SalarySetBo setbo = new SalarySetBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String tableName = null;
			String sqlStr = null;
			
			if("sp".equals(flag)){
				String accountingdate = (String)this.getFormHM().get("appdate"); 
			    accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
			    String accountingcount = (String)this.getFormHM().get("count"); 
			    accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
			    String cound = (String) this.getFormHM().get("cound");
			    //导出模板不需要序列号
			    sqlStr = inOutBo.getSpSql(accountingdate, accountingcount,cound, gzbo,setbo, false);
			    tableName="salaryspdetail_"+salaryid;
//			    tableName = PubFunc.encrypt("salaryhistory"); //加密
//				tableName = SafeCode.encode(tableName); //编码
			}else{
//				tableName = PubFunc.encrypt(gzbo.getGz_tablename()); //加密
//				tableName = SafeCode.encode(tableName); //编码
				tableName = "salary_"+salaryid; //加密
		//		TableDataConfigCache configCache = (TableDataConfigCache)this.userView.getHm().get(tableName);
				//xiegh 20170411 update bug 26774
				TableDataConfigCache configCache = (TableDataConfigCache)this.userView.getHm().get(tableName);
				
			    String tableSql = (String)configCache.getTableSql();//取得导出数据的sql
				String sortSql  = (String)configCache.getSortSql();//取得oder by
				String filterSql = gzbo.getfilter(gzbo.getGz_tablename());
				sqlStr = tableSql+" "+filterSql+" "+sortSql;
			}
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
			//系统设置唯一性指标代码
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
			if(uniquenessvalid==null|| "0".equals(uniquenessvalid))
				onlyname="";
			
			HashMap calcuItemMap = inOutBo.getCalcuItemMap();//公式计算项
			ArrayList<LazyDynaBean> itemSetList = gzbo.getSalaryItemList(null, salaryid,1);//所有薪资项目
			ArrayList<LazyDynaBean> headItemList = null;//可以导出的薪资项目
			//ArrayList<FieldItem> fieldlist=setbo.searchGzItem();//薪资项目	修改薪资导出模板先选择指标再进行导出
			//ArrayList<ColumnsInfo> column = setbo.toColumnsInfo(fieldlist);//页面显示字段  
			
			String itemids=((String)this.getFormHM().get("itemids")).toUpperCase();//获取页面选择的字段 sunjian 2017-05-31
			headItemList = setbo.toShowColumnsInfo(itemSetList,itemids,onlyname);//根据这个进行确定最后显示的字段 sunjian 2017-05-31
			//栏目设置已经存在，则从数据库中取
			int schemeId = gzbo.getSchemeId(tableName);
			//headItemList = inOutBo.getHeadItemList(itemSetList, column);
			// 从数据库中得到可以显示的薪资项目代码
			if(schemeId > 0){
				ArrayList<HashMap> itemIdList = gzbo.getTableItemsToMap(schemeId,"");
				headItemList = gzbo.getSchemedHeadHashMap(headItemList, itemIdList);
			}
			
			boolean _flag=true;//判断是否该显示唯一性指标（如果隐藏了唯一指标  那么导出模版我直接写死导出就带这列  如果帐套里面就没有这个指标  那么我把主键导出来） zhaoxg 2013-11-26
			for(int j =0;j<itemSetList.size();j++){
				LazyDynaBean bean = (LazyDynaBean)itemSetList.get(j);
				if(((String)bean.get("itemid")).equalsIgnoreCase(onlyname)){
					_flag=false;
					continue;
				}
			}
			//从选择指标进行导出，这里不判断excel_template_limit和excel_limits了
			//headList = getHeadList(headItemList, calcuItemMap);
			//是否是提成工资
			String  royalty_valid=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
			//得到导出excel列名数据
			headList = inOutBo.getHeadList(headItemList, onlyname, _flag, royalty_valid);
			if("1".equals(royalty_valid)){
				//提成工资关联指标
				String royalty_relation_fields=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
				//将提成工资关联指标追加到导出项目list
				headList = appendHeadList(headList,royalty_valid, royalty_relation_fields, itemSetList);
			}
			
			//得到要导出的数据
			//得到下拉数据
			HashMap dropDownMap = inOutBo.getDropDataList(inOutBo.getCodeCols(headList));
			ExportExcelUtil excelUtil = new ExportExcelUtil(this.getFrameconn(),this.userView);
			//去掉这个，使excel可以不用通过锁定表格进行拉伸  sunjian
			//excelUtil.setProtect(true);
			//导出excel名称
			
			String outName =this.userView.getUserName()+"_gz_template." + type_format;
			
			//不是北京移动，且系统没有设置唯一性指标，或薪资发放项目指标中没有唯一性指标，且不是是提成工资
			if (!(SystemConfig.getPropertyValue("clientName") != null && "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
					&& (onlyname == null || onlyname.trim().length() == 0 || _flag)
					&& !"1".equals(royalty_valid)) {
				String sqlcolumn = sqlStr.substring(0,sqlStr.indexOf("from"));
				if(sqlcolumn.length() > 0){//生成“主键标示串”对应的数据
					StringBuffer sql = new StringBuffer(sqlcolumn);
					sql.append(",NBASE" + Sql_switcher.concat() + "'|'" + Sql_switcher.concat());
					sql.append("A0100" + Sql_switcher.concat() + "'|'" + Sql_switcher.concat());
					sql.append(Sql_switcher.dateToChar("A00Z0","YYYY-MM-DD") + Sql_switcher.concat() + "'|'" + Sql_switcher.concat());
					sql.append(Sql_switcher.numberToChar("A00Z1") + " as key_id ");
					sql.append(sqlStr.substring(sqlStr.indexOf("from"),sqlStr.length()));
					sqlStr = sql.toString();
				}
			}
			
			//导出excel
			excelUtil.exportExcelBySql(outName,"",null, headList,sqlStr, dropDownMap,0);
			outName = PubFunc.encrypt(outName); //解密
			outName = SafeCode.encode(outName); //解码
			this.getFormHM().put("outName", outName);
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * @Title: appendHeadList 
	 * @Description: TODO(将提成工资关联指标追加到导出项目list) 
	 * @param royalty_valid 是否是提成工资
	 * @param royalty_relation_fields 提成工资关联指标，以”,“连接
	 * @param headList 可以导出的薪资项目
	 * @param itemSetList 所有的的薪资项目
	 * @return ArrayList
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-7-25 下午04:44:30
	 */
	private ArrayList appendHeadList(ArrayList<LazyDynaBean> headList,String royalty_valid,String royalty_relation_fields,ArrayList<LazyDynaBean> itemSetList) throws GeneralException{
		try {
			
			//如果是提成工资，关联指标必须输出
			if("1".equals(royalty_valid)&&royalty_valid.trim().length()>0)
			{
				StringBuffer buf=new StringBuffer(",");
				for (int i = 0; i < headList.size(); i++)
				{
					LazyDynaBean bean = headList.get(i);
					buf.append(((String)bean.get("itemid")).toLowerCase()+",");
				}
				String[] temps=royalty_relation_fields.toLowerCase().split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						if(buf.indexOf(","+temps[i]+",")==-1)//如果在导出的薪资项目中不存在，则要添加上
						{
							for(int j=0;j<itemSetList.size();j++)
							{
								LazyDynaBean bean = itemSetList.get(j);
								if(((String)bean.get("itemid")).toLowerCase().equals(temps[i]))
								{
									headList.add(bean);
									break;
								}
							}
						}
					}
				}
			}
			return headList;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * @Title: getHeadList 
	 * @Description: TODO(过滤以后返回可以导出的薪资项目) 
	 * @param headItemList 可以显示的薪资项目
	 * @param calcuItemMap 公式计算项
	 * @return ArrayList
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-7-25 下午04:36:01
	 */
	private ArrayList<LazyDynaBean> getHeadList(ArrayList<LazyDynaBean> headItemList,HashMap calcuItemMap) throws GeneralException{
		try {
			ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
			// export_limits:设置可以导出的只读指标项
			HashMap readOnlyFldsCanExport = new HashMap();
			String export_limits = SystemConfig.getPropertyValue("export_limits");
			if (StringUtils.isNotBlank(export_limits))
			{
				String[] readOnlyFlds = export_limits.split(",");
				for (int m = 0; m < readOnlyFlds.length; m++)
				{
					String temp = readOnlyFlds[m].trim();
					if (temp.length() > 0)
						readOnlyFldsCanExport.put(temp.toUpperCase(), "");
				}
			}
			
			String initFlag = null;
			for(int i=0;i<headItemList.size();i++){
				LazyDynaBean bean = headItemList.get(i);
				FieldItem item=DataDictionary.getFieldItem((String)bean.get("itemid"));
				initFlag = (String)bean.get("initflag");
				//排除系统项，但包含姓名、单位、部门
				if("3".equals(initFlag) && !"B0110".equals((String)headItemList.get(i).get("itemid")) && !"E0122".equals((String)headItemList.get(i).get("itemid")) && !"A0101".equals((String)headItemList.get(i).get("itemid")))
					continue;
				if (SystemConfig.getPropertyValue("excel_template_limit") != null && "true".equalsIgnoreCase(SystemConfig.getPropertyValue("excel_template_limit")))
				{
					// 去除公式计算项了
					if (calcuItemMap.get(((String)bean.get("itemid")).toLowerCase()) != null)
						continue;
					
					// 去除只读项
					if(item!=null)
					{
						String pri = this.userView.analyseFieldPriv((String)bean.get("itemid"));
						if ("1".equals(pri))// 只读
						{
							if (readOnlyFldsCanExport.size() > 0)
							{
								// 不属于允许导出的只读项
								if (readOnlyFldsCanExport.get(((String)bean.get("itemid")).toUpperCase()) == null)
									continue;
							} else
								// 没有设置允许导出的只读项
								continue;
						}
					}
				}
				headList.add(headItemList.get(i));
			}
			return headList;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
}
