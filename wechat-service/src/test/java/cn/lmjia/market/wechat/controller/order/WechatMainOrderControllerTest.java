package cn.lmjia.market.wechat.controller.order;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.SystemService;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.PaySuccessPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.concurrent.Callable;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author CJ
 */
public class WechatMainOrderControllerTest extends WechatTestBase {

    private static final Log log = LogFactory.getLog(WechatMainOrderControllerTest.class);

    @Test
    public void makeOrder() throws Exception {
        // 在微信端发起请求
        Login login1 = randomLogin(false);
        // 特别的设计，让这个帐号绑定到我个人微信openId 确保可以收到消息
        bindDeveloperWechat(login1);
        final Callable<Object> callable = () -> {
            mockMvc.perform(wechatGet(SystemService.wechatOrderURi))
//                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("wechat@orderPlace.html"));
            // 这波则是开干了
            //
            while (true) {
                try {

                    MockHttpServletRequestBuilder requestBuilder =
                            orderRequestBuilder(wechatPost("/wechatOrder"), randomOrderRequest());

                    String result = mockMvc.perform(
                            requestBuilder
                    )
                            .andExpect(status().is3xxRedirection())
                            .andReturn().getResponse().getHeader("Location");

                    // 使用 driver 打开!
                    driver.get("http://localhost" + result);
//                    mockMvc.perform(wechatGet("/paySuccess?mainOrderId=1"))
//                            .andDo(print());
                    PaySuccessPage page = PaySuccessPage.waitingForSuccess(this, driver, 3);
                    // 然后模拟订单完成支付
                    return null;
                } catch (AssertionError error) {
                    if (!error.getMessage().contains("200"))
                        throw error;
                    log.info("那就再试一次呗");
                    Thread.sleep(3000);
                }

            }
        };
        runWith(login1, callable);
        // 客户也可以下单
        final String customerMobile = randomMobile();
        newRandomOrderFor(login1, login1, customerMobile);
        runWith(loginService.byLoginName(customerMobile), callable);
    }

}