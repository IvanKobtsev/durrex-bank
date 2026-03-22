import { useAuth } from "react-oidc-context";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function Callback() {
  const auth = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!auth.isLoading && auth.isAuthenticated) {
      navigate("/"); // go to home after login
    }
  }, [auth.isLoading, auth.isAuthenticated]);

  if (auth.error) return <div>Error: {auth.error.message}</div>;
  return <div>Signing in...</div>;
}
