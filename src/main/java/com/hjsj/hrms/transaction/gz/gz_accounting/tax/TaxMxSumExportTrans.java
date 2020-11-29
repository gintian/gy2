package com.hjsj.hrms.transaction.gz.gz_accounting.tax;


import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.businessobject.gz.TaxMxExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class TaxMxSumExportTrans extends IBusiness{
	
	private String title="";
	public void execute() throws GeneralException 
	{
		FileInputStream fis=null;
		try
		{
			TaxMxBo tmb = new TaxMxBo(this.getFrameconn(),this.getUserView());
			String declaredate=(String)this.getFormHM().get("declaredate");
			String a_code=(String)this.getFormHM().get("a_code");
			String condtionsql=(String)this.getFormHM().get("condtionsql");
			 
			/* 安全问题 所得税管理 文件下载 xiaoyun 2014-9-12 start */
			condtionsql = PubFunc.decrypt(SafeCode.decode(condtionsql));
			/* 安全问题 所得税管理 文件下载 xiaoyun 2014-9-12 end */
			String filterByMdule=(String)this.getFormHM().get("filterByMdule");
			String fromtable=(String)this.getFormHM().get("fromtable");
			if(fromtable==null)
				fromtable="gz_tax_mx";
			String condition = getFilterCond(declaredate);
			StringBuffer strwhere = new StringBuffer();
			if(condition.length()>0)
			{
				strwhere.append(" and "+condition);
			}
			/**选择的机构树范围*/
			String deptid=tmb.getDeptID();
			if(!(a_code==null|| "".equalsIgnoreCase(a_code)))
			{
				String codesetid=a_code.substring(0, 2);
	    		String value=a_code.substring(2);
				if("false".equalsIgnoreCase(deptid))
				{
					if("UN".equalsIgnoreCase(codesetid))
			    	{
				    	strwhere.append(" and b0110 like '");
				    	strwhere.append(value);
				    	strwhere.append("%'");
				    }
			    	if("UM".equalsIgnoreCase(codesetid))
			    	{
				    	strwhere.append(" and e0122 like '");
				    	strwhere.append(value);
				    	strwhere.append("%'");
			    	}
				}
				else
				{
					
					/* 所得税管理/文件/导出申报汇总表，导出的数据不对 xiaoyun 2014-10-11 start */
					if(StringUtils.isNotEmpty(value)) {
						strwhere.append(" and deptid like '");
						strwhere.append(value);
						strwhere.append("%'");
					}				   
				    /* 所得税管理/文件/导出申报汇总表，导出的数据不对 xiaoyun 2014-10-11 end */
					           
				}
			}
			if(!(condtionsql==null|| "".equalsIgnoreCase(condtionsql)))
			{
				strwhere.append(" and "+condtionsql);
			}
			/**管理范围*//*
			if(!this.userView.isSuper_admin()&&!this.userView.getGroupId().equals("1"))
			{
				String code=this.userView.getManagePrivCode();
	        	 String value=this.userView.getManagePrivCodeValue();
	        	 if(code==null)
	        	 {
	        		 strwhere.append(" and 1=2 ");
	        	 }
	        	 else if(code.equalsIgnoreCase("UN"))
	        	 {
	        		 strwhere.append(" and (b0110 like '");
	        		 strwhere.append((value==null?"":value)+"%'");
	        		 if(value==null)
	        		 {
	        			 strwhere.append(" or b0110 is null ");
	        		 }
	        		 strwhere.append(")");
	        	 }
	        	 else if(code.equalsIgnoreCase("UM"))
	        	 {
	        		 strwhere.append(" and (e0122 like '");
	        		 strwhere.append((value==null?"":value)+"%'");
	        		 if(value==null)
	        		 {
	        			 strwhere.append(" or e0122 is null ");
	        		 }
	        		 strwhere.append(")");
	        	 }
			}
			*/
			String templateName = (String)this.getFormHM().get("templateName");
			String outname = "";
			if(!(templateName==null || "".equals(templateName)))
			{
				String path = SafeCode.decode((String)this.getFormHM().get("path"));
				/**没选择模板*/
				if("请选择...".equalsIgnoreCase(templateName))
				{
					/* 安全问题处理 所得税管理 文件下载 xiaoyun 2014-9-12 start */
					//outname=tmb.exportDefaultExcel(fromtable,strwhere.toString(),declaredate,this.getUserView(),filterByMdule).replaceAll(".xls","#");
					outname=tmb.exportDefaultExcel(fromtable,strwhere.toString(),declaredate,this.getUserView(),filterByMdule);
					/* 安全问题处理 所得税管理 文件下载 xiaoyun 2014-9-12 end */
				}else
				{
					File file = new File(path,templateName);
				    fis =  new FileInputStream(file);				
					ArrayList list = this.getExcelDataFiledList(fis);
					HashMap hm = this.getAddField(list);
					if(list.size()>0)
					{
						/* 安全问题处理 所得税管理 文件下载 xiaoyun 2014-9-12 start */
						//outname=tmb.exportMxSumExcel(fromtable,strwhere.toString(),declaredate,list,hm,this.title,this.getUserView(),filterByMdule).replaceAll(".xls","#");
						outname=tmb.exportMxSumExcel(fromtable,strwhere.toString(),declaredate,list,hm,this.title,this.getUserView(),filterByMdule);
						/* 安全问题处理 所得税管理 文件下载 xiaoyun 2014-9-12 end */
					}else		
					{
						/* 安全问题处理 所得税管理 文件下载 xiaoyun 2014-9-12 start */
						//outname=tmb.exportMinExcel(fromtable,strwhere.toString(),declaredate,this.getUserView(),filterByMdule).replaceAll(".xls","#");
						outname=tmb.exportMinExcel(fromtable,strwhere.toString(),declaredate,this.getUserView(),filterByMdule);
						/* 安全问题处理 所得税管理 文件下载 xiaoyun 2014-9-12 end */
					}
				}
			}else{
				/* 安全问题处理 所得税管理 文件下载 xiaoyun 2014-9-12 start */
				//outname=tmb.exportDefaultExcel(fromtable,strwhere.toString(),declaredate,this.getUserView(),filterByMdule).replaceAll(".xls","#");
				outname=tmb.exportDefaultExcel(fromtable,strwhere.toString(),declaredate,this.getUserView(),filterByMdule);
				/* 安全问题处理 所得税管理 文件下载 xiaoyun 2014-9-12 end */
			}
			/* 安全问题处理 所得税管理 文件下载 xiaoyun 2014-9-12 start */
			//this.getFormHM().put("outName",outname);
			this.getFormHM().put("outName",SafeCode.encode(PubFunc.encrypt(outname)));
			/* 安全问题处理 所得税管理 文件下载 xiaoyun 2014-9-12 end */
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			try
			{
				if(fis!=null)
				{
					fis.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public HashMap getAddField(ArrayList list)
	{
		StringBuffer sb = new StringBuffer();
		HashMap hm = new HashMap();
		try
		{
			for(int i=0;i<list.size();i++)
			{
				String field = (String)list.get(i);
				if(!(field==null || "".equals(field) || "jtynse".equalsIgnoreCase(field) || "jtSds".equalsIgnoreCase(field)))
					sb.append(",sum("+field+") as "+field+"");
				
			}		
			hm.put("t",sb.toString());
			sb.delete(0,sb.length());
			for(int i=0;i<list.size();i++)
			{
				String field = (String)list.get(i);
				if(!(field==null || "".equals(field) || "jtynse".equalsIgnoreCase(field) || "jtSds".equalsIgnoreCase(field)))
					sb.append(",e."+field+"");
				
			}			
			hm.put("e",sb.toString());
			sb.delete(0,sb.length());
			for(int i=0;i<list.size();i++)
			{
				String field = (String)list.get(i);
				if(!(field==null || "".equals(field) || "jtynse".equalsIgnoreCase(field) || "jtSds".equalsIgnoreCase(field)))
					sb.append(",d."+field+"");
				
			}
			
			hm.put("d",sb.toString());
			sb.delete(0,sb.length());
			for(int i=0;i<list.size();i++)
			{
				String field = (String)list.get(i);
				if(!(field==null || "".equals(field) || "jtynse".equalsIgnoreCase(field) || "jtSds".equalsIgnoreCase(field)))
					sb.append(",a."+field+"");
				
			}
			
			hm.put("a",sb.toString());
			sb.delete(0,sb.length());
//			for(int i=0;i<list.size();i++)
//			{
//				String field = (String)list.get(i);
//				if(!(field==null || field.equals("") || field.equalsIgnoreCase("ynse") || field.equalsIgnoreCase("Sds")))
//					sb.append(",a."+field+"");
//				
//			}
//			sb.delete(0,sb.length());
//			hm.put("a",sb.toString());
			for(int i=0;i<list.size();i++)
			{
				String field = (String)list.get(i);
				if(!(field==null || "".equals(field) || "jtynse".equalsIgnoreCase(field) || "jtSds".equalsIgnoreCase(field)
						))
					sb.append(",sum("+field+") as "+field);
				
			}
			
			hm.put("x",sb.toString());
			sb.delete(0,sb.length());
			for(int i=0;i<list.size();i++)
			{
				String field = (String)list.get(i);
				if(!(field==null || "".equals(field) || "jtynse".equalsIgnoreCase(field) || "jtSds".equalsIgnoreCase(field)
						|| "ynse".equalsIgnoreCase(field) || "Sds".equalsIgnoreCase(field)))
					sb.append(",sum("+field+") as "+field);
				
			}
			
			hm.put("s",sb.toString());
			sb.delete(0,sb.length());
			for(int i=0;i<list.size();i++)
			{
				String field = (String)list.get(i);
				if(!(field==null || "".equals(field) || "jtynse".equalsIgnoreCase(field) || "jtSds".equalsIgnoreCase(field)))
					sb.append(",sum("+Sql_switcher.isnull(field,"0")+") as "+field);
				
			}			
			hm.put("f",sb.toString());//zhaoxg add 2015-2-13 
			sb.delete(0,sb.length());
		}
		catch(Exception e)
		{
			e.printStackTrace();

		}
		return hm;
	}
	public ArrayList getClom(ArrayList list)
	{
		StringBuffer sb = new StringBuffer();
		ArrayList ret = new ArrayList();
		try
		{
			for(int i=0;i<list.size();i++)
			{
				String field = (String)list.get(i);
				sb.append(",sum("+field+")");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	public ArrayList getExcelDataFiledList(FileInputStream fileInputStream)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			TaxMxExcelBo tmeb=new TaxMxExcelBo(this.frameconn);
			tmeb.getSelfAttributeTwo(fileInputStream);
			list=tmeb.getExportFieldList(2);
			this.title=tmeb.getTemplateTitle(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	/**
	 * 取得报税时间过滤条件
	 * @param declaredate
	 * @return
	 */
	private String getFilterCond(String declaredate)
	{	
		StringBuffer buf=new StringBuffer();
		if(declaredate==null|| "".equalsIgnoreCase(declaredate)|| "all".equalsIgnoreCase(declaredate))
			return "";
		String[] datearr=StringUtils.split(declaredate, ".");
		String theyear=datearr[0];
		String themonth=datearr[1];
		buf.append(Sql_switcher.year("Declare_tax"));
		buf.append("=");
		buf.append(theyear);
		buf.append(" and ");
		buf.append(Sql_switcher.month("Declare_tax"));
		buf.append("=");
		buf.append(themonth);		
		return buf.toString();
	}
}