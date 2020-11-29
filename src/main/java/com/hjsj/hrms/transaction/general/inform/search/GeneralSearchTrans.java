package com.hjsj.hrms.transaction.general.inform.search;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class GeneralSearchTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String type = (String)reqhm.get("type");
		type=type!=null&&type.trim().length()>0?type:"1";
		reqhm.remove("type");
		
		String a_code = (String)reqhm.get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		reqhm.remove("a_code");
		
		String tablename = (String)reqhm.get("tablename");
		tablename=tablename!=null&&tablename.trim().length()>0?tablename:"";
		reqhm.remove("tablename");
		
		String secondflag = (String)reqhm.get("second");
		secondflag = StringUtils.isEmpty(secondflag) ? "0" : secondflag;
		reqhm.remove("second");

		ArrayList setlist = new ArrayList();
		ArrayList fieldsetlist = new ArrayList();
		String fieldsetid="0";
		String fieldsetdesc="";
		String priv="";
    	if("1".equals(type)){
	    	//setlist=this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
    		setlist=this.userView.getPrivFieldSetList(Constant.ALL_FIELD_SET);
	    }else if("2".equals(type)){
	    	setlist=this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
	    }else if("3".equals(type)){
	    	setlist=this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
	    }else if("5".equals(type)){// 绩效管理中的考核实施/自动分配考核主体简单条件
	    	ArrayList dblist=this.userView.getPrivDbList();
			DbNameBo dbvo=new DbNameBo(this.getFrameconn());
			dblist=dbvo.getDbNameVoList(dblist);
			ArrayList list=new ArrayList();
			priv=(String)reqhm.get("priv");
			priv=priv!=null?priv:"1";
			reqhm.remove("priv");
			setlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.USED_FIELD_SET);
			for(int i=0;i<dblist.size();i++)
			{
				CommonData vo=new CommonData();
				RecordVo dbname=(RecordVo)dblist.get(i);
				if(tablename.indexOf(dbname.getString("pre"))==-1)
					continue;
				vo.setDataName(dbname.getString("dbname"));
				vo.setDataValue(dbname.getString("pre"));
				list.add(vo);
			}
	    	
	    }else if("6".equals(type)){// 计件薪资 先按业务单位查询，2013-01-10 wangrd add
	    	setlist=this.userView.getPrivFieldSetList(Constant.ALL_FIELD_SET);
	    	
	    }else if("9".equals(type)){// 基准岗位
	    	setlist=this.userView.getPrivFieldSetList(Constant.JOB_FIELD_SET);
	    }
	    for(int i=0;i<setlist.size();i++){
		    FieldSet fieldset = (FieldSet)setlist.get(i);
		    if("5".equals(type)){
		    	 if("1".equals(priv)&& "0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
			    	  continue;
		    }
		    if("0".equalsIgnoreCase(fieldset.getUseflag()))
		     	continue;
		    if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
		    	continue;	
		    if("B00".equalsIgnoreCase(fieldset.getFieldsetid()))
		    	continue;
			if("K00".equalsIgnoreCase(fieldset.getFieldsetid()))
			    continue;
		    CommonData obj=new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
		    fieldsetlist.add(obj);
	    }
	    if(reqhm.get("fieldsetid")!=null&&!"".equals((String)reqhm.get("fieldsetid")))
	    {
			fieldsetid=(String)reqhm.get("fieldsetid");
			FieldSet ff=DataDictionary.getFieldSetVo(fieldsetid);
			fieldsetdesc=ff.getCustomdesc();
			reqhm.remove("fieldsetid");
		}
	    if(type!=null&& "5".equals(type)){
	    	ArrayList list=new ArrayList();
	    	ArrayList selectedlist=new ArrayList();
	    	String expr=(String)reqhm.get("expr");
	    	expr=SafeCode.decode(expr);
	    	expr=PubFunc.keyWord_reback(expr);
		    if(expr.trim().length()>0){
		    	int idx=expr.indexOf('|');
				String expression=expr.substring(0,idx);
				String strfactor=expr.substring(idx+1);			
				FactorList factorlist=new FactorList(expression,strfactor,"");			
				list=factorlist.getAllFieldList();			
				String strlog=getDelimiters(expression);	
				if(!(list==null||list.size()==0))
				{	
					FieldItem fielditem = null;
					for(int i=0;i<list.size();i++)
					{
						 Factor oldfactor=(Factor)factorlist.get(i);
						 fielditem = DataDictionary.getFieldItem(oldfactor.getFieldname());
						 LazyDynaBean bean =new LazyDynaBean();
					     oldfactor.setFieldtype(fielditem.getItemtype());
					     oldfactor.setItemlen(fielditem.getItemlength());
					     oldfactor.setItemdecimal(fielditem.getDecimalwidth());
					     oldfactor.setCodeid(fielditem.getCodesetid());
					     setFactorLog(oldfactor,i,strlog);
					     selectedlist.add(oldfactor);
					    
					}					
				}
	    	}
			this.getFormHM().put("factorlist",selectedlist);
	    }
		this.getFormHM().put("setlist",fieldsetlist);
		this.getFormHM().put("type",type);
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("tablename",tablename);
		this.getFormHM().put("fieldSetId", fieldsetid);
		this.getFormHM().put("fieldSetDesc", fieldsetdesc);
		this.getFormHM().put("secondflag", secondflag);
	}
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
}
