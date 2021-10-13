document.getElementById("box").onsubmit = async (e) => {
    e.preventDefault();

    let data = {
        username: document.getElementById("username").value,
        password: document.getElementById("password").value
    }
    let response = await fetch('/submit?chat_id='+document.getElementById("chat_id").value, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    if (response.ok) {
        response.json().then(() => {
            window.open('/login', '_self');
        })
    }
}