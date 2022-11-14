import { createContext, useEffect, useMemo, useState } from 'react';

import { getUserEmails } from '../api/users/users.api';
import { useLocalStorage } from '../hooks/useLocalStorage';

type UserDetails = {
  planitUserId?: number;
  ownerEmail?: string;
};

type State = {
  userDetails: UserDetails | null;
  setUserDetails: (userDetails: React.SetStateAction<UserDetails>) => void;
  userEmails: string[];
  isLoggedIn: boolean;
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

    const fetchAndSetEmails = async () => {
      const userEmails = await getUserEmails(userDetails.planitUserId!);

      setUserEmails(userEmails);
    };

    fetchAndSetEmails();
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
      isLoggedIn: !!userDetails?.planitUserId,
    }),
    [userDetails, setUserDetails, userEmails]
  );

  return <PlanitUserContext.Provider value={value}>{children}</PlanitUserContext.Provider>;
};
