package com.hjsj.hrms.module.gz.salaryaccounting.batch.businessobject;

import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：BatchUpdateBo 
 * 类描述：执行批量修改Bo类
 * 创建人：sunming
 * 创建时间：2015-7-23
 * @version
 */
public class BatchUpdateBo {
	private Connection conn=null;
	/**薪资表名称*/
	private String gz_tablename;
	/**薪资类别号*/
	private int salaryid=-1;
	/**登录用户*/
	private UserView userview;
	/**薪资控制参数*/
	private SalaryCtrlParamBo ctrlparam=null;
	private String   manager="";  //工资管理员，对共享类别有效; 
	private SalaryAccountBo salaryAccountBo=null; 
	private SalaryTemplateBo salaryTemplateBo=null;
	/**薪资类别数据对象*/
	private RecordVo templatevo=null; 	
	public BatchUpdateBo(Connection conn,int salaryid,UserView userview)
	{
		this.conn = conn;
		this.userview = userview;
		this.salaryid = salaryid;
		this.salaryAccountBo=new SalaryAccountBo(conn,this.userview,salaryid);
		this.salaryTemplateBo=this.salaryAccountBo.getSalaryTemplateBo();
		this.templatevo=this.salaryTemplateBo.getTemplatevo();
		this.manager=this.salaryTemplateBo.getManager();
		this.ctrlparam=this.salaryTemplateBo.getCtrlparam();
		this.gz_tablename=this.salaryTemplateBo.getGz_tablename();
	}
	
	/**
	 * 批量修改某个薪资项目数据
	 * @param itemid     目标指标
	 * @param formula    计算公式
	 * @param cond       修改条件
	 * @param whl        过滤条件
	 * @return
	 * @throws GeneralException
	 */
	public boolean batchUpdateItem(String itemid,String formula,String cond,String whl)throws GeneralException
	{
		boolean bflag=true;
		boolean _flag=true;
		try
		{	
			YksjParser yp=null;
			ArrayList fldvarlist = new ArrayList();
			fldvarlist.clear();
		    fldvarlist.addAll(this.salaryTemplateBo.getMidVarItemList(String.valueOf(salaryid)));
			fldvarlist.addAll(this.salaryTemplateBo.getSalaryItemList("",String.valueOf(salaryid),2));
		    StringBuffer strwhere=new StringBuffer();
			//对条件定义进行处理
			if(cond.length()==0|| "undefined".equalsIgnoreCase(cond))
				strwhere.append(" where 1=1");
			else
			{
				strwhere.append(" where ");
				yp = new YksjParser( this.userview ,fldvarlist,
						YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
				yp.run_where(cond);
				String strfilter=yp.getSQL();				
				strwhere.append(strfilter);
			}
			// 需要审批||控制已提交的数据是否能批量修改 
			String flow_flag=this.ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag"); 
			SalaryLProgramBo lpbo=new SalaryLProgramBo(this.salaryTemplateBo.getTemplatevo().getString("lprogram"));
			String allowEditSubdata=lpbo.getValue(SalaryLProgramBo.CONFIRM_TYPE,"allow_edit_subdata");       //薪资发放 是否允许提交后更改数据；具有 “允许提交后更改数据”   
			if("1".equals(flow_flag)||!"1".equals(allowEditSubdata))
			{
				strwhere.append(" and sp_flag in('01','07')");
			}	
			//共享薪资类别，其他操作人员引入数据 
			if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager))
			{
				strwhere.append(" and sp_flag2 in ('01','07')");
			}
			
			FieldItem fielditem=DataDictionary.getFieldItem(itemid);
			String datatype="A";
			if(fielditem==null)
			{
				if("A00Z0".equalsIgnoreCase(itemid))
					datatype="D";
				if("A00Z1".equalsIgnoreCase(itemid))
					datatype="N";
			}
			else
				datatype=fielditem.getItemtype();

			String strexpr="";
			if("D".equals(datatype)&&(formula.split("\\.").length==3||formula.split("-").length==3))
			{
				formula=formula.replaceAll("＃","#");
				if(formula.charAt(0)!='#'||formula.charAt(formula.length()-1)!='#')
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.dateFormatError")));	//日期格式不正确,格式为 #yyyy-mm-dd#!
				formula=formula.replaceAll("#","");
				String[] temp=null;
				if(formula.split("\\.").length==3)
					temp=formula.split("\\.");
				else
					temp=formula.split("-");
				Calendar d=Calendar.getInstance();
				try
				{
					d.set(Calendar.YEAR,Integer.parseInt(temp[0]));
					d.set(Calendar.MONTH,Integer.parseInt(temp[1])-1);
					d.set(Calendar.DATE,Integer.parseInt(temp[2]));
				}
				catch(Exception ee)
				{
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.dateError")));		//日期格式不正确!
				}
				
				String aflag=ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
				//共享薪资类别，其他操作人员引入数据 
				if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)&&aflag!=null&& "1".equals(aflag))
				{
					String dbpres=this.templatevo.getString("cbase");
					//应用库前缀
					String[] dbarr=StringUtils.split(dbpres, ",");
					for(int i=0;i<dbarr.length;i++)
					{
						String pre=dbarr[i];
						this.getBatchUpdateSql(itemid, whl, strwhere, d, pre,true);
					}
				}
				else
				{
					this.getBatchUpdateSql(itemid, whl,strwhere, d, "",false);
				}
				
			}
			else
			{
				int length = 0;
				int decimalwidth = 0;
				if(!"A00Z0".equalsIgnoreCase(itemid)&&!"A00Z1".equalsIgnoreCase(itemid)){
					length=fielditem.getItemlength();
					decimalwidth = fielditem.getDecimalwidth();
				}else{
					length = 15;
					decimalwidth = 0;
				}
			 
				if("N".equalsIgnoreCase(datatype)&&isNum(formula)){
					if(formula.indexOf(".")!=-1){
						String[] temp=formula.split("\\.");
						if(decimalwidth<temp[1].length()){
							 _flag=false;						 
							
							 throw GeneralExceptionHandler.Handle(new Exception( ResourceFactory.getProperty("gz_new.gz_accounting.inputDecimallength")+decimalwidth+ResourceFactory.getProperty("gz_new.gz_accounting.digit")));//输入内容的小数位不要长于 位！ 
						}
						 if(temp[0].length()>length){
							 _flag=false;
							 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.inputIntegerlength")+length+ResourceFactory.getProperty("gz_new.gz_accounting.byte")));//"输入内容的整数位不要长于" "个字符！"
						 }
					}else{
						 if(formula.length()>length){
							 _flag=false;
							 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.inputlength")+length+ResourceFactory.getProperty("gz_new.gz_accounting.byte")));//"输入内容的长度不要长于" "个字符！"
						 }
					}

				}
				yp=new YksjParser( this.userview ,fldvarlist,
						YksjParser.forNormal, getDataType(datatype),YksjParser.forPerson , "Ht", "");
				yp.run(formula,this.conn,cond,this.gz_tablename);
				/**单表计算*/
				strexpr=yp.getSQL();	
				/**为空不计算*/
				if(strexpr.trim().length()==0)
					return true;
				if(("a00z0".equalsIgnoreCase(itemid)|| "a00z1".equalsIgnoreCase(itemid))&& "NULL".equalsIgnoreCase(strexpr.trim()))
					return true;

				String aflag=ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
				//共享薪资类别，其他操作人员引入数据 
				if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)&&aflag!=null&& "1".equals(aflag))
				{
					String dbpres=this.templatevo.getString("cbase");
					//应用库前缀
					String[] dbarr=StringUtils.split(dbpres, ",");
					for(int i=0;i<dbarr.length;i++)
					{
						String pre=dbarr[i];
						_flag = getBatchUpdateSql_strexpr(itemid, whl,strwhere, datatype, strexpr, length, pre,true);
					}
				}
				else
				{
					_flag = getBatchUpdateSql_strexpr(itemid, whl,strwhere, datatype, strexpr, length, "",false);
				}
				
				
			}
		}
		catch(Exception ex)
		{
		    ex.printStackTrace();
			bflag=false;
			if(_flag){
				if(ex.toString().indexOf(ResourceFactory.getProperty("gz_new.gz_accounting.unique"))!=-1){//唯一
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.batchUpdateError")));//"同一个人有多条薪资数据，不能执行归属次数的批量修改！"
				}else{
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.inputError")));//输入内容格式不正确！
				}
				
			}else{
				throw GeneralExceptionHandler.Handle(ex);
			}
						
		}
		return bflag;	
	}
	/**
	 * 单表计算中的批量更新方法
	 * @param itemid     目标指标
	 * @param formula    计算公式
	 * @param cond       修改条件
	 * @param whl        过滤条件
	 * @param _flag
	 * @param strwhere   过滤条件
	 * @param datatype   类型
	 */
	private boolean getBatchUpdateSql_strexpr(String itemid, String whl,
			StringBuffer strwhere,
			String datatype, String strexpr,  int length,
			String pre,boolean flag) throws SQLException, GeneralException {
		ContentDAO dao=new ContentDAO(this.conn);
		boolean _flag=false; 
		RowSet rowSet;
		StringBuffer buf1=new StringBuffer(); 
		StringBuffer buf=new StringBuffer();
		buf.append("update ");
		buf.append(this.gz_tablename);
		buf.append(" set ");
		buf.append(itemid);
		buf.append("= "+strexpr.toString());
		
		buf1.append(strwhere.toString());
		if(whl!=null&&whl.trim().length()>0)
			buf1.append(" "+whl);
		
		if(flag){
			buf1.append(" and upper(nbase)='"+pre.toUpperCase()+"'");
			//权限过滤
			buf1.append(this.salaryTemplateBo.getFilterAndPrivSql_ff());
		}else{
			buf1.append(this.salaryTemplateBo.getfilter(this.getGz_tablename()));//获取表格控件前台过滤sql zhanghua 2018.5.25
		}
		
		
		if("A".equalsIgnoreCase(datatype))
		{  
			rowSet=dao.search("select "+strexpr+","+Sql_switcher.length(strexpr)+" a from "+this.gz_tablename+" "+buf1.toString()+" order by a desc");
			if(rowSet.next())
			{
				String _value=rowSet.getString(1)!=null?rowSet.getString(1):"";
				if(len(_value)>length)
				{
					 _flag=false;
					 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.inputlength")+length+ResourceFactory.getProperty("gz_new.gz_accounting.byte")));//"输入内容的长度不要长于" "个字符！"
				}
			} 
		}
//		ArrayList<String> dataList = new ArrayList<String>();
//		dataList.add(strexpr.toString());
		dao.update(buf.toString()+buf1.toString());
		
		return _flag;
	}
	
	/**
	 * 获取代码
	 * @param itemid
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<CommonData> getcodeItemList(String itemid) throws GeneralException
	{
		ArrayList<CommonData> list=new ArrayList<CommonData>();
		RowSet frowset = null;
		try
		{
//			list.add(new CommonData("", ""));
			ContentDAO dao=new ContentDAO(this.conn);
			FieldItem item=DataDictionary.getFieldItem(itemid.toLowerCase());
			if(item==null)
				return null;
			String codesetid=item.getCodesetid();
			if(!"0".equals(codesetid))
			{
				String sql="";
				if("UN".equals(codesetid)|| "UM".equals(codesetid)|| "@K".equals(codesetid))
				{
					sql="select codeitemid,codeitemdesc from organization where (codesetid='"+codesetid+"'";
				}
				else
				{
					sql="select codeitemid,codeitemdesc from codeitem where (codesetid='"+codesetid+"'";
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String backdate = sdf.format(new Date());
				if("UM".equals(codesetid)){//支持关联部门的指标也可以选择单位
					sql+= " or codesetid ='UN'";
				}
				sql+=") and " + Sql_switcher.dateValue(backdate)
     			+ " between start_date and end_date";
				if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid))
				{
					StringBuffer str = new StringBuffer();
					String b_units=this.userview.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
					String[] unitarr =b_units.split("`");
					for(int i=0;i<unitarr.length;i++)
					{
	    				String codeid=unitarr[i];
	    				if(codeid==null|| "".equals(codeid))
	    					continue;
		    			if(codeid!=null&&codeid.trim().length()>2)
	    				{
		    				String privCode = codeid.substring(0,2);
		    				String privCodeValue = codeid.substring(2);	
		    				str.append(" or  codeitemid like '"+privCodeValue+"%'");
	    				}
					}
					if(str.length()>0){//批量修改走优先级判断，zhaoxg add 2016-9-7
						sql = sql+" and ("+str.substring(3)+")";
					}
					sql=sql+(" ORDER BY a0000,codeitemid ");
				}else if(!"@@".equalsIgnoreCase(codesetid))
				{
					sql=sql+(" ORDER BY codeitemid ");
				}
				frowset=dao.search(sql);
				while(frowset.next())
				{
					list.add(new CommonData(frowset.getString(1),frowset.getString(1)+":"+frowset.getString(2)));
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(frowset);
		}
		return list;
	}

	/**
	 * 批量修改中批量更新方法
	 * @param itemid     目标指标
	 * @param whl        过滤条件
	 */
	private void getBatchUpdateSql(String itemid, String whl,
			 StringBuffer strwhere, Calendar d, String pre,boolean flag)
			throws SQLException {
		StringBuffer buf=new StringBuffer();
		buf.append("update ");
		buf.append(this.gz_tablename);
		buf.append(" set ");
		buf.append(itemid);
		buf.append("=?");
		buf.append(strwhere.toString());
		if(whl!=null&&whl.trim().length()>0)
			buf.append(" "+whl);
		if(flag){
			if(pre!=null&&pre.length()>0){
				buf.append(" and upper(nbase)='"+pre.toUpperCase()+"'");
			}
			
			//权限过滤 如果薪资类别是共享类别，操作用户是非管理员，并且属性设置了归属单位或部门，则为：1 否则为：0
			buf.append(this.salaryTemplateBo.getFilterAndPrivSql_ff());
			
		}else{
			buf.append(this.salaryTemplateBo.getfilter(this.gz_tablename));//获取表格控件前台过滤sql zhanghua 2018.5.25
		}
		java.sql.Date date=new java.sql.Date(d.getTimeInMillis());
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList paramList = new ArrayList();
		paramList.add(date);
		dao.update(buf.toString(),paramList);
	}


	/**
	 * 数值类型进行转换
	 * @param type
	 * @return
	 */
	private int getDataType(String type)
	{
		int datatype=0;
		switch(type.charAt(0))
		{
		case 'A':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'M':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'D':
			datatype=YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype=YksjParser.FLOAT;
			break;
		}
		return datatype;
	}
	/**
	 * 汉字占2个字符，其他的占一个字符
	 * @param s
	 * @return
	 */
	private int len(String s) { 
		int l = 0; 
		String[] a = s.split(""); 
		for (int i=2;i<a.length-1;i++) {  //去掉前后的双引号，字符型
			if (a[i].charAt(0)<299) {   
				l++;  
			} else {   
				l+=2;  
			} 
		} 
		return l;
	}
	/**
	 * 判断输入的是不是数字
	 * @param str
	 * @return
	 */
	private static boolean isNum(String str){
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");	
	}
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public int getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(int salaryid) {
		this.salaryid = salaryid;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public RecordVo getTemplatevo() {
		return templatevo;
	}

	public void setTemplatevo(RecordVo templatevo) {
		this.templatevo = templatevo;
	}

	public String getGz_tablename() {
		return gz_tablename;
	}

	public void setGz_tablename(String gz_tablename) {
		this.gz_tablename = gz_tablename;
	}

}
