package com.hjsj.hrms.transaction.sys.options.param;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class Taxis extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList fielditemlist = new ArrayList();
		SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.getFrameconn());
		ArrayList taglist = infoxml.getView_tag("set_a");
		if(taglist==null||taglist.size()==0){
			throw new GeneralException("", "请先设置子集分类!", "", "");
		}
		String tagorder =infoxml.getInfo_param("order");
		ArrayList sparetaglist = new ArrayList();
		if("".equalsIgnoreCase(tagorder)){
			fielditemlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
			ArrayList taglists = infoxml.getView_tag("set_a");
			ArrayList holdtaglist = new ArrayList();
	  	    for(int i=0;i<taglists.size();i++){
	  	    	String tagname = (String)taglists.get(i);
	  	    	String viewvalue = infoxml.getView_value("set_a",tagname);
	  	    	if(!"".equalsIgnoreCase(viewvalue)){
	  	    		String[] viewvalues = viewvalue.split(",");
	  	    		for(int j=0;j<viewvalues.length;j++){
	  	    			holdtaglist.add(viewvalues[j]);
	  	    		}
	  	    	}
	  	    }
			if(fielditemlist!=null){
		    	for(int j=0;j<fielditemlist.size();j++)
		    	{
		    		FieldSet fieldset=(FieldSet)fielditemlist.get(j);
			  	    /*if(this.userView.analyseTablePriv(fieldset.getFieldsetid()).equals("0"))
			  	    	continue;*/
			  	    if("B01".equalsIgnoreCase(fieldset.getFieldsetid())|| "B00".equalsIgnoreCase(fieldset.getFieldsetid()))
			  	    	continue;
			  	    String settag  = fieldset.getFieldsetid();
			  	    int x = 0;
			  	    for(int i=0;i<holdtaglist.size();i++){
			  	    	String oldtag = holdtaglist.get(i).toString();
			  	    	if(settag.equalsIgnoreCase(oldtag)){
			  	    		x =1;
			  	    	}
			  	    }
			  	    if(x==0)
		  	    		sparetaglist.add(fieldset.getCustomdesc());
		    	}
			}	
			for(int i=0;i<taglists.size();i++){
				
				sparetaglist.add(taglists.get(i));
			}
			
		}else{
			String[] tagorders = tagorder.split(",");			
			ArrayList set_A_list=infoxml.getSet_A_Name$Text();
			ArrayList fenlei_list=infoxml.getFenLeiDesc(set_A_list);
			for(int i=0;i<tagorders.length;i++){
				if(!isFenLei(fenlei_list,tagorders[i]))//如果不存在则放入排序列
				  sparetaglist.add(tagorders[i]);
			}
			fielditemlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
			String strs=fenlei_list.toString().replace("[", ",").replace("]", ",").replaceAll(", ", ",")+tagorder+",";
			strs = strs.toUpperCase();
			for(int i=0;i<fielditemlist.size();i++){
				FieldSet fs = (FieldSet)fielditemlist.get(i);
				if(strs.indexOf(","+fs.getCustomdesc().toUpperCase()+",")==-1)
					sparetaglist.add(fs.getCustomdesc());
			}
		}
		this.getFormHM().put("lawbasefilelist",sparetaglist);
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
