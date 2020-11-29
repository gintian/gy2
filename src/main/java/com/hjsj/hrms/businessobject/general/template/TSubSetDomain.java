/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title:TSubSetDomain</p>
 * <p>Description:子集</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 19, 200610:03:28 AM
 * @author chenmengqing
 * @version 4.0
 */
public class TSubSetDomain {
	private boolean bhl;
	private boolean bvl;
	private boolean bcolhead;
	private String fields;
	private String setname;
	private int datarowcount = 0;
	private int colheadheight = 0;
	private ArrayList fieldfmtlist;
	private String content;
	private HashMap<Integer, String> filemap=new HashMap<Integer, String>();  //用于保存附件信息
	private UserView userview=null;
	/**  数据提交入库不判断子集和指标权限, 0判断(默认值),1不判断 */
	private String UnrestrictedMenuPriv="0";
	/**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */
	private String UnrestrictedMenuPriv_Input="0";
	/**关联序号的变化后指标是否手工生成序号, 0加人时自动生成(默认值),1手工生成 */
	private String id_gen_manual="0";
	private int infor_type=1;  //1:人员  2：单位|部门  3：职位
	/** 子集历史记录（引入的）是否可编辑  1：可编辑  0：不可编辑 */
	private String his_edit = "1";//默认可编辑
	/**附件类型 ，个人：1 ，    公共： 0*/
	private String attachmentType="0";
	/**子集记录必填（变化后存在） "false" 不必填（ 默认值）,"true" 必填  */
	private String mustfillrecord = "false";
	/** 区分相同的变化后子集的标志*/
	private String id="";
	/**子集自动扩展*/
	private String autoextend="false";
	/**允许删除历史记录 "0"不允许（默认）  "1"允许 */
    private String allow_del_his = "0";
	private Connection con=null;
	
	public TSubSetDomain()
	{
		
	}
	
	public TSubSetDomain(String content) {
		super();
		this.content=content;
		fieldfmtlist=new ArrayList();
		parse_subdomain(this.content);
	}
	/**
	 * 根据单位元格宽度，重新调整每列的宽度
	 * 
	 * @param width
	 */
	public void reSetWidth(int width)
	{
		int sumWidth=0;
		for(int i=0;i<fieldfmtlist.size();i++)
		{
			TFieldFormat fieldformat=(TFieldFormat)fieldfmtlist.get(i);
			sumWidth=sumWidth+fieldformat.getWidth();
		}//for i loop end.
		float fScale = 100f;
		if(sumWidth!=0){
			fScale=(float)width/sumWidth;
		}
		for(int i=0;i<fieldfmtlist.size();i++)
		{
			TFieldFormat fieldformat=(TFieldFormat)fieldfmtlist.get(i);
			fieldformat.setWidth(Math.round(fScale*fieldformat.getWidth()));
		}
	}
	
	/**
	 * 列宽数组
	 * @return
	 */
	public float[] getColumns()
	{
		float[] fcolumns=new float[this.getFieldfmtlist().size()];
		for(int i=0;i<this.getFieldfmtlist().size();i++)
		{
				TFieldFormat fieldformat=(TFieldFormat)fieldfmtlist.get(i);
				fcolumns[i]=fieldformat.getWidth();
		}
		return fcolumns;
	}
	/**
	 * 解释子集区域定义
	 * @param xmls
	 */
	private void parse_subdomain(String xmls)
	{
		if(xmls==null|| "".equals(xmls)) {
            return;
        }
		Document doc=null;
		Element element=null;
		try
		{
			doc=PubFunc.generateDom(xmls);;
			String xpath="/sub_para/para";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			List childlist=findPath.selectNodes(doc);	
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				this.setname=(String)element.getAttributeValue("setname");
				this.fields=(String)element.getAttributeValue("fields");
				this.datarowcount = Integer.parseInt((String)element.getAttributeValue("datarowcount"));
				this.colheadheight = Integer.parseInt((String)element.getAttributeValue("colheadheight"));
				this.bhl=Boolean.parseBoolean((String)element.getAttributeValue("hl"));				
				this.bvl=Boolean.parseBoolean((String)element.getAttributeValue("vl"));				
				this.bcolhead=Boolean.parseBoolean((String)element.getAttributeValue("colhead"));	
				if (element.getAttributeValue("his_edit") != null) {
					his_edit = (String) element.getAttributeValue("his_edit");
				}
				if (element.getAttributeValue("attachmentType") != null) {
					attachmentType = (String) element.getAttributeValue("attachmentType");
				}
				if (element.getAttributeValue("mustfillrecord") != null) {
					mustfillrecord = (String) element.getAttributeValue("mustfillrecord");
				}
				if (element.getAttributeValue("id") != null) {
					id = (String) element.getAttributeValue("id");
				}
				if (element.getAttributeValue("autoextend") != null) {
					autoextend = (String) element.getAttributeValue("autoextend");
				}
				if (element.getAttributeValue("allow_del_his") != null) {
					allow_del_his = (String) element.getAttributeValue("allow_del_his");
				}
			}
			xpath="/sub_para/field";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);			
			if(childlist!=null&&childlist.size()>0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					TFieldFormat fieldformat=new TFieldFormat();
					fieldformat.setName((String)element.getAttributeValue("name"));
					fieldformat.setTitle((String)element.getAttributeValue("title"));
					fieldformat.setValue((String)element.getAttributeValue("default"));
					fieldformat.setWidth(Integer.parseInt((String)element.getAttributeValue("width")));
					fieldformat.setSlop((String)element.getAttributeValue("slop"));	//xieguiquan 20101027
					String sw=(String)element.getAttributeValue("align");
					if(sw==null||sw.length()==0) {
                        sw="0";
                    }
					fieldformat.setAlign(Integer.parseInt(sw));
					
					sw=(String)element.getAttributeValue("valign");
					if(sw==null||sw.length()==0) {
                        sw="0";
                    }
					fieldformat.setValign(Integer.parseInt(sw));
					fieldformat.setBneed(Boolean.parseBoolean((String)element.getAttributeValue("need")));
					fieldformat.setPre(element.getAttributeValue("pre"));
					String his_readonly = (String)element.getAttributeValue("his_readonly");
					if(StringUtils.isBlank(his_readonly)) {
                        his_readonly = "false";
                    }
					if("true".equals(his_readonly)&&"1".equals(this.his_edit)) {
                        this.his_edit = "0";
                    }
					fieldformat.setHis_readonly(Boolean.parseBoolean(his_readonly));
					fieldfmtlist.add(fieldformat);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
	}
	/**
	 * 取得内容列表
	 * @param content
	 * @return
	 */
	public ArrayList getRecordList(String content)
	{
		ArrayList list=new ArrayList();
		if(content==null||content.length()==0) {
            return list;
        }
		Document doc=null;
		Element element=null;
		try
		{
			doc=PubFunc.generateDom(content);;
			Element root=doc.getRootElement();
			String fields=root.getAttributeValue("columns");
			String[] fieldarr=StringUtils.split(fields, "`");
			
			String xpath="/records/record";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			List childlist=findPath.selectNodes(doc);	
			if(childlist!=null&&childlist.size()>0)
			{
				for(int i=0;i<childlist.size();i++)
				{				
					element=(Element)childlist.get(i);
					String values=element.getText();
					Object[] valuearr=PubFunc.split(values, "`");    //StringUtils.split(values,"`");
				//删除的不用校验 
					String state = element.getAttributeValue("state");
					if(state!=null&& "D".equalsIgnoreCase(state)) {
                        continue;
                    }
					HashMap map=new HashMap();
					for(int j=0;j<fieldarr.length;j++)
					{
						String field_name=fieldarr[j];
						FieldItem item=DataDictionary.getFieldItem(field_name);
						if(item==null&&!"attach".equalsIgnoreCase(field_name)) {
                            continue;
                        }
						if("attach".equalsIgnoreCase(field_name)) {
                            map.put(field_name.toLowerCase(), (String)valuearr[j]);
                        } else {
                            map.put(item.getItemid().toLowerCase(), (String)valuearr[j]);
                        }
					}//for j loop end.
					list.add(map);
				}// for i loop end.
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		
		return list;
	}
	/**
	 * 取得pdf内容列表
	 * @param content
	 * @return
	 */
	public ArrayList getRecordPdfList(String content)
	{
		ArrayList list=new ArrayList();
		if(content==null||content.length()==0) {
            return list;
        }
		Document doc=null;
		Element element=null;
		try
		{
			content=content.replace("&", "＆");
			doc=PubFunc.generateDom(content);;
			Element root=doc.getRootElement();
			String fields=root.getAttributeValue("columns");
			String[] fieldarr=StringUtils.split(fields, "`");
			
			String xpath="/records/record";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			List childlist=findPath.selectNodes(doc);	
			if(childlist!=null&&childlist.size()>0)
			{
				for(int i=0;i<childlist.size();i++)
				{				
					element=(Element)childlist.get(i);
					String values=element.getText();
					Object[] valuearr=PubFunc.split(values, "`");    //StringUtils.split(values,"`");
					String state = element.getAttributeValue("state");
					if(state!=null&& "D".equalsIgnoreCase(state)) {
                        continue;
                    }
					HashMap map=new HashMap();
					for(int j=0;j<fieldarr.length;j++)
					{
						String field_name=fieldarr[j];
						FieldItem item=DataDictionary.getFieldItem(field_name);
						if(item==null) {
                            continue;
                        }
						String value = (String)valuearr[j];
						if(StringUtils.isNotBlank(value)) {
							value = value.replace("＆", "&");
						}
						map.put(item.getItemid().toLowerCase(), value);
					}//for j loop end.
					list.add(map);
				}// for i loop end.
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		
		return list;
	}	
	/**
	 * 求对应字段的默认值
	 * @param fieldname
	 * @return
	 */
	private String getFieldDefaultValue(String fieldname)
	{
		String value="";
		for(int i=0;i<fieldfmtlist.size();i++)
		{
			TFieldFormat fmt=(TFieldFormat)fieldfmtlist.get(i);
			if(fmt.getName().equalsIgnoreCase(fieldname))
			{
				value=fmt.getValue();
				break;
			}
		}
		return value;
	}
	/**
	 * 取得插入子集定义的记录列表
	 * @param xml       内容
	 * @param tablename 表名
	 * @param a0100     人员编号
	 * @return
	 */
	public ArrayList getChangeRecList(String xml,String tablename,String a0100)
	{
		ArrayList list=new ArrayList();
		if(xml==null||xml.length()==0) {
            return list;
        }
		Document doc=null;
		Element element=null;
		try
		{
			doc=PubFunc.generateDom(xml);;
			Element root=doc.getRootElement();
			String fields=root.getAttributeValue("columns");
			String[] fieldarr=StringUtils.split(fields, "`");
			
			String xpath="/records/record";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			List childlist=findPath.selectNodes(doc);	
			//找出某个人对应的子集的存在的记录
			ArrayList existList = this.findExistRecord(tablename,a0100);
			String value=null;
			ContentDAO dao=null;
			if(this.con!=null) {
                dao=new ContentDAO(this.con);
            }
			if(childlist!=null&&childlist.size()>0)
			{
				if(filemap!=null && filemap.size()>0)
				{
				   filemap.clear();
				}
				int index=0;//记录数据是排除删除数据之外的第几条数据
				for(int i=0;i<childlist.size();i++)
				{				
					element=(Element)childlist.get(i);
					String values=element.getText();
					Object[] valuearr=PubFunc.split(values, "`");    //StringUtils.split(values,"`");
					
					String i9999=element.getAttributeValue("I9999");
					String _state=element.getAttributeValue("state");   //删除标记
					String _edit=element.getAttributeValue("edit");  //历史记录只读标记
					RecordVo vo=new RecordVo(tablename);
					vo.setInt("i9999", Integer.parseInt(i9999));
					if(this.infor_type==1) {
                        vo.setString("a0100", a0100);
                    } else if(this.infor_type==2) {
                        vo.setString("b0110",a0100);
                    } else if(this.infor_type==3) {
                        vo.setString("e01a1",a0100);
                    }
					//判断i9999不等于-1的数据是否存在，不存在的话将其改成-1
					if(!"-1".equals(i9999)) {
						if(!existList.contains(Integer.parseInt(i9999))) {
							i9999 = "-1";
							vo.setInt("i9999", Integer.parseInt(i9999));
						}
					}
					if(Integer.parseInt(i9999)!=-1&&_state!=null&& "D".equals(_state)&&this.con!=null&&(!"0".equals(_edit)||("0".equals(_edit)&&"1".equals(allow_del_his))))
					{
						String sql="delete from "+tablename+" where i9999="+i9999;
						if(this.infor_type==1) {
                            sql+=" and a0100='"+a0100+"'";
                        } else if(this.infor_type==2) {
                            sql+=" and b0110='"+a0100+"'";
                        } else if(this.infor_type==3) {
                            sql+=" and e01a1='"+a0100+"'";
                        }
						dao.update(sql);
						continue;
					}
					for(int j=0;j<fieldarr.length;j++)
					{
						String field_name=fieldarr[j];
						
						//当子集中包含附件的时候，将附件的内容插到hr_multimedia_file表中，而不是子表中 liuzy 20151029
						if("attach".equals(field_name) && Integer.parseInt(i9999)!=-1 && valuearr.length==fieldarr.length){
							value=(String)valuearr[j];
							submitMultimediaFile(dao, value, tablename, a0100, i9999);
						}else if("attach".equals(field_name) && Integer.parseInt(i9999)==-1 && valuearr.length==fieldarr.length){
							value=(String)valuearr[j];
							if(!"".equals(value)){
								filemap.put(index, value);    //因数据表中没有attach字段，所以暂时把值放到filemap集合中 liuzy 20151031
							}
						}else{
							FieldItem item=DataDictionary.getFieldItem(field_name);
							if(item==null) {
                                continue;
                            }
							if(this.userview!=null&&!this.userview.isSuper_admin())
							{
								String state=this.userview.analyseFieldPriv(item.getItemid());
			                	if(state!=null&& "0".equals(state)) {
                                    state=this.userview.analyseFieldPriv(item.getItemid().toUpperCase(),0);	//员工自助权限
                                }
								if((state==null||!"2".equals(state))&&("0".equals(this.UnrestrictedMenuPriv)))//||this.UnrestrictedMenuPriv_Input.equals("0")))
                                {
                                    continue;
                                }
							}
							
							/**加上默认值*/
							if( valuearr.length>j){//如果默认值小于字段数，补空格
								value=(String)valuearr[j];
							}else{
								value="";
							}
							if(value==null||value.length()==0) {
                                value=getFieldDefaultValue(field_name);
                            }
							/**可以在这里加上数据类型判断*/
							if("D".equals(item.getItemtype()))
							{
								value=value.replaceAll("\\.","-");
								//if(value==null||value.length()==0)
									//continue;
								vo.setDate(field_name.toLowerCase(), value);
							}
							else if("N".equals(item.getItemtype()))
							{
								//if(value==null||value.trim().length()==0)
									//continue;
								//else
								//{
									if(item.getDecimalwidth()==0) {
                                        vo.setInt(field_name.toLowerCase(), Integer.parseInt(PubFunc.round(value,0)));
                                    } else
									{
									 	int decimalwidth=item.getDecimalwidth();
										int length=item.getItemlength();
										//liuyz 解决double类型数据存储空值报错
										if(value==null||"".equals(value)||value.trim().length()==0)
										{
											vo.setObject(field_name.toLowerCase(),null);
										}else if(String.valueOf((int)(Double.parseDouble(value))).length()>length) {
                                            vo.setDouble(field_name.toLowerCase(),0f);
                                        } else {
                                            vo.setDouble(field_name.toLowerCase(), Double.parseDouble(PubFunc.round(value,decimalwidth)));
                                        }
										 
										//vo.setDouble(field_name.toLowerCase(), Double.parseDouble(value));
									}
								
								//}
							}
							else{
								//if(value==null||value.length()==0)
								//	continue;
								vo.setString(field_name.toLowerCase(), value);
							}
						}//判断不是附件attach的情况
					}//for j loop end.
					list.add(vo);
					index++;
				}// for i loop end.
			}
			/*ArrayList newList=new ArrayList();
			ArrayList updList=new ArrayList();
			int m = 0;
			HashMap keymap=new HashMap();
			for(int j=0;j<list.size();j++)
    		{
    			RecordVo recvo=(RecordVo)list.get(j);
    			int i9999=recvo.getInt("i9999");
    			if(i9999==-1){
    				keymap.put(j, m);
    				m++;
    				newList.add(recvo);
    			}
    			else {
    				updList.add(recvo);
    			}
    		}
			list.clear();
			list.addAll(newList);
			list.addAll(updList);
			HashMap<Integer, String> newfilemap=new HashMap<Integer, String>();
			for(Integer dataKey : filemap.keySet())    
			{    
				newfilemap.put(Integer.valueOf(keymap.get(dataKey).toString()), filemap.get(dataKey));
			} 
			filemap = newfilemap;*/
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		return list;
	}
	/**
	 * 找出某个人对应的子集的存在的记录
	 * @param tablename
	 * @param a0100
	 * @return
	 */
	private ArrayList findExistRecord(String tablename, String a0100) {
		ContentDAO dao=new ContentDAO(this.con);
		RowSet rset = null;
		ArrayList list = new ArrayList();
		try {
			ArrayList paramlist = new ArrayList();
			String sql = "select i9999 from "+tablename+" where ";
			if(this.infor_type==1) {
                sql+=" a0100=?";
            } else if(this.infor_type==2) {
                sql+=" b0110=?";
            } else if(this.infor_type==3) {
                sql+=" e01a1=?";
            }
			paramlist.add(a0100);
			rset = dao.search(sql,paramlist);
			while(rset.next()) {
				int i9999 = rset.getInt("i9999");
				list.add(i9999);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rset);
		}
		return list;
	}
	/**
	 * 管理附件，删除子集中删除掉的，增加子集中添加的 liuzy 20151103
	 * 附件记录格式： 39fc2dd2-2166-4cc3-b841-734cf69b5637.png|A\A422\A048\2BC8C2BC-6921-4141-A861-10818A9A43E6\A04\5DDCB864-48E1-4FDC-888E-7B0A32CF2795|1邮件模板.png|111|1|type:1
	 * @param dao
	 * @param value xml中解析得到的附件对应的值
	 * @param tablename 子集表名
	 * @param a0100 人员编号
	 * @param i9999
	 */
	public void submitMultimediaFile(ContentDAO dao,String value,String tablename,String a0100,String i9999)
	{
		try
		{
			ArrayList<String> newIdList=new ArrayList<String>();
			HashMap<String,String> valueMap=new HashMap<String,String>(); //id与具体的值放到map集合中，用于后面根据id提取
			if(!"".equals(value)){
				if(value.indexOf(",")!=-1){
					String[]arrValue=value.split(",");
					//将id提取出来放到list集合中，用于与现档案库中的数据作比较，将id与具体的值放到map集合中，用于后面根据id提取
					for(int m=0;m<arrValue.length;m++){
						String id = "";
						String text = arrValue[m];
						int lastIndex = text.lastIndexOf("|");    //得到最后一个"|"位置
						String lastValue = text.substring(lastIndex+1,text.length());
						if(lastValue.indexOf("type:")>-1){//有类型
							String subValue = text.substring(0, lastIndex);
							int secondLastIndex = subValue.lastIndexOf("|");   //得到倒数第二个"|"位置
							String thridValue = subValue.substring(0, secondLastIndex);
							int thridLastIndex = thridValue.lastIndexOf("|");   //得到倒数第三个"|"位置
							id = text.substring(thridLastIndex+1, secondLastIndex);
						}else{
							String subValue = text.substring(0, lastIndex);
							int secondLastIndex = subValue.lastIndexOf("|");   //得到倒数第二个"|"位置
							id = text.substring(secondLastIndex+1, lastIndex);
						}
						newIdList.add(id);
						valueMap.put(id, text);
					}
				}else{
					String id = "";
					int lastIndex = value.lastIndexOf("|");
					String lastValue = value.substring(lastIndex+1,value.length());
					if(lastValue.indexOf("type:")>-1){//有类型
						String subValue = value.substring(0, lastIndex);
						int secondLastIndex = subValue.lastIndexOf("|");   //得到倒数第二个"|"位置
						String thridValue = subValue.substring(0, secondLastIndex);
						int thridLastIndex = thridValue.lastIndexOf("|");   //得到倒数第三个"|"位置
						id = value.substring(thridLastIndex+1, secondLastIndex);
					}else{
						String subValue = value.substring(0, lastIndex);
						int secondLastIndex = subValue.lastIndexOf("|");   //得到倒数第二个"|"位置
						id = value.substring(secondLastIndex+1, lastIndex);
					}
					newIdList.add(id);
					valueMap.put(id, value);
				}
			}
			
			String nbase=tablename.substring(0, 3);
			String setid=tablename.substring(3);
			
			MultiMediaBo multimediabo=new MultiMediaBo(con, userview, "A",nbase,setid, a0100, Integer.parseInt(i9999));
			String sql="select id,filename,path,srcfilename from hr_multimedia_file where ";
			sql+="childguid=(select GUIDKEY from  "+tablename+" where A0100='"+a0100+"' and I9999="+i9999+")";
			RowSet flagSet=dao.search(sql); 
			ArrayList<String> oldIdList=new ArrayList<String>();
			//将数据库中查询得到的id放入list集合中，用于跟子集得到的id值进行比较
			while(flagSet.next()){
				String id=flagSet.getString("id");                   //文件唯一标识 
				oldIdList.add(id);
			}
			//遍历循环两个ArrayList，将它们之间共同拥有的变量去掉，保存到map集合中
			Map hm= compareList(newIdList, oldIdList);
			ArrayList<String> addIdlist=(ArrayList<String>) hm.get(1); //得到新插入的附件记录
			ArrayList<String> deleteIdList=(ArrayList<String>) hm.get(2); //数据库中要删除的记录
			if(deleteIdList.size()>0){
				for(int n=0;n<deleteIdList.size();n++){
					String oldid=deleteIdList.get(n);
					String deleteSql="delete from hr_multimedia_file where id='"+oldid+"' and ";
					deleteSql+="childguid=(select GUIDKEY from  "+tablename+" where A0100='"+a0100+"' and I9999="+i9999+")";
					dao.update(deleteSql);  //删除已经被删除的子集的附件记录
				}
			}
			if(addIdlist.size()>0){
				ArrayList addList=new ArrayList();
				for(int z=0;z<addIdlist.size();z++){
					String newid=addIdlist.get(z);
					String text=valueMap.get(newid);
					addList.add(text);
				}
				//根据子集弹出窗体添加的附件，保存到hr_multimedia_file表中 liuzy 20151102
				saveMultimediaFile(addList, multimediabo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
	}
	
	/**
	 * 根据子集弹出窗体添加的附件，保存到hr_multimedia_file表中 liuzy 20151102
	 * 附件记录格式： 39fc2dd2-2166-4cc3-b841-734cf69b5637.png|A\A422\A048\2BC8C2BC-6921-4141-A861-10818A9A43E6\A04\5DDCB864-48E1-4FDC-888E-7B0A32CF2795|1邮件模板.png|111|1|type:1
	 * filename+'|'+path+'|'+localname+'|'+size+'|'+id+'|'+m+'|type:'+filetype ;//新人事异动格式
	 * @param lists 在xml中解析得到附件存储的部分
	 * @param multimediabo MultiMediaBo对象
	 * @param a0100 人员编号
	 * @param strbase 人员库
	 * @param setid 子集表名
	 * @param i9999 
	 */
	public void saveMultimediaFile(ArrayList lists,MultiMediaBo multimediabo)
	{
		try {
			for(int i=0;i<lists.size();i++){
				String values=(String) lists.get(i);
				String[]arrValue=values.split("\\|");
				String filename=arrValue[0];
				String path=arrValue[1];
				String localName=arrValue[2];
				String filetype = "";
				/*if(filename.indexOf(".")==-1){//bug 43844 上传子集附件之后，进行查看提示“找不到该文件”
					try{
						filename=PubFunc.decrypt(SafeCode.decode(filename));
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}*/
				if(arrValue.length==7){
					filetype = arrValue[6];
					int index = filetype.indexOf(":");
					filetype = filetype.substring(index+1,filetype.length());
				}else {
                    filetype = "F";
                }
				String title=""; 
				String ext="";
				if(localName.indexOf(".")!=-1){
					int index=localName.lastIndexOf(".");
					ext=localName.substring(index);
					title=localName.substring(0, index);
				}else{
					title = localName;
					int index=filename.lastIndexOf(".");
					ext = filename.substring(index);
				}
				/*改为vfs调用
				 * String rootdir = multimediabo.getRootDir();
				if (!rootdir.endsWith(File.separator))
					rootdir =rootdir+File.separator;  
				if(!path.startsWith(rootdir)) {
					path = rootdir+"multimedia"+File.separator+path;
				}
				File file=new File(path.replace("\\", File.separator).replace("/", File.separator),filename);
				if(!file.exists()){
					continue;
				}*/
				
				HashMap hmap = new HashMap();
				hmap.put("mainguid", "");//暂时去掉
				hmap.put("childguid", multimediabo.getChildGuid());
				hmap.put("a0100", multimediabo.getA0100());
				hmap.put("nbase", multimediabo.getNbase());
				hmap.put("setid", multimediabo.getSetId());
				hmap.put("i9999", multimediabo.getI9999());
				hmap.put("dbflag", "A");
				hmap.put("srcfilename", localName);
				hmap.put("description", "");
				hmap.put("filetitle", title);
				hmap.put("ext", ext);
				hmap.put("filetype", filetype);
				VfsFileEntity enty=VfsService.getFileEntity(path);
		        String name=enty.getName();
		        hmap.put("path", path);
		        hmap.put("srcfilename", name);
		        hmap.put("ext",name.substring(name.lastIndexOf(".")));
		        hmap.put("mainguid", multimediabo.getMainGuid());
		        //提交时修改文件类型为员工管理附件类型 用于区分引入附件时 附件来源于人事异动还是员工管理
		        VfsService.updateFileTag(path, VfsModulesEnum.YG.toString());
				multimediabo.saveMultimediaFile(hmap,true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 遍历循环两个ArrayList，将它们之间共同拥有的变量去掉，保存到map集合中，liuzy 20151031
	 * @param newids 操作人事异动子集时产生的附件id的list集合
	 * @param oldids 从数据库中查询得到的附件id的list集合
	 * @return
	 */
	private Map compareList(ArrayList<String> newids,ArrayList<String> oldids)
	{
		ArrayList<String> newid=new ArrayList<String>();
		ArrayList<String> oldid=new ArrayList<String>();
		for(int i=0;i<newids.size();i++)
		{
			newid.add(newids.get(i));
		}
		for(int j=0;j<oldids.size();j++)
		{
			oldid.add(oldids.get(j));
		}
		Map<Integer,ArrayList> hm=new HashMap<Integer,ArrayList>();
		for(int i=0;i<newids.size();i++){
			for(int j=0;j<oldids.size();j++){
				if(newids.get(i).equals(oldids.get(j))){
					newid.remove(newids.get(i));
					oldid.remove(oldids.get(j));
				}
			}
		}
		hm.put(1, newid);
		hm.put(2, oldid);
		return hm;
	}
	
	/**
	 * 校验模板子集指标内容是否符合格式
	 * @param name 对象名称
	 * @param xml 子集xml
	 * @param doc
	 * @param element
	 * @return
	 * @throws GeneralException
	 */
	public boolean validateSubValue(String name,String xml,Document doc,Element element)throws GeneralException
	{
		boolean flag=true; 
		
		try
		{
			doc=PubFunc.generateDom(xml);;
			Element root=doc.getRootElement();
			String fields=root.getAttributeValue("columns");
			String[] fieldarr=StringUtils.split(fields, "`");
			
			String xpath="/records/record";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			List childlist=findPath.selectNodes(doc);	
			String value=null; 
			Pattern p=null;
			Matcher m=null;
			if(childlist!=null&&childlist.size()>0)
			{
				for(int n=0;n<childlist.size();n++)
				{	
					element=(Element)childlist.get(n);
					String values=element.getText();
					Object[] valuearr=PubFunc.split(values, "`");    //StringUtils.split(values,"`");
					
					for(int e=0;e<fieldarr.length;e++)
					{
						String field_name=fieldarr[e];
						FieldItem item=DataDictionary.getFieldItem(field_name);
						if(item==null) {
                            continue;
                        }
						/**加上默认值*/
						value=(String)valuearr[e];
						if(value==null||value.length()==0) {
                            continue;
                        }
						if("D".equals(item.getItemtype()))
						{
							value=value.replaceAll("\\.","-"); 
							p= Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");		
							m = p.matcher(value);		
							boolean dateFlag = m.matches();		
							if (!dateFlag) {	
								throw new GeneralException(ResourceFactory.getProperty(name+" "+item.getItemdesc()+"格式不正确!"));
							}		
							try{
    							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    							dateFormat.setLenient(false);
    							dateFormat.parse(value);
							}
							catch(Exception edate){
							    throw new GeneralException(ResourceFactory.getProperty(name+" "+item.getItemdesc()+"格式不正确!")); 
							}
                             
							
						}
						else if("N".equals(item.getItemtype()))
						{
							  //负数报错 暂时排除第一位为负数的情况 wangrd 2015-04-21
							    String _value=value.trim();
							    if (_value!=null && _value.length()>1){
							        if ("-".equals(_value.substring(0,1))){
							            _value=_value.substring(1);
							        }
							    }
								if(item.getDecimalwidth()==0)
								{
									if(_value.indexOf(".") > 0)//如果小数点位数是0，则去掉小数点
                                    {
                                        _value = _value.substring(0,_value.indexOf("."));
                                    }
									p= Pattern.compile("^\\d{0,"+item.getItemlength()+"}$");	 
									m= p.matcher(_value.trim());		
									boolean dateFlag = m.matches();		
									if (!dateFlag) {
                                        throw new GeneralException(ResourceFactory.getProperty(name+" "+item.getItemdesc()+"只能保留"+item.getItemlength()+"位长度!"));
                                    }
								}
								else
								{
								 	int decimalwidth=item.getDecimalwidth();
									int length=item.getItemlength(); 
									 
									String[] temps=_value.trim().split("\\.");
									p= Pattern.compile("^\\d{0,"+item.getItemlength()+"}$");	 
									m= p.matcher(temps[0].trim());		
									boolean dateFlag = m.matches();		
									if (!dateFlag) {
                                        throw new GeneralException(ResourceFactory.getProperty(name+" "+item.getItemdesc()+" 整数部分只能保留"+length+"位长度!"));
                                    }
									if(temps.length==2)
									{
											p= Pattern.compile("^\\d{0,"+item.getDecimalwidth()+"}$");	 
											m= p.matcher(temps[1].trim());		
										    dateFlag = m.matches();		
											if (!dateFlag) {
                                                throw new GeneralException(ResourceFactory.getProperty(name+" "+item.getItemdesc()+" 小数部分只能保留"+decimalwidth+"位长度!"));
                                            }
									}
											
									 
								}
						}
						else if("A".equals(item.getItemtype())){
							
							if(value.getBytes().length>item.getItemlength()) {
                                throw new GeneralException(ResourceFactory.getProperty(name+" "+item.getItemdesc()+"超出指标规定长度!"));
                            }
							
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
		return flag;
		
	}
	
	
	
	/**
	 * 生成插入子集区域的内容
	 * @param fieldlist
	 * @param reclist   i9999`...` 字符串列表 
	 * state  D 表示删除 此功能暂时未实现
	 * for examples
	 <?xml version="1.0" encoding="GB2312"?>
		<records columns="A0405`A0406`A0410`A0415`">
			<record I9999="1"  state="">31``080409`1981.10.01</record>
			<record I9999="2"  state="">11``020201`1985.09.01</record>
			<record I9999="3"  state="D">11``020101`1984.09.01</record>
		</records>
	 * @return
	 */
	public String outContentxml(ArrayList fieldlist,ArrayList reclist)
	{
		//if(reclist.size()==0||fieldlist.size()==0)
		//	return "";
		StringBuffer buf=new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		buf.append("<records columns=\"");
		StringBuffer fields=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem item=(FieldItem)fieldlist.get(i);
			fields.append(item.getItemid());
			fields.append("`");
		}
		fields.setLength(fields.length()-1);
		buf.append(fields.toString());
		buf.append("\">");
		
		for(int i=0;i<reclist.size();i++)
		{
			ArrayList valuelist=(ArrayList)reclist.get(i);
			buf.append("<record I9999=\"");
			buf.append(valuelist.get(0));
			buf.append("\" deleted=\"0\"  ");
			String record_key_id= (String) valuelist.get(valuelist.size()-1);//子集记录唯一值。
			if(StringUtils.isNotBlank(record_key_id)) {
				buf.append(" record_key_id=\"");
				buf.append(record_key_id);
				buf.append("\"  ");
			}else{
				record_key_id=this.userview.getUserName()+System.currentTimeMillis()+(Math.round(Math.random()*100)*Math.round(Math.random()*100));
				buf.append(" record_key_id=\"");
				buf.append(record_key_id);
				buf.append("\"  ");
			}
			buf.append(" >");
			fields.setLength(0);
			for(int j=1;j<valuelist.size();j++)
			{
				fields.append(valuelist.get(j));
				fields.append("`");
			}
			fields.setLength(fields.length()-1);
			buf.append(fields.toString());
			buf.append("</record>");
		}
		buf.append("</records>");
		return buf.toString();
	}
	
	/**
	 * 生成插入子集区域的内容(带附件的情况)
	 * @param fieldlist
	 * @param reclist   i9999`...` 字符串列表 
	 * state  D 表示删除 此功能暂时未实现
	 * for examples
	 <?xml version="1.0" encoding="GB2312"?>
		<records columns="A0405`A0406`A0410`A0415`">
			<record I9999="1"  state="">31``080409`1981.10.01</record>
			<record I9999="2"  state="">11``020201`1985.09.01</record>
			<record I9999="3"  state="D">11``020101`1984.09.01</record>
		</records>
	 * @return
	 */
	public String outContentxml(ArrayList fieldlist,ArrayList reclist,boolean attachFlag)
	{
		//if(reclist.size()==0||fieldlist.size()==0)
		//	return "";
		StringBuffer buf=new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		buf.append("<records columns=\"");
		StringBuffer fields=new StringBuffer();
		boolean isCdata = false;
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem item=(FieldItem)fieldlist.get(i);
			int inputtype = item.getInputtype();
			if("M".equalsIgnoreCase(item.getItemtype())&&inputtype==1&&!isCdata) {
                isCdata =true;
            }
			fields.append(item.getItemid());
			fields.append("`");
		}
		if(attachFlag){
			fields.append("attach`");
		}
		fields.setLength(fields.length()-1);
		buf.append(fields.toString());
		buf.append("\">");
		
		for(int i=0;i<reclist.size();i++)
		{
			ArrayList valuelist=(ArrayList)reclist.get(i);
			buf.append("<record I9999=\"");
			buf.append(valuelist.get(0));
			buf.append("\" deleted=\"0\"  ");
			buf.append("edit=\"");
			buf.append(this.his_edit);
			buf.append("\"");
			String record_key_id= (String) valuelist.get(valuelist.size()-1);
			String isHaveChange= (String) valuelist.get(valuelist.size()-2);//获取是否修改标识
			if(StringUtils.isNotBlank(record_key_id)) {
				buf.append(" record_key_id=\"");
				buf.append(record_key_id);
				buf.append("\"  ");
			}else{
				record_key_id=this.userview.getUserName()+System.currentTimeMillis()+(Math.round(Math.random()*100)*Math.round(Math.random()*100));
				buf.append(" record_key_id=\"");
				buf.append(record_key_id);
				buf.append("\"  ");
			}
			if(StringUtils.isNotBlank(isHaveChange)) {//组装是否修改标识到子集记录
				buf.append(" isHaveChange=\"");
				buf.append(isHaveChange);
				buf.append("\"  ");
			}else{
				isHaveChange="false";
				buf.append(" isHaveChange=\"");
				buf.append(isHaveChange);
				buf.append("\"  ");
			}
			buf.append(" >");
			fields.setLength(0);
			for(int j=2;j<valuelist.size()-2;j++)
			{
				if(j==2&&isCdata) {
                    fields.append(" <![CDATA[");
                }
				String value=valuelist.get(j)+"";
				value = value.replace("`","｀");
				fields.append(value);
				fields.append("`");
			}
			fields.setLength(fields.length()-1);
			if(isCdata) {
                fields.append("]]>");
            }
			buf.append(fields.toString());
			buf.append("</record>");
		}
		buf.append("</records>");
		return buf.toString();
	}

	/**
	 * 输出空记录
	 * @return
	 */
	public String outContentxml()
	{
		//if(reclist.size()==0||fieldlist.size()==0)
		//	return "";
		StringBuffer buf=new StringBuffer();
		String slops="";
		String coloumType="";
		String coloumCodeset="";
		String coloumLength="";
		String coloumDecimalLength="";
		buf.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		buf.append("<records columns=\"");
		StringBuffer fields=new StringBuffer();
		for(int i=0;i<this.fieldfmtlist.size();i++)
		{
			TFieldFormat item=(TFieldFormat)this.fieldfmtlist.get(i);
			fields.append(item.getName());
			fields.append("`");
			String slop=item.getSlop();
			String pre = item.getPre();
			if("attach".equalsIgnoreCase(item.getName())){//附件一些属性没有会报空指针，这里全部赋值空。此处是配合60锁前台显示设置日期格式使用。
				coloumType+="";
				coloumCodeset+="";
				coloumDecimalLength+="";
				coloumLength+="";
				slops+="";
			}else{
				FieldItem fielditem = item.getFielditem();
				if(fielditem!=null) {
					coloumType+=item.getFielditem().getItemtype().toUpperCase();	
					coloumCodeset+=item.getFielditem().getCodesetid();
					coloumDecimalLength+=item.getFielditem().getDecimalwidth();
					coloumLength+=item.getWidth();
					if("D".equalsIgnoreCase(item.getFielditem().getItemtype())){
						slops+=slop;
					}
				}
			}
			
			
			if(i!=fieldfmtlist.size()-1){
				slops+=",";
				coloumType+=",";
				coloumCodeset+=",";
				coloumDecimalLength+=",";
				coloumLength+=",";
			}
		}
		if(fields.length()>0) {
            fields.setLength(fields.length()-1);
        }
		buf.append(fields.toString());
		buf.append("\" slops=\"");
		buf.append(slops);
		buf.append("\" coloumType=\"");
		buf.append(coloumType);
		buf.append("\" coloumCodeset=\"");
		buf.append(coloumCodeset);
		buf.append("\" coloumDecimalLength=\"");
		buf.append(coloumDecimalLength);
		buf.append("\" coloumLength=\"");
		buf.append(coloumLength);			
		buf.append("\">");
		buf.append("</records>");
		return buf.toString();
	}	
	public String outContentxml(ArrayList fieldlist,ArrayList reclist,boolean attachFlag, String flag)
	{
		StringBuffer buf=new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		buf.append("<records columns=\"");
		StringBuffer fields=new StringBuffer();
		boolean isCdata = false;
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem item=(FieldItem)fieldlist.get(i);
			int inputtype = item.getInputtype();
			if("M".equalsIgnoreCase(item.getItemtype())&&inputtype==1&&!isCdata) {
                isCdata =true;
            }
			fields.append(item.getItemid());
			fields.append("`");
		}
		if(attachFlag){
			fields.append("attach`");
		}
		fields.setLength(fields.length()-1);
		buf.append(fields.toString());
		buf.append("\">");
		
		for(int i=0;i<reclist.size();i++)
		{
			ArrayList valuelist=(ArrayList)reclist.get(i);
			String timestamp = (String) valuelist.get(1);
			String record_key_id= (String) valuelist.get(valuelist.size()-1);
			buf.append("<record I9999=\"");
			buf.append(valuelist.get(0));
			if("1".equals(flag)) {
				buf.append("\" state=\"");
				buf.append("\"  ");
			}else {
                buf.append("\" deleted=\"0\"  ");
            }
			buf.append("edit=\"");
			if("-1".equals(valuelist.get(0))) {
				buf.append("1");
			}else {
                buf.append(this.his_edit);
            }
			buf.append("\"  ");
			if(StringUtils.isNotBlank(timestamp)) {
				buf.append("timestamp=\"");
				buf.append(timestamp);
				buf.append("\"  ");
			}
			if(StringUtils.isNotBlank(record_key_id)) {
				buf.append("record_key_id=\"");
				buf.append(record_key_id);
				buf.append("\"  ");
			}else{
				record_key_id=this.userview.getUserName()+System.currentTimeMillis()+(Math.round(Math.random()*100)*Math.round(Math.random()*100));
				buf.append(" record_key_id=\"");
				buf.append(record_key_id);
				buf.append("\"  ");
			}
			buf.append(" >");
			fields.setLength(0);
			for(int j=2;j<valuelist.size();j++)
			{
				if(j==2&&isCdata) {
                    fields.append(" <![CDATA[");
                }
				String value=valuelist.get(j)+"";
				value = value.replace("<", "〈").replace(">", "〉");
				value = value.replace("＜", "〈").replace("＞", "〉");
				fields.append(value);
				fields.append("`");
			}
			fields.setLength(fields.length()-1);
			if(isCdata) {
                fields.append("]]>");
            }
			buf.append(fields.toString());
			buf.append("</record>");
		}
		buf.append("</records>");
		return buf.toString();
	}
	/**
	 * 得到返回前台的数据
	 * @param fieldlist
	 * @param orderlist
	 * @param attachFlag
	 * @return
	 */
	public ArrayList getRecords(ArrayList fieldlist, ArrayList subsetlist, boolean attachFlag) {
		ArrayList recordslist = new ArrayList();
		for(int i=0;i<subsetlist.size();i++) {
			ArrayList list=(ArrayList)subsetlist.get(i);
			HashMap map = new HashMap();
			String i9999 = (String) list.get(0);
			String timestamp = (String) list.get(1);
			String isHaveChange = (String) list.get(list.size()-2);
			if(StringUtils.isNotBlank(isHaveChange)){
				map.put("isHaveChange",isHaveChange);
			}else{
				map.put("isHaveChange","false");
			}
			String record_key_id= (String) list.get(list.size()-1);
			map.put("I9999", i9999);
			if(StringUtils.isNotBlank(record_key_id)){
				map.put("record_key_id",record_key_id);
			}else{
				map.put("record_key_id",this.userview.getUserName()+System.currentTimeMillis()+(Math.round(Math.random()*100)*Math.round(Math.random()*100)));
			}
			for(int j=0;j<fieldlist.size();j++)
			{
				FieldItem item=(FieldItem)fieldlist.get(j);
				String itemtype = item.getItemtype();
				String codesetid = item.getCodesetid();
				String itemid = item.getItemid();
				String value = "";
				if("A".equalsIgnoreCase(itemtype)&&StringUtils.isNotBlank(codesetid)&&!"0".equalsIgnoreCase(codesetid)) {//代码型
					String id=list.get(j+2)+"";
					String codevalue = AdminCode.getCodeName(codesetid,id);
					if("UM".equalsIgnoreCase(codesetid)&&StringUtils.isBlank(codevalue)){//bug 43962 关联um指标选择的是单位，刷新后前台不显示。
						codevalue = AdminCode.getCodeName("UN",id);
					}
					if(StringUtils.isNotBlank(id)&&StringUtils.isNotBlank(codevalue)) {
                        value = id+"`"+codevalue;
                    } else {
                        value = "";
                    }
				}else if("D".equalsIgnoreCase(itemtype)) {
					for(int k=0;k<this.fieldfmtlist.size();k++) {
						TFieldFormat fieldformat=(TFieldFormat)fieldfmtlist.get(k); 
						String name=fieldformat.getName().toLowerCase();
						if(name.equalsIgnoreCase(itemid)) {
							String slop =fieldformat.getSlop();
							if(StringUtils.isNotBlank(slop)) {
								map.put(itemid.toUpperCase()+"_D", slop);
							}	
						}
					}
					value = list.get(j+2)+"";
				}else {
                    value = list.get(j+2)+"";
                }
				map.put(itemid.toUpperCase(), value.replace("^", "＾").replace("~","～"));
			}
			if(attachFlag) {
				String value=list.get(list.size()-3)+"";
				String _value = "";
				if(value.length()>0) {
					String values [] = value.split(",");
					for(int m=0;m<values.length;m++) {
						String valuearr []= values[m].split("\\|");
						String value_ = "";
						for(int k=0;k<valuearr.length;k++) {
							if(k==0||k==1) {
								value_+=PubFunc.encrypt(valuearr[k])+"|";
							}else {
                                value_+=valuearr[k]+"|";
                            }
						}
						value_ = value_.substring(0, value_.length()-1);
						_value+=","+value_;
					}
					if(_value.length()>0) {
                        _value = _value.substring(1);
                    }
				}
				//value = value.replace("<", "〈").replace(">", "〉");
				//value = value.replace("＜", "〈").replace("＞", "〉");
				map.put("attach", _value);
			}
			if("-1".equals(i9999)) {
				map.put("hisEdit","1");
				if(StringUtils.isNotBlank(timestamp)) {
                    map.put("timestamp",timestamp);
                }
			}else {
				map.put("hisEdit",this.his_edit);
			}
			map.put("canEdit","true");
			recordslist.add(map);
		}
		return recordslist;
	}
	/**
	 * 将同步过来得数据按照保存过的顺序排列，并且没有勾选历史记录制度的列得数据不被历史数据覆盖
	 * @param fieldlist 
	 * @param oldXml
	 * @param curRecList
	 * @param field_name
	 * @return
	 */
	public ArrayList getOrderRecList(ArrayList fieldlist, String oldXml, ArrayList curRecList, String field_name) {
		ArrayList newRecList = new ArrayList();
		try{
			String chgstate = field_name.substring(field_name.lastIndexOf("_")+1, field_name.length());
			//得到没有勾选历史记录只读的列
			ArrayList noreadonly = new ArrayList();
			for(int k=0;k<this.fieldfmtlist.size();k++) {
				TFieldFormat fieldformat=(TFieldFormat)fieldfmtlist.get(k); 
				String name=fieldformat.getName().toLowerCase();
				boolean his_readonly = fieldformat.isHis_readonly();
				if(!his_readonly&&"2".equals(chgstate)) {
                    noreadonly.add(name);
                }
			}
			ArrayList oldRecList=getOldSubRecI999List(oldXml);
			//对已存在的记录排序
			HashMap i999_keyMap=new HashMap();
			for (int i=0;i<oldRecList.size();i++){
				HashMap map= (HashMap)oldRecList.get(i);
				String i9999 = (String)map.get("i9999");	
				String timestamp = (String)map.get("timestamp");
				String record_key_id = (String)map.get("record_key_id");
				String isHaveChange = (String)map.get("isHaveChange");//获取某一条子集记录是否修改标识
				i999_keyMap.put(i9999, record_key_id);
				for (int j=0;j<curRecList.size();j++){
					ArrayList valueList = (ArrayList)curRecList.get(j);
					String _i9999= (String)valueList.get(0);
					if (i9999.equals(_i9999)){
						for(int m=0;m<fieldlist.size();m++) {
							FieldItem item=(FieldItem)fieldlist.get(m);
							String itemid = item.getItemid().toLowerCase();
							if(noreadonly.contains(itemid)&&"true".equalsIgnoreCase(isHaveChange)) {//如果不是只读列，且修改了此行数据，取用户修改的值，否则取信息库中的值
								String value=(String)map.get(itemid);
								if(StringUtils.isNotBlank(value)) {
                                    valueList.set(m+1, value);
                                }
							}
						}
						if(noreadonly.contains("attach")&&"true".equalsIgnoreCase(isHaveChange)) {
							String value = (String)map.get("attach");
							if(StringUtils.isNotBlank(value)) {
                                valueList.set(valueList.size()-1,value);
                            }
						}
						valueList.add(1, timestamp);
						valueList.add(isHaveChange);
						valueList.add(record_key_id);
						newRecList.add(valueList);
						curRecList.remove(j);
						break;
					}
				}
				//将-1的记录按顺序记录下来
				if("-1".equals(i9999)) {
					ArrayList valueList = new ArrayList();
					valueList.add(i9999);
					valueList.add(timestamp);
					for(int m=0;m<fieldlist.size();m++) {
						FieldItem item=(FieldItem)fieldlist.get(m);
						String itemid = item.getItemid().toLowerCase();
						String value=(String)map.get(itemid);
						valueList.add(value);
					}
					if(this.fields.indexOf("attach")!=-1) {
						String value = (String)map.get("attach");
						valueList.add(value);
					}
					valueList.add(isHaveChange);
					valueList.add(record_key_id);
					newRecList.add(valueList);
				}
			}
			//将不存在的记录（新增记录）全部放在后面
			for (int j=0;j<curRecList.size();j++){
				String record_key_id=this.userview.getUserName()+System.currentTimeMillis()+(Math.round(Math.random()*100)*Math.round(Math.random()*100));
				ArrayList valueList = (ArrayList)curRecList.get(j);
				if(i999_keyMap.containsKey(valueList.get(0))){
					record_key_id=(String) i999_keyMap.get(valueList.get(0));
				}
				valueList.add(1, "");
				valueList.add("false");
				valueList.add(record_key_id);
				newRecList.add(valueList);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		return newRecList;
	}
	
	private ArrayList getOldSubRecI999List(String xml)
	{
		ArrayList list=new ArrayList();
		if(xml==null||xml.length()==0) {
            return list;
        }
		Document doc=null;
		Element element=null;
		try
		{
	        xml=xml.replace("&", "＆");
			doc=PubFunc.generateDom(xml);;
			Element root=doc.getRootElement();
			String fields=root.getAttributeValue("columns");
			String[] fieldsarr=StringUtils.split(fields, "`");
			
			String xpath="/records/record";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			List childlist=findPath.selectNodes(doc);
			if(childlist!=null&&childlist.size()>0)
			{
				for(int i=0;i<childlist.size();i++)
				{				
					element=(Element)childlist.get(i);
					String values=element.getText();
					Object[] valuearr=PubFunc.split(values, "`");    
					HashMap map = new HashMap();
					String i9999=element.getAttributeValue("I9999");
					String timestamp=element.getAttributeValue("timestamp");
					String record_key_id=element.getAttributeValue("record_key_id");
					record_key_id=StringUtils.isBlank(record_key_id)?this.userview.getUserName()+System.currentTimeMillis()+(Math.round(Math.random()*100)*Math.round(Math.random()*100)):record_key_id;
					String isHaveChange=element.getAttributeValue("isHaveChange");
					if(StringUtils.isBlank(isHaveChange)){
						isHaveChange="false";
					}
					timestamp = StringUtils.isBlank(timestamp)?"":timestamp;
					String state=element.getAttributeValue("state");
					state = StringUtils.isBlank(state)?"":state;
					if("D".equalsIgnoreCase(state)) {
                        continue;
                    }
					map.put("i9999", i9999);
					map.put("timestamp", timestamp);
					map.put("record_key_id", record_key_id);
					map.put("isHaveChange", isHaveChange);
					for(int j=0;j<fieldsarr.length;j++) {
						String name = fieldsarr[j];
						String value = (String)valuearr[j];
						if(StringUtils.isNotBlank(value)) {
							value = value.replace("＆", "&").replace("undefined", "");
						}
						map.put(name.toLowerCase(), value);
					}
					list.add(map);
				}
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		return list;
	}
	
	/**
	 * 格式化子集数据 目前只针对数值型
	 * @param fieldlist
	 * @param subsetlist
	 * @return
	 */
	public ArrayList formatRecordList(ArrayList fieldlist, ArrayList subsetlist) {
		for (int j=0;j<subsetlist.size();j++){
			ArrayList valueList = (ArrayList)subsetlist.get(j);
			for(int m=0;m<fieldlist.size();m++) {
				FieldItem item=(FieldItem)fieldlist.get(m);
				String itemid = item.getItemid().toLowerCase();
				String itemtype = item.getItemtype();
				if("N".equalsIgnoreCase(itemtype)) {
					for(int k=0;k<this.fieldfmtlist.size();k++) {
						TFieldFormat fieldformat=(TFieldFormat)fieldfmtlist.get(k); 
						String name=fieldformat.getName().toLowerCase();
						if(name.equalsIgnoreCase(itemid)) {
							int slop = Integer.parseInt(fieldformat.getSlop());
							int fieldlength = item.getDecimalwidth();
							int decimal = slop<fieldlength?slop:fieldlength;
							String value=(String) valueList.get(m+2);
							value = PubFunc.DoFormatDecimal(value, decimal);
							valueList.set(m+2, value);
							break;
						}
					}
				}
			}
		}
		return subsetlist;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isBcolhead() {
		return bcolhead;
	}
	public void setBcolhead(boolean bcolhead) {
		this.bcolhead = bcolhead;
	}
	public boolean isBhl() {
		return bhl;
	}
	public void setBhl(boolean bhl) {
		this.bhl = bhl;
	}
	public boolean isBvl() {
		return bvl;
	}
	public void setBvl(boolean bvl) {
		this.bvl = bvl;
	}
	public ArrayList getFieldfmtlist() {
		return fieldfmtlist;
	}
	public void setFieldfmtlist(ArrayList fieldfmtlist) {
		this.fieldfmtlist = fieldfmtlist;
	}
	public String getFields() {
		return fields;
	}
	public void setFields(String fields) {
		this.fields = fields;
	}
	public String getSetname() {
		return setname;
	}
	public void setSetname(String setname) {
		this.setname = setname;
	}
	public UserView getUserview() {
		return userview;
	}
	public void setUserview(UserView userview) {
		this.userview = userview;
	}
	public String getUnrestrictedMenuPriv() {
		return UnrestrictedMenuPriv;
	}
	public void setUnrestrictedMenuPriv(String unrestrictedMenuPriv) {
		UnrestrictedMenuPriv = unrestrictedMenuPriv;
	}
	public int getInfor_type() {
		return infor_type;
	}
	public void setInfor_type(int infor_type) {
		this.infor_type = infor_type;
	}
	public Connection getCon() {
		return con;
	}
	public void setCon(Connection con) {
		this.con = con;
	}
	public String getUnrestrictedMenuPriv_Input() {
		return UnrestrictedMenuPriv_Input;
	}
	public void setUnrestrictedMenuPriv_Input(String unrestrictedMenuPriv_Input) {
		UnrestrictedMenuPriv_Input = unrestrictedMenuPriv_Input;
	}
	public String getId_gen_manual() {
		return id_gen_manual;
	}
	public void setId_gen_manual(String id_gen_manual) {
		this.id_gen_manual = id_gen_manual;
	}

	public int getDatarowcount() {
		return datarowcount;
	}

	public void setDatarowcount(int datarowcount) {
		this.datarowcount = datarowcount;
	}

	public int getColheadheight() {
		return colheadheight;
	}

	public void setColheadheight(int colheadheight) {
		this.colheadheight = colheadheight;
	}

	public HashMap getFilemap() {
		return filemap;
	}

	public String getHis_edit() {
		return his_edit;
	}

	public void setHis_edit(String his_edit) {
		this.his_edit = his_edit;
	}

	public String getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}
	
	public String getMustfillrecord() {
		return mustfillrecord;
	}

	public void setMustfillrecord(String mustfillrecord) {
		this.mustfillrecord = mustfillrecord;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getAutoextend() {
		return autoextend;
	}

	public void setAutoextend(String autoextend) {
		this.autoextend = autoextend;
	}
	
	public String getAllow_del_his() {
		return allow_del_his;
	}

	public void setAllow_del_his(String allow_del_his) {
		this.allow_del_his = allow_del_his;
	}
}
