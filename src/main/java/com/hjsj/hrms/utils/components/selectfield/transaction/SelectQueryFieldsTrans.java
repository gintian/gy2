package com.hjsj.hrms.utils.components.selectfield.transaction;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称 ：ehr
 * 类名称：SelectQueryFieldsTrans
 * 类描述：解析条件表达式，生成数据
 * 创建人： lis
 * 创建时间：2016-5-4
 */
public class SelectQueryFieldsTrans extends IBusiness {

	public void execute() throws GeneralException {
		String imodule = (String)this.getFormHM().get("imodule");
		ArrayList fields = (ArrayList)this.getFormHM().get("right_fields");
		String expression=(String)this.getFormHM().get("expression");//编辑因式表达式
		String queryType=(String)this.getFormHM().get("queryType");//0：简单查询，1：通用查询
		String type=(String)this.getFormHM().get("type");
		String nbases=(String)this.getFormHM().get("nbases");
		
		if(type==null|| "".equals(type))
			type="1";
		
		if (fields == null || fields.size() == 0) {
			throw new GeneralException(ResourceFactory.getProperty("errors.query.notexistfield"));
		}
		
		if("1".equals(queryType)||"2".equals(queryType)){//1通用查询,2简单查询
			ArrayList<CommonData> dbList = null;
			if("9".equals(imodule)){//haosl add 如果是职称模块调用，则只查询认证人员库
				dbList = getLoginDbList();
				if(dbList.size()==0)
					throw GeneralExceptionHandler.Handle(new GeneralException("您没有认证人员库权限！"));
			}else
			{
				if(StringUtils.isEmpty(nbases))
					dbList = getDbList(null);
				else
				{
					String[] nbase = nbases.split(",");
					dbList = getDbList(nbase);
				}
			}
				
			this.getFormHM().put("dbList", dbList);
		}
         
		/** 保存当前因子对象列表 */
		ArrayList factorlist = (ArrayList) this.getFormHM().get("factorlist");
		
		int j = 0;
		StringBuffer strexpr = new StringBuffer();
		ArrayList list = new ArrayList();
		ArrayList fieldlist=new ArrayList();//记录上一步已选指标数据
		/** 信息类型定义default=1（人员类型） */
		int nInform = Integer.parseInt(type);
		try {
			
			/** 定义条件项 */
			FieldItem item = null;
			for (int i = 0; i < fields.size(); i++) {
				String fieldname = (String)fields.get(i);
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
			
			//if("0".equals(imodule) && "1".equals(type) && "0".equals(queryType)){//薪资模块-薪资类别-简单条件 lis 20160504
				/**重新解释查询表达式*/
				String expr=(String)this.getFormHM().get("expr");
				expr = SafeCode.decode(expr);
				expr = PubFunc.keyWord_reback(expr);
				
				//add by wangchaoqun on 20140830  消除前台显示NULL
				if(expr!=null && !"".equals(expr)){
					expr = expr.replace("=NULL", "=");
					expr = expr.replace(">NULL", ">");
					expr = expr.replace("<NULL", "<");
				}
				reParser(expr,list);	
			//}
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		    /*String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
	    	if(uplevel==null||uplevel.length()==0)
	    		uplevel="0";
	    	this.getFormHM().put("uplevel", uplevel);*/
			this.getFormHM().put("status", this.userView.getStatus());//0业务用户，4自助用户
	    	this.getFormHM().put("factorlist", list);
	    	if(StringUtils.isBlank(expression))
	    		this.getFormHM().put("expression",SafeCode.encode(strexpr.toString()));
	    	else
	    		this.getFormHM().put("expression",SafeCode.encode(expression));
		} catch (Exception ee) {
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		} finally {
			
		}
	}

	/**
	 * @author lis
	 * @Description: 获得人员库
	 * @date 2016-1-28
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList<CommonData> getDbList(String[] _nbase) throws GeneralException{
		ArrayList<CommonData> dbDataList = new ArrayList<CommonData>();
		try {
			//人员库
			ArrayList dbList=userView.getPrivDbList();
			StringBuffer dbSql=new StringBuffer();
            dbSql.append("select pre,dbname from dbname where pre in (");
            for(int i=0;i<dbList.size();i++)
            {
                if(i!=0)
                	dbSql.append(",");
                dbSql.append("'");
                dbSql.append((String)dbList.get(i));
                dbSql.append("'");
            }
            if(dbList.size()==0)
            	dbSql.append("''");
            dbSql.append(")");
            
            if(_nbase!=null)
            {
            	String _str="";
            	for(int i=0;i<_nbase.length;i++)
            	{
            		if(!StringUtils.isEmpty(_nbase[i]))
            			_str+=",'"+_nbase[i]+"'";
            	}
            	if(_str.length()>0)
            	{
            		dbSql.append(" and lower(pre) in ( "+_str.toLowerCase().substring(1)+" )");
            	}
            }
            
            
            dbSql.append(" order by dbid");
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            this.frowset = dao.search(dbSql.toString());
            CommonData data = null;
            while(this.frowset.next()){
            	data = new CommonData(this.frowset.getString("pre"), this.frowset.getString("dbname"));
            	dbDataList.add(data);
            }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(this.frowset);
		}
		return dbDataList;
	}

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
			int idx=expr.indexOf('|');
			String expression=expr.substring(0,idx);
			String strfactor=expr.substring(idx+1);
			FactorList factorlist=new FactorList(expression,strfactor,"");
			/**简单查询*/
			String strlog=getDelimiters(expression);
			for (int i = 0; i < newfactorlist.size(); i++) {
				Factor factor = (Factor) newfactorlist.get(i);
				Factor oldfactor = null;
				for (Object o : factorlist) {
					if (factor.getFieldname().equalsIgnoreCase(((Factor) o).getFieldname())) {
						oldfactor = (Factor) o;
						factorlist.remove(o);
						break;
					}
				}
				if (oldfactor != null) {
					factor.setLog(PubFunc.keyWord_reback(oldfactor.getLog()));
					//factor.setLog(oldfactor.getLog());
					factor.setOper(oldfactor.getOper());
					String hzvalue=oldfactor.getHzvalue();
					if(DataDictionary.getFieldItem(factor.getFieldname())!=null&&
							"um".equalsIgnoreCase(DataDictionary.getFieldItem(factor.getFieldname()).getCodesetid())){
						String value=oldfactor.getValue();
						if(StringUtils.isNotBlank(AdminCode.getCodeName("UM",value))){
							hzvalue=AdminCode.getCodeName("UM",value);
						}else{
							hzvalue=AdminCode.getCodeName("UN",value);
						}
					}
					if ("0".equals(oldfactor.getCodeid())) {
						factor.setValue(oldfactor.getValue());
					} else {
						factor.setValue(oldfactor.getValue() + "`" +hzvalue );
					}
					factor.setHzvalue(hzvalue);
					factor.setHz(oldfactor.getHz());
				}
				setFactorLog(factor, i, strlog);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	/**
	 * 获得认证人员库列表
	 * @return
	 * @throws GeneralException
	 */
	 private ArrayList getLoginDbList() throws GeneralException {
	        ArrayList nbaseList = new ArrayList();
	        //获得人员库 haosl 2017-07-07
	       List privDb = this.userView.getPrivDbList();
	        DbNameBo dbNameBo = new DbNameBo(this.getFrameconn());
	        ArrayList dblist = dbNameBo.getAllLoginDbNameList();//所有认证人员库
	        if (dblist == null || dblist.size() == 0 
	        		|| privDb == null || privDb.size() == 0)
	        	return nbaseList;
	        CommonData da = null;
	        for (int i = 0; i < dblist.size(); i++) {
	            RecordVo vo = (RecordVo) dblist.get(i);
	            da = new CommonData();
	            String per = vo.getString("pre");

	            if(!privDb.contains(per))
	            	continue;
	            
	            String dbname = vo.getString("dbname");
	            da.setDataName(dbname);
	            da.setDataValue(per);
	            nbaseList.add(da);
	        }
	        return nbaseList;
	    }
}
