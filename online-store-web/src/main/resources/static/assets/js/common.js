/*var param = {
    alertSuccess: "success",
    alertError: "error"
}
*/
/**
 * 公共弹框
 * @param obj 弹框对象
 * @param msg 弹框消息
 * @param type 弹框类型
 * @constructor
 */
function Alert(obj, msg) {

    window.setTimeout(function () {
        showAlert(obj, msg);
        window.setTimeout(function () {
            hideAlert(obj);
        }, 2000);
    }, 20);
}

function showAlert(obj,msg){
    obj.text(msg);
    obj.addClass("in");
}

function hideAlert(obj) {
    obj.removeClass("in");
}

/**
 * 检查非空
 * @param value
 * @returns {boolean}
 */
function checkNotEmpty(value) {
    value = $.trim(value);
    if (value.length == 0) {
        return false;
    } else {
        return true;
    }
}