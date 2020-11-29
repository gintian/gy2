package com.hjsj.hrms.module.template.templatetoolbar.importmen.transaction;

import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
/**
 * 
* <p>Title: TemplateImprotTrans</p>
* <p>Description: 手工选择</p>
* <p>Company: HJSOFT</p> 
* @author gaohy
* @date 2015-12-7 下午05:28:10
 */
public class TemplateImprotmenTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		
		String tab_id = (String)this.getFormHM().get("tab_id");
		tab_id=tab_id!=null&&tab_id.trim().length()>0?tab_id:"";

		
		String infor_type = (String)this.getFormHM().get("infor_type");
		infor_type = infor_type!=null&&infor_type.trim().length()>0?infor_type:"";

		String ids = (String)this.getFormHM().get("ids");
		try{
			if(StringUtils.isBlank(ids)){
				String from_module = (String)this.getFormHM().get("from_module");
				if(from_module!=null&& "yj".equals(from_module)){ //来自预警
					ArrayList objlist=(ArrayList)this.getFormHM().get("objlist");//如：[Usr00001402, ]
					if(objlist==null||objlist.size()==0)
						return;
					else{
						ids="";
						for(int i=0;i<objlist.size();i++){
							String obj_id=(String)objlist.get(i);
							if(obj_id==null|| "".equals(obj_id))
								continue;
							if("selectall".equalsIgnoreCase(obj_id))
								continue;
							ids+="'"+SafeCode.encode(PubFunc.encrypt(obj_id));
						}
						if(ids.length()>0)
							ids=ids.substring(1);
						
					}
				}
				else
					return;
			}
			
			String[] idsArray = ids.split("'");
			Boolean flag = true;//所有数据导入成功
	        TemplateBo templateBo=new TemplateBo(this.getFrameconn(),this.userView,Integer.parseInt(tab_id));
			if("1".equals(infor_type)){//人事
				HashSet set=new HashSet();//存放所有人员库前缀
				for(String userid:idsArray){
					userid = PubFunc.decrypt(SafeCode.decode(userid));
					String nbase =userid.substring(0, 3);//人员库前缀
					set.add(nbase);
				}
				//导入数据
				for (Iterator i = set.iterator();i.hasNext();) {//遍历人员库前缀，按人员库前缀分类导入数据
					String dbName=(String) i.next();
					if(dbName==null||dbName.length()==0)
						continue;
					ArrayList a0100s = new ArrayList();//人员编号
					for (String userid:idsArray) {//遍历,按人员库前缀分类存放
						userid = PubFunc.decrypt(SafeCode.decode(userid));
						String nbase=userid.substring(0,3);//人员库前缀，用于比较，按人员库前缀类存放a0100
						String a0100=userid.substring(3);//人员编号
						if(nbase.equalsIgnoreCase(dbName)){
							a0100s.add(a0100);
						}
					}
					if(a0100s.size()>0){
						Boolean resultflag=templateBo.impDataFromArchive(a0100s, dbName);//按人员库前缀导入数据
						if(!resultflag)
							flag=false;//导入失败
					}
				}
			}else {//单位、部门
				ArrayList a0100s = new ArrayList();//人员编号
				for (String a0100:idsArray) {
					a0100s.add(PubFunc.decrypt(SafeCode.decode(a0100)));
				}
				if("2".equals(infor_type)){
					Boolean resultflag=templateBo.impDataFromArchive(a0100s, "B");//按人员库前缀导入数据
					if(!resultflag)
						flag=false;//导入失败
				}else if("3".equals(infor_type)){
					Boolean resultflag=templateBo.impDataFromArchive(a0100s, "K");//按人员库前缀导入数据
					if(!resultflag)
						flag=false;//导入失败
				}
			}
			this.getFormHM().put("flag", flag);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
