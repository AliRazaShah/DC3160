<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>

    <title>Login / signup page</title>
</head>
<body>
<h2>Please log in!</h2>
<form method="POST" action="http://localhost:8080/coursework/do/login">
    Username:<input type="text" name="username" value="" />----
    Password:<input type="password" name="password" value="" />
    <input type="submit" value="Click to log in" />
</form>

<form method="POST" action="http://localhost:8080/coursework/do/addUser" id="signUp">
    <h2> Don't yet have an account? </h2>
    Username:<input id="newUserNameField" type="text" name="newUsername" value="" minLength="7"/>----
    Password:<input type="password" name="newPassword" value="" />

    <input id="submitSignUp" type="submit" value="Sign up as a new user" disabled="disabled"/>

</form>

<script type="application/javascript">
    const form = document.getElementById('signUp');
    // This will look for changes in the form
    form.addEventListener("change",() => {
        // Disables the submit button if the username field is too short (7 characters):
        document.getElementById('submitSignUp').disabled = document.getElementById("newUserNameField").validity.tooShort;
    });
</script>
</body>
</html>
