import { useGoogleLogin } from '@react-oauth/google';
import axios from 'axios';
import { useState, useEffect, useCallback } from 'react';

import './App.css';
import { CreateEventForm } from './components/CreateEventForm/CreateEventForm';
import { EmailSelector } from './components/EmailSelector/EmailSelector';
import { useLocalStorage } from './hooks/useLocalStorage';
import { EventCreateRequest } from './models/event';

function App() {
  const [owner, setOwner] = useState('');
  const [accountEmails, setAccountEmails] = useState<string[]>([]);
  const [planitUserId, setPlanitUserId] = useLocalStorage('planitUserId', null);

  const onSuccess = useCallback(
    (response: any) => {
      console.log('succ: ', response);
      axios
        .post(
          '/plan-it/user/token',
          {
            code: response.code,
            planit_userId: planitUserId,
          },
          {
            headers: {
              'Bypass-Tunnel-Reminder': 1,
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
    },
    [planitUserId, setPlanitUserId]
  );

  const onEventSubmit = (result: EventCreateRequest) => {
    console.log('succ: ', result);
    axios
      .post('/plan-it/calendar/new-event', result)
      .then((response) => {
        console.log(response);
      })
      .catch((error) => console.log(error.message));
  };

  const getEmailList = (id: string) => {
    axios.get(`/plan-it/user/getAllEmails/${id}`).then((response) => setAccountEmails(response.data));
  };

  useEffect(() => {
    if (planitUserId !== null) getEmailList(planitUserId);
  }, [planitUserId]);

  const login = useGoogleLogin({
    onSuccess,
    flow: 'auth-code',
    scope:
      'profile email openid https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/calendar.readonly',
  });

  const onSelectEmail = (email: string) => {
    setOwner(email);
  };

  return (
    <div className="App">
      <button onClick={() => login()}>Sign in with Google 🚀</button>
      <button onClick={() => setPlanitUserId(null)}>Logout</button>;<div></div>
      <EmailSelector emails={accountEmails} selectChange={onSelectEmail}></EmailSelector>
      <br />
      <CreateEventForm onSubmit={onEventSubmit} owner={owner}></CreateEventForm>
    </div>
  );
}

export default App;
