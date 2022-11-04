import { createContext, useEffect, useMemo, useState } from 'react';

import { AxiosInstance } from '../config';
import { useLocalStorage } from '../hooks/useLocalStorage';

type UserDetails = {
  planitUserId?: string;
  ownerEmail?: string;
};

type State = {
  userDetails: UserDetails | null;
  setUserDetails: (userDetails: React.SetStateAction<UserDetails>) => void;
  userEmails: string[];
};

export const PlanitUserContext = createContext<State>({} as State);

type PlanitUserProviderProps = { children: React.ReactNode };
export const PlanitUserProvider = ({ children }: PlanitUserProviderProps) => {
  const [userDetails, setUserDetails] = useLocalStorage<UserDetails>('userDetails', {});
  const [userEmails, setUserEmails] = useState<string[]>([]);

  useEffect(() => {
    if (!userDetails?.planitUserId) {
      return;
    }

    AxiosInstance.get(`/plan-it/user/getAllEmails/${userDetails.planitUserId}`).then((response) =>
      setUserEmails(response.data)
    );
  }, [userDetails?.planitUserId]);

  useEffect(() => {
    if (!userDetails?.ownerEmail && userEmails.length > 0) {
      setUserDetails((prev) => ({ ...prev, ownerEmail: userEmails[0] }));
    }
  }, [setUserDetails, userDetails?.ownerEmail, userEmails]);

  const value = useMemo(
    () => ({
      userDetails,
      setUserDetails,
      userEmails,
    }),
    [userDetails, setUserDetails, userEmails]
  );

  return <PlanitUserContext.Provider value={value}>{children}</PlanitUserContext.Provider>;
};
