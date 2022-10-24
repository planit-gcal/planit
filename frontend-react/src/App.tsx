import React, {useState, useEffect, useCallback} from "react";
import "./App.css";
import {useGoogleLogin} from "@react-oauth/google";
import axios from "axios";
import {CreateEventForm} from "./components/CreateEventForm/CreateEventForm";
import {EventCreateRequest} from "./models/event";
import {EmailSelector} from "./components/EmailSelector/EmailSelector";
import {useLocalStorage} from "./hooks/useLocalStorage";

function App() {
    const [owner, setOwner] = useState('');
    const [accountEmails, setAccountEmails] = useState<string[]>([]);
    const [planitUserId, setPlanitUserId] = useLocalStorage("planitUserId", null);

    const onSuccess = useCallback((response: any) => {
        console.log("succ: ", response);
        axios
            .post(
                "/plan-it/user/token",
                {
                    code: response.code,
                    planit_userId: planitUserId
                },
                {
                    headers: {
                        "Bypass-Tunnel-Reminder": 1,
                    },
                }
            )
            .then((response) => {
                console.log(response);
                //here add set for local storage
                setPlanitUserId(response.data.planit_userId);
                console.log(planitUserId);
            })
            .catch((error) => console.log(error.message));
    },[planitUserId]);

    const onEventSubmit = (result: EventCreateRequest) => {
        console.log("succ: ", result);
        axios
            .post("/plan-it/calendar/new-event", result)
            .then((response) => {
                console.log(response);
            })
            .catch((error) => console.log(error.message));
    };

    const getEmailList = (id:string) => {
        axios
            .get(`/plan-it/user/getAllEmails/${id}`)
            .then(response => setAccountEmails(response.data))
    }

    useEffect(() => {
        if (planitUserId !== null)
            getEmailList(planitUserId);
    }, [planitUserId])

    // @ts-ignore
    const login = useGoogleLogin({
        onSuccess: onSuccess,
        flow: "auth-code",
        scope:
            "profile email openid https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/calendar.readonly",
        accessType: "offline",
    });

    const onSelectEmail = (email:string) => {
        setOwner(email);
    };

    return (
        <div className="App">
            <button onClick={() => login()}>Sign in with Google 🚀</button>
            <button onClick={() => setPlanitUserId(null)}>Logout</button>
            ;
            <div >

            </div>
            <EmailSelector emails={accountEmails} selectChange={onSelectEmail}></EmailSelector>
            <br/>
            <CreateEventForm onSubmit={onEventSubmit} owner={owner}></CreateEventForm>
        </div>
    );
}

export default App;
