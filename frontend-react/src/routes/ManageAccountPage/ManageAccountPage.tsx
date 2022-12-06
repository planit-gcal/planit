import { DeleteOutlined } from '@ant-design/icons';
import { Button, notification } from 'antd';
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
    <div>
      <Button type="primary" danger onClick={() => onRemoveAccount()} icon={<DeleteOutlined />}>
        Remove account
      </Button>
    </div>
  );
};
