package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;


import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title: OutProficientTrans </p>
 * <p>Description: 上会材料-单独生成账号密码 -获取信息</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-9-11 下午2:10:03</p>
 * @author liuyang
 * @version 1.0
 */
public class OutProficientTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
		
		String w0501 = (String)this.getFormHM().get("w0501");//申报人编号
		w0501 = PubFunc.decrypt(w0501);
		String w0301 = (String)this.getFormHM().get("w0301");//会议编号
		w0301 = PubFunc.decrypt(w0301);
		
		try {
			 ReviewFileBo reviewFileBo = new ReviewFileBo(this.getFrameconn(), this.userView);// 工具类
			
			/** 获取列头 */
			String columnfield = ",username:账号,password:密码,description:描述,w0107:姓名,w0105:部门,w0103:单位,state:状态,subflag:是否提交,w0501: ,w0301: ,user_id: ,";
			
			ArrayList<ColumnsInfo> columnList = reviewFileBo.getCheckProficientColumnList(columnfield);

			/** 获取查询语句 */
			String sql = reviewFileBo.getCheckProficientSql(columnfield, w0501, w0301);
			
			TableConfigBuilder builder = new TableConfigBuilder("reviewfile_outproficient_0001", columnList, "reviewfile_outproficient", userView, this.getFrameconn());
			builder.setDataSql(sql);
			builder.setAutoRender(false);
			builder.setSelectable(true);
			builder.setEditable(false);
			builder.setConstantName("jobtitle/reviewoutexpert");
			builder.setTableTools(reviewFileBo.getCheckProficientButtonList());
			builder.setPageSize(10);
			builder.setOrderBy("order by username");
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
			
			// 启用禁用状态
			ArrayList<HashMap> list = new ArrayList<HashMap>();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("dataValue", "1");
			map.put("dataName", "启用");
			list.add(map);
			HashMap<String, String> map1 = new HashMap<String, String>();
			map1.put("dataValue", "0");
			map1.put("dataName", "禁用");
			list.add(map1);
			this.getFormHM().put("outproficientstate", list);

			// 知否已评
			ArrayList<HashMap> list1 = new ArrayList<HashMap>();
			HashMap<String, String> map2 = new HashMap<String, String>();
			map2.put("dataValue", "3");
			map2.put("dataName", "是");
			list1.add(map2);
			HashMap<String, String> map3 = new HashMap<String, String>();
			map3.put("dataValue", "1");
			map3.put("dataName", "否");
			list1.add(map3);
			HashMap<String, String> map4 = new HashMap<String, String>();
			map4.put("dataValue", "2");
			map4.put("dataName", "否");
			list1.add(map4);
			HashMap<String, String> map5 = new HashMap<String, String>();
			map5.put("dataValue", "");
			map5.put("dataName", "否");
			list1.add(map5);
			this.getFormHM().put("subflag", list1);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
    
 


