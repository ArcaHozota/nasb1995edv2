$(document).ready(function() {
    adjustWidth();
    $("#tableBody").hide();
    $("#saraniSearchBtn").on("mousemove", function(e) {
        let x = e.pageX - $(this).offset().left;
        let y = e.pageY - $(this).offset().top;
        $(this).css("--x", x + "px");
        $(this).css("--y", y + "px");
    });
});
$("#saraniSearchBtn").on("click", function(e) {
    e.preventDefault();
    let hymnId = $("#hymnRecordEdit").val();
    if (hymnId === "0" || hymnId === 0) {
        layer.msg('賛美歌を選択してください');
    }else{
        swal.fire({
            title: '警告',
            text: '長い時間をかかる金海嶺氏の検索を行なっててよろしいでしょうか。',
            icon: 'warning',
            showDenyButton: true,
            denyButtonText: 'いいえ',
            confirmButtonText: 'はい',
            confirmButtonColor: '#7F0020',
            denyButtonColor: '#002FA7'
        }).then((result) => {
            if (result.isConfirmed) {
                adjustWidth();
                $("#loadingBackground2").show();
                $("#tableBody").show();
                $("#saraniSearchBtn").css("pointer-events", "none");
                saraniRetrieve(hymnId);
                setTimeout(function() {
                    $("#loadingBackground2").hide();
                    $("#saraniSearchBtn").css("pointer-events", "auto");
                }, 330000);
            } else if (result.isDenied) {
                $(this).close();
            }
        });
    }
});
$("#hymnRecordEdit").on("change", function() {
    let hymnId = $(this).val();
    if (hymnId === "0" || hymnId === 0) {
        $("#tableBody").hide();
        return;
    }
    $.ajax({
        url: '/hymns/getInfoById',
        data: 'hymnId=' + hymnId,
        success: function(response) {
            buildTableBody(response);
        },
        error: function(result) {
            layer.msg(result.responseJSON.message);
        }
    });
    $("#tableBody").show();
});
$("#tableBody").on("click", '.link-btn', function(e) {
    e.preventDefault();
    let transferVal = $(this).attr('transferVal');
    window.open(transferVal);
});
function saraniRetrieve(hymnId) {
    return;
    $.ajax({
        url: '/hymns/saraniRetrieve',
        data: 'hymnId=' + hymnId,
        success: function(response) {
            buildTableBody(response);
        },
        error: function(result) {
            layer.msg(result.responseJSON.message);
        }
    });
}
function buildTableBody(response) {
    $("#tableBody").empty();
    $.each(response, (response, item) => {
        let nameMixTd = $("<td class='text-center' style='width: 80%;vertical-align: middle;'></td>")
            .append($("<a href='#' class='link-btn' transferVal='" + item.link + "'>" + item.nameJp + "/" + item.nameKr + "</a>"));
        if (item.linenumber === 'BUNRGUNDY') {
            $("<tr class='table-danger'></tr>").append(nameMixTd).appendTo("#tableBody");
        } else if (item.linenumber === 'NAPLES') {
            $("<tr class='table-warning'></tr>").append(nameMixTd).appendTo("#tableBody");
        } else if (item.linenumber === 'CADIMIUM') {
            $("<tr class='table-success'></tr>").append(nameMixTd).appendTo("#tableBody");
        } else {
            $("<tr class='table-light'></tr>").append(nameMixTd).appendTo("#tableBody");
        }
    });
}
function adjustWidth() {
    const $indexTable = $("#indexTable");
    if ($indexTable.length) {
        $('.background2').css('width', $indexTable.outerWidth() + 'px');
    }
}