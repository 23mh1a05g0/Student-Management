const BASE_URL = "/auth";

// ✅ Signup
async function register() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const res = await fetch(`${BASE_URL}/register`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username,
            password,
            role: "USER"
        })
    });

    if (res.ok) {
        alert("Signup successful!");
        window.location.href = "/login";
    } else {
        alert("Signup failed");
    }
}

// ✅ Login
async function login() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const res = await fetch(`${BASE_URL}/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username,
            password
        })
    });

    if (res.ok) {
        const token = await res.text();

        localStorage.setItem("token", token);

        window.location.href = "/dashboard";
    } else {
        alert("Invalid username or password");
    }
}

// ✅ Logout
function logout() {
    localStorage.removeItem("token");
    window.location.href = "/login";
}