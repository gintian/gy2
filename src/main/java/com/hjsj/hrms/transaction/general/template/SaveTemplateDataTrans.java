/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TFieldFormat;
import com.hjsj.hrms.businessobject.general.template.TSubSetDomain;
import com.hjsj.hrms.businessobject.general.template.TemplateSetBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 10, 20061:47:41 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SaveTemplateDataTrans extends IBusiness {

	private HashMap searchSquence(ArrayList fieldlist)
	{
		HashMap hm=new HashMap();
		for(int i=0;i<fieldlist.size();i++)
		{
			Field item=(Field)fieldlist.get(i);
			if(item.isSequenceable())
			{
				hm.put(item.getName().toString(),item.getSequencename());
			}
		}
		return hm;
	}
	/**
	 * 设置消息的状态
	 * @param vo
	 * @param template_id
	 * @throws GeneralException
	 */
	private void setMessageState(RecordVo vo,String template_id)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer buf=new StringBuffer();
			buf.append("update tmessage set state=0 where a0100=? and db_type=? and noticetempid=?");
			ArrayList paralist=new ArrayList();
			paralist.add(vo.getString("a0100"));
			paralist.add(vo.getString("basepre"));
			paralist.add(template_id);
			dao.update(buf.toString(),paralist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 数据清洗，清洗掉变化前的历史记录
	 * @param vo
	 * @param hismap
	 */
	private void clearData(RecordVo vo,HashMap hismap)
	{
        Iterator seq=hismap.entrySet().iterator();
		while(seq.hasNext())
		{
			Entry entry=(Entry)seq.next();
			String fieldname=(String)entry.getKey();
			vo.removeValue(fieldname.toLowerCase().toString());
		}
	}
	/**处理数据
	 * nowrite_map2：无权限的单元格
	 * */
	private void decodeData(RecordVo vo,HashMap submap,String unrestrictedMenuPriv_Input,HashMap nowrite_map2) throws GeneralException
	{
        Iterator seq=submap.entrySet().iterator();
        try
        {
			while(seq.hasNext())
			{
				Entry entry=(Entry)seq.next();
				String fieldname=(String)entry.getKey();
				
				String[] temps=fieldname.split("_");
				String state=this.userView.analyseTablePriv(temps[1].toUpperCase());//子集权限
				
				if ("1".equals(temps[2])){//变化前，不需要更新 wangrd 2015-03-25
				    vo.removeValue(fieldname.toLowerCase());
                    continue;
				}
				
				if(nowrite_map2.get(fieldname.substring(2).toLowerCase())!=null)//&&unrestrictedMenuPriv_Input.equals("0"))
				{
	        		vo.removeValue(fieldname.toLowerCase());
	        		continue;
	        	}
				/**else if((state==null||!state.equalsIgnoreCase("2"))&&unrestrictedMenuPriv_Input.equals("0")) //判断子集是否有写权限
	        	{
	        		vo.removeValue(fieldname.toLowerCase());
	        		continue;
	        	}郭峰注释  只要指标有写权限，就能写入，而不去考虑子集权限*/
				else if((state==null|| "0".equalsIgnoreCase(state))&& "0".equals(unrestrictedMenuPriv_Input)) //判断子集是否有写权限
                {
                    vo.removeValue(fieldname.toLowerCase());
                    continue;
                }//wangrd 2015-03-25 无子集权限 还是要删除
				
				String value=vo.getString(fieldname.toLowerCase());
				/**考虑同一页的子集区域*/
				if(!(value==null||value.length()==0))
				{
					TemplateSetBo _bo=(TemplateSetBo)submap.get(fieldname);
					String xml_param=_bo.getXml_param();
					TSubSetDomain domain=new TSubSetDomain(xml_param);
					
					value=SafeCode.decode(value);
					value = PubFunc.keyWord_reback(value);
					value=value.replaceAll("&", "");//如果连续分隔符`，被替换成,号啦
				//	isMustFill_sub(value,domain,vo);
					vo.setString(fieldname.toLowerCase(), value);
				}
			}
        }
        catch(Exception e)
        {
        	throw GeneralExceptionHandler.Handle(e);
        }
	}
	/**将变化后子集的xml中rwPriv、fieldsPriv、fieldsWidth、fieldsTitle这四个属性去掉。郭峰*/
	private void removeAttributesFromXml(RecordVo vo,ArrayList fieldlist){
		try{
			int n = fieldlist.size();
			for(int i=0;i<n;i++){
				String newxml = "";
				Field field = (Field)fieldlist.get(i);
				String itemid = field.getName().toLowerCase();
				if(itemid.indexOf("t_")==0){//t_***
					int index = itemid.lastIndexOf("_");
					if((index<itemid.length()-1) && itemid.charAt(index+1)=='2'){//t_***_2
						String xml = vo.getString(itemid);
						if ("".equals(xml)){
							continue;
						}
						//开始从xml删除属性
						Document doc=PubFunc.generateDom(xml);
						Element element=null;
						XMLOutputter outputter = new XMLOutputter();
						Format format = Format.getPrettyFormat();
						format.setEncoding("UTF-8");//问题号：15705，“璟”字乱码，GB2312里没有该字，gaohy,2016-1-8
						outputter.setFormat(format);
						String xpath="/records";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
						element =(Element) findPath.selectSingleNode(doc);
						if(element!=null){
							if(element.getAttribute("rwPriv")!=null){
								element.removeAttribute("rwPriv");
							}
							if(element.getAttribute("fieldsPriv")!=null){
								element.removeAttribute("fieldsPriv");
							}
							if(element.getAttribute("fieldsWidth")!=null){
								element.removeAttribute("fieldsWidth");
							}
							if(element.getAttribute("fieldsTitle")!=null){//wangrd 2015-02-13
                                element.removeAttribute("fieldsTitle");
                            }
							if(element.getAttribute("fieldsDefault")!=null){
                                element.removeAttribute("fieldsDefault");
                            }
							if(element.getAttribute("slops")!=null){
                                element.removeAttribute("slops");
                            }
							if(element.getAttribute("coloumType")!=null){
                                element.removeAttribute("coloumType");
                            }
							if(element.getAttribute("coloumCodeset")!=null){
                                element.removeAttribute("coloumCodeset");
                            }
							if(element.getAttribute("coloumDecimalLength")!=null){
                                element.removeAttribute("coloumDecimalLength");
                            }
							if(element.getAttribute("coloumLength")!=null){
                                element.removeAttribute("coloumLength");
                            }
						}
						newxml = outputter.outputString(doc);
						vo.setString(itemid, newxml);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 模板子集是否有必填项没填
	 * @param value
	 * @param domain
	 * @return
	 */
	private boolean  isMustFill_sub(String value,TSubSetDomain domain,RecordVo vo) throws GeneralException
	{
		boolean flag=false;
		Document doc=null;
		Element element=null;
		TFieldFormat tf=null;
		try
		{
			if(value!=null&&value.length()>0)
			{
				doc=PubFunc.generateDom(value);;
				String xpath="/records/record";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0)
				{
					for(int i=0;i<childlist.size();i++)
					{
						element=(Element)childlist.get(i);
						String contentValue=element.getValue();
						if(contentValue!=null&&contentValue.length()>0)
						{
							String[] temps=contentValue.split("`");
							ArrayList fieldList=domain.getFieldfmtlist();
							for(int j=0;j<fieldList.size();j++)
							{
								tf=(TFieldFormat)fieldList.get(j);
								if(tf.isBneed())
								{
									String name="";
									if(vo.hasAttribute("a0101_1"))
										name=vo.getString("a0101_1");
									else if(vo.hasAttribute("a0101_2"))
										name=vo.getString("a0101_2");
									if(j>=temps.length)
									{
										flag=true;
										throw new Exception(name+" 模板子集中 "+tf.getTitle()+" 为必填项!");
										 
									}
									else if(temps[j].trim().length()==0)
									{
										flag=true;
										throw new Exception(name+" 模板子集中 "+tf.getTitle()+" 为必填项!");
									}
								}
								
							}
							
							
						}
					
					}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return flag;
	}
	
	
	
	
	
	
	/**
	 * 人员调入模板同步姓名至a0101_1
	 * @param operationtype
	 */
	private void sysnA0101(RecordVo vo,int operationtype,ArrayList fieldlist)
	{
		boolean bflag=false;
		if(operationtype==0)
		{
			if(vo.hasAttribute("a0101_2")&&vo.getValues().get("a0101_2")!=null) //xyy20141208当变化后的值被过滤掉的时候就不用给变化前的指标附上变化后的值
			{
				for(int i=0;i<fieldlist.size();i++)
				{
					Field tmp=(Field)fieldlist.get(i);
					/**数据集中有变化后姓名字段*/
					if("a0101_2".equalsIgnoreCase(tmp.getName()))
					{
						bflag=true;
						break;
					}
				}
				if(bflag)
					vo.setString("a0101_1", vo.getString("a0101_2"));
			}
		}
		if(operationtype==5)
		{  
			if(vo.hasAttribute("codeitemdesc_2")&&vo.getValues().get("codeitemdesc_2")!=null)//xyy20141208当变化后的值被过滤掉的时候就不用给变化前的指标附上变化后的值
			{
				for(int i=0;i<fieldlist.size();i++)
				{
					Field tmp=(Field)fieldlist.get(i);
					/**数据集中有变化后姓名字段*/
					if("codeitemdesc_2".equalsIgnoreCase(tmp.getName()))
					{
						bflag=true;
						break;
					}
				}
				if(bflag)
					vo.setString("codeitemdesc_1", vo.getString("codeitemdesc_2"));
			}
		}
	}

 
	
	private String  isPriv_ctrl(HashMap cell_param_map,String field,String value ){
		String sub_domain="";
		Document doc = null;
		Element element=null;
		StringBuffer sb = new StringBuffer();
		LazyDynaBean bean = (LazyDynaBean)cell_param_map.get(field);
		if(bean!=null&&bean.get("sub_domain")!=null)
			sub_domain=(String)bean.get("sub_domain");
		sub_domain = SafeCode.decode(sub_domain);
		if(sub_domain!=null&&sub_domain.length()>0)
		{
			try {
				doc=PubFunc.generateDom(sub_domain);;
				String xpath="/sub_para/para";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0)
				{
					element=(Element)childlist.get(0);
					String priv =(String)element.getAttributeValue("limit_manage_priv");
					if("1".equals(priv)){
						if(!this.userView.isSuper_admin()){
						if(value!=null&&!"".equals(value)&&!value.startsWith(this.userView.getManagePrivCodeValue())){
							
							if("b0110_2".equalsIgnoreCase(field))
								sb.append("变化后指标\"单位\"设置了按管理范围控制,请选择管理范围下的单位！\r\n");
							if("e0122_2".equalsIgnoreCase(field))
								sb.append("变化后指标\"部门\"设置了按管理范围控制,请选择管理范围下的部门！\r\n");
							if("E01A1_2".equalsIgnoreCase(field))
								sb.append("变化后指标\"职位\"设置了按管理范围控制,请选择管理范围下的职位！\r\n");
						
						}
						}
					}
				}
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("templet_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("templet_record");
		int idx=name.lastIndexOf("_");
		String tab_id=name.substring(idx+1);
		StringBuffer valueLengthError = new StringBuffer();
		/**数据集字段列表*/
		ArrayList fieldlist=(ArrayList)hm.get("templet_items");
		HashMap seqHm=searchSquence(fieldlist);
		/**分析是否为临时表还是审批表*/
		
		 
		String blacklist_per="";//黑名单人员库
		String blacklist_field="";//黑名单人员指标
		
		ContentDAO dao=null;
		try
		{
			String a0100=null;
            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
            dao=new ContentDAO(this.getFrameconn());
            Iterator seq=seqHm.entrySet().iterator();
			/**
			 * 查找变化前的历史记录单元格
			 * 保存时把这部分单元格的内容
			 * 过滤掉，不作处理
			 * */            
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tab_id),this.userView);
			String unrestrictedMenuPriv_Input=tablebo.getUnrestrictedMenuPriv_Input(); //数据录入不判断子集和指标权限, 0判断(默认值),1不判断 
			if(tablebo.getOperationtype()==0)//人员调入模板
			{
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				blacklist_per=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"base");//黑名单人员库
				blacklist_field=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"field");//黑名单人员指标
			}
			 
			HashMap his_map=tablebo.getHisModeCell();
            HashMap sub_map=tablebo.getHisModeSubCell();
            HashMap cell_map=tablebo.getModeCell3();
            HashMap cell_param_map=tablebo.getModeCell4();
            TSubSetDomain tSubSetDomain=new TSubSetDomain();
            HashMap templateFieldPriv_node=new HashMap();
			if(!(list==null||list.size()==0))
			{
				/** 查找 变化后的无写权限的单元格( 非子集 ) 过滤掉 */ 
				HashMap nowrite_map=tablebo.getModeCell2();
				 
				
				int ins_id=0;
				String task_id="";
				boolean batchCope=false; //是否是批量处理
				Document doc=null;
				Element element=null;
				
				for(int i=0;i<list.size();i++)
				{
					RecordVo vo=(RecordVo)list.get(i);
					int state=vo.getState();
					/**把记录集中的BLOB类型值清空*/
					vo.removeValue("photo");
					vo.removeValue("ext");
					vo.removeValue("submitflag");
					vo.removeValue("signature");
					vo.removeValue("attachment");
					if(state==-1)//Insert record ,新增记录,刷新数据时，定位不对,不能这样做啦
					{
						//a0100=  idg.getId("rsbd.a0100"); //去掉，未新加人或选人
						//vo.setString("a0100",a0100);
						/**自动生成序号,有问题，在架构里已取过数啦。。。,调入模板中不支持同单异号和同单同号规则*/
//						while(seq.hasNext())
//						{
//							Entry entry=(Entry)seq.next();
//							String fieldname=(String)entry.getKey();
//							String seqname=(String)entry.getValue();
//							String seq_no=idg.getId(seqname);
//							vo.setString(fieldname,seq_no);
//						}

						//dao.addValueObject(vo);
					}
					if(state==2)//更新记录
					{
						/**
						 * 查找变化前的历史记录单元格
						 * 保存时把这部分单元格的内容
						 * 过滤掉，不作处理
						 * */
						clearData(vo,his_map);
						
						//审批节点定义的指标权限
						
						if("templet_".equalsIgnoreCase(name.substring(0,8).toLowerCase()))
						{
							 
							String basepre=vo.getString("basepre").toLowerCase();
							if(ins_id!=0&&vo.getInt("ins_id")!=ins_id)
								batchCope=true;
							ins_id=vo.getInt("ins_id"); 
							task_id = ""+vo.getString("task_id");
							String _a0100=vo.getString("a0100");
//							this.frowset=dao.search("select task_id from "+name+" where lower(basepre)='"+basepre+"' and ins_id="+ins_id+" and a0100='"+_a0100+"' ");
//							if(this.frowset.next())
//								task_id=this.frowset.getInt("task_id");
							
							HashMap filedPrivMap=new HashMap();
							
	                    	
							if(templateFieldPriv_node.get(""+task_id)==null)
	                    	{
	                    		HashMap filedPriv=getFieldPriv(task_id,this.getFrameconn());
	                    		if(filedPriv.size()!=0)
	                    			templateFieldPriv_node.put(""+task_id,filedPriv);
	                    	}
	                    	filedPrivMap=(HashMap)templateFieldPriv_node.get(""+task_id);
	                    	if(filedPrivMap!=null&&filedPrivMap.size()>0)
	                    	{
	                    		Set keySet=filedPrivMap.keySet();
	                    		for(Iterator t=keySet.iterator();t.hasNext();)
	                    		{
	                    			String key=(String)t.next();
	                    			if(!"2".equals((String)filedPrivMap.get(key)))//&&tablebo.getUnrestrictedMenuPriv_Input().equals("0"))
	                    			{
	                    				nowrite_map.put(key,"1");
	                    			}  
	                    			else
	                    			{
	                    				if(nowrite_map.get(key)!=null)
	                    					nowrite_map.remove(key);
	                    			}
	                    		} 
	                    	}
						}
				
						
						
						/** 查找 变化后的无写权限的单元格( 非子集 ) 过滤掉 */ 
							clearData(vo,nowrite_map);
						
						HashMap nowrite_map2=new HashMap();
						Iterator seq0=nowrite_map.entrySet().iterator();
						while(seq0.hasNext())
						{
								Entry entry=(Entry)seq0.next();
								String fieldname=(String)entry.getKey();
								nowrite_map2.put(fieldname.toLowerCase().trim(),"1");
						}
						
						
						
						decodeData(vo,sub_map,unrestrictedMenuPriv_Input,nowrite_map2);
						//将变化后子集的xml中rwPriv、fieldsPriv、fieldsWidth这三个属性去掉。因为是脏数据。郭峰
						removeAttributesFromXml(vo,fieldlist);
						sysnA0101(vo,tablebo.getOperationtype(),fieldlist);	
						
					/*	for(int j=0;j<fieldlist.size();j++)
						{
							Field tmp=(Field)fieldlist.get(j);
							if(tmp.getCodesetid().equalsIgnoreCase("UM")||tmp.getCodesetid().equalsIgnoreCase("UN"))
							{
								String codesetid=tmp.getCodesetid();
								String temp="部门";
								if(codesetid.equalsIgnoreCase("UN"))
									temp="单位";
								if(vo.getString(tmp.getName())!=null&&vo.getString(tmp.getName()).trim().length()>0)
								{
									String value=vo.getString(tmp.getName()).trim();
									if(AdminCode.getCode(codesetid,value)==null)
										throw new Exception("不能填写非"+temp+"数据!");
								}
							}
						}*/
						
						StringBuffer priverror = new StringBuffer();
						for(int j=0;j<fieldlist.size();j++)
						{
							Field tmp=(Field)fieldlist.get(j);
							if(nowrite_map2.get(tmp.getName().toLowerCase())!=null)
								continue;
							if(tmp.getName().toUpperCase().indexOf("T_")==-1&& "0".equals(tmp.getCodesetid())&&tmp.getDatatype()!=DataType.DATE){
							    String _value=vo.getString(tmp.getName().toLowerCase());
							    if(tmp.getDatatype()==DataType.FLOAT||tmp.getDatatype()==DataType.DOUBLE){
							        if(_value.indexOf("E")!=-1){
							            int EIndex=_value.indexOf("E");//如果前台是用科学计数法的形式传过来,需要得到E的位置;
	                                    String Ehome =_value.substring(0, EIndex);//E 前面的数字
	                                    String Eend=_value.substring(EIndex+1);//得到E后面的数字
	                                    String value=(new BigDecimal(Math.pow(10, Double.parseDouble(Eend.trim()))).multiply(new BigDecimal(Ehome))).toString();
	                                    _value=PubFunc.round(value, tmp.getDecimalDigits());//得到转换后的数值
							        }
							        if(_value.indexOf(".")!=-1){
							            if(_value.split("\\.")[0].length()>tmp.getLength()){
							                valueLengthError.append(tmp.getLabel()+ResourceFactory.getProperty("templa.value.lengthError")+tmp.getLength()+","+ResourceFactory.getProperty("templa.value.fix"));
							                throw new Exception(valueLengthError.toString());
							            }
							        }else{
							            if(_value.length()>tmp.getLength()){
	                                         valueLengthError.append(tmp.getLabel()+ResourceFactory.getProperty("templa.value.lengthError")+tmp.getLength()+","+ResourceFactory.getProperty("templa.value.fix"));
	                                         throw new Exception(valueLengthError.toString());
							            }
							        }
							    }
							    //判断大文本内容的长度不能超过字典表设置的长度 2016-11-3
							    if("_2".equals(tmp.getName().substring(tmp.getName().length()-2))&&tmp.getName().indexOf("_")!=-1&&tmp.getDatatype()==DataType.CLOB)
							    { 
							    	String _itemid=tmp.getName().substring(0,tmp.getName().indexOf("_"));
							    	FieldItem item=DataDictionary.getFieldItem(_itemid.toLowerCase());
							    	if(item!=null&& "M".equals(item.getItemtype()))
							    	{
								    	if(item.getItemlength()!=0&&item.getItemlength()!=10)
								    	{ 
								    		if(_value.length()>item.getItemlength())
								    			throw new Exception(tmp.getLabel()+"超过了系统设置的最大字数"+item.getItemlength()+"!");
								    	} 
							    	}
							    }
							}
							if(tmp.getDatatype()==DataType.DATE&&(tmp.getName().indexOf("_1")!=-1||tmp.getName().indexOf("_2")!=-1)&&tmp.getName().toUpperCase().indexOf("T_")==-1)
							{
								
								if(vo.getString(tmp.getName())!=null&&vo.getString(tmp.getName()).trim().length()>0)
								{
									Date d=vo.getDate(tmp.getName());
									SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
									String dd=format.format(d);
									vo.setDate(tmp.getName().toLowerCase(), dd);
								}
							}
							else if(tmp.getDatatype()==DataType.CLOB&&tmp.getName().indexOf("_2")!=-1&&tmp.getName().toUpperCase().startsWith("T_")) //变化后子集 
							{
								String xml = vo.getString(tmp.getName().toLowerCase());
								String item_id="codeitemdesc_1";
								if(tablebo.getInfor_type()==1)
									item_id="a0101_1";
								if(xml.length()>0)
								{
									String _name="";
									if(vo.hasAttribute(item_id))
										_name=vo.getString(item_id);
									tSubSetDomain.validateSubValue(_name,xml,doc,element);
								}
								
							}
							else if(tmp.getDatatype()==DataType.STRING&&tmp.getName().indexOf("_2")!=-1&&tmp.getName().toUpperCase().indexOf("T_")==-1)
							{
								
								String _value=vo.getString(tmp.getName().toLowerCase());
								_value=_value.replaceAll("%26lt;","<");
								_value=_value.replaceAll("%26gt;",">");
								_value=_value.replaceAll("%26quot;","\"");
								_value=_value.replaceAll("%26apos;","'");
								
								
//								if(tmp.getName().equalsIgnoreCase("b0110_2")||tmp.getName().equalsIgnoreCase("e0122_2")||tmp.getName().equalsIgnoreCase("E01A1_2")){
//								String error =isPriv_ctrl(cell_param_map,tmp.getName().toLowerCase(),_value);
//								if(error.length()>0)
//									priverror.append(error);
//								}
								vo.setString(tmp.getName().toLowerCase(),_value);
							}
						}
						if(priverror.length()>0)
							throw new Exception(priverror.toString());
						
						//判断是否是黑名单里的人物
						if(blacklist_per!=null&&blacklist_field!=null&&blacklist_per.trim().length()>0&&blacklist_field.trim().length()>0)
						{
							if(cell_map.get(blacklist_field+"_2")!=null)
							{
								String value=vo.getString(blacklist_field+"_2");
								if(value!=null&&value.trim().length()>0)
								{
									if(tablebo.validateIsBlackList(blacklist_per,blacklist_field,value))
									{
										throw new Exception(vo.getString("a0101_1")+"在黑名单库有记录，不允许保存!");
									}
								}
							}
						}
						
						dao.updateValueObject(vo);
					}
					if(state==1)//删除记录
					{
						/**
						 * 来自于消息,如果删除了此记录，则需要把tmessage中的消息
						 * 置为未处理状态
						 * */
						String state_flag=vo.getString("state");
						if(state_flag==null|| "".equalsIgnoreCase(state_flag))
							state_flag="0";
						int from_msg=Integer.parseInt(state_flag);
						if(from_msg==1)
							setMessageState(vo,tab_id);
						dao.deleteValueObject(vo);
					}
				}//for loop end.
		 		
				Boolean bCalc=false;	
	    		if("0".equals(task_id)||"".equals(task_id) || "1".equals(tablebo.isStartNode(task_id))){
	    			if(tablebo.getAutoCaculate().length()==0){
	    				if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
	    					bCalc=true;
	    				}
	    			}
	    			else if("1".equals(tablebo.getAutoCaculate())){
	    				bCalc=true;
	    			}	
	    		}else {
	    			if(tablebo.getSpAutoCaculate().length()==0){
	    				if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
	    					bCalc=true;
	    				}
	    			}
	    			else if("1".equals(tablebo.getSpAutoCaculate())){
	    				bCalc=true;
	    			}
	    		}
	    		
				//如果为考勤业务申请模板，保存时增加自动计算功能
	    		if(bCalc)
				{  
		 			ArrayList formulalist=tablebo.readFormula();
		 			if(formulalist.size()>0)
		 			{
			 			if(name.equalsIgnoreCase("g_templet_"+tab_id))
			 			{	
			 				tablebo.setBEmploy(true);  
			 				tablebo.batchCompute("0");
				 			 
			 			} 
			 			else if(ins_id==0)
			 			{
			 				tablebo.batchCompute("0");  
			 			}
			 			else  //if(!batchCope&&tablebo.isStartNode(String.valueOf(ins_id),task_id,tab_id,tablebo.getSp_mode()).equals("1"))
			 			{
			 				tablebo.batchCompute(String.valueOf(ins_id));
			 			}
		 			}
				} 
			} 
	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}

	
	/**
	 * 获得节点定义的指标权限
	 * @param task_id
	 * @return
	 */
	public HashMap getFieldPriv(String task_id,Connection conn)
	{
		HashMap _map=new HashMap();
		Document doc=null;
		Element element=null;
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			String sql="select * from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_id+" )";
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
				if(ext_param!=null&&ext_param.trim().length()>0)
				{
				doc=PubFunc.generateDom(ext_param); 
				String xpath="/params/field_priv/field";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);
				if(childlist.size()==0){
					xpath="/params/field_priv/field";
					 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
					 childlist=findPath.selectNodes(doc);
				}
				if(childlist!=null&&childlist.size()>0)
				{
					for(int i=0;i<childlist.size();i++)
					{
						element=(Element)childlist.get(i);
						String editable="";
						//0|1|2(无|读|写)
						if(element!=null&&element.getAttributeValue("editable")!=null)
							editable=element.getAttributeValue("editable");
						if(editable!=null&&editable.trim().length()>0)
						{
							String columnname=element.getAttributeValue("name").toLowerCase();
							_map.put(columnname, editable);
						}
						
					}
				  }
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return _map;
	}
	
	
}
