package com.hjsj.hrms.utils.components.vfs.servlet;

import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.hjadmin.api.ResponseFactory;
import com.hrms.hjsj.hjadmin.api.RetResult;
import com.hrms.hjsj.hjadmin.cache.CacheUtil;
import com.hrms.hjsj.hjadmin.cache.FrameworkCacheKeysEnum;
import com.hrms.hjsj.hjadmin.util.FrameWorkConstant;
import com.hrms.hjsj.hjadmin.util.JwtUtil;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * vfs 文件删除servlet
 * @author ZhangHua
 * @date 18:05 2020/6/15
 */
public class VfsRemoveFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HashMap json=new HashMap();
        try {
            UserView userView = (UserView) request.getSession().getAttribute(WebConstant.userView);
            if (userView == null) {
                String authorization = request.getHeader(JwtUtil.DEFAULT_JWT_PARAM);

                userView = (UserView) CacheUtil.get(FrameworkCacheKeysEnum.userViewCache, JwtUtil.parseJWT(authorization, JwtUtil.TOKENTYPE_ACCESS_TOKEN).getSubject());
            }
            if (userView == null) {
                throw new Exception("Identity authentication failed！");
            }
            String xml = request.getParameter(FrameWorkConstant.APIDATAKEY);
            xml = SafeCode.keyWord_reback(xml);
            JSONObject data = JSONObject.fromObject(xml);
            String fileid = (String) data.get("fileId");
            boolean bool=VfsService.deleteFile(userView.getUserName(),fileid);
            json.put("successed", bool);
            json.put("msg", "");

        }catch (Exception e){
            e.printStackTrace();
            json.put("successed", false);
            json.put("msg", e.getMessage());
        }
        ResponseFactory.buildResponseSuccess(response, new RetResult(json));
    }
}
