/**
 * 检查输入价格
 * @param value
 * @returns {boolean}
 */
function checkRadixPoint(value) {
    value = $.trim(value);
    var alertObj = $("#checkRadixPointAlert");
    if(!checkNotEmpty(value)){
        Alert(alertObj,"价格不能为空！");
        $("#updateProductButton").attr("disabled", true);
        return false;
    }
    if (value.indexOf(".") == 0 || value.indexOf(".") == value.length - 1) {
        Alert(alertObj,"请输入正确的价格！");
        $("#updateProductButton").attr("disabled", true);
        return false;
    }
    if (value.indexOf(".") > 0) {
        if (value.length - (value.indexOf(".") + 1) > 2) {
            Alert(alertObj,"价格只能保留两位小数点，请检查输入格式！");
            $("#updateProductButton").attr("disabled", true);
            return false;
        }
    }
    $("#updateProductButton").attr("disabled",false);
}

/**
 * 检查名称不为空
 * @param value
 * @returns {boolean}
 */
function checkProductName(value){
    value = $.trim(value);
    var alertObj=$("#checkRadixPointAlert");
    if(!checkNotEmpty(value)){
        Alert(alertObj,"产品名称不能为空！");
        $("#updateProductButton").attr("disabled", true);
        return false;
    }
    $("#updateProductButton").attr("disabled", false);
}



