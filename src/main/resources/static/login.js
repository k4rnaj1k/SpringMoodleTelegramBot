document.getElementById("box").onsubmit = async (e) => {
    e.preventDefault();

    let data = {
        username: document.getElementById("username").value,
        password: document.getElementById("password").value
    }
    let response = await fetch('/submit?chat_id=' + document.getElementById("chat_id").value, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    if (response.ok) {
        window.open('/success', '_self');
    } else {
        if (document.getElementById("error") == null) {
            let error = document.createElement("p");
            error.setAttribute("id", "error");
            document.getElementById("box").appendChild(error);
        }
        response.json().then(json =>
            document.getElementById("error").innerText = json.message);
    }
}