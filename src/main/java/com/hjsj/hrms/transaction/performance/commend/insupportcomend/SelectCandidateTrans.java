package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SelectCandidateTrans extends IBusiness{
	public void execute() throws GeneralException{
		try{
			String content = "";
			String title = "";
			String p0201="";
			String nbase = "";
			String A0100="";
			String e0122="";
			String b0110="";
			String a0101="";
			String p0305="";
			String p0307="";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer sql = new StringBuffer();
			StringBuffer insertSql=new StringBuffer();
			if( this.getFormHM().get("content")!= null&&((String)this.getFormHM().get("content")).trim().length()>0 )
				content=(String)this.getFormHM().get("content");
			if( this.getFormHM().get("title") != null&&((String)this.getFormHM().get("title")).trim().length()>0 )
				title=(String)this.getFormHM().get("title");
			if( this.getFormHM().get("selectId")!=null&&((String)this.getFormHM().get("selectId")).trim().length()>0 )
				p0201=(String)this.getFormHM().get("selectId");
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			String p0300 = "";
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
  		    String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
  		    if(display_e0122==null|| "00".equals(display_e0122))
  			   display_e0122="0";
			if(content.indexOf(",")!= -1){
				String[] content_Arr = content.split(",");
				for(int i=0;i<content_Arr.length;i++){
					if(content_Arr[i].indexOf("UN") !=-1 || content_Arr[i].indexOf("UM")!=-1 || content_Arr[i].indexOf("@K")!=-1)
						continue;
					nbase=content_Arr[i].substring(0,3);
					A0100 = content_Arr[i].substring(3);
					sql.append("select b0110,a0101,e0122 from ");
					sql.append(nbase+"a01");
					sql.append(" where a0100 ='");
					sql.append(A0100+"'");
					this.frowset=dao.search(sql.toString());
					while(this.frowset.next()){
						b0110=this.frowset.getString("b0110");
						a0101=this.frowset.getString("a0101");
						e0122=this.frowset.getString("e0122");
					}
					if(e0122==null||e0122.trim().length()<=0){
						p0307=this.getParentItem("UN",b0110, display_e0122);
					}else{
						p0307=this.getParentItem("UM", e0122, display_e0122);
					}
					p0300=idg.getId("p03.p0300");
					insertSql.append(" insert into p03 (p0300,a0100,nbase,p0201,b0110,e0122,a0101,p0304,p0307) ");
					insertSql.append("values ");
					insertSql.append("('");
					insertSql.append(p0300+"','");
					insertSql.append(A0100+"','");
					insertSql.append(nbase+"',");
					insertSql.append(p0201+",'");
					insertSql.append(b0110+"','");
					insertSql.append(e0122+"','");
					insertSql.append(a0101+"',");
					insertSql.append("0,'");
					insertSql.append(p0307+"')");
					dao.insert(insertSql.toString(),new ArrayList());
					sql.setLength(0);
					insertSql.setLength(0);
				}
			}
		}catch(Exception e){
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
