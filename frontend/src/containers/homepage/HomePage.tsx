import {useNavigate} from "react-router-dom";
import './HomePage.css';

function HomePage() {
    const navigate = useNavigate();

    const handleLoginClick = () => {
        navigate('/login');
    };

    const handleRegisterClick = () => {
        navigate('/register')
    }

    return <div className="home-page">
        <span>Arcoop</span>
        <div className="home-page-button-container">
            <button className="home-page-login-button" onClick={handleLoginClick}>Login</button>
            <button className="home-page-login-button" onClick={handleRegisterClick}>Register</button>
        </div>
    </div>
}

export default HomePage;