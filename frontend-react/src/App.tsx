
import React from "react";
import "./App.css";
import { useGoogleLogin } from "@react-oauth/google";
import axios from "axios";
import {CreateEventForm} from "./components/CreateEventForm/CreateEventForm";
import {EventCreateRequest} from "./models/event";

function App() {
  const onSuccess = (response: any) => {
    console.log("succ: ", response);
    axios
      .post(
        "/plan-it/user/token",
        {
          code: response.code,
        },
        {
          headers: {
            "Bypass-Tunnel-Reminder": 1,
          },
        }
      )
      .then((response) => {
        console.log(response);
      })
      .catch((error) => console.log(error.message));
  };

    const onEventSubmit = (result: EventCreateRequest) => {
        console.log("succ: ", result);
        axios
            .post("/plan-it/calendar/new-event/1", result)
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
      <br/>
      <CreateEventForm onSubmit={onEventSubmit}></CreateEventForm>
    </div>
  );
}

export default App;
