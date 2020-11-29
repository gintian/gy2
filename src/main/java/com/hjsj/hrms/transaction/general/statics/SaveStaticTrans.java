package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveStaticTrans extends IBusiness {
	
	/**
	 * 求常用统计条件序号
	 * @return
	 */
	private String getMaxId()throws GeneralException
	{
		int nid=-1;
		StringBuffer sql=new StringBuffer("select max(id)+1 as nmax from sname");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				nid=this.frowset.getInt("nmax");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	       throw GeneralExceptionHandler.Handle(ex);			
		}
		return String.valueOf(nid);
	}
	/**
	 * 删除常用统计条件
	 * @param id
	 * @throws GeneralException
	 */
	private void deleteSName(String id)throws GeneralException
	{
		StringBuffer sql=new StringBuffer();

		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			sql.append("delete from sname where id=");
			sql.append(id);			
			dao.update(sql.toString());
			sql.setLength(0);
			sql.append("delete from slegend where id=");
			sql.append(id);	
			dao.update(sql.toString());			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
	   	    throw GeneralExceptionHandler.Handle(ex);					
		}
	}
	private String changefunction(String type,String value)
	{
		//当月：$THISMONTH[]
		//当年：$THISYR[]
		//年限：$YRS[cValue]
		if("当月".equals(value))
			value="$THISMONTH[]";
		else if("当年".equals(value))
			value="$THISYR[]";
		return value;
	}
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList flist=(ArrayList)this.getFormHM().get("factorlist");
        String find=(String)this.getFormHM().get("find");
        String result=(String)this.getFormHM().get("result");
        /**=0全体,=1查询结果*/
        if(result==null|| "".equals(result))
        	result="0";
        if(find==null|| "".equals(find))
            find="0"; //如果没有选择，默认值为0;
        String history=(String)this.getFormHM().get("history");
        if(history==null|| "".equals(history))
        	history="0";
        String title = (String)this.getFormHM().get("title");
        String infor_Flag = (String)this.getFormHM().get("infor_Flag");
        String id = (String)this.getFormHM().get("hvalue");
        
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        if(id==null|| "".equals(id))
        	id=getMaxId();
        else
        {
        	deleteSName(id);        	
        }
		StringBuffer sql=new StringBuffer();
		ArrayList paralist=new ArrayList();		
		sql.append("insert into sname(Id,Name,Flag,Type,InfoKind)values(?,?,?,'1',?)");
		cat.debug("id="+id);
		paralist.add(new Integer(id));
		paralist.add(title);
		paralist.add(result);
		paralist.add(new Integer(infor_Flag));
		try
		{
			dao.update(sql.toString(),paralist);
			/**保存统计图例*/
			sql.setLength(0);
			sql.append("insert into slegend(Id,nOrder,Legend,LExpr,Factor,Direction,flag)values(?,?,?,?,?,?,?)");
			String value=null;
			rebackKeyword(flist);
		    for(int i=0; i<flist.size();i++)
		    {
				paralist.clear();		    	
				Factor fc=(Factor)flist.get(i);
		    	paralist.add(new Integer(id));
		    	paralist.add(new Integer(i+1));
		    	value=fc.getValue();
		    	fc.setOper(PubFunc.keyWord_reback(fc.getOper()));
		    	if("0".equals(fc.getCodeid()))
		    	{
		    		if(fc.getValue()==null || fc.getValue().length()==0)
		    			if("<>".equals(fc.getOper()))
		    			  paralist.add("非空值");
		    			else
		    			  paralist.add("空值");
		    		else 
		    			if("<>".equals(fc.getOper()))
	    			      paralist.add("非" + fc.getValue());
		    			else
		    			  paralist.add(fc.getValue());	
		    		if("1".equals(find)&& "A".equalsIgnoreCase(fc.getFieldtype()))
		    			value="%"+value+"%";
		    		if("D".equalsIgnoreCase(fc.getFieldtype()))
		    			value=changefunction(fc.getFieldtype(),value);
		    	}
		    	else
		    	{
		    		if(fc.getValue()==null || fc.getValue().length()==0)
		    			if("<>".equals(fc.getOper()))
			    		   paralist.add("非空值");
			    	    else
			    		   paralist.add("空值");
		    		else 
			    		if("<>".equals(fc.getOper()))
			    		    paralist.add("非" + fc.getHzvalue());
			    		else
			    			paralist.add(fc.getHzvalue());
		    		if("1".equals(find)&& "A".equalsIgnoreCase(fc.getFieldtype()))
		    			value=value+"%";
		    		if("D".equalsIgnoreCase(fc.getFieldtype()))
		    			value=changefunction(fc.getFieldtype(),value);
		    	}
		    	paralist.add("1");
		    	paralist.add(fc.getFieldname().toUpperCase()+fc.getOper()+value);
		    	paralist.add("0");
		    	paralist.add(history);
		    	dao.update(sql.toString(),paralist);
		    }
		    /**保存资源,chenmengqing added at */
			UserObjectBo user_bo=new UserObjectBo(this.getFrameconn());
			user_bo.saveResource(id,this.userView,IResourceConstant.STATICS);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
	   	    throw GeneralExceptionHandler.Handle(ex);				
		}
	}
	
	private void rebackKeyword(ArrayList list){
		for(int i=0;i<list.size();i++){
			Factor factor = (Factor)list.get(i);
			String hz = factor.getHz();
			String oper = factor.getOper();
			String log = factor.getLog();
			String value = factor.getValue();
			String hzvalue = factor.getHzvalue();
			hz = PubFunc.hireKeyWord_filter_reback(hz);
			oper = PubFunc.hireKeyWord_filter_reback(oper);
			log = PubFunc.hireKeyWord_filter_reback(log);
			value = PubFunc.hireKeyWord_filter_reback(value);
			hzvalue = PubFunc.hireKeyWord_filter_reback(hzvalue);
			factor.setHz(hz);
			factor.setOper(oper);
			factor.setLog(log);
			factor.setValue(value);
			factor.setHzvalue(hzvalue);
		}
	}
}
