/*
 * Created on 2005-5-9
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.hjsj.hrms.businessobject.parse.parsebusiness;

import java.util.Vector;
/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CondAnalyse {
	//分解取得各个条件项；把分解的各项放入到Vector对象中
	public String[] mySplit(String src, String division) {
		try {
			Vector vFactors = new Vector();
			int beginIndex = 0;
			int endIndex = 0;
			if (src != null) {
				while (true) {
					endIndex = src.indexOf(division, beginIndex);
					if (endIndex == -1 || endIndex >= src.length()) {
						break;
					}
					vFactors.add(src.substring(beginIndex, endIndex));
					beginIndex = endIndex + 1;
				}
			}
			return (String[]) vFactors.toArray(new String[0]);
		} catch (Exception e) {
			System.out.println("分解各个因子出错!");
		}
		return null;
	}	
}
