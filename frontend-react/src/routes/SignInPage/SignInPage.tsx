//External imports
import { useCallback, useContext, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';

import { getPlanItUserIdFromEmail } from '../../api/oauth/oauth.api';
import { googleOAuthClientId } from '../../config';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';
import { parseJwt, loadScript } from '../../utils/oauth.utils';

const src = 'https://accounts.google.com/gsi/client';

const GoogleAuth = () => {
  const { userDetails, setUserDetails } = useContext(PlanitUserContext);
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
            client_id: googleOAuthClientId,
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
