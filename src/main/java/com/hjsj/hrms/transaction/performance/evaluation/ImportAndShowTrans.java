package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataCollectBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:ImportAndShowTrans.java</p>
 * <p>Description>:绩效评估批量导入</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 05, 2010 09:15:57 AM</p>
 * <p>@author: JinChunhai </p>
 * <p>@version: 5.0</p>
 */

public class ImportAndShowTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try{			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String plan_id = (String) hm.get("plan_id");
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = _bo.isHavePriv(this.userView, plan_id);
			if(!_flag){
				return;
			}
			FormFile form_file = (FormFile) getFormHM().get("file");
			boolean flag = FileTypeUtil.isFileTypeEqual(form_file);
			if(!flag){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
	
	//		String busitype = (String) this.getFormHM().get("busitype");
	//		System.out.println(busitype);
			
			DataCollectBo bo = new DataCollectBo(this.getFrameconn(), plan_id,this.userView);
			RecordVo vo=bo.getPerPlanVo(plan_id);
			String object_type = String.valueOf(vo.getInt("object_type")); // 1部门 2：人员
			String template_id = vo.getString("template_id");
			String code=(String)this.getFormHM().get("code");
			String oname="";
			String onlyname="";
			String canimport="";
			if("2".equalsIgnoreCase(object_type))
			{
				Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
				oname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
				if(oname!=null&&oname.trim().length()!=0)
				{
					onlyname=DataDictionary.getFieldItem(oname).getItemdesc();
				}
				if(oname==null||oname.trim().length()==0){
					 canimport="请到系统参数中维护'唯一性指标'！";
				}
			}else
			{
				RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.frameconn);
				if(unit_code_field_constant_vo!=null)
					oname=unit_code_field_constant_vo.getString("str_value");
				if( oname.indexOf("#")==-1&&oname.length()!=0)
				{
				    onlyname=DataDictionary.getFieldItem(oname).getItemdesc();
				}
				if(oname==null||oname.trim().length()==0){
					 canimport="请到'岗位参数设置'中维护'单位代码指标'！";
				}
			}
			if(canimport!=null&&canimport.trim().length()==0){
				String whl = "";
				PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
				whl = pb.getPrivWhere(userView);// 根据用户权限先得到一个考核对象的范围
				if (code != null && !"-1".equals(code))
				{
					if (AdminCode.getCodeName("UN", code) != null && AdminCode.getCodeName("UN", code).length() > 0)
						whl += " and b0110 like '" + code + "%'";
					else if (AdminCode.getCodeName("UM", code) != null && AdminCode.getCodeName("UM", code).length() > 0)
						whl += " and e0122 like '" + code + "%'";
		
				}
				String bodyid = (String) this.getFormHM().get("bodyid");
				if (bodyid == null || bodyid.length() == 0)
					bodyid = "all";
				String order_str = (String) this.getFormHM().get("order_str");
				if (order_str == null || order_str.length() == 0)
					order_str = "";
				ArrayList list=new ArrayList();
				list.add("姓名");
				list.add("唯一标识");
				list.add("修正分值");
				list.add("修正原因");
				ArrayList beanList=new ArrayList();
				this.getFormHM().put("implist", new ArrayList());
				BatchGradeBo bb = new BatchGradeBo(this.getFrameconn(),plan_id);
				
				try
				{
					String computeFashion = (String) this.getFormHM().get("computeFashion");
					ArrayList pointList = bb.getPerPointList(template_id,plan_id);
					String pointResult = (String) this.getFormHM().get("pointResult");
					if (pointResult == null || pointResult.length() == 0)
						pointResult = "1";
					LoadXml parameter_content = new LoadXml(this.getFrameconn(), plan_id);
					
					Hashtable params = parameter_content.getDegreeWhole();
					
					PerEvaluationBo pe = new PerEvaluationBo(this.getFrameconn(), plan_id, template_id,this.userView);
					String handEval = (String) params.get("HandEval");
					String handScore = "0";
					if (handEval != null && "TRUE".equalsIgnoreCase(handEval))// 启动录入结果
						handScore = "1";
					else
						handScore = "0";
					
					ArrayList computeFashionList = new ArrayList();
					computeFashionList = pe.getComputeFashionList();
					if (computeFashion == null || computeFashion.length() == 0)
						computeFashion = ((CommonData) computeFashionList.get(0)).getDataValue();
					pe.getEvaluationTableHtml(computeFashion, whl, pointResult, order_str, bodyid, handScore,"0");
					ArrayList setlist = pe.getDataList();
					HashMap a0101m=new HashMap();
					for(int i=0;i<setlist.size();i++){
						LazyDynaBean bean=(LazyDynaBean)setlist.get(i);
						String a0101=(String)bean.get("a0101");
						a0101m.put(a0101, a0101);
					}
					beanList=bo.anExcel(form_file, list, oname, false,a0101m);
					String errors = bo.getErrors();
					if(errors.length()==0){
						ArrayList showlist=new ArrayList();
						if(beanList!=null){
							showlist=(ArrayList)beanList.get(0);
						}
						this.getFormHM().put("implist", showlist);
						this.getFormHM().put("contBean", beanList.get(1));
					}else
						canimport = errors;							
				}catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			this.getFormHM().put("canimport", canimport);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
