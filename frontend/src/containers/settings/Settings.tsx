import './Settings.css'
import React, {useState} from "react";
import {useNavigate} from "react-router-dom";
import {Button, Modal} from "react-bootstrap";

function settings() {
    const navigate = useNavigate();
    const [name, setName] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [showAccountModal, setShowAccountModal] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

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

    const handleShowDeleteModal = () => {
        setShowDeleteModal(true);
    };

    const handleCloseDeleteModal = () => {
        setShowDeleteModal(false);
    };

    const handleShowAccountModal = () => {
        setShowAccountModal(true);
    };

    const handleCloseAccountModal = () => {
        setShowAccountModal(false);
    };


    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (password) {
            if (!validatePassword(password)) {
                alert("Password must contain at least one uppercase letter, one lowercase letter, one number, one special character, and be at least 6 characters long.");
            }
        }

        const token = localStorage.getItem("jwt");
        console.log("token: " + token)
        const config = {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ` + token,
            },
            body: JSON.stringify({ name, username, password }),
        };

        try {
            const response = await fetch("/api/user/update", config);
            if (!response.ok) {
                return;
            }

            const data = await response.json();
            console.log("Data: " + data);
            localStorage.setItem("jwt", data.token);
            navigate("/user-home");
        } catch (error) {
            console.error(error);
        }
    };

    const handleDelete = async () => {
        const token = localStorage.getItem("jwt");
        const config = {
            method: "DELETE",
            headers: {
                Authorization: `Bearer ` + token,
            },
        };

        try {
            const response = await fetch("/api/user/delete-user", config);
            if (!response.ok) {
                return;
            }

            localStorage.removeItem("jwt");
            navigate("/");
        } catch (error) {
            console.log(error);
        }
    }

    return <div className="settings-page">
        <Button className="change-account-button" onClick={handleShowAccountModal}>Change account information</Button>

        <Modal className="modal-account" show={showAccountModal} onHide={handleCloseAccountModal}>
            <Modal.Header closeButton className="modal-account-header">
                <Modal.Title>Account Information</Modal.Title>
            </Modal.Header>
            <Modal.Body className="modal-account-body">
                <form className="modal-form-account" onSubmit={handleSubmit}>
                    <label htmlFor="name">New Name:</label>
                    <input
                        type="text"
                        id="new-name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />
                    <label htmlFor="username">New Username:</label>
                    <input
                        type="text"
                        id="new-username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                    <label htmlFor="password">New Password:</label>
                    <input
                        type="password"
                        id="new-password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <button className="modal-account-submit-button" type="submit">Submit</button>
                    </form>
            </Modal.Body>
        </Modal>

        <button className="delete-account-button" onClick={handleShowDeleteModal}>Delete Account</button>

        <Modal className="modal-delete" show={showDeleteModal} onHide={handleCloseDeleteModal}>
            <Modal.Header className="modal-delete-header">
                <Modal.Title>Delete Account</Modal.Title>
            </Modal.Header>
            <Modal.Body className="modal-delete-body">
                Are you sure you want to delete your account? <br/>
                This action cannot be undone.
            </Modal.Body>
            <Modal.Footer className="modal-delete-footer">
                <button className="modal-delete-cancel-button" onClick={handleCloseDeleteModal}>Cancel</button>
                <button className="modal-delete-confirm-button" onClick={handleDelete}>Confirm</button>
            </Modal.Footer>
        </Modal>
    </div>
}

export default settings;