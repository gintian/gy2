package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class TablePersonTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String name = (String)hm.get("name");
		name = name!=null&&name.trim().length()>0?name:"";
		name=SafeCode.decode(name);
		hm.remove("name");
		
		String nbase = (String)hm.get("nbase");
		nbase = nbase!=null&&nbase.trim().length()>0?nbase:"";
		hm.remove("nbase");
		
  		TrainCourseBo bo = new TrainCourseBo(userView);
  		String a_code = bo.getUnitIdByBusi();
		
		String itemkey = (String)hm.get("itemkey");
		itemkey = itemkey!=null&&itemkey.trim().length()>0?itemkey:"";
		hm.remove("itemkey");
		
		String preflag = (String)hm.get("preflag");
		preflag = preflag!=null&&preflag.trim().length()>0?preflag:"1";
		hm.remove("preflag");
		
		StringBuffer tablestr = new StringBuffer();
		tablestr.append("<table id=\"namestr\" width=\"240\" border=\"0\" cellspacing=\"0\"");
		tablestr.append("  align=\"left\" cellpadding=\"0\"");
		tablestr.append("<tr style=\"position:relative;top:expression(document.getElementById(\"namePerson\").scrollTop);z-index: 10;\">");
		tablestr.append("<td align=\"center\" width=\"120\" class=\"TableRow\">"+ResourceFactory.getProperty("gz.columns.a0101")+"</td>");
		tablestr.append("<td align=\"center\" class=\"TableRow\"  style=\"border-left: none;\">"+ResourceFactory.getProperty("gz.columns.e0122")+"</td></tr>");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		 ArrayList dblist = new ArrayList();
         nbase=nbase!=null&&nbase.trim().length()>0?nbase:"";
         nbase= "all".equalsIgnoreCase(nbase)?"":nbase;
         String nbasearr[] = nbase.split(","); 
         if(nbase.trim().length()>0&&nbasearr.length>0){
       	  for(int i=0;i<nbasearr.length;i++){
       		  if(nbasearr[i]!=null&&nbasearr[i].length()>0)
       			  dblist.add(nbasearr[i]); 
       	  }
         }else
       	  dblist=userView.getPrivDbList();
       //培训考试计划手工选人特殊处理  取参培参数设置的交集
         ArrayList arrayList = new ArrayList();
         ArrayList sel_nbase = new ArrayList();
		 ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
		 String tmpnbase = constantbo.getTextValue("/param/post_traincourse/nbase");
		 if(tmpnbase!=null&&tmpnbase.length()>0){
			 String nbs[]=tmpnbase.split(",");
			 for(int i=0;i<nbs.length;i++){
				 if(nbs[i]!=null&&nbs[i].length()>0){
					 sel_nbase.add(nbs[i]);
				 }
			 }
		 }
		 for (int i = 0; i < dblist.size(); i++) {
				if(sel_nbase.contains(dblist.get(i)))
					arrayList.add(dblist.get(i));
		  }
		 
         
         //ConstantXml cx=new ConstantXml(this.getFrameconn());
         //String ZP_DBNAME =  cx.getConstantValue("ZP_DBNAME");
        for(int i=0;i<arrayList.size();i++){
        	String pre=(String)arrayList.get(i);
      	  	if(pre!=null&&pre.trim().length()>0){
      	  		//if(pre.equalsIgnoreCase(ZP_DBNAME))// LiWeichao 陈总提到人员库过滤去掉招聘人员库 2011年5月4日 10:20:57
      	  		//	continue;
      	  		
      	  		StringBuffer buf = new StringBuffer();
      	  		buf.append("select A0100,A0101,B0110,E0122,");
      	  		buf.append("(select codeitemdesc from organization where codeitemid=");
      	  		buf.append(pre+"A01.B0110) as B0110_desc,");
      	  		buf.append("(select codeitemdesc from organization where codeitemid=");
      	  		buf.append(pre+"A01.E0122) as E0122_desc");
      	  		buf.append(" from "+pre+"A01 where A0101 like '%");
      	  		buf.append(name+"%'");

      			if(a_code!=null&&(a_code.length()==3||a_code.toUpperCase().indexOf("UN`")!=-1)){
      				//buf.append(" AND 1=1 ");
      			}else if(a_code !=null && a_code.trim().length()>2){
      				String tmp[] = a_code.split("`");
      				buf.append(" AND (");
      				for (int j = 0; j < tmp.length; j++) {
						if(j>0)
							buf.append(" or ");
						if(a_code.startsWith("UN"))
							buf.append(pre+"A01.B0110 like '"+tmp[j].substring(2)+"%'");
	      	  			else if(a_code.startsWith("UM"))
	      	  			buf.append(pre+"A01.E0122 like '"+tmp[j].substring(2)+"%'");
					}
      				buf.append(")");
      			}else{
      				buf.append(" AND 1=2");
      			}
      	  		
      	  		if("1".equals(preflag)){
      	  			buf.append(" and A0100 not in(select R4001 from R40 where R4005='");
      	  			buf.append(itemkey+"' and nbase='"+pre+"')");  
      	  		}else if("2".equals(preflag)){
      	  		  if(itemkey!=null&&itemkey.length()>0){
      	  			  buf.append(" and A0100 not in(select A0100 from tr_selected_lesson where nbase='"+pre+"' and r5000 in (");
	  	  			  String[] t = itemkey.split(",");
	  	  			  int j = 0;
	  	  			  for (; j < t.length; j++) {
	  	  				  if(j>0)
	  	  					buf.append(",");
							 buf.append(t[j]);
						  } 
	  	  			  buf.append(") GROUP BY A0100 HAVING count(A0100)>"+(j-1));
	  	  			  buf.append(")");
	   	  		  }
      	  		}
      	  		try {
					this.frowset = dao.search(buf.toString());
					 while (this.frowset.next()){
	    		          String codeitemid=this.frowset.getString("A0100");
	    		          String codeitemdesc = this.frowset.getString("A0101");
	    		          String B0110 = this.frowset.getString("B0110");
	    		          String E0122 = this.frowset.getString("E0122");
	    		          String B0110_desc = this.frowset.getString("B0110_desc");
	    		          B0110_desc=B0110_desc!=null?B0110_desc:"";
	    		          String E0122_desc = this.frowset.getString("E0122_desc");
	    		          E0122_desc=E0122_desc!=null?E0122_desc:"";
	    		          String id=SafeCode.encode(PubFunc.encrypt(codeitemid))+"::"+codeitemdesc;
	    		          if(B0110!=null&&B0110.trim().length()>0){
	    		        	  	if(E0122!=null&&E0122.trim().length()>0){
	    		        	  		id+="::"+SafeCode.encode(PubFunc.encrypt("UM"+ E0122));
	    		        	  	}else{
	    		        	  		id+="::"+SafeCode.encode(PubFunc.encrypt("UN"+ B0110)); 
	    		        	  		E0122_desc=B0110_desc;
	    		        	  	}
	    		          }else{
	    		        	  id+="::root";  
	    		          }
	    		          id+="::"+ E0122_desc; 
	    		          id+="::"+SafeCode.encode(PubFunc.encrypt(pre)); 
	    		          tablestr.append("<tr id=\""+id+"\" style=\"cursor:hand;\" onclick=\"selectName('"+id+"');\"");
	    		          tablestr.append(" ondblclick=\"selectNamePer('"+id+"');\">");
	    		          tablestr.append("<td class=\"RecordRow\" style=\"border-top: none;\">");
	    		          tablestr.append(codeitemdesc+"</td>");
	    		          if(E0122_desc == null || E0122_desc.length() < 1)
	    		              E0122_desc = "&nbsp;";
	    		          tablestr.append("<td class=\"RecordRow\" style=\"border-top: none;border-left: none;\">"+E0122_desc+"</td></tr>");
					 }
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
      	  	}
        }
        tablestr.append("</table>");
        this.getFormHM().put("tablestr",tablestr.toString());
	}
}
