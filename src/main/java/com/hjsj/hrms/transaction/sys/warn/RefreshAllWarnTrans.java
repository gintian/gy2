package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.businessobject.sys.warn.ConfigCtrlInfoVO;
import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 刷新全部预警记录
 * <p>Title:RefreshAllWarnTrans.java</p>
 * <p>Description>:RefreshAllWarnTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 26, 2010 2:40:32 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class RefreshAllWarnTrans extends IBusiness implements IConstant {

	public void execute() throws GeneralException {
		
		ArrayList alBasePre = DataDictionary.getDbpreList();	
	    
		String sql="select * from hrpwarn where valid='1'";
		DomainTool tool = new DomainTool(); //预警对象分析工具对象
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String value=null;
		try {
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				DynaBean dbean = new LazyDynaBean();
	            for (int i = 0; i < Key_HrpWarn_Fields.length; i++) {
	            	value=this.frowset.getString(Key_HrpWarn_Fields[i]);
	            	if(value==null||value.length()<=0|| "null".equals(value)){
	            		value="";
	            	}                	
	            	dbean.set(Key_HrpWarn_Fields[i], value);
	            }
	            
	            // 封装预警控制信息(xml)
	            String xml = dbean.get( Key_HrpWarn_FieldName_CtrlInf ).toString();
	            ConfigCtrlInfoVO ctrlVo =  new ConfigCtrlInfoVO(xml);               
	            dbean.set(Key_XmlResul_Freq, ctrlVo.getFreqShow());  //预警频度显示信息              
	            dbean.set(Key_Domain_Names, tool.getDomainNames(ctrlVo.getStrDomain()));//预警对象中文名称显示(逗号分割)             
	            dbean.set( Key_HrpWarn_Ctrl_VO, ctrlVo);//预警控制对象     
	            ScanTrans.runWarn(dbean,alBasePre);
	           // break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		
		
		
	}

}
