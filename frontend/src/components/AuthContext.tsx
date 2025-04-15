import React, { createContext, useContext } from "react";

interface AuthContextData {
    token: string | null;
    setToken: React.Dispatch<React.SetStateAction<String | null>>;
}

const AuthContext = createContext<AuthContextData>({
    token: null,
    setToken: () => {},
});

export const AuthProvider = AuthContext.Provider;
export const useAuth = () => useContext(AuthContext);