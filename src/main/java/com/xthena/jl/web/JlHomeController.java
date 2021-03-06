package com.xthena.jl.web;

import com.xthena.api.user.UserConnector;
import com.xthena.core.hibernate.PropertyFilter;
import com.xthena.core.mapper.BeanMapper;
import com.xthena.core.spring.MessageHelper;
import com.xthena.ext.export.Exportor;
import com.xthena.gcgl.domain.PjRy;
import com.xthena.gcgl.domain.PjXm;
import com.xthena.gcgl.manager.JlShizhongTempManager;
import com.xthena.gcgl.manager.PjRyManager;
import com.xthena.gcgl.manager.PjXmManager;
import com.xthena.hr.domain.CommRy;
import com.xthena.hr.manager.CommRyManager;
import com.xthena.jl.domain.Jlf;
import com.xthena.jl.domain.JlfRecord;
import com.xthena.jl.domain.PjXmImg;
import com.xthena.jl.manager.JlDeptManager;
import com.xthena.jl.manager.JlfManager;
import com.xthena.jl.manager.JlfRecordManager;
import com.xthena.jl.manager.PjXmImgManager;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("jl")
public class JlHomeController {

    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @Autowired
    private PjXmImgManager pjXmImgManager;

    @Autowired
    private JlDeptManager jlDeptManager;

    @Autowired
    private JlShizhongTempManager jlShizhongTempManager;

    @Autowired
    private PjXmManager pjXmManager;

    @Autowired
    private PjRyManager pjRyManager;

    @Autowired
    private CommRyManager commRyManager;

    @Autowired
    private JlfManager jlfManager;

    @Autowired
    private JlfRecordManager jlfRecordManager;

    @RequestMapping("jl-home")
    public String home(Model model, HttpServletRequest request, @RequestParam Map<String, Object> parameterMap) {
        long fxmid = jlDeptManager.getXmId(request);

        if (fxmid == -1) {
            /*  messageHelper.addFlashMessage(redirectAttributes, "request",
                        "您是管理权限，请先选择项目。");*/
            return "../common/jianlibutiaozhuanyemian";
        }

        String hql = "from PjXmImg img " +
                " where img.fxmid=" + fxmid +
                " order by img.fid desc";

        Query query = pjXmImgManager.getSession().createQuery(hql);
        query.setFirstResult(0);
        query.setMaxResults(5);
        List<PjXmImg> pjXmJds = query.list();
        model.addAttribute("xmImgs", pjXmJds);

        PjXm pjXm = pjXmManager.get(jlDeptManager.getXmId(request));
        String xmgaikuang = pjXm.getFxmgaikuang();
        model.addAttribute("xmgaikuang", xmgaikuang);


        PjXm pj = pjXmManager.get(jlDeptManager.getXmId(request));
        if (pj != null) {
            Jlf jlf1 = jlfManager.get(pjXm.getFid());
            if (jlf1 != null) {
                model.addAttribute("htke", jlf1.getFhtke());
            }
        }

        parameterMap.put("filter_EQL_fxmid", jlDeptManager.getXmId(request));
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        List<JlfRecord> jlfRecords = jlfRecordManager.find(propertyFilters);
        double jlfSum = 0;
        for (JlfRecord jlfRecord : jlfRecords) {
            jlfSum += Double.valueOf(jlfRecord.getFhtjlf());
        }

        model.addAttribute("jlf", jlfSum);

        parameterMap.put("filter_EQL_fxmid", jlDeptManager.getXmId(request));
        propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        List<PjRy> pjRies = pjRyManager.find(propertyFilters);
        List<String> zjList = new ArrayList<>();
        List<String> qtList = new ArrayList<>();
        for (PjRy pjRy : pjRies) {
            if (pjRy.getFryid() != null) {
                CommRy ry = commRyManager.get(pjRy.getFryid());
                if (ry != null && ry.getFname() != null && !ry.getFname().isEmpty()) {
                    if (pjRy.getFgangwei() != null && pjRy.getFgangwei().contains("总监")) {
                        zjList.add(ry.getFname());
                    } else {
                        qtList.add(ry.getFname());
                    }
                }
            }
        }

        model.addAttribute("zjList", zjList);
        model.addAttribute("qtList", qtList);

        return "jl/jl-home";
    }

    @RequestMapping("jldw-info-input")
    public String jldwhome(Model model) {
        return "jl/jldw-info-input";
    }

    @RequestMapping("zzryzj-info")
    public String zzryzj(Model model) {
        return "jl/zzryzj-info";
    }

    @RequestMapping("taizhang-info")
    public String taizhangInfo(Model model) {
        return "jl/taizhang-info";
    }

    @RequestMapping("pxjcjl-info")
    public String pxjcjlInfo(Model model) {
        return "jl/pxjcjl-info";
    }

    @RequestMapping("mbzrs-info")
    public String mbzrshome(Model model) {
        return "jl/mbzrs-info";
    }

    @RequestMapping("gsdbsy-info")
    public String gsdbsyhome(Model model) {
        return "jl/gsdbsy-info";
    }

    @RequestMapping("xmdbsy-info")
    public String xmdbsyhome(Model model) {
        return "jl/xmdbsy-info";
    }

    @RequestMapping("jlwzgl-info")
    public String jlwzglhome(Model model) {
        return "jl/jlwzgl-info";
    }

    @RequestMapping("qitaflfg-info")
    public String qitaflfghome(Model model) {
        return "jl/qitaflfg-info";
    }

    @RequestMapping("jlJd-info-input")
    public String jlJdhome(Model model) {
        return "jl/jlJd-info-input";
    }

    @RequestMapping("jlzz-info-input")
    public String jlzzhome(Model model) {
        return "jl/jlzz-info-input";
    }

    @RequestMapping("jl-sz-zlkz-input")
    public String zlkzhome(Model model) {
        return "jl/jl-sz-zlkz-input";
    }

    @RequestMapping("jl-sz-jdkz-input")
    public String jdkzhome(Model model) {
        return "jl/jl-sz-jdkz-input";
    }

    @RequestMapping("jl-sz-aqgl-input")
    public String aqglhome(Model model) {

        return "jl/jl-sz-aqgl-input";
    }

    @RequestMapping("jl-sz-aqgl-input-new")
    public String aqglhomenew(Model model) {
        model.addAttribute("aqList", jlShizhongTempManager.find("select jlShizhong from JlShizhongTemp jlShizhong where ftype=? order by forder", "1"));
        //model.addAttribute("aqList",jlShizhongTempManager.findBy("ftype","1"));
        return "jl/jl-sz-aqgl-input-new";
    }

    @RequestMapping("jl-sz-htgl-input")
    public String htglhome(Model model) {
        return "jl/jl-sz-htgl-input";
    }

    @RequestMapping("jl-sz-xxgl-input")
    public String xxglhome(Model model) {
        return "jl/jl-sz-xxgl-input";
    }

    @RequestMapping("jl-sz-tzkz-input")
    public String tzglhome(Model model) {
        return "jl/jl-sz-tzkz-input";
    }


    @RequestMapping("jl-sq-jdimg-info")
    public String sqjdhome(Model model) {
        return "jl/jl-sq-jdimg-info";
    }

    @RequestMapping("jl-sq-jlbimg-info")
    public String sqjlbhome(Model model) {
        return "jl/jl-sq-jlbimg-info";
    }


}
