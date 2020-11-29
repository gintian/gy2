package com.hjsj.hrms.transaction.sys.roleexcle;

import com.hjsj.hrms.businessobject.sys.rolemanagement.IndividualManagementBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:导出选中人员的权限</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 4, 2009:3:59:01 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class IndividualRoleExcelTrana extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HSSFWorkbook workbook = null;
		try
		{
			System.gc();
			String role_id = (String)this.getFormHM().get("role");
			String role = role_id.substring(0,role_id.length()-1);
			String [] roleid = role.split("/");
			IndividualManagementBo ind = new IndividualManagementBo(this.getFrameconn());
			if(roleid==null||roleid.length==0)
				  return;
			//top黄色部分
			ArrayList topinfolist = ind.toprole(roleid);
			ArrayList dbnamelist = ind.getdbname(); //人员库左边信息
			//功能权限
			ArrayList searchlist = ind.searchFunctionXmlHtml();
			//管理范围
			ArrayList listvalue = ind.uuu();
			//子集指标
			HashMap map=ind.infomap();
			ArrayList classlist = (ArrayList)map.get("1");
			HashMap fielsetidmap = (HashMap)map.get("2");
			HashMap itemdescmap = (HashMap)map.get("3");
			ArrayList sublist = (ArrayList)map.get("4");
			
			workbook= new HSSFWorkbook();   // 创建新的Excel 工作簿
			HSSFRow row = null;  //行
			HSSFCell cell=null;   //单元格
			HSSFComment comment=null;  //定义注释
			String outName="权限分配表_"+PubFunc.getStrg()+".xls";
			
			ind.setxmlexcel(searchlist,workbook,row,cell,topinfolist);  //写入功能权限
			ind.setdbname(topinfolist,dbnamelist,workbook,row,cell); //set人员库
			ind.setmanagementexcel(topinfolist,listvalue,workbook, row, cell);//写入管理范围
			ind.setinfoexcel(classlist, fielsetidmap, itemdescmap, workbook, row, cell,topinfolist,sublist);//写入子集指标
			HashMap maprole=ind.getRoleDetail(roleid);//	导出角色的人员列表
			ind.creatRoleDetailSheet(topinfolist,maprole, workbook, row, cell);//	导出角色的人员列表
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
			workbook.write(fileOut);
			fileOut.close();
			outName = PubFunc.encrypt(outName);
			this.getFormHM().put("outName",outName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(workbook);
		}
	}

}
