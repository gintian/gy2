package com.hjsj.hrms.module.certificate.config.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;


public class CertificateConfigTrans extends IBusiness {
	
    @Override
    public void execute() throws GeneralException {
        
        try {
        	/**
        	 * flag为前台操作步骤参数，
        	 * =1时为初始化加载数据， 
        	 * =2时为页面保存操作，
        	 * =3时为添加证照借阅子集操作，
        	 * =4时为页面证书类别代码类发生变化，需改变证书类别选择下拉框
        	 * =5时为页面选中证书信息集发生变化，需加载对应指标，
        	 * */
            boolean iscombobox = true;
            CertificateConfigBo certificateConfigBo = new CertificateConfigBo(this.getFrameconn(),userView);
            String flag = (String) this.getFormHM().get("flag");
            String fieldsetid = (String) this.getFormHM().get("certSubsetid");
            String certCategoryCode = (String) this.getFormHM().get("certCategoryCode");
            
            HashMap certMap = certificateConfigBo.getCertMap();
            if (certMap != null) {
            	// 校验参数map
            	certificateConfigBo.checkCertMap(certMap);
            	// 49366 只有在初始页面需要获取原有的参数设置
            	if("1".equals(flag)) {
            		fieldsetid = (String) certMap.get("cert_subset");
            		certCategoryCode = (String) certMap.get("cert_category_code");
            	}
            }
            
            if ("1".equalsIgnoreCase(flag)) {
            	
                iscombobox = false;
                ArrayList nbaseList = certificateConfigBo.getNbaseList();
                // 子集集合 支持附件
                ArrayList fieldSubsetlist = certificateConfigBo.getFieldsetlist("1");
                this.getFormHM().put("subsetlist", fieldSubsetlist);
                // 全部子集集合
                ArrayList fieldSubsetAlllist = certificateConfigBo.getFieldsetlist("2");
                this.getFormHM().put("subsetAlllist", fieldSubsetAlllist);
                //全部人员库
                this.getFormHM().put("nbaseList", nbaseList);
            } else if ("2".equals(flag)) {
            	
            	String certMapJson = (String) this.getFormHM().get("certMapJson");
            	String info = certificateConfigBo.saveCertificateConfig(certMapJson);
                
                this.formHM.put("msg", StringUtils.isBlank(info) ? "true" : info);
                return;
            } else if ("3".equals(flag)) {
            	
                String codevalue = certificateConfigBo.getAddSubset(certCategoryCode);
                // 全部子集集合
                ArrayList fieldSubsetAlllist = certificateConfigBo.getFieldsetlist("2");
                
                this.getFormHM().put("subsetAlllist", fieldSubsetAlllist);
                this.getFormHM().put("certBorrowSubset", codevalue);
                this.formHM.put("msg", "true");
                return;
            } else if ("4".equals(flag)) {
            	
                certificateConfigBo.setCertCategoryCode(certCategoryCode);
                iscombobox = false;
            }

            // 证书信息集合字符串指标下拉列表
            ArrayList<HashMap<String, String>> certStrList = new ArrayList<HashMap<String, String>>();
            // 证书信息集合日期型指标下拉列表
            ArrayList<HashMap<String, String>> certDateList = new ArrayList<HashMap<String, String>>();
            // 证书状态下拉列表
            ArrayList<HashMap<String, String>> certStatuslist = new ArrayList<HashMap<String, String>>();
            // 证书所属组织下拉列表
            ArrayList<HashMap<String, String>> certOrganizationlist = new ArrayList<HashMap<String, String>>();
            // 证书类别下拉列表
            ArrayList<HashMap<String, String>> certCategorylist = new ArrayList<HashMap<String, String>>();
            // 证书是否借出指标下拉列表
            ArrayList<HashMap<String, String>> certBorrowlist = new ArrayList<HashMap<String, String>>();

            //加载子集指标数据
            ArrayList<FieldItem> fielditemlist = new ArrayList<FieldItem>();

            if (StringUtils.isNotEmpty(fieldsetid) && (((certMap != null) && (certMap.size() != 0))
            		|| "4".equals(flag) || "5".equals(flag))) {
            	
                fielditemlist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("dataValue", "");
                map.put("dataName", "请选择...");
                
                certStrList.add(map);
                certDateList.add(map);
                certStatuslist.add(map);
                certOrganizationlist.add(map);
                certCategorylist.add(map);
                certBorrowlist.add(map);

                for (int i = 0; i < fielditemlist.size(); i++) {
                    FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                    // 未构库指标不能出现在页面
                    if (null==fielditem || "0".equalsIgnoreCase(fielditem.getUseflag())) 
                        continue;

                    map = new HashMap<String, String>();
                    String fielditemdesc = (String) fielditem.getItemid().toUpperCase() + ":" + (String) fielditem.getItemdesc();
                    map.put("dataValue", fielditem.getItemid());
                    map.put("dataName", fielditemdesc);
                    
                    // 字符型指标
                    if ("A".equalsIgnoreCase(fielditem.getItemtype()) && "0".equalsIgnoreCase(fielditem.getCodesetid()))
                    	certStrList.add(map);
                    // 日期型指标
                    if ("D".equalsIgnoreCase(fielditem.getItemtype()))
                    	certDateList.add(map);
                    
                    if ("A".equalsIgnoreCase(fielditem.getItemtype()) && "83".equalsIgnoreCase(fielditem.getCodesetid()))
                        certStatuslist.add(map);
                    
                    if ("A".equalsIgnoreCase(fielditem.getItemtype()) && "45".equalsIgnoreCase(fielditem.getCodesetid()))
                    	certBorrowlist.add(map);

                    if ("UN".equalsIgnoreCase(fielditem.getCodesetid()) || "UM".equalsIgnoreCase(fielditem.getCodesetid()))
                        certOrganizationlist.add(map);

                    if (StringUtils.isNotEmpty(certCategoryCode) && certCategoryCode.equalsIgnoreCase(fielditem.getCodesetid()))
                        certCategorylist.add(map);
                }
            }

            this.formHM.put("certStrlist", certStrList);
            this.formHM.put("certDatelist", certDateList);
            this.formHM.put("certStatuslist", certStatuslist);
            this.formHM.put("certOrganizationlist", certOrganizationlist);
            this.formHM.put("certCategorylist", certCategorylist);
            this.formHM.put("certBorrowlist", certBorrowlist);

            if (iscombobox) 
                return;

            ArrayList certCategoryCodeList = new ArrayList();
            certCategoryCodeList = certificateConfigBo.getCertCategoryCodeList();
            String certCategoryCodeListJson = JSONArray.fromObject(certCategoryCodeList).toString();
            this.formHM.put("certCategoryCodeList", certCategoryCodeList);
            this.formHM.put("certMap", certMap);
            this.formHM.put("fielditemlist", fielditemlist);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
    }
}
