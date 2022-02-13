function changeTheme(name, css) {
    $("#bootswatch-style").attr('href', css);
}

$(function() {
    $.getJSON('https://bootswatch.com/api/4.json', function(data) {
        var items = [];
        $.each(data.themes, function(index, theme) {
            items.push( "<li>" + "<a id='" + theme.name + "' class='dropdown-item' href='#' onclick=\"changeTheme('" + theme.name + "', '" + theme.cssMin + "');return false;\">" + theme.name + "</a>" + "</li>" );
        });
        $("#bootswatch-themes").append(items);
    });
});