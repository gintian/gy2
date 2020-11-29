package com.hjsj.hrms.businessobject.org.orgdata;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class OrgDataBo {
	private Connection conn=null;
	private UserView userview=null;
	public OrgDataBo(Connection conn,UserView userview){
		this.conn = conn;
		this.userview = userview;
	}
	public ArrayList setList(ArrayList fieldlist){
		ArrayList setlist=new ArrayList();	
		GzAmountXMLBo xmlbo = new GzAmountXMLBo(conn,0);
		String viewname = "";
			viewname = xmlbo.getValue("base_set");
			viewname += xmlbo.getValue("ins_base_set");
		
		for(int i=0;i<fieldlist.size();i++){
			FieldSet fieldset=(FieldSet)fieldlist.get(i);
			if(viewname.toLowerCase().indexOf(fieldset.getFieldsetid().toLowerCase())!=-1) {
                continue;
            }
			if(fieldset==null) {
                continue;
            }
			/**未构库不加进来*/
			if("0".equalsIgnoreCase(fieldset.getUseflag())) {
                continue;
            }
//			if(fieldset.getFieldsetid().equalsIgnoreCase("B00"))
//				continue;
//			if(fieldset.getFieldsetid().equalsIgnoreCase("K00"))
//				continue;
			String pri =userview.analyseTablePriv(fieldset.getFieldsetid());
			if(pri!=null&& "0".equals(pri)) {
                continue;
            }
			CommonData temp=new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
			setlist.add(temp);
		}//for i loop end.
		return setlist;
	}
	public ArrayList itemList(FieldSet fieldset){
		ArrayList list = new ArrayList();
		ParameterXMLBo parameterXMLBo=new ParameterXMLBo(conn);
		String orgFieldIDs="";
		String contentType="";
		try {
			HashMap map=parameterXMLBo.getAttributeValues();
			if(map.get("org_brief")!=null&&((String)map.get("org_brief")).trim().length()>0){
				String temp=(String)map.get("org_brief");
				String[] temps=temp.split(",");
				orgFieldIDs=temps[0];
				contentType=temps[1];
			}
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuffer buf = new StringBuffer();
		buf.append("select itemid,displaywidth,displayid from fielditem where fieldsetid='");
		buf.append(fieldset.getFieldsetid());
		buf.append("' and useflag=1 order by displayid");
		
		ContentDAO dao=new ContentDAO(conn);
		try {
			
			if(!fieldset.isMainset()){
				FieldItem tempitem=DataDictionary.getFieldItem("B0110");
				Field tempfield=tempitem.cloneField();
				tempfield.setReadonly(true);
				list.add(tempfield);
				Field tempfield1=new Field("I9999","序号");
				tempfield1.setDatatype(DataType.INT);
				tempfield1.setReadonly(true);
				tempfield1.setVisible(true);
				list.add(tempfield1);
			}else{
				FieldItem tempitem=DataDictionary.getFieldItem("B0110");
				Field tempfield=tempitem.cloneField();
				tempfield.setReadonly(false);
				list.add(tempfield);
			}
			RowSet rs = dao.search(buf.toString());
			String tabpri =userview.analyseTablePriv(fieldset.getFieldsetid());
			while(rs.next()){
				String itemid=rs.getString("itemid");
				if(itemid.equalsIgnoreCase(orgFieldIDs)) {
                    continue;
                }
				if(itemid.equalsIgnoreCase(contentType)) {
                    continue;
                }
				FieldItem fielditem = DataDictionary.getFieldItem(itemid);
				if(fielditem!=null){
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
							||!"0".equals(fieldset.getChangeflag())){
						int displaywidth = rs.getInt("displaywidth");
						
						Field field=fielditem.cloneField();
						StringBuffer format=new StringBuffer();	
						field.setLength(fielditem.getItemlength());
						field.setCodesetid(fielditem.getCodesetid());
						if("N".equals(fielditem.getItemtype())){
							field.setDecimalDigits(fielditem.getDecimalwidth());
							if(fielditem.getDecimalwidth()>0){
								for(int j=0;j<fielditem.getDecimalwidth();j++){
									format.append("#");	
								}
								field.setFormat("####."+format.toString());
							}else{
								field.setFormat("####");
							}
						}
						field.setDatatype(getColumType(fielditem.getItemtype(),fielditem.getDecimalwidth()));
						String pri = userview.analyseFieldPriv(fielditem.getItemid());
						if(pri!=null&& "0".equals(pri)){
							field.setReadonly(true);
						}else if(pri!=null&& "1".equals(pri)){
							field.setReadonly(true);
						}else{
							field.setReadonly(false);
						}
						
						if(displaywidth<1){
							field.setVisible(false);
						}else{
							field.setVisible(true);
						}
						if(tabpri!=null&& "0".equals(tabpri)){
							field.setReadonly(true);
						}else if(tabpri!=null&& "1".equals(tabpri)){
							field.setReadonly(true);
						}
						list.add(field);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList itemList(FieldSet fieldset,String infor){
		ArrayList list = new ArrayList();
		ParameterXMLBo parameterXMLBo=new ParameterXMLBo(conn);
		String orgFieldIDs="";
		String contentType="";
		String mianitem="";
		try {
		    if(fieldset.isMainset())
		    {
		    	FieldItem item3=new FieldItem();
		    	item3.setFieldsetid(fieldset.getFieldsetid());
		    	item3.setItemid("oper");
		    	item3.setItemdesc(ResourceFactory.getProperty("column.operation"));
		    	item3.setItemtype("A");
		    	item3.setCodesetid("0");
		    	item3.setAlign("center");
		    	item3.setReadonly(true);
		    	list.add(item3.cloneField());
		    }	    
		    
			HashMap map=parameterXMLBo.getAttributeValues();
			if("2".equals(infor)){
				/** 过滤掉网址,和单位介绍*/
				if(map.get("org_brief")!=null&&((String)map.get("org_brief")).trim().length()>0){
					String temp=(String)map.get("org_brief");
					String[] temps=temp.split(",");
					orgFieldIDs=temps[0];
					contentType=temps[1];
				}
				mianitem="B0110";
			}else if("3".equals(infor)){
				mianitem="E01A1";
				FieldItem b0110item = DataDictionary.getFieldItem("B0110");
				list.add(b0110item.cloneField());
				FieldItem e01a1item = DataDictionary.getFieldItem("E0122");
				list.add(e01a1item.cloneField());
			}
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuffer buf = new StringBuffer();
		buf.append("select itemid,displaywidth,displayid,reserveitem from fielditem where fieldsetid='");
		buf.append(fieldset.getFieldsetid());
		buf.append("' and useflag=1 order by displayid");
		
		ContentDAO dao=new ContentDAO(conn);
		try {
			
			if(!fieldset.isMainset()){
				FieldItem tempitem=DataDictionary.getFieldItem(mianitem);
				Field tempfield=tempitem.cloneField();
				tempfield.setReadonly(true);
				list.add(tempfield);
				Field tempfield1=new Field("I9999","序号");
				tempfield1.setDatatype(DataType.INT);
				tempfield1.setReadonly(true);
				tempfield1.setVisible(true);
				list.add(tempfield1);
			}else{				
				FieldItem tempitem=DataDictionary.getFieldItem(mianitem);
				Field tempfield=tempitem.cloneField();
				tempfield.setReadonly(true);
				list.add(tempfield);
			}
			RowSet rs = dao.search(buf.toString());
			String tabpri =userview.analyseTablePriv(fieldset.getFieldsetid());
			while(rs.next()){
				String itemid=rs.getString("itemid");
				FieldItem fielditem = DataDictionary.getFieldItem(itemid);
				if(fielditem!=null){
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
							||!"0".equals(fieldset.getChangeflag())){
						int displaywidth = rs.getInt("displaywidth");
						
						Field field=fielditem.cloneField();
						StringBuffer format=new StringBuffer();	
						field.setLength(fielditem.getItemlength());
						field.setCodesetid(fielditem.getCodesetid());
						if("N".equals(fielditem.getItemtype())){
							field.setDecimalDigits(fielditem.getDecimalwidth());
							if(fielditem.getDecimalwidth()>0){
								for(int j=0;j<fielditem.getDecimalwidth();j++){
									format.append("#");	
								}
								field.setFormat("####."+format.toString());
							}else{
								field.setFormat("####");
							}
						}
						field.setDatatype(getColumType(fielditem.getItemtype(),fielditem.getDecimalwidth()));
						if(!this.userview.isSuper_admin()){
							if("0".equals(this.userview.analyseFieldPriv(fielditem.getItemid()))
									&& "0".equals(this.userview.analyseFieldPriv(fielditem.getItemid(),1))) {
                                field.setVisible(false);
                            } else {
                                field.setVisible(true);
                            }
							if("1".equals(this.userview.analyseFieldPriv(fielditem.getItemid()))
									|| "1".equals(this.userview.analyseFieldPriv(fielditem.getItemid(),1))) {
                                field.setReadonly(true);
                            }
						}else{
							field.setVisible(true);
						}
						if(displaywidth<1){
							field.setVisible(false);
						}
						String reserveitem = rs.getString("reserveitem");
						reserveitem=reserveitem!=null&&reserveitem.trim().length()>0?reserveitem:"0";
						if("1".equals(reserveitem)){
							field.setLabel(field.getLabel()+"<font color='red'>*</font>");
						}
						if(itemid.equalsIgnoreCase(orgFieldIDs)) {
                            field.setReadonly(true);
                        }
						if(itemid.equalsIgnoreCase(contentType)) {
                            field.setReadonly(true);
                        }
						list.add(field);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList fieldList(FieldSet fieldset,String infor){
		ArrayList list = new ArrayList();
		ParameterXMLBo parameterXMLBo=new ParameterXMLBo(conn);
		String orgFieldIDs="";
		String contentType="";
		String mianitem="";
		try {
			HashMap map=parameterXMLBo.getAttributeValues();
			if("2".equals(infor)){
				if(map.get("org_brief")!=null&&((String)map.get("org_brief")).trim().length()>0){
					String temp=(String)map.get("org_brief");
					String[] temps=temp.split(",");
					orgFieldIDs=temps[0];
					contentType=temps[1];
				}
				mianitem="B0110";
			}else if("3".equals(infor)){
				mianitem="E01A1";
				FieldItem b0110item = DataDictionary.getFieldItem("B0110");
				Field field=b0110item.cloneField();
				field.setVisible(false);
				list.add(field);
				
				FieldItem e01a1item = DataDictionary.getFieldItem("E0122");
				field=e01a1item.cloneField();
				field.setVisible(false);
				list.add(field);
			}
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuffer buf = new StringBuffer();
		buf.append("select itemid,displaywidth,displayid,reserveitem from fielditem where fieldsetid='");
		buf.append(fieldset.getFieldsetid());
		buf.append("' and useflag=1 order by displayid");
		
		ContentDAO dao=new ContentDAO(conn);
		try {
			
			if(!fieldset.isMainset()){
			        
			    	FieldItem item3=new FieldItem();
				item3.setFieldsetid(fieldset.getFieldsetid());
				item3.setItemid("oper");
				item3.setItemdesc(ResourceFactory.getProperty("column.operation"));
				item3.setItemtype("A");
				item3.setCodesetid("0");
				item3.setAlign("center");
				item3.setReadonly(true);
				
				String pri = "2";
				if(!this.userview.isSuper_admin()){
					pri = this.userview.analyseTablePriv(fieldset.getFieldsetid());
				}
				if(!"2".equals(pri))//子集是读权限 存在写权限的指标就可以编辑这些指标
				{
				    boolean isVisible=false;				
				    RowSet rs = dao.search(buf.toString());
					while(rs.next()){
						String itemid=rs.getString("itemid");
						if(itemid.equalsIgnoreCase(orgFieldIDs)) {
                            continue;
                        }
						if(itemid.equalsIgnoreCase(contentType)) {
                            continue;
                        }
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem!=null){
							if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
									||!"0".equals(fieldset.getChangeflag())){
								String fieldPriv = this.userview.analyseFieldPriv(fielditem.getItemid());
								if("2".equals(fieldPriv))//指标写权限
								{
								    isVisible=true;
								    break;
								}
							}
				    
						}
					}
					item3.setVisible(isVisible);				   
				}			
				
				list.add(item3.cloneField());
			    
				FieldItem tempitem=DataDictionary.getFieldItem(mianitem);
				Field tempfield=tempitem.cloneField();
				tempfield.setReadonly(true);
				tempfield.setVisible(false);
				list.add(tempfield);
				
				Field tempfield1=new Field("I9999","序号");
				tempfield1.setDatatype(DataType.INT);
				tempfield1.setReadonly(true);
				tempfield1.setVisible(false);
				list.add(tempfield1);
			}else{
				FieldItem tempitem=DataDictionary.getFieldItem(mianitem);
				Field tempfield=tempitem.cloneField();
				tempfield.setReadonly(false);
				list.add(tempfield);
			}
			RowSet rs = dao.search(buf.toString());
			String tabpri =userview.analyseTablePriv(fieldset.getFieldsetid());
			while(rs.next()){
				String itemid=rs.getString("itemid");
				if(itemid.equalsIgnoreCase(orgFieldIDs)) {
                    continue;
                }
				if(itemid.equalsIgnoreCase(contentType)) {
                    continue;
                }
				FieldItem fielditem = DataDictionary.getFieldItem(itemid);
				if(fielditem!=null){
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
							||!"0".equals(fieldset.getChangeflag())){
						int displaywidth = rs.getInt("displaywidth");
						
						Field field=fielditem.cloneField();
						StringBuffer format=new StringBuffer();	
						field.setLength(fielditem.getItemlength());
						field.setCodesetid(fielditem.getCodesetid());
						if("N".equals(fielditem.getItemtype())){
							field.setDecimalDigits(fielditem.getDecimalwidth());
							if(fielditem.getDecimalwidth()>0){
								for(int j=0;j<fielditem.getDecimalwidth();j++){
									format.append("#");	
								}
								field.setFormat("####."+format.toString());
							}else{
								field.setFormat("####");
							}
						}
						field.setDatatype(getColumType(fielditem.getItemtype(),fielditem.getDecimalwidth()));
						field.setVisible(true);
						
						if("0".equals(this.userview.analyseFieldPriv(fielditem.getItemid(),0))
								&& "0".equals(this.userview.analyseFieldPriv(fielditem.getItemid(),1))) {
                            field.setVisible(false);
                        }
						if("1".equals(this.userview.analyseFieldPriv(fielditem.getItemid(),0))
								|| "1".equals(this.userview.analyseFieldPriv(fielditem.getItemid(),1))) {
                            field.setReadonly(true);
                        }

//						if(tabpri!=null&&tabpri.equals("0")){
//							field.setReadonly(true);
//						}else if(tabpri!=null&&tabpri.equals("1")){
//							field.setReadonly(true);
//						}
						String reserveitem = rs.getString("reserveitem");
						reserveitem=reserveitem!=null&&reserveitem.trim().length()>0?reserveitem:"0";
						if("1".equals(reserveitem)){
							field.setLabel(field.getLabel()+"<font color='red'>*</font>");
						}
						list.add(field);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList a00ItemList(String infor){
		infor=infor!=null&&infor.trim().length()>0?infor:"1";
		ArrayList itemlist = new ArrayList();
		String setname = "B00";
		if("1".equals(infor)){
			setname = "A00";
		}else if("2".equals(infor)){
			setname = "B00";
		}else if("3".equals(infor)){
			setname = "K00";
		}else{
			setname = "A00";
		}
		Field fielditem = new Field("state","状态");
		fielditem.setCodesetid("23");
		fielditem.setDatatype(DataType.STRING);
		itemlist.add(fielditem);
	
		fielditem = new Field("flag","分类");
		fielditem.setCodesetid("0");
		fielditem.setDatatype(DataType.STRING);
		fielditem.setReadonly(true);
		itemlist.add(fielditem);
		
		fielditem = new Field("Title","名称");
		fielditem.setCodesetid("0");
		fielditem.setDatatype(DataType.STRING);
		itemlist.add(fielditem);
		
		fielditem = new Field("downole","浏览");
		fielditem.setCodesetid("0");
		fielditem.setDatatype(DataType.STRING);
		fielditem.setReadonly(true);
		itemlist.add(fielditem);
		
		fielditem = new Field("upole","调入");
		fielditem.setCodesetid("0");
		fielditem.setDatatype(DataType.STRING);
		fielditem.setReadonly(true);
		itemlist.add(fielditem);
		
		return itemlist;
	}
	public int getColumType(String type,int decimalwidth) {
		int temp=1;
		
		if("A".equals(type)){
			temp=DataType.STRING;
		}else if("D".equals(type)){
			temp=DataType.DATE;
		}else if("N".equals(type)){
				temp=DataType.FLOAT;
		}else if("M".equals(type)){
			temp=DataType.CLOB;
		}else{
			temp=DataType.STRING;
		}
		
		return temp;
	}
	
	/**
	 * 上级部门名称
	 * @param UMid 本部门codeitemid
	 * @param sept 分割符
	 * @param level 上几级部门
	 * @param dao 
	 * @return String like xx部/xx部/xx部
	 */
	public String getUpOrgUMDesc(String UMid,String sept,String level,ContentDAO dao){
		
	    RowSet rs = null;
	    String contant="";
	    try{
	    	String sql = "select codeitemid,codeitemdesc from organization where codeitemid = "+Sql_switcher.substr("'"+UMid+"'", "1", Sql_switcher.length("codeitemid"))+" and codesetid='UM' and codeitemid<>'"+UMid+"' order by codeitemid desc";
	    	rs = dao.search(sql);
	    	int lev = Integer.parseInt(level);
	    	for(int i=0;rs.next() && i<lev; i++ ){
	    		contant = sept+rs.getString("codeitemdesc")+contant;
	    	}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	    	
    		try {
    			if(rs!=null) {
                    rs.close();
                }
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
       }
	    
	    return contant;
	    
   }
}
