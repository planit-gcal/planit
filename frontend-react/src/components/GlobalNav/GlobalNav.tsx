import { DownOutlined } from '@ant-design/icons';
import { Button, Dropdown, MenuProps, Space, Tabs as AntdTabs } from 'antd';
import { useContext } from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

import { PlanitUserContext } from '../../contexts/PlanitUserContext';

const Tabs = styled(AntdTabs)`
  .ant-tabs-nav::before {
    border-bottom: 0;
  }
`;

const accountMenuItems: MenuProps['items'] = [
  {
    key: '1',
    label: <Link to="account-settings">Account settings</Link>,
  },
  {
    key: '4',
    danger: true,
    label: 'Logout',
  },
];

const GlobalNav = () => {
  const { planitUserId } = useContext(PlanitUserContext);

  const OperationsSlot = {
    right: (
      <Dropdown menu={{ items: accountMenuItems }}>
        <Button type="text" onClick={(e) => e.preventDefault()}>
          <Space>
            {planitUserId}
            <DownOutlined />
          </Space>
        </Button>
      </Dropdown>
    ),
  };

  const items = [
    { key: '1', label: <Link to="create-events">Create events</Link> },
    { key: '2', label: <Link to="manage-presets">Manage presets</Link> },
  ];

  return <Tabs items={items} tabBarExtraContent={OperationsSlot} tabBarStyle={{ margin: '0' }} />;
};

export default GlobalNav;
