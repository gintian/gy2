package com.hjsj.hrms.module.jobtitle.configfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Title:SaveResultsArchivingConfigTrans </p>
 * <p>Description: 保存评审结果归档方案配置信息交易类</p>
 * <p>Company: hjsj</p> 
 * <p>create time: 2016-3-4</p>
 * @author liuy
 * @version 1.0
 */
@SuppressWarnings("serial")
public class SaveResultsArchivingConfigTrans extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		String msg = "";
		ContentDAO dao = new ContentDAO(this.frameconn);
		MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("configInfo");
		ReviewFileBo bo = new ReviewFileBo(this.getFrameconn(), this.userView);
		RowSet rs=null;
		try {
			String xmlValue = "<?xml version=\"1.0\" encoding=\"GB2312\"?>" +
			"  <params><templates></templates></params>";
			//常量表如果没有，则插入
			rs = dao.search("select Str_Value from Constant where constant='JOBTITLE_CONFIG'");
			if(!(rs.next())){//没有则插入空
				String sql="insert into Constant(Constant,Type,Describe,str_value)" +
						" values('JOBTITLE_CONFIG','A','职称评审配置参数','')";
				dao.insert(sql, new ArrayList());
			}else{
				String str_value = rs.getString("Str_Value");
				if(StringUtils.isNotEmpty(str_value))
					xmlValue = rs.getString("Str_Value");
			}
			//解析xml
	        Document doc = PubFunc.generateDom(xmlValue);
	        //取的根元素
            Element root = doc.getRootElement();
            root.removeChild("archiving");
            Element archivingEl = new Element("archiving");
            List<String> archivingItems = bo.getResultsArchivingList(JobtitleUtil.ZC_REVIEWARCHIVE_STR);
			for(String item:archivingItems){
					Object value = bean.get(item);
					if(value==null){
						value = "";
					}
					archivingEl.setAttribute(item, (String)value);
			}
			root.addContent(archivingEl);
			  //设置xml字体编码，然后输出为字符串
            Format format=Format.getRawFormat();
        	format.setEncoding("UTF-8");
            XMLOutputter output=new XMLOutputter(format);
        	String xml=output.outputString(doc);//最终处理后xml
			int row = dao.update("update constant  set Str_Value=? where Constant='JOBTITLE_CONFIG'", Arrays.asList(new String[] {xml}));
			//读取静态常量
			if(row==1){//是否成功
				RecordVo paramsVo=ConstantParamter.getConstantVo("JOBTITLE_CONFIG");
				if(paramsVo==null){
					paramsVo = new RecordVo("Constant");
					paramsVo.setString("constant", "JOBTITLE_CONFIG");
					paramsVo.setString("describe", "职称评审配置参数");
					paramsVo.setString("type", "A");
				}
				paramsVo.setString("str_value", xml);
				ConstantParamter.putConstantVo(paramsVo, "JOBTITLE_CONFIG");
				this.getFormHM().put("msg", "保存成功！");
		    }else {
		    	this.getFormHM().put("msg", "保存失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			if(rs!=null)
				PubFunc.closeResource(rs);
		}
	}
}
