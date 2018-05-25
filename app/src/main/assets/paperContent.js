function switchNightMode(isNightMode) {
    if (isNightMode) {
        document.body.style.backgroundColor = "#424242";
        var a = document.getElementsByTagName("font");
        for (var i=0;i<a.length;i++)
        {
            a[i].style.color = "#bdbdbd"
        }
    } else {
        document.body.style.backgroundColor = "#FAFAFA";
        var a = document.getElementsByTagName("font");
                for (var i=0;i<a.length;i++)
                {
                    a[i].style.color = "#616161"
                }
    }
}