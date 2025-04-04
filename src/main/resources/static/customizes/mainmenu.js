$(document).ready(function() {
	let loginMsg = $("#loginMsgContainer").val().trim();
	if (loginMsg !== emptyString) {
		layer.msg(loginMsg);
	}
	let message = localStorage.getItem('redirectMessage');
	if (message) {
		layer.msg(message);
		localStorage.removeItem('redirectMessage');
	}
});
$("#booksKanriMainmenu").on("click", function() {
	layer.msg(delayApology);
	// let url = '/books/initial';
	// checkPermissionAndTransfer(url);
});
$("#hymnsKanriMainmenu").on("click", function() {
	let url = '/hymns/toPages.action?pageNum=1';
	checkPermissionAndTransfer(url);
});
$("#randomKanriMainmenu").on("click", function() {
	let url = '/hymns/toRandomFive.action';
	checkPermissionAndTransfer(url);
});