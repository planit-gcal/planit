import React from "react";
import "./App.css";
import { useGoogleLogin } from "@react-oauth/google";
import axios from "axios";

function App() {
  const onSuccess = (response: any) => {
    console.log("succ: ", response);
    axios
      .post("/plan-it/user/token", {
        code: response.code,
      })
      .then((response) => {
        console.log(response);
      })
      .catch((error) => console.log(error.message));
  };

  // @ts-ignore
  const login = useGoogleLogin({
    onSuccess: onSuccess,
    flow: "auth-code",
    scope:
      "profile email openid https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/calendar.readonly",
    accessType: "offline",
  });

  return (
    <div className="App">
      <button onClick={() => login()}>Sign in with Google 🚀 </button>;
    </div>
  );
}

export default App;
