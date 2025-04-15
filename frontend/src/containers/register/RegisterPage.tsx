import {useNavigate} from "react-router-dom";
import React, {useState} from "react";
import './RegisterPage.css';

function RegisterPage() {
    const navigate = useNavigate();
    const [name, setName] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const validatePassword = (password: string) => {
        let upperCaseFlag = false;
        let lowerCaseFlag = false;
        let numberFlag = false;
        let specialCharacterFlag = false;
        let lengthIsOverOr6Flag = password.length >= 6;

        for (let i = 0; i < password.length; i++) {
            const ch = password.charAt(i);
            if (/[a-z]/.test(ch)) {
                lowerCaseFlag = true;
            }
            if (/[A-Z]/.test(ch)) {
                upperCaseFlag = true;
            }
            if (/[0-9]/.test(ch)) {
                numberFlag = true;
            }
            if (/[^a-zA-Z0-9]/.test(ch)) {
                specialCharacterFlag = true;
            }
        }

        return upperCaseFlag && lowerCaseFlag && numberFlag && specialCharacterFlag && lengthIsOverOr6Flag
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (!validatePassword(password)) {
            alert("Password must contain at least one uppercase letter, one lowercase letter, one number, one special character, and be at least 6 characters long.");
        }

        const response = await fetch("/api/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ name, username, password }),
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

    return <div className="register-page">
        <form onSubmit={handleSubmit}>
            <label htmlFor="name">Name:</label>
            <input type="text" id="name" value={name} onChange={(e) =>
                setName(e.target.value)
            }/>

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

export default RegisterPage;