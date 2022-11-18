import { GoogleOAuthProvider } from '@react-oauth/google';
import 'antd/dist/antd.css';
import ReactDOM from 'react-dom';
import { BrowserRouter } from 'react-router-dom';

import App from './App';
import { googleOAuthClientId } from './config';
import { PlanitUserProvider } from './contexts/PlanitUserContext';
import './index.css';
import reportWebVitals from './reportWebVitals';

ReactDOM.render(
  <BrowserRouter>
    <GoogleOAuthProvider clientId={googleOAuthClientId}>
      <PlanitUserProvider>
        <App />
      </PlanitUserProvider>
    </GoogleOAuthProvider>
  </BrowserRouter>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
