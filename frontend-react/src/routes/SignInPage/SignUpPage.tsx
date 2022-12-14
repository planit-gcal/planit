import { CodeResponse, useGoogleLogin } from '@react-oauth/google';
import { Typography, Col, Row, Button } from 'antd';
import { useCallback, useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

import { createNewUser } from '../../api/users/users.api';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';
import { useUrlQuery } from '../../hooks/useUrlQuery';

export const SignUpPage = () => {
  const { isLoggedIn, setUserDetails } = useContext(PlanitUserContext);
  const navigate = useNavigate();
  const query = useUrlQuery();

  useEffect(() => {
    isLoggedIn && navigate({ pathname: '/create-events', search: `?${query.toString()}` });
  }, [isLoggedIn, navigate, query]);

  const onSuccess = useCallback(
    async ({ code }: CodeResponse) => {
      try {
        const response = await createNewUser(code);

        setUserDetails((prev) => ({ ...prev, planitUserId: response.planit_user_id }));
        navigate({ pathname: '/create-events', search: `?${query.toString()}` });
      } catch (e) {
        console.log(e);
      }
    },
    [navigate, query, setUserDetails]
  );

  const login = useGoogleLogin({
    onSuccess,
    flow: 'auth-code',
    scope:
      'profile email openid https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/calendar.readonly',
  });

  return (
    <Row justify="center" gutter={16}>
      <Col span={16}>
        <Row justify="center">
          <Typography.Title level={2}>Welcome to PlanIt:</Typography.Title>
        </Row>
        <Row>
          <Typography.Text>
            PlanIt is both a website and a Google Addon, it allows users to automatically schedule events for groups
            based on the desired criteria. After you sign in, you will fill out a form outlining what event you want to
            create, then the application will find the best time slot for your event.
          </Typography.Text>
        </Row>
        <Row justify="end">
          <Button type={'primary'} onClick={() => login()}>
            Sign up with Google
          </Button>
        </Row>
      </Col>
    </Row>
  );
};
