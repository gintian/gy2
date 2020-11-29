package com.hjsj.hrms.module.gz.salarytype.transaction;

import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.templateset.DownLoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 项目名称 ：ehr7.x
 * 类名称：ImportZipTrans
 * 类描述：导入薪资类别
 * 创建人： lis
 * 创建时间：2015-11-28
 */
public class ImportZipTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			String gz_module=(String)this.getFormHM().get("gz_module");
			SalaryTypeBo bo=new SalaryTypeBo(this.getFrameconn(),this.getUserView());
			//salaryid`repeat`oldid, 
			String isRepeat = "";//0是追加，1是覆盖,23`1`23,24`1`24
			String name = "";
			String oldid = "";
			String id = "";
			StringBuffer deleteRepeatData = new StringBuffer();
			StringBuffer repeatNames = new StringBuffer("0");
			ArrayList<MorphDynaBean> salaryData=(ArrayList<MorphDynaBean>)this.getFormHM().get("salaryData");
			String[] salarySetIDs= new String[salaryData.size()];
			int i = 0;
			ArrayList list = new ArrayList();
			HashMap<String, String> nameMap = new HashMap<String, String>();
            int addNum=0;
			for(MorphDynaBean bean:salaryData){
				ArrayList<String> list2 = new ArrayList<String>();
				isRepeat = (String)bean.get("isrepeat");
				name = (String)bean.get("name");
				id = (String)bean.get("id");
				oldid = (String)bean.get("oldid");
				id  = PubFunc.decrypt(SafeCode.decode(id));
				oldid  = PubFunc.decrypt(SafeCode.decode(oldid));
				salarySetIDs[i] = id;
				i++;
				list2.add(name);
				list2.add(id);
				list.add(list2);
				if("0".equals(isRepeat)){//0追加则是新增，需要判断薪资类别名称是否重复
                    addNum++;
					if(bo.isHaveName(name, gz_module, "1", id)) {
                        repeatNames.append("，" + name);
                    }
				}else{
					if(bo.isHaveName(name, gz_module, "0", id)){
						repeatNames.append("，" + name);
					}
				}
				nameMap.put(id, name);
				deleteRepeatData.append(","+id+"`"+isRepeat+"`"+oldid);
			}

			bo.controlNumberOfSalaryTemplate(gz_module,addNum);//判断薪资账套是否达到上限
			if(!"0".equals(repeatNames.toString())){
				this.getFormHM().put("repeatNames", repeatNames.toString().substring(2));
				return;	
			}else{
				this.getFormHM().put("repeatNames", repeatNames.toString());
			}
			
			// 上传组件 vfs改造
            String fileid = (String)this.getFormHM().get("fileid");
            
		    bo.deleteRepeatRecord(deleteRepeatData.toString());
			HashMap map=getNewSalaryID(salarySetIDs,deleteRepeatData.toString());
			String[] arrids=(String[])map.get("1");//导入的类别
			HashMap hm=(HashMap)map.get("2");//覆盖导入，现类别号《====》原类别号
			DownLoadXml.nameMap = nameMap;
			//导入
			bo.importPkg(fileid, arrids, hm);
			
			DownLoadXml dowmloadxml = new DownLoadXml();
			StringBuffer error1 = DownLoadXml.getError1();
			StringBuffer error2 = DownLoadXml.getError2();
			StringBuffer error = new StringBuffer("");
			if(error1!=null && !"".equals(error1.toString())){
				error.append(ResourceFactory.getProperty("gz_new.gz_tempVarNameIsRepeat"));
				error.append("\r\n");
				error.append(error1);
				error.append("\r\n");
			}
			
			if(error2!=null && !"".equals(error2.toString())){
				error.append(ResourceFactory.getProperty("gz_new.gz_temVarIsRepeat"));
				error.append("\r\n");
				error.append(error2);
			}
			if(error!=null&&error.length()>0){
				String filename = PubFunc.getTxtFile(error.toString());
				filename = PubFunc.encrypt(filename);
				filename = SafeCode.encode(filename);
				this.getFormHM().put("filename", filename);
			}else{
				this.getFormHM().put("filename", "");//如果没有内容，把名字置为空，否则会导出上次的那个txt  zhaoxg add 2015-2-16
			}
			DownLoadXml.setError1(new StringBuffer(""));
			DownLoadXml.setError2(new StringBuffer(""));
		    bo.saveSalarySetResource(salarySetIDs,Integer.valueOf(gz_module));
		    bo.saveResourceOfOrg(gz_module, nameMap);//如果有应用机构保存应用机构对应的填报人的资源权限和业务范围
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	/**
	 * 
	 * @param list
	 * @param ids
	 * @return
	 */
	public HashMap getNewSalaryID(String[] ids,String isrepeat)
	{
		HashMap map = new HashMap();
		try
		{
			String[] ret= new String[ids.length];
			HashMap hm = new HashMap();
			//salaryid`repeat`oldid, salaryid`repeat`oldid
			String[] arr= isrepeat.substring(1).split(",");
			for(int i=0;i<ids.length;i++)//被选择的要导入的工资类别id
			{
				String[] temp=arr[i].split("`");
    			String salaryid = temp[0];
    			String repeatid=temp[1];
    			String oldid1="";
    			if(temp.length>2)
    				oldid1=temp[2];
    			if("1".equals(repeatid))//覆盖导入的类别
    			{
    				hm.put(salaryid, oldid1);
    			}   			
    			ret[i]=salaryid;
			}
			map.put("1",ret);
			map.put("2",hm);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

}
