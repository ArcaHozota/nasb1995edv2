$(document).ready(function() {
	adjustWidth();
	initialPagination(1, null);
	$("#saraniSearchBtn").on("mousemove", function(e) {
		let x = e.pageX - $(this).offset().left;
		let y = e.pageY - $(this).offset().top;
		$(this).css("--x", x + "px");
		$(this).css("--y", y + "px");
	});
});
$("#saraniSearchBtn").on("click", function(e) {
	e.preventDefault();
	let hymnId = $("#nameDisplay").attr('idVal');
	if (hymnId === "0" || hymnId === 0 || hymnId === null || hymnId === undefined) {
		layer.msg('賛美歌を選択してください');
	} else {
		swal.fire({
			title: "HINT",
			text: "選択された曲に基づくセリフが似てる三つの曲を検索します。検索が約1分間ぐらいかかりますので行ってよろしいでしょうか。",
			footer: '<p style="font-size: 13px;">※この画面及び検索は金海嶺氏のアイディアによって作成されたものです。</p>',
			icon: "info",
			showDenyButton: true,
			denyButtonText: 'いいえ',
			confirmButtonText: 'はい',
			confirmButtonColor: '#7F0020',
			denyButtonColor: '#006400'
		}).then((result) => {
			if (result.isConfirmed) {
				adjustWidth();
				$("#loadingBackground2").show();
				$("#saraniSearchBtn").css("pointer-events", "none");
				kanumiRetrieve(hymnId);
				setTimeout(function() {
					$("#loadingBackground2").hide();
					$("#saraniSearchBtn").css("pointer-events", "auto");
					let nameJp = $('.table-danger').find("td:eq(1)").children("a").text();
					let slashIndex = nameJp.indexOf('/');
					$("#nameDisplay").text('検索完了---' + nameJp.substring(0, slashIndex));
					$("#nameDisplay").attr('idVal', 0);
				}, 33000);
			} else if (result.isDenied) {
				$(this).close();
			}
		});
	}
});
$("#tableBody").on("click", '.form-check-input', function() {
	if ($(this).prop('checked')) {
		let idVal = $(this).val();
		$.ajax({
			url: '/hymns/getInfoById',
			data: 'hymnId=' + idVal,
			success: function(response) {
				$("#nameDisplay").text(response.nameJp);
				$("#nameDisplay").attr('idVal', response.id);
			},
			error: function(xhr) {
				let message = xhr.responseText.replace(/^"|"$/g, emptyString);
				layer.msg(message);
			}
		});
	} else {
		let checkBoxArray = $("#tableBody").find('.form-check-input');
		for (const element of checkBoxArray) {
			if ($(element).prop('checked')) {
				$.ajax({
					url: '/hymns/getInfoById',
					data: 'hymnId=' + $(element).val(),
					success: function(response) {
						$("#nameDisplay").text(response.nameJp);
						$("#nameDisplay").attr('idVal', response.id);
					},
					error: function(xhr) {
						let message = xhr.responseText.replace(/^"|"$/g, emptyString);
						layer.msg(message);
					}
				});
				return;
			}
		}
	}
});
$("#tableBody").on("click", '.link-btn', function(e) {
	e.preventDefault();
	let transferVal = $(this).attr('transferVal');
	window.open(transferVal);
});
function initialPagination(pageNum, keyword) {
	$.ajax({
		url: '/hymns/pagination',
		data: 'pageNum=' + pageNum,
		success: function(response) {
			buildTableBody1(response);
			buildPageInfos(response);
			buildPageNavi(response);
		},
		error: function(xhr) {
			let message = xhr.responseText.replace(/^"|"$/g, emptyString);
			layer.msg(message);
		}
	});
}
function buildTableBody1(response) {
	$("#tableBody").empty();
	let index = response.records;
	$.each(index, (index, item) => {
		let checkBoxTd = $("<td class='text-center' style='width: 10%;vertical-align: middle;'></td>")
			.append($("<input class='form-check-input mt-0' style='vertical-align: middle;' type='checkbox' value='" + item.id + "'>"));
		let nameMixTd = $("<td class='text-left' style='width: 70%;vertical-align: middle;'></td>")
			.append($("<a href='#' class='link-btn' transferVal='" + item.link + "'>" + item.nameJp + "/" + item.nameKr + "</a>"));
		let scoreTd = $("<td class='text-center' style='width: 20%;vertical-align: middle;'></td>")
			.append($("<a href='#' class='score-download-btn' scoreId='" + item.id + "'>&#x1D11E;</a>"));
		$("<tr></tr>").append(checkBoxTd).append(nameMixTd).append(scoreTd).appendTo("#tableBody");
	});
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
	let keyword = null;
	$("#pageNavi").empty();
	let ul = $("<ul></ul>").addClass('pagination');
	let firstPageLi = $("<li class='page-item'></li>").append(
		$("<a class='page-link'></a>").append("最初へ").attr("href", "#"));
	let previousPageLi = $("<li class='page-item'></li>").append(
		$("<a class='page-link'></a>").append("&laquo;").attr("href", "#"));
	if (!result.hasPreviousPage) {
		firstPageLi.addClass('disabled');
		previousPageLi.addClass('disabled');
	} else {
		firstPageLi.click(function() {
			initialPagination(1, keyword);
		});
		previousPageLi.click(function() {
			initialPagination(pageNum - 1, keyword);
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
		nextPageLi.click(function() {
			initialPagination(pageNum + 1, keyword);
		});
		lastPageLi.click(function() {
			initialPagination(totalPages, keyword);
		});
	}
	ul.append(firstPageLi).append(previousPageLi);
	$.each(result.navigatePageNums, (index, item) => {
		let numsLi = $("<li class='page-item'></li>").append(
			$("<a class='page-link'></a>").append(item).attr("href", "#"));
		if (pageNum === item) {
			numsLi.attr("href", "#").addClass("active");
		}
		numsLi.click(function() {
			initialPagination(item, keyword);
		});
		ul.append(numsLi);
	});
	ul.append(nextPageLi).append(lastPageLi);
	$("<nav></nav>").append(ul).appendTo("#pageNavi");
}
function kanumiRetrieve(hymnId) {
	$.ajax({
		url: '/hymns/kanumiRetrieve',
		data: 'hymnId=' + hymnId,
		success: function(response) {
			buildTableBody2(response);
		},
		error: function(result) {
			layer.msg(result.responseJSON.message);
		}
	});
}
function buildTableBody2(response) {
	$("#tableBody").empty();
	$.each(response, (index, item) => {
		let checkBoxTd = $("<td class='text-center' style='width: 10%;vertical-align: middle;'></td>")
			.append($("<input class='form-check-input mt-0' style='vertical-align: middle;' type='checkbox' value='" + item.id + "'>"));
		let nameMixTd = $("<td class='text-left' style='width: 70%;vertical-align: middle;'></td>")
			.append($("<a href='#' class='link-btn' transferVal='" + item.link + "'>" + item.nameJp + "/" + item.nameKr + "</a>"));
		let scoreTd = $("<td class='text-center' style='width: 20%;vertical-align: middle;'></td>")
			.append($("<a href='#' class='score-download-btn' scoreId='" + item.id + "'>&#x1D11E;</a>"));
		if (item.linenumber === 'BUNRGUNDY') {
			$("<tr class='table-danger'></tr>").append(checkBoxTd).append(nameMixTd).append(scoreTd).appendTo("#tableBody");
		} else if (item.linenumber === 'NAPLES') {
			$("<tr class='table-warning'></tr>").append(checkBoxTd).append(nameMixTd).append(scoreTd).appendTo("#tableBody");
		} else if (item.linenumber === 'CADIMIUM') {
			$("<tr class='table-success'></tr>").append(checkBoxTd).append(nameMixTd).append(scoreTd).appendTo("#tableBody");
		} else {
			$("<tr class='table-light'></tr>").append(checkBoxTd).append(nameMixTd).append(scoreTd).appendTo("#tableBody");
		}
	});
}
function adjustWidth() {
	const $indexTable = $("#indexTable");
	if ($indexTable.length) {
		$('.background2').css('width', $indexTable.outerWidth() + 'px');
	}
}