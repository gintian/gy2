package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.utils.PubFunc;
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

public class SelectMusterSetTrans extends IBusiness {

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

		String priv=(String)hm.get("priv");
		priv=priv!=null&&priv.trim().length()>0?priv:"1";
		
		String path = (String)hm.get("path");//path=值为功能号（便于查哪个功能）
		hm.remove("path");
		
		if(infor==null|| "".equals(infor))
			infor="1";
	    ArrayList list=new ArrayList();
	    ArrayList fieldsetlist=null;
	    if("1".equals(infor))
	    {
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.USED_FIELD_SET);

	      if("2306514".equals(path)){
		      ArrayList  fieldunitlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
		      ArrayList  fieldposlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);	
		      for(int i=0;i<fieldunitlist.size();i++)
		      {
		    	  fieldsetlist.add(fieldunitlist.get(i));
		      }
		      for(int i=0;i<fieldposlist.size();i++)
		      {
		    	  fieldsetlist.add(fieldposlist.get(i));
		      }
	      }
	    }
	    else if(("2".equals(infor)))
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
	    else if(("3".equals(infor)))
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
	    else if(("4".equals(infor)|| "9".equals(infor)))  // 基准岗位
		      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.JOB_FIELD_SET);	    
	    else if(("5".equals(infor)))
		      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.PARTY_FIELD_SET);	    
	    else if(("6".equals(infor)))
		      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.MEMBER_FIELD_SET);
	    else if(("7".equals(infor)))
		      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.TRADEUNION_FIELD_SET);		    
	    else{
	    	fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.USED_FIELD_SET);
	    	ArrayList  fieldunitlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
	    	ArrayList  fieldposlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);	
	    	for(int i=0;i<fieldunitlist.size();i++)
	    	{
	    		fieldsetlist.add(fieldunitlist.get(i));
	    	}
	    	for(int i=0;i<fieldposlist.size();i++)
	    	{
	    		fieldsetlist.add(fieldposlist.get(i));
	    	}
	    }
	    for(int i=0;i<fieldsetlist.size();i++)
	    {
	      FieldSet fieldset=(FieldSet)fieldsetlist.get(i);
	      if("1".equals(priv)&& "0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
	    	  continue;
	      if("A00".equalsIgnoreCase(fieldset.getFieldsetid())|| "B00".equalsIgnoreCase(fieldset.getFieldsetid())
	    		  || "K00".equalsIgnoreCase(fieldset.getFieldsetid())|| "Y00".equalsIgnoreCase(fieldset.getFieldsetid())|| "W00".equalsIgnoreCase(fieldset.getFieldsetid())|| "V00".equalsIgnoreCase(fieldset.getFieldsetid())|| "H00".equalsIgnoreCase(fieldset.getFieldsetid()))
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
	    expr = PubFunc.keyWord_reback(expr);
		reParseExpression(expr);	    
	    /**为了不让前台弹出提示框*/
	    //System.out.println("---infor=="+infor);
	   // System.out.println("---eeeeeee=="+list.toString());
		this.getFormHM().put("base",infor);
	    this.getFormHM().put("message","");
	    this.getFormHM().put("setlist",list);
	}

}
