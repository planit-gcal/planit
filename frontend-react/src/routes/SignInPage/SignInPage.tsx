import google from 'google-one-tap';
import { useCallback, useContext, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';

import { getPlanItUserIdFromEmail } from '../../api/oauth/oauth.api';
import { googleOAuthClientId } from '../../config';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';
import { parseJwt, loadScript, clearScript } from '../../utils/oauth.utils';

const src = 'https://accounts.google.com/gsi/client';

const GoogleAuth = () => {
  const { userDetails, setUserDetails } = useContext(PlanitUserContext);
  const navigate = useNavigate();

  const googleButton = useRef<HTMLDivElement>(null);

  const handleCredentialResponse = useCallback(
    async (response: google.CredentialResponse) => {
      const email = parseJwt(response.credential).email;

      const planitUserId = await getPlanItUserIdFromEmail(email);

      setUserDetails({ planitUserId, ownerEmail: email });

      navigate(!!planitUserId ? '/create-events' : '/sign-up');
    },
    [navigate, setUserDetails]
  );

  useEffect(() => {
    loadScript(src)
      .then(async () => {
        google.accounts.id.initialize({
          client_id: googleOAuthClientId,
          callback: handleCredentialResponse,
        });
        google.accounts.id.renderButton(googleButton.current!, { theme: 'outline', size: 'large' });
      })
      .catch(console.error);

    return () => clearScript(src);
  }, [handleCredentialResponse, navigate, userDetails]);

  return <div ref={googleButton} />;
};

export default GoogleAuth;
