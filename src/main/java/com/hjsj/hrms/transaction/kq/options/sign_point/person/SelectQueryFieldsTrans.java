package com.hjsj.hrms.transaction.kq.options.sign_point.person;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>
 * Title:SelectQueryFieldsTrans
 * </p>
 * <p>
 * Description:选择查询指标
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2006-4-28:13:38:11
 * </p>
 * 
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SelectQueryFieldsTrans extends IBusiness {

	/**
	 * 查找是否存在相同的因子对象
	 * 
	 * @param name
	 * @param list
	 * @return
	 */
	private Factor findFactor(String name, ArrayList list, int index) {
		Factor factor = null;
		for (int i = 0; i < list.size(); i++) {
			factor = (Factor) list.get(i);
			if (name.equalsIgnoreCase(factor.getFieldname()) && (i == index))
				break;
			factor = null;
		}
		return factor;
	}
	/**
	 * 取得逻辑操作符
	 * @param expr
	 * @return
	 */
	private String getDelimiters(String expr)
	{
		StringBuffer sv=new StringBuffer();
		for(int i=0;i<expr.length();i++)
		{
			if(expr.charAt(i)=='+'||expr.charAt(i)=='*')
				sv.append(expr.charAt(i));
		}
		return sv.toString();
	}
	
	private void setFactorLog(Factor factor,int index,String strLog)
	{
		if(index==0)
			return;
		if(index-1>strLog.length())
			return;
		if(strLog.length()==0)
			return;
		StringBuffer  log=new StringBuffer();
		if(strLog.length() >= index){
			log.append(strLog.charAt(index-1));
		}else
			return;
		factor.setLog(log.toString());
	}
	private void reParser(String expr,ArrayList newfactorlist)throws GeneralException
	{
		if(expr==null|| "".equals(expr))
			return;
		try
		{
			ArrayList list=null;
			int idx=expr.indexOf('|');
			String expression=expr.substring(0,idx);
			String strfactor=expr.substring(idx+1);
			FactorList factorlist=new FactorList(expression,strfactor,"");
			/**简单查询*/
			String strlog=getDelimiters(expression);			
			for(int i=0;i<newfactorlist.size();i++)
			{
				Factor factor=(Factor)newfactorlist.get(i);
				if(i<factorlist.size())
				{
					Factor oldfactor=(Factor)factorlist.get(i);
					if(factor.getFieldname().equalsIgnoreCase(oldfactor.getFieldname()))
					{
						factor.setLog(PubFunc.keyWord_reback(oldfactor.getLog()));
						factor.setLog(oldfactor.getLog());
						factor.setOper(oldfactor.getOper());
						factor.setValue(oldfactor.getValue());
						factor.setHzvalue(oldfactor.getHzvalue());
						factor.setHz(oldfactor.getHz());
					}
					setFactorLog(factor,i,strlog);					
				}

			}

			
			
			this.getFormHM().put("expression",expression.toString());			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	public void execute() throws GeneralException {
		String[] fields = (String[]) this.getFormHM().get("right_fields");
		String type=(String)this.getFormHM().get("type");
        String strexprsave=(String)this.getFormHM().get("expression");
        strexprsave = PubFunc.keyWord_reback(strexprsave);
		if(type==null|| "".equals(type))
			type="1";
		if (fields == null || fields.length == 0) {
			throw new GeneralException(ResourceFactory.getProperty("errors.query.notexistfield"));
		}
		int j = 0;
		StringBuffer strexpr = new StringBuffer();
		ArrayList list = new ArrayList();
		ArrayList fieldlist=new ArrayList();
		/** 信息类型定义default=1（人员类型） */
		int nInform = Integer.parseInt(type);
		try {
			/** 保存的因子对象列表 */
			ArrayList factorlist = (ArrayList) this.getFormHM().get(
					"factorlist");
			/** 定义条件项 */
			FieldItem item = null;
			for (int i = 0; i < fields.length; i++) {
				String fieldname = fields[i];
				if (fieldname == null || "".equals(fieldname))
					continue;
				cat.debug("field_name=" + fieldname);
				item = DataDictionary.getFieldItem(fieldname.toUpperCase());
				Factor factor = null;
				if (item != null) {
					/**选中的指标列表*/
					CommonData vo=new CommonData();
					vo.setDataName(item.getItemdesc());
					vo.setDataValue(item.getItemid());
					fieldlist.add(vo);							
					/** 已定义的因子再现 */
					if (factorlist != null) {
						factor = findFactor(fieldname, factorlist, i);
						if (factor != null) {
							list.add(factor);
							continue;
						}
					}
					factor = new Factor(nInform);
					factor.setCodeid(item.getCodesetid());
					factor.setFieldname(item.getItemid());
					factor.setHz(item.getItemdesc());
					factor.setFieldtype(item.getItemtype());
					factor.setItemlen(item.getItemlength());
					factor.setItemdecimal(item.getDecimalwidth());
					factor.setOper("=");// default
					factor.setLog("*");// default
					list.add(factor);
					++j;
					strexpr.append(j);
					strexpr.append("*");
				}

			}
			if (strexpr.length() > 0)
				strexpr.setLength(strexpr.length() - 1);
			/**重新解释查询表达式*/
			String expr=(String)this.getFormHM().get("expr");
			expr = PubFunc.keyWord_reback(expr);
			reParser(expr,list);	
			
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
	    	if(uplevel==null||uplevel.length()==0)
	    		uplevel="0";
	    	this.getFormHM().put("uplevel", uplevel);
		} catch (Exception ee) {
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		} finally {
			this.getFormHM().put("factorlist", list);
			this.getFormHM().put("selectedlist",fieldlist);
			if(strexprsave==null|| "".equals(strexprsave))
				this.getFormHM().put("expression",strexpr.toString());
		}
	}

}
