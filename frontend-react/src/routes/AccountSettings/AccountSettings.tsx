import { useState, useContext, useEffect } from 'react';

import { EmailSelector } from '../../components/EmailSelector/EmailSelector';
import { AxiosInstance } from '../../config';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';

export const AccountSettings = () => {
  const [accountEmails, setAccountEmails] = useState<string[]>([]);
  const { userDetails, setUserDetails } = useContext(PlanitUserContext);

  const getEmailList = (id: string) => {
    AxiosInstance.get(`/plan-it/user/getAllEmails/${id}`).then((response) => setAccountEmails(response.data));
  };

  useEffect(() => {
    if (userDetails?.planitUserId) getEmailList(userDetails.planitUserId);
  }, [userDetails?.planitUserId]);

  const onSelectEmail = (email: string) => {
    setUserDetails((prev) => ({ ...prev, ownerEmail: email }));
  };

  return (
    <div>
      <EmailSelector emails={accountEmails} selectChange={onSelectEmail}></EmailSelector>
    </div>
  );
};
