package com.hjsj.hrms.transaction.sys.roleexcle;

import com.hjsj.hrms.businessobject.sys.rolemanagement.GuidePurviewExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.upload.FormFile;

import java.io.InputStream;
import java.util.HashMap;
/***
 * 
 * <p>Title:</p>
 * <p>Description:导入权限分配excel</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 22, 2008:3:17:02 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class UploadExcelTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		InputStream is =null;
		try{
			
			GuidePurviewExcelBo guide = new GuidePurviewExcelBo(this.getFrameconn(),this.userView);
			FormFile file = (FormFile)this.getFormHM().get("importfile");
			int info = 0;  //用来判断输出信息
			String retInfo=""; //输出内容
			if(file==null||file.getFileData().length==0)
			{
				info=2;
				this.getFormHM().put("info",String.valueOf(info));
				this.getFormHM().put("retInfo","您选择的文件是个空文件");
				return;
			}
			is = file.getInputStream();
			HSSFWorkbook workbook = new HSSFWorkbook(is);
			int num=workbook.getNumberOfSheets(); //获得sheet的个数
			if(num!=5){
				info=3;
				retInfo="由于您选择的文件不是用户授权表,导入失败";
				this.getFormHM().put("info",String.valueOf(info));
				this.getFormHM().put("returnInfo",retInfo);
				return;
			}
			//获得四个sheet内容
			HSSFSheet sheet1=workbook.getSheetAt(0);  //功能授权
			HSSFSheet sheet2=workbook.getSheetAt(1);  //人员库
			HSSFSheet sheet3=workbook.getSheetAt(2);  //管理范围
			HSSFSheet sheet4=workbook.getSheetAt(3);  //子集指标授权
			if(sheet1==null||sheet2==null||sheet3==null||sheet4==null)
			{
				info=3;
				retInfo="由于您选择的文件不是用户授权表,导入失败";
				this.getFormHM().put("info",String.valueOf(info));
				this.getFormHM().put("returnInfo",retInfo);
				return;
			}
			//获得4个sheet表名字
			String sheet1name=workbook.getSheetName(0);
			String sheet2name=workbook.getSheetName(1);
			String sheet3name=workbook.getSheetName(2);
			String sheet4name=workbook.getSheetName(3);
			if(!sheet1name.equalsIgnoreCase(ResourceFactory.getProperty("kjg.title.functionshouquan"))){  //功能授权
				info=1;
				retInfo=ResourceFactory.getProperty("kjg.title.functionshouquan");
				this.getFormHM().put("info",String.valueOf(info));
				this.getFormHM().put("returnInfo","EXCEL中没找到:"+retInfo);
				return;
			}
			if(!sheet2name.equalsIgnoreCase(ResourceFactory.getProperty("label.dbase"))){   //人员库
				info=1;
				retInfo=ResourceFactory.getProperty("label.dbase");
				this.getFormHM().put("info",String.valueOf(info));
				this.getFormHM().put("returnInfo","EXCEL中没找到:"+retInfo);
				return;
			}
			if(!sheet3name.equalsIgnoreCase(ResourceFactory.getProperty("menu.manage"))){ //menu.manage
				info=1;
				retInfo=ResourceFactory.getProperty("menu.manage");
				this.getFormHM().put("info",String.valueOf(info));
				this.getFormHM().put("returnInfo","EXCEL中没找到:"+retInfo);
				return;
			}
			if(!sheet4name.equalsIgnoreCase(ResourceFactory.getProperty("kjg.title.zjshouquan"))){  //子集指标授权
				info=1;
				retInfo=ResourceFactory.getProperty("kjg.title.zjshouquan");
				this.getFormHM().put("info",String.valueOf(info));
				this.getFormHM().put("returnInfo","EXCEL中没找到:"+retInfo);
				return;
			}
			HashMap usemap = guide.getusepurview(workbook,sheet2);//得到人员指标
			guide.setvalue();//写入人员指标
			guide.getcaretaker(workbook, sheet3); //得到管理范围授权
			guide.setcaretaker(); //写入管理范围
			guide.getsubset(workbook, sheet4); //得到子集指标授权
			guide.setsubset(); //写入子集指标授权
			guide.setindex();//写入指标授权
			guide.getfunction(workbook, sheet1); //得到功能授权
			guide.setfunction(); //写入功能授权
			this.getFormHM().put("info",String.valueOf(info));
			this.getFormHM().put("returnInfo",retInfo);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(is);
		}
	}

}
