package com.xthena.common.web;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.xthena.api.user.UserConnector;
import com.xthena.common.domain.XhfCommonDoc;
import com.xthena.common.manager.XhfCommonDocManager;
import com.xthena.core.mapper.BeanMapper;
import com.xthena.core.spring.MessageHelper;
import com.xthena.ext.export.Exportor;

@Controller
@RequestMapping("comm")
public class CommDocController {
   
	private XhfCommonDocManager xhfCommonDocManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

   @RequestMapping("comm-doc-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
        	XhfCommonDoc xhfCommonPlain = xhfCommonDocManager.get(id);
            model.addAttribute("model", xhfCommonPlain);
        }

        return "comm/comm-doc-info";
    }



    // ~ ======================================================================
    @Resource
    public void setxhfCommonDocManager(XhfCommonDocManager xhfCommonDocManager) {
        this.xhfCommonDocManager = xhfCommonDocManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
