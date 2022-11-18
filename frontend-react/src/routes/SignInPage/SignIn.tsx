//External imports
import { useCallback, useContext, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';

import { getPlanItUserIdFromEmail } from '../../api/oauth/oauth.api';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';

const src = 'https://accounts.google.com/gsi/client';
const id = '937013173995-cmhpl3s97umb1njiseqtk3c049guefi5.apps.googleusercontent.com';

const loadScript = (src: any) =>
  new Promise<void>((resolve, reject) => {
    if (document.querySelector(`script[src="${src}"]`)) return resolve();
    const script = document.createElement('script');
    script.src = src;
    script.onload = () => resolve();
    script.onerror = (err) => reject(err);
    document.body.appendChild(script);
  });

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

const GoogleAuth = () => {
  const { isLoggedIn, userDetails, setUserDetails } = useContext(PlanitUserContext);
  const navigate = useNavigate();

  const googleButton = useRef(null);

  const handleCredentialResponse = useCallback(
    async (response: any) => {
      const email = parseJwt(response.credential).email;

      const planitUserId = await getPlanItUserIdFromEmail(email);

      setUserDetails({ planitUserId, ownerEmail: email });

      if (!planitUserId) {
        console.log('navigate');

        navigate('/sign-up');
      } else {
        navigate('/create-events');
      }
    },
    [navigate, setUserDetails]
  );

  useEffect(() => {
    const doStuff = async () => {
      loadScript(src)
        .then(async () => {
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
    };

    doStuff();

    return () => {
      const scriptTag = document.querySelector(`script[src="${src}"]`);
      if (scriptTag) document.body.removeChild(scriptTag);
    };
  }, [handleCredentialResponse, navigate, userDetails]);

  return <div ref={googleButton}></div>;
};

export default GoogleAuth;
