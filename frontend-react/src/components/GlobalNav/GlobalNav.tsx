import { DownOutlined, PlusOutlined } from '@ant-design/icons';
import { useGoogleLogin } from '@react-oauth/google';
import { Button, Divider, Dropdown, Input, MenuProps, Row, Select, Space, Tabs as AntdTabs, Typography } from 'antd';
import { useCallback, useContext } from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

import { AxiosInstance } from '../../config';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';

const { Text } = Typography;

const Tabs = styled(AntdTabs)`
  .ant-tabs-nav::before {
    border-bottom: 0;
  }
`;

const GlobalNav = () => {
  const { userDetails, setUserDetails, userEmails } = useContext(PlanitUserContext);

  const onSuccess = useCallback(
    (response: any) => {
      console.log('succ: ', response);
      AxiosInstance.post('/plan-it/user/token', {
        code: response.code,
        planit_userId: userDetails?.planitUserId || null,
      })
        .then((response) => {
          console.log(response);
          // setUserDetails((prev) => ({ ...prev, planitUserId: response.data.planit_userId! }));
        })
        .catch((error) => console.log(error.message));
    },
    [setUserDetails, userDetails?.planitUserId]
  );

  const login = useGoogleLogin({
    onSuccess,
    flow: 'auth-code',
    scope:
      'profile email openid https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/calendar.readonly',
  });

  const addItem = (e: React.MouseEvent<HTMLAnchorElement>) => {
    e.preventDefault();
    e.stopPropagation();
    login();
  };

  const onOwnerEmailChange = (ownerEmail: string) => {
    setUserDetails((prev) => ({ ...prev, ownerEmail }));
  };

  const OperationsSlot = {
    right: (
      <Space direction="horizontal">
        <Text type="secondary" style={{ margin: 0 }}>
          Creating events as
        </Text>

        <Select
          style={{ width: 220 }}
          value={userDetails?.ownerEmail}
          onClick={(e) => {
            e.stopPropagation();
          }}
          onChange={onOwnerEmailChange}
          dropdownRender={(menu) => (
            <>
              {menu}
              <Divider style={{ margin: '8px 0' }} />
              <Space style={{ padding: '0 8px 4px' }}>
                <Button type="text" icon={<PlusOutlined />} onClick={addItem}>
                  Add email
                </Button>
              </Space>
            </>
          )}
          options={userEmails.map((item) => ({ label: item, value: item }))}
        />

        <Button type="primary" danger onClick={() => setUserDetails({})}>
          Logout
        </Button>
      </Space>
    ),
  };

  const items = [
    { key: '1', label: <Link to="create-events">Create events</Link> },
    { key: '2', label: <Link to="manage-presets">Manage presets</Link> },
  ];

  return <Tabs items={items} tabBarExtraContent={OperationsSlot} tabBarStyle={{ margin: '0' }} />;
};

export default GlobalNav;
