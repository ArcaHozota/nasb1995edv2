const responseSuccess = 'SUCCESS';
const responseFailure = 'FAILURE';
const emptyString = '';
const inputWarning = '入力情報不正';
const inputedString = '追加済み';
const delimiter = '　/　';
const delayApology = 'すみませんが、当機能はまだ実装されていません';
const showVadMsgError = '名称を空になってはいけません。';
$(document).ready(() => {
	let treeData = [
		{
			text: "聖書奉読",
			icon: "fa-solid fa-book-bible",
			expanded: true,
			nodes: [
				{
					id: "toBookSearch",
					text: "章節選択",
					icon: "fa-solid fa-anchor"
				},
				{
					id: "toTemporary",
					text: "章節入力",
					icon: "fa-solid fa-box-archive"
				}
			]
		},
		{
			text: "賛美歌集め",
			icon: "fa-solid fa-music",
			expanded: true,
			nodes: [
				{
					id: "toCollection",
					text: "コレクション一覧",
					icon: "fa-solid fa-rss"
				},
				{
					id: "toRandomFive",
					text: "ランダム五つ",
					icon: "fa-regular fa-copyright"
				}
			]
		}
	];
	$('#mainmenuTree').bstreeview({
		data: treeData,
		expandIcon: 'fa fa-angle-down fa-fw',
		collapseIcon: 'fa fa-angle-right fa-fw',
		indent: 1.5,
		parentsMarginLeft: '1.25rem',
		openNodeLinkOnNewTab: true
	});
	$("#logoutBtn").on("click", () => {
		swal.fire({
			title: '警告',
			text: 'ログアウトしてよろしいでしょうか。',
			icon: 'warning',
			showDenyButton: true,
			denyButtonText: 'いいえ',
			confirmButtonText: 'はい',
			confirmButtonColor: '#7F0020',
			denyButtonColor: '#002FA7'
		}).then((result) => {
			if (result.isConfirmed) {
				$("#logoutForm").submit();
			} else if (result.isDenied) {
				$(this).close();
			}
		});
	});
	$("#toMainmenu").on("click", (e) => {
		e.preventDefault();
		window.location.replace('/category/to-mainmenu');
	});
	$("#toMainmenu2").on("click", (e) => {
		e.preventDefault();
		window.location.replace('/category/to-mainmenu');
	});
	$("#toPersonal").on("click", (e) => {
		e.preventDefault();
		let userId = $(this).find("input").val().replace(/,/g, emptyString);
		let url = '/students/to-edition?editId=' + userId;
		checkPermissionAndTransfer(url);
	});
	$("#toMessage").on("click", (e) => {
		e.preventDefault();
		layer.msg(delayApology);
	});
	$("#toBookSearch").on("click", (e) => {
		e.preventDefault();
		layer.msg(delayApology);
		// let url = '/books/to-pages?pageNum=1';
		// checkPermissionAndTransfer(url);
	});
	$("#toTemporary").on("click", (e) => {
		let url = '/books/to-addition';
		checkPermissionAndTransfer(url);
	});
	$("#toCollection").on("click", (e) => {
		e.preventDefault();
		let url = '/hymns/to-pages?pageNum=1';
		checkPermissionAndTransfer(url);
	});
	$("#toRandomFive").on("click", (e) => {
		e.preventDefault();
		let url = '/hymns/to-random-five';
		checkPermissionAndTransfer(url);
	});
});

function checkPermissionAndTransfer(stringUrl) {
	let ajaxResponse = $.ajax({
		url: stringUrl,
		type: 'GET',
		async: false
	});
	if (ajaxResponse.status === 200) {
		window.location.replace(stringUrl);
	} else {
		layer.msg(ajaxResponse.message);
	}
}

function buildPageInfos(response) {
	let pageInfos = $("#pageInfos");
	pageInfos.empty();
	pageNum = response.pageNum;
	totalPages = response.totalPages;
	totalRecords = response.totalRecords;
	pageInfos.append(totalPages + "ページ中の" + pageNum + "ページ、" + totalRecords + "件のレコードが見つかりました。");
}

function buildPageNavi(result) {
	$("#pageNavi").empty();
	let ul = $("<ul></ul>").addClass('pagination');
	let firstPageLi = $("<li class='page-item'></li>").append(
		$("<a class='page-link'></a>").append("最初へ").attr("href", "#"));
	let prevPageLi = $("<li class='page-item'></li>").append(
		$("<a class='page-link'></a>").append("&laquo;").attr("href", "#"));
	if (!result.hasPrevPage) {
		firstPageLi.addClass('disabled');
		prevPageLi.addClass('disabled');
	} else {
		firstPageLi.click(() => {
			toSelectedPg(1, keyword);
		});
		prevPageLi.click(() => {
			toSelectedPg(pageNum - 1, keyword);
		});
	}
	let nextPageLi = $("<li class='page-item'></li>").append(
		$("<a class='page-link'></a>").append("&raquo;").attr("href", "#"));
	let lastPageLi = $("<li class='page-item'></li>").append(
		$("<a class='page-link'></a>").append("最後へ").attr("href", "#"));
	if (!result.hasNextPage) {
		nextPageLi.addClass('disabled');
		lastPageLi.addClass('disabled');
	} else {
		lastPageLi.addClass('success');
		nextPageLi.click(() => {
			toSelectedPg(pageNum + 1, keyword);
		});
		lastPageLi.click(() => {
			toSelectedPg(totalPages, keyword);
		});
	}
	ul.append(firstPageLi).append(prevPageLi);
	$.each(result.navigateNums, (index, item) => {
		let numsLi = $("<li class='page-item'></li>").append(
			$("<a class='page-link'></a>").append(item).attr("href", "#"));
		if (pageNum === item) {
			numsLi.attr("href", "#").addClass("active");
		}
		numsLi.click(() => {
			toSelectedPg(item, keyword);
		});
		ul.append(numsLi);
	});
	ul.append(nextPageLi).append(lastPageLi);
	$("<nav></nav>").append(ul).appendTo("#pageNavi");
}

function formReset(element) {
	$(element)[0].reset();
	$(element).find(".form-control").removeClass('is-valid is-invalid');
	$(element).find(".form-select").removeClass('is-valid is-invalid');
	$(element).find(".form-text").removeClass('valid-feedback invalid-feedback');
	$(element).find(".form-text").text(emptyString);
}

function showValidationMsg(element, status, msg) {
	$(element).removeClass('is-valid is-invalid');
	$(element).next("span").removeClass('valid-feedback invalid-feedback');
	$(element).next("span").text(emptyString);
	if (status === responseSuccess) {
		$(element).addClass('is-valid');
		$(element).next("span").addClass('valid-feedback');
	} else {
		$(element).addClass('is-invalid');
		$(element).next("span").addClass('invalid-feedback').text(msg);
	}
}

function projectAjaxModify(url, type, data, successFunction) {
	let header = $("meta[name=_csrf_header]").attr("content");
	let token = $("meta[name=_csrf_token]").attr("content");
	$.ajax({
		url: url,
		type: type,
		data: data,
		headers: {
			[header]: token
		},
		dataType: 'json',
		contentType: 'application/json;charset=UTF-8',
		success: successFunction,
		error: (xhr) => {
			let message = xhr.responseText.replace(/^"|"$/g, emptyString);
			layer.msg(message);
		}
	});
}

function projectNullInputboxDiscern(inputArrays) {
	for (const element of inputArrays) {
		if ($(element).val().trim() === emptyString) {
			showValidationMsg(element, responseFailure, '上記の入力ボックスを空になってはいけません。');
		}
	}
}

function projectInputContextGet(inputArrays) {
	let listArray = [];
	for (const element of inputArrays) {
		let inputContext = $(element).val().trim();
		if (!$(element).hasClass('is-invalid')) {
			listArray.push(inputContext);
			showValidationMsg(element, responseSuccess, emptyString);
		}
	}
	return listArray;
}

function normalDeleteSuccessFunction(result) {
	if (result.status === responseSuccess) {
		layer.msg(result.message);
		toSelectedPg(pageNum, keyword);
	} else {
		layer.msg(result.message);
	}
}

function normalAddbtnFunction(checkUrl, modalName) {
	let ajaxResult = $.ajax({
		url: checkUrl,
		type: 'GET',
		async: false
	});
	if (ajaxResult.status !== 200) {
		layer.msg(ajaxResult.responseJSON.message);
		return;
	}
	let modalForm = $(modalName).find("form");
	formReset(modalForm);
	$(modalName).modal({
		backdrop: 'static'
	});
}

function normalDeletebtnFunction(url, message, deleteId) {
	$.ajax({
		url: url + 'deletion-check',
		type: 'GET',
		success: () => {
			swal.fire({
				title: 'メッセージ',
				text: message,
				icon: 'question',
				showCloseButton: true,
				confirmButtonText: 'はい',
				confirmButtonColor: '#7F0020'
			}).then((result) => {
				if (result.isConfirmed) {
					projectAjaxModify(url + 'info-delete?id=' + deleteId, 'DELETE', null, normalDeleteSuccessFunction);
				} else {
					$(this).close();
				}
			});
		},
		error: (response) => {
			layer.msg(response.responseJSON.message);
		}
	});
}