package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * 项目名称 ：ehr
 * 类名称：ExportExcelTrans
 * 类描述：任务监控导出excel
 * 创建人： lis
 * 创建时间：2016-4-22
 */
public class ExportExcelTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		//Boolean doSelectAll=(Boolean) this.getFormHM().get("doSelectAll");//全选
		Boolean isExportAll=(Boolean) this.getFormHM().get("isExportAll");//是否全部导出
		//doSelectAll为false时是勾选数据，doSelectAll是true时是未勾选数据
		ArrayList selectedlist=(ArrayList) this.getFormHM().get("selectdata");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ExportExcelUtil excelUtil = new ExportExcelUtil(this.frameconn);
		
		try
		{   //60732 VFS+UTF- 8+达梦：人事异动/业务处理/任务监控，查看审批过程，导出excel以及功能导航/导出excel，模板统一命名为： 登陆用户_相应信息
			String fileName = this.userView.getUserName()+"_ctrl_export_data"+".xls";
			StringBuffer taskIds = new StringBuffer();//id串，以“，”分割
			//表格控件对象
			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(SafeCode.encode(PubFunc.encrypt("ctrltask")));
			ArrayList<ColumnsInfo> columns = tableCache.getTableColumns();//表格控件列
			ArrayList<ColumnsInfo> columnsTemp = new ArrayList<ColumnsInfo>();//可以显示的列
			StringBuffer tableSql = new StringBuffer();
			for(ColumnsInfo info:columns){
				//审批过程和浏览打印不显示
				if("sploop".equalsIgnoreCase(info.getColumnId()) || "browseprint".equalsIgnoreCase(info.getColumnId()))
					continue;
				if(info.getLoadtype() != 3){//排除隐藏列
					if("name".equalsIgnoreCase(info.getColumnId()))
						info.setColumnWidth(500);
					columnsTemp.add(info);
				}
			}
			if(isExportAll.booleanValue()){//全部导出
				for(int i=0;i<selectedlist.size();i++)
				{					
					DynaBean rec=(DynaBean)selectedlist.get(i);  
					String task_id=PubFunc.decrypt((String)rec.get("task_id_e"));
					taskIds.append("," + task_id);
				}
				//查询数据sql
				tableSql.append(tableCache.getTableSql());
				tableSql.append(" and task_id not in(-1");
				if(StringUtils.isNotBlank(taskIds.toString()))
					tableSql.append(taskIds.toString());
				tableSql.append(") ");
				tableSql.append(tableCache.getSortSql());
			}else{
				for(int i=0;i<selectedlist.size();i++)
				{					
					DynaBean rec=(DynaBean)selectedlist.get(i);  
					String task_id=PubFunc.decrypt((String)rec.get("task_id_e"));
					taskIds.append(","+task_id);
				}
				//查询数据sql
				tableSql.append(tableCache.getTableSql());
				tableSql.append(" and task_id in(-1");
				if(StringUtils.isNotBlank(taskIds.toString()))
					tableSql.append(taskIds.toString());
				tableSql.append(") ");
				tableSql.append(tableCache.getSortSql());
			}
			
			ArrayList<LazyDynaBean> mergedCellList = new ArrayList<LazyDynaBean>();
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("content", "任务监控流程");// 名称
			bean.set("fromRowNum", 0);// 合并单元格从那行开始
			bean.set("toRowNum", 0);// 合并单元格到哪行结束
			bean.set("fromColNum", 0);// 合并单元格从哪列开始
			bean.set("toColNum", columnsTemp.size()-1);// 合并单元格从哪列结束
			mergedCellList.add(bean);
			
			excelUtil.exportExcelByColum(fileName, "任务监控流程", mergedCellList, columnsTemp, tableSql.toString(), null, 1);
			this.getFormHM().put("fileName",PubFunc.encrypt(fileName));
		}
		catch(Exception e)
		{
			e .printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
