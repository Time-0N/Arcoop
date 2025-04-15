import {Navigate} from "react-router-dom";
import {isJwtValid} from "./JwtValidationFunction.ts";

export const ProtectedRoute = ({ children }: {children: JSX.Element }) => {
    const jwt = localStorage.getItem('jwt');

    if (!jwt || !isJwtValid(jwt)) {
        return <Navigate to="/" replace/>;
    }

    return children;
};