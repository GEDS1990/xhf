package com.xthena.gcgl.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.xthena.api.user.UserConnector;
import com.xthena.core.hibernate.PropertyFilter;
import com.xthena.core.mapper.BeanMapper;
import com.xthena.core.page.Page;
import com.xthena.core.spring.MessageHelper;
import com.xthena.ext.export.Exportor;
import com.xthena.ext.export.TableModel;
import com.xthena.security.util.SpringSecurityUtils;
import com.xthena.util.CommRyMapUtil;
import com.xthena.util.JsonResponseUtil;
import com.xthena.util.PjXmMapUtil;
import com.xthena.util.WzMapUtil;
import com.xthena.xz.domain.WzList;
import com.xthena.xz.manager.WzListManager;

import org.hibernate.persister.entity.PropertyMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("gcgl")
public class GcglWzListController {
	
	@Autowired
	private WzListManager wzListManager;
	
	private Exportor exportor;
	private BeanMapper beanMapper = new BeanMapper();
	private UserConnector userConnector;
	private MessageHelper messageHelper;

	@RequestMapping("wzList-info-list")
	public String list(@ModelAttribute Page page,
			@RequestParam Map<String, Object> parameterMap, Model model) {
		/* String ftype = parameterMap.get("ftype").toString(); */

	/*	// 固定资产
		Map<String, Object> gzMap = new HashMap<String, Object>();
		Page gzpage = new Page();
		gzpage = page;
		gzMap.putAll(parameterMap);
		gzMap.put("filter_LIKES_ftype", "1");
		List<PropertyFilter> gzpropertyFilters = PropertyFilter
				.buildFromMap(gzMap);
		gzpage = wzListManager.pagedQuery(gzpage, gzpropertyFilters);

		model.addAttribute("pageGZ", gzpage);
*/
		// 低值易耗品
		Map<String, Object> dzMap = new HashMap<String, Object>();
		Page dzpage = new Page();
		dzpage = page;
		dzMap.putAll(parameterMap);
		dzMap.put("filter_LIKES_ftype", "2");
		List<PropertyFilter> dzpropertyFilters = PropertyFilter
				.buildFromMap(dzMap);
		dzpage = wzListManager.pagedQuery(dzpage, dzpropertyFilters);
		model.addAttribute("pageDZ", dzpage);

		// 消耗品
		Map<String, Object> xhMap = new HashMap<String, Object>();
		Page xhpage = new Page();
		xhpage = page;
		xhMap.putAll(parameterMap);
		xhMap.put("filter_LIKES_ftype", "3");
		List<PropertyFilter> propertyFilters = PropertyFilter
				.buildFromMap(xhMap);
		xhpage = wzListManager.pagedQuery(xhpage, propertyFilters);
		model.addAttribute("pageXH", xhpage);
		model.addAttribute("xmMap", PjXmMapUtil.getXmMap());
		model.addAttribute("ryMap", CommRyMapUtil.getRyMap());
		return "gcgl/wzList-info-list";
	}

	//仪器
	@RequestMapping("wzList-yq-info-list")
	public String gzlist(@ModelAttribute Page page,
			@RequestParam Map<String, Object> parameterMap, Model model) {


		// 根据项目名称查询
		if (parameterMap.get("filter_LIKES_fprojectname") != null && parameterMap.get("filter_LIKES_fprojectname") !=
		 "") {

		StringBuffer hql = new StringBuffer(
				"select wz from PjXm xm,WzList wz where xm.fid=wz.fxmid");

			hql.append(" and xm.fxmname like '%"
					+ parameterMap.get("filter_LIKES_fprojectname") + "%' ");
			parameterMap.put("filter_LIKES_ftype", "4");
			parameterMap.remove("filter_LIKES_fprojectname");
		List<PropertyFilter> propertyFilters = PropertyFilter
				.buildFromMap(parameterMap);
		page = wzListManager.pagedQuery(hql.toString(), page, propertyFilters);
	}else {
		parameterMap.put("filter_LIKES_ftype", "4");
		parameterMap.remove("filter_LIKES_fprojectname");
		List<PropertyFilter> propertyFilters = PropertyFilter
				.buildFromMap(parameterMap);
		page = wzListManager.pagedQuery(page, propertyFilters);
	}


	model.addAttribute("page", page);

		model.addAttribute("xmMap", PjXmMapUtil.getXmMap());
		model.addAttribute("ryMap", CommRyMapUtil.getRyMap());
		return "gcgl/wzList-yq-info-list";
	}

	@RequestMapping("wzList-simple-list")
	public void listWz(@ModelAttribute Page page,
			@RequestParam Map<String, Object> parameterMap,
			HttpServletResponse response) {
		parameterMap.put("filter_LIKES_ftype", "1");
		List<PropertyFilter> propertyFilters = PropertyFilter
				.buildFromMap(parameterMap);
		page = wzListManager.pagedQuery(page, propertyFilters);
		JsonResponseUtil.write(response, page);
	}

	@PostConstruct
	public void intRmMap() {
		WzMapUtil.initWzMap(wzListManager);
	}

	@RequestMapping("wzList-info-input")
	public String input(@RequestParam(value = "id", required = false) Long id,
			Model model) {
		if (id != null) {
			WzList wzList = wzListManager.get(id);
			model.addAttribute("model", wzList);
		}

		model.addAttribute("xmMap", PjXmMapUtil.getXmMap());
		model.addAttribute("ryMap", CommRyMapUtil.getRyMap());
		return "gcgl/wzList-info-input";
	}

	@RequestMapping("wzList-dz-info-input")
	public String dzinput(
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "ftype", required = false) String ftype,
			Model model) {
		if (id != null) {
			WzList wzList = wzListManager.get(id);
			model.addAttribute("ftype", wzList.getFtype());
			model.addAttribute("model", wzList);
		} else {
			model.addAttribute("ftype", ftype);
		}
		return "gcgl/wzList-dz-info-input";
	}

	@RequestMapping("wzList-info-save")
	public String save(@ModelAttribute WzList wzList,
			@RequestParam Map<String, Object> parameterMap,
			RedirectAttributes redirectAttributes) {
		WzList dest = null;

		Long id = wzList.getFid();

		if (id != null) {
			dest = wzListManager.get(id);
			beanMapper.copy(wzList, dest);
		} else {
			dest = wzList;
		}

		wzListManager.save(dest);
		WzMapUtil.refreshWzMap(dest);
		messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
				"保存成功");
		return "redirect:/gcgl/wzList-yq-info-list.do";
	}

	@RequestMapping("wzList-info-remove")
	public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
			RedirectAttributes redirectAttributes) {
		List<WzList> wzLists = wzListManager.findByIds(selectedItem);

		wzListManager.removeAll(wzLists);
		WzMapUtil.deleteWzMap(wzLists);
		messageHelper.addFlashMessage(redirectAttributes,
				"core.success.delete", "删除成功");

		return "redirect:/gcgl/wzList-info-list.do";
	}

	@RequestMapping("wzList-info-remove-yq")
	public String removeYq(@RequestParam("selectedItem") List<Long> selectedItem,
						   RedirectAttributes redirectAttributes) {
		List<WzList> wzLists = wzListManager.findByIds(selectedItem);

		wzListManager.removeAll(wzLists);
		WzMapUtil.deleteWzMap(wzLists);
		messageHelper.addFlashMessage(redirectAttributes,
				"core.success.delete", "删除成功");

		return "redirect:/gcgl/wzList-yq-info-list.do";
	}

	@RequestMapping("wzList-info-export")
	public void export(@ModelAttribute Page page,
			@RequestParam Map<String, Object> parameterMap,
			HttpServletResponse response) throws Exception {
		List<PropertyFilter> propertyFilters = PropertyFilter
				.buildFromMap(parameterMap);
		page = wzListManager.pagedQuery(page, propertyFilters);

		List<WzList> wzLists = (List<WzList>) page.getResult();

		TableModel tableModel = new TableModel();
		// tableModel.setName("wzList info");
		// tableModel.addHeaders("id", "name");
		tableModel.setData(wzLists);
		exportor.export(response, tableModel);
	}

	// ~ ======================================================================
	@Resource
	public void setWzListManager(WzListManager wzListManager) {
		this.wzListManager = wzListManager;
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
