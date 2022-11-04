import { DownOutlined } from '@ant-design/icons';
import { Button, Dropdown, MenuProps, Row, Space, Tabs as AntdTabs, Typography } from 'antd';
import { useContext } from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

import { PlanitUserContext } from '../../contexts/PlanitUserContext';

const { Text } = Typography;

const Tabs = styled(AntdTabs)`
  .ant-tabs-nav::before {
    border-bottom: 0;
  }
`;

const GlobalNav = () => {
  const { userDetails, setUserDetails } = useContext(PlanitUserContext);

  const accountMenuItems: MenuProps['items'] = [
    {
      key: '1',
      label: <Link to="account-settings">Account settings</Link>,
    },
    {
      key: '4',
      danger: true,
      label: 'Logout',
      onClick: () => {
        setUserDetails({});
      },
    },
  ];

  const OperationsSlot = {
    right: (
      <Row align={'middle'}>
        <Text type="secondary" style={{ margin: 0 }}>
          Creating events as
        </Text>

        <Dropdown menu={{ items: accountMenuItems }}>
          <Button type="text" onClick={(e) => e.preventDefault()}>
            <Space>
              {userDetails?.ownerEmail}
              <DownOutlined />
            </Space>
          </Button>
        </Dropdown>
      </Row>
    ),
  };

  const items = [
    { key: '1', label: <Link to="create-events">Create events</Link> },
    { key: '2', label: <Link to="manage-presets">Manage presets</Link> },
  ];

  return <Tabs items={items} tabBarExtraContent={OperationsSlot} tabBarStyle={{ margin: '0' }} />;
};

export default GlobalNav;
