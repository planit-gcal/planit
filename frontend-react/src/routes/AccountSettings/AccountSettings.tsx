import { useState, useContext, useEffect } from 'react';

import { EmailSelector } from '../../components/EmailSelector/EmailSelector';
import { AxiosInstance } from '../../config';
import { CountStateContext } from '../../contexts/PlanitUserContext';

export const AccountSettings = () => {
  const [accountEmails, setAccountEmails] = useState<string[]>([]);
  const { planitUserId, setOwnerEmail } = useContext(CountStateContext);

  const getEmailList = (id: string) => {
    AxiosInstance.get(`/plan-it/user/getAllEmails/${id}`).then((response) => setAccountEmails(response.data));
  };

  useEffect(() => {
    if (planitUserId !== null) getEmailList(planitUserId);
  }, [planitUserId]);

  const onSelectEmail = (email: string) => {
    setOwnerEmail(email);
  };

  return (
    <div>
      <EmailSelector emails={accountEmails} selectChange={onSelectEmail}></EmailSelector>
    </div>
  );
};
