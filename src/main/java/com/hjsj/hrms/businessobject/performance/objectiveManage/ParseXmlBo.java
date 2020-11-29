package com.hjsj.hrms.businessobject.performance.objectiveManage;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

/*
<?xml version="1.0" encoding="GB2312"?>
<root>
	<rec a0100="010101011" name="单位/部门/职位/姓名" date="2009.10.10">
       总结内容。。。
    </rec>
<rec a0100="010101012" >
  总结内容。。。
</rec>
</root>
*/


public class ParseXmlBo {
	 Document a_doc = null;
	 private Connection conn=null;
	 
	 public ParseXmlBo(Connection con)
	 {
		 conn=con;
	 }
	 
	 
	 
	 private HashMap objectMap=new HashMap();
	 private HashMap point_objectMap=new HashMap();
	 public  void getPoint_objectMap(String object_id,String plan_id,String fieldItem,int object_type)
	 {
		 ContentDAO dao = new ContentDAO(this.conn);
		 try
		 {
			 StringBuffer sql=new StringBuffer("select "+fieldItem+",p0400 from p04 where plan_id="+plan_id);
			 if(object_type==1||object_type==3||object_type==4)
					sql.append(" and b0110='"+object_id+"'");
			 else if(object_type==2)
					sql.append(" and a0100='"+object_id+"'");
			 RowSet rowSet=dao.search(sql.toString());
			
			 ByteArrayInputStream input=null;
			 Element root=null;
			 while(rowSet.next())
			 {
				 String p0400=rowSet.getString("p0400");
				 String desc=Sql_switcher.readMemo(rowSet,fieldItem);
				 if(desc!=null&&desc.length()>0)
				 {
					    a_doc = PubFunc.generateDom(desc);
						root = a_doc.getRootElement();
						
						List list=root.getChildren();
						LazyDynaBean abean=null;
						for(Iterator t=list.iterator();t.hasNext();)
						{
							Element element=(Element)t.next();
							String name=element.getAttributeValue("name");
							abean=new LazyDynaBean();
							abean.set("name", name);
							abean.set("date", element.getAttributeValue("date"));
							abean.set("a0100", element.getAttributeValue("a0100"));
							abean.set("context",element.getValue());
							point_objectMap.put(p0400+"/"+element.getAttributeValue("a0100"), abean);
							String[] temp=name.split("/");
							objectMap.put(element.getAttributeValue("a0100")+"/"+temp[3], "1");
						}
				 }
				 
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 
	 }
	 
	 public  HashMap getTotalReviewMap(String object_id,String plan_id,String fieldItem,String txid)
	 {
		 ContentDAO dao = new ContentDAO(this.conn);
		 HashMap map=new HashMap();
		 try
		 {
			 StringBuffer sql=new StringBuffer("select "+fieldItem+" from per_object where object_id='"+object_id+"' and plan_id="+plan_id);
			 RowSet rowSet=dao.search(sql.toString());
			 ArrayList txList = new ArrayList();
			 ArrayList alist = new ArrayList();
			 txList.add(new CommonData("-1","全部"));
			 ByteArrayInputStream input=null;
			 Element root=null;
			 HashMap amap = new HashMap();
			 if(rowSet.next())
			 {
				 String desc=Sql_switcher.readMemo(rowSet,fieldItem);
				 if(desc!=null&&desc.length()>0)
				 {
					    a_doc = PubFunc.generateDom(desc);
						root = a_doc.getRootElement();
						
						List list=root.getChildren();
						LazyDynaBean abean=null;
						for(Iterator t=list.iterator();t.hasNext();)
						{
							Element element=(Element)t.next();
							String name=element.getAttributeValue("name");
							abean=new LazyDynaBean();
							abean.set("name", name);
							abean.set("date", element.getAttributeValue("date"));
							abean.set("a0100", element.getAttributeValue("a0100"));
							abean.set("context",element.getValue());
							String [] tt = name.split("/");
							if(amap.get(element.getAttributeValue("a0100"))==null)
							{
						    	CommonData cd = new CommonData(element.getAttributeValue("a0100"),tt[tt.length-1]);
						    	txList.add(cd);
						    	amap.put(element.getAttributeValue("a0100"), "1");
							}
							if(!"-1".equals(txid)&&!txid.equalsIgnoreCase(element.getAttributeValue("a0100")))
								continue;
							alist.add(abean);
						}
				 }
				 
			 }
			 map.put("txlist",txList);
			 map.put("alist",alist);
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return map;
	 }
	 
	 public  HashMap getPointReviewMap(String p0400,String fieldItem,String txid)
	 {
		 ContentDAO dao = new ContentDAO(this.conn);
		 HashMap map=new HashMap();
		 try
		 {
			 StringBuffer sql=new StringBuffer("select "+fieldItem+" from p04 where p0400="+p0400);
			 RowSet rowSet=dao.search(sql.toString());
			ArrayList txList = new ArrayList();
			ArrayList alist = new ArrayList();
			txList.add(new CommonData("-1","全部"));
			 ByteArrayInputStream input=null;
			 Element root=null;
			 HashMap amap = new HashMap();
			 if(rowSet.next())
			 {
				 String desc=Sql_switcher.readMemo(rowSet,fieldItem);
				 if(desc!=null&&desc.length()>0)
				 {
					    a_doc = PubFunc.generateDom(desc);
						root = a_doc.getRootElement();
						
						List list=root.getChildren();
						LazyDynaBean abean=null;
						for(Iterator t=list.iterator();t.hasNext();)
						{
							Element element=(Element)t.next();
							String name=element.getAttributeValue("name");
							abean=new LazyDynaBean();
							abean.set("name", name);
							abean.set("date", element.getAttributeValue("date"));
							abean.set("a0100", element.getAttributeValue("a0100"));
							abean.set("context",element.getValue());
							//map.put(element.getAttributeValue("a0100"), abean);
							String [] tt = name.split("/");
							if(amap.get(element.getAttributeValue("a0100"))==null)
							{
						    	CommonData cd = new CommonData(element.getAttributeValue("a0100"),tt[tt.length-1]);
						    	txList.add(cd);
						    	amap.put(element.getAttributeValue("a0100"), "1");
							}
							if(!"-1".equals(txid)&&!txid.equalsIgnoreCase(element.getAttributeValue("a0100")))
								continue;
							alist.add(abean);
						}
				 }
				 
			 }
			 map.put("txlist",txList);
			 map.put("alist",alist);
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return map;
	 }
	 
	 
	 
	 /**
	  * 往xml中插入内容
	  * @param objectid
	  * @param mainbodyid
	  * @param context
	  */
	 public void insertContext(String mainbodyid,String bodyNbase,String context,String p0400,String fieldItem,String plan_id,String object_id)
	 {
		 ContentDAO dao = new ContentDAO(this.conn);
		 try
		 {
			 
			StringBuffer temp_xml=new StringBuffer();
			temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
			temp_xml.append("<root>");
			temp_xml.append("</root>");		
			 
			 
			 StringBuffer sql=new StringBuffer("select "+fieldItem+",p0400 from p04 where p0400="+p0400);
			 int id=0;
			 if("-1".equalsIgnoreCase(p0400))
			 {
				 sql.setLength(0);
				 sql.append("select "+fieldItem+",id from per_object where object_id='"+object_id+"' and plan_id="+plan_id);
			 }
			 
			 RowSet rowSet=dao.search(sql.toString());
			 boolean isValue=false;
			 String xml="";
			 if(rowSet.next())
			 {
				 String content=Sql_switcher.readMemo(rowSet,fieldItem);
				 if(content.trim().length()>0)
				 {
					xml=content.toString();
				 }
				 else
					 xml=temp_xml.toString();
				 if("-1".equalsIgnoreCase(p0400))
					 id=rowSet.getInt(2);
			 }
			 a_doc = PubFunc.generateDom(xml);
			 Element root = this.a_doc.getRootElement();
			 XPath  xpath = XPath.newInstance("/root/rec[@a0100='"+mainbodyid+"']");
			 Element element = (Element)xpath.selectSingleNode(a_doc);
			 if(element!=null&&("opinions".equalsIgnoreCase(fieldItem)))//签批如果已有，不重新增加;总结回顾，回顾现改为全为新增
				 element.setText(context);
			 else
			 {
				
				 element = (Element)xpath.selectSingleNode(a_doc);
				 element=new Element("rec");
				 element.setAttribute("a0100",mainbodyid);
				 element.setAttribute("name",getName(mainbodyid,bodyNbase));
				 SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd HH:mm");
				 element.setAttribute("date",df.format(new Date()));
				 element.setText(context);
				 root.addContent(element);
			 }
			 
			 
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(a_doc));
			
			if("-1".equalsIgnoreCase(p0400))
			{
				RecordVo vo=new RecordVo("per_object");
				vo.setInt("id",id);
				vo=dao.findByPrimaryKey(vo);
				vo.setString(fieldItem, buf.toString());
				dao.updateValueObject(vo);
			}
			else
			{
				RecordVo vo=new RecordVo("p04");
				vo.setInt("p0400",Integer.parseInt(p0400));
				vo=dao.findByPrimaryKey(vo);
				vo.setString(fieldItem, buf.toString());
				dao.updateValueObject(vo);
			}
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	 }
	 /**
	  * 判断xml是否真正包含内容
	  * @param xml
	  * @return
	  */
	 public static boolean hasRecord(String xml)
	 {
		 boolean flag = false;
		 if(xml==null|| "".equals(xml))
			 return flag;
		 try
		 {
	     	 Document a_doc = PubFunc.generateDom(xml);
	    	 Element root = a_doc.getRootElement();
	    	 if(root.getChildren().size()>0)
	    		 flag=true;
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return flag;
	 }
	 public String getName(String mainbodyid,String bodyNbase)
	 {
		 String str="";
		 try
		 {
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet rowSet=dao.search("select * from "+bodyNbase+"A01 where a0100='"+mainbodyid+"'");
			 if(rowSet.next())
			 {
				 if(rowSet.getString("b0110")!=null)
					 str+=AdminCode.getCodeName("UN",rowSet.getString("b0110"));
				 str+="/";
				 if(rowSet.getString("e0122")!=null)
					 str+=AdminCode.getCodeName("UM",rowSet.getString("e0122"));
				 str+="/";
				 if(rowSet.getString("e01a1")!=null)
					 str+=AdminCode.getCodeName("@K",rowSet.getString("e01a1"));
				 str+="/";
				 str+=rowSet.getString("a0101");
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return str;
	 }
	 
	 
	public HashMap getObjectMap() {
		return objectMap;
	}
	public void setObjectMap(HashMap objectMap) {
		this.objectMap = objectMap;
	}
	public HashMap getPoint_objectMap() {
		return point_objectMap;
	}
	public void setPoint_objectMap(HashMap point_objectMap) {
		this.point_objectMap = point_objectMap;
	}
	
	
	
	
	
/////////////////////////////////////////////////////////////////////////////////////
	/**
	 * <?xml version="1.0" encoding="GB2312"?>
	 *<root>
	 *<record opt_object="010101011" name="单位/部门/职位/姓名"  date="2009.10.10"  sp_flag="02"  flag="1|0"(0：自批  1:代批 )  agent_id="0000002"  agent_name="单位/部门/职位/姓名"  report_to="单位/部门/职位/姓名">
     *      内容。。。
     *</record>
	 *<record .......... >
  	 *		内容。。。
	 *</record >
	 *</root>
	 */
	
	RecordVo plan_vo=null;
	/**
	  * 往xml中插入内容
	  * @param objectid
	  * @param mainbodyid
	  * @param context
	  */
	 public String produceRecord(String object_id,String  plan_id,String opt_object,String nbase,String context,String sp_flag,String flag,String agent_id,String agent_name,String report_to)
	 {
		 StringBuffer buf=new StringBuffer();
		 ContentDAO dao = new ContentDAO(this.conn);
		 try
		 {
			 
			StringBuffer temp_xml=new StringBuffer();
			temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
			temp_xml.append("<root>");
			temp_xml.append("</root>");		
			 
			 
			 StringBuffer sql=new StringBuffer("select reasons  from per_object where plan_id="+plan_id+" and object_id='"+object_id+"'");
			
			 RowSet rowSet=dao.search(sql.toString());
			 boolean isValue=false;
			 String xml="";
			 if(rowSet.next())
			 {
				 String content=Sql_switcher.readMemo(rowSet,"reasons");
				 if(content.trim().length()>0)
				 {
					xml=content.toString();
				 }
				 else
					xml=temp_xml.toString();
			 }
			 a_doc = PubFunc.generateDom(xml);;
			 Element root = this.a_doc.getRootElement();
			 {
				
				
				 Element element=new Element("record");
				 if("".equals(opt_object))
				 {
					 element.setAttribute("name",agent_name);
				 }
				 else
				 {
			    	 element.setAttribute("opt_object",opt_object);
			    	 if(this.plan_vo!=null&&this.plan_vo.getInt("plan_type")==0)
			    	 {
			    		 String _sql="select per_mainbody.*,per_mainbodyset.name from per_mainbody ,per_mainbodyset where per_mainbody.body_id=per_mainbodyset.body_id  and plan_id="+plan_id+" and object_id='"+object_id+"'";
			    		 _sql+=" and mainbody_id='"+opt_object+"'";
			    		 rowSet=dao.search(_sql);
				    	 if(rowSet.next())
				    		 element.setAttribute("name",rowSet.getString("name"));
					 
			    	 }
			       	 else				 
					    element.setAttribute("name",getName(opt_object,nbase));
				 }
				 SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd HH:mm");
				 element.setAttribute("date",df.format(new Date()));
				 element.setAttribute("flag",flag);
				 element.setAttribute("sp_flag",sp_flag);
				 element.setAttribute("agent_id",agent_id);
				 element.setAttribute("agent_name",agent_name);
				 element.setAttribute("report_to",report_to);
				 element.setText(context);
				 root.addContent(element);
			 }
		
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(a_doc));
			
			
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return buf.toString();
		 
	 }
	
		/**
		 * 取得考核对象审批流程明细
		 * @param plan_id
		 * @param object_id
		 * @return
		 */
		public HashMap getObjectSpDetailInfo(String plan_id,String object_id)
		{
			HashMap map = new HashMap();
			try
			{
				 StringBuffer buf = new StringBuffer("");
				 StringBuffer buf2 = new StringBuffer("");
				 StringBuffer sql=new StringBuffer("select reasons,a0101,score_process from per_object where plan_id="+plan_id+" and object_id='"+object_id+"'");
				 ContentDAO dao = new ContentDAO(this.conn);
				 RowSet rowSet=dao.search(sql.toString());
				 String content="";
				 String a0101="";
				 String score_process="";
				 while(rowSet.next())
				 {
				    content=Sql_switcher.readMemo(rowSet,"reasons");
				    a0101=rowSet.getString("a0101");
				    score_process=Sql_switcher.readMemo(rowSet,"score_process");
				 }
				 if(content.trim().length()<=0)
				 {
					 StringBuffer temp_xml=new StringBuffer();
					 temp_xml.append("<?xml version='1.0' encoding='UTF-8' ?>");
					 temp_xml.append("<root>");
					 temp_xml.append("</root>");
					 content=temp_xml.toString();
				 }
				 if(score_process.trim().length()<=0)
				 {
					 StringBuffer temp_xml=new StringBuffer();
					 temp_xml.append("<?xml version='1.0' encoding='UTF-8' ?>");
					 temp_xml.append("<root>");
					 temp_xml.append("</root>");
					 score_process=temp_xml.toString();
				 }
				 // 改由字符流创建Document，避免编码错误 by 刘蒙
		    	 a_doc = PubFunc.generateDom(score_process);
		    	 Element root1 = this.a_doc.getRootElement();
		    	 List list1=root1.getChildren();
		    	 a_doc=null;
		    	 for(Iterator t=list1.iterator();t.hasNext();)//评分过程  zhaoxg add 2014-3-20
				 {
						Element element=(Element)t.next();
						String name=element.getAttributeValue("name");
						String date=element.getAttributeValue("date");
						String status_desc=element.getAttributeValue("status_desc");
						String reason=element.getAttributeValue("reason");
						String report_to=element.getAttributeValue("report_to");
						if(report_to.length()>0){
							if(",".equals(report_to.substring(report_to.length()-1))){
								report_to=report_to.substring(0, report_to.length()-1);
							}
						}


						buf2.append(date+"  ");
						buf2.append(name==null?"":name);
						if(status_desc != null && status_desc.startsWith("退回")){
							if ("退回".equals(status_desc)) {
								buf2.append("  退回"+report_to+" 的评分");
								buf2.append("\r\n");
								buf2.append("  退回原因："+reason);
							} else {
								buf2.append("  ").append(status_desc);
								if (reason != null && !"".equals(reason.trim())) {
									buf2.append("\r\n  退回原因："+reason);
								}
							}
						}else if("提交".equals(status_desc)){
							buf2.append("  提交评分");
						}else if("同意".equals(status_desc)){
							buf2.append("  同意");
							buf2.append("\r\n");
							buf2.append("  意见："+reason);
						}
						buf2.append("\r\n");
						buf2.append("\r\n");
				 }
				 map.put("pfopinion",buf2.toString());


				content= content.replace("encoding=\"GB2312\"", "encoding=\"UTF-8\"").replace("encoding=\"gb2312\"", "encoding=\"UTF-8\"");
		    	 a_doc = PubFunc.generateDom(content);
		    	 Element root = this.a_doc.getRootElement();
		    	 List list=root.getChildren();

				 for(Iterator t=list.iterator();t.hasNext();)
				 {
						Element element=(Element)t.next();
						String flag=element.getAttributeValue("flag");
						String sp_flag=element.getAttributeValue("sp_flag");//==07
						buf.append(element.getAttributeValue("date")+"  ");
						buf.append(element.getAttributeValue("name")==null?"":element.getAttributeValue("name"));
						buf.append("   "+MyObjectiveBo.getSpflagDesc(sp_flag));
						if("02".equals(sp_flag)&&element.getAttributeValue("report_to")!=null)
							buf.append("  "+element.getAttributeValue("report_to"));//报批给
						else if("07".equals(sp_flag)&&element.getAttributeValue("report_to")!=null)
							buf.append("  "+element.getAttributeValue("report_to"));//驳回给
						if("01".equals(sp_flag))
							buf.append("(初始化)");
						if("-01".equals(sp_flag))
							buf.append("(状态初始化)");
						buf.append("   ");
						if("1".equals(flag))
						{
							buf.append("\r\n");
							buf.append("(代批人：");
							buf.append(element.getAttributeValue("agent_name")+")");
						}
						if("07".equals(sp_flag))
							buf.append("\r\n审核意见："+element.getText());
						/*if(sp_flag.equals("02"))
                            buf.append("\r\n交办原因："+element.getText());*/
						buf.append("\r\n");
						buf.append("\r\n");
				 }
				 map.put("detail",buf.toString());
				 map.put("a0101",a0101);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return map;
		}

		public RecordVo getPlan_vo() {
			return plan_vo;
		}

		public void setPlan_vo(RecordVo plan_vo) {
			this.plan_vo = plan_vo;
		}
	 
}
