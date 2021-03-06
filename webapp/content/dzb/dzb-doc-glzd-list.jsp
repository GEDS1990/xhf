<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%
	pageContext.setAttribute("currentHeader", "dzb-home");
%>
<%
	pageContext.setAttribute("currentMenu", "dzb-gd");
%>
<!doctype html>
<html lang="en">
<head>
<%@include file="/common/meta.jsp"%>
<title><spring:message code="dev.dzb-plain-info.list.title"
		text="列表" /></title>
<%@include file="/common/s.jsp"%>
<script type="text/javascript">
	var config = {
		id : 'dzb-doc-glzd-infoGrid',
		pageNo : ${page.pageNo},
		pageSize : ${page.pageSize},
		totalCount : ${page.totalCount},
		resultSize : ${page.resultSize},
		pageCount : ${page.pageCount},
		orderBy : '${page.orderBy == null ? "" : page.orderBy}',
		asc : ${page.asc},
		params : {
			'filter_LIKES_ftitle' : '${param.filter_LIKES_ftitle}',
		//'filter_EQS_fmodulecode': 'dzb'
		},
		selectedItemClass : 'selectedItem',
		gridFormId : 'dzb-doc-glzd-infoGridForm',
		exportUrl : 'dzb-doc-glzd-export.do'
	};

	var table;

	$(function() {
		table = new Table(config);
		table.configPagination('.m-pagination');
		table.configPageInfo('.m-page-info');
		table.configPageSize('.m-page-size');
	});
</script>
</head>
<body>
	<%@include file="/header.jsp"%>
	<div class="row-fluid">
			  <%@include file="/menu/dzb-workspace.jsp"%>
		<!-- start of main -->
		<section id="m-main" class="span10">
			<article class="m-blank">
				<div class="pull-left">
					<region:region-permission permission="dzb-plain-info:create">
						<button class="btn btn-small a-insert"
							onclick="location.href='dzb-doc-glzd-input.do'">新建</button>
					</region:region-permission>
					<region:region-permission permission="dzb-plain-info:delete">
						<button class="btn btn-small a-remove" onclick="table.removeAll()">删除</button>
					</region:region-permission>
					<button class="btn btn-small a-export"
						onclick="table.exportExcel()">导出</button>
				</div>
				<div class="pull-right">
					每页显示 <select class="m-page-size">
						<option value="10">10</option>
						<option value="20">20</option>
						<option value="50">50</option>
					</select> 条
				</div>
				<div class="m-clear"></div>
			</article>
			<article class="m-widget">
				<header class="header">
					<h4 class="title">
						<spring:message code="dzb-plain-info.dzb-plain-info.list.title"
							text="列表" />
					</h4>
				</header>
				<div class="content">
					<form id="dzb-doc-glzd-infoGridForm" name="dzb-doc-glzd-infoGridForm"
						method='post' action="dzb-doc-glzd-remove.do" class="m-form-blank">
						<table id="dzb-doc-glzd-infoGrid" class="m-table table-hover table-bordered">
							<thead>
								<tr>
									<%--  <th class="sorting" name="fcode"><spring:message code="dzb-plain-info.dzb-plain-info.list.name" text="序号"/></th> --%>
									<th width="10" class="m-table-check"><input
										type="checkbox" name="checkAll"
										onchange="toggleSelectedItems(this.checked)"></th>
									<th class="sorting" name="ftitle">制度名称</th>
									<th class="sorting" name="fstatus">状态</th>
									<!-- <th class="sorting" name="fstartdate">开始时间</th>
									<th class="sorting" name="fenddate">废止时间</th>
									<th class="sorting" name="fuserid">编制人</th> -->
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${page.result}" var="item">
									<tr
										ondblclick="window.location='dzb-doc-glzd-input.do?id=${item.fid}'">
										<td><input type="checkbox" class="selectedItem a-check"
											name="selectedItem" value="${item.fid}"></td>
										<%-- <td>${items.index}</td> --%>
										<td><a style="color:#005580;"
											href="../comm/comm-doc-input.do?id=${item.fid}"
											target="_blank">${item.ftitle}</a></td>
										<td><c:if test="${item.fstatus==1}">
												<font color="#0066CC">试行</font>
											</c:if> <c:if test="${item.fstatus==2}">实行</c:if> <c:if
												test="${item.fstatus==3}">
												<font color="#FF3333">废除</font>
											</c:if></td>
										<%-- <td>${item.fstartdate}</td>
										<td>${item.fenddate}</td>
										<td>${item.fcreator}</td> --%>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</form>
				</div>
			</article>
			<div class="m-spacer"></div>
		</section>
		<!-- end of main -->
	</div>
</body>
</html>
