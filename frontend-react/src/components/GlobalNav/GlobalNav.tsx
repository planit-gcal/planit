import { Button, Tabs } from 'antd';
import { useContext } from 'react';
import { Link } from 'react-router-dom';

import { CountStateContext } from '../../contexts/PlanitUserContext';

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

  return (
    <div>
      <Tabs items={items} tabBarExtraContent={OperationsSlot} />
    </div>
  );
};

export default GlobalNav;
