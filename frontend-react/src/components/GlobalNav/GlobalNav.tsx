import { Button, Tabs as AntdTabs } from 'antd';
import { useContext } from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

import { CountStateContext } from '../../contexts/PlanitUserContext';

const Tabs = styled(AntdTabs)`
  .ant-tabs-nav::before {
    border-bottom: 0;
  }
`;

const GlobalNav = () => {
  const { setPlanitUserId } = useContext(CountStateContext);

  const OperationsSlot = {
    right: <Button onClick={() => setPlanitUserId(null)}>Logout</Button>,
  };

  const items = [
    { key: '1', label: <Link to="create-events">Create events</Link> },
    { key: '2', label: <Link to="manage-presets">Manage presets</Link> },
    { key: '3', label: <Link to="account-settings">Account settings</Link> },
  ];

  return <Tabs items={items} tabBarExtraContent={OperationsSlot} tabBarStyle={{ margin: '0' }} />;
};

export default GlobalNav;
