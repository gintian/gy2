package com.hjsj.hrms.transaction.gz.voucher;

import com.hjsj.hrms.businessobject.gz.voucher.ShowExcelBo;
import com.hjsj.hrms.businessobject.gz.voucher.VoucherJounalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * @author xuchangshun
 * */
public class ShowExcelTrans extends com.hrms.struts.facade.transaction.IBusiness {

	public void execute() throws GeneralException {

		String titleValue=(String) this.getFormHM().get("titleValue");
		String none_fieldValue=(String) this.getFormHM().get("none_fieldValue");
		String pn_id=(String) this.getFormHM().get("pn_id");
		
		String xmlValue =(String) this.getFormHM().get("xmlValue");
		String sqlValue=(String)this.getFormHM().get("sqlValue");
		
		String[] xmlArray=xmlValue.split(",");//列名
		ArrayList titleList = new ArrayList();//要在Excel中显示的中文字段名
		ArrayList none_fieldList=new ArrayList();//用来排除不显示的列
		VoucherJounalBo vjb = new VoucherJounalBo(this.frameconn);
		String[] titleArrayValue=titleValue.split(",");
		String[]none_fielArraydValue=none_fieldValue.split(",");
		
		for(int i=0;i<titleArrayValue.length;i++){
			titleList.add(titleArrayValue[i]);
		}
		for(int i=0;i<none_fielArraydValue.length;i++){
			none_fieldList.add(none_fielArraydValue[i]);
		}
		
		String[] ExcelArray=sqlValue.split(",");
		ArrayList xmlArrayList=new ArrayList();
		ArrayList excelList=new ArrayList();
		for(int i=0;i<xmlArray.length;i++){
			xmlArrayList.add(xmlArray[i]);
		}
		for(int i=0;i<ExcelArray.length;i++){
			if("fl_id".equals(ExcelArray[i])){
				continue;
			}
			if("seq".equals(ExcelArray[i])){
				continue;
			}
			excelList.add(ExcelArray[i]);
		}
		xmlArrayList.removeAll(none_fieldList);
		xmlArrayList.removeAll(excelList);
		xmlArrayList.remove("fl_id");
		excelList.addAll(xmlArrayList);
		ArrayList columnlist =excelList;// 列名
		ArrayList column = new ArrayList();// 中文字段名
		ArrayList infolist = new ArrayList();// 各列数值
	
		for(int i=0;i<titleList.size();i++){
			column.add(titleList.get(i));
		}
		String sql="select "+sqlValue+" from gz_warrantlist where pn_id='"+pn_id+"'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				for(int i=0;i<ExcelArray.length;i++){
					if("fl_id".equals(ExcelArray[i])){
						continue;
					}
					if("c_subject".equals(ExcelArray[i])){
						String ccode=frowset.getString(ExcelArray[i]);
						if(!("".equals(ccode)||ccode==null)){
							String sql1="select ccode_name from gz_code where ccode='"+ccode+"'";	
							this.frecset=dao.search(sql1);
							while(frecset.next()){
								bean.set(ExcelArray[i], PubFunc.nullToStr(frecset.getString("ccode_name")));
							}
							continue;
						}else{
							bean.set(ExcelArray[i], PubFunc.nullToStr(""));
							continue;
						}
					}
					if("c_where".equals(ExcelArray[i])||"c_itemsql".equals(ExcelArray[i])||"c_group".equals(ExcelArray[i])){
						bean.set(ExcelArray[i], PubFunc.nullToStr(""));
						continue;
					}
					if("seq".equals(ExcelArray[i])){
						continue;
					}
					FieldItem item = DataDictionary.getFieldItem(ExcelArray[i].toLowerCase());
					if(item!=null){
					    if("A".equalsIgnoreCase(item.getItemtype())){
					        if(!("0".equalsIgnoreCase(item.getCodesetid())||"".equalsIgnoreCase(item.getCodesetid())||item.getCodesetid()==null)){
					            String desc = vjb.getDesc(item.getCodesetid(),frowset.getString(ExcelArray[i]));
					            bean.set(ExcelArray[i], PubFunc.nullToStr(desc));
					            continue;
					        }
					    }
					}
					bean.set(ExcelArray[i], PubFunc.nullToStr(frowset.getString(ExcelArray[i])));
				}
				for(int i=0;i<xmlArrayList.size();i++){
					if("fl_id".equals(xmlArrayList.get(i))){
						continue;
					}
					if("seq".equals(xmlArrayList.get(i))){
						continue;
					}
					bean.set( (String) xmlArrayList.get(i), PubFunc.nullToStr(""));
				}
				Integer seq = new Integer(frowset.getInt("seq"));
				String ss =seq.toString();
				bean.set("seq", PubFunc.nullToStr(ss));
				infolist.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		ShowExcelBo show = new ShowExcelBo();
		String filename = show.creatExcel(column, infolist, columnlist);
		/* 安全问题 文件导出 财务凭证定义-导出excel xiaoyun 2014-9-16 start */
		filename = SafeCode.encode(PubFunc.encrypt(filename));
		/* 安全问题 文件导出 财务凭证定义-导出excel xiaoyun 2014-9-16 end */
		this.getFormHM().put("excelfile", filename);
	}

}
