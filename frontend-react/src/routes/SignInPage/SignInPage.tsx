import { useGoogleLogin } from '@react-oauth/google';
import { useCallback, useContext } from 'react';
import { useNavigate } from 'react-router-dom';

import { AxiosInstance } from '../../config';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';

export const SignInPage = () => {
  const { userDetails, setUserDetails } = useContext(PlanitUserContext);
  const navigate = useNavigate();

  const onSuccess = useCallback(
    (response: any) => {
      console.log('succ: ', response);
      AxiosInstance.post('/plan-it/user/token', {
        code: response.code,
        planit_userId: userDetails?.planitUserId || null,
      })
        .then((response) => {
          console.log(response);
          setUserDetails((prev) => ({ ...prev, planitUserId: response.data.planit_userId! }));
          navigate('/create-events');
        })
        .catch((error) => console.log(error.message));
    },
    [navigate, setUserDetails, userDetails?.planitUserId]
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
