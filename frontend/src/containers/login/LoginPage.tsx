import {useNavigate} from "react-router-dom";
import React, {useState} from "react";
import './LoginPage.css';

function LoginPage() {
    const navigate = useNavigate();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        const response = await fetch("/api/auth/authenticate", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ username, password }),
        });

        if (!response.ok) {
            const data = await response.json();
            alert(data.message);
            return;
        }

        const data = await response.json();
        localStorage.setItem("jwt", data.token);
        navigate("/user-home");
    };

    return <div className="login-page">
        <form onSubmit={handleSubmit}>
            <label htmlFor="username">Username:</label>
            <input type="text" id="username" value={username} onChange={(e) =>
                setUsername(e.target.value)
            }/>

            <label htmlFor="password">Password:</label>
            <input type="password" id="password" value={password} onChange={(e) =>
                setPassword(e.target.value)
            }/>

            <button type="submit">Submit</button>
        </form>
    </div>
}

export default LoginPage;