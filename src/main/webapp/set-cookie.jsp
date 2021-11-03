<%
    String itemToAdd = request.getParameter("item");
    if (itemToAdd != null && itemToAdd != "") {
        Cookie items = new Cookie(itemToAdd, itemToAdd);
        items.setMaxAge(60*60*24);
        response.addCookie(items);
    }
    response.sendRedirect("/");
%>

<html>
<head>
    <title>Setting Cookies</title>
</head>

<body>

<a href="/">Back</a>

</body>
</html>