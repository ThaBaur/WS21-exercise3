<html>
<head>
    <title>Reading Cookies</title>
</head>

<body>
<p>Content of database-cart: ${cart}</p>

<%
   Cookie[] cookies = request.getCookies();

    if( cookies != null) {
        out.println("<h2>Found Cookies: </h2>");
        for(Cookie cookie: cookies) {
            if(cookie.getName().equals("JSESSIONID")) {
                continue;
            }
            out.println("Name: " + cookie.getName() + ", Value: " + cookie.getValue() + "<br/>");
        }
    } else {
        out.println("<h2>No Cookies found!</h2>");
    }
%>

<form action = "/set" method = "POST">
    Item to add: <input type = "text" name = "item"><br />
    <input type = "submit" value = "Submit" />
</form>

</body>

</html>