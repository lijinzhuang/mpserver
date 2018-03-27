package com.self.wx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.self.wx.model.WXCongfig;
import com.self.wx.util.HttpClientUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lijz on 2018/3/19.
 */
public class MpMenuServiceTest {
    private static final String WXAPI_MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create";
    private static final String GETTOKEN = "https://api.weixin.qq.com/cgi-bin/token";
    private static final String APPID = "wx7a3b17a05dbbee34";
    private static final String SECRET = "9cceeb524ba860d589c6f1bded659354";
    private WXCongfig wxCongfig;

    @Before
    public void prodBefore() throws Exception {
        Map<String, String> param = new HashMap<>();
        param.put("grant_type", "client_credential");
        param.put("appid", APPID);
        param.put("secret", SECRET);
        String res = HttpClientUtil.doGet(GETTOKEN, param, "utf-8");
        System.out.println(res);
        JSONObject object = JSON.parseObject(res);
        wxCongfig = new WXCongfig();
        wxCongfig.setAccessToken(object.getString("access_token"));
    }

    @Test
    public void createMenu() {
        String bodyStr = "{\"button\":[{\"type\":\"view\",\"name\":\"往期精彩\",\"url\":\"https://mp.weixin.qq.com/mp/profile_ext?action=home&__biz=MzU0OTczNTk1Nw==#wechat_redirect\"}]}";
        System.out.println(wxCongfig.getAccessToken());
        String res = HttpClientUtil.doPostJson(WXAPI_MENU_CREATE + "?access_token=" + wxCongfig.getAccessToken(),
                bodyStr, "utf-8");
        System.out.println(res);
    }
}
