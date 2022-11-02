import { useGoogleLogin } from '@react-oauth/google';
import { useCallback } from 'react';

import { AxiosInstance } from '../../config';
import { useLocalStorage } from '../../hooks/useLocalStorage';

export const SignInPage = () => {
  const [planitUserId, setPlanitUserId] = useLocalStorage('planitUserId', null);

  const onSuccess = useCallback(
    (response: any) => {
      console.log('succ: ', response);
      AxiosInstance.post('/plan-it/user/token', {
        code: response.code,
        planit_userId: planitUserId,
      })
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
