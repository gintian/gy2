/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SelectCodeTrans</p>
 * <p>Description:查询代码树交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-1-18:14:52:43</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SelectCodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		
		String codesetid=(String)hm.get("codesetid");
  	    this.getFormHM().put("codesetid",codesetid);		
//		String codeitemid=(String)hm.get("codeitemid");
//		String privflag=(String)hm.get("privflag");
//		/**取得查询字符串*/
//		String sql=getQueryString(codesetid, codeitemid, privflag); 
//		this.getFormHM().put("sql",sql);
//		
//		/**数据集字段列表*/
//		ArrayList list=new ArrayList();
//		Field field=new Field("codesetid","codesetid");
//		field.setDatatype(DataType.STRING);
//		field.setLength(2);
//		list.add(field);
//
//		 field=new Field("codeitemid","codeitemid");
//		 field.setDatatype(DataType.STRING);
//		 field.setLength(30);
//		 list.add(field);
//		 
//		 field=new Field("codeitemdesc","codeitemdesc");
//		 field.setDatatype(DataType.STRING);
//		 field.setLength(50);
//		 list.add(field);	
//		 field=new Field("childid","childid");
//		 field.setDatatype(DataType.STRING);
//		 field.setLength(30);
//		 list.add(field);	
//		 
//		 this.getFormHM().put("fieldlist",list);
		
	}

	/**
	 * @param codesetid
	 * @param codeitemid
	 * @param privflag
	 */
	private String getQueryString(String codesetid, String codeitemid, String privflag) {
		StringBuffer str=new StringBuffer();
        if("UN".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid))
        {
            if("UN".equalsIgnoreCase(codesetid))
            {
                str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where codesetid='");
                str.append(codesetid);
                str.append("'");
            }
            else if("UM".equalsIgnoreCase(codesetid))
            {
                str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
                str.append(codesetid);
                str.append("' or codesetid='UN') ");
            }  
            else if ("@K".equalsIgnoreCase(codesetid))
            {
                str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
                str.append(codesetid);
                str.append("' or codesetid='UN' or codesetid='UM') ");
            }               
        }
        else
        {
            str.append("select codesetid,codeitemid,codeitemdesc,childid from codeitem where codesetid='");
            str.append(codesetid);
            str.append("'");            
        }
		
        /**所有的第一层代码值列表*/
        if(privflag==null|| "".equals(privflag))
        {
        	if(codeitemid==null|| "".equals(codeitemid)|| "ALL".equals(codeitemid))
	        {
	              str.append(" and parentid=codeitemid");
	        }
	        else
	        {
	            str.append(" and parentid<>codeitemid and parentid='");
	            str.append(codeitemid);
	            str.append("'");
	         }    
        }
        else //根据管理范围过滤相应的节点内容
        {
            if("ALL".equals(codeitemid))
	        {
            	   str.append(" and parentid=codeitemid");
	        }
            else if(codeitemid==null|| "".equals(codeitemid))
            {
         	   str.append(" and 1=2");            	
            }
	        else
	        {
	        	str.append(" and codeitemid='");
	           	str.append(codeitemid);
	        	str.append("'");
	        }
        }
        return str.toString();
	}

}
