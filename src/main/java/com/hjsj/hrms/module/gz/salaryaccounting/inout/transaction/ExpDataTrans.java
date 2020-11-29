package com.hjsj.hrms.module.gz.salaryaccounting.inout.transaction;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.gz.salaryaccounting.inout.businessobject.SalaryInOutBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.utils.SalarySetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *Title:ExpDataTrans
 *Description:薪资发放导出
 *Company:HJHJ
 *Create time:2015-7-3 
 *@author lis
 */
public class ExpDataTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid =PubFunc.decrypt(SafeCode.decode(salaryid)); //解密
			String appdate =(String)this.getFormHM().get("appdate");
			appdate=PubFunc.decrypt(SafeCode.decode(appdate));
			String imodule=(String)this.getFormHM().get("imodule");//薪资和保险区分标识  1：保险  否则是薪资
			imodule=PubFunc.decrypt(SafeCode.decode(imodule));
			//判读用户是否有此薪资类别的权限
			safeBo.isSalarySetResource(salaryid,null);
			
			String flag=(String)this.getFormHM().get("flag");//是否是薪资审批
			flag=StringUtils.isBlank(flag)?"":flag;
			
		    String type=(String)this.getFormHM().get("type");   //1: excel  2.text
		    //导出的薪资项目，用“/”分割
		    String itemids=((String)this.getFormHM().get("itemids")).toUpperCase()+"/";
		    
			
			Connection conn = this.getFrameconn();
			SalaryTemplateBo gzbo = new SalaryTemplateBo(conn, Integer.valueOf(salaryid), this.userView);
			ArrayList<LazyDynaBean> salaryItemList = gzbo.getSalaryItemList("", salaryid, 1);// 取得所有薪资项目
			SalaryInOutBo inOutBo = new SalaryInOutBo(conn, Integer.valueOf(salaryid), this.userView);
			SalarySetBo setbo = new SalarySetBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String flow_flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");//是否走审批1：是
			if("1".equalsIgnoreCase(flow_flag)) {//导出审批意见
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("itemid", "appprocess");
				abean.set("itemdesc", "审批意见");
				abean.set("itemtype", "M");
				abean.set("codesetid", "0");
				abean.set("decwidth", "0");
				abean.set("displaywidth", 7000);
				salaryItemList.add(0,abean);
			}
			String sql = null;
			String tabelName = null;
			
			//----------------------------根据栏目设置进行排序--------------------
			String tName="";
			if("sp".equals(flag))
				tName="salaryspdetail_"+salaryid;
			else if("sptotal".equals(flag))
				tName="salarysp_"+salaryid;
			else
				tName="salary_"+salaryid;
			
			//栏目设置已经存在，则从数据库中取
			int schemeId = gzbo.getSchemeId(tName);
			// 从数据库中得到可以显示的薪资项目代码
			if(schemeId > 0){
				ArrayList<HashMap> itemIdList = gzbo.getTableItemsToMap(schemeId,"");
				salaryItemList = gzbo.getSchemedHeadHashMap(salaryItemList, itemIdList);
			}else{
				//如果没有栏目设置，则将所有数值型项目设置导出合计
				for(LazyDynaBean bean :salaryItemList){
					if("N".equalsIgnoreCase((String)bean.get("itemtype"))&&
							!"a00z1".equalsIgnoreCase((String)bean.get("itemid"))&&!"a00z3".equalsIgnoreCase((String)bean.get("itemid")))
						bean.set("is_sum", "1");
				}
			}
			//---------------------------排序结束---------------------------
			if("sp".equals(flag)||"sptotal".equals(flag))
				tabelName="salaryhistory";
			else
				tabelName=gzbo.getGz_tablename();
			if("sp".equals(flag)){
//				String accountingdate = (String)this.getFormHM().get("appdate"); 
//			    accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
			    String accountingcount = (String)this.getFormHM().get("count"); 
			    accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
			    String cound = (String) this.getFormHM().get("cound");
				sql = inOutBo.getSpSql(appdate, accountingcount,cound, gzbo,setbo, true);
				
			}else if("sptotal".equals(flag)){//审批导出合计
				ArrayList<MorphDynaBean> list=(ArrayList)this.getFormHM().get("dataList");//页面数据
				String [] itemidList=itemids.replaceFirst("/", "").split("/");//导出项
				ArrayList<String> itemText=(ArrayList)this.getFormHM().get("itemText");//页面显示列名
				ArrayList<LazyDynaBean> dataList=new ArrayList<LazyDynaBean>();
				for(MorphDynaBean bean : list){
					LazyDynaBean nbean=new LazyDynaBean();
					for(String str:itemidList){
						if("text".equalsIgnoreCase(str)|| "num".equalsIgnoreCase(str)){//text,num 在数据集中小写显示
							if("text".equalsIgnoreCase(str)){//添加树层级
								String space="";
								int num=Integer.parseInt(bean.get("depth").toString())-1;
								while(num>0){
									space=space+"  ";
									num--;
								}
								nbean.set(str, space+bean.get(str.toLowerCase()));
							}else
							nbean.set(str, bean.get(str.toLowerCase()));
						}
						else
							nbean.set(str, bean.get(str));
					}
					dataList.add(nbean);
				}
				String fileName="";
				//导出excel
				fileName=inOutBo.exportFile(tabelName, dataList,itemidList,itemText,appdate,imodule);
				this.getFormHM().put("fileName",SafeCode.encode(PubFunc.encrypt(fileName)));
				this.getFormHM().put("flag",flag);
				return;
			}
			else{
				TableDataConfigCache tableCache = (TableDataConfigCache) this.userView.getHm().get(tName);
				String sortSql = tableCache.getSortSql();// 取得oder by
				StringBuffer strSql=new StringBuffer("select ");
				sql = (String)tableCache.get("combineSql");
				sql=sql.replace("*","myGridData.*");//由于导出组件会加个序号列，因此不能直接用*
				if(Sql_switcher.searchDbServer()==2) {//是oracle
					strSql.append(" rownum,fff.* ");
					strSql.append(" from (").append(sql).append(sortSql).append(") fff ");
				}else{
					strSql.append(" ROW_NUMBER() OVER("+sortSql+") as rownum,fff.* ");
					strSql.append(" from (").append(sql).append(") fff ");
					strSql.append(sortSql);
				}
				sql=strSql.toString();
			}
			
			String fileName="";
			
			//导出excel
			fileName=inOutBo.exportFile(tabelName, sql, type, itemids, salaryItemList,appdate,imodule);
			this.getFormHM().put("fileName",SafeCode.encode(PubFunc.encrypt(fileName)));
			this.getFormHM().put("flag",flag);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
