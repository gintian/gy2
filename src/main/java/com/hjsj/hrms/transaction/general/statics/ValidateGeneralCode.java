package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ValidateGeneralCode extends IBusiness {
	
	private boolean isChecked(String expre,int nmax)
    {
        boolean is=true;
        String str="";
        int num=0;
        for(int i=0;i<expre.length();i++)
        {
          char v =expre.charAt(i);
          if(((i+1)!=expre.length())&&(v>='0'&&v<='9'))
          {
        	  str=str+v;
          }
          else
          {
            if(v>='0'&&v<='9')
            {
            	str=str+v;
            }
            if(!"".equals(str))
            {
            	num=Integer.parseInt(str);
              if(num>nmax)
              {
                  is=false;
                   break;
              }
            }
            str="";
          }
        }        
        return is;
    }
	/**
	 * chenmengqing 测试协议
	 * @param hm
	 */
	private void test(HashMap hm)
	{
	    Iterator iterator = hm.entrySet().iterator();
		while(iterator.hasNext())
		{
	        java.util.Map.Entry entry = (java.util.Map.Entry) iterator.next();
	        String name = entry.getKey().toString();
	        Object value = entry.getValue();
		}
	}
	
	public void execute() throws GeneralException {
		try
		{
		  ArrayList arr=(ArrayList)this.getFormHM().get("arr");
		  if(arr==null)
				throw GeneralExceptionHandler.Handle(new GeneralException("统计条件不能为空！"));
		  HashMap hm=(HashMap)this.getFormHM();
		  //test(hm);
		  int sno=Integer.parseInt((String)hm.get("sno"));
		  String type=(String)hm.get("type");
	      String text=(String)hm.get("texts");
	      text=PubFunc.keyWord_reback(text);//【7885】统计分析增加统计条件，表达式中输入 -1 ，点击“检查”，系统提示校验通过。 jingq add 2015.03.09
	      if(text==null||text.length()<=0||text.indexOf("-")!=-1)
	    	  throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
	      TSyntax syntax=new TSyntax();
	      String factor=getFactor(arr);
	      factor=PubFunc.keyWord_reback(factor);
		  if(!"0".equals(text)&&this.isChecked(text,sno)&&syntax.Lexical(text)&&syntax.DoWithProgram())
		  {
			    this.getFormHM().put("texts",text);
			    this.getFormHM().put("flag", "true");
			    InfoUtils infoUtils=new InfoUtils();
		        if(!infoUtils.sqlCheckFactor(type, "Usr", text, factor+"`", userView, this.getFrameconn()))
		        {
		        	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
		        }
		  }
		  else 
		  {
			  this.getFormHM().put("flag", "false");
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfactor"),"",""));
		  }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		this.getFormHM().put("arr", new ArrayList());
	}
	/**
	 * 组合条件
	 * @param arrList
	 * @return
	 */
    private String getFactor(ArrayList arrList)
    {
    	StringBuffer factor=new StringBuffer();
    	if(arrList==null)
    		return "";
    	for(int i=0;i<arrList.size();i++)
    	{
    		LazyDynaBean bean=(LazyDynaBean)arrList.get(i);
    		if(bean==null)
    			continue;
    		String fieldname=(String)bean.get("fieldname");
    		String oper=(String)bean.get("oper");
    		oper=PubFunc.keyWord_reback(oper);
    		String value=(String)bean.get("value");
    		if(value==null||value.length()<=0)
    			value="Null";
    		factor.append(fieldname);
    		factor.append(oper);
    		factor.append(value);
    		factor.append("`");
    	}
    	if(factor.length()>0)
    		factor.setLength(factor.length()-1);
    	return factor.toString();
    }
}
