import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {logout} from "../../components/LogOut";
import './UserHome.css';

type User = {
    name: string,
    medals: number,
    equippedColor: string | undefined
}

function UserHome() {
    const navigate = useNavigate();
    const [user, setUser] = useState<User>({equippedColor: "#FFFFFF", name: "loading...", medals: 0})

    const handleButtonSettings = () => {
        navigate("/user-home/settings");
    }
    const handleButtonLogout = () => {
        logout();
        navigate("/login");
    };
    const handleButtonTicTacToe = () => {
        navigate("/user-home/tic-tac-toe");
    }
    const handleButtonShop = () => {
        navigate("/user-home/shop");
    }

    useEffect(() => {
        const fetchData = async () => {
            const config = {
                headers: {
                    Authorization: "Bearer " + localStorage.getItem("jwt"),
                },
            };

            try {
                const response = await fetch("/api/user/get/user", config);
                console.log(response);
                const data = await response.json();
                console.log(data);
                setUser(data);
            } catch (error) {
                console.error(error);
            }
        };
        fetchData();
    }, []);

    return (
        <div className="user-page">
            <div className="button-container">
                <button onClick={handleButtonSettings} className="menu-button settings">Settings</button>
                <button onClick={handleButtonShop} className="menu-button shop">Shop</button>
                <button onClick={handleButtonTicTacToe} className="menu-button play">Play</button>
            </div>
            <div className="user-details-container">
                <h1>Home</h1>
                <div className="UserName">
                    <span style={{color: user.equippedColor}}>{user.name}</span>
                </div>
                <div className="UserMedals">
                    <span>Coins: {user.medals}</span>
                </div>
            </div>
            <div className="button-container">
                <button onClick={handleButtonLogout} className="menu-button logout">Logout</button>
            </div>
        </div>
    );
}

export default UserHome;