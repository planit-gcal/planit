//External imports
import { useContext, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';

import { getPlanItUserIdFromEmail } from '../../api/oauth/oauth.api';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';

const GoogleAuth = () => {
  const { isLoggedIn, setUserDetails } = useContext(PlanitUserContext);
  const navigate = useNavigate();

  const googleButton = useRef(null);

  useEffect(() => {
    const src = 'https://accounts.google.com/gsi/client';
    const id = '597544385697-i377oetpc36gvu981mic15rpe8or16oi.apps.googleusercontent.com';

    loadScript(src)
      .then(() => {
        /*global google*/
        // @ts-ignore
        console.log(google);
        // @ts-ignore
        google.accounts.id.initialize({
          client_id: id,
          callback: handleCredentialResponse,
        });
        // @ts-ignore
        google.accounts.id.renderButton(googleButton.current, { theme: 'outline', size: 'large' });
      })
      .catch(console.error);

    return () => {
      const scriptTag = document.querySelector(`script[src="${src}"]`);
      if (scriptTag) document.body.removeChild(scriptTag);
    };
  }, []);

  async function handleCredentialResponse(response: any) {
    const email = parseJwt(response.credential).email;

    console.log('Encoded JWT ID token: ' + email);
    const planitUserId = await getPlanItUserIdFromEmail(email);

    setUserDetails({ planitUserId, ownerEmail: email });
  }
  function parseJwt(token: any) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split('')
        .map(function (c) {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join('')
    );

    return JSON.parse(jsonPayload);
  }

  return <div ref={googleButton}></div>;
};
const loadScript = (src: any) =>
  new Promise<void>((resolve, reject) => {
    if (document.querySelector(`script[src="${src}"]`)) return resolve();
    const script = document.createElement('script');
    script.src = src;
    script.onload = () => resolve();
    script.onerror = (err) => reject(err);
    document.body.appendChild(script);
  });

export default GoogleAuth;
