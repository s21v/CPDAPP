function textSizeChange(size) {
    document.getElementsByTagName("table")[0].style.fontSize = size+"px";
}

function switchNightMode(isNightMode) {
    var table = document.getElementsByTagName("table")[0];
    if (isNightMode) {
        table.style.backgroundColor = "#424242";
        table.style.color = "#bdbdbd";
    } else {
        table.style.backgroundColor = "#ffffff";
        table.style.color = "#616161";
    }
}