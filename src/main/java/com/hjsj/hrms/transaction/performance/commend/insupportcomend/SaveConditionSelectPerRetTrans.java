package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveConditionSelectPerRetTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			
		   String p0201=(String)this.getFormHM().get("p0201");
           ArrayList list = (ArrayList)this.getFormHM().get("perList");
           insertSelectPersons(list,p0201); 
        }
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	 /**
     * 保存选择的候选人
     * @param list
     */
    public void insertSelectPersons(ArrayList list,String p0201)
    {
    	try
    	{
    		String str="";
    		StringBuffer selectBuffer = new StringBuffer();
    		StringBuffer insertBuffer = new StringBuffer();
    		ContentDAO dao = new ContentDAO(this.getFrameconn());
    		IDGenerator idg = new IDGenerator(2, this.getFrameconn());
    		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
  		    String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
  		    if(display_e0122==null|| "00".equals(display_e0122))
  			   display_e0122="0";
    		for(int i=0;i<list.size();i++)
    		{
    		    str=list.get(i).toString();
    		    selectBuffer.append("select b0110,a0101,e0122 from ");
    		    selectBuffer.append(str.substring(0,3)+"a01");
    		    selectBuffer.append(" where a0100='");
    		    selectBuffer.append(str.substring(3)+"'");
    			this.frowset=dao.search(selectBuffer.toString());
    			while(this.frowset.next())
    			{
    				insertBuffer.append("insert into p03 (p0300,a0100,nbase,p0201,b0110,e0122,a0101,p0304,p0307) ");
    				insertBuffer.append("values ");
    				insertBuffer.append("('");
    				insertBuffer.append(idg.getId("p03.p0300")+"','");
    				insertBuffer.append(str.substring(3)+"','");
    				insertBuffer.append(str.substring(0,3)+"',");
    				insertBuffer.append(p0201+",'");
    				insertBuffer.append(this.frowset.getString("b0110")+"','");
    				insertBuffer.append(this.frowset.getString("e0122")+"','");
    				insertBuffer.append(this.frowset.getString("a0101")+"',");
    				insertBuffer.append("0,'");
    				insertBuffer.append(this.frowset.getString("e0122")==null?this.getParentItem("UN", this.frowset.getString("b0110"),display_e0122):this.getParentItem("UM", this.frowset.getString("e0122"), display_e0122));
    				insertBuffer.append("')");
    				dao.insert(insertBuffer.toString(),new ArrayList());
    				selectBuffer.setLength(0);
    				insertBuffer.setLength(0);
    			}
    		}
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    }
    /**
     * 根据候选人所在部门或单位，向后推
     * @param codesetid
     * @param itemid
     * @param display_e0122
     * @return
     */
    public String getParentItem(String codesetid,String itemid,String display_e0122)
    {
    	String parentid="";
    	try
    	{
    		if(itemid!=null)
    		{
        		CodeItem item=AdminCode.getCode(codesetid,itemid,Integer.parseInt(display_e0122));
    	    	parentid =item.getCodeitem();
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return parentid;
    }

}
