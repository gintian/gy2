package com.hjsj.hrms.transaction.sys.options.param;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *<p>Title:AddSubclass.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class AddSubclass extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tag = (String)this.getFormHM().get("tag");
		String tagname = (String)this.getFormHM().get("tagname");
		ArrayList subclass_value=(ArrayList)this.getFormHM().get("subclass_value");
		String[] subclass = new String[subclass_value.size()];
		for(int i=0;i<subclass_value.size();i++){
			subclass[i] = (String)subclass_value.get(i);
		}
		SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.getFrameconn());
		String oldsubclass=infoxml.saveView_Value(tag,tagname,subclass,this.getFrameconn());
		String mess = "";
		if("set_a".equalsIgnoreCase(tag)){
			for(int i=1;i<subclass.length+1;i++){
				FieldSet fieldset =DataDictionary.getFieldSetVo(subclass[i-1].toUpperCase());
				if(fieldset!=null)
					mess += fieldset.getCustomdesc()+",";
				if(i%5==0)
					mess += "<br>";
			}
			ArrayList newsubclass= new ArrayList();
			ArrayList delsubclass = new ArrayList();
			String[] oldsubclasss = oldsubclass.split(",");
			String cursubclass = subclass_value.toString();
			for(int i=0;i<oldsubclasss.length;i++){
				if(cursubclass.indexOf(oldsubclasss[i])==-1){//减少的
					delsubclass.add(oldsubclasss[i]);
				}
			}
			for(int i=0;i<subclass.length;i++){
				if(oldsubclass.indexOf(subclass[i])==-1){//新增的
					newsubclass.add(subclass[i]);
				}
			}
			String tagorder =infoxml.getInfo_param("order");
			
			if(tagorder!=null&&tagorder.length()>0)
			{
				/*String[] tagorders = tagorder.split(",");			
				ArrayList set_A_list=infoxml.getSet_A_Name$Text();
				ArrayList fenlei_list=infoxml.getFenLeiDesc(set_A_list);
				ArrayList sparetaglist=new ArrayList();
				for(int i=0;i<tagorders.length;i++){
					if(!isFenLei(fenlei_list,tagorders[i]))//如果不存在则放入排序列
					  sparetaglist.add(tagorders[i]);
				}
				String[] orders=new String[sparetaglist.size()];
				for(int i=0;i<sparetaglist.size();i++)
				{
					orders[i]=(String)sparetaglist.get(i);
				}*/
				if(!tagorder.startsWith(",")){
					tagorder=","+tagorder;
				}
				for(int i=0;i<newsubclass.size();i++){
					String setid = (String)newsubclass.get(i);
					FieldSet fieldset =DataDictionary.getFieldSetVo(setid.toUpperCase());
					if(fieldset!=null){
						String temp = tagorder.toUpperCase();
						if(temp.indexOf(","+fieldset.getCustomdesc().toUpperCase()) != -1)
							tagorder =tagorder.substring(0,temp.indexOf(","+fieldset.getCustomdesc().toUpperCase())) + tagorder.substring(temp.indexOf(","+fieldset.getCustomdesc().toUpperCase())+fieldset.getCustomdesc().length()+1);
					}
				}
				for(int i=0;i<delsubclass.size();i++){
					String setid = (String)delsubclass.get(i);
					FieldSet fieldset =DataDictionary.getFieldSetVo(setid.toUpperCase());
					//添加 tagorder.indexOf(","+fieldset.getCustomdesc())==-1 判断。如果已经存在就不再添加。解决重复添加问题   guodd 2014-12-31
					if(fieldset!=null && tagorder.indexOf(","+fieldset.getCustomdesc())==-1){
						tagorder=tagorder+","+fieldset.getCustomdesc();
					}
				}
				String[] orders = tagorder.split(",");
				infoxml.saveInfo_paramNode("order",orders,this.getFrameconn());
			}
		}else {
			for(int i=1;i<subclass.length+1;i++){
				if("b0110".equalsIgnoreCase(subclass[i-1])){
					mess += "单位名称,";
				}else if("e01a1".equalsIgnoreCase(subclass[i-1])){
					mess += "职位名称,";
				}else if("e0122".equalsIgnoreCase(subclass[i-1])){
					mess += "部门,";
				}else{
					FieldItem fielditem =DataDictionary.getFieldItem(subclass[i-1].toUpperCase());
					if(fielditem!=null)
						mess += fielditem.getItemdesc()+",";
				}
				if(i%5==0)
					mess += "<br>";
			}
		}
		
		// 刷新系统参数中的子集信息
		//SaveInfo_paramXml infoxmls = new SaveInfo_paramXml(this.frameconn);
		//infoxml.reOrederSet();
		
		mess = mess.substring(0,mess.length()-1);
		this.getFormHM().put("mess",mess);

	}
	/**
     * 检查排序的描述是否在分类中
     * @param fenlei_list
     * @param tagorderName
     * @return存在返回true；不存在返回false；
     */
	public boolean isFenLei(ArrayList fenlei_list,String tagorderName)
	{
		boolean isC=false;
		if(fenlei_list==null||fenlei_list.size()<=0)
			return isC;
		for(int i=0;i<fenlei_list.size();i++)
		{
			String desc=fenlei_list.get(i)!=null&&fenlei_list.get(i).toString().length()>0?(String)fenlei_list.get(i):"";
			if(desc.equalsIgnoreCase(tagorderName))
			{
				isC=true;
				break;
			}
		}
		return isC;
	}
}
