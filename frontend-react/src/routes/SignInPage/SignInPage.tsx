import { CodeResponse, useGoogleLogin } from '@react-oauth/google';
import { useCallback, useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

import { createNewUser } from '../../api/users/users.api';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';

export const SignInPage = () => {
  const { isLoggedIn, setUserDetails } = useContext(PlanitUserContext);
  const navigate = useNavigate();

  useEffect(() => {
    isLoggedIn && navigate('/create-events');
  }, [isLoggedIn, navigate]);

  const onSuccess = useCallback(
    async ({ code }: CodeResponse) => {
      try {
        const response = await createNewUser(code);

        setUserDetails((prev) => ({ ...prev, planitUserId: response.planit_user_id }));
        navigate('/create-events');
      } catch (e) {
        console.log(e);
      }
    },
    [navigate, setUserDetails]
  );

  const login = useGoogleLogin({
    onSuccess,
    flow: 'auth-code',
    scope:
      'profile email openid https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/calendar.readonly',
  });

  return (
    <div>
      <button onClick={() => login()}>Sign in with Google 🚀</button>
    </div>
  );
};
