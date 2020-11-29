/**
 * 
 */
package com.hjsj.hrms.transaction.sys.dataimport;

import com.hjsj.hrms.businessobject.sys.dataimport.DataImportBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Map;

/**
 * <p>
 * Title:DataImportShowDialogTrans
 * </p>
 * <p>
 * Description:查询A01字段
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-06-29
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class DataImportShowDialogTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		// request中的参数
		Map request = (Map) this.getFormHM().get("requestPamaHM");
		String opt = "";
		if (request != null) {
			opt = (String) request.get("opt");
		}
		if ("querry".equals(opt)) {
			ArrayList dic = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
			
			ArrayList fieldList = new ArrayList();
			
			for (int i = 0; i < dic.size(); i++) {
				FieldItem item = (FieldItem) dic.get(i);
				if ("A".equalsIgnoreCase(item.getItemtype()) && "0".equalsIgnoreCase(item.getCodesetid())) {
					CommonData data = new CommonData();
					data.setDataName(item.getItemdesc());
					data.setDataValue(item.getItemid());
					fieldList.add(data);
				}
			}
			
			
			CommonData data = new CommonData();
			data.setDataName("人员唯一ID");
			data.setDataValue("GUIDKEY");
			
			fieldList.add(data);
			
			this.getFormHM().put("fieldList", fieldList);
		} else {
			String tableName = (String) this.getFormHM().get("tableName");
			opt = (String) this.getFormHM().get("opt");
			if ("ajax".equals(opt)) {
				if (tableName == null || tableName.trim().length() <= 0) {
					return;
				}
				ArrayList fieldList = new ArrayList();
				DataImportBo bo = new DataImportBo(this.frameconn);
				ArrayList dic = DataDictionary.getFieldList(tableName.trim().toUpperCase(), Constant.USED_FIELD_SET);
				
				try
                {
				    if (null != dic && 0 < dic.size())//(bo.isMainSet(tableName)) 
				    {
				        CommonData data = new CommonData();
				        //人员表添加人员编号字段 guodd 17-04-06
				        if(tableName.trim().toUpperCase().startsWith("A")){
				            data.setDataName("eHR内部人员编号");
	                        data.setDataValue("a0100");
	                        fieldList.add(data);
				        }
				        //单位表，并且不是单位主集添加机构B0110。因为B01 fielditem里已经有B0110了，但是子集fielditem中没有 guodd 17-04-06
				        if(tableName.trim().toUpperCase().startsWith("B") && !"B01".equalsIgnoreCase(tableName)){
				        	    FieldItem b0110 = DataDictionary.getFieldItem("B0110");
				        		data.setDataName(b0110.getItemdesc());
	                        data.setDataValue(b0110.getItemid());
	                        fieldList.add(data);
				        }
				        
				        //岗位表，并且不是岗位主集添加岗位E01A1。因为K01 fielditem里已经有E01A1了，但是子集fielditem中没有 guodd 17-04-06
				        if(tableName.trim().toUpperCase().startsWith("K") && !"K01".equalsIgnoreCase(tableName)){
				        	    FieldItem e01a1 = DataDictionary.getFieldItem("E01A1");
				        		data.setDataName(e01a1.getItemdesc());
	                        data.setDataValue(e01a1.getItemid());
	                        fieldList.add(data);
				        }
                       
                        data = new CommonData();
                        data.setDataName("记录顺序");
                        data.setDataValue("i9999");
                        fieldList.add(data);
  
                        for (int i = 0; i < dic.size(); i++) 
                        {
                            FieldItem item = (FieldItem) dic.get(i);
                            data = new CommonData();
                            data.setDataName(item.getItemdesc());
                            data.setDataValue(item.getItemid());
                            fieldList.add(data);
                        }
	                    
	                    //fieldList.addAll(bo.getTableFieldList("usr" + tableName));
	                } else {
	                    fieldList.addAll(bo.getTableFieldList(tableName));
	                }
                } catch (Exception e)
                {
                    
                }
				
				
				StringBuffer fieldStr = new StringBuffer();
				StringBuffer itemdescStr = new StringBuffer();
				for (int i = 0; i < fieldList.size(); i++) {
					CommonData data = (CommonData) fieldList.get(i);
					fieldStr.append(data.getDataValue());
					itemdescStr.append(data.getDataName());
					if (i != fieldList.size() - 1) {
						fieldStr.append(",");
						itemdescStr.append(",");
					}
				}
				
				this.getFormHM().put("fieldStr", fieldStr.toString());
				this.getFormHM().put("itemdescStr", itemdescStr.toString());
			}
		}
		
	} 
	
	public static void main(String[] str) {
		String n = "1,2,,3,4,";
		String []ns = n.split(",");
		for (int i = 0; i < ns.length; i++) {
			System.out.println("----" + ns[i] + "-----");
		}
	}
}
