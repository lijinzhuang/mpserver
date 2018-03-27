package com.self.wx.controller;

import com.alibaba.fastjson.JSON;
import com.self.wx.service.PassiveReplyServer;
import com.self.wx.service.WxProgramServer;
import com.self.wx.util.SignUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lijz on 2017/11/28.
 */
@RestController
@RequestMapping(value = "/wx")
public class CallBackController {
    @Autowired
    private PassiveReplyServer passiveReplyServer;
    @Autowired
    private WxProgramServer wxProgramServer;
    private static final String MSGTYPE = "MsgType";
    private static final String TEXT = "text";
    private static final String CONTENT = "Content";
    private static final String OPENID = "openid";
    private static final String TIMESTAMP = "timestamp";
    private static final String NONCE = "nonce";
    private static final String SIGNATURE = "signature";
    private static final String ECHOSTR = "echostr";
    private static final String TOUSERNAME = "ToUserName";
    private static final String MYTYPE_VALUE_SEARCH = "wxxcx-search-book";
    private static final String MYTYPE_VALUE_TYPE = "wxxcx-type-book";
    private static final String MYTYPE = "mytype";
    private static final String PARAMKEY = "paramkey";
    @RequestMapping(value = "/callback.do")
    public String callback(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(JSON.toJSON(request.getParameterNames()));
        String timestamp = request.getParameter(TIMESTAMP);
        String nonce = request.getParameter(NONCE);
        String signature = request.getParameter(SIGNATURE);
        String echostr = request.getParameter(ECHOSTR);
        if (!StringUtils.isEmpty(echostr)) {
            return echostrCheck(timestamp, nonce, signature, echostr);
        }
        String openid = request.getParameter(OPENID);
        if (!StringUtils.isEmpty(openid)) {
            return passiveReply(request);
        }
        String myType = request.getParameter(MYTYPE);
        if(!StringUtils.isEmpty(myType) && MYTYPE_VALUE_SEARCH.equals(myType)){
            String name = request.getParameter(PARAMKEY);
            return getBooksBySearch(name);
        }
        if(!StringUtils.isEmpty(myType) && MYTYPE_VALUE_TYPE.equals(myType)){
            String type = request.getParameter(PARAMKEY);
            return wxProgramServer.searchBookByTypeId(type);
        }
        return "Hello";
    }
    private String getBooksBySearch(String name){
        return wxProgramServer.searchBooksByName(name);
    }

    private String passiveReply(HttpServletRequest request) {
        SAXReader saxReader = null;
        Document doc = null;
        String msg = "";
        String toUserName = "";
        String openid = request.getParameter(OPENID);
        try {
            saxReader = new SAXReader();
            doc = saxReader.read(request.getInputStream());
            Element root = doc.getRootElement();
            // 文字类型的消息自动回复
            if (root.element(MSGTYPE) != null && TEXT.equals(root.element(MSGTYPE).getText())) {
                msg = root.element(CONTENT).getText();
                toUserName = root.element(TOUSERNAME).getText();
                return passiveReplyServer.passiveReply(openid, toUserName, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 配置回调地址时，token校验使用
     * 后续操作应该是不在调用这个方法
     *
     * @param timestamp
     * @param nonce
     * @param signature
     * @param echostr
     * @return
     */
    private String echostrCheck(String timestamp, String nonce, String signature, String echostr) {
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {
            return echostr;
        }
        return "false";
    }
}
