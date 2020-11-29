package com.hjsj.hrms.module.gz.salarytype.transaction.salaryitem;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 项目名称 ：ehr7.x
 * 类名称：FieldItemOptTrans
 * 类描述：新增薪资项目数据初始化
 * 创建人： lis
 * 创建时间：2015-10-21
 */
public class FieldItemOptTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			String opt=(String)this.getFormHM().get("opt");   // 1: 保存新增的薪资项目   2:删除薪资项目
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			//薪资或删除的薪资项目id
			String salarySetIDs=(String)this.getFormHM().get("salarySetIDs");
			//薪资类别id
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid=SafeCode.decode(salaryid); //解码
			salaryid =PubFunc.decrypt(salaryid); //解密
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			SalaryTypeBo bo = new SalaryTypeBo(this.getFrameconn(),this.getUserView());
			StringBuffer context = new StringBuffer();
			String[] fielditemIDsArr = salarySetIDs.split("/");
			if("1".equals(opt)){//保存子集指标
				//保存薪资项目
				bo.saveSalarySet(salarySetIDs,Integer.valueOf(salaryid));
				
				context.append("新增:"+bo.getSalaryName(salaryid)+"("+salaryid+")<br>");
				for(int i=0;i<fielditemIDsArr.length;i++)
				{
					FieldItem tempItem=DataDictionary.getFieldItem(fielditemIDsArr[i].toLowerCase());
					if(tempItem == null)
						continue;
					if(i!=0)
						context.append(",");
					context.append(tempItem.getItemdesc());
				}
			}else if("2".equals(opt)){//删除薪资项目
				String delete = ResourceFactory.getProperty("button.delete");//删除
				context.append(delete + ":"+bo.getSalaryName(salaryid)+"("+salaryid+")<br>");
				StringBuffer whl=new StringBuffer("");
				for(int i=0;i<fielditemIDsArr.length;i++){
					String id = fielditemIDsArr[i];
					if(StringUtils.isBlank(id))
						continue;
					whl.append(","+id);
				}
				
				ArrayList list = notDeleteItem(salaryid);
				RowSet rs = dao.search("select itemdesc,itemid from  salaryset where salaryid=? and fieldid in ("+whl.substring(1)+")",Arrays.asList(salaryid));
				while(rs.next())
				{
					String itemid = rs.getString("itemid");
					if(list.indexOf(itemid) != -1) {
						throw GeneralExceptionHandler.Handle(new Exception("你选择的“"+rs.getString("itemdesc")+"”已在属性中设置，请重新选择！"));
					}
					context.append(rs.getString("itemdesc"));
					context.append(",");
				}
				
				bo.delSalarySet(salarySetIDs,salaryid);
				this.getFormHM().put("@eventlog", context.toString().substring(0, context.length()-1));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	/**
	 * @author lis
	 * @Description: 判断是否有e01a1
	 * @date 2015-10-21
	 * @param salaryid
	 * @return
	 * @throws GeneralException
	 */
	private boolean hasE01a1Field(String salaryid) throws GeneralException
	{
		boolean flag=true;
		RowSet rs=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs=dao.search("select * from salaryset where salaryid="+salaryid+" and UPPER(itemid)='E01A1'");
			while(rs.next())
			{
				flag=false;
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return flag;
	}
	
	/**
	 * 薪资类别属性中，当设置了计税单位指标，然后在项目定义中删除该指标，属性中计税单位处显示指标代码
	 * （这里删除的时候先找出属性中设置的指标，如果包含了，不准删除）
	 * @Title: notDeleteItem   
	 * @Description:    
	 * @param @param salaryid
	 * @param @return 
	 * @return ArrayList    
	 * @throws
	 */
	private ArrayList notDeleteItem(String salaryid) {
		ArrayList list = new ArrayList();
		SalaryTemplateBo salaryTemplatebo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.getUserView());
		list.add(salaryTemplatebo.getCtrlparam().getValue(SalaryCtrlParamBo.TAX_DATE_FIELD));//计税时间
		list.add(salaryTemplatebo.getCtrlparam().getValue(SalaryCtrlParamBo.DECLARE_TAX));//报税时间
		list.add(salaryTemplatebo.getCtrlparam().getValue(SalaryCtrlParamBo.PAY_FLAG));//发薪标识
		list.add(salaryTemplatebo.getCtrlparam().getValue(SalaryCtrlParamBo.TAX_MODE));//计税方式
		list.add(salaryTemplatebo.getCtrlparam().getValue(SalaryCtrlParamBo.TAX_DESC));//纳税项目
		list.add(salaryTemplatebo.getCtrlparam().getValue(SalaryCtrlParamBo.TAX_UNIT));//计税单位
		list.add(salaryTemplatebo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD, "collect_field"));//汇总指标
		list.add(salaryTemplatebo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"));//归属单位
		list.add(salaryTemplatebo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD, "deptid"));//归属部门
		return list;
	}

}
