function textSizeChange(size) {
    document.getElementsByTagName("table")[0].style.fontSize = size+"px";
}

function switchNightMode(isNightMode) {
    if (isNightMode) {
        document.body.style.backgroundColor = "#424242";
        document.body.style.color = "#bdbdbd";
    } else {
        document.body.style.backgroundColor = "#ffffff";
        document.body.style.color = "#616161";
    }
}