package com.hjsj.hrms.transaction.hire.jp_contest.param;

import com.hjsj.hrms.businessobject.general.deci.leader.LeaderParam;
import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParamXML;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:SelectMusterSetTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 3, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class SelectFileSetTrans extends IBusiness {

	private void reParseExpression(String expr)throws GeneralException
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
			list=factorlist.getAllFieldList();
			ArrayList selectedlist=new ArrayList();
			if(!(list==null||list.size()==0))
			{
				for(int i=0;i<list.size();i++)
				{
					 FieldItem fielditem=(FieldItem)list.get(i);
				     CommonData dataobj = new CommonData(fielditem.getItemid(), fielditem.getItemdesc());;
				     selectedlist.add(dataobj);
				}
				this.getFormHM().put("selectedlist",selectedlist);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String infor=(String)hm.get("base");
		String field_falg=(String)this.getFormHM().get("field_falg");

		
		if(infor==null|| "".equals(infor))
			infor="1";
	    ArrayList list=new ArrayList();
	    ArrayList fieldsetlist=null;
	    if("1".equals(infor))
	    {
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.USED_FIELD_SET);
	      /*ArrayList  fieldunitlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
	      ArrayList  fieldposlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);	
	      for(int i=0;i<fieldunitlist.size();i++)
	      {
	    	  fieldsetlist.add(fieldunitlist.get(i));
	      }
	      for(int i=0;i<fieldposlist.size();i++)
	      {
	    	  fieldsetlist.add(fieldposlist.get(i));
	      }*/
	    }
	    else if(("2".equals(infor)))
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
	    else
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
	    for(int i=0;i<fieldsetlist.size();i++)
	    {
	      FieldSet fieldset=(FieldSet)fieldsetlist.get(i);
	      if("0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
	        continue;
	      if("A00".equals(fieldset.getFieldsetid())|| "B01".equals(fieldset.getFieldsetid())|| "B00".equals(fieldset.getFieldsetid()))
	    	  continue;
	     /* if(infor.equals("1"))
	      {
	    	  if(fieldset.getFieldsetid().equals("B01")||fieldset.getFieldsetid().equals("K01"))
	    		  continue;
	      }*/
	      
	      CommonData dataobj = new CommonData(fieldset.getFieldsetid(), /*"(" + fieldset.getFieldsetid() + ")"+*/ fieldset.getCustomdesc()/*getFieldsetdesc()*/);
          list.add(dataobj);
	    }
		/**1*2*3|A0101=`A0405<>45`A0107=1`*/
		String expr=(String)hm.get("expr");	    
	    this.getFormHM().clear();
		/**重新解释查询表达式*/
		reParseExpression(expr);	    
	    /**为了不让前台弹出提示框*/
	    //System.out.println("---infor=="+infor);
	   // System.out.println("---eeeeeee=="+list.toString());
		this.getFormHM().put("base",infor);
	    //this.getFormHM().put("message","");
	    this.getFormHM().put("setlist",list);
	    this.getFormHM().put("field_falg",field_falg);
	    
	    
		if(field_falg==null||field_falg.length()<=0)
			field_falg="attend";
		EngageParamXML engageParamXML=new EngageParamXML(this.getFrameconn());	
		if("attend".equals(field_falg))
		{
			getOutput(engageParamXML);
		}
	}
	
	public void getOutput(EngageParamXML engageParamXML)
	{
			
		String output_field=engageParamXML.getTextValue(EngageParamXML.ATTENT_VIEW);		
		LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);	
		ArrayList list=leaderParam.getFields(output_field);
		this.getFormHM().put("itemlist",list);
	}
}
