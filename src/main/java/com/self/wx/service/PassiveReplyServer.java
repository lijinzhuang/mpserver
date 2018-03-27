package com.self.wx.service;

import com.self.wx.auto.dao.BooksDAO;
import com.self.wx.auto.model.BooksDO;
import com.tuan.core.common.lang.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by lijz on 2018/3/16.
 */
@Component
public class PassiveReplyServer {
    @Autowired
    private BooksDAO booksDAO;
    private static final String DEFAULTTYPE = "暂时不支持此业务";
    private static final String ISEMPTY = "暂时未找该图书";
    private static final String SBOOK = "搜书";
    private static final String CBOOK = "查书";
    private static final String ZBOOK = "找书";

    /**
     * 文字类型消息回复处理，暂时只支持电子书查询功能
     * @param openID
     * @param toUserName
     * @param msg
     * @return
     */
    public String passiveReply(String openID, String toUserName, String msg) {
        String res = DEFAULTTYPE;
        if (msg.startsWith(CBOOK) || msg.startsWith(SBOOK) || msg.startsWith(ZBOOK) || msg.startsWith("《")) {
            String name = msg.substring(2).trim().replace("《","").replace("》","");
            res = searchBook(name);

        }
        String textTpl = "<xml><ToUserName><![CDATA[" + openID + "]]></ToUserName>" +
                "<FromUserName><![CDATA[" + toUserName + "]]></FromUserName>" +
                "<CreateTime>" + TimeUtil.getNowTimestamp10Int() + "</CreateTime>" +
                "<MsgType><![CDATA[text]]></MsgType>" +
                "<Content><![CDATA[" + res + "]]></Content></xml>";
        return textTpl;
    }

    private String searchBook(String name) {
        List<BooksDO> list = booksDAO.selectByName("%" + name + "%");
        if(CollectionUtils.isEmpty(list)){
            return ISEMPTY;
        }
        StringBuffer bookStr = new StringBuffer();
        for (BooksDO book : list) {
            bookStr.append("书名：").append(book.getBookname());
            bookStr.append("\n").append("网盘：").append(book.getPan()).append("\n").append("******").append("\n");
        }
        return bookStr.toString();
    }
}
