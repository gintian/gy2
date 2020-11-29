package com.hjsj.hrms.businessobject.general.info;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class BatchHandBo {
	private UserView uv=null;
	
	public BatchHandBo(UserView uv){
		this.uv = uv;
	}
	
	/**
	 * 根据fieldsetid字段获取以构库的指标集
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList indList(String setname){
		ArrayList list = new ArrayList();
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		ArrayList dylist = DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		String pri = uv.analyseTablePriv(setname);
		if(dylist!=null&& "2".equals(pri)){
			for(int i=0;i<dylist.size();i++){
				FieldItem fielditem = (FieldItem)dylist.get(i);
				if(fielditem==null) {
                    continue;
                }
				if("2".equals(uv.analyseFieldPriv(fielditem.getItemid()))){
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
							||!"0".equals(fieldset.getChangeflag())){
						CommonData dataobj = new CommonData(fielditem.getItemid()+":"+
								fielditem.getItemtype()+":"+fielditem.getCodesetid(),fielditem.getItemdesc());
						list.add(dataobj);
					}
				}
			}
		}else{
			CommonData dataobj = new CommonData("","");
			list.add(dataobj);
		}
		
		return list;
	}
	/**
	 * 根据fieldsetid字段获取以构库的指标集
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList indList(String setname,String infor){
		ArrayList list = new ArrayList();
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		ArrayList dylist = DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		String pri = uv.analyseTablePriv(setname);
		if(dylist!=null&&!"0".equals(pri)){
			for(int i=0;i<dylist.size();i++){
				FieldItem fielditem = (FieldItem)dylist.get(i);
				if(fielditem==null) {
                    continue;
                }
				if("2".equals(infor)){
					if("B0110".equalsIgnoreCase(fielditem.getItemid())) {
                        continue;
                    }
				}
				if("3".equals(infor)){
					if("E01A1".equalsIgnoreCase(fielditem.getItemid())) {
                        continue;
                    }
				}
				if("2".equals(uv.analyseFieldPriv(fielditem.getItemid()))){
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
							||!"0".equals(fieldset.getChangeflag())){
						CommonData dataobj = new CommonData(fielditem.getItemid()+":"+
								fielditem.getItemtype()+":"+fielditem.getCodesetid(),fielditem.getItemdesc());
						list.add(dataobj);
					}
				}
			}
		}else{
			CommonData dataobj = new CommonData("","");
			list.add(dataobj);
		}
		
		return list;
	}
	/**
	 * 取得默认选中的字段
	 * @author FanZhiGuo
	 */
	public String getDefaultField(String setname,String field_name){
	        String field= "";
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		ArrayList dylist = DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		String pri = uv.analyseTablePriv(setname);
		if(dylist!=null&& "2".equals(pri)){
			for(int i=0;i<dylist.size();i++){
				FieldItem fielditem = (FieldItem)dylist.get(i);
				if("2".equals(uv.analyseFieldPriv(fielditem.getItemid()))){
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
							||!"0".equals(fieldset.getChangeflag())){
					    	if(fielditem.getItemid().equalsIgnoreCase(field_name)) {
                                field=fielditem.getItemid()+":"+fielditem.getItemtype()+":"+fielditem.getCodesetid();
                            }
					}
				}
			}
		}
		return field;
	}
	/**
	 * 根据fieldsetid字段获取以构库的指标集
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList refList(String itemid){
		ArrayList list = new ArrayList();
		if(itemid!=null&&itemid.trim().length()>0){
			FieldItem fielditem = DataDictionary.getFieldItem(itemid);
			if("2".equalsIgnoreCase(uv.analyseFieldPriv(fielditem.getItemid()))){
				if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))){
					String fieldtype = fielditem.getItemtype();
					ArrayList dylist = this.uv.getPrivFieldList(fielditem.getFieldsetid(),Constant.USED_FIELD_SET);
					if(dylist!=null){
						if(fielditem.isCode()){
							for(int i=0;i<dylist.size();i++){
								FieldItem field = (FieldItem)dylist.get(i);
								if(field.isCode()){
									if(field.getCodesetid().equalsIgnoreCase(fielditem.getCodesetid())
											&&!field.getItemid().equalsIgnoreCase(itemid)){
										CommonData dataobj = new CommonData(field.getItemid(),field.getItemdesc());
										list.add(dataobj);
									}
								}
							}
						}else{
							for(int i=0;i<dylist.size();i++){
								FieldItem field = (FieldItem)dylist.get(i);
								if(!field.isCode()){
									if(fieldtype.equalsIgnoreCase(field.getItemtype())){
										if(!"A".equalsIgnoreCase(fielditem.getItemtype())){
											if(!fielditem.getItemid().equalsIgnoreCase(field.getItemid())&&fielditem.getItemlength()>=field.getItemlength()){
												CommonData dataobj = new CommonData(field.getItemid(),field.getItemdesc());
												list.add(dataobj);
											}
										}else{
											if(!fielditem.getItemid().equalsIgnoreCase(field.getItemid())&&fielditem.getItemlength()>=field.getItemlength()){
												CommonData dataobj = new CommonData(field.getItemid(),field.getItemdesc());
												list.add(dataobj);
											}
										}
									}
								}
							}
						}
					}
				}
			}else{
				CommonData dataobj = new CommonData("","");
				list.add(dataobj);
			}
		}else{
			CommonData dataobj = new CommonData(" "," ");
			list.add(dataobj);
		}
		return list;
	}
}
