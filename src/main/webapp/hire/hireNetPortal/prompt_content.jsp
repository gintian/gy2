<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<LINK href="/css/hireNetStyle.css" type=text/css rel=stylesheet>
<script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
<html:form action="/hire/hireNetPortal/search_zp_position"> 
<br>
<table align="center" width='80%' border='0' cellSpacing=0 cellPadding=0>
<tr>
<td align="center">
  <font size="4"><strong>
    提示信息
  </strong>
  </font>
</td>
</tr>
<tr>
              <td align="center"><div class="zphr1">
                <hr class="viewhr" />
              </div></td>
            </tr>
<tr>
<td >
${employPortalForm.licenseAgreement}
</td>
</tr>
<tr>
              <td align="center"><div class="zphr1">
                <hr class="viewhr" />
              </div></td>
            </tr>
<tr>
<td style="TEXT-ALIGN:center">
 <input type="button" name="zc" value="确定"  class="hj_xkrz_but" onclick="enter();"/>&nbsp;
 <input type="button" name="qx" value="取消"  class="hj_xkrz_but" class="s_btn_big" onclick="qxl();"/>
</td>
</tr>
</table>
</html:form>