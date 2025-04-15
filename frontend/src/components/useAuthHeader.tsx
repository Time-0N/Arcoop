import { useEffect } from "react";
import Cookies from "js-cookie";
import {useAuth} from "./AuthContext.tsx";

const useAuthHeader = () => {
    const { token, setToken } = useAuth();
    const getToken = () => {
        return Cookies.get("jwt");
    };

    useEffect(() => {
        const tokenFromCookie = getToken();

        if (tokenFromCookie) {
            setToken(tokenFromCookie);
        }

        if (token) {
            fetch("/", {
                headers: {
                    authorization: `Bearer ${token}`,
                },
            })
                .then((response) => response.json())
                .then((data) => console.log(data))
        }
    }, []);
};

export default useAuthHeader;