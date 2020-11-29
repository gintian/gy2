package com.hjsj.hrms.transaction.kq.options.struts;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class AddTimeTrans extends IBusiness {
	private String ret(ArrayList ins)
	{  
		StringBuffer sbf=new StringBuffer();
		
		if(ins.size()!=0)
		{ 
			for(int n=0;n<ins.size();n++)
			{
				if(n+1<ins.size())
				{
				   String te=(String) ins.get(n);
				   String tem=(String) ins.get(n+1);
				  if(te==null|| "".equals(te)||te.length()<=0||tem==null|| "".equals(tem)||tem.length()<=0)
				  { 
				  }else{
				     if(sbf.toString()==null|| "".equals(sbf.toString())||sbf.toString().length()==0)
				     {
				     }else{
					   sbf.append("|");
				     }
					   sbf.append(ins.get(n)+"~"+ins.get(n+1));
				       n++;
				  }
				}
				
			}
		}
		
		return sbf.toString();
	}
	

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String one=(String)this.getFormHM().get("one");
		String ones=(String)this.getFormHM().get("ones");
		String two=(String)this.getFormHM().get("two");
		String twos=(String)this.getFormHM().get("twos");
		String thre=(String)this.getFormHM().get("thre");
		String thres=(String)this.getFormHM().get("thres");
		String four=(String)this.getFormHM().get("four");
		String fours=(String)this.getFormHM().get("fours");
		
		
		if(ones.indexOf("00:00")!=-1)
			ones="23:59";
		if(twos.indexOf("00:00")!=-1)
			twos="23:59";
		if(thres.indexOf("00:00")!=-1)
			thres="23:59";
		if(fours.indexOf("00:00")!=-1)
			fours="23:59";

		
		ArrayList salst=new ArrayList();
		if(one!=null||!"".equals(one))
		  salst.add(one);
		if(ones!=null||!"".equals(ones))
		   salst.add(ones);
		if(two!=null||!"".equals(two))
			  salst.add(two);
		if(twos!=null||!"".equals(twos))
			salst.add(twos);
		if(thre!=null||!"".equals(thre))
			  salst.add(thre);
		if(thres!=null||!"".equals(thres))
			  salst.add(thres);
		if(four!=null||!"".equals(four))
			  salst.add(four);
		if(fours!=null||!"".equals(fours))
			  salst.add(fours);
		 String nam=this.ret(salst);
		 
		 
		 KqParameter para=new KqParameter(this.getFormHM(),this.userView,"UN"+userView.getUserOrgId(),this.getFrameconn());
		 
		 para.setWhours(nam);
		 
		 this.getFormHM().put("sige","1");

	}

}
