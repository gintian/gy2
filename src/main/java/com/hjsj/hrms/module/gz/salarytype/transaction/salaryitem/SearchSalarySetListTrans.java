package com.hjsj.hrms.module.gz.salarytype.transaction.salaryitem;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTableStructBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.axis.utils.StringUtils;

import java.util.ArrayList;

/**
 * 查询薪资类别中的薪资项目列表
 * @author lis
 * 2015-10-17
 */
public class SearchSalarySetListTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			String salaryid = (String)this.getFormHM().get("salaryid");
			String where = SafeCode.decode((String)this.getFormHM().get("where"));
			StringBuffer whereBuf = new StringBuffer();
			if(StringUtils.isEmpty(where))
				whereBuf.append(" UPPER(itemid)<>'A0000' and UPPER(itemid)<>'A0100'");
			else
				whereBuf.append(where + " and UPPER(itemid)<>'A0000' and UPPER(itemid)<>'A0100'");
			if(StringUtils.isEmpty(salaryid)){
				MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("customParams");
				salaryid = (String)bean.get("salaryid");
			}
			
			salaryid=SafeCode.decode(salaryid); //解码
			salaryid =PubFunc.decrypt(salaryid); //解密
			
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			SalaryTemplateBo bo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.getUserView());
			SalaryTableStructBo salaryTableStructBo=new SalaryTableStructBo(this.getFrameconn(),this.userView);
			SalaryTypeBo salaryTypeBo = new SalaryTypeBo(this.getFrameconn(), this.userView);
			
			ArrayList<String> valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
			StringBuffer where_str = new StringBuffer("");
			if(valuesList != null){
				for(String value:valuesList){
					where_str.append("or (Lower(itemdesc) like '%"+SafeCode.decode(value).toLowerCase()+"%' or Lower(itemid) like '%"+SafeCode.decode(value).toLowerCase()+"%') ");
				}
				if(!StringUtils.isEmpty(where_str.toString()))
					where = where_str.toString().substring(2);
				this.getFormHM().put("where",SafeCode.encode(where));
				return;
			}

// 不再在这里做结构同步 没必要zhanghua 2017-5-23
//			HashMap paramMap = new HashMap();
//			paramMap.put("username",this.userView.getUserName());
//			paramMap.put("ctrlParamBo", new SalaryCtrlParamBo(this.getFrameconn(), Integer.valueOf(salaryid)));
//			paramMap.put("salaryid",salaryid);
//			paramMap.put("salaryItemList", bo.getSalaryItemList("",""+salaryid,1));  //薪资项
//			paramMap.put("midVariableList", bo.getMidVariableListByTable(salaryid)); //薪资类别涉及的临时变量
//			
//			salaryTableStructBo.syncGzTableStruct(paramMap);//同步薪资表结构
			
			String errorMessage = salaryTypeBo.synchronismSalarySet(Integer.valueOf(salaryid));//同步工资类别里的工资项
			
			ArrayList list=bo.getSalaryItemList(whereBuf.toString(), salaryid, 1);//得到薪资类别
		
			this.getFormHM().put("errorMessage", errorMessage);
			this.getFormHM().put("salaryItemList",list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
