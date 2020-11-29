package com.hjsj.hrms.transaction.train.signCollect;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.attendance.ExportExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>ExportExcelTrans.java</p>
 * <p>Description:培训考勤导出Excel</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-03-09 上午14:07:55</p>
 * @author LiWeichao
 * @version 5.0
 */
public class ExportSignExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		String columns=(String)this.userView.getHm().get("train_columns");
		String sql=(String)this.userView.getHm().get("train_sql");
		sql=PubFunc.keyWord_reback(sql);
		String sort=(String)this.getFormHM().get("sort");
		String title="培训考勤签到汇总记录";
		ArrayList fielditem=DataDictionary.getFieldList("r47", Constant.EMPLOY_FIELD_SET);
		StringBuffer colStr=new StringBuffer();
		String[] cols=columns.split(",");
		ExportExcelBo exl=new ExportExcelBo();
		if("1".equals(sort))
		{
			if(cols!=null&&cols.length>1){
				for (int i = 0; i < cols.length; i++) {
					if("a0100".equalsIgnoreCase(cols[i])||"nbase".equalsIgnoreCase(cols[i]))
						continue;
					for (int j = 0; j < fielditem.size(); j++) {
						FieldItem fi=(FieldItem)fielditem.get(j);
						if(cols[i].equalsIgnoreCase(fi.getItemid())){
							if(!fi.isVisible()){
								columns=columns.replaceAll(fi.getItemid()+",", "");
								continue;
							}
							colStr.append(fi.getItemdesc()+",");
						}
					}
				}
				
				ConstantXml constantbo = new ConstantXml(this.getFrameconn(), "TR_PARAM");
                String cardId = constantbo.getTextValue("/param/attendance/card_no");// 获得设置的卡号字段名称
                if(!cardId.isEmpty())
                {
                    StringBuffer cs = new StringBuffer(colStr);
                    int colStrNumber = colStr.indexOf("姓名,"); 
                    int columnsNumber = columns.indexOf("a0101,"); 
                    cs.insert(colStrNumber + 3, "卡号,");//在指定的位置插入卡号
                    colStr = cs;
                    cs = new StringBuffer(columns);
                    cardId = cardId + ",";
                    cs.insert(columnsNumber + 6, cardId);//在指定的位置插入卡号字段
                    columns = cs.toString();
                    //传递参数，表示是出勤汇总页面
                    exl.setFlag("1");
                }
                
				colStr.setLength(colStr.length()-1);
				columns = columns.replaceAll("a0100,", "").replaceAll("nbase,", "");
			}
		}else if("2".equals(sort))
		{
			if(cols!=null&&cols.length>1){
				for (int i = 0; i < cols.length; i++) {
					if("a0100".equalsIgnoreCase(cols[i])||"nbase".equalsIgnoreCase(cols[i]))
						continue;
					for (int j = 0; j < fielditem.size(); j++) {
						FieldItem fi=(FieldItem)fielditem.get(j);
						if(cols[i].equalsIgnoreCase(fi.getItemid())){
							if(!fi.isVisible()&&!"r4101".equalsIgnoreCase(fi.getItemid())){
								columns=columns.replaceAll(fi.getItemid()+",", "");
								continue;
							}
							colStr.append(fi.getItemdesc()+",");
						}
					}
				}
				colStr.setLength(colStr.length()-1);
				columns=columns.replaceAll("a0100,", "").replaceAll("nbase,", "");
			}
		}else if("3".equals(sort))
		{
			colStr.append("培训班,");
			if(cols!=null&&cols.length>1){
				for (int i = 0; i < cols.length; i++) {
					if("a0100".equalsIgnoreCase(cols[i])||"nbase".equalsIgnoreCase(cols[i]))
						continue;
					for (int j = 0; j < fielditem.size(); j++) {
						FieldItem fi=(FieldItem)fielditem.get(j);
						if(cols[i].equalsIgnoreCase(fi.getItemid())){
							if(!fi.isVisible()){
								columns=columns.replaceAll(fi.getItemid()+",", "");
								continue;
							}
							colStr.append(fi.getItemdesc()+",");
						}
					}
				}
				colStr.setLength(colStr.length()-1);
				columns=columns.replaceAll("a0100,", "").replaceAll("nbase,", "");
			}
		}
		
		
		try {
			String filename = exl.ExportExcel(this.getFrameconn(), title, columns, colStr.toString(), sql, this.userView);
			this.getFormHM().put("filename", PubFunc.encrypt(filename));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
