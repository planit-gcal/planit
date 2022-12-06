import { DeleteOutlined } from '@ant-design/icons';
import { Button, notification, Space, Typography } from 'antd';
import { useCallback, useContext } from 'react';

import { removeAccount } from '../../api/users/users.api';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';

export const ManageAccountPage = () => {
  const { userDetails, setUserDetails } = useContext(PlanitUserContext);

  const onRemoveAccount = useCallback(async () => {
    if (!userDetails?.planitUserId) {
      return;
    }

    try {
      await removeAccount(userDetails!.planitUserId!);
      notification.success({ message: 'You have been successfully logged out!', placement: 'bottom' });
      setUserDetails({ planitUserId: null, ownerEmail: null });
    } catch (e) {
      console.error(e);
    }
  }, [setUserDetails, userDetails]);

  return (
    <Space direction="vertical" align="center" style={{ width: '100%' }}>
      <Typography.Title level={2}>Manage your account</Typography.Title>
      <Typography.Text>
        If you wish to remove your PlanIt account along with saved Google Accounts, you can do it here.
      </Typography.Text>
      <div>
        <Button
          type="primary"
          danger
          onClick={() => onRemoveAccount()}
          style={{ marginTop: '16px' }}
          icon={<DeleteOutlined />}
        >
          Remove account
        </Button>
      </div>
    </Space>
  );
};
