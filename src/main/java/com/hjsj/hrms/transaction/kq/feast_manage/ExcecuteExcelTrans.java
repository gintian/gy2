package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.feast_manage.ExcelHols;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 导出
 *<p>Title:ExcecuteExcelTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 7, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class ExcecuteExcelTrans extends IBusiness {

	public void execute() throws GeneralException
	{
	    try{
    		String strsql= (String) this.getUserView().getHm().get("key_kq_sql1");		
    		ArrayList fielditemlist = DataDictionary.getFieldList("Q17",
    				Constant.USED_FIELD_SET);;
    		//szk导出加工号
    				  KqParameter kq_paramter = new KqParameter(this.userView, "", this.getFrameconn());
    		            HashMap hashmap = kq_paramter.getKqParamterMap();
    		            String kq_gno = (String) hashmap.get("g_no");
    		            
    		            FieldItem field_gno = new FieldItem();
    		            field_gno.setItemid("f1");
    		            field_gno.setItemdesc("工号");
    		            field_gno.setItemtype("A");
    		            field_gno.setCodesetid("0");
    		            field_gno.setVisible(true);
    		            
    		            //把工号指标固定插入到a0101之后
    		            for (int i = 0; i < fielditemlist.size(); i++) {
    		                FieldItem field = (FieldItem) fielditemlist.get(i);
    		                if ("a0101".equalsIgnoreCase(field.getItemid())) {
    		                	fielditemlist.add(i + 1, field_gno);
    		                }
    		            }
    		ExcelHols excelHols=new ExcelHols(this.getFrameconn());
    		strsql = PubFunc.keyWord_reback(strsql);
    		String excelfile=excelHols.creatExcel(strsql,fielditemlist);
    		//xiexd 2014.09.15加密文件名
    		excelfile = PubFunc.encrypt(excelfile);
    		this.getFormHM().put("excelfile",excelfile);
	    }
	    catch (Exception e) {
	        e.printStackTrace();
        }
	}

}
