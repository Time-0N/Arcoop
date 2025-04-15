import './App.css'
import {Route, Routes} from "react-router-dom";
import HomePage from "./containers/homepage/HomePage";
import LoginPage from "./containers/login/LoginPage";
import RegisterPage from "./containers/register/RegisterPage";
import UserHome from "./containers/userhome/UserHome";
import Settings from "./containers/settings/Settings";
import TicTacToe from "./containers/tictactoe/TicTacToe";
import Shop from "./containers/shop/Shop";
import {ProtectedRoute} from "./components/ProtectedRoute";

function App() {

    return (
        <div className="App">
            <Routes>
                <Route path={"/"} element={<HomePage/>}/>

                <Route path={"/login"} element={<LoginPage/>}/>

                <Route path={"/register"} element={<RegisterPage/>}/>

                <Route path={"/user-home"} element={<ProtectedRoute children={<UserHome/>}/>}/>

                <Route path={"/user-home/settings"} element={<ProtectedRoute children={<Settings/>}/>}/>

                <Route path={"/user-home/tic-tac-toe"} element={<ProtectedRoute children={<TicTacToe/>}/>}/>

                <Route path={"/user-home/shop"} element={<ProtectedRoute children={<Shop/>}/>}/>
            </Routes>
        </div>
    )
}

export default App