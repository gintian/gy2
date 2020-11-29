package com.hjsj.hrms.module.gz.salarytype.transaction;

import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 薪资类别 主界面初始化
 * @createtime July 02, 2015 9:07:55 PM
 * @author chent
 *
 */
public class SalaryTypeTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		RowSet rs = null;
		try {
			SalaryTypeBo salaryTypeBo = new SalaryTypeBo(this.getFrameconn(), this.userView);// 工具类
			String cnameLength = "0";
			String url = "";
			if(!StringUtils.isEmpty((String)this.getFormHM().get("url"))){
				url = (String)this.getFormHM().get("url");
			}else{//快速查询
				MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("customParams");
				url = (String)bean.get("url");
			}
			
			String imodule = salaryTypeBo.getValByStr(url, "imodule");// 0:薪资  1:保险
			String returnvalue = salaryTypeBo.getValByStr(url, "returnvalue");// 页面源  1：左侧导航树 2：图形菜单 3：首页待办 4：外部系统待办
			
			// 模块id
			ArrayList<String> valuesList = new ArrayList<String>();
			String subModuleId = (String) this.getFormHM().get("subModuleId");
			if("gz_salaryType_00000001".equals(subModuleId)){
				// 查询类型，1为输入查询，2为方案查询
				String type = (String) this.getFormHM().get("type");
				if("1".equals(type)) {
					// 输入的内容
					valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
				}
			}
			
			//新增薪资类别时，根据数据库字段长度限制输入名称长度，lis，2016-11-16
			ContentDAO dao = new ContentDAO(this.frameconn);
			rs = dao.search("select * from salarytemplate where 1=2");
			ResultSetMetaData data=rs.getMetaData();
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
					String columnName=data.getColumnName(i).toLowerCase();
					if("cname".equals(columnName)){
						cnameLength=data.getColumnDisplaySize(i)+"";
						break;
					}
			 }
			 if(rs!=null){
				 rs.close();
			 }
			 
			/** 获取列头 */
			ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
			columnsInfo = salaryTypeBo.getColumnList(imodule);

			//根据数据库字段长度限制输入名称长度
			for (ColumnsInfo info : columnsInfo) {
				if("cname".equalsIgnoreCase(info.getColumnId())){
					if(Integer.parseInt(cnameLength)>0){
						info.setColumnLength(Integer.parseInt(cnameLength));
					}
					break;
				}
			}
			
			/** 获取数据 */
			ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
			dataList = salaryTypeBo.getDataList(imodule, valuesList);
			String CurrentPage = (String) this.getFormHM().get("CurrentPage");
			HashMap map = new HashMap();//存放各种参数，用来传送到其他方法中 zhaoxg add 2016-3-4
			map.put("imodule", imodule);
			/** 加载表格 */
			TableConfigBuilder builder = new TableConfigBuilder("salaryType", columnsInfo, "salaryType", userView, this.getFrameconn());
			builder.setCurrentPage(Integer.parseInt(CurrentPage==null?"1":CurrentPage));
			builder.setDataList(dataList);
			builder.setAutoRender(true);
			builder.setEditable(true);
			boolean commissionFlag = false;//xiegh 20170412 bug 25544 add 提成标识
			if("0".equals(imodule)){
				builder.setTitle(ResourceFactory.getProperty("label.gz.salarytype"));
				commissionFlag = this.userView.hasTheFunction("324080802");
			}else{
				builder.setTitle(ResourceFactory.getProperty("label.gz.instype"));
			}
			builder.setSetScheme(false);
			builder.setSelectable(true);
			builder.setTableTools(salaryTypeBo.getButtonList(returnvalue,map));
			builder.setPageSize(20);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
			this.getFormHM().put("cnameLength", cnameLength);
			this.getFormHM().put("imodule", imodule);
			this.getFormHM().put("commissionFlag", commissionFlag);
			this.getFormHM().put("reNamePriv", ((!"1".equals(imodule)&&this.userView.hasTheFunction("3240804"))
					||("1".equals(imodule)&&this.userView.hasTheFunction("3250504")))?"1":"0");//重命名权限
			
			this.getFormHM().put("movePriv", ((!"1".equals(imodule)&&this.userView.hasTheFunction("3240813"))
					||("1".equals(imodule)&&this.userView.hasTheFunction("3250513")))?"1":"0");//调整顺序权限
			
			StringBuffer str = new StringBuffer();
			String unitcodes=this.userView.getUnitIdByBusi("1");  //UM010101`UM010105` 
			String[] units = unitcodes.split("`");
			for(int i=0;i<units.length;i++)
			{
				String codeid=units[i];
				if(codeid==null|| "".equals(codeid))
					continue;
    			if(codeid!=null&&codeid.trim().length()>2)
				{
    				String privCodeValue = codeid.substring(2);
    				str.append(",");
    				str.append(privCodeValue);
				}
			}
			this.getFormHM().put("orgid", str.length()>0?str.substring(1):"");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
	}

}
