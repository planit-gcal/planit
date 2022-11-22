import { createContext, useCallback, useEffect, useMemo, useState } from 'react';

import { getUserEmails } from '../api/users/users.api';
import { useLocalStorage } from '../hooks/useLocalStorage';

type UserDetails = {
  planitUserId: number | null;
  ownerEmail: string | null;
};

type State = {
  userDetails: UserDetails | null;
  setUserDetails: (userDetails: React.SetStateAction<UserDetails>) => void;
  userEmails: string[];
  isLoggedIn: boolean;
  fetchAndSetEmails: () => void;
};

export const PlanitUserContext = createContext<State>({} as State);

type PlanitUserProviderProps = { children: React.ReactNode };
export const PlanitUserProvider = ({ children }: PlanitUserProviderProps) => {
  const [userDetails, setUserDetails] = useLocalStorage<UserDetails>('userDetails', {
    planitUserId: null,
    ownerEmail: null,
  });
  const [userEmails, setUserEmails] = useState<string[]>([]);

  const fetchAndSetEmails = useCallback(async () => {
    const userEmails = await getUserEmails(userDetails.planitUserId!);

    setUserEmails(userEmails);
  }, [userDetails.planitUserId]);

  useEffect(() => {
    if (!userDetails?.planitUserId) {
      return;
    }

    fetchAndSetEmails();
  }, [userDetails?.planitUserId, fetchAndSetEmails]);

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
      fetchAndSetEmails,
    }),
    [userDetails, setUserDetails, userEmails, fetchAndSetEmails]
  );

  return <PlanitUserContext.Provider value={value}>{children}</PlanitUserContext.Provider>;
};
