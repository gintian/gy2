package com.hjsj.hrms.module.system.distributedreporting.datalog.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.jdom.Document;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * @version: 1.0
 * @Description: 导出日志记录（EXCEL）交易类
 * @author: zhiyh  
 * @date: 2019年3月12日 下午1:58:55
 */
public class ExportLogRecordExcelTrans extends IBusiness{
	@Override
	public void execute() throws GeneralException {
		try {
			String id = (String) this.getFormHM().get("id");//t_sys_asyn_acceptinfo 的主键id
			String type = (String) this.getFormHM().get("type");//判断是否是校验结果
			SetupSchemeBo bo = new SetupSchemeBo(userView, frameconn);
			String UnitDesc = bo.getUnitDesc(id,type);//获得单位名称
	        String fileName = UnitDesc+"日志记录_" +this.userView.getUserName()+ ".xls"; //文件名称
	        if (StringUtils.isNotEmpty(type)) {
	            fileName = UnitDesc+"生成数据的校验结果_" +this.userView.getUserName()+ ".xls"; //文件名称
            }
			// 导出工具类
	        ExportExcelUtil excelUtil = new ExportExcelUtil(this.frameconn);
	        excelUtil.setProtect(false);//是否启用锁定页面
	        ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();//表头
	        //固定列头
	    	LazyDynaBean lazyDynaBean = new LazyDynaBean();
	        lazyDynaBean.set("content", "guidkey");
	        lazyDynaBean.set("codesetid", "0");
	        lazyDynaBean.set("colType", "A");
	        lazyDynaBean.set("decwidth", "0");
	        lazyDynaBean.set("itemid", "guidkey");
	        headList.add(lazyDynaBean);
	        lazyDynaBean = new LazyDynaBean();
	        lazyDynaBean.set("content", "名称");
	        lazyDynaBean.set("codesetid", "0");
	        lazyDynaBean.set("colType", "A");
	        lazyDynaBean.set("decwidth", "0");
	        lazyDynaBean.set("itemid", "name");
	        headList.add(lazyDynaBean);
	        lazyDynaBean = new LazyDynaBean();
	        lazyDynaBean.set("content", "校验指标");
	        lazyDynaBean.set("codesetid", "0");
	        lazyDynaBean.set("colType", "A");
	        lazyDynaBean.set("decwidth", "0");
	        lazyDynaBean.set("itemid", "itemid");
	        headList.add(lazyDynaBean);
	        lazyDynaBean = new LazyDynaBean();
	        lazyDynaBean.set("content", "校验条件");
	        lazyDynaBean.set("codesetid", "0");
	        lazyDynaBean.set("colType", "A");
	        lazyDynaBean.set("decwidth", "0");
	        lazyDynaBean.set("itemid", "reason");
	        headList.add(lazyDynaBean);
	        lazyDynaBean = new LazyDynaBean();
	        lazyDynaBean.set("content", "校验结果");
	        lazyDynaBean.set("codesetid", "0");
	        lazyDynaBean.set("colType", "A");
	        lazyDynaBean.set("decwidth", "0");
	        lazyDynaBean.set("itemid", "value");
	        headList.add(lazyDynaBean);
	        ArrayList<LazyDynaBean> dataList = null;//数据
	        LazyDynaBean dataBean = null;
	        LazyDynaBean rowDataBean = null;
	        ArrayList<LazyDynaBean> mergedCellList = new ArrayList<LazyDynaBean>();//是否有合并列
	        int headStartRowNum =0 ;//表头从第几行开始
	        HashMap dropDownMap = new HashMap();//下拉数据
	        ArrayList<String> recordSetidList = bo.getRecordSetid(id,type);//获得t_sys_asyn_record的记录错误的子集
	        //
		 	lazyDynaBean = new LazyDynaBean();
            lazyDynaBean.set("content", "总体概况");
            lazyDynaBean.set("codesetid", "0");
            lazyDynaBean.set("colType", "A");
            lazyDynaBean.set("decwidth", "0");
            lazyDynaBean.set("itemid", "ztgk");
            ArrayList<LazyDynaBean> headListone = new ArrayList<LazyDynaBean>();//表头
            headListone.add(lazyDynaBean);
            ArrayList<LazyDynaBean> dataListone = new ArrayList<LazyDynaBean>();//表头
            rowDataBean = new LazyDynaBean();
            dataBean = new LazyDynaBean();
            StringBuffer buffer = new StringBuffer();
            if (StringUtils.isNotEmpty(type)) {
                buffer.append(UnitDesc+"生成数据的校验结果：\r\n");
            }else {
                buffer.append(UnitDesc+"的上报日志：\r\n");
            }
            String situation = bo.getSituation(id,type);
            if (null!=situation) {
            	buffer.append(situation);
			}
            String strings=bo.getExpression(recordSetidList,id,type);
            buffer.append(strings);
            dataBean.set("content", buffer.toString());
            rowDataBean.set("ztgk", dataBean);//判断把数值添加到那一列
            dataListone.add(rowDataBean);
			excelUtil.exportExcel("总体概况", mergedCellList, headListone, dataListone, dropDownMap, headStartRowNum);
			HSSFSheet sheet= excelUtil.getSheet();
			sheet.setColumnWidth(0,150*256);
	    	for(int i=0;i<recordSetidList.size();i++) {
	    		dataList = new ArrayList<LazyDynaBean>();
	        	String setid = recordSetidList.get(i);
	        	String sheetName="";
				if ("PHOTO".equalsIgnoreCase(setid)) {
					sheetName="Photo";
				}else if("RULE".equalsIgnoreCase(setid)){
					sheetName="校验规则";
				}else {
					FieldSet fieldSet = DataDictionary.getFieldSetVo(setid);
					sheetName=fieldSet.getFieldsetdesc();//页签名称
				}
	        	//根据setid获得错误的记录
	        	ArrayList<ArrayList<String>> recordList = bo.getRecordList(setid,id,type);
	        	for(int j=0;j<recordList.size();j++) {
	        		rowDataBean = new LazyDynaBean();
	        		ArrayList<String> record = recordList.get(j);
	        		dataBean = new LazyDynaBean();
	        		dataBean.set("content", record.get(0));
	        		rowDataBean.set("guidkey", dataBean);//判断把数值添加到那一列
	        		dataBean = new LazyDynaBean();
	        		dataBean.set("content", record.get(1));
	        		rowDataBean.set("name", dataBean);//判断把数值添加到那一列
	        		String extMemo = record.get(2);
	        		Document document = bo.getDocument(extMemo);
	        		 // 4.通过document对象获取xml文件的根节点
	    	        Element rootElement = document.getRootElement();
	    	        // 5.获取根节点下的子节点的List集合
	    	        List<Element> bodyList = rootElement.getChildren();
	    	        Element  element = null;
	    	        for(int k=0;k<bodyList.size();k++) {
	    	        	LazyDynaBean dataBean1 =(LazyDynaBean) rowDataBean.get("itemid");
	    	        	if (null!=dataBean1) {
	    	        		rowDataBean = new LazyDynaBean();
	    	        		dataBean = new LazyDynaBean();
	                		dataBean.set("content", record.get(0));
	                		rowDataBean.set("guidkey", dataBean);//判断把数值添加到那一列
	                		dataBean = new LazyDynaBean();
	                		dataBean.set("content", record.get(1));
	                		rowDataBean.set("name", dataBean);//判断把数值添加到那一列
						}
	    	        	element= bodyList.get(k);
	    	        	String itemid = element.getAttributeValue("itemid");
	    	        	dataBean = new LazyDynaBean();
	            		dataBean.set("content",itemid);
	            		rowDataBean.set("itemid", dataBean);//判断把数值添加到那一列
	            		String reason = element.getAttributeValue("reason");
	            		dataBean = new LazyDynaBean();
	            		dataBean.set("content",reason);
	            		rowDataBean.set("reason", dataBean);//判断把数值添加到那一列
	            		String value = element.getAttributeValue("value");
	            		dataBean = new LazyDynaBean();
	            		dataBean.set("content",value);
	            		rowDataBean.set("value", dataBean);//判断把数值添加到那一列
	            		dataList.add(rowDataBean);
	    	        }
	        	}
				excelUtil.exportExcel(sheetName, mergedCellList, headList, dataList, dropDownMap, headStartRowNum);
	        }
	    	 excelUtil.exportExcel(fileName);
	         this.getFormHM().put("fileName", PubFunc.encrypt(fileName));
	         this.getFormHM().put("flag", true);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
