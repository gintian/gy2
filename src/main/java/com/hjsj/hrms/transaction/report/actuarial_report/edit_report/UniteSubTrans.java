package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class UniteSubTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			
			String insert="";
			String update ="";
			StringBuffer errorStr=new StringBuffer();
			String id=(String)this.getFormHM().get("id");
			String unitcode=(String)this.getFormHM().get("unitcode");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=new RecordVo("tt_cycle");
			vo.setInt("id",Integer.parseInt(id));
			vo=dao.findByPrimaryKey(vo);
			ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
			
			String info=ab.validateReportFill(unitcode,vo);
			
			if(info.length()>0){
				//errorStr.append(info);
				throw new GeneralException(info);
			}
		
			if(vo.getInt("kmethod")==0) //完整精算评估  校验是否有警告，并填写相关原因
			{ 
				ArrayList dataHeadList=ab.getDataHeadList_U03();
				ArrayList dataHeadList_u05=ab.getDataHeadList_U05();
				ArrayList compareDataList=ab.getCompareDataList(id,unitcode,dataHeadList);
				ArrayList compareDataList_5=ab.getCompareDataList_u05(id,unitcode,vo.getString("theyear"),dataHeadList_u05);
				
				if((compareDataList.size()>0||compareDataList_5.size()>0))
				{
					info=ab.checkCompareDataList(compareDataList,compareDataList_5,id,unitcode,dataHeadList,dataHeadList_u05);  // 1:成功 0：表3不成功 －1：表5不成功
					if("0".equals(info)){
						errorStr.append("\r\n表3数据存在警告，需对警告内容进行文字解释方允许上报!");
						//throw new GeneralException("表3数据存在警告，需对警告内容进行文字解释方允许上报!");
					}
					else if("-1".equals(info)){
						errorStr.append("\r\n表5数据存在警告，需对警告内容进行文字解释方允许上报!");
						//throw new GeneralException("表5数据存在警告，需对警告内容进行文字解释方允许上报!");
					}
					else if("-2".equals(info)){
						errorStr.append("\r\n表3,表5 数据存在警告，需对警告内容进行文字解释方允许上报!");
						//throw new GeneralException("表3,表5 数据存在警告，需对警告内容进行文字解释方允许上报!");
					}
				}
			}
//			if(vo.getInt("kmethod")==1) //向前滚动评估  校验原有人员是否超过上一次的
//			{
//				
//			}
			RecordVo _vo=new RecordVo("u01");
			_vo.setInt("id", Integer.parseInt(id));
			_vo.setString("unitcode", unitcode);
			String paracopy=(String)this.getFormHM().get("paracopy");
			String paracopy2=(String)this.getFormHM().get("paracopy2");
			String para[] = paracopy.split(",");
			String para2[] = paracopy2.split(",");
			String temp="";
			EditReport edit = new EditReport();
			ArrayList listu01  = edit.getU01FieldList(this.getFrameconn());
			StringBuffer insertsql=new StringBuffer();
			StringBuffer updatesql =new StringBuffer();
			
			if(listu01.size()>0){
				updatesql.append(" update u01 set id="+id+" ");
				insertsql.append(" insert into u01(id,unitcode,");
				StringBuffer inservalue = new StringBuffer();
				inservalue.append(" )values("+id+",'"+unitcode+"',");
				int m =0;
				for(int i =0;i<listu01.size();i++){
					
					FieldItem fielditem = (FieldItem)listu01.get(i);
					if(fielditem==null)
						continue;
				String itemid =	fielditem.getItemid();
				insertsql.append(itemid+",");
				String desc =fielditem.getItemdesc();
				desc = desc.replace(",","");
				String type =fielditem.getItemtype();
				int length =fielditem.getItemlength();
				String date =PubFunc.FormatDate(new java.util.Date(),"yyyy-MM-dd");
				if("D".equalsIgnoreCase(type)){
					if(this.getFormHM().get(desc)==null|| "".equals(this.getFormHM().get(desc))){
						inservalue.append("'"+date+"',");
						updatesql.append(" , "+itemid+"='"+date+"'");
					}else{
						inservalue.append("'"+this.getFormHM().get(desc)+"',");
						updatesql.append(" , "+itemid+"='"+this.getFormHM().get(desc)+"'");	
					}
					
				}else{
				inservalue.append("'"+this.getFormHM().get(desc)+"',");
				updatesql.append(" , "+itemid+"='"+this.getFormHM().get(desc)+"'");
				}
				}
				insertsql.setLength(insertsql.length() - 1);
				inservalue.setLength(inservalue.length() - 1);
				inservalue.append(")");
				insertsql.append(inservalue);
				updatesql.append(" where id="+id+" and unitcode='"+unitcode+"'");
				
			}
					insert = insertsql.toString();
					update = updatesql.toString();
				
				 
					//System.out.println(insertsql.toString());
					// System.out.println(updatesql.toString());
					 
				
				
		
			
			for(int i=0;i<para.length;i++){
				 temp = (String)this.getFormHM().get(para[i]);
				 
			}
			for(int i=0;i<para2.length;i++){
				 temp = (String)this.getFormHM().get(para2[i]);
			}
			String infou01="";
			try
			{
				_vo=dao.findByPrimaryKey(_vo);
			}
			catch(Exception ee)
			{
				infou01 ="\r\n表1内容没有填写完整不允许上报!";
				//throw new GeneralException("表1内容没有填写完整不允许上报!");
			}
			if(_vo.getString("u0101")==null||_vo.getString("u0101").trim().length()==0){
				infou01 ="\r\n表1内容没有填写完整不允许上报!";
				//	throw new GeneralException("表1内容没有填写完整不允许上报!");
			}
			if(_vo.getString("u0103")==null||_vo.getString("u0103").trim().length()==0){
				infou01 ="\r\n表1内容没有填写完整不允许上报!";
				//	throw new GeneralException("表1内容没有填写完整不允许上报!");
			}
			try
			{
				dao.findByPrimaryKey(_vo);
				if(!"".equals(update))
				 dao.update(update);
			}
			catch(Exception ee)
			{
				if(!"".equals(insert))
				 dao.insert(insert, new ArrayList());
			}
		
			if(!"".equals(infou01))
				errorStr.append(infou01);
			if(!"".equals(errorStr.toString()))	{
				throw new GeneralException(errorStr.toString());
			}
			
			ab.subAllReport(unitcode,vo,_vo,insert,update);
			
		
		
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
			
		}

	}

}
