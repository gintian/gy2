package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.AnychartBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowStaticChartTrans extends IBusiness {
	public ShowStaticChartTrans(){
		   super();
		}
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList flist=(ArrayList)this.getFormHM().get("factorlist");
	    String history=(String)this.getFormHM().get("history");
		String userbase=(String)this.getFormHM().get("userbase");
		String userbases=(String)this.getFormHM().get("userbases");
        String infor_Flag = (String)this.getFormHM().get("infor_Flag");
        String find=(String)this.getFormHM().get("find");
        String result=(String)this.getFormHM().get("result");
		boolean bhistory=false;
	    if(history==null|| "".equals(history)|| "0".equals(history))
	    	bhistory=false;
	    else
	    	bhistory=true;
		int[] statvalues;
       	String[] fieldDisplay;        
		String SNameDisplay;
		ArrayList rlist = new ArrayList();
		ArrayList list=new ArrayList();	   
		HashMap jfreemap=new HashMap();
		StatDataEncapsulation simplestat=new StatDataEncapsulation();
		if(userbase==null|| "".equals(userbase))
			 userbase="Usr";
	    boolean ret=true;
	    if(result==null|| "".equals(result)|| "0".equals(result))
	    	ret=true; 
	    else
	    	ret =false;
		/**对人员信息群须指定人员库*/
		if((userbase==null|| "".equals(userbase))&& "1".equals(infor_Flag))
	   	    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.static.notdbname"),"",""));	
		if("2".equals(infor_Flag))
			userbase="B";
		if("3".equals(infor_Flag))
			userbase="K";
		String tem=null;
    	String name="";		
	    for(int i=0; i<flist.size();i++)
		{
			rlist.clear();
		    Factor fc=(Factor)flist.get(i);
		    String opt=com.hjsj.hrms.utils.PubFunc.keyWord_reback(fc.getOper());
		    fc.setOper(opt);
		    //opt = getOperator(opt);
		    if("0".equalsIgnoreCase(fc.getCodeid())) //非代码型
		    {
		    	if(fc.getValue()==null|| "".equals(fc.getValue()))
		    	{
		    		tem="`";
		    		name=ResourceFactory.getProperty("label.null");
		    	}
		    	else
		    	{
		    		/**like仅对字符型指标有作用*/
		    		if("1".equals(find)&& "A".equalsIgnoreCase(fc.getFieldtype()))
		    		{
		    				tem="%"+fc.getValue()+"%`";
		    		}
		    		else
		    		{
		    			tem=fc.getValue()+"`";
		    		}
		    		name=fc.getValue();
		    	}
		    	rlist.add(fc.getHz()+opt+name+"|1|"+fc.getFieldname()+fc.getOper()+tem);
		    }
		    else
		    {
		    	name=fc.getHzvalue();
		    	tem=fc.getValue();
		    	if(fc.getValue()==null|| "".equals(fc.getValue()))
		    	{
		    		tem="`";
		    		name=ResourceFactory.getProperty("label.null");
		    	}
		    	else
		    	{
		    		if("1".equals(find))
		    			tem=fc.getValue()+"%`";
		    		else
		    			tem=tem+"`";
		    	}
		        rlist.add(fc.getHz()+opt+name+"|1|"+fc.getFieldname()+fc.getOper()+tem);
		    }
		    if(userbases==null||userbases.length()==0){
		    	statvalues=simplestat.getLexprData(rlist,bhistory,userView,userbase,infor_Flag,ret);
		    }else
		    	statvalues=simplestat.getLexprData(rlist,bhistory,userView,userbase.toUpperCase(),infor_Flag,ret,userbases);
		    if (statvalues != null && statvalues.length > 0) 
			{
		    	 fieldDisplay = simplestat.getDisplay();
				int statTotal = 0;
				for (int j = 0; j < statvalues.length; j++)
				{
					  
					 CommonData vo=new CommonData();
					 if("".equals(fc.getValue()) || fc.getValue() == null){ //如果查询值为空 则指标与空并显示 反之则只显示查询值
						 vo.setDataName(fieldDisplay[j]);
					 }else{
						 vo.setDataName(name);
					 }
					 vo.setDataValue(String.valueOf(statvalues[j]));
					 list.add(vo);
				     statTotal += statvalues[j];
				     
				}
				
	        }
        }//for loop end.
	    
 	    SNameDisplay = simplestat.getSNameDisplay();
  	    this.getFormHM().put("snamedisplay",SNameDisplay);
  	    jfreemap.put(SNameDisplay, list);
  	    this.getFormHM().put("xangle", AnychartBo.computeXangle(list));//add by xiegh on 20171107 解决简单查询中，横向条件项缺失问题 bug31960
		this.getFormHM().put("list",list);
		this.getFormHM().put("jfreemap" ,jfreemap);
	}
	
	/**
	 * 符号转换
	 * @param opt
	 * @return
	 */
	private String getOperator(String opt) {
		if("<>".equals(opt))
		{
			opt=ResourceFactory.getProperty("kq.formula.not");
		}
		if(">".equals(opt))
		{
			opt=ResourceFactory.getProperty("kq.formula.over");
		}
		if(">=".equals(opt))
		{
			opt=ResourceFactory.getProperty("kq.formula.overo");
		}
		if("<".equals(opt))
		{
			opt=ResourceFactory.getProperty("kq.formula.lower");
		}
		if("<=".equals(opt))
		{
			opt=ResourceFactory.getProperty("kq.formula.lowero");
		}
		if("=".equals(opt))
		{
			opt=ResourceFactory.getProperty("kq.formula.equal");
		}
		return opt;
	}
}
