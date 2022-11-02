import { GoogleOAuthProvider } from '@react-oauth/google';
import 'antd/dist/antd.css';
import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Routes } from 'react-router-dom';

import App from './App';
import { PlanitUserProvider } from './contexts/PlanitUserContext';
import './index.css';
import reportWebVitals from './reportWebVitals';
import { AccountSettings } from './routes/AccountSettings/AccountSettings';
import { CreateEventPage } from './routes/CreateEventPage/CreateEventPage';
import { ManagePresetsPage } from './routes/ManagePresetsPage/ManagePresetsPage';
import { SignInPage } from './routes/SignInPage/SignInPage';

ReactDOM.render(
  <BrowserRouter>
    <GoogleOAuthProvider clientId={'937013173995-cmhpl3s97umb1njiseqtk3c049guefi5.apps.googleusercontent.com'}>
      <PlanitUserProvider>
        <Routes>
          <Route path="/" element={<App />}>
            <Route index element={<SignInPage />} />
            <Route path="create-events" element={<CreateEventPage />} />
            <Route path="manage-presets" element={<ManagePresetsPage />} />
            <Route path="account-settings" element={<AccountSettings />} />
          </Route>
        </Routes>
      </PlanitUserProvider>
    </GoogleOAuthProvider>
  </BrowserRouter>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
