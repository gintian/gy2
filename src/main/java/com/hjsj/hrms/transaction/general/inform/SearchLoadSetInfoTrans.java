/*
 * Created on 2006-2-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.inform;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchLoadSetInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String setid=(String)hm.get("setid");
		String pre=(String)hm.get("pre");
		String a0100=(String)hm.get("a0100");
		String isinfoself=(String)hm.get("isinfoself");
		String infokind=(String)hm.get("infokind");
		String state=(String)hm.get("sts");
		String fenleitype=(String)hm.get("fenleitype");
		
		//检查是否越权 gdd 14-09-23
		CheckPrivSafeBo bo = new CheckPrivSafeBo(this.frameconn, userView);
		a0100 = bo.checkOrg(a0100, "");
		
		/*加载字段标题*/
		this.getFormHM().clear();
		LoadFieldInfo(setid,pre,a0100,isinfoself,infokind,state,fenleitype);
		/*加载数据*/
		LoadData(setid,pre,a0100,isinfoself,infokind,state,fenleitype);
	}
	
	private void LoadFieldInfo(String setid,String pre,String a0100,String isinfoself,String infokind,String state,String fenleitype)
	{
		cat.debug("setid " + setid);
		if(setid!=null && setid.length()==8)
	        setid=setid.substring(5,8);
		else
	    	return;
	    cat.debug("setid " + setid);
	    StringBuffer setinfo=new StringBuffer();
	    List fieldlist=new ArrayList();



	    if("A00".equalsIgnoreCase(setid))
	    {
//		    yuxiaochun add programe ****************
	    	if(state!=null&&state.length()>0){
	    	FieldItem fielditemtitle=new FieldItem();
	    	fielditemtitle.setItemdesc(ResourceFactory.getProperty("conlumn.mediainfo.info_title"));
	    	fieldlist.add(fielditemtitle);
	    	FieldItem fielditemtype=new FieldItem();
	    	fielditemtype.setItemdesc(ResourceFactory.getProperty("conlumn.mediainfo.info_sort"));
	    	fieldlist.add(fielditemtype);
	    	}
//		    yuxiaochun add programe ****************
	    	if(state!=null&&state.length()>0){
			    FieldItem statefi=new FieldItem();
				statefi.setItemdesc(ResourceFactory.getProperty("hire.jp.pos.state"));
				statefi.setItemid("state");
				statefi.setItemtype(setid);
				statefi.setFieldsetid("A01");
				statefi.setCodesetid("0");
				fieldlist.add(0,statefi);
				FieldItem check=new FieldItem();
				check.setItemdesc(ResourceFactory.getProperty("lable.select"));
				check.setItemid("state");
				check.setItemtype("A");
				check.setFieldsetid(setid);
				check.setCodesetid("0");
				fieldlist.add(0,check);
	    	}
//			    yuxiaochun add programe ****************
		//huaitao add			
	    }else if("K00".equalsIgnoreCase(setid)||"B00".equalsIgnoreCase(setid) || "H00".equalsIgnoreCase(setid)){
	    	FieldItem fielditemtitle=new FieldItem();
	    	fielditemtitle.setItemdesc(ResourceFactory.getProperty("hire.zp_persondb.filetitle"));
	    	fieldlist.add(fielditemtitle);
	    	FieldItem fielditemdesc=new FieldItem();
	    	fielditemdesc.setItemdesc(ResourceFactory.getProperty("label.org.type_org"));
	    	fieldlist.add(fielditemdesc);
	    	
	    //huaitao add	
	    }else
	    {
	        if("0".equals(isinfoself)){
	  	      fieldlist= userView.getPrivFieldList(setid,0);   //获得当前子集的所有属性
//	  	    yuxiaochun add programe ****************
	  	      
		  	  if(state!=null&&state.length()>0){
			     FieldItem statefi=new FieldItem();
				 statefi.setItemdesc(ResourceFactory.getProperty("hire.jp.pos.state"));
				 statefi.setItemid("state");
				 statefi.setItemtype(setid);
				 statefi.setFieldsetid("A01");
				 statefi.setCodesetid("0");
				 fieldlist.add(0,statefi);
				 FieldItem check=new FieldItem();
				 check.setItemdesc(ResourceFactory.getProperty("kq.item.select"));
				 check.setItemid("state");
				 check.setItemtype("A");
				 check.setFieldsetid(setid);
				 check.setCodesetid("0");
				 fieldlist.add(0,check);
		  	   }
	        }
//		    yuxiaochun add programe ****************
	  	    else{
	  	      if(fenleitype!=null&&fenleitype.length()>0)
			  {
	  	    	InfoUtils infoUtils=new InfoUtils();
	  	    	fieldlist=infoUtils.getSubPrivFieldList(this.userView,setid,fenleitype);	    		
				if(fieldlist==null||fieldlist.size()<=0)//如果分类中得不到指标则用默认权限的
					fieldlist=userView.getPrivFieldList(setid);   //获得当前子集的所有属性
			  }else
	  	         fieldlist= userView.getPrivFieldList(setid); 	
//	  	    yuxiaochun add programe ****************
	  	    if(state!=null&&state.length()>0){
		    FieldItem statefi=new FieldItem();
			 statefi.setItemdesc(ResourceFactory.getProperty("hire.jp.pos.state"));
			 statefi.setItemid("state");
			 statefi.setItemtype(setid);
			 statefi.setFieldsetid("A01");
			 statefi.setCodesetid("0");
			 fieldlist.add(0,statefi);
			 FieldItem check=new FieldItem();
			 check.setItemdesc(ResourceFactory.getProperty("kq.item.select"));
			 check.setItemid("state");
			 check.setItemtype("A");
			 check.setFieldsetid(setid);
			 check.setCodesetid("0");
			 fieldlist.add(0,check);
			 }
	  	    }
//		    yuxiaochun add programe ****************
	    }    
		try
		{	     
		   setinfo.append("<TABLE width=\\\"100%\\\" border=\\\"0\\\" cellspacing=\\\"0\\\"  align=\\\"center\\\" cellpadding=\\\"0\\\" class=\\\"ListTable\\\" id=\\\"");
		   setinfo.append(setid);
		   setinfo.append("list\\\">");
		   setinfo.append("<TR>");
		   for(int i=0;i<fieldlist.size();i++)
		   {
		   	FieldItem fielditem=(FieldItem)fieldlist.get(i);
		    setinfo.append("<TD align=\\\"center\\\" class=\\\"TableRow\\\" nowrap>&nbsp;");
		    setinfo.append(/*PubFunc.reLineString(*/fielditem.getItemdesc().trim().replaceAll("\r\n",/*"\\r\\n"*/"").replaceAll("\r", "").replaceAll("\n", "")/*,10,"</br>")*/);
		    setinfo.append("&nbsp;</TD>");
		   }		  
		   setinfo.append("</TR>");
		   setinfo.append("</TABLE>");
		}catch(Exception sqle)
		{
		  sqle.printStackTrace();
		  
		}	
		cat.debug("---setinfo--->" + setinfo.toString());
		this.getFormHM().put("setinfo",setinfo.toString());
	}
	
	private void LoadData(String setid,String pre,String a0100,String isinfoself,String infokind,String state,String fenleitype)
	{
		//System.out.println("fadsfasdfasdfasdfasdf");
		if(setid!=null && setid.length()==8)
	        setid=setid.substring(5,8);
		else
	    	return;
	    cat.debug("setid " + setid);
	    StringBuffer strsql=new StringBuffer();                     //保存sql的字符串
		if("1".equals(infokind))
		{
			if("A00".equalsIgnoreCase(setid))
			{
				strsql.append("select mediasort.SORTNAME,");
				strsql.append(pre);
				strsql.append(setid);
				strsql.append(".title,");
				strsql.append(pre);
				strsql.append(setid);
				strsql.append(".a0100,");
				strsql.append(pre);
				strsql.append(setid);
				strsql.append(".fileid,");
				strsql.append(pre);
				strsql.append(setid);
				strsql.append(".i9999");
				strsql.append(" from mediasort," + pre + setid);
				strsql.append(" where ");
				strsql.append(pre + setid);
				strsql.append(".A0100='");
				strsql.append(a0100);
				strsql.append("' and ");
				strsql.append(pre + setid);
				strsql.append(".flag<>'p' and mediasort.flag=");
				strsql.append(pre + setid);
				strsql.append(".flag");
				strsql.append(" and mediasort.dbflag=1");
			}else
			{
			    strsql.append("select * from " + pre + setid);
			    strsql.append(" where A0100='" + a0100 + "'");
		    }			
		}else if("2".equals(infokind))          //单位库
		{
		//huaitao add
			if("B00".equalsIgnoreCase(setid))
			{
				strsql.append("select mediasort.SORTNAME,");
				strsql.append(setid);
				strsql.append(".title,");
				strsql.append(setid);
				strsql.append(".b0110,");
				strsql.append(setid);
				strsql.append(".fileid,");
				strsql.append(setid);
				strsql.append(".i9999");
				strsql.append(" from mediasort,"+setid);
				strsql.append(" where ");
				strsql.append(setid);
				strsql.append(".B0110='");
				strsql.append(a0100);
				strsql.append("' and ");
				strsql.append(setid);
				strsql.append(".flag<>'p' and mediasort.flag=");
				strsql.append(setid);
				strsql.append(".flag");
				strsql.append(" and mediasort.dbflag=2");
			}else{
				strsql.append("select * from " + setid);
				strsql.append(" where b0110='" + a0100 + "' order by i9999");
			}
		//huaitao add
		
		}else if("3".equals(infokind))          //职位库
		{
			//huaitao add
			if("K00".equalsIgnoreCase(setid))
			{
				strsql.append("select mediasort.SORTNAME,");
				strsql.append(setid);
				strsql.append(".title,");
				strsql.append(setid);
				strsql.append(".e01a1,");
				strsql.append(setid);
				strsql.append(".fileid,");
				strsql.append(setid);
				strsql.append(".i9999");
				strsql.append(" from mediasort,"+setid);
				strsql.append(" where ");
				strsql.append(setid);
				strsql.append(".E01A1='");
				strsql.append(a0100);
				strsql.append("' and ");
				strsql.append( setid);
				strsql.append(".flag<>'p' and mediasort.flag=");
				strsql.append( setid);
				strsql.append(".flag");
				strsql.append(" and mediasort.dbflag=3");
			}else{
				strsql.append("select * from " + setid);
				strsql.append(" where e01a1='" + a0100 + "'");
			}
		//huaitao add
		}else{
			if("K00".equalsIgnoreCase(setid) || "H00".equalsIgnoreCase(setid) )
			{
				strsql.append("select mediasort.SORTNAME,");
				strsql.append(setid);
				strsql.append(".title,");
				strsql.append(setid);
				strsql.append(".h0100,");
				strsql.append(setid);
				strsql.append(".fileid,");
				strsql.append(setid);
				strsql.append(".i9999");
				strsql.append(" from mediasort,"+setid);
				strsql.append(" where ");
				strsql.append(setid);
				strsql.append(".h0100='");
				strsql.append(a0100);
				strsql.append("' and ");
				strsql.append( setid);
				strsql.append(".flag<>'p' and mediasort.flag=");
				strsql.append( setid);
				strsql.append(".flag");
				if("K00".equalsIgnoreCase(setid))
					strsql.append(" and mediasort.dbflag=3");
				else
					strsql.append(" and mediasort.dbflag=4");
			}else{
				strsql.append("select * from " + setid);
				strsql.append(" where h0100='" + a0100 + "'");
			}
		}
		cat.debug("set strsql" +  strsql.toString());
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    List fieldlist=new ArrayList();

	    if("A00".equalsIgnoreCase(setid)||"B00".equalsIgnoreCase(setid)||"K00".equalsIgnoreCase(setid)||"H00".equals(setid))
	    {
	    	FieldItem fielditemtitle=new FieldItem();
	    	fielditemtitle.setItemid("title");
	    	fielditemtitle.setCodesetid("0");
	    	fielditemtitle.setItemtype("A");
	    	fieldlist.add(fielditemtitle);
	    	FieldItem fielditemtype=new FieldItem();
	    	fielditemtype.setItemid("sortname");
	    	fielditemtype.setCodesetid("0");
	    	fielditemtype.setItemtype("A");
	    	fieldlist.add(fielditemtype);
//		    yuxiaochun add program *****************************
	    	if(state!=null&&state.length()>0){
		    FieldItem statefi=new FieldItem();
			 statefi.setItemdesc(ResourceFactory.getProperty("hire.jp.pos.state"));
			 statefi.setItemid("state");
			 statefi.setItemtype("A");
			 statefi.setFieldsetid(setid);
			 statefi.setCodesetid("0");
			 fieldlist.add(0,statefi);
	    	}
//			    yuxiaochun add program *********************************
	    }else{
	        if("0".equals(isinfoself)){
		      fieldlist= userView.getPrivFieldList(setid,0);   //获得当前子集的所有属性
//			    yuxiaochun add program *****************************
		      if(state!=null&&state.length()>0){
			    FieldItem statefi=new FieldItem();
				 statefi.setItemdesc(ResourceFactory.getProperty("hire.jp.pos.state"));
				 statefi.setItemid("state");
				 statefi.setItemtype("A");
				 statefi.setFieldsetid(setid);
				 statefi.setCodesetid("0");
				 fieldlist.add(0,statefi);
		      }
//				    yuxiaochun add program *********************************
	        }
		    else{
		    	 if(fenleitype!=null&&fenleitype.length()>0)
				  {
		  	    	InfoUtils infoUtils=new InfoUtils();
		  	    	fieldlist=infoUtils.getSubPrivFieldList(this.userView,setid,fenleitype);	    		
					if(fieldlist==null||fieldlist.size()<=0)//如果分类中得不到指标则用默认权限的
						fieldlist=userView.getPrivFieldList(setid);   //获得当前子集的所有属性
				  }else
		            fieldlist= userView.getPrivFieldList(setid); 
//			    yuxiaochun add program *****************************
		      if(state!=null&&state.length()>0){
			    FieldItem statefi=new FieldItem();
				 statefi.setItemdesc(ResourceFactory.getProperty("hire.jp.pos.state"));
				 statefi.setItemid("state");
				 statefi.setItemtype("A");
				 statefi.setFieldsetid(setid);
				 statefi.setCodesetid("0");
				 fieldlist.add(0,statefi);
				 
		      }
//				    yuxiaochun add program *********************************
		    }
        }
        ArrayList rowlist=new ArrayList();      
		try
		{	     
		   List rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());    //获取子集的纪录数据
		   if("K00".equalsIgnoreCase(setid)||"H00".equalsIgnoreCase(setid)){
			   String posid="e01a1";
			   posid = "H00".equalsIgnoreCase(setid)?"h0100":posid;
			   String sql = "select '岗位说明书' as SORTNAME,title,"+posid+",i9999 from "+setid+" where flag='K' and "+posid+"='"+a0100+"'";
			   List pos = ExecuteSQL.executeMyQuery(sql,this.getFrameconn());
			   if(pos.size()>0)
			      rs.addAll(pos);
		   }
		   for(int r=0;!rs.isEmpty() && r<rs.size();r++)
		   {
		     ArrayList collist=new ArrayList();
		   	 LazyDynaBean rec=(LazyDynaBean)rs.get(r);
		     if(!fieldlist.isEmpty())                         //字段s
			 {
			  for(int i=0;i<fieldlist.size();i++)
			  {
			     FieldItem fielditem=(FieldItem)fieldlist.get(i);
			     if(!"0".equals(fielditem.getCodesetid()))                 //是否是代码类型的
			     {
			     	String codevalue=rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"";        //是,转换代码->数据描述	
			     	String codesetid=fielditem.getCodesetid();
			        if(codevalue !=null && codevalue.trim().length()>0 && codesetid !=null && codesetid.trim().length()>0)
			     	{
			     		String value=AdminCode.getCodeName(codesetid,codevalue);
			     		collist.add("&nbsp;"+value);
					}else{
						collist.add("");
					}
			     }else
			     {
				    if("D".equals(fielditem.getItemtype()))      //日期类型的有待格式化处理
				    {
				     	if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==10){
				     	   collist.add("&nbsp;"+new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,10)));
				     	}
	                    else if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==4){
	                       collist.add("&nbsp;"+new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,4)));
	                    }
	                    else if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==7){
	                       collist.add("&nbsp;"+new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,7)));	
	                    }	                     
	                    else{
	                    	collist.add("");
	                    }
				     }
				     else if("N".equals(fielditem.getItemtype()))                //数值类型的
				     {
				     	 collist.add("&nbsp;"+PubFunc.DoFormatDecimal(rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"",fielditem.getDecimalwidth()));
				     }else if("M".equals(fielditem.getItemtype()))
				     {
				    	 String value=rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString().replaceAll("\r\n","<br>"):"";
		       			 value=value.replaceAll("\n", "<br>");
		       			 value=value.replaceAll("\"", "“");
		       			 if(value!=null&&value.length()>10)
		       			 {   value = value.replaceAll("\r|\n", "<br>");
		       				 StringBuffer table=new StringBuffer();
			       			 table.append("<TABLE width=\\\"100%\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\"> ");
			       			 table.append("<TR>");		     		   
			       			 table.append("<TD onmouseout=\\\"UnTip()\\\" onmouseover=\\\"Tip('"+PubFunc.getTagStr(value)+"','STICKY',true)\\\" nowrap>&nbsp;");
			       			 String subvalue=value.substring(0,10);
			       			 if(subvalue.indexOf("<")==9)
			       				subvalue=subvalue.replaceAll("<", "");
			       			 table.append(subvalue+"...");
			       			 table.append("&nbsp;</TD>");
						   	 table.append("</TR>");
			       			 table.append("</TABLE>");
			       			 collist.add(table.toString());
		       			 }else
		       			 {
		       				 collist.add("&nbsp;"+value);
		       			 }
		       			 
				     }
				     else                                                       //其他字符串类型
				     {
				     	 if(("A00".equalsIgnoreCase(setid) || "B00".equalsIgnoreCase(setid) 
				     			 || "K00".equalsIgnoreCase(setid) || "H00".equalsIgnoreCase(setid))
				     			 && "title".equalsIgnoreCase(fielditem.getItemid())) {
				     	 	String title=rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString().replaceAll("\r\n","\\r\\n"):"";
				     	 	String fileId = (String) rec.get("fileid");
				     	 	if(StringUtils.isNotEmpty(fileId)) {
				     	 		title = "<a href=\\\"/servlet/vfsservlet?fromjavafolder=true&fileid=" + fileId + "\\\" target=\\\"_blank\\\">" + title + "</a>";
				     	 	}
				     	 	
				     	 	collist.add("&nbsp;"+ title);
					     }else{
					     //huaitao add
				       		 if("state".equalsIgnoreCase(fielditem.getItemid())){
				       			String sst=rec.get(fielditem.getItemid()).toString().replaceAll("\r\n","\\\r\\\n");
				       			if(sst==null||sst.length()<1){
				       				sst="3";
				       			}
				       		if(!"0".equals(sst)&&!"4".equals(sst)){
				       			collist.add("&nbsp;"+"<input type='checkbox' name=idset'"+fielditem.getFieldsetid()+"' value='"+setid+"/"+rec.get("a0100")+"/"+rec.get("i9999")+"' onclick='onchecks(this)'/>");
				       		}else{
				       			collist.add("");
				       		}
				       			 collist.add("&nbsp;"+ResourceFactory.getProperty("info.appleal.state"+sst));
				       		 }else{
				       			 String value=rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString().replaceAll("\r\n",/*"\\r\\n"*/"").replaceAll("\r", "").replaceAll("\n", ""):"";
				       			 value=value.replaceAll("\"", "“").replaceAll("：", ":");
				       			 collist.add("&nbsp;"+/*PubFunc.reLineString(*/value/*,11,"<br/>")*/);
				       		 }
				       		 }
                     }
			     }
			    }
			   }
			   rowlist.add(collist);
			  }		   
			 }
		     catch(Exception sqle)
			 {
			   sqle.printStackTrace();			   
			 }	
		     //System.out.println("rowlist" + rowlist);
		this.getFormHM().put("setdata",rowlist);	
	}

}
