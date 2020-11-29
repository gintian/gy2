package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
 */

public class GroPayMentTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm = this.getFormHM();
			HashMap reqhm=(HashMap) hm.get("requestPamaHM");
			String ctrl_type = (String)hm.get("ctrl_type");
			String cascadingctrl=(String)hm.get("cascadingctrl");
			String viewUnit=(String)hm.get("viewUnit");
			String code = "";
			String filtervalue = (String)hm.get("filtervalue");
			
			String spType = (String)hm.get("spType");
			//20180720  zxj 严格过滤，防止注入
            spType = PubFunc.hireKeyWord_filter(spType);        
            if(null == AdminCode.getCode("23", spType)) 
                spType = "";
            
			String opt=(String)reqhm.get("opt");
			if("init".equalsIgnoreCase(opt))
				code=(String)reqhm.get("a_code");			
			else			
				code=(String)hm.get("code");
											
			/* 薪资总额月份保存优化 xiaoyun 2014-10-23 start */
			/*if(filtervalue==null||opt.equalsIgnoreCase("init"))
				filtervalue="0";*/	
			if(filtervalue == null) {
				filtervalue = "0";
			}
			/* 薪资总额月份保存优化 xiaoyun 2014-10-23 end */
			//reqhm.remove("a_code");
			code=code!=null&&code.trim().length()>0?code:"";
			
			String codeitemid = (String)hm.get("codeitemid");
			codeitemid=codeitemid!=null&&codeitemid.trim().length()>0?codeitemid:"";
			if(code.trim().length()>=2){
				codeitemid = code.substring(2,code.length());
			}
			else
				codeitemid="";
			
			String yearnum = (String)hm.get("yearnum");
			yearnum=yearnum!=null&&yearnum.trim().length()>0?yearnum:"";
			if(/*opt.equalsIgnoreCase("init")||*/yearnum.trim().length()!=4){
				Calendar  calendar = Calendar.getInstance();
				yearnum = calendar.get(Calendar.YEAR)+"";
			}
	
			GrossManagBo gross = new GrossManagBo(this.getFrameconn(),this.getUserView());
			gross.setCascadingctrl(cascadingctrl);
			gross.setViewUnit(viewUnit);
			String fieldsetid = (String)hm.get("fieldsetid");
			fieldsetid=fieldsetid!=null&&fieldsetid.trim().length()>0?fieldsetid:"";
			
			String spflagid = (String)hm.get("spflagid");
			spflagid=spflagid!=null&&spflagid.trim().length()>0?spflagid:"";
			
			//GrossPayManagement gpm = new GrossPayManagement(this.frameconn,"GZ_PARAM");
	
			GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap map =bo.getValuesMap();
			String hasParam="0";
			String fc_flag="";
			String ctrlAmountField="";
			if(map==null)
			{
				//throw GeneralExceptionHandler.Handle(new Exception("薪资总额参数未定义"));
				hasParam="1";
				this.getFormHM().put("hasParam", hasParam);
				return;
			}
			if(fieldsetid.trim().length()<1)
			{
				//ArrayList setidlist = gpm.elementName("/Params/Gz_amounts","setid");
				fieldsetid = ((String)map.get("setid")).length()>0?(String)map.get("setid"):"";
				if(fieldsetid==null|| "".equals(fieldsetid.trim()))
				{
					//throw GeneralExceptionHandler.Handle(new Exception("薪资总额参数未定义"));
					hasParam="1";
					this.getFormHM().put("hasParam", hasParam);
					return;
				}
			}
			fc_flag=(String)map.get("fc_flag");
			String ctrl_peroid=(String)map.get("ctrl_peroid");//年月控制标识=1按年，=0按月=2按季度
			if(ctrl_peroid==null|| "".equals(ctrl_peroid))
				ctrl_peroid="0";
			if(spflagid.trim().length()<1)
			{
			//	ArrayList spflaglist = gpm.elementName("/Params/Gz_amounts","sp_flag");
				spflagid = ((String)map.get("sp_flag")).length()>0?(String)map.get("sp_flag"):"";
			}
			if(StringUtils.isBlank(spType) || (spflagid==null || spflagid.trim().length()<=0)/*||opt.equalsIgnoreCase("init")*/)
				spType = "0";
			String sql="";
			String un = "ctrl_item";
			ArrayList dataList = new ArrayList();
			dataList=(ArrayList) map.get(un.toLowerCase());
			if(fc_flag!=null && fc_flag.length()!=0)
				gross.setFc_flag(fc_flag);
			
			if(dataList==null||dataList.size()==0)
			{
				//throw GeneralExceptionHandler.Handle(new Exception("薪资总额参数未定义计划项目，实发项目和剩余项目参数!"));
				hasParam="1";
				this.getFormHM().put("hasParam", hasParam);
				return;
			}
			if(map.get("ctrl_field")!=null)
			{
				ctrlAmountField=(String)map.get("ctrl_field");
			}
			String isCanCreate=gross.isCanCreateRecord(codeitemid, ctrlAmountField);
			if("1".equals(ctrl_peroid))// by year
			{
				sql=fieldsetid!=null&&fieldsetid.length()>0?gross.getColumnSql(fieldsetid, yearnum, spflagid, dataList, codeitemid, ctrl_type, spType, "0"):"";
			}
			else if("2".equals(ctrl_peroid))// by quarter
			{
				String filtersql = gross.getFilterSql(fieldsetid+"z0a", filtervalue, ctrl_peroid, spflagid, spType);
				sql = fieldsetid!=null&&fieldsetid.length()>0?gross.getColumnSqlBySeason(fieldsetid, yearnum, spflagid, dataList, codeitemid, ctrl_type,filtervalue,filtersql, "0"):"";
			}
			else//by month
			{
				String filtersql = gross.getFilterSql(fieldsetid+"z0", filtervalue, ctrl_peroid, spflagid, spType);
				sql = fieldsetid!=null&&fieldsetid.length()>0?gross.sqlStr(fieldsetid,yearnum,codeitemid,spflagid,ctrl_type,ctrl_peroid,1,filtervalue,filtersql, "0"):"";
			}		
			hm.put("isCanCreate", isCanCreate);
			hm.put("ctrlAmountField",ctrlAmountField);
		    hm.put("sqlstr",sql);
			hm.put("gz_grossname",fieldsetid);
			ArrayList idlist = gross.fieldList(ctrl_peroid,fieldsetid,spflagid,dataList);
			if("1".equals(ctrl_peroid)|| "2".equals(ctrl_peroid))
			{
				Field newField = new Field("b0110a", "明细");
				newField.setReadonly(true);
				newField.setSortable(true);
				idlist.add(newField);
				if("2".equals(ctrl_peroid))
				{
					Field newield = new Field("season", "newield");
					newield.setReadonly(true);
					newield.setSortable(true);
					newield.setVisible(false);
					idlist.add(newield);
				}
				Field newd = null;
				if("1".equals(ctrl_peroid))
					newd=new Field(fieldsetid+"z0b", "年份");
				if("2".equals(ctrl_peroid))
					newd=new Field(fieldsetid+"z0b", "季度");
				newd.setReadonly(true);
				newd.setSortable(true);
				if(fc_flag!=null && fc_flag.trim().length()>0)
					idlist.add(3,newd);
				else
					idlist.add(2,newd);
			}
			ArrayList filterList = gross.getFilterList(ctrl_peroid);			
			ArrayList spTypeList = gross.getSpTypeList();
			hm.put("filterList",filterList);
			hm.put("spTypeList",spTypeList);
			hm.put("spflagid",spflagid);
			hm.put("fieldlist",idlist);
			hm.put("yearnum",yearnum);
			hm.put("codeitemid",codeitemid);
			hm.put("fieldsetid",fieldsetid);
			hm.put("code",code);
			hm.put("ctrl_peroid",ctrl_peroid);
			
			if("1".equals(ctrl_peroid))// 如果是按年那么显示计算按钮
				filtervalue=yearnum;
			
			hm.put("filtervalue", filtervalue);
			hm.put("spType", spType);
			hm.put("hasParam", hasParam);
			hm.put("fc_flag", fc_flag);
			String amountAdjustSet="-1";
			if(map!=null&&map.get("amountAdjustSet")!=null&&((String)map.get("amountAdjustSet")).trim().length()>0)
				amountAdjustSet=(String)map.get("amountAdjustSet");
			this.getFormHM().put("isHasAdjustSet", amountAdjustSet);
			String fcVisible ="0";
			if(map.get("fc_flag")!=null&&((String)map.get("fc_flag")).trim().length()>0)
				fcVisible="1";
			this.getFormHM().put("fcVisible", fcVisible);//是否设置封存指标参数
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
