function changeTheme(name, css) {
    $("#bootswatch-style").attr('href', css);
}

$(function() {
    $("#bootswatch-navlink").on("click", function() {
        $.getJSON('https://bootswatch.com/api/4.json', function(data) {
            var items = [];
            items.push("<li><a class=\"dropdown-item\" href=\"#\" onclick=\"changeTheme('default', '');return false;\">Default</a></li>");
            $.each(data.themes, function(index, theme) {
                items.push( "<li>" + "<a id='" + theme.name + "' class='dropdown-item' href='#' onclick=\"changeTheme('" + theme.name + "', '" + theme.cssCdn + "');return false;\">" + theme.name + "</a>" + "</li>" );
            });
            $("#bootswatch-themes").empty();
            $("#bootswatch-themes").append(items);
        });
    });
});