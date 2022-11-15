import { GoogleOAuthProvider } from '@react-oauth/google';
import 'antd/dist/antd.css';
import ReactDOM from 'react-dom';
import { BrowserRouter } from 'react-router-dom';

import App from './App';
import { PlanitUserProvider } from './contexts/PlanitUserContext';
import './index.css';
import reportWebVitals from './reportWebVitals';
import SignIn from "./routes/SignInPage/SignIn";

ReactDOM.render(
  <BrowserRouter>
    <GoogleOAuthProvider clientId={'597544385697-i377oetpc36gvu981mic15rpe8or16oi.apps.googleusercontent.com'}>
      {/*<PlanitUserProvider>*/}
      {/*  <App />*/}
      {/*</PlanitUserProvider>*/}
        <SignIn/>
    </GoogleOAuthProvider>
  </BrowserRouter>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
