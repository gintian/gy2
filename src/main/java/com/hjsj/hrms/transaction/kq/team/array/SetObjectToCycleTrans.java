package com.hjsj.hrms.transaction.kq.team.array;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 周期排班中选择对象的处理
 * <p>Title:SetObjectToCycleTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 27, 2006 9:34:31 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SetObjectToCycleTrans extends IBusiness{

	public void execute() throws GeneralException
	{
		String object_flag=(String)this.getFormHM().get("object_flag");
		String object_str=(String)this.getFormHM().get("object_str");
		if(object_flag==null||object_flag.length()<=0)
			return;
		if(object_str==null||object_str.length()<=0)
			return;
		ArrayList object_list=new ArrayList();
		if("0".equals(object_flag))
		{
			/*******个人*********/
			object_list=getSelectEmployeeList(object_str);
			
		}else if("1".equals(object_flag))
		{
			/*******组*********/
			object_list=getSelectGroupList(object_str);
		}
		this.getFormHM().put("object_list",object_list);
		this.getFormHM().put("selected_object",object_flag);
	}
	public ArrayList getSelectGroupList(String object_str)
	{
		 String object_Array[]=object_str.split("`");
		 ArrayList list=new ArrayList();
		 CommonData vo = null;
		  for(int i=0;i<object_Array.length;i++)
		  {
			  String array_str=object_Array[i].toString();
	    	  String one_array[]=array_str.split("\\^");
			  if(one_array.length>1)
			  {
				 vo = new CommonData();
	 			 vo.setDataName(one_array[1]);
	 			 vo.setDataValue(one_array[0]);
	 			list.add(vo);
			  }
		  }
		  return list;
	}
	
	/**
	 * 选择人员处理
	 * @param object_str
	 * @return
	 */
    public ArrayList getSelectEmployeeList(String object_str)
    {
    	ArrayList list=new ArrayList();
    	String object_Array[]=object_str.split("`");
    	
    	CommonData vo = null;
    	for(int i=0;i<object_Array.length;i++)
  	    {
    		vo = new CommonData();
    		String array_str=object_Array[i].toString();
    		String one_array[]=array_str.split("\\^");
    		String set_name="";
	    	String set_value="";
    		for(int j=0;j<one_array.length;j++)
    		{
    			
    			if(j<2)
    			{
    				set_value=set_value+one_array[j]+"`";
    			}else
    			{
    				set_name=one_array[j];
    			}
    		}
    		vo.setDataName(set_name);
    		vo.setDataValue(set_value);
    		list.add(vo);
  	    }
  	  
    	return list;
    }
}
